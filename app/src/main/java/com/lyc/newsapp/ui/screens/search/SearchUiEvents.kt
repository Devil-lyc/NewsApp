package com.lyc.newsapp.ui.screens.search

sealed class SearchUiEvents {
    data class SearchNews(val query: String) : SearchUiEvents()
}