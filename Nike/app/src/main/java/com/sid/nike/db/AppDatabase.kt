package com.sid.nike.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sid.nike.db.dao.DefinitionDao
import com.sid.nike.db.dto.Definition

@Database(entities = [Definition::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun DefinitionDao(): DefinitionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private val lock = Any()

        fun getInstance(application: Application): AppDatabase =
            INSTANCE ?: synchronized(lock) {
                INSTANCE ?: buildInstance(application).also { INSTANCE = it }
            }

        private fun buildInstance(application: Application): AppDatabase =
            Room.databaseBuilder(
                application.applicationContext,
                AppDatabase::class.java,
                "nike.db"
            ).build()
    }

}