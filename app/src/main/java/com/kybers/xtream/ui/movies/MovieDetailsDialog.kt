package com.kybers.xtream.ui.movies

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import com.bumptech.glide.Glide
import com.kybers.xtream.R
import com.kybers.xtream.data.model.Movie
import com.kybers.xtream.databinding.DialogMovieDetailsBinding

class MovieDetailsDialog(
    context: Context,
    private val movie: Movie,
    private val onPlayClick: (Movie) -> Unit,
    private val onFavoriteClick: (Movie) -> Unit
) : Dialog(context) {
    
    private lateinit var binding: DialogMovieDetailsBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        
        binding = DialogMovieDetailsBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        
        // Set dialog properties
        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        setupViews()
    }
    
    private fun setupViews() {
        binding.apply {
            // Movie info
            tvMovieTitleDetail.text = movie.name
            tvMovieYearDetail.text = movie.year ?: "Año desconocido"
            tvMovieCategoryDetail.text = movie.category
            tvMoviePlotDetail.text = movie.plot ?: "Sin descripción disponible"
            tvMovieTmdbId.text = movie.tmdbId ?: "No disponible"
            tvMovieCategoryInfo.text = movie.category
            
            // Load movie poster
            if (!movie.cover.isNullOrEmpty()) {
                Glide.with(context)
                    .load(movie.cover)
                    .placeholder(R.drawable.ic_movie_black_24dp)
                    .error(R.drawable.ic_movie_black_24dp)
                    .into(ivMoviePosterDetail)
            }
            
            // Click listeners
            btnClose.setOnClickListener {
                dismiss()
            }
            
            btnPlayMovie.setOnClickListener {
                onPlayClick(movie)
                dismiss()
            }
            
            btnFavoriteDetail.setOnClickListener {
                onFavoriteClick(movie)
                // TODO: Update favorite icon state
            }
            
            // TODO: Set favorite state from preferences
            // btnFavoriteDetail.setImageResource(
            //     if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_black_24dp
            // )
        }
    }
}