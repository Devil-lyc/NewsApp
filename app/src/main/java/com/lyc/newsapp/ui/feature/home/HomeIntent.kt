package com.lyc.newsapp.ui.feature.home

/**
 * User intentions for the home feed (MVI).
 */
sealed class HomeIntent {
    data class SelectCategory(val categoryId: String) : HomeIntent()
    data class Refresh(val category: String) : HomeIntent()
    data class LoadNextPage(val page: String, val category: String) : HomeIntent()
}
