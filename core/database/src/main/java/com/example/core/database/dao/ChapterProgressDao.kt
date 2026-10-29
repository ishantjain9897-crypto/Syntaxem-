package com.example.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.core.database.entity.ChapterCompletionState
import com.example.core.database.entity.ChapterProgress
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object dedicated to tracking chapter completion states,
 * notes, target dates, and revision requirements.
 */
@Dao
interface ChapterProgressDao {

    @Query("SELECT * FROM chapter_progress WHERE chapterId = :chapterId LIMIT 1")
    fun getProgressForChapter(chapterId: String): Flow<ChapterProgress?>

    @Query("SELECT * FROM chapter_progress WHERE chapterId = :chapterId LIMIT 1")
    suspend fun getProgressForChapterOnce(chapterId: String): ChapterProgress?

    @Query("SELECT * FROM chapter_progress ORDER BY lastUpdatedAt DESC")
    fun getAllProgress(): Flow<List<ChapterProgress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: ChapterProgress)

    @Query(
        """
        UPDATE chapter_progress 
        SET status = :status, 
            lastUpdatedAt = :updatedAt, 
            completedAt = CASE WHEN :status = 'COMPLETED' THEN :completedAt ELSE completedAt END 
        WHERE chapterId = :chapterId
        """
    )
    suspend fun updateCompletionStatus(
        chapterId: String,
        status: ChapterCompletionState,
        updatedAt: Long = System.currentTimeMillis(),
        completedAt: Long? = if (status == ChapterCompletionState.COMPLETED) System.currentTimeMillis() else null
    )

    suspend fun updateCompletionStatus(
        chapterId: String,
        statusString: String,
        updatedAt: Long = System.currentTimeMillis(),
        completedAt: Long? = if (statusString == "COMPLETED") System.currentTimeMillis() else null
    ) {
        updateCompletionStatus(
            chapterId = chapterId,
            status = ChapterCompletionState.fromValue(statusString),
            updatedAt = updatedAt,
            completedAt = completedAt
        )
    }

    @Transaction
    suspend fun markCompleted(chapterId: String, completedAt: Long = System.currentTimeMillis()) {
        val existing = getProgressForChapterOnce(chapterId)
        if (existing != null) {
            updateCompletionStatus(
                chapterId = chapterId,
                status = ChapterCompletionState.COMPLETED,
                updatedAt = completedAt,
                completedAt = completedAt
            )
        } else {
            upsertProgress(
                ChapterProgress(
                    chapterId = chapterId,
                    status = ChapterCompletionState.COMPLETED,
                    completedAt = completedAt,
                    lastUpdatedAt = completedAt
                )
            )
        }
    }

    @Transaction
    suspend fun markInProgress(chapterId: String, updatedAt: Long = System.currentTimeMillis()) {
        val existing = getProgressForChapterOnce(chapterId)
        if (existing != null) {
            updateCompletionStatus(
                chapterId = chapterId,
                status = ChapterCompletionState.IN_PROGRESS,
                updatedAt = updatedAt
            )
        } else {
            upsertProgress(
                ChapterProgress(
                    chapterId = chapterId,
                    status = ChapterCompletionState.IN_PROGRESS,
                    lastUpdatedAt = updatedAt
                )
            )
        }
    }

    @Transaction
    suspend fun markNeedsRevision(chapterId: String, updatedAt: Long = System.currentTimeMillis()) {
        val existing = getProgressForChapterOnce(chapterId)
        if (existing != null) {
            updateCompletionStatus(
                chapterId = chapterId,
                status = ChapterCompletionState.NEEDS_REVISION,
                updatedAt = updatedAt
            )
        } else {
            upsertProgress(
                ChapterProgress(
                    chapterId = chapterId,
                    status = ChapterCompletionState.NEEDS_REVISION,
                    lastUpdatedAt = updatedAt
                )
            )
        }
    }

    @Transaction
    suspend fun resetToNotStarted(chapterId: String, updatedAt: Long = System.currentTimeMillis()) {
        val existing = getProgressForChapterOnce(chapterId)
        if (existing != null) {
            updateCompletionStatus(
                chapterId = chapterId,
                status = ChapterCompletionState.NOT_STARTED,
                updatedAt = updatedAt,
                completedAt = null
            )
        } else {
            upsertProgress(
                ChapterProgress(
                    chapterId = chapterId,
                    status = ChapterCompletionState.NOT_STARTED,
                    lastUpdatedAt = updatedAt
                )
            )
        }
    }

    @Query(
        """
        UPDATE chapter_progress 
        SET personalNotes = :notes, 
            targetDate = :targetDate, 
            lastUpdatedAt = :updatedAt 
        WHERE chapterId = :chapterId
        """
    )
    suspend fun updateNotesAndTargetDate(
        chapterId: String,
        notes: String?,
        targetDate: Long?,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query(
        """
        SELECT COUNT(cp.chapterId) FROM chapter_progress cp
        INNER JOIN chapters c ON cp.chapterId = c.id
        WHERE c.subjectId = :subjectId AND cp.status = 'COMPLETED' AND c.isApplicable = 1
        """
    )
    fun getCompletedCountForSubject(subjectId: String): Flow<Int>

    @Query(
        """
        SELECT COUNT(cp.chapterId) FROM chapter_progress cp
        INNER JOIN chapters c ON cp.chapterId = c.id
        WHERE c.subjectId = :subjectId AND cp.status != 'COMPLETED' AND c.isApplicable = 1
        """
    )
    fun getIncompleteCountForSubject(subjectId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM chapter_progress WHERE status = :status")
    fun getProgressCountByStatus(status: String): Flow<Int>

    @Query("DELETE FROM chapter_progress WHERE chapterId = :chapterId")
    suspend fun deleteProgress(chapterId: String)

    @Query("DELETE FROM chapter_progress")
    suspend fun clearAllProgress()
}
