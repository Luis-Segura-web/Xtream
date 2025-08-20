package com.kybers.xtream.ui.series

import androidx.recyclerview.widget.DiffUtil
import com.kybers.xtream.data.model.Series

data class SeriesCategoryItem(
    val name: String,
    val series: List<Series>,
    val seriesCount: Int = series.size
)

class SeriesCategoryDiffCallback : DiffUtil.ItemCallback<SeriesCategoryItem>() {
    
    override fun areItemsTheSame(oldItem: SeriesCategoryItem, newItem: SeriesCategoryItem): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: SeriesCategoryItem, newItem: SeriesCategoryItem): Boolean {
        return oldItem.name == newItem.name && 
               oldItem.seriesCount == newItem.seriesCount &&
               oldItem.series.size == newItem.series.size
    }

    override fun getChangePayload(oldItem: SeriesCategoryItem, newItem: SeriesCategoryItem): Any? {
        val changes = mutableListOf<String>()
        
        if (oldItem.seriesCount != newItem.seriesCount) {
            changes.add("count")
        }
        
        if (oldItem.series != newItem.series) {
            changes.add("series")
        }
        
        return if (changes.isNotEmpty()) changes else null
    }
}