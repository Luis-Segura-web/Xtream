package com.kybers.xtream.data.entities

import androidx.room.Entity
import androidx.room.Fts4

@Fts4(contentEntity = Vod::class)
@Entity(tableName = "vod_fts")
data class VodFts(
    val title: String,
    val genres: String?,
    val plot: String?
)