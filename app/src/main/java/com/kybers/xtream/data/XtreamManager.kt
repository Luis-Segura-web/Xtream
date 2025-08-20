package com.kybers.xtream.data

import android.content.Context
import android.util.Log
import com.kybers.xtream.data.model.XtreamContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class XtreamManager private constructor(
    private val context: Context
) {
    private val xtreamDataManager = XtreamDataManager(context)
    private val cacheManager = CacheManager(context)
    
    companion object {
        private const val TAG = "XtreamManager"
        
        @Volatile
        private var INSTANCE: XtreamManager? = null
        
        fun getInstance(context: Context): XtreamManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: XtreamManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    suspend fun connectAndLoadContent(
        serverUrl: String,
        username: String,
        password: String,
        forceRefresh: Boolean = false
    ): Result<XtreamContent> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "Connecting and loading content with provided credentials")
            xtreamDataManager.loadContentWithCredentials(serverUrl, username, password, forceRefresh)
        } catch (e: Exception) {
            Log.e(TAG, "Error connecting and loading content", e)
            Result.failure(e)
        }
    }
    
    fun getCachedContent(): XtreamContent? {
        return xtreamDataManager.getCachedContent()
    }
    
    fun clearCache() {
        xtreamDataManager.clearCache()
    }
    
    suspend fun loadSeriesEpisodes(seriesId: String): List<com.kybers.xtream.data.model.Episode> {
        // This would need to be implemented in XtreamDataManager if needed
        return emptyList()
    }
    
    fun isCacheValid(): Boolean {
        return xtreamDataManager.isCacheValid()
    }
    
    suspend fun loadContentWithDefaults(forceRefresh: Boolean = false): Result<XtreamContent> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "Loading content with default credentials")
            xtreamDataManager.loadContent(forceRefresh)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading content with defaults", e)
            Result.failure(e)
        }
    }
}