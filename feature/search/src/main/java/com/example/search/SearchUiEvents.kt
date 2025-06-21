package com.example.search

sealed class SearchUiEvents {
    data class SearchNews(val query: String) : SearchUiEvents()
}