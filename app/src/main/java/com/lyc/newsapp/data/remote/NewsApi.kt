package com.lyc.newsapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    companion object {
        const val BASE_URL = "https://newsdata.io/api/1/"
    }
    /**
     * 获取最新新闻
     */
    @GET("latest")
    suspend fun getLatestNews(
        @Query("language") language: String = "zh",
        @Query("country") country: String = "cn",
        @Query("apikey") apiKey: String? = null,
        @Query("image") image: Int = 1
    ): NewsResponse

    /**
     * 根据ID获取新闻详情
     */
    @GET("latest")
    suspend fun getNewsById(
        @Query("id") id: String,
        @Query("apikey") apiKey: String? = null
    ): NewsResponse

    /**
     * 根据分类获取新闻
     */
    @GET("latest")
    suspend fun getNewsByCategory(
        @Query("category") category: String,
        @Query("language") language: String = "zh",
        @Query("country") country: String = "cn",
        @Query("apikey") apiKey: String? = null,
        @Query("image") image: Int = 1
    ): NewsResponse

    /**
     * 加载下一页
     */
    @GET("latest")
    suspend fun getNextPage(
        @Query("category") category: String,
        @Query("page") page: String,
        @Query("language") language: String = "zh",
        @Query("country") country: String = "cn",
        @Query("apikey") apiKey: String? = null,
        @Query("image") image: Int = 1
    ): NewsResponse

    /**
     * 搜索新闻
     */
    @GET("latest")
    suspend fun searchNews(
        @Query("q") q: String,
        @Query("language") language: String = "zh",
        @Query("country") country: String = "cn",
        @Query("apikey") apiKey: String? = null,
        @Query("image") image: Int = 1
    ): NewsResponse
}