package com.kybers.xtream.ui.tv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kybers.xtream.R
import com.kybers.xtream.data.model.Channel
import com.kybers.xtream.databinding.ItemChannelBinding
import java.text.SimpleDateFormat
import java.util.*

class ChannelAdapter(
    private val onChannelClick: (Channel) -> Unit,
    private val onFavoriteClick: (Channel) -> Unit
) : RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder>() {

    private var channels: List<Channel> = emptyList()
    private var favoriteChannels: Set<String> = emptySet()

    inner class ChannelViewHolder(private val binding: ItemChannelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(channel: Channel) {
            binding.apply {
                // Channel name
                tvChannelName.text = channel.name

                // Channel logo - usar Glide cuando tengamos URLs reales
                Glide.with(binding.root.context)
                    .load(channel.logo ?: R.drawable.ic_tv_24)
                    .placeholder(R.drawable.ic_tv_24)
                    .error(R.drawable.ic_tv_24)
                    .into(ivChannelLogo)

                // Category badge
                when {
                    channel.name.contains("4K", ignoreCase = true) -> {
                        tvChannelCategory.text = "4K"
                        tvChannelCategory.setBackgroundResource(R.drawable.category_badge_4k_background)
                    }
                    channel.name.contains("HD", ignoreCase = true) -> {
                        tvChannelCategory.text = "HD"
                        tvChannelCategory.setBackgroundResource(R.drawable.category_badge_background)
                    }
                    channel.category.isNotEmpty() -> {
                        tvChannelCategory.text = channel.category.take(3).uppercase()
                        tvChannelCategory.setBackgroundResource(R.drawable.category_badge_background)
                    }
                    else -> {
                        tvChannelCategory.text = "TV"
                        tvChannelCategory.setBackgroundResource(R.drawable.category_badge_background)
                    }
                }

                // EPG Data - Mock data por ahora
                setupEPGData(channel)

                // Favorite button
                val isFavorite = favoriteChannels.contains(channel.id)
                if (isFavorite) {
                    btnFavorite.setImageResource(R.drawable.ic_favorite_filled)
                    btnFavorite.setColorFilter(binding.root.context.getColor(R.color.favorite_active))
                } else {
                    btnFavorite.setImageResource(R.drawable.ic_favorite_border)
                    btnFavorite.setColorFilter(binding.root.context.getColor(R.color.favorite_inactive))
                }

                // Click listeners
                root.setOnClickListener { onChannelClick(channel) }
                btnFavorite.setOnClickListener { onFavoriteClick(channel) }
            }
        }

        private fun setupEPGData(channel: Channel) {
            val currentTime = Calendar.getInstance()
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            // Mock EPG data - en una implementación real esto vendría de la API
            val currentPrograms = mapOf(
                "General" to listOf("Noticias del Mediodía", "Deportes", "Entretenimiento", "Película de la Tarde"),
                "Deportes" to listOf("Fútbol Internacional", "Análisis Deportivo", "Noticias Deportivas", "Fútbol Local"),
                "Noticias" to listOf("Informativo Central", "Política Hoy", "Economía", "Internacional"),
                "Entretenimiento" to listOf("Novela", "Show de Variedades", "Concurso", "Película"),
                "Infantil" to listOf("Dibujos Animados", "Programas Educativos", "Aventuras", "Música Infantil")
            )

            val category = channel.category.ifEmpty { "General" }
            val programs = currentPrograms[category] ?: currentPrograms["General"]!!
            
            val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
            val programIndex = (currentHour / 6) % programs.size
            val nextProgramIndex = (programIndex + 1) % programs.size

            // Current program
            val currentStartTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, (programIndex * 6))
                set(Calendar.MINUTE, 0)
            }
            val currentEndTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, (programIndex * 6) + 6)
                set(Calendar.MINUTE, 0)
            }

            binding.tvCurrentProgram.text = programs[programIndex]
            binding.tvCurrentTime.text = "${timeFormat.format(currentStartTime.time)}-${timeFormat.format(currentEndTime.time)}"

            // Next program
            val nextStartTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, (nextProgramIndex * 6))
                set(Calendar.MINUTE, 0)
            }

            binding.tvNextProgram.text = "Siguiente: ${programs[nextProgramIndex]}"
            binding.tvNextTime.text = timeFormat.format(nextStartTime.time)
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
        holder.bind(channels[position])
    }

    override fun getItemCount(): Int = channels.size

    fun updateChannels(newChannels: List<Channel>) {
        channels = newChannels
        notifyDataSetChanged()
    }

    fun updateFavorites(favorites: Set<String>) {
        favoriteChannels = favorites
        notifyDataSetChanged()
    }

    fun getChannelAt(position: Int): Channel = channels[position]
}