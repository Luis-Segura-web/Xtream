package com.kybers.xtream.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "episodes",
    foreignKeys = [
        ForeignKey(
            entity = Season::class,
            parentColumns = ["id"],
            childColumns = ["season_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["season_id"]),
        Index(value = ["number"]),
        Index(value = ["last_watched"])
    ]
)
data class Episode(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "season_id")
    val seasonId: String,
    
    val title: String,
    
    val number: Int,
    
    @ColumnInfo(name = "stream_url")
    val streamUrl: String,
    
    val plot: String? = null,
    
    @ColumnInfo(name = "thumbnail_url")
    val thumbnailUrl: String? = null,
    
    val duration: Int? = null, // In minutes
    
    @ColumnInfo(name = "air_date")
    val airDate: String? = null,
    
    val rating: Float? = null,
    
    @ColumnInfo(name = "last_watched")
    val lastWatched: Long? = null,
    
    @ColumnInfo(name = "watch_progress")
    val watchProgress: Float = 0f, // 0.0 to 1.0
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)