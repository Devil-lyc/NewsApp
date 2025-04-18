package com.lyc.newsapp.ui.screens.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyc.newsapp.domain.model.Favorite
import com.lyc.newsapp.domain.repository.FavoriteRepository
import com.lyc.newsapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * BookmarkScreen的ViewModel，负责管理收藏数据和状态
 */
@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {
    
    // UI状态
    private val _uiState = MutableStateFlow(BookmarkUiState())
    val uiState: StateFlow<BookmarkUiState> = _uiState.asStateFlow()
    
    // 是否已加载数据的标志
    private var isInitialized = false
    
    init {
        // 初始化时加载收藏列表
        getFavorites()
    }
    
    /**
     * 在页面聚焦或可见时调用，刷新收藏数据
     */
    fun onResume() {
        getFavorites()
    }
    
    /**
     * 获取收藏列表
     */
    fun getFavorites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = !isInitialized, error = null) }
            
            favoriteRepository.getFavorites().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.update { 
                            it.copy(
                                favorites = result.data ?: emptyList(),
                                isLoading = false
                            )
                        }
                        isInitialized = true
                    }
                    is Resource.Error -> {
                        _uiState.update { 
                            it.copy(
                                error = result.message,
                                isLoading = false
                            )
                        }
                    }
                    is Resource.Loading -> {
                        if (!isInitialized) {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 删除收藏
     */
    fun deleteFavorite(favoriteId: String) {
        viewModelScope.launch {
            favoriteRepository.deleteFavorite(favoriteId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        if (result.data == true) {
                            // 删除成功，更新列表
                            getFavorites()
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { 
                            it.copy(
                                error = result.message,
                                isLoading = false
                            )
                        }
                    }
                    is Resource.Loading -> {
                        // 正在删除中，可以选择显示加载状态
                    }
                }
            }
        }
    }
    
    /**
     * 清空所有收藏
     * 实现方式：逐个删除所有收藏
     */
    fun clearAllFavorites() {
        viewModelScope.launch {
            val favoriteIds = _uiState.value.favorites.map { it.id }
            
            for (id in favoriteIds) {
                favoriteRepository.deleteFavorite(id).collect {}
            }
            
            // 清空后刷新列表
            getFavorites()
        }
    }
}

/**
 * BookmarkScreen的UI状态
 */
data class BookmarkUiState(
    val favorites: List<Favorite> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) 