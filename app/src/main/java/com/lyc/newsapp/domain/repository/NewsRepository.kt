package com.lyc.newsapp.domain.repository

import com.lyc.newsapp.core.result.Resource
import com.lyc.newsapp.domain.model.News
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

    suspend fun getNewsListByCategory(
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
