package com.kybers.xtream.ui.tv

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kybers.xtream.data.model.Channel
import com.kybers.xtream.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val onChannelClick: (Channel) -> Unit,
    private val onFavoriteClick: (Channel) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var categories: Map<String, List<Channel>> = emptyMap()
    private var expandedCategory: String? = null

    class CategoryViewHolder(private val binding: ItemCategoryBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(
            categoryName: String, 
            channels: List<Channel>, 
            isExpanded: Boolean,
            onChannelClick: (Channel) -> Unit,
            onFavoriteClick: (Channel) -> Unit,
            onCategoryClick: (String) -> Unit
        ) {
            binding.tvCategoryName.text = categoryName
            binding.tvChannelCount.text = channels.size.toString()
            
            // Animate expand/collapse icon
            val targetRotation = if (isExpanded) 180f else 0f
            if (binding.ivExpand.rotation != targetRotation) {
                ObjectAnimator.ofFloat(binding.ivExpand, "rotation", binding.ivExpand.rotation, targetRotation).apply {
                    duration = 200
                    start()
                }
            }
            
            // Show/hide content
            binding.rvChannels.visibility = if (isExpanded) View.VISIBLE else View.GONE
            binding.separator.visibility = if (isExpanded) View.VISIBLE else View.GONE
            
            binding.llCategoryHeader.setOnClickListener {
                onCategoryClick(categoryName)
            }
            
            // Setup channels RecyclerView
            if (isExpanded) {
                val channelAdapter = ChannelAdapter(
                    onChannelClick = onChannelClick,
                    onFavoriteClick = onFavoriteClick
                )
                channelAdapter.updateChannels(channels)
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
        val isExpanded = expandedCategory == categoryName
        
        holder.bind(categoryName, channels, isExpanded, onChannelClick, onFavoriteClick) { category ->
            toggleCategory(category)
        }
    }

    override fun getItemCount() = categories.size
    
    fun updateCategories(newCategories: Map<String, List<Channel>>) {
        categories = newCategories
        notifyDataSetChanged()
    }
    
    private fun toggleCategory(categoryName: String) {
        val previousExpanded = expandedCategory
        expandedCategory = if (expandedCategory == categoryName) {
            null // Contraer la categoría actual
        } else {
            categoryName // Expandir nueva categoría
        }
        
        // Notificar cambios solo para las categorías afectadas
        if (previousExpanded != null) {
            val previousIndex = categories.keys.indexOf(previousExpanded)
            if (previousIndex >= 0) notifyItemChanged(previousIndex)
        }
        
        val currentIndex = categories.keys.indexOf(categoryName)
        if (currentIndex >= 0) notifyItemChanged(currentIndex)
    }
}