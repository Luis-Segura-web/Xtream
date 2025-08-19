package com.kybers.xtream.ui.series

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kybers.xtream.data.model.Episode
import com.kybers.xtream.databinding.ItemEpisodeBinding

class EpisodeAdapter(
    private val episodes: List<Episode>,
    private val onEpisodeClick: (Episode) -> Unit
) : RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder>() {

    class EpisodeViewHolder(private val binding: ItemEpisodeBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(episode: Episode, onEpisodeClick: (Episode) -> Unit) {
            binding.tvEpisodeTitle.text = episode.title
            binding.tvEpisodeInfo.text = "T${episode.seasonNumber} E${episode.episodeNumber}"
            
            binding.root.setOnClickListener {
                onEpisodeClick(episode)
            }
            
            binding.btnPlayEpisode.setOnClickListener {
                onEpisodeClick(episode)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val binding = ItemEpisodeBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return EpisodeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        holder.bind(episodes[position], onEpisodeClick)
    }

    override fun getItemCount() = episodes.size
}