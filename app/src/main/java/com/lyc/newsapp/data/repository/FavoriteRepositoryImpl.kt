package com.lyc.newsapp.data.repository

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.lyc.newsapp.data.model.FavoriteRequest
import com.lyc.newsapp.data.remote.FavoriteApiService
import com.lyc.newsapp.domain.model.Favorite
import com.lyc.newsapp.domain.repository.FavoriteRepository
import com.lyc.newsapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import javax.inject.Inject

/**
 * 收藏仓库实现
 */
class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteApiService: FavoriteApiService
) : FavoriteRepository {
    
    companion object {
        private const val TAG = "FavoriteRepositoryImpl"
    }
    
    /**
     * 解析错误响应体
     */
    private fun parseErrorBody(errorBody: ResponseBody?): String {
        return try {
            errorBody?.let {
                val errorJson = Gson().fromJson(it.string(), JsonObject::class.java)
                val message = errorJson.get("message")?.asString
                val errors = errorJson.getAsJsonArray("errors")
                
                if(!errors.isJsonNull && errors.size() > 0) {
                    val firstError = errors.get(0).asJsonObject
                    val errorMsg = firstError.get("msg")?.asString
                    val errorParam = firstError.get("param")?.asString
                    
                    if (errorMsg != null && errorParam != null) {
                        "$message: $errorParam - $errorMsg"
                    } else {
                        message ?: "未知错误"
                    }
                } else {
                    message ?: "未知错误"
                }
            } ?: "响应错误"
        } catch (e: Exception) {
//            Log.e(TAG, "解析错误响应失败: ${e.message}")
            "无法解析错误响应"
        }
    }
    
    /**
     * 获取收藏列表
     */
    override suspend fun getFavorites(
        category: String?,
        sourceName: String?,
        author: String?,
        page: Int,
        limit: Int,
        newsId: String?
    ): Flow<Resource<List<Favorite>>> = flow {
        emit(Resource.Loading())
        try {
            val response = favoriteApiService.getFavorites(
                category = category,
                sourceName = sourceName,
                author = author,
                page = page,
                limit = limit
            )
            
            if (response.isSuccessful && response.body() != null) {
                var favoritesList = response.body()!!.data.favorites.map { it.toDomainModel() }
                
                // 如果指定了newsId，则进行本地过滤
                if (newsId != null) {
                    favoritesList = favoritesList.filter { it.newsId == newsId }
                }
                
                emit(Resource.Success(favoritesList))
            } else {
                val errorMessage = parseErrorBody(response.errorBody())
//                Log.e(TAG, "获取收藏列表失败: $errorMessage")
                emit(Resource.Error(errorMessage))
            }
        } catch (e: Exception) {
//            Log.e(TAG, "获取收藏列表异常: ${e.message}")
            emit(Resource.Error( "服务器连接失败"))
        }
    }
    
    /**
     * 获取收藏详情
     */
    override suspend fun getFavoriteById(id: String): Flow<Resource<Favorite>> = flow {
        emit(Resource.Loading())
        try {
            val response = favoriteApiService.getFavoriteById(id)
            
            if (response.isSuccessful && response.body() != null) {
                val favorite = response.body()!!.data!!.favorite.toDomainModel()
                emit(Resource.Success(favorite))
            } else {
                val errorMessage = parseErrorBody(response.errorBody())
//                Log.e(TAG, "获取收藏详情失败: $errorMessage")
                emit(Resource.Error(errorMessage))
            }
        } catch (e: Exception) {
//            Log.e(TAG, "获取收藏详情异常: ${e.message}")
            emit(Resource.Error(e.localizedMessage ?: "发生未知错误"))
        }
    }
    
    /**
     * 添加收藏
     */
    override suspend fun addFavorite(
        title: String,
        url: String,
        description: String?,
        sourceName: String?,
        publishedAt: String?,
        category: String?,
        imageUrl: String?,
        author: String?,
        newsId: String?
    ): Flow<Resource<Favorite>> = flow {
        emit(Resource.Loading())
        try {
            // 记录请求参数日志
//            Log.d(TAG, "添加收藏请求: 标题=$title, URL=$url, 类别=$category")
            
            // 使用工厂方法创建带验证的请求对象
            val favoriteRequest = FavoriteRequest.create(
                title = title,
                url = url,
                description = description,
                sourceName = sourceName,
                publishedAt = publishedAt,
                category = category,
                imageUrl = imageUrl,
                author = author,
                newsId = newsId
            )
            
            // 记录处理后的数据
//            Log.d(TAG, "处理后的请求: 标题=${favoriteRequest.title}, URL=${favoriteRequest.url}")
            
            val response = favoriteApiService.addFavorite(favoriteRequest)
            
            if (response.isSuccessful && response.body() != null) {
                val favorite = response.body()!!.data!!.favorite.toDomainModel()
//                Log.d(TAG, "添加收藏成功: ${favorite.id}")
                emit(Resource.Success(favorite))
            } else {
                val errorMessage = parseErrorBody(response.errorBody())
//                Log.e(TAG, "添加收藏失败: $errorMessage")
                emit(Resource.Error(errorMessage))
            }
        } catch (e: Exception) {
//            Log.e(TAG, "添加收藏异常: ${e.message}")
            emit(Resource.Error(e.localizedMessage ?: "发生未知错误"))
        }
    }
    
    /**
     * 删除收藏
     */
    override suspend fun deleteFavorite(id: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
//            Log.d(TAG, "删除收藏ID: $id")
            val response = favoriteApiService.deleteFavorite(id)
            
            if (response.isSuccessful) {
//                Log.d(TAG, "删除收藏成功: $id")
                emit(Resource.Success(true))
            } else {
                val errorMessage = parseErrorBody(response.errorBody())
//                Log.e(TAG, "删除收藏失败: $errorMessage")
                emit(Resource.Error(errorMessage))
            }
        } catch (e: Exception) {
//            Log.e(TAG, "删除收藏异常: ${e.message}")
            emit(Resource.Error(e.localizedMessage ?: "发生未知错误"))
        }
    }
    
    /**
     * 检查新闻是否已收藏
     * 实现方式：尝试获取收藏列表中匹配newsId的项
     */
    override suspend fun isFavorite(newsId: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = favoriteApiService.getFavorites()
            
            if (response.isSuccessful && response.body() != null) {
                val favorites = response.body()!!.data.favorites
                val isFavorite = favorites.any { it.newsId == newsId }
                emit(Resource.Success(isFavorite))
            } else {
                val errorMessage = parseErrorBody(response.errorBody())
//                Log.e(TAG, "检查收藏状态失败: $errorMessage")
                emit(Resource.Error(errorMessage))
            }
        } catch (e: Exception) {
//            Log.e(TAG, "检查收藏状态异常: ${e.message}")
            emit(Resource.Error(e.localizedMessage ?: "发生未知错误"))
        }
    }
} 