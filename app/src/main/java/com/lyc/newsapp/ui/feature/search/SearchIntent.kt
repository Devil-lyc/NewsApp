package com.lyc.newsapp.ui.feature.search

/**
 * Search screen intents. Add [QueryChanged] later for debounced suggestions without touching UI.
 */
sealed class SearchIntent {
    data class SubmitSearch(val query: String) : SearchIntent()
    object ClearError : SearchIntent()
}
