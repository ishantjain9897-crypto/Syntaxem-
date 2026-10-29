package com.example.syntaxem.data.local.dao

import androidx.room.*
import com.example.syntaxem.data.local.entity.*
import kotlinx.coroutines.flow.Flow

/**
 * Data class combining a chapter with its optional progress record.
 */
data class ChapterWithProgress(
    @Embedded val chapter: ChapterEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "chapterId"
    )
    val progress: ChapterProgressEntity?
)

@Dao
interface SyllabusDao {

    @Query("SELECT * FROM curricula ORDER BY academicSession DESC")
    fun getAllCurricula(): Flow<List<CurriculumEntity>>

    @Query("SELECT * FROM curricula WHERE isSelected = 1 LIMIT 1")
    fun getSelectedCurriculum(): Flow<CurriculumEntity?>

    @Query("SELECT * FROM curricula WHERE id = :id LIMIT 1")
    suspend fun getCurriculumById(id: String): CurriculumEntity?

    @Query("UPDATE curricula SET isSelected = CASE WHEN id = :selectedId THEN 1 ELSE 0 END")
    suspend fun setSelectedCurriculum(selectedId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurriculum(curriculum: CurriculumEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectEntity>)

    @Query("SELECT * FROM subjects WHERE curriculumId = :curriculumId ORDER BY displayOrder ASC")
    fun getSubjectsForCurriculum(curriculumId: String): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects ORDER BY displayOrder ASC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)

    @Transaction
    @Query("SELECT * FROM chapters WHERE subjectId = :subjectId ORDER BY displayOrder ASC")
    fun getChaptersWithProgressForSubject(subjectId: String): Flow<List<ChapterWithProgress>>

    @Transaction
    @Query("SELECT * FROM chapters ORDER BY displayOrder ASC")
    fun getAllChaptersWithProgress(): Flow<List<ChapterWithProgress>>

    @Transaction
    @Query("SELECT * FROM chapters WHERE id = :chapterId LIMIT 1")
    fun getChapterWithProgress(chapterId: String): Flow<ChapterWithProgress?>

    @Transaction
    @Query("SELECT * FROM chapters WHERE id = :chapterId LIMIT 1")
    suspend fun getChapterWithProgressOnce(chapterId: String): ChapterWithProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: ChapterProgressEntity)

    @Query("SELECT * FROM chapter_progress WHERE chapterId = :chapterId LIMIT 1")
    suspend fun getProgressForChapter(chapterId: String): ChapterProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatusHistory(history: StatusHistoryEntity)

    @Query("SELECT * FROM status_history ORDER BY id DESC LIMIT 1")
    suspend fun getLatestStatusHistory(): StatusHistoryEntity?

    @Query("DELETE FROM status_history WHERE id = :id")
    suspend fun deleteStatusHistoryById(id: Long)

    @Query("SELECT COUNT(*) FROM chapters")
    suspend fun getChapterCount(): Int

    @Query("DELETE FROM chapter_progress")
    suspend fun clearAllProgress()
}
