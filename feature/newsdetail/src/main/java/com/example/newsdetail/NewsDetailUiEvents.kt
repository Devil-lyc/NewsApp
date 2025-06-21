package com.example.newsdetail

/**
 * 新闻详情页事件
 */
sealed class NewsDetailUiEvents{
    /**
     * 加载新闻详情
     */
    data class LoadNewsDetail(val newsId: String): NewsDetailUiEvents()
    
    /**
     * 切换收藏状态
     */
    object ToggleBookmark: NewsDetailUiEvents()
    
    /**
     * 检查收藏状态
     */
    data class CheckBookmarkStatus(val newsId: String): NewsDetailUiEvents()
    
    /**
     * 设置初始收藏状态
     */
    data class SetInitialBookmarkState(val isBookmarked: Boolean): NewsDetailUiEvents()
}