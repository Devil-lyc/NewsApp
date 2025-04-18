package com.lyc.newsapp.di

import android.content.Context
import com.lyc.newsapp.data.preferences.ThemePreference
import com.lyc.newsapp.util.ApiKeyConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 应用模块，提供应用级别的依赖
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * 提供ThemePreference单例
     */
    @Provides
    @Singleton
    fun provideThemePreference(@ApplicationContext context: Context): ThemePreference {
        return ThemePreference(context)
    }

    @Provides
    @Singleton
    fun provideApiKeyConfig(@ApplicationContext context: Context): ApiKeyConfig {
        return ApiKeyConfig(context)
    }
} 