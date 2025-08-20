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
import androidx.recyclerview.widget.LinearLayoutManager
import com.kybers.xtream.data.CacheManager
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
    private lateinit var categoryAdapter: SeriesCategoryAdapter
    private var allSeries: List<Series> = emptyList()
    private var filteredSeries: List<Series> = emptyList()
    private var currentSortSettings = SortSettings()

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
        setupRecyclerView()
        setupSearch()
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
                filterSeries(s.toString())
            }
        })
        
        binding.btnClearSearch.setOnClickListener {
            binding.etSearch.text.clear()
        }
        
        binding.btnSortContainer.setOnClickListener {
            showSortDialog()
        }
    }
    
    private fun loadSeries() {
        val cachedContent = cacheManager.getCachedContent()
        if (cachedContent != null) {
            allSeries = cachedContent.series
            filteredSeries = allSeries
            updateCategoriesDisplay()
        }
    }
    
    private fun filterSeries(query: String) {
        val baseSeries = if (query.isEmpty()) {
            allSeries
        } else {
            allSeries.filter { series ->
                series.name.contains(query, ignoreCase = true)
            }
        }
        
        filteredSeries = applySortToSeries(baseSeries, currentSortSettings)
        updateCategoriesDisplay()
    }
    
    private fun updateCategoriesDisplay() {
        val categories = filteredSeries.groupBy { it.category }
        categoryAdapter.updateCategories(categories)
        updateSeriesCount()
    }
    
    private fun updateSeriesCount() {
        val count = filteredSeries.size
        val categoriesCount = filteredSeries.groupBy { it.category }.size
        val text = "$count series en $categoriesCount categorías"
        binding.tvSeriesCount.text = text
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
            currentSortSettings
        ) { newSettings ->
            currentSortSettings = newSettings
            applySorting()
        }
        dialog.show()
    }
    
    private fun applySorting() {
        filteredSeries = applySortToSeries(filteredSeries, currentSortSettings)
        updateCategoriesDisplay()
        
        val message = when {
            currentSortSettings.categoriesSort != SortOption.DEFAULT || 
            currentSortSettings.itemsSort != SortOption.DEFAULT -> "Ordenamiento aplicado"
            else -> "Orden restablecido"
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    
    private fun applySortToSeries(
        series: List<Series>,
        settings: SortSettings
    ): List<Series> {
        val groupedSeries = series.groupBy { it.category }
        
        // Sort categories
        val sortedCategories = when (settings.categoriesSort) {
            SortOption.A_Z -> groupedSeries.toSortedMap()
            SortOption.Z_A -> groupedSeries.toSortedMap(reverseOrder())
            SortOption.DEFAULT -> groupedSeries
        }
        
        // Sort series within each category and flatten
        return sortedCategories.flatMap { (_, categorySeries) ->
            when (settings.itemsSort) {
                SortOption.A_Z -> categorySeries.sortedBy { it.name.lowercase() }
                SortOption.Z_A -> categorySeries.sortedByDescending { it.name.lowercase() }
                SortOption.DEFAULT -> categorySeries
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}