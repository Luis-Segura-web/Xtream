package com.kybers.xtream.ui.movies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kybers.xtream.R
import com.kybers.xtream.data.model.Movie
import com.kybers.xtream.databinding.ItemMovieBinding

class MovieAdapter(
    private var movies: List<Movie>,
    private val onMovieClick: (Movie) -> Unit,
    private val onFavoriteClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(private val binding: ItemMovieBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(
            movie: Movie, 
            onMovieClick: (Movie) -> Unit,
            onFavoriteClick: (Movie) -> Unit
        ) {
            binding.tvMovieName.text = movie.name
            binding.tvMovieYear.text = movie.year ?: "Año desconocido"
            binding.tvMoviePlot.text = movie.plot ?: "Sin descripción disponible"
            
            // Load movie poster
            if (!movie.cover.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(movie.cover)
                    .placeholder(R.drawable.ic_movie_black_24dp)
                    .error(R.drawable.ic_movie_black_24dp)
                    .into(binding.ivMoviePoster)
            }
            
            // Click listeners
            binding.root.setOnClickListener {
                onMovieClick(movie)
            }
            
            binding.btnPlay.setOnClickListener {
                onMovieClick(movie)
            }
            
            binding.btnFavorite.setOnClickListener {
                onFavoriteClick(movie)
            }
            
            // TODO: Set favorite state from preferences
            // binding.btnFavorite.setImageResource(
            //     if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_black_24dp
            // )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position], onMovieClick, onFavoriteClick)
    }

    override fun getItemCount() = movies.size
    
    fun updateMovies(newMovies: List<Movie>) {
        if (movies != newMovies) {
            movies = newMovies
            notifyDataSetChanged()
        }
    }
}