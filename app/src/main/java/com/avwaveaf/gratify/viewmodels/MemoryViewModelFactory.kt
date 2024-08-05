package com.avwaveaf.gratify.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.avwaveaf.gratify.repository.MemoryRepository

class MemoryViewModelFactory(val app: Application, private val repository: MemoryRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MemoryViewModel(app, repository) as T
    }
}