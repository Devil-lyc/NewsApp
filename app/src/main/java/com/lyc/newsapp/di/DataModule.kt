package com.lyc.newsapp.di

import com.lyc.newsapp.core.config.ApiKeyConfig
import com.lyc.newsapp.data.remote.NewsApi
import com.lyc.newsapp.data.remote.NewsApi.Companion.BASE_URL
import com.lyc.newsapp.data.remote.interceptor.ApiKeyInterceptor
import com.lyc.newsapp.data.remote.interceptor.ManualNewsCacheInterceptor
import com.lyc.newsapp.data.remote.interceptor.MetricsInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
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
    @NewsHttpClientRef
    fun provideNewsHttpClientRef(): AtomicReference<OkHttpClient?> = AtomicReference(null)

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
        @NewsHttpClientRef clientRef: AtomicReference<OkHttpClient?>,
        manualNewsCacheInterceptor: ManualNewsCacheInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        apiKeyInterceptor: ApiKeyInterceptor,
        metricsInterceptor: MetricsInterceptor
    ): OkHttpClient {
        val client = OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(manualNewsCacheInterceptor)
            .addInterceptor(metricsInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
        clientRef.set(client)
        return client
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