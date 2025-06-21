package com.example.search

import com.example.model.News

data class SearchUiState(
    val query: String = "",
    val history: List<String> = emptyList(),
    val searchResults: List<com.example.model.News> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)