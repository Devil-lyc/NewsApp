package com.lyc.newsapp.ui.screens.news

sealed class NewsDetailUiEvents{
    data class LoadNewsDetail(val newsId:String): NewsDetailUiEvents()
}