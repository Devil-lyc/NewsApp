package com.lyc.newsapp.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyc.newsapp.data.repository.NewsRepository
import com.lyc.newsapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel(){
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: SearchUiEvents) {
        when (event) {
            is SearchUiEvents.SearchNews -> {
                searchNews(event.query)
            }
         }
    }

    private fun searchNews(query: String) {
        // 重置搜索状态
        _uiState.value = SearchUiState(
            query = query,
            isLoading = true,
            error = null,
            searchResults = emptyList()
        )
        
        viewModelScope.launch {
            newsRepository.searchNews(query)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _uiState.value = _uiState.value.copy(
                                searchResults = result.data ?: emptyList(),
                                isLoading = false,
                                error = null
                            )
                        }
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                error = result.message,
                                isLoading = false
                            )
                        }
                        is Resource.Loading -> {
                            if (result.isLoading) {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = true
                                )
                            }
                        }
                    }
                }
        }
    }
}