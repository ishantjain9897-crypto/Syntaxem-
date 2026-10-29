package com.example.syntaxem.data.local

import android.content.Context
import com.example.core.database.SyntaxemDatabase
import com.example.core.database.dao.ChapterProgressDao
import com.example.core.database.dao.SyllabusDao

/**
 * Convenience provider configuring and exposing the core:database
 * RoomDatabase implementation for the Syntaxem app module.
 */
object SyntaxemDatabaseProvider {

    fun getDatabase(context: Context): SyntaxemDatabase {
        return SyntaxemDatabase.getInstance(context)
    }

    fun getChapterProgressDao(context: Context): ChapterProgressDao {
        return getDatabase(context).chapterProgressDao()
    }

    fun getSyllabusDao(context: Context): SyllabusDao {
        return getDatabase(context).syllabusDao()
    }
}
