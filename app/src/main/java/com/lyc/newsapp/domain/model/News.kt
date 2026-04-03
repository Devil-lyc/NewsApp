package com.lyc.newsapp.domain.model

/**
 * 表示一篇新闻文章的数据模型
 */
data class News(
    val id: String,
    val title: String,
    val description: String,
    val content: String,
    val url: String,
    val imageUrl: String,
    val language: String,
    val category: String,
    val publishedAt: String,
    val author: String,
    val country: String,

    val nextPage:String,

    val source_name: String,
    val source_icon: String,
    val source_id: String,
    val source_url: String,

    val ai_org: String,
    val ai_region: String,
    val ai_tag: String,

    val duplicate: Boolean,
    val keywords: String,
    val pubDateTZ: String,
    val sentiment: String,
    val videoUrl: String,

    val isSaved: Boolean = false,
)