package com.lyc.newsapp.di

import com.lyc.newsapp.data.remote.AuthApiService
import com.lyc.newsapp.data.remote.interceptor.AuthInterceptor
import com.lyc.newsapp.data.remote.FavoriteApiService
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
 * 认证相关限定符
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthClient

/**
 * 收藏API相关限定符
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FavoriteClient

/**
 * Auth Retrofit限定符
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthRetrofit

/**
 * Favorite Retrofit限定符
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FavoriteRetrofit

// 定义Retrofit类型别名，避免多重绑定错误
typealias AuthRetrofitType = Retrofit
typealias FavoriteRetrofitType = Retrofit

/**
 * 网络模块 - 提供Retrofit相关依赖
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    private const val BASE_URL = "https://kkzynytfzajt.sealoshzh.site/"
    
    /**
     * 提供用于认证的OkHttpClient实例（不包含认证令牌）
     */
    @AuthClient
    @Provides
    @Singleton
    fun provideAuthOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * 提供用于收藏API的OkHttpClient实例（包含认证令牌）
     */
    @FavoriteClient
    @Provides
    @Singleton
    fun provideFavoriteOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)  // 添加授权拦截器
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * 提供用于认证的Retrofit实例
     */
    @AuthRetrofit
    @Provides
    @Singleton
    fun provideAuthRetrofit(@AuthClient okHttpClient: OkHttpClient): AuthRetrofitType {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * 提供用于收藏API的Retrofit实例
     */
    @FavoriteRetrofit
    @Provides
    @Singleton
    fun provideFavoriteRetrofit(@FavoriteClient okHttpClient: OkHttpClient): FavoriteRetrofitType {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * 提供认证API服务
     */
    @Provides
    @Singleton
    fun provideAuthApiService(@AuthRetrofit retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }
    
    /**
     * 提供收藏API服务
     */
    @Provides
    @Singleton
    fun provideFavoriteApiService(@FavoriteRetrofit retrofit: Retrofit): FavoriteApiService {
        return retrofit.create(FavoriteApiService::class.java)
    }
} 