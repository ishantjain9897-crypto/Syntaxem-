package com.example.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.core.database.converter.SyntaxemTypeConverters
import com.example.core.database.dao.ChapterProgressDao
import com.example.core.database.dao.SyllabusDao
import com.example.core.database.entity.Chapter
import com.example.core.database.entity.ChapterProgress
import com.example.core.database.entity.Subject
import com.example.core.database.entity.Syllabus

/**
 * Main Room database for core:database containing Syllabus, Subject, Chapter, and ChapterProgress.
 */
@Database(
    entities = [
        Syllabus::class,
        Subject::class,
        Chapter::class,
        ChapterProgress::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(SyntaxemTypeConverters::class)
abstract class SyntaxemDatabase : RoomDatabase() {

    abstract fun chapterProgressDao(): ChapterProgressDao
    abstract fun syllabusDao(): SyllabusDao

    companion object {
        const val DATABASE_NAME = "syntaxem_curriculum.db"

        @Volatile
        private var INSTANCE: SyntaxemDatabase? = null

        fun getInstance(context: Context): SyntaxemDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SyntaxemDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun createInMemory(context: Context): SyntaxemDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                SyntaxemDatabase::class.java
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}
