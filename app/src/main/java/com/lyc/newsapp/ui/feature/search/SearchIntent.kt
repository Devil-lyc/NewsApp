package com.lyc.newsapp.ui.feature.search

/**
 * Search screen intents. Add [QueryChanged] later for debounced suggestions without touching UI.
 */
sealed class SearchIntent {
    data class QueryChanged(val query: String) : SearchIntent()
    data class SubmitSearch(val query: String) : SearchIntent()
    object ClearQuery : SearchIntent()
    object ClearError : SearchIntent()
}
