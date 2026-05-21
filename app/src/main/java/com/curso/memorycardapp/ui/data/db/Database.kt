package com.curso.memorycardapp.ui.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PartidaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MemoryCardDatabase : RoomDatabase() {

    abstract fun partidaDao(): PartidaDao

    companion object {
        private const val DB_NAME = "memory_card_db"

        @Volatile
        private var INSTANCE: MemoryCardDatabase? = null

        fun getInstance(context: Context): MemoryCardDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MemoryCardDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}