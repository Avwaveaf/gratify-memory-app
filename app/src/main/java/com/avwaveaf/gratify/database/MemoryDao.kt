package com.avwaveaf.gratify.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.avwaveaf.gratify.models.Memory

@Dao
interface MemoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(memory: Memory)

    @Update
    suspend fun update(memory: Memory)

    @Delete
    suspend fun delete(memory: Memory)

    @Query("SELECT * FROM memory_data ORDER BY memory_id DESC")
    fun getAllMemory(): LiveData<List<Memory>>

    @Query("SELECT * FROM memory_data WHERE memory_title LIKE '%' || :searchQuery || '%' OR memory_detail LIKE '%' || :searchQuery || '%' ")
    fun searchMemories(searchQuery: String?): LiveData<List<Memory>>

    @Query("SELECT * FROM memory_data WHERE is_favorite=1")
    fun getAllFavorite():LiveData<List<Memory>>


}