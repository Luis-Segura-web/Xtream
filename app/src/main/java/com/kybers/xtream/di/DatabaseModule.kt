package com.kybers.xtream.di

import android.content.Context
import androidx.room.Room
import com.kybers.xtream.data.database.XtreamDatabase
import com.kybers.xtream.data.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideXtreamDatabase(@ApplicationContext context: Context): XtreamDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            XtreamDatabase::class.java,
            XtreamDatabase.DATABASE_NAME
        )
            .enableMultiInstanceInvalidation()
            .fallbackToDestructiveMigration() // TODO: Remove in production
            .build()
    }
    
    @Provides
    fun provideChannelDao(database: XtreamDatabase): ChannelDao {
        return database.channelDao()
    }
    
    @Provides
    fun provideVodDao(database: XtreamDatabase): VodDao {
        return database.vodDao()
    }
    
    @Provides
    fun provideSeasonDao(database: XtreamDatabase): SeasonDao {
        return database.seasonDao()
    }
    
    @Provides
    fun provideEpisodeDao(database: XtreamDatabase): EpisodeDao {
        return database.episodeDao()
    }
    
    @Provides
    fun provideEpgDao(database: XtreamDatabase): EpgDao {
        return database.epgDao()
    }
}