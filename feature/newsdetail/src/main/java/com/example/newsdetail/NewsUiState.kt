package com.example.newsdetail

import com.example.model.News

data class NewsUiState(
    val news: com.example.model.News? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isBookmarked: Boolean = false
)