package com.example.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.core.database.entity.Chapter
import com.example.core.database.entity.ChapterWithProgress
import com.example.core.database.entity.Subject
import com.example.core.database.entity.Syllabus
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Syllabus, Subject, and Chapter querying with composite progress.
 */
@Dao
interface SyllabusDao {

    @Query("SELECT * FROM syllabus ORDER BY academicSession DESC")
    fun getAllSyllabi(): Flow<List<Syllabus>>

    @Query("SELECT * FROM syllabus WHERE isSelected = 1 LIMIT 1")
    fun getSelectedSyllabus(): Flow<Syllabus?>

    @Query("SELECT * FROM syllabus WHERE id = :id LIMIT 1")
    suspend fun getSyllabusById(id: String): Syllabus?

    @Query("UPDATE syllabus SET isSelected = CASE WHEN id = :selectedId THEN 1 ELSE 0 END")
    suspend fun setSelectedSyllabus(selectedId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyllabus(syllabus: Syllabus)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<Subject>)

    @Query("SELECT * FROM subjects WHERE syllabusId = :syllabusId ORDER BY displayOrder ASC")
    fun getSubjectsForSyllabus(syllabusId: String): Flow<List<Subject>>

    @Query("SELECT * FROM subjects ORDER BY displayOrder ASC")
    fun getAllSubjects(): Flow<List<Subject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<Chapter>)

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

    @Transaction
    @Query(
        """
        SELECT c.* FROM chapters c
        INNER JOIN chapter_progress cp ON c.id = cp.chapterId
        WHERE cp.status = 'NEEDS_REVISION' AND c.isApplicable = 1
        ORDER BY cp.lastUpdatedAt DESC
        """
    )
    fun getRevisionQueue(): Flow<List<ChapterWithProgress>>

    @Query("SELECT COUNT(*) FROM chapters WHERE subjectId = :subjectId AND isApplicable = 1")
    fun getTotalApplicableChaptersCount(subjectId: String): Flow<Int>

    @Query(
        """
        SELECT COUNT(c.id) FROM chapters c
        INNER JOIN chapter_progress cp ON c.id = cp.chapterId
        WHERE c.subjectId = :subjectId AND cp.status = 'COMPLETED' AND c.isApplicable = 1
        """
    )
    fun getCompletedApplicableChaptersCount(subjectId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM chapters")
    suspend fun getTotalChapterCount(): Int
}
