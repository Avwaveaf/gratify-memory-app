package com.avwaveaf.gratify.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.avwaveaf.gratify.models.Memory

@Database(entities = [Memory::class], version = 1)
@TypeConverters(MemoryTypeConverter::class, UUIDTypeConverter::class)
abstract class GratifyDatabase: RoomDatabase() {
    abstract fun memoryDao(): MemoryDao

    companion object {
        @Volatile
        private var INSTANCE: GratifyDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = INSTANCE ?: synchronized(LOCK) {
            INSTANCE ?: createDatabase(context).also {
                INSTANCE = it
            }
        }

        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            GratifyDatabase::class.java,
            "noteapp_db"
        ).build()
    }
}
