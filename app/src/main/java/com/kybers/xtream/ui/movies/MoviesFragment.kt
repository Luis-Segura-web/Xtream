package com.kybers.xtream.ui.movies

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.datasource.DefaultHttpDataSource
import com.kybers.xtream.data.CacheManager
import com.kybers.xtream.data.model.Movie
import com.kybers.xtream.databinding.FragmentMoviesBinding
import com.kybers.xtream.ui.common.SortOption
import com.kybers.xtream.ui.common.SortOptionsDialog
import com.kybers.xtream.ui.common.SortSettings

class MoviesFragment : Fragment() {

    private var _binding: FragmentMoviesBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var cacheManager: CacheManager
    private lateinit var categoryAdapter: MovieCategoryAdapter
    private var allMovies: List<Movie> = emptyList()
    private var filteredMovies: List<Movie> = emptyList()
    private var currentSortSettings = SortSettings()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        cacheManager = CacheManager(requireContext())
        setupRecyclerView()
        setupSearch()
        loadMovies()
        startAnimations()
    }
    
    private fun setupRecyclerView() {
        categoryAdapter = MovieCategoryAdapter(
            onMovieClick = { movie -> showMovieDetails(movie) },
            onFavoriteClick = { movie -> toggleFavorite(movie) }
        )
        binding.rvMovieCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }
    }
    
    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Show/hide clear button
                binding.btnClearSearch.visibility = if (s?.isNotEmpty() == true) View.VISIBLE else View.GONE
            }
            override fun afterTextChanged(s: Editable?) {
                filterMovies(s.toString())
            }
        })
        
        binding.btnClearSearch.setOnClickListener {
            binding.etSearch.text.clear()
        }
        
        binding.btnSortContainer.setOnClickListener {
            showSortDialog()
        }
    }
    
    private fun loadMovies() {
        val cachedContent = cacheManager.getCachedContent()
        if (cachedContent != null) {
            allMovies = cachedContent.movies
            filteredMovies = allMovies
            updateCategoriesDisplay()
        }
    }
    
    private fun filterMovies(query: String) {
        val baseMovies = if (query.isEmpty()) {
            allMovies
        } else {
            allMovies.filter { movie ->
                movie.name.contains(query, ignoreCase = true)
            }
        }
        
        filteredMovies = applySortToMovies(baseMovies, currentSortSettings)
        updateCategoriesDisplay()
    }
    
    private fun updateCategoriesDisplay() {
        val categories = filteredMovies.groupBy { it.category }
        categoryAdapter.updateCategories(categories)
        updateMovieCount()
    }
    
    private fun updateMovieCount() {
        val count = filteredMovies.size
        val categoriesCount = filteredMovies.groupBy { it.category }.size
        val text = "$count películas en $categoriesCount categorías"
        binding.tvMovieCount.text = text
    }
    
    private fun showMovieDetails(movie: Movie) {
        val dialog = MovieDetailsDialog(
            requireContext(),
            movie,
            onPlayClick = { playMovie(it) },
            onFavoriteClick = { toggleFavorite(it) }
        )
        dialog.show()
    }
    
    private fun playMovie(movie: Movie) {
        try {
            // TODO: Implementar reproductor de películas
            // Para ahora solo mostramos un Toast
            Toast.makeText(
                requireContext(), 
                "Reproduciendo: ${movie.name}", 
                Toast.LENGTH_SHORT
            ).show()
            
            // TODO: Crear VideoPlayerActivity y iniciarla
            // val intent = Intent(requireContext(), VideoPlayerActivity::class.java)
            // intent.putExtra("movie_url", movie.streamUrl)
            // intent.putExtra("movie_title", movie.name)
            // startActivity(intent)
            
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(), 
                "Error al reproducir película: ${e.message}", 
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    private fun toggleFavorite(movie: Movie) {
        // TODO: Implementar sistema de favoritos
        Toast.makeText(
            requireContext(), 
            "Favorito agregado: ${movie.name}", 
            Toast.LENGTH_SHORT
        ).show()
    }
    
    private fun showSortDialog() {
        val dialog = SortOptionsDialog(
            requireContext(),
            "Películas",
            currentSortSettings
        ) { newSettings ->
            currentSortSettings = newSettings
            applySorting()
        }
        dialog.show()
    }
    
    private fun applySorting() {
        filteredMovies = applySortToMovies(filteredMovies, currentSortSettings)
        updateCategoriesDisplay()
        
        val message = when {
            currentSortSettings.categoriesSort != SortOption.DEFAULT || 
            currentSortSettings.itemsSort != SortOption.DEFAULT -> "Ordenamiento aplicado"
            else -> "Orden restablecido"
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    
    private fun applySortToMovies(
        movies: List<Movie>,
        settings: SortSettings
    ): List<Movie> {
        val groupedMovies = movies.groupBy { it.category }
        
        // Sort categories
        val sortedCategories = when (settings.categoriesSort) {
            SortOption.A_Z -> groupedMovies.toSortedMap()
            SortOption.Z_A -> groupedMovies.toSortedMap(reverseOrder())
            SortOption.DEFAULT -> groupedMovies
        }
        
        // Sort movies within each category and flatten
        return sortedCategories.flatMap { (_, categoryMovies) ->
            when (settings.itemsSort) {
                SortOption.A_Z -> categoryMovies.sortedBy { it.name.lowercase() }
                SortOption.Z_A -> categoryMovies.sortedByDescending { it.name.lowercase() }
                SortOption.DEFAULT -> categoryMovies
            }
        }
    }
    
    private fun startAnimations() {
        // Header animation
        val headerAlpha = ObjectAnimator.ofFloat(binding.cardHeader, "alpha", 0f, 1f)
        val headerTranslateY = ObjectAnimator.ofFloat(binding.cardHeader, "translationY", -50f, 0f)
        
        val headerAnimatorSet = AnimatorSet()
        headerAnimatorSet.playTogether(headerAlpha, headerTranslateY)
        headerAnimatorSet.duration = 600
        headerAnimatorSet.interpolator = DecelerateInterpolator()
        
        // Search card animation
        val searchAlpha = ObjectAnimator.ofFloat(binding.cardSearch, "alpha", 0f, 1f)
        val searchScaleX = ObjectAnimator.ofFloat(binding.cardSearch, "scaleX", 0.9f, 1f)
        val searchScaleY = ObjectAnimator.ofFloat(binding.cardSearch, "scaleY", 0.9f, 1f)
        
        val searchAnimatorSet = AnimatorSet()
        searchAnimatorSet.playTogether(searchAlpha, searchScaleX, searchScaleY)
        searchAnimatorSet.duration = 500
        searchAnimatorSet.startDelay = 200
        searchAnimatorSet.interpolator = DecelerateInterpolator()
        
        // Movies list animation
        val listAlpha = ObjectAnimator.ofFloat(binding.rvMovieCategories, "alpha", 0f, 1f)
        val listTranslateY = ObjectAnimator.ofFloat(binding.rvMovieCategories, "translationY", 100f, 0f)
        
        val listAnimatorSet = AnimatorSet()
        listAnimatorSet.playTogether(listAlpha, listTranslateY)
        listAnimatorSet.duration = 600
        listAnimatorSet.startDelay = 400
        listAnimatorSet.interpolator = DecelerateInterpolator()
        
        // Start animations
        headerAnimatorSet.start()
        searchAnimatorSet.start()
        listAnimatorSet.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}