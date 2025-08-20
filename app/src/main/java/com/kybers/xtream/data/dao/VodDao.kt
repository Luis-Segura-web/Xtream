package com.kybers.xtream.data.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.kybers.xtream.data.entities.Vod
import com.kybers.xtream.data.entities.VodType
import kotlinx.coroutines.flow.Flow

@Dao
interface VodDao {
    
    @Query("SELECT * FROM vod WHERE type = :type ORDER BY title ASC")
    fun getVodByTypePaged(type: VodType): PagingSource<Int, Vod>
    
    @Query("SELECT * FROM vod WHERE type = :type AND genres LIKE '%' || :genre || '%' ORDER BY title ASC")
    fun getVodByTypeAndGenrePaged(type: VodType, genre: String): PagingSource<Int, Vod>
    
    @Query("SELECT * FROM vod WHERE type = :type AND country = :country ORDER BY title ASC")
    fun getVodByTypeAndCountryPaged(type: VodType, country: String): PagingSource<Int, Vod>
    
    @Query("SELECT * FROM vod WHERE type = :type AND language = :language ORDER BY title ASC")
    fun getVodByTypeAndLanguagePaged(type: VodType, language: String): PagingSource<Int, Vod>
    
    @Query("SELECT * FROM vod WHERE type = :type AND year = :year ORDER BY title ASC")
    fun getVodByTypeAndYearPaged(type: VodType, year: Int): PagingSource<Int, Vod>
    
    @Query("SELECT * FROM vod WHERE type = :type AND rating >= :minRating ORDER BY rating DESC, title ASC")
    fun getVodByTypeAndMinRatingPaged(type: VodType, minRating: Float): PagingSource<Int, Vod>
    
    @Query("SELECT * FROM vod WHERE is_favorite = 1 ORDER BY title ASC")
    fun getFavoriteVodPaged(): PagingSource<Int, Vod>
    
    @Query("SELECT * FROM vod WHERE last_seen > 0 ORDER BY last_seen DESC")
    fun getRecentlyWatchedPaged(): PagingSource<Int, Vod>
    
    @Query("""
        SELECT v.* FROM vod v
        JOIN vod_fts fts ON v.rowid = fts.rowid
        WHERE vod_fts MATCH :query AND type = :type
        ORDER BY title ASC
    """)
    fun searchVodPaged(query: String, type: VodType): PagingSource<Int, Vod>
    
    @Query("SELECT * FROM vod WHERE id = :vodId")
    suspend fun getVodById(vodId: String): Vod?
    
    @Query("SELECT DISTINCT genres FROM vod WHERE type = :type AND genres IS NOT NULL")
    suspend fun getGenresByType(type: VodType): List<String>
    
    @Query("SELECT DISTINCT country FROM vod WHERE type = :type AND country IS NOT NULL ORDER BY country ASC")
    fun getCountriesByType(type: VodType): Flow<List<String>>
    
    @Query("SELECT DISTINCT language FROM vod WHERE type = :type AND language IS NOT NULL ORDER BY language ASC")
    fun getLanguagesByType(type: VodType): Flow<List<String>>
    
    @Query("SELECT DISTINCT year FROM vod WHERE type = :type AND year IS NOT NULL ORDER BY year DESC")
    fun getYearsByType(type: VodType): Flow<List<Int>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVod(vod: List<Vod>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVodItem(vod: Vod)
    
    @Update
    suspend fun updateVod(vod: Vod)
    
    @Query("UPDATE vod SET is_favorite = :isFavorite WHERE id = :vodId")
    suspend fun updateFavoriteStatus(vodId: String, isFavorite: Boolean)
    
    @Query("UPDATE vod SET last_seen = :timestamp WHERE id = :vodId")
    suspend fun updateLastSeen(vodId: String, timestamp: Long = System.currentTimeMillis())
    
    @Delete
    suspend fun deleteVod(vod: Vod)
    
    @Query("DELETE FROM vod WHERE last_seen < :cutoffTime")
    suspend fun deleteOldVod(cutoffTime: Long)
    
    @Query("DELETE FROM vod WHERE type = :type")
    suspend fun deleteVodByType(type: VodType)
    
    @Query("DELETE FROM vod")
    suspend fun deleteAllVod()
    
    @Query("SELECT COUNT(*) FROM vod WHERE type = :type")
    suspend fun getVodCountByType(type: VodType): Int
    
    // FTS maintenance
    @Query("INSERT INTO vod_fts(rowid, title, genres, plot) SELECT rowid, title, genres, plot FROM vod")
    suspend fun rebuildFts()
}