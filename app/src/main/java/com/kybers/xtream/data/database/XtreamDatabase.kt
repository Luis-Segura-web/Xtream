package com.kybers.xtream.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.kybers.xtream.data.entities.*
import com.kybers.xtream.data.dao.*

@Database(
    entities = [
        Channel::class,
        ChannelFts::class,
        Vod::class,
        VodFts::class,
        Season::class,
        Episode::class,
        EpgProgram::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class XtreamDatabase : RoomDatabase() {
    
    abstract fun channelDao(): ChannelDao
    abstract fun vodDao(): VodDao
    abstract fun seasonDao(): SeasonDao
    abstract fun episodeDao(): EpisodeDao
    abstract fun epgDao(): EpgDao
    
    companion object {
        const val DATABASE_NAME = "xtream_database"
        
        @Volatile
        private var INSTANCE: XtreamDatabase? = null
        
        fun getDatabase(context: Context): XtreamDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    XtreamDatabase::class.java,
                    DATABASE_NAME
                )
                    .enableMultiInstanceInvalidation()
                    .fallbackToDestructiveMigration() // TODO: Remove in production
                    .build()
                    
                INSTANCE = instance
                instance
            }
        }
    }
}