package com.example.data


import com.example.model.News
import com.example.network.model.NewsDto

fun NewsDto.toNews(nextPage: String): com.example.model.News {
    return com.example.model.News(
        id = article_id ?: "",
        title = title ?: "",
        description = description ?: "",
        content = content ?: "",
        url = link ?: "",
        imageUrl = image_url ?: "",
        language = language ?: "",
        category = category?.get(0) ?: "",
        publishedAt = pubDate ?: "",
        author = creator?.get(0) ?: "",
        country = country?.get(0) ?: "",
        source_name = source_name ?: "",
        source_icon = source_icon ?: "",
        source_id = source_id ?: "",
        source_url = source_url ?: "",
        ai_org = ai_org ?: "",
        ai_region = ai_region ?: "",
        ai_tag = ai_tag ?: "",
        duplicate = duplicate ?: false,
        keywords = keywords?.get(0) ?: "",
        pubDateTZ = pubDateTZ ?: "",
        sentiment = sentiment ?: "",
        videoUrl = video_url ?: "",
        nextPage = nextPage
    )
}