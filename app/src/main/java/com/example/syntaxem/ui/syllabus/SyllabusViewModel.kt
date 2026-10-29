package com.example.syntaxem.ui.syllabus

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.syntaxem.data.local.dao.ChapterWithProgress
import com.example.syntaxem.data.local.entity.CurriculumEntity
import com.example.syntaxem.data.local.entity.SubjectEntity
import com.example.syntaxem.data.repository.SyllabusProgress
import com.example.syntaxem.data.repository.SyllabusRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class StatusFilter(val label: String) {
    ALL("All"),
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    NEEDS_REVISION("Needs Revision")
}

enum class SyllabusSort(val label: String) {
    CHAPTER_NUMBER("Chapter Number"),
    STATUS("Status"),
    TARGET_DATE("Target Date")
}

data class SyllabusUiState(
    val activeCurriculum: CurriculumEntity? = null,
    val availableCurricula: List<CurriculumEntity> = emptyList(),
    val subjects: List<SubjectEntity> = emptyList(),
    val selectedSubjectId: String? = null, // null means all
    val allChapters: List<ChapterWithProgress> = emptyList(),
    val filteredChapters: List<ChapterWithProgress> = emptyList(),
    val progress: SyllabusProgress = SyllabusProgress(0, 0, 0, 0, 0, 0f),
    val searchQuery: String = "",
    val statusFilter: StatusFilter = StatusFilter.ALL,
    val sortOrder: SyllabusSort = SyllabusSort.CHAPTER_NUMBER,
    val selectedChapterForDetail: ChapterWithProgress? = null,
    val canUndo: Boolean = false,
    val snackbarMessage: String? = null
)

class SyllabusViewModel(application: Application) : AndroidViewModel(application) {

    private val syllabusRepo = SyllabusRepository.getInstance(application)

    private val _uiState = MutableStateFlow(SyllabusUiState())
    val uiState: StateFlow<SyllabusUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            syllabusRepo.initializeBundledDataIfNeeded()
        }

        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                syllabusRepo.selectedCurriculum,
                syllabusRepo.allCurricula,
                syllabusRepo.allSubjects,
                syllabusRepo.allChaptersWithProgress
            ) { activeCurr, curricula, subjects, chapters ->
                val activeId = activeCurr?.id ?: "curr_cbse_11_science_2026"
                val filteredSubjects = subjects.filter { it.curriculumId == activeId }
                val currChapters = chapters.filter { item ->
                    filteredSubjects.any { it.id == item.chapter.subjectId }
                }

                _uiState.update { current ->
                    current.copy(
                        activeCurriculum = activeCurr,
                        availableCurricula = curricula,
                        subjects = filteredSubjects,
                        allChapters = currChapters
                    )
                }
                applyFiltersAndCalculations()
            }.collect()
        }
    }

    fun selectSubject(subjectId: String?) {
        _uiState.update { it.copy(selectedSubjectId = subjectId) }
        applyFiltersAndCalculations()
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFiltersAndCalculations()
    }

    fun setStatusFilter(filter: StatusFilter) {
        _uiState.update { it.copy(statusFilter = filter) }
        applyFiltersAndCalculations()
    }

    fun setSortOrder(sort: SyllabusSort) {
        _uiState.update { it.copy(sortOrder = sort) }
        applyFiltersAndCalculations()
    }

    fun openChapterDetail(item: ChapterWithProgress?) {
        _uiState.update { it.copy(selectedChapterForDetail = item) }
    }

    fun updateChapterStatus(chapterId: String, newStatus: String) {
        viewModelScope.launch {
            syllabusRepo.updateChapterStatus(chapterId, newStatus)
            _uiState.update { it.copy(canUndo = true, snackbarMessage = "Status updated to $newStatus") }
            // Refresh detail sheet if open
            if (_uiState.value.selectedChapterForDetail?.chapter?.id == chapterId) {
                val updated = _uiState.value.allChapters.firstOrNull { it.chapter.id == chapterId }
                _uiState.update { it.copy(selectedChapterForDetail = updated) }
            }
        }
    }

    fun undoLastStatusChange() {
        viewModelScope.launch {
            val undone = syllabusRepo.undoLastStatusChange()
            if (undone) {
                _uiState.update { it.copy(canUndo = false, snackbarMessage = "Undid status change") }
            }
        }
    }

    fun updateChapterNotesAndTarget(chapterId: String, notes: String?, targetDate: Long?) {
        viewModelScope.launch {
            syllabusRepo.updateChapterDetails(chapterId, notes, targetDate, null)
            _uiState.update { it.copy(snackbarMessage = "Notes & Target date saved") }
        }
    }

    fun selectCurriculum(curriculumId: String) {
        viewModelScope.launch {
            syllabusRepo.selectCurriculum(curriculumId)
            _uiState.update { it.copy(selectedSubjectId = null) }
        }
    }

    suspend fun exportProgress(): String {
        return syllabusRepo.exportProgressToJson()
    }

    fun importProgress(jsonString: String, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = syllabusRepo.importProgressFromJson(jsonString)
            res.onSuccess { count ->
                _uiState.update { it.copy(snackbarMessage = "Successfully imported $count progress records") }
                onComplete(true, "Imported $count records")
            }.onFailure { error ->
                onComplete(false, error.message ?: "Failed to import JSON")
            }
        }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    private fun applyFiltersAndCalculations() {
        val state = _uiState.value
        val all = state.allChapters

        // Subject filter
        var list = if (state.selectedSubjectId != null) {
            all.filter { it.chapter.subjectId == state.selectedSubjectId }
        } else {
            all
        }

        // Search query
        if (state.searchQuery.isNotBlank()) {
            val q = state.searchQuery.trim().lowercase()
            list = list.filter {
                it.chapter.title.lowercase().contains(q) ||
                        it.chapter.chapterNumber.toString().contains(q) ||
                        it.chapter.description.lowercase().contains(q)
            }
        }

        // Status filter
        list = when (state.statusFilter) {
            StatusFilter.ALL -> list
            StatusFilter.NOT_STARTED -> list.filter { (it.progress?.status ?: "NOT_STARTED") == "NOT_STARTED" }
            StatusFilter.IN_PROGRESS -> list.filter { it.progress?.status == "IN_PROGRESS" }
            StatusFilter.COMPLETED -> list.filter { it.progress?.status == "COMPLETED" }
            StatusFilter.NEEDS_REVISION -> list.filter { it.progress?.status == "NEEDS_REVISION" }
        }

        // Sort order
        list = when (state.sortOrder) {
            SyllabusSort.CHAPTER_NUMBER -> list.sortedBy { it.chapter.chapterNumber }
            SyllabusSort.STATUS -> list.sortedBy { it.progress?.status ?: "Z" }
            SyllabusSort.TARGET_DATE -> list.sortedBy { it.progress?.targetDate ?: Long.MAX_VALUE }
        }

        val progress = syllabusRepo.calculateProgress(
            if (state.selectedSubjectId != null) {
                all.filter { it.chapter.subjectId == state.selectedSubjectId }
            } else {
                all
            }
        )

        _uiState.update { current ->
            current.copy(
                filteredChapters = list,
                progress = progress
            )
        }
    }
}
