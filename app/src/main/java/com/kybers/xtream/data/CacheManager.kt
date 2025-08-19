package com.kybers.xtream.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.kybers.xtream.data.model.XtreamContent

class CacheManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("xtream_cache", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val KEY_CONTENT = "content"
    }
    
    fun saveContent(content: XtreamContent) {
        val json = gson.toJson(content)
        sharedPreferences.edit().putString(KEY_CONTENT, json).apply()
    }
    
    fun getCachedContent(): XtreamContent? {
        val json = sharedPreferences.getString(KEY_CONTENT, null) ?: return null
        return try {
            val content = gson.fromJson(json, XtreamContent::class.java)
            if (content.isExpired()) {
                clearCache()
                null
            } else {
                content
            }
        } catch (e: Exception) {
            clearCache()
            null
        }
    }
    
    fun clearCache() {
        sharedPreferences.edit().remove(KEY_CONTENT).apply()
    }
    
    fun isCacheValid(): Boolean {
        return getCachedContent() != null
    }
}