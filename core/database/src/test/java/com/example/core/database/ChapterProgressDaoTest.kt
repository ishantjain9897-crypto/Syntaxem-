package com.example.core.database

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.database.dao.ChapterProgressDao
import com.example.core.database.dao.SyllabusDao
import com.example.core.database.entity.Chapter
import com.example.core.database.entity.ChapterCompletionState
import com.example.core.database.entity.ChapterProgress
import com.example.core.database.entity.Subject
import com.example.core.database.entity.Syllabus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ChapterProgressDaoTest {

    private lateinit var db: SyntaxemDatabase
    private lateinit var progressDao: ChapterProgressDao
    private lateinit var syllabusDao: SyllabusDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = SyntaxemDatabase.createInMemory(context)
        progressDao = db.chapterProgressDao()
        syllabusDao = db.syllabusDao()

        // Seed parent syllabus, subject, and chapter
        runBlocking {
            syllabusDao.insertSyllabus(
                Syllabus(
                    id = "curr_cbse_11",
                    authority = "CBSE / NCERT",
                    classLevel = 11,
                    stream = "Science",
                    academicSession = "2026–27",
                    isSelected = true
                )
            )

            syllabusDao.insertSubjects(
                listOf(
                    Subject(
                        id = "sub_math_11",
                        syllabusId = "curr_cbse_11",
                        name = "Mathematics",
                        code = "MATH-041",
                        displayOrder = 1
                    )
                )
            )

            syllabusDao.insertChapters(
                listOf(
                    Chapter(
                        id = "ch_sets",
                        subjectId = "sub_math_11",
                        chapterNumber = 1,
                        title = "Sets",
                        isApplicable = true,
                        displayOrder = 1
                    ),
                    Chapter(
                        id = "ch_relations",
                        subjectId = "sub_math_11",
                        chapterNumber = 2,
                        title = "Relations & Functions",
                        isApplicable = true,
                        displayOrder = 2
                    ),
                    Chapter(
                        id = "ch_trig",
                        subjectId = "sub_math_11",
                        chapterNumber = 3,
                        title = "Trigonometric Functions",
                        isApplicable = true,
                        displayOrder = 3
                    )
                )
            )
        }
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndTrackChapterCompletionStates() = runBlocking {
        // Initially no progress record exists
        val initial = progressDao.getProgressForChapterOnce("ch_trig")
        assertNull(initial)

        // 1. Mark in progress
        progressDao.markInProgress("ch_trig")
        val inProgress = progressDao.getProgressForChapterOnce("ch_trig")
        assertNotNull(inProgress)
        assertEquals(ChapterCompletionState.IN_PROGRESS, inProgress!!.status)
        assertEquals(ChapterCompletionState.IN_PROGRESS.value, inProgress.statusString)
        assertEquals(ChapterCompletionState.IN_PROGRESS, inProgress.completionState)
        assertFalse(inProgress.isCompleted)

        // 2. Mark completed
        val completedTimestamp = 1700000000000L
        progressDao.markCompleted("ch_trig", completedTimestamp)
        val completed = progressDao.getProgressForChapterOnce("ch_trig")
        assertNotNull(completed)
        assertEquals(ChapterCompletionState.COMPLETED, completed!!.status)
        assertEquals(ChapterCompletionState.COMPLETED.value, completed.statusString)
        assertTrue(completed.isCompleted)
        assertEquals(completedTimestamp, completed.completedAt)

        // 3. Mark needs revision
        progressDao.markNeedsRevision("ch_trig")
        val needsRevision = progressDao.getProgressForChapterOnce("ch_trig")
        assertNotNull(needsRevision)
        assertEquals(ChapterCompletionState.NEEDS_REVISION, needsRevision!!.status)
        assertEquals(ChapterCompletionState.NEEDS_REVISION.value, needsRevision.statusString)
        assertFalse(needsRevision.isCompleted)

        // 4. Reset to not started
        progressDao.resetToNotStarted("ch_trig")
        val reset = progressDao.getProgressForChapterOnce("ch_trig")
        assertNotNull(reset)
        assertEquals(ChapterCompletionState.NOT_STARTED, reset!!.status)
        assertEquals(ChapterCompletionState.NOT_STARTED.value, reset.statusString)
    }

    @Test
    fun updateNotesAndTargetDate() = runBlocking {
        progressDao.markInProgress("ch_sets")
        val target = System.currentTimeMillis() + 86400000L
        progressDao.updateNotesAndTargetDate("ch_sets", "Review Venn diagrams and power sets", target)

        val updated = progressDao.getProgressForChapterOnce("ch_sets")
        assertNotNull(updated)
        assertEquals("Review Venn diagrams and power sets", updated!!.personalNotes)
        assertEquals(target, updated.targetDate)
    }

    @Test
    fun queryChapterWithProgressRelation() = runBlocking {
        progressDao.markCompleted("ch_sets")
        progressDao.markNeedsRevision("ch_relations")

        val list = syllabusDao.getChaptersWithProgressForSubject("sub_math_11").first()
        assertEquals(3, list.size)

        val sets = list.first { it.chapter.id == "ch_sets" }
        assertEquals(ChapterCompletionState.COMPLETED.value, sets.status)
        assertTrue(sets.isCompleted)

        val relations = list.first { it.chapter.id == "ch_relations" }
        assertEquals(ChapterCompletionState.NEEDS_REVISION.value, relations.status)
        assertFalse(relations.isCompleted)

        val trig = list.first { it.chapter.id == "ch_trig" }
        assertEquals(ChapterCompletionState.NOT_STARTED.value, trig.status)
        assertNull(trig.progress)

        // Revision queue should contain relations
        val revisionQueue = syllabusDao.getRevisionQueue().first()
        assertEquals(1, revisionQueue.size)
        assertEquals("ch_relations", revisionQueue.first().chapter.id)
    }

    @Test
    fun subjectProgressCountsAreAccurate() = runBlocking {
        progressDao.markCompleted("ch_sets")
        progressDao.markCompleted("ch_relations")
        progressDao.markInProgress("ch_trig")

        val completedCount = progressDao.getCompletedCountForSubject("sub_math_11").first()
        assertEquals(2, completedCount)

        val incompleteCount = progressDao.getIncompleteCountForSubject("sub_math_11").first()
        assertEquals(1, incompleteCount)
    }

    @Test
    fun typeConvertersHandleStatesDatesAndStringListsCorrectly() {
        val converter = com.example.core.database.converter.SyntaxemTypeConverters()

        // 1. ChapterCompletionState
        assertEquals("COMPLETED", converter.fromCompletionState(ChapterCompletionState.COMPLETED))
        assertEquals(ChapterCompletionState.NEEDS_REVISION, converter.toCompletionState("NEEDS_REVISION"))
        assertEquals(ChapterCompletionState.NOT_STARTED, converter.toCompletionState(null))

        // 2. Date
        val now = java.util.Date()
        val timestamp = converter.fromDate(now)
        assertEquals(now.time, timestamp)
        val restoredDate = converter.toDate(timestamp)
        assertEquals(now, restoredDate)

        // 3. String list
        val topics = listOf("Venn Diagrams", "Subsets", "Power Set")
        val converted = converter.fromStringList(topics)
        assertTrue(converted.contains("Venn Diagrams"))
        val restoredList = converter.toStringList(converted)
        assertEquals(3, restoredList.size)
        assertEquals("Subsets", restoredList[1])
    }

    @Test
    fun entitiesWithConvertedTypesArePersistedAndRetrievedCorrectly() = runBlocking {
        // Insert chapter with topics (List<String>)
        syllabusDao.insertChapters(
            listOf(
                Chapter(
                    id = "ch_complex",
                    subjectId = "sub_math_11",
                    chapterNumber = 4,
                    title = "Complex Numbers",
                    topics = listOf("Argand Plane", "Polar Representation", "Modulus & Conjugate"),
                    isApplicable = true,
                    displayOrder = 4
                )
            )
        )

        // Insert progress with completionState and tags
        progressDao.upsertProgress(
            ChapterProgress(
                chapterId = "ch_complex",
                status = ChapterCompletionState.IN_PROGRESS,
                personalNotes = "Practice polar conversion formulas",
                tags = listOf("Important for JEE", "Algebra", "Vectors Connection")
            )
        )

        val retrieved = progressDao.getProgressForChapterOnce("ch_complex")
        assertNotNull(retrieved)
        assertEquals(ChapterCompletionState.IN_PROGRESS, retrieved!!.completionState)
        assertEquals(3, retrieved.tags.size)
        assertTrue(retrieved.tags.contains("Important for JEE"))

        val chapterWithProg = syllabusDao.getChapterWithProgressOnce("ch_complex")
        assertNotNull(chapterWithProg)
        assertEquals(3, chapterWithProg!!.chapter.topics.size)
        assertEquals("Polar Representation", chapterWithProg.chapter.topics[1])
    }
}

