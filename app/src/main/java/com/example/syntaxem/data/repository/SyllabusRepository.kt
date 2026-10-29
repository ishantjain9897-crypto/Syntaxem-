package com.example.syntaxem.data.repository

import android.content.Context
import com.example.syntaxem.data.local.AppDatabase
import com.example.syntaxem.data.local.BundledCurriculumData
import com.example.syntaxem.data.local.dao.ChapterWithProgress
import com.example.syntaxem.data.local.dao.SyllabusDao
import com.example.syntaxem.data.local.entity.ChapterProgressEntity
import com.example.syntaxem.data.local.entity.CurriculumEntity
import com.example.syntaxem.data.local.entity.StatusHistoryEntity
import com.example.syntaxem.data.local.entity.SubjectEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * Calculated progress metrics with numerator and denominator.
 */
data class SyllabusProgress(
    val completedCount: Int,
    val totalCount: Int,
    val inProgressCount: Int,
    val needsRevisionCount: Int,
    val notStartedCount: Int,
    val percentage: Float
) {
    val displayFraction: String get() = "$completedCount / $totalCount Chapters"
    val displayPercentage: String get() = "%.1f%%".format(percentage)
}

class SyllabusRepository(private val syllabusDao: SyllabusDao) {

    val selectedCurriculum: Flow<CurriculumEntity?> = syllabusDao.getSelectedCurriculum()
    val allCurricula: Flow<List<CurriculumEntity>> = syllabusDao.getAllCurricula()
    val allSubjects: Flow<List<SubjectEntity>> = syllabusDao.getAllSubjects()
    val allChaptersWithProgress: Flow<List<ChapterWithProgress>> = syllabusDao.getAllChaptersWithProgress()

    fun getSubjectsForCurriculum(curriculumId: String): Flow<List<SubjectEntity>> {
        return syllabusDao.getSubjectsForCurriculum(curriculumId)
    }

    fun getChaptersForSubject(subjectId: String): Flow<List<ChapterWithProgress>> {
        return syllabusDao.getChaptersWithProgressForSubject(subjectId)
    }

    suspend fun initializeBundledDataIfNeeded() = withContext(Dispatchers.IO) {
        val count = syllabusDao.getChapterCount()
        if (count == 0) {
            // Seed Curricula
            syllabusDao.insertCurriculum(BundledCurriculumData.defaultCurriculum)
            syllabusDao.insertCurriculum(BundledCurriculumData.vidyaBharatiProvisionalCurriculum)
            syllabusDao.insertCurriculum(BundledCurriculumData.cbseClass12Curriculum)

            // Seed Subjects
            val defaultSubjects = BundledCurriculumData.getSubjects(BundledCurriculumData.defaultCurriculum.id)
            val vbSubjects = BundledCurriculumData.getSubjects(BundledCurriculumData.vidyaBharatiProvisionalCurriculum.id)
            val c12Subjects = BundledCurriculumData.getSubjects(BundledCurriculumData.cbseClass12Curriculum.id)
            syllabusDao.insertSubjects(defaultSubjects + vbSubjects + c12Subjects)

            // Seed Chapters
            for (sub in defaultSubjects) {
                syllabusDao.insertChapters(BundledCurriculumData.getChapters(sub.id))
            }
            for (sub in vbSubjects) {
                syllabusDao.insertChapters(BundledCurriculumData.getChapters(sub.id))
            }
            for (sub in c12Subjects) {
                syllabusDao.insertChapters(BundledCurriculumData.getChapters(sub.id))
            }

            // Set initial progress for a couple sample chapters to show initial dashboard state
            syllabusDao.upsertProgress(
                ChapterProgressEntity(
                    chapterId = "ch_11_m_01",
                    status = "COMPLETED",
                    completedAt = System.currentTimeMillis() - 86400000L * 3,
                    personalNotes = "Completed NCERT Exercises 1.1 to 1.6; reviewed power sets."
                )
            )
            syllabusDao.upsertProgress(
                ChapterProgressEntity(
                    chapterId = "ch_11_m_03",
                    status = "IN_PROGRESS",
                    targetDate = System.currentTimeMillis() + 86400000L * 4,
                    personalNotes = "Working on trigonometric identities and ASTC quadrant signs."
                )
            )
            syllabusDao.upsertProgress(
                ChapterProgressEntity(
                    chapterId = "ch_11_p_03",
                    status = "NEEDS_REVISION",
                    personalNotes = "Revisit projectile range formula R = (v0^2 sin 2θ)/g."
                )
            )
        }
    }

    suspend fun selectCurriculum(curriculumId: String) = withContext(Dispatchers.IO) {
        syllabusDao.setSelectedCurriculum(curriculumId)
    }

    suspend fun updateChapterStatus(chapterId: String, newStatus: String) = withContext(Dispatchers.IO) {
        val current = syllabusDao.getProgressForChapter(chapterId)
        val oldStatus = current?.status ?: "NOT_STARTED"

        if (oldStatus != newStatus) {
            syllabusDao.insertStatusHistory(
                StatusHistoryEntity(
                    chapterId = chapterId,
                    previousStatus = oldStatus,
                    newStatus = newStatus
                )
            )

            val now = System.currentTimeMillis()
            val completedTime = if (newStatus == "COMPLETED") (current?.completedAt ?: now) else null

            syllabusDao.upsertProgress(
                ChapterProgressEntity(
                    chapterId = chapterId,
                    status = newStatus,
                    completedAt = completedTime,
                    lastUpdatedAt = now,
                    targetDate = current?.targetDate,
                    personalNotes = current?.personalNotes,
                    revisionDueAt = current?.revisionDueAt
                )
            )
        }
    }

