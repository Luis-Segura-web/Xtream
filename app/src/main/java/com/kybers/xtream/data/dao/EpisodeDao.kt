package com.kybers.xtream.data.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.kybers.xtream.data.entities.Episode
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao {
    
    @Query("SELECT * FROM episodes WHERE season_id = :seasonId ORDER BY number ASC")
    fun getEpisodesBySeasonPaged(seasonId: String): PagingSource<Int, Episode>
    
    @Query("SELECT * FROM episodes WHERE season_id = :seasonId ORDER BY number ASC")
    fun getEpisodesBySeason(seasonId: String): Flow<List<Episode>>
    
    @Query("SELECT * FROM episodes WHERE last_watched > 0 ORDER BY last_watched DESC LIMIT :limit")
    fun getRecentlyWatchedEpisodes(limit: Int = 20): Flow<List<Episode>>
    
    @Query("SELECT * FROM episodes WHERE watch_progress > 0 AND watch_progress < 1 ORDER BY last_watched DESC")
    fun getContinueWatchingEpisodes(): Flow<List<Episode>>
    
    @Query("SELECT * FROM episodes WHERE id = :episodeId")
    suspend fun getEpisodeById(episodeId: String): Episode?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisodes(episodes: List<Episode>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisode(episode: Episode)
    
    @Update
    suspend fun updateEpisode(episode: Episode)
    
    @Query("UPDATE episodes SET last_watched = :timestamp, watch_progress = :progress WHERE id = :episodeId")
    suspend fun updateWatchProgress(
        episodeId: String, 
        progress: Float, 
        timestamp: Long = System.currentTimeMillis()
    )
    
    @Delete
    suspend fun deleteEpisode(episode: Episode)
    
    @Query("DELETE FROM episodes WHERE season_id = :seasonId")
    suspend fun deleteEpisodesBySeasonId(seasonId: String)
    
    @Query("DELETE FROM episodes")
    suspend fun deleteAllEpisodes()
    
    @Query("SELECT COUNT(*) FROM episodes WHERE season_id = :seasonId")
    suspend fun getEpisodeCountBySeason(seasonId: String): Int
}