package com.lyc.newsapp.ui.screens.home

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
class HomeViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        load()
    }
    
    private fun load() {
        loadAllNews()
//        loadNewsByCategory("technology")
//        loadNewsByCategory("business")
//        loadNewsByCategory("entertainment")
//        loadNewsByCategory("health")
//        loadNewsByCategory("science")
//        loadNewsByCategory("sports")
//        loadNewsByCategory("politics")
    }

    private fun loadAllNews(){
        viewModelScope.launch {
            newsRepository.getNewsList()
                .collect{ result ->
                    when(result){
                        is Resource.Success -> {
                           result.data?.let { newsList ->
                               _uiState.value = _uiState.value.copy(
                                   isLoading = false,
                                   newsList_all = newsList
                               )
                           }
                        }
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                        is Resource.Loading -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = true
                            )
                        }
                    }
                }
        }
    }
    private fun loadNewsByCategory(category: String){
        viewModelScope.launch {
            newsRepository.getNewsByCategory(category = category)
                .collect{ result ->
                    when(result){
                        is Resource.Success -> {
                            result.data?.let { newsList ->
                                when (category) {
                                    "technology" -> {
                                        _uiState.value = _uiState.value.copy(
                                            isLoading = false,
                                            newsList_technology = newsList
                                        )
                                    }
                                    "business" -> {
                                        _uiState.value = _uiState.value.copy(
                                            isLoading = false,
                                            newsList_business = newsList
                                        )
                                    }
                                    "entertainment" -> {
                                        _uiState.value = _uiState.value.copy(
                                            isLoading = false,
                                            newsList_entertainment = newsList
                                        )
                                    }
                                    "health" -> {
                                        _uiState.value = _uiState.value.copy(
                                            isLoading = false,
                                            newsList_health = newsList
                                        )
                                    }
                                    "science" -> {
                                        _uiState.value = _uiState.value.copy(
                                            isLoading = false,
                                            newsList_science = newsList
                                        )
                                    }
                                    "sports" -> {
                                        _uiState.value = _uiState.value.copy(
                                            isLoading = false,
                                            newsList_sports = newsList
                                        )
                                    }
                                    "politics" -> {
                                        _uiState.value = _uiState.value.copy(
                                            isLoading = false,
                                            newsList_politics = newsList
                                        )
                                    }
                                    else -> {}
                                }
                            }
                        }
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                        is Resource.Loading -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = true
                            )
                        }
                    }
                }
        }
    }
}