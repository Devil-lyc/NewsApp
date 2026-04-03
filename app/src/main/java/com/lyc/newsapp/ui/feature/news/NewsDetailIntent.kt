package com.lyc.newsapp.ui.feature.news

/**
 * 新闻详情用户意图（MVI）。
 */
sealed class NewsDetailIntent {
    data class LoadNewsDetail(val newsId: String) : NewsDetailIntent()
    object ToggleBookmark : NewsDetailIntent()
    data class CheckBookmarkStatus(val newsId: String) : NewsDetailIntent()
    data class SetInitialBookmarkState(val isBookmarked: Boolean) : NewsDetailIntent()
}
