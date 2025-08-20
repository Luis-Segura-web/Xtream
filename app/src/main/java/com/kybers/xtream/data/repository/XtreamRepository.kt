package com.kybers.xtream.data.repository

import android.util.Log
import com.kybers.xtream.data.api.NetworkClient
import com.kybers.xtream.data.api.XtreamApiService
import com.kybers.xtream.data.api.model.*
import com.kybers.xtream.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class XtreamRepository {
    
    private var apiService: XtreamApiService? = null
    private var currentCredentials: Triple<String, String, String>? = null // baseUrl, username, password
    
    companion object {
        private const val TAG = "XtreamRepository"
    }
    
    fun initialize(baseUrl: String, username: String, password: String) {
        val normalizedUrl = if (!baseUrl.endsWith("/")) "$baseUrl/" else baseUrl
        currentCredentials = Triple(normalizedUrl, username, password)
        apiService = NetworkClient.createApiService(normalizedUrl)
        Log.d(TAG, "Initialized with server: $normalizedUrl")
    }
    
    suspend fun authenticateAndLoadContent(): Result<XtreamContent> = withContext(Dispatchers.IO) {
        return@withContext try {
            val (baseUrl, username, password) = currentCredentials 
                ?: return@withContext Result.failure(Exception("Repository not initialized"))
            
            val service = apiService 
                ?: return@withContext Result.failure(Exception("API service not available"))
            
            Log.d(TAG, "Starting authentication for user: $username")
            
            // First authenticate the user
            val authResponse = service.authenticateUser(username, password)
            
            if (!authResponse.isSuccessful) {
                Log.e(TAG, "Authentication failed: ${authResponse.code()}")
                return@withContext Result.failure(Exception("Authentication failed: ${authResponse.message()}"))
            }
            
            val authData = authResponse.body()
            Log.d(TAG, "Auth response: ${authData?.userInfo?.status}")
            
            if (authData?.userInfo?.auth != 1) {
                Log.e(TAG, "User not authorized: ${authData?.userInfo?.message}")
                return@withContext Result.failure(Exception("User not authorized: ${authData?.userInfo?.message}"))
            }
            
            Log.d(TAG, "Authentication successful, loading content...")
            
            // Load all content in parallel
            val channels = loadChannels(username, password)
            val movies = loadMovies(username, password)
            val series = loadSeries(username, password)
            
            val content = XtreamContent(
                channels = channels,
                movies = movies,
                series = series,
                cacheTimestamp = System.currentTimeMillis()
            )
            
            Log.d(TAG, "Content loaded: ${channels.size} channels, ${movies.size} movies, ${series.size} series")
            Result.success(content)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error loading content", e)
            Result.failure(e)
        }
    }
    
    private suspend fun loadChannels(username: String, password: String): List<Channel> {
        return try {
            val service = apiService ?: return emptyList()
            
            Log.d(TAG, "Loading live categories...")
            val categoriesResponse = service.getLiveCategories(username, password)
            val categories = if (categoriesResponse.isSuccessful) {
                categoriesResponse.body() ?: emptyList()
            } else {
                Log.w(TAG, "Failed to load categories: ${categoriesResponse.code()}")
                emptyList()
            }
            
            Log.d(TAG, "Loading live streams...")
            val streamsResponse = service.getLiveStreams(username, password)
            val streams = if (streamsResponse.isSuccessful) {
                streamsResponse.body() ?: emptyList()
            } else {
                Log.w(TAG, "Failed to load streams: ${streamsResponse.code()}")
                emptyList()
            }
            
            // Create category map for faster lookup
            val categoryMap = categories.associateBy { it.categoryId }
            
            // Convert API models to app models
            val channels = streams.mapNotNull { stream ->
                val categoryName = categoryMap[stream.categoryId]?.categoryName ?: "Unknown"
                
                stream.streamId?.let { streamId ->
                    val streamUrl = buildStreamUrl(currentCredentials!!.first, username, password, streamId, "live")
                    Channel(
                        id = streamId.toString(),
                        name = stream.name ?: "Unknown Channel",
                        streamUrl = streamUrl,
                        category = categoryName,
                        logo = stream.streamIcon
                    )
                }
            }
            
            Log.d(TAG, "Converted ${channels.size} channels")
            channels
            
        } catch (e: Exception) {
            Log.e(TAG, "Error loading channels", e)
            emptyList()
        }
    }
    
    private suspend fun loadMovies(username: String, password: String): List<Movie> {
        return try {
            val service = apiService ?: return emptyList()
            
            Log.d(TAG, "Loading VOD categories...")
            val categoriesResponse = service.getVodCategories(username, password)
            val categories = if (categoriesResponse.isSuccessful) {
                categoriesResponse.body() ?: emptyList()
            } else {
                Log.w(TAG, "Failed to load VOD categories: ${categoriesResponse.code()}")
                emptyList()
            }
            
            Log.d(TAG, "Loading VOD streams...")
            val streamsResponse = service.getVodStreams(username, password)
            val streams = if (streamsResponse.isSuccessful) {
                streamsResponse.body() ?: emptyList()
            } else {
                Log.w(TAG, "Failed to load VOD streams: ${streamsResponse.code()}")
                emptyList()
            }
            
            // Create category map for faster lookup
            val categoryMap = categories.associateBy { it.categoryId }
            
            // Convert API models to app models
            val movies = streams.mapNotNull { stream ->
                val categoryName = categoryMap[stream.categoryId]?.categoryName ?: "Unknown"
                
                stream.streamId?.let { streamId ->
                    val streamUrl = buildStreamUrl(currentCredentials!!.first, username, password, streamId, "movie")
                    Movie(
                        id = streamId.toString(),
                        name = stream.name ?: "Unknown Movie",
                        streamUrl = streamUrl,
                        category = categoryName,
                        cover = stream.streamIcon,
                        tmdbId = null, // Could be populated later with detailed info
                        year = null,
                        plot = null
                    )
                }
            }
            
            Log.d(TAG, "Converted ${movies.size} movies")
            movies
            
        } catch (e: Exception) {
            Log.e(TAG, "Error loading movies", e)
            emptyList()
        }
    }
    
    private suspend fun loadSeries(username: String, password: String): List<Series> {
        return try {
            val service = apiService ?: return emptyList()
            
            Log.d(TAG, "Loading series categories...")
            val categoriesResponse = service.getSeriesCategories(username, password)
            val categories = if (categoriesResponse.isSuccessful) {
                categoriesResponse.body() ?: emptyList()
            } else {
                Log.w(TAG, "Failed to load series categories: ${categoriesResponse.code()}")
                emptyList()
            }
            
            Log.d(TAG, "Loading series streams...")
            val streamsResponse = service.getSeriesStreams(username, password)
            val streams = if (streamsResponse.isSuccessful) {
                streamsResponse.body() ?: emptyList()
            } else {
                Log.w(TAG, "Failed to load series streams: ${streamsResponse.code()}")
                emptyList()
            }
            
            // Create category map for faster lookup
            val categoryMap = categories.associateBy { it.categoryId }
            
            // Convert API models to app models (without episodes for now)
            val series = streams.mapNotNull { stream ->
                val categoryName = categoryMap[stream.categoryId]?.categoryName ?: "Unknown"
                
                stream.seriesId?.let { seriesId ->
                    Series(
                        id = seriesId.toString(),
                        name = stream.name ?: "Unknown Series",
                        category = categoryName,
                        cover = stream.cover,
                        tmdbId = null,
                        year = stream.releaseDate,
                        plot = stream.plot,
                        episodes = emptyList() // Episodes can be loaded on demand
                    )
                }
            }
            
            Log.d(TAG, "Converted ${series.size} series")
            series
            
        } catch (e: Exception) {
            Log.e(TAG, "Error loading series", e)
            emptyList()
        }
    }
    
    private fun buildStreamUrl(baseUrl: String, username: String, password: String, streamId: Int, type: String): String {
        return when (type) {
            "live" -> "${baseUrl}${username}/${password}/${streamId}"
            "movie" -> "${baseUrl}movie/${username}/${password}/${streamId}.mp4"
            "series" -> "${baseUrl}series/${username}/${password}/${streamId}.mp4"
            else -> "${baseUrl}${username}/${password}/${streamId}"
        }
    }
    
    suspend fun loadSeriesEpisodes(seriesId: String): List<Episode> = withContext(Dispatchers.IO) {
        return@withContext try {
            val (_, username, password) = currentCredentials ?: return@withContext emptyList()
            val service = apiService ?: return@withContext emptyList()
            
            Log.d(TAG, "Loading episodes for series: $seriesId")
            
            val episodesResponse = service.getSeriesEpisodes(username, password, seriesId = seriesId)
            if (!episodesResponse.isSuccessful) {
                Log.w(TAG, "Failed to load episodes: ${episodesResponse.code()}")
                return@withContext emptyList()
            }
            
            val episodesData = episodesResponse.body()
            val episodes = mutableListOf<Episode>()
            
            episodesData?.episodes?.forEach { (seasonKey, seasonEpisodes) ->
                seasonEpisodes.forEach { apiEpisode ->
                    apiEpisode.id?.let { episodeId ->
                        val streamUrl = buildStreamUrl(
                            currentCredentials!!.first,
                            username,
                            password,
                            episodeId.toIntOrNull() ?: 0,
                            "series"
                        )
                        
                        episodes.add(
                            Episode(
                                id = episodeId,
                                title = apiEpisode.title ?: "Unknown Episode",
                                streamUrl = streamUrl,
                                seasonNumber = apiEpisode.season ?: 1,
                                episodeNumber = apiEpisode.episodeNum ?: 1
                            )
                        )
                    }
                }
            }
            
            Log.d(TAG, "Loaded ${episodes.size} episodes for series $seriesId")
            episodes
            
        } catch (e: Exception) {
            Log.e(TAG, "Error loading episodes for series $seriesId", e)
            emptyList()
        }
    }
}