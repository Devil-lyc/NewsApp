package com.lyc.newsapp.ui.screens.news

import com.lyc.newsapp.domain.model.News

data class NewsUiState(
    val news: News? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isBookmarked: Boolean = false
)