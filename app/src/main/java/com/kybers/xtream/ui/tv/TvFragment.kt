package com.kybers.xtream.ui.tv

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.kybers.xtream.data.CacheManager
import com.kybers.xtream.data.model.Channel
import com.kybers.xtream.databinding.FragmentTvBinding

class TvFragment : Fragment() {

    private var _binding: FragmentTvBinding? = null
    private val binding get() = _binding!!
    
    private var exoPlayer: ExoPlayer? = null
    private lateinit var cacheManager: CacheManager
    private lateinit var categoryAdapter: CategoryAdapter
    private var allChannels: List<Channel> = emptyList()
    private var filteredChannels: List<Channel> = emptyList()

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
        setupPlayer()
        setupRecyclerView()
        setupSearch()
        loadChannels()
    }
    
    private fun setupPlayer() {
        exoPlayer = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.player = exoPlayer
    }
    
    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter { channel ->
            playChannel(channel)
        }
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }
    }
    
    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterChannels(s.toString())
            }
        })
        
        binding.btnSort.setOnClickListener {
            // TODO: Implementar ordenamiento
        }
    }
    
    private fun loadChannels() {
        val cachedContent = cacheManager.getCachedContent()
        if (cachedContent != null) {
            allChannels = cachedContent.channels
            filteredChannels = allChannels
            updateCategoriesDisplay()
        }
    }
    
    private fun filterChannels(query: String) {
        filteredChannels = if (query.isEmpty()) {
            allChannels
        } else {
            allChannels.filter { channel ->
                channel.name.contains(query, ignoreCase = true)
            }
        }
        updateCategoriesDisplay()
    }
    
    private fun updateCategoriesDisplay() {
        val categories = filteredChannels.groupBy { it.category }
        categoryAdapter.updateCategories(categories)
    }
    
    private fun playChannel(channel: Channel) {
        try {
            val dataSourceFactory = DefaultHttpDataSource.Factory()
            val mediaSource = HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(channel.streamUrl))
            
            exoPlayer?.setMediaSource(mediaSource)
            exoPlayer?.prepare()
            exoPlayer?.play()
        } catch (e: Exception) {
            // TODO: Manejar error de reproducción
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        exoPlayer?.release()
        exoPlayer = null
        _binding = null
    }
}