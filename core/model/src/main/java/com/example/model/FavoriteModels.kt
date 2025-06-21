package com.example.model

import android.webkit.URLUtil
import com.google.gson.annotations.SerializedName

/**
 * 收藏请求体
 * 
 * 确保必填字段都有值，且URL格式有效
 */
data class FavoriteRequest(
    val title: String,
    val url: String,
    val description: String? = null,
    @SerializedName("source_name") val sourceName: String? = null,
    val publishedAt: String? = null,
    val category: String? = null,
    val imageUrl: String? = null,
    val author: String? = null,
    val newsId: String? = null
) {
    companion object {
        /**
         * 工厂方法，创建有效的FavoriteRequest实例
         * 对输入数据进行验证和处理
         */
        fun create(
            title: String,
            url: String,
            description: String? = null,
            sourceName: String? = null,
            publishedAt: String? = null,
            category: String? = null,
            imageUrl: String? = null,
            author: String? = null,
            newsId: String? = null
        ): FavoriteRequest {
            return FavoriteRequest(
                title = title.take(100).ifBlank { "未命名文章" },
                url = if (URLUtil.isValidUrl(url)) url else "https://example.com/unavailable",
                description = description?.take(500),
                sourceName = sourceName?.ifBlank { null },
                publishedAt = publishedAt?.ifBlank { null },
                category = category?.ifBlank { null },
                imageUrl = if (imageUrl != null && URLUtil.isValidUrl(imageUrl)) imageUrl else null,
                author = author?.ifBlank { null },
                newsId = newsId?.ifBlank { null }
            )
        }
    }
}

/**
 * 收藏响应
 */
data class FavoriteResponse(
    val success: Boolean,
    val message: String? = null,
    val data: FavoriteData? = null
)

/**
 * 收藏数据
 */
data class FavoriteData(
    val favorite: FavoriteDto
)

/**
 * 收藏列表响应
 */
data class FavoritesListResponse(
    val success: Boolean,
    val data: FavoritesListData
)

/**
 * 收藏列表数据
 */
data class FavoritesListData(
    val favorites: List<FavoriteDto>,
    val pagination: PaginationInfo
)

/**
 * 分页信息
 */
data class PaginationInfo(
    val total: Int,
    val page: Int,
    val limit: Int,
    val pages: Int
)

/**
 * 收藏DTO
 */
data class FavoriteDto(
    @SerializedName("_id") val id: String,
    val user: String,
    val title: String,
    val description: String,
    val url: String,
    @SerializedName("source_name") val sourceName: String,
    val publishedAt: String,
    val category: String,
    val imageUrl: String,
    val author: String,
    val newsId: String,
    val createdAt: String,
    val updatedAt: String
) {
    /**
     * 转换为领域模型
     */
    fun toDomainModel(): Favorite {
        return Favorite(
            id = id,
            title = title,
            description = description,
            url = url,
            sourceName = sourceName,
            publishedAt = publishedAt,
            category = category,
            imageUrl = imageUrl,
            author = author,
            newsId = newsId,
            createdAt = createdAt
        )
    }
} 