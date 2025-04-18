package com.lyc.newsapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyc.newsapp.data.repository.NewsRepository
import com.lyc.newsapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.isSensitiveHeader
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()
    
    // 是否已加载数据的标志
    private var isDataLoaded = false

    init {
        // 仅在第一次创建时加载数据
        if (!isDataLoaded) {
            load()
        }
    }

    fun onEvent(event: HomeUiEvents){
        when(event){
            is HomeUiEvents.onRefresh -> {
                _uiState.value = _uiState.value.copy(
                    isLoading = true
                )
                refreshData(event.category)
            }
            is HomeUiEvents.onPageChange-> {
                onPageChange(event.page, event.category)
            }
        }
    }
    /**
     * 手动刷新数据
     */

    
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

    private fun onPageChange(page: String, category: String){
        viewModelScope.launch {
            newsRepository.getNextPage(category = category, nextPage = page)
                .collect{ result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { newsList ->
                                when(category){
                                    "all" -> {
                                        _uiState.update {
                                            it.copy(
                                                isLoading = false,
                                                newsList_all = uiState.value.newsList_all + newsList
                                            )
                                        }
                                    }
                                    "technology" -> {
                                        _uiState.update {
                                            it.copy(
                                                isLoading = false,
                                                newsList_technology = uiState.value.newsList_technology + newsList
                                            )
                                        }
                                    }
                                    "business" -> {
                                        _uiState.update {
                                            it.copy(
                                                isLoading = false,
                                                newsList_business = uiState.value.newsList_business + newsList
                                            )
                                        }
                                    }
                                    "entertainment" -> {
                                        _uiState.update {
                                            it.copy(
                                                isLoading = false,
                                                newsList_entertainment = uiState.value.newsList_entertainment + newsList
                                            )
                                        }
                                    }
                                    "health" -> {
                                        _uiState.update {
                                            it.copy(
                                                isLoading = false,
                                                newsList_health = uiState.value.newsList_health + newsList
                                            )
                                        }
                                    }
                                    "science" -> {
                                        _uiState.update {
                                            it.copy(
                                                isLoading = false,
                                                newsList_science = uiState.value.newsList_science + newsList
                                            )
                                        }
                                    }
                                    "sports" -> {
                                        _uiState.update {
                                            it.copy(
                                                isLoading = false,
                                                newsList_sports = uiState.value.newsList_sports + newsList
                                            )
                                        }
                                    }
                                    "politics" -> {
                                        _uiState.update {
                                            it.copy(
                                                isLoading = false,
                                                newsList_politics = uiState.value.newsList_politics + newsList
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        is Resource.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = result.message
                                )
                           }
                        }
                        is Resource.Loading -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = it.isLoading
                                )
                            }
                        }
                    }

                }
        }

    }
    private fun refreshData(category: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true
            )
            when (category) {
                "all" -> {
                    val shuffledMediaList = _uiState.value.newsList_all.toMutableList()
                    shuffledMediaList.shuffle()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            newsList_all = shuffledMediaList.toList()
                        )
                    }
                }
                "technology" ->{
                    val shuffledMediaList = _uiState.value.newsList_technology.toMutableList()
                    shuffledMediaList.shuffle()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            newsList_technology = shuffledMediaList.toList()
                        )
                    }
                }
                "business" ->   {
                    val shuffledMediaList = _uiState.value.newsList_business.toMutableList()
                    shuffledMediaList.shuffle()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            newsList_business = shuffledMediaList.toList()
                        )
                    }
                }
                "entertainment" -> {
                    val shuffledMediaList = _uiState.value.newsList_entertainment.toMutableList()
                    shuffledMediaList.shuffle()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            newsList_entertainment = shuffledMediaList.toList()
                        )
                    }
                }
                "health" -> {
                    val shuffledMediaList = _uiState.value.newsList_health.toMutableList()
                    shuffledMediaList.shuffle()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            newsList_health = shuffledMediaList.toList()
                        )
                    }
                }
                "science" ->  {
                    val shuffledMediaList = _uiState.value.newsList_science.toMutableList()
                    shuffledMediaList.shuffle()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            newsList_science = shuffledMediaList.toList()
                        )
                    }
                }
                "sports" -> {
                    val shuffledMediaList = _uiState.value.newsList_sports.toMutableList()
                    shuffledMediaList.shuffle()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            newsList_sports = shuffledMediaList.toList()
                        )
                    }
                }
                "politics" -> {
                    val shuffledMediaList = _uiState.value.newsList_politics.toMutableList()
                    shuffledMediaList.shuffle()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            newsList_politics = shuffledMediaList.toList()
                        )
                    }
                }
            }
        }
    }

    private fun loadAllNews(){
        viewModelScope.launch {
            // 如果已经有数据，不显示加载状态，避免闪烁
            if (!isDataLoaded) {
                _uiState.value = _uiState.value.copy(isLoading = true)
            }
            
            newsRepository.getNewsList()
                .collect{ result ->
                    when(result){
                        is Resource.Success -> {
                           result.data?.let { newsList ->
                               _uiState.value = _uiState.value.copy(
                                   isLoading = false,
                                   newsList_all = newsList
                               )
                               // 标记数据已加载
                               isDataLoaded = true
                           }
                        }
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                        is Resource.Loading -> {
                            // 如果首次加载，才显示加载状态
                            if (!isDataLoaded) {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = true
                                )
                            }
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
                            // 如果首次加载，才显示加载状态
                            if (!isDataLoaded) {
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