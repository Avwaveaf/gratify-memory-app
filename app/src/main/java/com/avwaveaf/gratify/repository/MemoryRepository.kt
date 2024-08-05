package com.avwaveaf.gratify.repository

import com.avwaveaf.gratify.database.GratifyDatabase
import com.avwaveaf.gratify.models.Memory

class MemoryRepository(private val db: GratifyDatabase) {

    suspend fun insert(memory: Memory) = db.memoryDao().insert(memory)
    suspend fun update(memory: Memory) = db.memoryDao().update(memory)
    suspend fun delete(memory: Memory) = db.memoryDao().delete(memory)

    fun getAllMemory() = db.memoryDao().getAllMemory()
    fun searchMemory(searchQuery: String?) = db.memoryDao().searchMemories(searchQuery)

    fun getAllFavorite() = db.memoryDao().getAllFavorite()
}