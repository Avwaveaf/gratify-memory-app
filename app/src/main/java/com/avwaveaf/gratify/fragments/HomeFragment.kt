package com.avwaveaf.gratify.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.avwaveaf.gratify.MainActivity
import com.avwaveaf.gratify.R
import com.avwaveaf.gratify.adapters.MemoryAdapter
import com.avwaveaf.gratify.databinding.FragmentHomeBinding
import com.avwaveaf.gratify.models.Memory
import com.avwaveaf.gratify.viewmodels.MemoryViewModel


class HomeFragment : Fragment(R.layout.fragment_home), SearchView.OnQueryTextListener,
    MenuProvider {

    private var homeFragmentBinding: FragmentHomeBinding? = null
    private val binding
        get() = checkNotNull(homeFragmentBinding) {
            "Home Fragment Binding are Null!!"
        }

    // VIEW MODEL
    private lateinit var memoryViewModel: MemoryViewModel

    // ADAPTER
    private lateinit var mainMemoryAdapter: MemoryAdapter
    private lateinit var favoriteMemoryAdapter: MemoryAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        homeFragmentBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        // setup menu provider
        requireActivity().addMenuProvider(this, viewLifecycleOwner)

        setupInitConfig()
        setupRecyclerView()

        setupShowAddDialog()
    }


    private fun setupShowAddDialog() {
        binding.fabAddNewMemory.setOnClickListener {
            showAddMemoryDialog()
        }
    }

    private fun showAddMemoryDialog() {
        val addMemoryFragment = AddMemoryFragment()
        addMemoryFragment.show(childFragmentManager, "AddTaskDialog")
    }

    private fun setupRecyclerView() {
        mainMemoryAdapter = MemoryAdapter()
        favoriteMemoryAdapter = MemoryAdapter()

        // setup the main recyclerview
        binding.mainMemoryRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            adapter = mainMemoryAdapter
        }

        // setup favorite only recycler view
        binding.favoriteMemoryRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = favoriteMemoryAdapter

        }

        activity?.let {
            memoryViewModel.getAllMemory().observe(viewLifecycleOwner) { list ->
                mainMemoryAdapter.differ.submitList(list)
                updateGeneralUI(list)
            }

            memoryViewModel.getAllFavorite().observe(viewLifecycleOwner) { favs ->
                favoriteMemoryAdapter.differ.submitList(favs)
                updateFavoriteSectionUI(favs)
            }
        }


    }

    private fun updateGeneralUI(list: List<Memory>?) {
        if (list != null) {
            if (list.isNotEmpty()) {
                binding.imgNoItem.visibility = View.GONE
                binding.tvNoItemDescription.visibility = View.GONE

                binding.tvFavorite.visibility = View.VISIBLE
                binding.tvYourMemoriesTitle.visibility = View.VISIBLE

                // visibility on both recycler view
                binding.favoriteMemoryRecyclerView.visibility = View.VISIBLE
                binding.mainMemoryRecyclerView.visibility = View.VISIBLE

            } else {
                binding.imgNoItem.visibility = View.VISIBLE
                binding.tvNoItemDescription.visibility = View.VISIBLE

                binding.tvYourMemoriesTitle.visibility = View.GONE
                binding.tvFavorite.visibility = View.GONE

                // visibility on both recycler view
                binding.favoriteMemoryRecyclerView.visibility = View.GONE
                binding.mainMemoryRecyclerView.visibility = View.GONE

            }
        }
    }

    private fun updateFavoriteSectionUI(favs: List<Memory>?) {
        if (favs != null) {
            if (favs.isNotEmpty()) {
                binding.tvFavorite.visibility = View.VISIBLE
                binding.favoriteMemoryRecyclerView.visibility = View.VISIBLE
            } else {
                binding.tvFavorite.visibility = View.GONE
                binding.favoriteMemoryRecyclerView.visibility = View.GONE
            }
        }
    }

    private fun setupInitConfig() {
        memoryViewModel = (activity as MainActivity).memoryViewModel
    }


    override fun onDestroyView() {
        super.onDestroyView()
        homeFragmentBinding = null
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.homeToolbar)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            searchNotes(newText)
        }
        return true
    }

    private fun searchNotes(searchQuery: String?) {
        if (searchQuery.isNullOrEmpty()) {
            memoryViewModel.getAllMemory().observe(this) { memories ->
                mainMemoryAdapter.differ.submitList(memories)
            }
            return
        }

        val searchString = searchQuery.replace("%", "\\%") // Escape wildcard character
        memoryViewModel.searchMemories(searchString).observe(this) { memoriesFound ->
            mainMemoryAdapter.differ.submitList(memoriesFound)
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.home_menu, menu)
        val searchItem = menu.findItem(R.id.search_menu)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search your memories"

        searchView.setOnQueryTextListener(this)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }
}