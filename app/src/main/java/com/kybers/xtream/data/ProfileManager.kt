package com.kybers.xtream.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kybers.xtream.data.model.UserProfile

class ProfileManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("xtream_profiles", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val KEY_PROFILES = "profiles"
        private const val KEY_CURRENT_PROFILE = "current_profile"
    }
    
    fun saveProfile(profile: UserProfile) {
        val profiles = getAllProfiles().toMutableList()
        val existingIndex = profiles.indexOfFirst { it.profileName == profile.profileName }
        
        if (existingIndex >= 0) {
            profiles[existingIndex] = profile.copy(id = profiles[existingIndex].id)
        } else {
            val newId = (profiles.maxByOrNull { it.id }?.id ?: 0) + 1
            profiles.add(profile.copy(id = newId))
        }
        
        val json = gson.toJson(profiles)
        sharedPreferences.edit().putString(KEY_PROFILES, json).apply()
    }
    
    fun getAllProfiles(): List<UserProfile> {
        val json = sharedPreferences.getString(KEY_PROFILES, null) ?: return emptyList()
        val type = object : TypeToken<List<UserProfile>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
    
    fun setCurrentProfile(profile: UserProfile) {
        val json = gson.toJson(profile)
        sharedPreferences.edit().putString(KEY_CURRENT_PROFILE, json).apply()
    }
    
    fun getCurrentProfile(): UserProfile? {
        val json = sharedPreferences.getString(KEY_CURRENT_PROFILE, null) ?: return null
        return gson.fromJson(json, UserProfile::class.java)
    }
    
    fun deleteProfile(profile: UserProfile) {
        val profiles = getAllProfiles().toMutableList()
        profiles.removeAll { it.id == profile.id }
        val json = gson.toJson(profiles)
        sharedPreferences.edit().putString(KEY_PROFILES, json).apply()
    }
}