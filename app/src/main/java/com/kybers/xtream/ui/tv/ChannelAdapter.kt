package com.kybers.xtream.ui.tv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kybers.xtream.data.model.Channel
import com.kybers.xtream.databinding.ItemChannelBinding

class ChannelAdapter(
    private val channels: List<Channel>,
    private val onChannelClick: (Channel) -> Unit
) : RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder>() {

    class ChannelViewHolder(private val binding: ItemChannelBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(channel: Channel, onChannelClick: (Channel) -> Unit) {
            binding.tvChannelName.text = channel.name
            
            // Load channel logo
            if (!channel.logo.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(channel.logo)
                    .into(binding.ivChannelLogo)
            }
            
            binding.root.setOnClickListener {
                onChannelClick(channel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val binding = ItemChannelBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return ChannelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.bind(channels[position], onChannelClick)
    }

    override fun getItemCount() = channels.size
}