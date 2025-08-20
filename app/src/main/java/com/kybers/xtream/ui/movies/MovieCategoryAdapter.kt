package com.kybers.xtream.ui.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kybers.xtream.data.model.Movie
import com.kybers.xtream.databinding.ItemMovieCategoryBinding

class MovieCategoryAdapter(
    private val onMovieClick: (Movie) -> Unit,
    private val onFavoriteClick: (Movie) -> Unit
) : ListAdapter<CategoryItem, MovieCategoryAdapter.CategoryViewHolder>(MovieCategoryDiffCallback()) {

    private var expandedCategories = mutableSetOf<String>()
    private val movieAdapterCache = mutableMapOf<String, MovieAdapter>()

    class CategoryViewHolder(val binding: ItemMovieCategoryBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(
            categoryItem: CategoryItem, 
            isExpanded: Boolean,
            onMovieClick: (Movie) -> Unit,
            onFavoriteClick: (Movie) -> Unit,
            movieAdapterCache: MutableMap<String, MovieAdapter>,
            onCategoryClick: (String) -> Unit
        ) {
            binding.tvCategoryName.text = categoryItem.name
            binding.tvMovieCount.text = categoryItem.movieCount.toString()
            
            // Setup expand/collapse with animation
            val targetRotation = if (isExpanded) 180f else 0f
            if (binding.ivExpand.rotation != targetRotation) {
                binding.ivExpand.animate()
                    .rotation(targetRotation)
                    .setDuration(200)
                    .start()
            }
            
            binding.rvMovies.visibility = if (isExpanded) View.VISIBLE else View.GONE
            
            binding.llCategoryHeader.setOnClickListener {
                onCategoryClick(categoryItem.name)
            }
            
            // Setup movies RecyclerView with caching
            if (isExpanded) {
                var movieAdapter = movieAdapterCache[categoryItem.name]
                if (movieAdapter == null) {
                    movieAdapter = MovieAdapter(categoryItem.movies, onMovieClick, onFavoriteClick)
                    movieAdapterCache[categoryItem.name] = movieAdapter
                    
                    binding.rvMovies.apply {
                        layoutManager = LinearLayoutManager(binding.root.context)
                        adapter = movieAdapter
                        // Optimizar RecyclerView
                        setHasFixedSize(true)
                        setItemViewCacheSize(20)
                    }
                } else {
                    // Actualizar datos existentes si han cambiado
                    movieAdapter.updateMovies(categoryItem.movies)
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
        val categoryItem = getItem(position)
        val isExpanded = expandedCategories.contains(categoryItem.name)
        
        holder.bind(categoryItem, isExpanded, onMovieClick, onFavoriteClick, movieAdapterCache) { category ->
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
                    holder.binding.tvMovieCount.text = categoryItem.movieCount.toString()
                }
                if ("movies" in changes && isExpanded) {
                    movieAdapterCache[categoryItem.name]?.updateMovies(categoryItem.movies)
                }
            }
        } else {
            onBindViewHolder(holder, position)
        }
    }
    
    fun updateCategories(newCategories: Map<String, List<Movie>>) {
        val categoryItems = newCategories.map { (name, movies) ->
            CategoryItem(name, movies)
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
        movieAdapterCache.clear()
    }
}