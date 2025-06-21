package com.example.data.repository

import com.example.common.util.Resource
import com.example.model.News
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
    ): Flow<Resource<com.example.model.News>>

    suspend fun getNewsByCategory(
        category: String,
        language: String = "zh",
        country: String = "cn",
    ): Flow<Resource<List<com.example.model.News>>>

    suspend fun getNextPage(
        category: String,
        nextPage: String,
        language: String = "zh",
        country: String = "cn",
    ): Flow<Resource<List<com.example.model.News>>>

    suspend fun searchNews(
        query: String,
        language: String = "zh",
        country: String = "cn",
    ): Flow<Resource<List<com.example.model.News>>>
}