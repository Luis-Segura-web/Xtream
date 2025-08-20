package com.kybers.xtream.data.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.kybers.xtream.data.entities.EpgProgram
import kotlinx.coroutines.flow.Flow

@Dao
interface EpgDao {
    
    @Query("""
        SELECT * FROM epg_programs 
        WHERE channel_id = :channelId 
        AND start_time >= :startTime 
        AND start_time <= :endTime 
        ORDER BY start_time ASC
    """)
    fun getEpgByChannelAndTimeRange(
        channelId: String,
        startTime: Long,
        endTime: Long
    ): Flow<List<EpgProgram>>
    
    @Query("""
        SELECT * FROM epg_programs 
        WHERE channel_id = :channelId 
        AND start_time >= :startTime 
        AND start_time <= :endTime 
        ORDER BY start_time ASC
    """)
    fun getEpgByChannelAndTimeRangePaged(
        channelId: String,
        startTime: Long,
        endTime: Long
    ): PagingSource<Int, EpgProgram>
    
    @Query("""
        SELECT * FROM epg_programs 
        WHERE start_time <= :currentTime 
        AND end_time > :currentTime 
        ORDER BY start_time ASC
    """)
    fun getCurrentPrograms(currentTime: Long): Flow<List<EpgProgram>>
    
    @Query("""
        SELECT * FROM epg_programs 
        WHERE channel_id = :channelId 
        AND start_time <= :currentTime 
        AND end_time > :currentTime 
        LIMIT 1
    """)
    suspend fun getCurrentProgramForChannel(channelId: String, currentTime: Long): EpgProgram?
    
    @Query("""
        SELECT * FROM epg_programs 
        WHERE channel_id = :channelId 
        AND start_time > :currentTime 
        ORDER BY start_time ASC 
        LIMIT 1
    """)
    suspend fun getNextProgramForChannel(channelId: String, currentTime: Long): EpgProgram?
    
    @Query("SELECT * FROM epg_programs WHERE id = :programId")
    suspend fun getProgramById(programId: String): EpgProgram?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrograms(programs: List<EpgProgram>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgram(program: EpgProgram)
    
    @Update
    suspend fun updateProgram(program: EpgProgram)
    
    @Delete
    suspend fun deleteProgram(program: EpgProgram)
    
    @Query("DELETE FROM epg_programs WHERE channel_id = :channelId")
    suspend fun deleteProgramsByChannelId(channelId: String)
    
    @Query("DELETE FROM epg_programs WHERE end_time < :cutoffTime")
    suspend fun deleteOldPrograms(cutoffTime: Long)
    
    @Query("DELETE FROM epg_programs")
    suspend fun deleteAllPrograms()
    
    @Query("SELECT COUNT(*) FROM epg_programs WHERE channel_id = :channelId")
    suspend fun getProgramCountByChannel(channelId: String): Int
}