package com.example.network.model

data class NewsResponse(
    val nextPage: String? = null,
    val results: List<NewsDto>? = null,
    val status: String? = null,
    val totalResults: Int? = null
)

data class NewsDto(
    val ai_org: String? = null,
    val ai_region: String? = null,
    val ai_tag: String? = null,
    val article_id: String? = null,
    val category: List<String>? = null,
    val content: String? = null,
    val country: List<String>? = null,
    val creator: List<String>? = null,
    val description: String? = null,
    val duplicate: Boolean? = null,
    val image_url: String? = null,
    val keywords: List<String>? = null,
    val language: String? = null,
    val link: String? = null,
    val pubDate: String? = null,
    val pubDateTZ: String? = null,
    val sentiment: String? = null,
    val sentiment_stats: Any? = null,
    val source_icon: String? = null,
    val source_id: String? = null,
    val source_name: String? = null,
    val source_priority: Int ?= null,
    val source_url: String? = null,
    val title: String? = null,
    val video_url: String? = null
)

data class SentimentStats(
    val negative: Double? = null,
    val neutral: Double? = null,
    val positive: Double? = null
)