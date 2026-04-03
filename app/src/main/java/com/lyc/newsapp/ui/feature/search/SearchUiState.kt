package com.lyc.newsapp.ui.feature.search

import com.lyc.newsapp.domain.model.News

data class SearchUiState(
    val query: String = "",
    val history: List<String> = emptyList(),
    val searchResults: List<News> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)