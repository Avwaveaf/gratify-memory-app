package com.avwaveaf.gratify.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.avwaveaf.gratify.MainActivity
import com.avwaveaf.gratify.R
import com.avwaveaf.gratify.databinding.FragmentDetailMemoryBinding
import com.avwaveaf.gratify.models.Memory
import com.avwaveaf.gratify.util.MemoryDateFormatter
import com.avwaveaf.gratify.viewmodels.MemoryViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class DetailMemoryFragment : Fragment(R.layout.fragment_detail_memory), MenuProvider {

    // BINDING
    private var detailmemoryBinding: FragmentDetailMemoryBinding? = null
    private val binding
        get() = checkNotNull(detailmemoryBinding)
        {
            "Fragment Detail Memory Binding are currently null"
        }

    // VIEW MODEL
    private lateinit var memoryViewModel: MemoryViewModel

    // CURRENT NOTE
    private lateinit var currentMemory: Memory

    // CATCH ARGS
    private val args: DetailMemoryFragmentArgs by navArgs()

    // CHANGES FLAGS
    private var isTitleChanged = false
    private var isDetailChanged = false
    private var isFavoriteChanged = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        detailmemoryBinding = FragmentDetailMemoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupInitConfig()
        setupChangeListener()
    }

    private fun setupChangeListener() {
        binding.editTextMemoryTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int, before: Int, count: Int
            ) {
                isTitleChanged = s.toString().trim() != currentMemory.memoryTitle
                checkFabVisibility()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.editTextMemoryDetail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int, before: Int, count: Int
            ) {
                isDetailChanged = s.toString().trim() != currentMemory.memoryDetail
                checkFabVisibility()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.materialCbIsFavorited.setOnCheckedChangeListener { _, isChecked ->
            isFavoriteChanged = isChecked != currentMemory.isFavorite
            checkFabVisibility()
        }
    }

    private fun checkFabVisibility() {
        val hasChanges = isTitleChanged || isDetailChanged || isFavoriteChanged
        binding.fabEdit.isEnabled = hasChanges
    }

    private fun setupInitConfig() {
        memoryViewModel = (activity as MainActivity).memoryViewModel
        // initialize current memory
        currentMemory = args.memory!!
        updateUI()
        setupFab()
    }

    private fun setupFab() {
        binding.fabDelete.setOnClickListener {
            deleteMemory()
        }

        binding.fabEdit.setOnClickListener {
            val newTitle = binding.editTextMemoryTitle.text.toString().trim()
            val newDetail = binding.editTextMemoryDetail.text.toString().trim()
            val newIsFavorite = binding.materialCbIsFavorited.isChecked

            if (newTitle.isNotEmpty()) {
                val updatedMemory = Memory(
                    memoryId = currentMemory.memoryId,
                    memoryTitle = newTitle,
                    memoryDetail = newDetail,
                    isFavorite = newIsFavorite,
                    date = currentMemory.date
                )

                // update via viewmodel
                memoryViewModel.updateMemory(updatedMemory)


                // Reset change flags
                isTitleChanged = false
                isDetailChanged = false
                isFavoriteChanged = false
                checkFabVisibility()

                // pop back
                view?.findNavController()?.popBackStack(R.id.homeFragment, false)
            } else {
                Toast.makeText(context, "Memory title cannot be empty!!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun updateUI() {
        binding.editTextMemoryTitle.setText(currentMemory.memoryTitle)
        binding.editTextMemoryDetail.setText(currentMemory.memoryDetail)
        binding.editTextMemoryDate.setText(MemoryDateFormatter.format(currentMemory.date))
        binding.materialCbIsFavorited.isChecked = currentMemory.isFavorite
    }

    private fun deleteMemory() {
        val builder = MaterialAlertDialogBuilder(requireActivity())

        builder.setTitle("Delete this Memory")
            .setMessage("Are you sure you want to delete this Memory?")
            .setPositiveButton("Delete") { dialog, _ ->
                memoryViewModel.deleteMemory(currentMemory)
                Toast.makeText(context, "Note deleted Successfully!", Toast.LENGTH_SHORT).show()
                dialog.dismiss() // Dismiss the dialog after positive action
                view?.findNavController()?.popBackStack(R.id.homeFragment, false)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss() // Dismiss the dialog on cancel
            }

        builder.create().show()
    }

    private fun setupToolbar() {
        // setup the title in toolbar for later on
        binding.materialToolbarDetailMemory.title = ""
        (activity as AppCompatActivity).setSupportActionBar(binding.materialToolbarDetailMemory)
        // add menu provider
        (activity as AppCompatActivity).addMenuProvider(this, viewLifecycleOwner)
    }

    override fun onDestroy() {
        super.onDestroy()
        detailmemoryBinding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.detail_memory_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.share_menu -> {
                shareMemory()
                true
            }

            else -> false

        }
    }

    private fun shareMemory() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, currentMemory.memoryTitle) // Title as subject
            putExtra(
                Intent.EXTRA_TEXT,
                formatMemoryForSharing(
                    currentMemory.memoryTitle,
                    MemoryDateFormatter.format(currentMemory.date),
                    currentMemory.memoryDetail
                )
            )
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Sharing My Memory"))
    }
    private fun formatMemoryForSharing(title: String, date: String, detail: String): String {
        return """
        Hey There, I would like to share my experience and beautiful memories.
        All written in my Gratify App.

        **$title** 
        $date

        $detail
    """.trimIndent()
    }
}