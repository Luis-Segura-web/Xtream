package com.kybers.xtream.data.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.kybers.xtream.data.entities.Channel
import com.kybers.xtream.data.entities.ChannelFts
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {
    
    @Query("SELECT * FROM channels ORDER BY name ASC")
    fun getAllChannelsPaged(): PagingSource<Int, Channel>
    
    @Query("SELECT * FROM channels WHERE group_name = :groupName ORDER BY name ASC")
    fun getChannelsByGroupPaged(groupName: String): PagingSource<Int, Channel>
    
    @Query("SELECT * FROM channels WHERE country = :country ORDER BY name ASC")
    fun getChannelsByCountryPaged(country: String): PagingSource<Int, Channel>
    
    @Query("SELECT * FROM channels WHERE language = :language ORDER BY name ASC")
    fun getChannelsByLanguagePaged(language: String): PagingSource<Int, Channel>
    
    @Query("SELECT * FROM channels WHERE is_favorite = 1 ORDER BY name ASC")
    fun getFavoriteChannelsPaged(): PagingSource<Int, Channel>
    
    @Query("""
        SELECT c.* FROM channels c
        JOIN channels_fts fts ON c.rowid = fts.rowid
        WHERE channels_fts MATCH :query
        ORDER BY name ASC
    """)
    fun searchChannelsPaged(query: String): PagingSource<Int, Channel>
    
    @Query("SELECT * FROM channels WHERE id = :channelId")
    suspend fun getChannelById(channelId: String): Channel?
    
    @Query("SELECT DISTINCT group_name FROM channels WHERE group_name IS NOT NULL ORDER BY group_name ASC")
    fun getChannelGroups(): Flow<List<String>>
    
    @Query("SELECT DISTINCT country FROM channels WHERE country IS NOT NULL ORDER BY country ASC")
    fun getChannelCountries(): Flow<List<String>>
    
    @Query("SELECT DISTINCT language FROM channels WHERE language IS NOT NULL ORDER BY language ASC")
    fun getChannelLanguages(): Flow<List<String>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<Channel>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: Channel)
    
    @Update
    suspend fun updateChannel(channel: Channel)
    
    @Query("UPDATE channels SET is_favorite = :isFavorite WHERE id = :channelId")
    suspend fun updateFavoriteStatus(channelId: String, isFavorite: Boolean)
    
    @Query("UPDATE channels SET last_seen = :timestamp WHERE id = :channelId")
    suspend fun updateLastSeen(channelId: String, timestamp: Long = System.currentTimeMillis())
    
    @Delete
    suspend fun deleteChannel(channel: Channel)
    
    @Query("DELETE FROM channels WHERE last_seen < :cutoffTime")
    suspend fun deleteOldChannels(cutoffTime: Long)
    
    @Query("DELETE FROM channels")
    suspend fun deleteAllChannels()
    
    @Query("SELECT COUNT(*) FROM channels")
    suspend fun getChannelCount(): Int
    
    // FTS maintenance
    @Query("INSERT INTO channels_fts(rowid, name, group_name, country, language) SELECT rowid, name, group_name, country, language FROM channels")
    suspend fun rebuildFts()
}