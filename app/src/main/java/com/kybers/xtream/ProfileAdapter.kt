package com.kybers.xtream

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kybers.xtream.data.model.UserProfile
import com.kybers.xtream.databinding.ItemProfileBinding

class ProfileAdapter(
    private val profiles: List<UserProfile>,
    private val onProfileClick: (UserProfile) -> Unit
) : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    class ProfileViewHolder(private val binding: ItemProfileBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(profile: UserProfile, onProfileClick: (UserProfile) -> Unit) {
            binding.tvProfileName.text = profile.profileName
            binding.tvServerUrl.text = profile.serverUrl
            
            binding.root.setOnClickListener {
                onProfileClick(profile)
            }
            
            binding.ivDelete.setOnClickListener {
                // TODO: Implementar eliminación de perfil
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val binding = ItemProfileBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return ProfileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(profiles[position], onProfileClick)
    }

    override fun getItemCount() = profiles.size
}