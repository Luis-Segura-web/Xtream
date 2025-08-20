package com.kybers.xtream.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kybers.xtream.data.dao.ChannelDao
import com.kybers.xtream.data.entities.Channel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChannelRepository @Inject constructor(
    private val channelDao: ChannelDao
) {
    
    companion object {
        private const val PAGE_SIZE = 20
        private const val PREFETCH_DISTANCE = 5
    }
    
    fun getAllChannelsPaged(): Flow<PagingData<Channel>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { channelDao.getAllChannelsPaged() }
        ).flow
    }
    
    fun getChannelsByGroupPaged(groupName: String): Flow<PagingData<Channel>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { channelDao.getChannelsByGroupPaged(groupName) }
        ).flow
    }
    
    fun getChannelsByCountryPaged(country: String): Flow<PagingData<Channel>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { channelDao.getChannelsByCountryPaged(country) }
        ).flow
    }
    
    fun getChannelsByLanguagePaged(language: String): Flow<PagingData<Channel>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { channelDao.getChannelsByLanguagePaged(language) }
        ).flow
    }
    
    fun getFavoriteChannelsPaged(): Flow<PagingData<Channel>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { channelDao.getFavoriteChannelsPaged() }
        ).flow
    }
    
    fun searchChannelsPaged(query: String): Flow<PagingData<Channel>> {
        val ftsQuery = query.split(" ").joinToString(" AND ") { "$it*" }
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { channelDao.searchChannelsPaged(ftsQuery) }
        ).flow
    }
    
    suspend fun getChannelById(channelId: String): Channel? {
        return channelDao.getChannelById(channelId)
    }
    
    fun getChannelGroups(): Flow<List<String>> {
        return channelDao.getChannelGroups()
    }
    
    fun getChannelCountries(): Flow<List<String>> {
        return channelDao.getChannelCountries()
    }
    
    fun getChannelLanguages(): Flow<List<String>> {
        return channelDao.getChannelLanguages()
    }
    
    suspend fun insertChannels(channels: List<Channel>) {
        channelDao.insertChannels(channels)
    }
    
    suspend fun updateFavoriteStatus(channelId: String, isFavorite: Boolean) {
        channelDao.updateFavoriteStatus(channelId, isFavorite)
    }
    
    suspend fun updateLastSeen(channelId: String) {
        channelDao.updateLastSeen(channelId)
    }
    
    suspend fun deleteOldChannels(cutoffTime: Long) {
        channelDao.deleteOldChannels(cutoffTime)
    }
    
    suspend fun getChannelCount(): Int {
        return channelDao.getChannelCount()
    }
}