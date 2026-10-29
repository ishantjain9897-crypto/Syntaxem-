package com.example.syntaxem.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.syntaxem.data.local.dao.ChapterWithProgress
import com.example.syntaxem.data.local.entity.CurriculumEntity
import com.example.syntaxem.data.local.entity.SubjectEntity
import com.example.syntaxem.data.repository.SyllabusProgress
import com.example.syntaxem.data.repository.SyllabusRepository
import com.example.syntaxem.data.weather.CityLocation
import com.example.syntaxem.data.weather.LocationHelper
import com.example.syntaxem.data.weather.WeatherRepository
import com.example.syntaxem.data.weather.WeatherSnapshot
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class SubjectProgressSummary(
    val subject: SubjectEntity,
    val progress: SyllabusProgress,
    val totalChapters: Int,
    val completedChapters: Int
)

data class DashboardUiState(
    val currentTimeString: String = "",
    val currentDateString: String = "",
    val timeZoneString: String = "",
    val is24HourFormat: Boolean = false,
    val academicSession: String = "2026–27",
    val curriculum: CurriculumEntity? = null,
    val overallProgress: SyllabusProgress = SyllabusProgress(0, 0, 0, 0, 0, 0f),
    val subjectSummaries: List<SubjectProgressSummary> = emptyList(),
    val continueChapter: ChapterWithProgress? = null,
    val revisionQueue: List<ChapterWithProgress> = emptyList(),
    val dailySuggestion: String = "Begin today's study block with Mathematics: Chapter 3 Trigonometric Functions.",
    val weather: WeatherSnapshot? = null,
    val isWeatherEnabled: Boolean = true,
    val selectedCity: CityLocation = CityLocation.DEFAULT_CITIES.first(),
    val hasLocationPermission: Boolean = false,
    val isRefreshingWeather: Boolean = false
)

class AcademicDashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val syllabusRepo = SyllabusRepository.getInstance(application)
    private val weatherRepo = WeatherRepository.getInstance(application)

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            syllabusRepo.initializeBundledDataIfNeeded()
        }

        startClockTicker()
        observeSyllabus()
        observeWeather()
        checkLocationPermissionAndRefresh()
    }

    private fun startClockTicker() {
        viewModelScope.launch {
            while (isActive) {
                updateTime()
                delay(1000)
            }
        }
    }

    private fun updateTime() {
        val now = Date()
        val is24 = _uiState.value.is24HourFormat
        val timePattern = if (is24) "HH:mm:ss" else "hh:mm:ss a"
        val timeFormat = SimpleDateFormat(timePattern, Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())

        val tz = TimeZone.getDefault()
        val tzDisplay = "${tz.getDisplayName(false, TimeZone.SHORT)} (${tz.id})"

        _uiState.update { current ->
            current.copy(
                currentTimeString = timeFormat.format(now),
                currentDateString = dateFormat.format(now),
                timeZoneString = tzDisplay
            )
        }
    }

    fun toggleTimeFormat() {
        _uiState.update { it.copy(is24HourFormat = !it.is24HourFormat) }
        updateTime()
    }

    private fun observeSyllabus() {
        viewModelScope.launch {
            combine(
                syllabusRepo.selectedCurriculum,
                syllabusRepo.allSubjects,
                syllabusRepo.allChaptersWithProgress
            ) { curr, subjects, chapters ->
                val activeCurriculumId = curr?.id ?: "curr_cbse_11_science_2026"
                val filteredSubjects = subjects.filter { it.curriculumId == activeCurriculumId }
                val applicableChapters = chapters.filter { it.chapter.isApplicable }

                val overall = syllabusRepo.calculateProgress(applicableChapters)

                val summaries = filteredSubjects.map { sub ->
                    val subChapters = applicableChapters.filter { it.chapter.subjectId == sub.id }
                    val prog = syllabusRepo.calculateProgress(subChapters)
                    SubjectProgressSummary(
                        subject = sub,
                        progress = prog,
                        totalChapters = prog.totalCount,
                        completedChapters = prog.completedCount
                    )
                }

                // Continue studying: most recently updated in-progress or not-started chapter
                val continueItem = applicableChapters
                    .sortedByDescending { it.progress?.lastUpdatedAt ?: 0L }
                    .firstOrNull { it.progress?.status == "IN_PROGRESS" }
                    ?: applicableChapters.firstOrNull { it.progress?.status == "NOT_STARTED" }

                // Revision Queue
                val revisionList = applicableChapters.filter { it.progress?.status == "NEEDS_REVISION" }

                // Study Suggestion
                val suggestion = when {
                    revisionList.isNotEmpty() -> "Review priority: '${revisionList.first().chapter.title}' needs revision before starting new topics."
                    continueItem != null -> "Keep the momentum going on '${continueItem.chapter.title}'. Check derivation steps and test your recall."
                    overall.completedCount == overall.totalCount && overall.totalCount > 0 -> "All chapters completed! Run through Mistake Journal in Game Arena for revision."
                    else -> "Focus on your Class 11 core foundations today."
                }

                _uiState.update { current ->
                    current.copy(
                        curriculum = curr,
                        academicSession = curr?.academicSession ?: "2026–27",
                        overallProgress = overall,
                        subjectSummaries = summaries,
                        continueChapter = continueItem,
                        revisionQueue = revisionList,
                        dailySuggestion = suggestion
                    )
                }
            }.collect()
        }
    }

    private fun observeWeather() {
        viewModelScope.launch {
            combine(
                weatherRepo.currentWeather,
                weatherRepo.isWeatherEnabled,
                weatherRepo.selectedCity
            ) { snapshot, enabled, city ->
                _uiState.update { current ->
                    current.copy(
                        weather = snapshot,
                        isWeatherEnabled = enabled,
                        selectedCity = city
                    )
                }
            }.collect()
        }
    }

    fun checkLocationPermissionAndRefresh() {
        val context = getApplication<Application>()
        val hasPerm = LocationHelper.hasLocationPermission(context)
        _uiState.update { it.copy(hasLocationPermission = hasPerm) }

        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshingWeather = true) }
            weatherRepo.refreshWeatherWithLocation(hasPermissionOverride = hasPerm)
            _uiState.update { it.copy(isRefreshingWeather = false) }
        }
    }

    fun selectCity(city: CityLocation) {
        weatherRepo.setSelectedCity(city)
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshingWeather = true) }
            weatherRepo.refreshWeather(
                latitude = city.latitude,
                longitude = city.longitude,
                locationLabel = city.displayName
            )
            _uiState.update { it.copy(isRefreshingWeather = false) }
        }
    }

    fun toggleWeatherEnabled(enabled: Boolean) {
        weatherRepo.setWeatherEnabled(enabled)
    }
}
