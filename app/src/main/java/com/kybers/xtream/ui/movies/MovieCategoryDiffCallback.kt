package com.kybers.xtream.ui.movies

import androidx.recyclerview.widget.DiffUtil
import com.kybers.xtream.data.model.Movie

data class CategoryItem(
    val name: String,
    val movies: List<Movie>,
    val movieCount: Int = movies.size
)

class MovieCategoryDiffCallback : DiffUtil.ItemCallback<CategoryItem>() {
    
    override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
        return oldItem.name == newItem.name && 
               oldItem.movieCount == newItem.movieCount &&
               oldItem.movies.size == newItem.movies.size
    }

    override fun getChangePayload(oldItem: CategoryItem, newItem: CategoryItem): Any? {
        val changes = mutableListOf<String>()
        
        if (oldItem.movieCount != newItem.movieCount) {
            changes.add("count")
        }
        
        if (oldItem.movies != newItem.movies) {
            changes.add("movies")
        }
        
        return if (changes.isNotEmpty()) changes else null
    }
}