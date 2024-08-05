package com.avwaveaf.gratify.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.avwaveaf.gratify.databinding.ItemMemoryLayoutBinding
import com.avwaveaf.gratify.fragments.HomeFragmentDirections
import com.avwaveaf.gratify.models.Memory
import com.avwaveaf.gratify.util.MemoryDateFormatter

class MemoryAdapter: RecyclerView.Adapter<MemoryAdapter.MemoryViewHolder>() {

    inner class MemoryViewHolder(val itemBinding: ItemMemoryLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Memory>() {
        override fun areItemsTheSame(oldItem: Memory, newItem: Memory): Boolean {
            return oldItem.memoryId == newItem.memoryId &&
                    oldItem.memoryTitle == newItem.memoryTitle &&
                    oldItem.memoryDetail == newItem.memoryDetail &&
                    oldItem.isFavorite == newItem.isFavorite
        }

        override fun areContentsTheSame(oldItem: Memory, newItem: Memory): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryViewHolder {
        return MemoryViewHolder(
            ItemMemoryLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MemoryViewHolder, position: Int) {
        val currentItem = differ.currentList[position]

        val formattedDate = MemoryDateFormatter.format(currentItem.date)

        holder.itemBinding.textViewMemoryTitle.text = currentItem.memoryTitle
        holder.itemBinding.textViewMemoryDate.text = formattedDate
        holder.itemBinding.textViewMemoryDescription.text = currentItem.memoryDetail
        holder.itemBinding.materialCbIsFavorite.isChecked = currentItem.isFavorite

        holder.itemView.setOnClickListener{
            val navDirection =
                HomeFragmentDirections.actionHomeFragmentToDetailMemoryFragment(currentItem)
            it.findNavController().navigate(navDirection)
        }
    }

}