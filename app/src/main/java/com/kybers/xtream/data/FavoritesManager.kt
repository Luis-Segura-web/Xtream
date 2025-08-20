package com.kybers.xtream.data

import android.content.Context
import android.content.SharedPreferences

class FavoritesManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("favorites_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val FAVORITE_CHANNELS_KEY = "favorite_channels"
        private const val FAVORITE_MOVIES_KEY = "favorite_movies"
        private const val FAVORITE_SERIES_KEY = "favorite_series"
    }
    
    fun addChannelToFavorites(channelId: String) {
        val favorites = getFavoriteChannels().toMutableSet()
        favorites.add(channelId)
        saveFavoriteChannels(favorites)
    }
    
    fun removeChannelFromFavorites(channelId: String) {
        val favorites = getFavoriteChannels().toMutableSet()
        favorites.remove(channelId)
        saveFavoriteChannels(favorites)
    }
    
    fun isChannelFavorite(channelId: String): Boolean {
        return getFavoriteChannels().contains(channelId)
    }
    
    fun getFavoriteChannels(): Set<String> {
        return sharedPreferences.getStringSet(FAVORITE_CHANNELS_KEY, emptySet()) ?: emptySet()
    }
    
    private fun saveFavoriteChannels(favorites: Set<String>) {
        sharedPreferences.edit()
            .putStringSet(FAVORITE_CHANNELS_KEY, favorites)
            .apply()
    }
    
    fun toggleChannelFavorite(channelId: String): Boolean {
        return if (isChannelFavorite(channelId)) {
            removeChannelFromFavorites(channelId)
            false
        } else {
            addChannelToFavorites(channelId)
            true
        }
    }
    
    // Métodos similares para películas y series
    fun addMovieToFavorites(movieId: String) {
        val favorites = getFavoriteMovies().toMutableSet()
        favorites.add(movieId)
        saveFavoriteMovies(favorites)
    }
    
    fun removeMovieFromFavorites(movieId: String) {
        val favorites = getFavoriteMovies().toMutableSet()
        favorites.remove(movieId)
        saveFavoriteMovies(favorites)
    }
    
    fun isMovieFavorite(movieId: String): Boolean {
        return getFavoriteMovies().contains(movieId)
    }
    
    fun getFavoriteMovies(): Set<String> {
        return sharedPreferences.getStringSet(FAVORITE_MOVIES_KEY, emptySet()) ?: emptySet()
    }
    
    private fun saveFavoriteMovies(favorites: Set<String>) {
        sharedPreferences.edit()
            .putStringSet(FAVORITE_MOVIES_KEY, favorites)
            .apply()
    }
    
    fun toggleMovieFavorite(movieId: String): Boolean {
        return if (isMovieFavorite(movieId)) {
            removeMovieFromFavorites(movieId)
            false
        } else {
            addMovieToFavorites(movieId)
            true
        }
    }
    
    fun addSeriesToFavorites(seriesId: String) {
        val favorites = getFavoriteSeries().toMutableSet()
        favorites.add(seriesId)
        saveFavoriteSeries(favorites)
    }
    
    fun removeSeriesFromFavorites(seriesId: String) {
        val favorites = getFavoriteSeries().toMutableSet()
        favorites.remove(seriesId)
        saveFavoriteSeries(favorites)
    }
    
    fun isSeriesFavorite(seriesId: String): Boolean {
        return getFavoriteSeries().contains(seriesId)
    }
    
    fun getFavoriteSeries(): Set<String> {
        return sharedPreferences.getStringSet(FAVORITE_SERIES_KEY, emptySet()) ?: emptySet()
    }
    
    private fun saveFavoriteSeries(favorites: Set<String>) {
        sharedPreferences.edit()
            .putStringSet(FAVORITE_SERIES_KEY, favorites)
            .apply()
    }
    
    fun toggleSeriesFavorite(seriesId: String): Boolean {
        return if (isSeriesFavorite(seriesId)) {
            removeSeriesFromFavorites(seriesId)
            false
        } else {
            addSeriesToFavorites(seriesId)
            true
        }
    }
}