package com.example.syntaxem.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Education Board / Curriculum Authority metadata.
 */
@Entity(tableName = "curricula")
data class CurriculumEntity(
    @PrimaryKey val id: String,
    val authority: String, // e.g. "CBSE / NCERT", "Vidya Bharati (Provisional)", "ICSE"
    val schoolVariant: String, // e.g. "Standard All-India", "Vidya Bharati Akhil Bharatiya Shiksha Sansthan"
    val academicSession: String, // e.g. "2026–27"
    val classLevel: Int, // 6..12, default 11
    val stream: String, // "Science", "Commerce", "Humanities"
    val medium: String, // "English", "Hindi"
    val version: String, // e.g. "2026.1.0"
    val verificationStatus: String, // "VERIFIED", "PROVISIONAL", "CUSTOM"
    val sourceReference: String, // Source document citation
    val isSelected: Boolean = false
)

/**
 * Subject within an academic curriculum.
 */
@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val id: String,
    val curriculumId: String,
    val name: String, // e.g. "Mathematics", "Physics", "Chemistry"
    val code: String, // e.g. "MATH-041", "PHYS-042"
    val iconName: String = "calculate",
    val colorHex: String = "#6C6CF6",
    val displayOrder: Int
)

/**
 * A curriculum chapter.
 */
@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey val id: String,
    val subjectId: String,
    val chapterNumber: Int,
    val title: String,
    val description: String,
    val isApplicable: Boolean = true,
    val displayOrder: Int
)

/**
 * Student's progress and notes for a chapter.
 */
@Entity(tableName = "chapter_progress")
data class ChapterProgressEntity(
    @PrimaryKey val chapterId: String,
    val status: String, // "NOT_STARTED", "IN_PROGRESS", "COMPLETED", "NEEDS_REVISION"
    val completedAt: Long? = null,
    val lastUpdatedAt: Long = System.currentTimeMillis(),
    val targetDate: Long? = null,
    val personalNotes: String? = null,
    val revisionDueAt: Long? = null
)

/**
 * Historical log of status changes for reliable Undo support.
 */
@Entity(tableName = "status_history")
data class StatusHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val chapterId: String,
    val previousStatus: String,
    val newStatus: String,
    val changedAt: Long = System.currentTimeMillis()
)
