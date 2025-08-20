package com.kybers.xtream.ui.tv

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
import com.kybers.xtream.data.FavoritesManager
import com.kybers.xtream.data.model.Channel
import com.kybers.xtream.databinding.FragmentTvBinding
import com.kybers.xtream.ui.common.SortOption
import com.kybers.xtream.ui.common.SortOptionsDialog
import com.kybers.xtream.ui.common.SortSettings

class TvFragment : Fragment() {

    private var _binding: FragmentTvBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var cacheManager: CacheManager
    private lateinit var favoritesManager: FavoritesManager
    private lateinit var categoryAdapter: CategoryAdapter
    private var allChannels: List<Channel> = emptyList()
    private var allCategories: Map<String, List<Channel>> = emptyMap()
    private var filteredCategories: Map<String, List<Channel>> = emptyMap()
    private var currentSortSettings = SortSettings()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTvBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        cacheManager = CacheManager(requireContext())
        favoritesManager = FavoritesManager(requireContext())
        setupRecyclerView()
        setupSearch()
        loadChannels()
        startAnimations()
    }
    
    
    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter(
            onChannelClick = { channel ->
                Toast.makeText(requireContext(), "Reproduciendo: ${channel.name}", Toast.LENGTH_SHORT).show()
                // TODO: Implementar reproductor en pantalla completa
            },
            onFavoriteClick = { channel ->
                val isFavorite = favoritesManager.toggleChannelFavorite(channel.id)
                val message = if (isFavorite) {
                    "${channel.name} agregado a favoritos"
                } else {
                    "${channel.name} removido de favoritos"
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                updateFavorites()
            }
        )
        
        binding.rvChannels.apply {
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
                filterChannels(s.toString())
            }
        })
        
        binding.btnClearSearch.setOnClickListener {
            binding.etSearch.text.clear()
        }
        
        binding.btnSortContainer.setOnClickListener {
            showSortDialog()
        }
    }
    
    private fun loadChannels() {
        val cachedContent = cacheManager.getCachedContent()
        if (cachedContent != null) {
            allChannels = cachedContent.channels
            allCategories = allChannels.groupBy { it.category }
            filteredCategories = allCategories
            updateCategoriesDisplay()
            updateChannelCount()
        }
    }
    
    private fun filterChannels(query: String) {
        val baseCategories = if (query.isEmpty()) {
            allCategories
        } else {
            allCategories.mapValues { (_, channels) ->
                channels.filter { channel ->
                    channel.name.contains(query, ignoreCase = true) ||
                    channel.category.contains(query, ignoreCase = true)
                }
            }.filter { (_, channels) -> channels.isNotEmpty() }
        }
        
        filteredCategories = applySortToCategories(baseCategories, currentSortSettings)
        updateCategoriesDisplay()
        updateChannelCount()
    }
    
    private fun updateCategoriesDisplay() {
        categoryAdapter.updateCategories(filteredCategories)
    }
    
    private fun updateFavorites() {
        // TODO: Implementar favoritos en vista de categorías
    }
    
    private fun updateChannelCount() {
        val count = filteredCategories.values.sumOf { it.size }
        val categoriesCount = filteredCategories.size
        val text = "$count canales en $categoriesCount categorías"
        binding.tvChannelCount.text = text
    }
    
    private fun showSortDialog() {
        val dialog = SortOptionsDialog(
            requireContext(),
            "Canales",
            currentSortSettings
        ) { newSettings ->
            currentSortSettings = newSettings
            applySorting()
        }
        dialog.show()
    }
    
    private fun applySorting() {
        filteredCategories = applySortToCategories(filteredCategories, currentSortSettings)
        updateCategoriesDisplay()
        
        val message = when {
            currentSortSettings.categoriesSort != SortOption.DEFAULT || 
            currentSortSettings.itemsSort != SortOption.DEFAULT -> "Ordenamiento aplicado"
            else -> "Orden restablecido"
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    
    private fun applySortToCategories(
        categories: Map<String, List<Channel>>,
        settings: SortSettings
    ): Map<String, List<Channel>> {
        // Sort categories
        val sortedCategories = when (settings.categoriesSort) {
            SortOption.A_Z -> categories.toSortedMap()
            SortOption.Z_A -> categories.toSortedMap(reverseOrder())
            SortOption.DEFAULT -> categories
        }
        
        // Sort channels within each category
        return sortedCategories.mapValues { (_, channels) ->
            when (settings.itemsSort) {
                SortOption.A_Z -> channels.sortedBy { it.name.lowercase() }
                SortOption.Z_A -> channels.sortedByDescending { it.name.lowercase() }
                SortOption.DEFAULT -> channels
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
        
        // Channels list animation
        val listAlpha = ObjectAnimator.ofFloat(binding.rvChannels, "alpha", 0f, 1f)
        val listTranslateY = ObjectAnimator.ofFloat(binding.rvChannels, "translationY", 100f, 0f)
        
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
        updateFavorites()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}