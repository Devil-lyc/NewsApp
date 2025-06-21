package com.example.data.repository

import kotlinx.coroutines.flow.Flow

/**
 * 收藏仓库接口
 */
interface FavoriteRepository {
    
    /**
     * 获取收藏列表
     * 
     * @param category 可选，按分类筛选
     * @param sourceName 可选，按来源名称筛选
     * @param author 可选，按作者筛选
     * @param page 页码，默认1
     * @param limit 每页数量，默认10
     * @param newsId 可选，按新闻ID筛选
     * @return 包含收藏列表的Flow
     */
    suspend fun getFavorites(
        category: String? = null,
        sourceName: String? = null,
        author: String? = null,
        page: Int = 1,
        limit: Int = 10,
        newsId: String? = null
    ): Flow<com.example.common.util.Resource<List<com.example.model.Favorite>>>
    
    /**
     * 获取收藏详情
     * 
     * @param id 收藏ID
     * @return 包含收藏详情的Flow
     */
    suspend fun getFavoriteById(id: String): Flow<com.example.common.util.Resource<com.example.model.Favorite>>
    
    /**
     * 添加收藏
     * 
     * @param title 标题
     * @param url URL
     * @param description 描述
     * @param sourceName 来源名称
     * @param publishedAt 发布时间
     * @param category 分类
     * @param imageUrl 图片URL
     * @param author 作者
     * @param newsId 新闻ID
     * @return 包含添加结果的Flow
     */
    suspend fun addFavorite(
        title: String,
        url: String,
        description: String? = null,
        sourceName: String? = null,
        publishedAt: String? = null,
        category: String? = null,
        imageUrl: String? = null,
        author: String? = null,
        newsId: String? = null
    ): Flow<com.example.common.util.Resource<com.example.model.Favorite>>
    
    /**
     * 删除收藏
     * 
     * @param id 收藏ID
     * @return 包含删除结果的Flow
     */
    suspend fun deleteFavorite(id: String): Flow<com.example.common.util.Resource<Boolean>>
    
    /**
     * 检查新闻是否已收藏
     * 
     * @param newsId 新闻ID
     * @return 包含检查结果的Flow
     */
    suspend fun isFavorite(newsId: String): Flow<com.example.common.util.Resource<Boolean>>
} 