package com.kybers.xtream.ui.series

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kybers.xtream.data.CacheManager
import com.kybers.xtream.data.model.Episode
import com.kybers.xtream.data.model.Series
import com.kybers.xtream.databinding.FragmentSeriesBinding

class SeriesFragment : Fragment() {

    private var _binding: FragmentSeriesBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var cacheManager: CacheManager
    private lateinit var categoryAdapter: SeriesCategoryAdapter
    private var allSeries: List<Series> = emptyList()
    private var filteredSeries: List<Series> = emptyList()

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
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterSeries(s.toString())
            }
        })
        
        binding.btnSort.setOnClickListener {
            // TODO: Implementar ordenamiento
            Toast.makeText(requireContext(), "Función de ordenamiento próximamente", Toast.LENGTH_SHORT).show()
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
        filteredSeries = if (query.isEmpty()) {
            allSeries
        } else {
            allSeries.filter { series ->
                series.name.contains(query, ignoreCase = true)
            }
        }
        updateCategoriesDisplay()
    }
    
    private fun updateCategoriesDisplay() {
        val categories = filteredSeries.groupBy { it.category }
        categoryAdapter.updateCategories(categories)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}