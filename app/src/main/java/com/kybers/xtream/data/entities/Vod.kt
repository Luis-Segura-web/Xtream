package com.kybers.xtream.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class VodType {
    MOVIE, SERIES
}

@Entity(
    tableName = "vod",
    indices = [
        Index(value = ["type"]),
        Index(value = ["title"]),
        Index(value = ["year"]),
        Index(value = ["country"]),
        Index(value = ["language"]),
        Index(value = ["rating"]),
        Index(value = ["is_favorite"]),
        Index(value = ["last_seen"])
    ]
)
data class Vod(
    @PrimaryKey
    val id: String,
    
    val type: VodType,
    
    val title: String,
    
    val year: Int? = null,
    
    @ColumnInfo(name = "poster_url")
    val posterUrl: String? = null,
    
    @ColumnInfo(name = "backdrop_url")
    val backdropUrl: String? = null,
    
    val genres: String? = null, // JSON array or comma-separated
    
    val country: String? = null,
    
    val language: String? = null,
    
    val rating: Float? = null,
    
    val plot: String? = null,
    
    @ColumnInfo(name = "stream_url")
    val streamUrl: String? = null, // For movies, null for series
    
    @ColumnInfo(name = "last_seen")
    val lastSeen: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,
    
    val duration: Int? = null, // In minutes
    
    @ColumnInfo(name = "quality")
    val quality: String? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)