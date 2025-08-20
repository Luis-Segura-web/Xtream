package com.kybers.xtream.data

import android.content.Context
import android.util.Log
import com.kybers.xtream.data.model.XtreamContent
import com.kybers.xtream.data.repository.XtreamRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class XtreamDataManager(private val context: Context) {
    
    private val cacheManager = CacheManager(context)
    private val repository = XtreamRepository()
    
    companion object {
        private const val TAG = "XtreamDataManager"
        
        // Test credentials provided by user
        const val DEFAULT_USERNAME = "DMWyCAxket"
        const val DEFAULT_PASSWORD = "kfvRWYajJJ"
        const val DEFAULT_SERVER_URL = "http://gzytv.vip:8880"
    }
    
    suspend fun loadContent(forceRefresh: Boolean = false): Result<XtreamContent> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "Loading content, forceRefresh: $forceRefresh")
            
            // Check cache first if not forcing refresh
            if (!forceRefresh && cacheManager.isCacheValid()) {
                val cachedContent = cacheManager.getCachedContent()
                if (cachedContent != null) {
                    Log.d(TAG, "Returning cached content")
                    return@withContext Result.success(cachedContent)
                }
            }
            
            Log.d(TAG, "Cache miss or force refresh, loading from API")
            
            // Initialize repository with credentials
            repository.initialize(DEFAULT_SERVER_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD)
            
            // Load fresh content from API
            val result = repository.authenticateAndLoadContent()
            
            if (result.isSuccess) {
                val content = result.getOrThrow()
                // Save to cache
                cacheManager.saveContent(content)
                Log.d(TAG, "Content loaded and cached successfully")
                Result.success(content)
            } else {
                Log.e(TAG, "Failed to load content from API", result.exceptionOrNull())
                
                // Try to return cached content as fallback
                val cachedContent = cacheManager.getCachedContent()
                if (cachedContent != null) {
                    Log.d(TAG, "API failed, returning cached content as fallback")
                    Result.success(cachedContent)
                } else {
                    result
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in loadContent", e)
            Result.failure(e)
        }
    }
    
    suspend fun loadContentWithCredentials(
        serverUrl: String,
        username: String,
        password: String,
        forceRefresh: Boolean = true
    ): Result<XtreamContent> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "Loading content with custom credentials")
            
            // Initialize repository with provided credentials
            repository.initialize(serverUrl, username, password)
            
            // Load fresh content from API
            val result = repository.authenticateAndLoadContent()
            
            if (result.isSuccess) {
                val content = result.getOrThrow()
                // Save to cache
                cacheManager.saveContent(content)
                Log.d(TAG, "Content loaded with custom credentials and cached successfully")
                Result.success(content)
            } else {
                Log.e(TAG, "Failed to load content with custom credentials", result.exceptionOrNull())
                result
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in loadContentWithCredentials", e)
            Result.failure(e)
        }
    }
    
    fun getCachedContent(): XtreamContent? {
        return cacheManager.getCachedContent()
    }
    
    fun clearCache() {
        cacheManager.clearCache()
    }
    
    fun isCacheValid(): Boolean {
        return cacheManager.isCacheValid()
    }
    
    suspend fun refreshContent(): Result<XtreamContent> {
        return loadContent(forceRefresh = true)
    }
}