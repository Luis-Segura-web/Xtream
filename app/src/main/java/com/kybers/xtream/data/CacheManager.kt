package com.kybers.xtream.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.kybers.xtream.data.model.XtreamContent

class CacheManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("xtream_cache", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val TAG = "CacheManager"
        private const val KEY_CONTENT = "content"
    }
    
    fun saveContent(content: XtreamContent) {
        Log.d(TAG, "Saving content to cache:")
        Log.d(TAG, "  Channels: ${content.channels.size}")
        Log.d(TAG, "  Movies: ${content.movies.size}")
        Log.d(TAG, "  Series: ${content.series.size}")
        
        if (content.channels.isNotEmpty()) {
            Log.d(TAG, "  First 3 channels: ${content.channels.take(3).map { "${it.name} (${it.category})" }}")
        }
        
        val json = gson.toJson(content)
        sharedPreferences.edit().putString(KEY_CONTENT, json).apply()
        Log.d(TAG, "Content saved successfully")
    }
    
    fun getCachedContent(): XtreamContent? {
        val json = sharedPreferences.getString(KEY_CONTENT, null) ?: run {
            Log.d(TAG, "No cached content found")
            return null
        }
        return try {
            val content = gson.fromJson(json, XtreamContent::class.java)
            if (content.isExpired()) {
                Log.d(TAG, "Cached content expired, clearing cache")
                clearCache()
                null
            } else {
                Log.d(TAG, "Loading cached content:")
                Log.d(TAG, "  Channels: ${content.channels.size}")
                Log.d(TAG, "  Movies: ${content.movies.size}")
                Log.d(TAG, "  Series: ${content.series.size}")
                
                if (content.channels.isNotEmpty()) {
                    Log.d(TAG, "  First 3 channels: ${content.channels.take(3).map { "${it.name} (${it.category})" }}")
                }
                content
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing cached content", e)
            clearCache()
            null
        }
    }
    
    fun clearCache() {
        Log.d(TAG, "Clearing cache")
        sharedPreferences.edit().remove(KEY_CONTENT).apply()
    }
    
    fun isCacheValid(): Boolean {
        return getCachedContent() != null
    }
}