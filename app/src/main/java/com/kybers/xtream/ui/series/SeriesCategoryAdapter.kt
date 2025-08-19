package com.kybers.xtream.ui.series

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kybers.xtream.data.model.Series
import com.kybers.xtream.databinding.ItemSeriesCategoryBinding

class SeriesCategoryAdapter(
    private val onSeriesClick: (Series) -> Unit,
    private val onFavoriteClick: (Series) -> Unit
) : RecyclerView.Adapter<SeriesCategoryAdapter.CategoryViewHolder>() {

    private var categories: Map<String, List<Series>> = emptyMap()
    private var expandedCategories = mutableSetOf<String>()

    class CategoryViewHolder(private val binding: ItemSeriesCategoryBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(
            categoryName: String, 
            series: List<Series>, 
            isExpanded: Boolean,
            onSeriesClick: (Series) -> Unit,
            onFavoriteClick: (Series) -> Unit,
            onCategoryClick: (String) -> Unit
        ) {
            binding.tvCategoryName.text = categoryName
            binding.tvSeriesCount.text = series.size.toString()
            
            // Setup expand/collapse
            binding.ivExpand.rotation = if (isExpanded) 180f else 0f
            binding.rvSeries.visibility = if (isExpanded) View.VISIBLE else View.GONE
            
            binding.llCategoryHeader.setOnClickListener {
                onCategoryClick(categoryName)
            }
            
            // Setup series RecyclerView
            if (isExpanded) {
                val seriesAdapter = SeriesAdapter(series, onSeriesClick, onFavoriteClick)
                binding.rvSeries.apply {
                    layoutManager = LinearLayoutManager(binding.root.context)
                    adapter = seriesAdapter
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
        val categoryName = categories.keys.elementAt(position)
        val series = categories[categoryName] ?: emptyList()
        val isExpanded = expandedCategories.contains(categoryName)
        
        holder.bind(categoryName, series, isExpanded, onSeriesClick, onFavoriteClick) { category ->
            toggleCategory(category)
        }
    }

    override fun getItemCount() = categories.size
    
    fun updateCategories(newCategories: Map<String, List<Series>>) {
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