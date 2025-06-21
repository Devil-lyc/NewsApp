package com.example.network

import com.example.model.FavoriteRequest
import com.example.model.FavoriteResponse
import com.example.model.FavoritesListResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * 收藏相关API接口
 * 基于 https://kkzynytfzajt.sealoshzh.site/ API
 */
interface FavoriteApiService {
    
    /**
     * 添加收藏
     * 
     * @param favoriteRequest 包含收藏信息的请求体
     * @return 返回添加成功的收藏信息
     */
    @POST("api/favorites")
    suspend fun addFavorite(
        @Body favoriteRequest: FavoriteRequest
    ): Response<FavoriteResponse>
    
    /**
     * 获取收藏列表
     * 
     * @param category 可选，按分类筛选
     * @param sourceName 可选，按来源名称筛选
     * @param author 可选，按作者筛选
     * @param page 页码，默认1
     * @param limit 每页数量，默认10
     * @return 返回收藏列表及分页信息
     */
    @GET("api/favorites")
    suspend fun getFavorites(
        @Query("category") category: String? = null,
        @Query("source_name") sourceName: String? = null,
        @Query("author") author: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<FavoritesListResponse>
    
    /**
     * 获取单个收藏详情
     * 
     * @param id 收藏ID
     * @return 返回收藏详情
     */
    @GET("api/favorites/{id}")
    suspend fun getFavoriteById(
        @Path("id") id: String
    ): Response<FavoriteResponse>
    
    /**
     * 更新收藏
     * 
     * @param id 收藏ID
     * @param favoriteRequest 包含更新信息的请求体
     * @return 返回更新后的收藏信息
     */
    @PUT("api/favorites/{id}")
    suspend fun updateFavorite(
        @Path("id") id: String,
        @Body favoriteRequest: FavoriteRequest
    ): Response<FavoriteResponse>
    
    /**
     * 删除收藏
     * 
     * @param id 收藏ID
     * @return 返回删除成功的消息
     */
    @DELETE("api/favorites/{id}")
    suspend fun deleteFavorite(
        @Path("id") id: String
    ): Response<Map<String, Any>>
} 