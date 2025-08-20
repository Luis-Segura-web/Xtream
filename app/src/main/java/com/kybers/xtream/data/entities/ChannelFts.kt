package com.kybers.xtream.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

@Fts4(contentEntity = Channel::class)
@Entity(tableName = "channels_fts")
data class ChannelFts(
    val name: String,
    
    @ColumnInfo(name = "group_name")
    val groupName: String?,
    
    val country: String?,
    
    val language: String?
)