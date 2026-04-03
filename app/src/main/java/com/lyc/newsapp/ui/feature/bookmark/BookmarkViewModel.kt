package com.lyc.newsapp.ui.feature.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyc.newsapp.core.result.Resource
import com.lyc.newsapp.domain.model.Favorite
import com.lyc.newsapp.domain.repository.FavoriteRepository
import com.lyc.newsapp.ui.mvi.MviHost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel(), MviHost<BookmarkUiState, BookmarkIntent> {

    private val _uiState = MutableStateFlow(BookmarkUiState())
    override val uiState: StateFlow<BookmarkUiState> = _uiState.asStateFlow()

    private var isInitialized = false

    init {
        dispatch(BookmarkIntent.Refresh)
    }

    override fun dispatch(intent: BookmarkIntent) {
        when (intent) {
            is BookmarkIntent.Refresh,
            is BookmarkIntent.ScreenBecameVisible -> loadFavorites()
            is BookmarkIntent.DeleteFavorite -> deleteFavorite(intent.id)
            is BookmarkIntent.ClearAll -> clearAllFavorites()
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = !isInitialized, error = null) }

            favoriteRepository.getFavorites()
                .collect { result ->
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

    private fun deleteFavorite(favoriteId: String) {
        viewModelScope.launch {
            favoriteRepository.deleteFavorite(favoriteId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        if (result.data == true) {
                            loadFavorites()
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
                    is Resource.Loading -> Unit
                }
            }
        }
    }

    private fun clearAllFavorites() {
        viewModelScope.launch {
            val favoriteIds = _uiState.value.favorites.map { it.id }

            for (id in favoriteIds) {
                favoriteRepository.deleteFavorite(id).collect { }
            }

            loadFavorites()
        }
    }
}

data class BookmarkUiState(
    val favorites: List<Favorite> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
