package com.kybers.xtream.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "seasons",
    foreignKeys = [
        ForeignKey(
            entity = Vod::class,
            parentColumns = ["id"],
            childColumns = ["vod_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["vod_id"]),
        Index(value = ["number"])
    ]
)
data class Season(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "vod_id")
    val vodId: String,
    
    val number: Int,
    
    val title: String? = null,
    
    @ColumnInfo(name = "poster_url")
    val posterUrl: String? = null,
    
    @ColumnInfo(name = "episode_count")
    val episodeCount: Int = 0,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)