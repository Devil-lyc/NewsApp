package com.example.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNewsRepository(
        newsRepositoryImpl: com.example.data.repository.NewsRepositoryImpl
    ): com.example.data.repository.NewsRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(
        favoriteRepositoryImpl: com.example.data.repository.FavoriteRepositoryImpl
    ): com.example.data.repository.FavoriteRepository
}