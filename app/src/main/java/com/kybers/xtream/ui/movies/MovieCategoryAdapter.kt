package com.kybers.xtream.ui.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kybers.xtream.data.model.Movie
import com.kybers.xtream.databinding.ItemMovieCategoryBinding

class MovieCategoryAdapter(
    private val onMovieClick: (Movie) -> Unit,
    private val onFavoriteClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieCategoryAdapter.CategoryViewHolder>() {

    private var categories: Map<String, List<Movie>> = emptyMap()
    private var expandedCategories = mutableSetOf<String>()

    class CategoryViewHolder(private val binding: ItemMovieCategoryBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(
            categoryName: String, 
            movies: List<Movie>, 
            isExpanded: Boolean,
            onMovieClick: (Movie) -> Unit,
            onFavoriteClick: (Movie) -> Unit,
            onCategoryClick: (String) -> Unit
        ) {
            binding.tvCategoryName.text = categoryName
            binding.tvMovieCount.text = movies.size.toString()
            
            // Setup expand/collapse
            binding.ivExpand.rotation = if (isExpanded) 180f else 0f
            binding.rvMovies.visibility = if (isExpanded) View.VISIBLE else View.GONE
            
            binding.llCategoryHeader.setOnClickListener {
                onCategoryClick(categoryName)
            }
            
            // Setup movies RecyclerView
            if (isExpanded) {
                val movieAdapter = MovieAdapter(movies, onMovieClick, onFavoriteClick)
                binding.rvMovies.apply {
                    layoutManager = LinearLayoutManager(binding.root.context)
                    adapter = movieAdapter
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemMovieCategoryBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val categoryName = categories.keys.elementAt(position)
        val movies = categories[categoryName] ?: emptyList()
        val isExpanded = expandedCategories.contains(categoryName)
        
        holder.bind(categoryName, movies, isExpanded, onMovieClick, onFavoriteClick) { category ->
            toggleCategory(category)
        }
    }

    override fun getItemCount() = categories.size
    
    fun updateCategories(newCategories: Map<String, List<Movie>>) {
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