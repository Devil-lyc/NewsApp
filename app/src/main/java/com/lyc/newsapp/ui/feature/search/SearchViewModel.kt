package com.lyc.newsapp.ui.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyc.newsapp.core.result.Resource
import com.lyc.newsapp.domain.repository.NewsRepository
import com.lyc.newsapp.ui.mvi.MviHost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel(), MviHost<SearchUiState, SearchIntent> {

    private val _uiState = MutableStateFlow(SearchUiState())
    override val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    override fun dispatch(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.SubmitSearch -> searchNews(intent.query)
            is SearchIntent.ClearError -> _uiState.value = _uiState.value.copy(error = null)
        }
    }

    private fun searchNews(query: String) {
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
                                _uiState.value = _uiState.value.copy(isLoading = true)
                            }
                        }
                    }
                }
        }
    }
}
