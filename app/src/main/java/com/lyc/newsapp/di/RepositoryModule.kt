package com.lyc.newsapp.di

import com.lyc.newsapp.data.repository.FavoriteRepositoryImpl
import com.lyc.newsapp.domain.repository.FavoriteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 仓库模块 - 提供所有仓库的绑定
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    /**
     * 绑定收藏仓库实现
     */
    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(
        favoriteRepositoryImpl: FavoriteRepositoryImpl
    ): FavoriteRepository
} 