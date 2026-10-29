package com.example.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

/**
 * Chapter completion states representing student mastery.
 */
enum class ChapterCompletionState(val value: String) {
    NOT_STARTED("NOT_STARTED"),
    IN_PROGRESS("IN_PROGRESS"),
    COMPLETED("COMPLETED"),
    NEEDS_REVISION("NEEDS_REVISION");

    companion object {
        fun fromValue(value: String): ChapterCompletionState {
            return values().firstOrNull { it.value.equals(value, ignoreCase = true) } ?: NOT_STARTED
        }
    }
}

/**
 * Root academic syllabus metadata entity representing a curriculum authority,
 * session, stream, and class level.
 */
@Entity(tableName = "syllabus")
data class Syllabus(
    @PrimaryKey
    val id: String,
    val authority: String, // e.g. "CBSE / NCERT", "Vidya Bharati (Provisional)", "ICSE"
    val schoolVariant: String = "Standard All-India",
    val academicSession: String = "2026–27",
    val classLevel: Int = 11, // 6..12
    val stream: String = "Science", // "Science", "Commerce", "Humanities"
    val medium: String = "English", // "English", "Hindi"
    val version: String = "2026.1.0",
    val verificationStatus: String = "VERIFIED", // "VERIFIED", "PROVISIONAL", "CUSTOM"
    val sourceReference: String = "Official NCERT 2026–27 Curriculum Framework",
    val isSelected: Boolean = false
)

/**
 * Subject within an academic syllabus.
 */
@Entity(
    tableName = "subjects",
    foreignKeys = [
        ForeignKey(
            entity = Syllabus::class,
            parentColumns = ["id"],
            childColumns = ["syllabusId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["syllabusId"])]
)
data class Subject(
    @PrimaryKey
    val id: String,
    val syllabusId: String,
    val name: String, // e.g. "Mathematics", "Physics", "Chemistry"
    val code: String, // e.g. "MATH-041", "PHYS-042"
    val iconName: String = "calculate",
    val colorHex: String = "#6C6CF6",
    val displayOrder: Int = 0
)

/**
 * Chapter entity under a curriculum subject.
 */
@Entity(
    tableName = "chapters",
    foreignKeys = [
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["subjectId"])]
)
data class Chapter(
    @PrimaryKey
    val id: String,
    val subjectId: String,
    val chapterNumber: Int,
    val title: String,
    val description: String = "",
    val topics: List<String> = emptyList(),
    val isApplicable: Boolean = true,
    val displayOrder: Int = 0
)

/**
 * Student progress tracking entity for chapter completion states.
 */
@Entity(
    tableName = "chapter_progress",
    foreignKeys = [
        ForeignKey(
            entity = Chapter::class,
            parentColumns = ["id"],
            childColumns = ["chapterId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ChapterProgress(
    @PrimaryKey
    val chapterId: String,
    val status: ChapterCompletionState = ChapterCompletionState.NOT_STARTED,
    val completedAt: Long? = null,
    val lastUpdatedAt: Long = System.currentTimeMillis(),
    val targetDate: Long? = null,
    val personalNotes: String? = null,
    val revisionDueAt: Long? = null,
    val tags: List<String> = emptyList(),
    val confidenceLevel: Int = 0 // 1-5 rating
) {
    val completionState: ChapterCompletionState
        get() = status

    val statusString: String
        get() = status.value

    val isCompleted: Boolean
        get() = status == ChapterCompletionState.COMPLETED
}

/**
 * 1-to-1 Room relation pairing a chapter with its recorded progress.
 */
data class ChapterWithProgress(
    @Embedded
    val chapter: Chapter,
    @Relation(
        parentColumn = "id",
        entityColumn = "chapterId"
    )
    val progress: ChapterProgress?
) {
    val status: String
        get() = progress?.status?.value ?: ChapterCompletionState.NOT_STARTED.value

    val completionState: ChapterCompletionState
        get() = progress?.status ?: ChapterCompletionState.NOT_STARTED

    val isCompleted: Boolean
        get() = progress?.isCompleted ?: false
}
