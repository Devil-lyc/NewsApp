package com.lyc.newsapp.di

import com.lyc.newsapp.data.remote.interceptor.ApiKeyInterceptor
import com.lyc.newsapp.data.remote.NewsApi
import com.lyc.newsapp.data.remote.NewsApi.Companion.BASE_URL
import com.lyc.newsapp.core.config.ApiKeyConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * 新闻API相关限定符
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NewsClient

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideApiKeyInterceptor(apiKeyConfig: ApiKeyConfig): ApiKeyInterceptor {
        return ApiKeyInterceptor(apiKeyConfig.getNewsApiKey())
    }

    @NewsClient
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        apiKeyInterceptor: ApiKeyInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor) // 首先添加 API 密钥
            .addInterceptor(loggingInterceptor) // 然后添加日志拦截器，可以看到带 API 密钥的请求
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsApi(@NewsClient client: OkHttpClient): NewsApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApi::class.java)
    }
}