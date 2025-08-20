package com.kybers.xtream.ui.series

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.kybers.xtream.R
import com.kybers.xtream.data.model.Episode
import com.kybers.xtream.data.model.Series
import com.kybers.xtream.databinding.DialogSeriesDetailsBinding

class SeriesDetailsDialog(
    context: Context,
    private val series: Series,
    private val onEpisodePlay: (Episode) -> Unit,
    private val onFavoriteClick: (Series) -> Unit
) : Dialog(context) {
    
    private lateinit var binding: DialogSeriesDetailsBinding
    private var exoPlayer: ExoPlayer? = null
    private var currentEpisode: Episode? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        
        binding = DialogSeriesDetailsBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        
        // Set dialog properties
        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            (context.resources.displayMetrics.heightPixels * 0.8).toInt()
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        setupViews()
        setupPlayer()
    }
    
    private fun setupPlayer() {
        exoPlayer = ExoPlayer.Builder(context).build()
        binding.playerViewSeries.player = exoPlayer
        
        // Play first episode if available
        if (series.episodes.isNotEmpty()) {
            playEpisode(series.episodes.first())
        }
    }
    
    private fun playEpisode(episode: Episode) {
        currentEpisode = episode
        try {
            val dataSourceFactory = DefaultHttpDataSource.Factory()
            val mediaSource = HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(episode.streamUrl))
            
            exoPlayer?.setMediaSource(mediaSource)
            exoPlayer?.prepare()
        } catch (e: Exception) {
            // Handle error loading episode
        }
    }
    
    private fun setupViews() {
        binding.apply {
            // Series info
            tvSeriesTitleDetail.text = series.name
            tvSeriesYearDetail.text = series.year ?: "Año desconocido"
            tvSeriesCategoryDetail.text = series.category
            tvSeriesPlotDetail.text = series.plot ?: "Sin descripción disponible"
            tvEpisodeCountDetail.text = "${series.episodes.size} episodios disponibles"
            tvSeriesTmdbId.text = series.tmdbId ?: "No disponible"
            tvSeriesCategoryInfo.text = series.category
            
            // Load series poster
            if (!series.cover.isNullOrEmpty()) {
                Glide.with(context)
                    .load(series.cover)
                    .placeholder(R.drawable.ic_series_black_24dp)
                    .error(R.drawable.ic_series_black_24dp)
                    .into(ivSeriesPosterDetail)
            }
            
            // Setup episodes RecyclerView
            val episodeAdapter = EpisodeAdapter(series.episodes) { episode ->
                playEpisode(episode)
            }
            rvEpisodes.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = episodeAdapter
            }
            
            // Click listeners
            btnClose.setOnClickListener {
                exoPlayer?.release()
                dismiss()
            }
            
            btnViewEpisodes.setOnClickListener {
                if (series.episodes.isNotEmpty()) {
                    onEpisodePlay(series.episodes.first())
                    dismiss()
                } else {
                    Toast.makeText(context, "No hay episodios disponibles", Toast.LENGTH_SHORT).show()
                }
            }
            
            btnFavoriteDetail.setOnClickListener {
                onFavoriteClick(series)
                // TODO: Update favorite icon state
            }
            
            // TODO: Set favorite state from preferences
            // btnFavoriteDetail.setImageResource(
            //     if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_black_24dp
            // )
        }
    }
    
    override fun dismiss() {
        exoPlayer?.release()
        exoPlayer = null
        super.dismiss()
    }
}