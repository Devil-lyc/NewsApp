package com.example.newsdetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.NewsRepository
import com.example.data.repository.FavoriteRepository
import com.example.common.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NewsDetailViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel(){
    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState = _uiState.asStateFlow()

    companion object {
        private const val TAG = "NewsDetailViewModel"
    }

    fun onEvent(event: NewsDetailUiEvents){
        when(event){
            is NewsDetailUiEvents.LoadNewsDetail -> {
                loadNewsDetail(event.newsId)
            }
            is NewsDetailUiEvents.ToggleBookmark -> {
                toggleBookmark()
            }
            is NewsDetailUiEvents.CheckBookmarkStatus -> {
                checkBookmarkStatus(event.newsId)
            }
            is NewsDetailUiEvents.SetInitialBookmarkState -> {
                _uiState.value = _uiState.value.copy(
                    isBookmarked = event.isBookmarked
                )
            }
        }
    }
    
    /**
     * 加载新闻详情
     */
    private fun loadNewsDetail(newsId: String){
        viewModelScope.launch {
            // 先检查收藏状态，避免按钮闪烁
            checkBookmarkStatusSync(newsId)
            
            _uiState.value = _uiState.value.copy(
                isLoading = true,
            )
            newsRepository.getNewsById(id = newsId)
                .collect{ result ->
                    when(result){
                        is com.example.common.util.Resource.Success -> {
                            result.data?.let {
                                _uiState.value = _uiState.value.copy(
                                    news = it,
                                    isLoading = false,
                                )
                                // 加载成功后再次检查收藏状态，确保数据一致性
                                checkBookmarkStatus(newsId)
                            }
                        }
                        is com.example.common.util.Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                error = result.message,
                                isLoading = false,
                            )
                        }
                        is com.example.common.util.Resource.Loading -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = result.isLoading,
                            )
                        }
                    }
                }
        }
    }
    
    /**
     * 同步检查新闻是否已收藏（在加载新闻前先执行）
     */
    private suspend fun checkBookmarkStatusSync(newsId: String) {
        try {
            val result = favoriteRepository.isFavorite(newsId).first()
            if (result is com.example.common.util.Resource.Success) {
                _uiState.value = _uiState.value.copy(
                    isBookmarked = result.data ?: false
                )
            }
        } catch (e: Exception) {
            // 如果出错，保持默认状态
            Log.e(TAG, "检查收藏状态失败: ${e.message}")
        }
    }
    
    /**
     * 检查新闻是否已收藏
     */
    private fun checkBookmarkStatus(newsId: String) {
        viewModelScope.launch {
            favoriteRepository.isFavorite(newsId).collect { result ->
                when (result) {
                    is com.example.common.util.Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isBookmarked = result.data ?: false
                        )
                    }
                    is com.example.common.util.Resource.Error -> {
                        Log.e(TAG, "检查收藏状态错误: ${result.message}")
                    }
                    else -> {}
                }
            }
        }
    }
    
    /**
     * 切换收藏状态
     */
    private fun toggleBookmark() {
        viewModelScope.launch {
            val news = _uiState.value.news ?: return@launch
            val isBookmarked = _uiState.value.isBookmarked
            
            if (isBookmarked) {
                // 已收藏，需要取消收藏
                // 查找对应的收藏记录
                favoriteRepository.getFavorites(newsId = news.id).collect { result ->
                    if (result is com.example.common.util.Resource.Success && result.data?.isNotEmpty() == true) {
                        // 找到收藏记录，删除它
                        val favoriteId = result.data!!.first().id
                        favoriteRepository.deleteFavorite(favoriteId).collect { deleteResult ->
                            when (deleteResult) {
                                is com.example.common.util.Resource.Success -> {
                                    if (deleteResult.data == true) {
                                        // 删除成功，更新UI状态
                                        _uiState.value = _uiState.value.copy(
                                            isBookmarked = false
                                        )
                                        Log.d(TAG, "取消收藏成功: ${news.id}")
                                    }
                                }
                                is com.example.common.util.Resource.Error -> {
                                    Log.e(TAG, "取消收藏失败: ${deleteResult.message}")
                                }
                                else -> {}
                            }
                        }
                    } else if (result is com.example.common.util.Resource.Error) {
                        Log.e(TAG, "获取收藏信息失败: ${result.message}")
                    }
                }
            } else {
                // 准备收藏数据，确保必填字段不为空
                val title = news.title.takeIf { it.isNotBlank() } ?: "无标题文章"
                val url = news.url.takeIf { it.isNotBlank() } ?: "https://example.com/no-url"
                
                // 其他字段，如果为空则填入合理的默认值
                val description = news.description.takeIf { it.isNotBlank() } ?: "没有描述"
                val sourceName = news.source_name.takeIf { it.isNotBlank() } ?: "未知来源"
                val publishedAt = news.publishedAt.takeIf { it.isNotBlank() } ?: ""
                val category = news.category.takeIf { it.isNotBlank() } ?: "未分类"
                val imageUrl = news.imageUrl
                val author = news.author.takeIf { it.isNotBlank() } ?: "未知作者"
                val newsId = news.id
                
                Log.d(TAG, "开始添加收藏: 标题='$title', URL='$url'")
                
                // 未收藏，添加收藏
                favoriteRepository.addFavorite(
                    title = title,
                    url = url,
                    description = description,
                    sourceName = sourceName,
                    publishedAt = publishedAt,
                    category = category,
                    imageUrl = imageUrl,
                    author = author,
                    newsId = newsId
                ).collect { result ->
                    when (result) {
                        is com.example.common.util.Resource.Success -> {
                            // 添加收藏成功，更新UI状态
                            _uiState.value = _uiState.value.copy(
                                isBookmarked = true
                            )
                            Log.d(TAG, "添加收藏成功: ${result.data?.id}")
                        }
                        is com.example.common.util.Resource.Error -> {
                            Log.e(TAG, "添加收藏失败: ${result.message}")
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}