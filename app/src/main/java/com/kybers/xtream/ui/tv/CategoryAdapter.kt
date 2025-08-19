package com.kybers.xtream.ui.tv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kybers.xtream.data.model.Channel
import com.kybers.xtream.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val onChannelClick: (Channel) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var categories: Map<String, List<Channel>> = emptyMap()
    private var expandedCategories = mutableSetOf<String>()

    class CategoryViewHolder(private val binding: ItemCategoryBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(
            categoryName: String, 
            channels: List<Channel>, 
            isExpanded: Boolean,
            onChannelClick: (Channel) -> Unit,
            onCategoryClick: (String) -> Unit
        ) {
            binding.tvCategoryName.text = categoryName
            binding.tvChannelCount.text = channels.size.toString()
            
            // Setup expand/collapse
            binding.ivExpand.rotation = if (isExpanded) 180f else 0f
            binding.rvChannels.visibility = if (isExpanded) View.VISIBLE else View.GONE
            
            binding.llCategoryHeader.setOnClickListener {
                onCategoryClick(categoryName)
            }
            
            // Setup channels RecyclerView
            if (isExpanded) {
                val channelAdapter = ChannelAdapter(channels, onChannelClick)
                binding.rvChannels.apply {
                    layoutManager = LinearLayoutManager(binding.root.context)
                    adapter = channelAdapter
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val categoryName = categories.keys.elementAt(position)
        val channels = categories[categoryName] ?: emptyList()
        val isExpanded = expandedCategories.contains(categoryName)
        
        holder.bind(categoryName, channels, isExpanded, onChannelClick) { category ->
            toggleCategory(category)
        }
    }

    override fun getItemCount() = categories.size
    
    fun updateCategories(newCategories: Map<String, List<Channel>>) {
        categories = newCategories
        notifyDataSetChanged()
    }
    
    private fun toggleCategory(categoryName: String) {
        if (expandedCategories.contains(categoryName)) {
            expandedCategories.remove(categoryName)
        } else {
            expandedCategories.add(categoryName)
        }
        notifyDataSetChanged()
    }
}