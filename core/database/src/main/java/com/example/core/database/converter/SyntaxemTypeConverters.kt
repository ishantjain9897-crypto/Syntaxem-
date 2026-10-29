package com.example.core.database.converter

import androidx.room.TypeConverter
import com.example.core.database.entity.ChapterCompletionState
import java.util.Date

/**
 * Room TypeConverters for Syntaxem entities:
 * 1. ChapterCompletionState <-> String
 * 2. Date <-> Long timestamp
 * 3. List<String> <-> delimited String for topic tags and prerequisites
 */
class SyntaxemTypeConverters {

    @TypeConverter
    fun fromCompletionState(state: ChapterCompletionState?): String {
        return state?.value ?: ChapterCompletionState.NOT_STARTED.value
    }

    @TypeConverter
    fun toCompletionState(value: String?): ChapterCompletionState {
        return value?.let { ChapterCompletionState.fromValue(it) } ?: ChapterCompletionState.NOT_STARTED
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        if (list.isNullOrEmpty()) return ""
        return list.joinToString(SEPARATOR)
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        if (data.isNullOrBlank()) return emptyList()
        return data.split(SEPARATOR).map { it.trim() }.filter { it.isNotEmpty() }
    }

    companion object {
        private const val SEPARATOR = "::"
    }
}
