package com.example.syntaxem.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.core.database.converter.SyntaxemTypeConverters
import com.example.syntaxem.data.local.dao.SyllabusDao
import com.example.syntaxem.data.local.entity.*

@Database(
    entities = [
        CurriculumEntity::class,
        SubjectEntity::class,
        ChapterEntity::class,
        ChapterProgressEntity::class,
        StatusHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(SyntaxemTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun syllabusDao(): SyllabusDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "syntaxem_syllabus.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
