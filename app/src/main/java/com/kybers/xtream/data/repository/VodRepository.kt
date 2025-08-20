package com.kybers.xtream.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kybers.xtream.data.dao.VodDao
import com.kybers.xtream.data.entities.Vod
import com.kybers.xtream.data.entities.VodType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VodRepository @Inject constructor(
    private val vodDao: VodDao
) {
    
    companion object {
        private const val PAGE_SIZE = 20
        private const val PREFETCH_DISTANCE = 5
    }
    
    fun getVodByTypePaged(type: VodType): Flow<PagingData<Vod>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { vodDao.getVodByTypePaged(type) }
        ).flow
    }
    
    fun getVodByTypeAndGenrePaged(type: VodType, genre: String): Flow<PagingData<Vod>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { vodDao.getVodByTypeAndGenrePaged(type, genre) }
        ).flow
    }
    
    fun getVodByTypeAndCountryPaged(type: VodType, country: String): Flow<PagingData<Vod>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { vodDao.getVodByTypeAndCountryPaged(type, country) }
        ).flow
    }
    
    fun getVodByTypeAndLanguagePaged(type: VodType, language: String): Flow<PagingData<Vod>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { vodDao.getVodByTypeAndLanguagePaged(type, language) }
        ).flow
    }
    
    fun getVodByTypeAndYearPaged(type: VodType, year: Int): Flow<PagingData<Vod>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { vodDao.getVodByTypeAndYearPaged(type, year) }
        ).flow
    }
    
    fun getVodByTypeAndMinRatingPaged(type: VodType, minRating: Float): Flow<PagingData<Vod>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { vodDao.getVodByTypeAndMinRatingPaged(type, minRating) }
        ).flow
    }
    
    fun getFavoriteVodPaged(): Flow<PagingData<Vod>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { vodDao.getFavoriteVodPaged() }
        ).flow
    }
    
    fun getRecentlyWatchedPaged(): Flow<PagingData<Vod>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { vodDao.getRecentlyWatchedPaged() }
        ).flow
    }
    
    fun searchVodPaged(query: String, type: VodType): Flow<PagingData<Vod>> {
        val ftsQuery = query.split(" ").joinToString(" AND ") { "$it*" }
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { vodDao.searchVodPaged(ftsQuery, type) }
        ).flow
    }
    
    suspend fun getVodById(vodId: String): Vod? {
        return vodDao.getVodById(vodId)
    }
    
    suspend fun getGenresByType(type: VodType): List<String> {
        return vodDao.getGenresByType(type)
    }
    
    fun getCountriesByType(type: VodType): Flow<List<String>> {
        return vodDao.getCountriesByType(type)
    }
    
    fun getLanguagesByType(type: VodType): Flow<List<String>> {
        return vodDao.getLanguagesByType(type)
    }
    
    fun getYearsByType(type: VodType): Flow<List<Int>> {
        return vodDao.getYearsByType(type)
    }
    
    suspend fun insertVod(vod: List<Vod>) {
        vodDao.insertVod(vod)
    }
    
    suspend fun updateFavoriteStatus(vodId: String, isFavorite: Boolean) {
        vodDao.updateFavoriteStatus(vodId, isFavorite)
    }
    
    suspend fun updateLastSeen(vodId: String) {
        vodDao.updateLastSeen(vodId)
    }
    
    suspend fun deleteOldVod(cutoffTime: Long) {
        vodDao.deleteOldVod(cutoffTime)
    }
    
    suspend fun getVodCountByType(type: VodType): Int {
        return vodDao.getVodCountByType(type)
    }
}