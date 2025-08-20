package com.kybers.xtream.ui.series

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kybers.xtream.R
import com.kybers.xtream.data.model.Series
import com.kybers.xtream.databinding.ItemSeriesBinding

class SeriesAdapter(
    private var series: List<Series>,
    private val onSeriesClick: (Series) -> Unit,
    private val onFavoriteClick: (Series) -> Unit
) : RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder>() {

    class SeriesViewHolder(private val binding: ItemSeriesBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(
            series: Series, 
            onSeriesClick: (Series) -> Unit,
            onFavoriteClick: (Series) -> Unit
        ) {
            binding.tvSeriesName.text = series.name
            binding.tvSeriesYear.text = series.year ?: "Año desconocido"
            binding.tvSeriesPlot.text = series.plot ?: "Sin descripción disponible"
            binding.tvEpisodeCount.text = "${series.episodes.size} episodios"
            
            // Load series poster
            if (!series.cover.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(series.cover)
                    .placeholder(R.drawable.ic_series_black_24dp)
                    .error(R.drawable.ic_series_black_24dp)
                    .into(binding.ivSeriesPoster)
            }
            
            // Click listeners
            binding.root.setOnClickListener {
                onSeriesClick(series)
            }
            
            binding.btnEpisodes.setOnClickListener {
                onSeriesClick(series)
            }
            
            binding.btnFavorite.setOnClickListener {
                onFavoriteClick(series)
            }
            
            // TODO: Set favorite state from preferences
            // binding.btnFavorite.setImageResource(
            //     if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_black_24dp
            // )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesViewHolder {
        val binding = ItemSeriesBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return SeriesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SeriesViewHolder, position: Int) {
        holder.bind(series[position], onSeriesClick, onFavoriteClick)
    }

    override fun getItemCount() = series.size
    
    fun updateSeries(newSeries: List<Series>) {
        if (series != newSeries) {
            series = newSeries
            notifyDataSetChanged()
        }
    }
}