package com.avwaveaf.gratify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.avwaveaf.gratify.models.Memory
import com.avwaveaf.gratify.repository.MemoryRepository
import kotlinx.coroutines.launch

class MemoryViewModel(app: Application, private val repository: MemoryRepository) :
    AndroidViewModel(app) {
    fun addMemory(memory: Memory) = viewModelScope.launch {
        repository.insert(memory)
    }

    fun updateMemory(memory: Memory) = viewModelScope.launch {
        repository.update(memory)
    }

    fun deleteMemory(memory: Memory) = viewModelScope.launch {
        repository.delete(memory)
    }

    fun getAllMemory() = repository.getAllMemory()
    fun searchMemories(searchQuery: String?) = repository.searchMemory(searchQuery)

    fun getAllFavorite() = repository.getAllFavorite()
}