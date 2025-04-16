package com.lyc.newsapp.ui.screens.news

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
class NewsDetailViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel(){
    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: NewsDetailUiEvents){
        when(event){
            is NewsDetailUiEvents.LoadNewsDetail -> {
                loadNewsDetail(event.newsId)
            }
        }
    }
    private fun loadNewsDetail(newsId: String){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
            )
            newsRepository.getNewsById(id = newsId)
                .collect{ result ->
                    when(result){
                        is Resource.Success -> {
                            result.data?.let {
                                _uiState.value = _uiState.value.copy(
                                    news = it,
                                    isLoading = false,
                                )
                            }
                        }
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                error = result.message,
                                isLoading = false,
                            )
                        }
                        is Resource.Loading -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = result.isLoading,
                            )
                        }
                    }
                }
        }
    }
}