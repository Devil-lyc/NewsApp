package com.lyc.newsapp.di

import android.content.Context
import com.lyc.newsapp.util.ApiKeyConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideApiKeyConfig(@ApplicationContext context: Context): ApiKeyConfig {
        return ApiKeyConfig(context)
    }
} 