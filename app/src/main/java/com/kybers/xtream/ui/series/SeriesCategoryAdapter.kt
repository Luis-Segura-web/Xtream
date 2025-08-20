package com.kybers.xtream.ui.series

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kybers.xtream.data.model.Series
import com.kybers.xtream.databinding.ItemSeriesCategoryBinding

class SeriesCategoryAdapter(
    private val onSeriesClick: (Series) -> Unit,
    private val onFavoriteClick: (Series) -> Unit
) : ListAdapter<SeriesCategoryItem, SeriesCategoryAdapter.CategoryViewHolder>(SeriesCategoryDiffCallback()) {

    private var expandedCategories = mutableSetOf<String>()
    private val seriesAdapterCache = mutableMapOf<String, SeriesAdapter>()

    class CategoryViewHolder(val binding: ItemSeriesCategoryBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(
            categoryItem: SeriesCategoryItem, 
            isExpanded: Boolean,
            onSeriesClick: (Series) -> Unit,
            onFavoriteClick: (Series) -> Unit,
            seriesAdapterCache: MutableMap<String, SeriesAdapter>,
            onCategoryClick: (String) -> Unit
        ) {
            binding.tvCategoryName.text = categoryItem.name
            binding.tvSeriesCount.text = categoryItem.seriesCount.toString()
            
            // Setup expand/collapse with animation
            val targetRotation = if (isExpanded) 180f else 0f
            if (binding.ivExpand.rotation != targetRotation) {
                binding.ivExpand.animate()
                    .rotation(targetRotation)
                    .setDuration(200)
                    .start()
            }
            
            binding.rvSeries.visibility = if (isExpanded) View.VISIBLE else View.GONE
            
            binding.llCategoryHeader.setOnClickListener {
                onCategoryClick(categoryItem.name)
            }
            
            // Setup series RecyclerView with caching
            if (isExpanded) {
                var seriesAdapter = seriesAdapterCache[categoryItem.name]
                if (seriesAdapter == null) {
                    seriesAdapter = SeriesAdapter(categoryItem.series, onSeriesClick, onFavoriteClick)
                    seriesAdapterCache[categoryItem.name] = seriesAdapter
                    
                    binding.rvSeries.apply {
                        layoutManager = LinearLayoutManager(binding.root.context)
                        adapter = seriesAdapter
                        // Optimizar RecyclerView
                        setHasFixedSize(true)
                        setItemViewCacheSize(20)
                    }
                } else {
                    // Actualizar datos existentes si han cambiado
                    seriesAdapter.updateSeries(categoryItem.series)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemSeriesCategoryBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val categoryItem = getItem(position)
        val isExpanded = expandedCategories.contains(categoryItem.name)
        
        holder.bind(categoryItem, isExpanded, onSeriesClick, onFavoriteClick, seriesAdapterCache) { category ->
            toggleCategory(category)
        }
    }
    
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            val categoryItem = getItem(position)
            val isExpanded = expandedCategories.contains(categoryItem.name)
            
            // Solo actualizar los cambios específicos
            val changes = payloads.firstOrNull() as? List<String>
            if (changes != null) {
                if ("count" in changes) {
                    holder.binding.tvSeriesCount.text = categoryItem.seriesCount.toString()
                }
                if ("series" in changes && isExpanded) {
                    seriesAdapterCache[categoryItem.name]?.updateSeries(categoryItem.series)
                }
            }
        } else {
            onBindViewHolder(holder, position)
        }
    }
    
    fun updateCategories(newCategories: Map<String, List<Series>>) {
        val categoryItems = newCategories.map { (name, series) ->
            SeriesCategoryItem(name, series)
        }
        submitList(categoryItems)
    }
    
    private fun toggleCategory(categoryName: String) {
        val position = currentList.indexOfFirst { it.name == categoryName }
        if (position != -1) {
            if (expandedCategories.contains(categoryName)) {
                expandedCategories.remove(categoryName)
            } else {
                expandedCategories.add(categoryName)
            }
            notifyItemChanged(position)
        }
    }
    
    fun clearCache() {
        seriesAdapterCache.clear()
    }
}