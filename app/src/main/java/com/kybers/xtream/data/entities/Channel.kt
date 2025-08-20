package com.kybers.xtream.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "channels",
    indices = [
        Index(value = ["name"]),
        Index(value = ["group_name"]),
        Index(value = ["country"]),
        Index(value = ["language"]),
        Index(value = ["is_favorite"]),
        Index(value = ["last_seen"])
    ]
)
data class Channel(
    @PrimaryKey
    val id: String,
    
    val name: String,
    
    @ColumnInfo(name = "logo_url")
    val logoUrl: String? = null,
    
    @ColumnInfo(name = "group_name")
    val groupName: String? = null,
    
    val country: String? = null,
    
    val language: String? = null,
    
    @ColumnInfo(name = "stream_url")
    val streamUrl: String,
    
    @ColumnInfo(name = "last_seen")
    val lastSeen: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,
    
    val quality: String? = null,
    
    @ColumnInfo(name = "epg_id")
    val epgId: String? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)