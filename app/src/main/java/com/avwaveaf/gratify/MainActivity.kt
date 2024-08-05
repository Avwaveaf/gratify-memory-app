package com.avwaveaf.gratify

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.avwaveaf.gratify.database.GratifyDatabase
import com.avwaveaf.gratify.repository.MemoryRepository
import com.avwaveaf.gratify.viewmodels.MemoryViewModel
import com.avwaveaf.gratify.viewmodels.MemoryViewModelFactory

class MainActivity : AppCompatActivity() {


    lateinit var memoryViewModel: MemoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViewModel()
    }

    private fun setupViewModel() {
        val repos = MemoryRepository(GratifyDatabase(this))
        val memoryViewModelFactory = MemoryViewModelFactory(application, repos)
        memoryViewModel =
            ViewModelProvider(this, memoryViewModelFactory)[MemoryViewModel::class.java]
    }
}