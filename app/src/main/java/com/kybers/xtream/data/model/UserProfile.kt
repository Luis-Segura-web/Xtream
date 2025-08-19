package com.kybers.xtream.data.model

data class UserProfile(
    val id: Long = 0,
    val profileName: String,
    val username: String,
    val password: String,
    val serverUrl: String,
    val createdAt: Long = System.currentTimeMillis()
)