    suspend fun updateChapterDetails(
        chapterId: String,
        notes: String?,
        targetDate: Long?,
        revisionDueAt: Long?
    ) = withContext(Dispatchers.IO) {
        val current = syllabusDao.getProgressForChapter(chapterId)
        val now = System.currentTimeMillis()
        syllabusDao.upsertProgress(
            ChapterProgressEntity(
                chapterId = chapterId,
                status = current?.status ?: "NOT_STARTED",
                completedAt = current?.completedAt,
                lastUpdatedAt = now,
                targetDate = targetDate,
                personalNotes = notes,
                revisionDueAt = revisionDueAt
            )
        )
    }

    suspend fun undoLastStatusChange(): Boolean = withContext(Dispatchers.IO) {
        val lastHistory = syllabusDao.getLatestStatusHistory() ?: return@withContext false
        val current = syllabusDao.getProgressForChapter(lastHistory.chapterId)

        val revertedStatus = lastHistory.previousStatus
        val now = System.currentTimeMillis()

        syllabusDao.upsertProgress(
            ChapterProgressEntity(
                chapterId = lastHistory.chapterId,
                status = revertedStatus,
                completedAt = if (revertedStatus == "COMPLETED") current?.completedAt else null,
                lastUpdatedAt = now,
                targetDate = current?.targetDate,
                personalNotes = current?.personalNotes,
                revisionDueAt = current?.revisionDueAt
            )
        )

        syllabusDao.deleteStatusHistoryById(lastHistory.id)
        true
    }

    fun calculateProgress(items: List<ChapterWithProgress>): SyllabusProgress {
        val applicable = items.filter { it.chapter.isApplicable }
        val total = applicable.size
        if (total == 0) {
            return SyllabusProgress(0, 0, 0, 0, 0, 0f)
        }

        var completed = 0
        var inProgress = 0
        var needsRevision = 0
        var notStarted = 0

        for (item in applicable) {
            when (item.progress?.status ?: "NOT_STARTED") {
                "COMPLETED" -> completed++
                "IN_PROGRESS" -> inProgress++
                "NEEDS_REVISION" -> needsRevision++
                else -> notStarted++
            }
        }

        val percentage = (completed.toFloat() / total.toFloat()) * 100f
        return SyllabusProgress(
            completedCount = completed,
            totalCount = total,
            inProgressCount = inProgress,
            needsRevisionCount = needsRevision,
            notStartedCount = notStarted,
            percentage = percentage
        )
    }

    suspend fun exportProgressToJson(): String = withContext(Dispatchers.IO) {
        val allChapters = syllabusDao.getAllChaptersWithProgress().firstOrNull() ?: emptyList()
        val curr = syllabusDao.getSelectedCurriculum().firstOrNull() ?: BundledCurriculumData.defaultCurriculum

        val root = JSONObject().apply {
            put("app", "Syntaxem")
            put("schemaVersion", 1)
            put("exportedAt", System.currentTimeMillis())
            put("curriculumId", curr.id)
            put("academicSession", curr.academicSession)
            put("classLevel", curr.classLevel)
            put("authority", curr.authority)
        }

        val progressArray = JSONArray()
        for (item in allChapters) {
            val prog = item.progress
            if (prog != null && prog.status != "NOT_STARTED") {
                val obj = JSONObject().apply {
                    put("chapterId", item.chapter.id)
                    put("title", item.chapter.title)
                    put("status", prog.status)
                    put("completedAt", prog.completedAt ?: 0L)
                    put("lastUpdatedAt", prog.lastUpdatedAt)
                    put("targetDate", prog.targetDate ?: 0L)
                    put("personalNotes", prog.personalNotes ?: "")
                    put("revisionDueAt", prog.revisionDueAt ?: 0L)
                }
                progressArray.put(obj)
            }
        }
        root.put("chapterProgress", progressArray)
        root.toString(2)
    }

    suspend fun importProgressFromJson(jsonString: String): kotlin.Result<Int> = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            if (!root.has("chapterProgress")) {
                return@withContext kotlin.Result.failure(IllegalArgumentException("Invalid syllabus progress file: missing 'chapterProgress' array"))
            }

            val array = root.getJSONArray("chapterProgress")
            var importedCount = 0
            val now = System.currentTimeMillis()

            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val chapterId = obj.getString("chapterId")
                val status = obj.optString("status", "NOT_STARTED")
                val notes = obj.optString("personalNotes", "")
                val targetDate = if (obj.has("targetDate") && obj.getLong("targetDate") > 0) obj.getLong("targetDate") else null
                val completedAt = if (obj.has("completedAt") && obj.getLong("completedAt") > 0) obj.getLong("completedAt") else null

                syllabusDao.upsertProgress(
                    ChapterProgressEntity(
                        chapterId = chapterId,
                        status = status,
                        completedAt = completedAt,
                        lastUpdatedAt = now,
                        targetDate = targetDate,
                        personalNotes = if (notes.isNotBlank()) notes else null
                    )
                )
                importedCount++
            }
            kotlin.Result.success(importedCount)
        } catch (e: Exception) {
            kotlin.Result.failure(e)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SyllabusRepository? = null

        fun getInstance(context: Context): SyllabusRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getInstance(context)
                val instance = SyllabusRepository(db.syllabusDao())
                INSTANCE = instance
                instance
            }
        }
    }
}
