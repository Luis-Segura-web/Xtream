package com.kybers.xtream.di

import com.kybers.xtream.data.dao.ChannelDao
import com.kybers.xtream.data.dao.VodDao
import com.kybers.xtream.data.repository.ChannelRepository
import com.kybers.xtream.data.repository.VodRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideChannelRepository(channelDao: ChannelDao): ChannelRepository {
        return ChannelRepository(channelDao)
    }
    
    @Provides
    @Singleton
    fun provideVodRepository(vodDao: VodDao): VodRepository {
        return VodRepository(vodDao)
    }
}