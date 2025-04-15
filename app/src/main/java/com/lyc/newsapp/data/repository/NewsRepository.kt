package com.lyc.newsapp.data.repository

import com.lyc.newsapp.domain.model.News
import com.lyc.newsapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface NewsRepository {

    suspend fun getNewsList(
        language: String = "zh",
        country: String = "cn",
    ): Flow<Resource<List<News>>>

    suspend fun getNewsById(
        id: String,
        language: String = "zh",
        country: String = "cn",
    ): Flow<Resource<News>>

    suspend fun getNewsByCategory(
        category: String,
        language: String = "zh",
        country: String = "cn",
    ): Flow<Resource<List<News>>>

    suspend fun getNextPage(
        category: String,
        nextPage: String,
        language: String = "zh",
        country: String = "cn",
    ): Flow<Resource<List<News>>>

    suspend fun searchNews(
        query: String,
        language: String = "zh",
        country: String = "cn",
    ): Flow<Resource<List<News>>>
}