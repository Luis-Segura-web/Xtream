package com.kybers.xtream.ui.common

interface RefreshListener {
    suspend fun onRefreshRequested(): Boolean
}