package com.example.model

/**
 * 收藏的领域模型
 */
data class Favorite(
    val id: String,
    val title: String,
    val description: String,
    val url: String,
    val sourceName: String,
    val publishedAt: String,
    val category: String,
    val imageUrl: String,
    val author: String,
    val newsId: String,
    val createdAt: String
) 