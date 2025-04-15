package com.lyc.newsapp.ui.screens.home

import com.lyc.newsapp.domain.model.News

data class HomeUiState(
    val newsList_all: List<News> = emptyList(),
    val newsList_technology: List<News> = emptyList(),
    val newsList_business: List<News> = emptyList(),
    val newsList_entertainment: List<News> = emptyList(),
    val newsList_health: List<News> = emptyList(),
    val newsList_science: List<News> = emptyList(),
    val newsList_sports: List<News> = emptyList(),
    val newsList_politics: List<News> = emptyList(),

    val isLoading: Boolean = false,
    val errorMessage: String? = null
)