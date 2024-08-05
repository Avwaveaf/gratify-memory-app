package com.avwaveaf.gratify.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.avwaveaf.gratify.MainActivity
import com.avwaveaf.gratify.databinding.FragmentAddMemoryBinding
import com.avwaveaf.gratify.models.Memory
import com.avwaveaf.gratify.viewmodels.MemoryViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Date
import java.util.UUID


class AddMemoryFragment : BottomSheetDialogFragment() {

    private var addMemoryFragmentBinding: FragmentAddMemoryBinding? = null
    private val binding
        get() = checkNotNull(addMemoryFragmentBinding)
        {
            "Add memory fragment binding are null"
        }

    // VIEW MODEL
    private lateinit var memoryViewModel: MemoryViewModel

    // SELECTED DATE
    private var selectedDate: Date = Date()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        addMemoryFragmentBinding = FragmentAddMemoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupShowDescriptionButton()
        setupSaveButton()
        setupAddDateButton()
    }

    private fun setupAddDateButton() {
        binding.btnSetDate.setOnClickListener{
            showCalendarDialog()
        }
    }

    private fun showCalendarDialog() {
        // Get the current date in UTC milliseconds
        val today = MaterialDatePicker.todayInUtcMilliseconds()

        // Configure the constraints for the date picker (e.g., max date as today)
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now()) // Only allow past dates, up to today
            .build()

        // Create the DatePicker with constraints
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setCalendarConstraints(constraintsBuilder)
            .setSelection(today) // Set the default selection to today
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            // Convert the selected timestamp to Date object
            selectedDate = Date(selection)
        }

        datePicker.show(parentFragmentManager, "MATERIAL_DATE_PICKER")
    }




    private fun setupShowDescriptionButton() {
        binding.btnShowDetails.setOnClickListener {
            binding.editTextMemoryDetail.visibility =
                if (binding.editTextMemoryDetail.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

    private fun setupSaveButton() {
        val editTextTitle = binding.editTextMemoryTitle
        val saveButton = binding.saveButton

        editTextTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int, before: Int, count: Int
            ) {
                val title = s.toString().trim()
                saveButton.isEnabled = title.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        saveButton.setOnClickListener {
            saveMemory()
        }
    }

    private fun saveMemory() {

        val title = binding.editTextMemoryTitle.text.toString().trim()
        val detail = binding.editTextMemoryDetail.text.toString().trim()
        val isFavorite = binding.cbIsFavorite.isChecked

        if (title.isNotEmpty()) {
            val newMemory = Memory(
                memoryId = UUID.randomUUID(),
                memoryTitle = title,
                memoryDetail = detail,
                date = selectedDate,
                isFavorite = isFavorite
            )
            memoryViewModel.addMemory(newMemory)
            Toast.makeText(requireContext(), "New Memory Added!", Toast.LENGTH_SHORT).show()
            dismiss()
        } else {
            Toast.makeText(requireContext(), "Title Cannot be null!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupViewModel() {
        memoryViewModel = (activity as MainActivity).memoryViewModel
    }

    override fun onDestroy() {
        super.onDestroy()
        addMemoryFragmentBinding = null
    }

}