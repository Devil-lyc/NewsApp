package com.example.home

import com.example.model.News

data class HomeUiState(
    val newsList_all: List<com.example.model.News> = emptyList(),
    val newsList_technology: List<com.example.model.News> = emptyList(),
    val newsList_business: List<com.example.model.News> = emptyList(),
    val newsList_entertainment: List<com.example.model.News> = emptyList(),
    val newsList_health: List<com.example.model.News> = emptyList(),
    val newsList_science: List<com.example.model.News> = emptyList(),
    val newsList_sports: List<com.example.model.News> = emptyList(),
    val newsList_politics: List<com.example.model.News> = emptyList(),

    val isLoading: Boolean = false,
    val errorMessage: String? = null
)