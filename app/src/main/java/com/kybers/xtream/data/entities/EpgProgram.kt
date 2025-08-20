package com.kybers.xtream.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "epg_programs",
    foreignKeys = [
        ForeignKey(
            entity = Channel::class,
            parentColumns = ["id"],
            childColumns = ["channel_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["channel_id", "start_time"]),
        Index(value = ["start_time"]),
        Index(value = ["end_time"])
    ]
)
data class EpgProgram(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "channel_id")
    val channelId: String,
    
    @ColumnInfo(name = "start_time")
    val startTime: Long,
    
    @ColumnInfo(name = "end_time")
    val endTime: Long,
    
    val title: String,
    
    val description: String? = null,
    
    val category: String? = null,
    
    val rating: String? = null,
    
    @ColumnInfo(name = "poster_url")
    val posterUrl: String? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)