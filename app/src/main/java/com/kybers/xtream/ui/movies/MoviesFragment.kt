package com.kybers.xtream.ui.movies

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.kybers.xtream.data.CacheManager
import com.kybers.xtream.data.model.Movie
import com.kybers.xtream.databinding.FragmentMoviesBinding

class MoviesFragment : Fragment() {

    private var _binding: FragmentMoviesBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var cacheManager: CacheManager
    private lateinit var categoryAdapter: MovieCategoryAdapter
    private var allMovies: List<Movie> = emptyList()
    private var filteredMovies: List<Movie> = emptyList()

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
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterMovies(s.toString())
            }
        })
        
        binding.btnSort.setOnClickListener {
            // TODO: Implementar ordenamiento
            Toast.makeText(requireContext(), "Función de ordenamiento próximamente", Toast.LENGTH_SHORT).show()
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
        filteredMovies = if (query.isEmpty()) {
            allMovies
        } else {
            allMovies.filter { movie ->
                movie.name.contains(query, ignoreCase = true)
            }
        }
        updateCategoriesDisplay()
    }
    
    private fun updateCategoriesDisplay() {
        val categories = filteredMovies.groupBy { it.category }
        categoryAdapter.updateCategories(categories)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}