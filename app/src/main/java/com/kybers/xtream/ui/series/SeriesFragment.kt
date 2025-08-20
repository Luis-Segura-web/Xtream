package com.kybers.xtream.ui.series

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import com.kybers.xtream.data.CacheManager
import kotlinx.coroutines.launch
import com.kybers.xtream.data.XtreamManager
import com.kybers.xtream.data.model.Episode
import com.kybers.xtream.data.model.Series
import com.kybers.xtream.databinding.FragmentSeriesBinding
import com.kybers.xtream.ui.common.SortOption
import com.kybers.xtream.ui.common.SortOptionsDialog
import com.kybers.xtream.ui.common.SortSettings

class SeriesFragment : Fragment() {

    private var _binding: FragmentSeriesBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var cacheManager: CacheManager
    private lateinit var xtreamManager: XtreamManager
    private lateinit var categoryAdapter: SeriesCategoryAdapter
    private val viewModel: SeriesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        cacheManager = CacheManager(requireContext())
        xtreamManager = XtreamManager.getInstance(requireContext())
        setupRecyclerView()
        setupSearch()
        setupObservers()
        loadSeries()
        startAnimations()
    }
    
    private fun setupRecyclerView() {
        categoryAdapter = SeriesCategoryAdapter(
            onSeriesClick = { series -> showSeriesDetails(series) },
            onFavoriteClick = { series -> toggleFavorite(series) }
        )
        binding.rvSeriesCategories.apply {
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
                viewModel.searchSeries(s.toString())
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
    
    private fun updateUI(state: SeriesUiState) {
        // Actualizar categorías
        if (state.categories.isNotEmpty()) {
            categoryAdapter.updateCategories(state.categories)
        }
        
        // Actualizar contador
        val text = "${state.totalSeries} series en ${state.totalCategories} categorías"
        binding.tvSeriesCount.text = text
        
        // Mostrar error si existe
        state.error?.let { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }
    
    private fun loadSeries() {
        val cachedContent = xtreamManager.getCachedContent()
        if (cachedContent != null) {
            viewModel.loadSeries(cachedContent.series)
        } else {
            // No hay contenido en caché, mostrar mensaje para conectar
            showNoContentMessage()
        }
    }
    
    private fun showNoContentMessage() {
        Toast.makeText(requireContext(), "Configura tu conexión a Xtream Codes en la pestaña Dashboard", Toast.LENGTH_LONG).show()
    }
    
    
    private fun showSeriesDetails(series: Series) {
        val dialog = SeriesDetailsDialog(
            requireContext(),
            series,
            onEpisodePlay = { episode -> playEpisode(episode) },
            onFavoriteClick = { toggleFavorite(it) }
        )
        dialog.show()
    }
    
    private fun playEpisode(episode: Episode) {
        try {
            // TODO: Implementar reproductor de episodios
            // Para ahora solo mostramos un Toast
            Toast.makeText(
                requireContext(), 
                "Reproduciendo: ${episode.title}", 
                Toast.LENGTH_SHORT
            ).show()
            
            // TODO: Crear VideoPlayerActivity y iniciarla
            // val intent = Intent(requireContext(), VideoPlayerActivity::class.java)
            // intent.putExtra("episode_url", episode.streamUrl)
            // intent.putExtra("episode_title", episode.title)
            // startActivity(intent)
            
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(), 
                "Error al reproducir episodio: ${e.message}", 
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    private fun toggleFavorite(series: Series) {
        // TODO: Implementar sistema de favoritos
        Toast.makeText(
            requireContext(), 
            "Favorito agregado: ${series.name}", 
            Toast.LENGTH_SHORT
        ).show()
    }
    
    private fun showSortDialog() {
        val dialog = SortOptionsDialog(
            requireContext(),
            "Series",
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
        
        // Series list animation
        val listAlpha = ObjectAnimator.ofFloat(binding.rvSeriesCategories, "alpha", 0f, 1f)
        val listTranslateY = ObjectAnimator.ofFloat(binding.rvSeriesCategories, "translationY", 100f, 0f)
        
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
        loadSeries()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        categoryAdapter.clearCache()
        _binding = null
    }
}