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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.datasource.DefaultHttpDataSource
import com.kybers.xtream.data.CacheManager
import kotlinx.coroutines.launch
import com.kybers.xtream.data.XtreamManager
import com.kybers.xtream.data.model.Movie
import com.kybers.xtream.databinding.FragmentMoviesBinding
import com.kybers.xtream.ui.common.SortOption
import com.kybers.xtream.ui.common.SortOptionsDialog
import com.kybers.xtream.ui.common.SortSettings

class MoviesFragment : Fragment() {

    private var _binding: FragmentMoviesBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var cacheManager: CacheManager
    private lateinit var xtreamManager: XtreamManager
    private lateinit var categoryAdapter: MovieCategoryAdapter
    private val viewModel: MoviesViewModel by viewModels()

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
        xtreamManager = XtreamManager.getInstance(requireContext())
        setupRecyclerView()
        setupSearch()
        setupObservers()
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
            // Optimizaciones de rendimiento
            setHasFixedSize(true)
            setItemViewCacheSize(10)
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
                viewModel.searchMovies(s.toString())
            }
        })
        
        binding.btnClearSearch.setOnClickListener {
            binding.etSearch.text.clear()
        }
        
        binding.btnSortContainer.setOnClickListener {
            showSortDialog()
        }
    }
    
    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updateUI(state)
            }
        }
    }
    
    private fun updateUI(state: MoviesUiState) {
        // Mostrar/ocultar indicador de carga
        binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        
        // Actualizar categorías
        if (state.categories.isNotEmpty()) {
            categoryAdapter.updateCategories(state.categories)
        }
        
        // Actualizar contador
        val text = "${state.totalMovies} películas en ${state.totalCategories} categorías"
        binding.tvMovieCount.text = text
        
        // Mostrar error si existe
        state.error?.let { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }
    
    private fun loadMovies() {
        val cachedContent = xtreamManager.getCachedContent()
        if (cachedContent != null) {
            viewModel.loadMovies(cachedContent.movies)
        } else {
            // No hay contenido en caché, mostrar mensaje para conectar
            showNoContentMessage()
        }
    }
    
    private fun showNoContentMessage() {
        Toast.makeText(requireContext(), "Configura tu conexión a Xtream Codes en la pestaña Dashboard", Toast.LENGTH_LONG).show()
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
            viewModel.uiState.value.sortSettings
        ) { newSettings ->
            viewModel.applySorting(newSettings)
            
            val message = when {
                newSettings.categoriesSort != SortOption.DEFAULT || 
                newSettings.itemsSort != SortOption.DEFAULT -> "Ordenamiento aplicado"
                else -> "Orden restablecido"
            }
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
        dialog.show()
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

    override fun onResume() {
        super.onResume()
        // Recargar contenido si se ha actualizado desde el Dashboard
        loadMovies()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        categoryAdapter.clearCache()
        _binding = null
    }
}