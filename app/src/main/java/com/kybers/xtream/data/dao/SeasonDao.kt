package com.kybers.xtream.data.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.kybers.xtream.data.entities.Season
import kotlinx.coroutines.flow.Flow

@Dao
interface SeasonDao {
    
    @Query("SELECT * FROM seasons WHERE vod_id = :vodId ORDER BY number ASC")
    fun getSeasonsByVodPaged(vodId: String): PagingSource<Int, Season>
    
    @Query("SELECT * FROM seasons WHERE vod_id = :vodId ORDER BY number ASC")
    fun getSeasonsByVod(vodId: String): Flow<List<Season>>
    
    @Query("SELECT * FROM seasons WHERE id = :seasonId")
    suspend fun getSeasonById(seasonId: String): Season?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeasons(seasons: List<Season>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeason(season: Season)
    
    @Update
    suspend fun updateSeason(season: Season)
    
    @Delete
    suspend fun deleteSeason(season: Season)
    
    @Query("DELETE FROM seasons WHERE vod_id = :vodId")
    suspend fun deleteSeasonsByVodId(vodId: String)
    
    @Query("DELETE FROM seasons")
    suspend fun deleteAllSeasons()
    
    @Query("SELECT COUNT(*) FROM seasons WHERE vod_id = :vodId")
    suspend fun getSeasonCountByVod(vodId: String): Int
}