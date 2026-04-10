package com.lyc.newsapp.ui.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyc.newsapp.domain.model.NewsCategories
import com.lyc.newsapp.domain.repository.NewsRepository
import com.lyc.newsapp.ui.mvi.MviHost
import com.lyc.newsapp.core.result.Resource
import com.lyc.newsapp.util.performance.StartupTracer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * HomeViewModel - 主页数据管理
 * 
 * 性能优化:
 * 1. 懒加载策略 - 只在需要时加载数据
 * 2. 预加载策略 - 提前加载下一页数据
 * 3. 缓存机制 - 避免重复请求
 * 4. 分批加载 - 减轻初始负担
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), MviHost<HomeUiState, HomeIntent> {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            isLoading = true,
            errorMessage = null,
            selectedCategoryId = restoreCategoryId(savedStateHandle)
        )
    )
    override val uiState = _uiState.asStateFlow()

    override fun dispatch(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.SelectCategory -> {
                val id = normalizeCategoryId(intent.categoryId)
                savedStateHandle[KEY_SELECTED_CATEGORY] = id
                _uiState.update { it.copy(selectedCategoryId = id) }
            }
            is HomeIntent.Refresh -> {
                _uiState.update { it.copy(isLoading = true) }
                refreshData(intent.category)
            }
            is HomeIntent.LoadNextPage -> onPageChange(intent.page, intent.category)
        }
    }

    // 是否已加载数据的标志
    private var isDataLoaded = false
    
    // 延迟加载各分类新闻的标志
    private var isCategoryDataLoaded = false
    
    // 是否正在加载数据的标志 - 防止重复请求
    private val loadingState = mutableMapOf(
        "all" to false,
        "technology" to false,
        "business" to false,
        "entertainment" to false,
        "health" to false,
        "science" to false,
        "sports" to false,
        "politics" to false
    )

    init {
        StartupTracer.startStage(StartupTracer.Stages.VIEWMODEL_INIT)
        Timber.d("HomeViewModel初始化")
        
        // 保持已恢复的 selectedCategoryId，仅刷新加载态
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        
        // 优化初始化 - 在后台线程准备ViewModel状态
        viewModelScope.launch(Dispatchers.Default) {
            // 准备初始化数据
            StartupTracer.markEvent("preparing_initial_state")
            
            // 仅在第一次创建时加载数据
            if (!isDataLoaded) {
                loadInitialData()
            }
        }
        
        StartupTracer.endStage(StartupTracer.Stages.VIEWMODEL_INIT)
    }

    /**
     * 初始数据加载 - 分批次进行，减少启动时的负担
     */
    private fun loadInitialData() {
        Timber.d("开始加载初始新闻数据")
        
        // 首次启动时只加载"全部"分类，其他分类延迟加载
        viewModelScope.launch(Dispatchers.Default) {
            // 第一阶段：加载全部新闻（主要数据）
            StartupTracer.markEvent("start_loading_main_news")
            loadAllNews()
            
            // 第二阶段：启动后1秒再开始加载其他分类（避免卡顿）
            viewModelScope.launch(Dispatchers.Default) {
                kotlinx.coroutines.delay(1000)
                if (!isCategoryDataLoaded) {
                    StartupTracer.markEvent("start_loading_categories")
                    
                    // 使用单独的协程以非阻塞方式加载每个分类
                    launch { loadNewsByCategory("technology") }
                    
                    // 延迟200ms加载下一批，避免同时发起太多请求
                    kotlinx.coroutines.delay(200)
                    launch { loadNewsByCategory("business") }
                    
                    kotlinx.coroutines.delay(200)
                    launch { loadNewsByCategory("entertainment") }
                    
                    kotlinx.coroutines.delay(200)
                    launch { loadNewsByCategory("health") }
                    
                    kotlinx.coroutines.delay(200)
                    launch { loadNewsByCategory("science") }
                    
                    kotlinx.coroutines.delay(200)
                    launch { loadNewsByCategory("sports") }
                    
                    kotlinx.coroutines.delay(200)
                    launch { loadNewsByCategory("politics") }
                    
                    isCategoryDataLoaded = true
                    StartupTracer.markEvent("all_categories_scheduled")
                }
            }
        }
    }

    private fun onPageChange(page: String, category: String) {
        // 如果该分类正在加载，不重复请求
        if (loadingState[category] == true) {
            return
        }
        
        // 标记为正在加载
        loadingState[category] = true
        
        viewModelScope.launch {
            newsRepository.getNextPage(category = category, nextPage = page)
                .flowOn(Dispatchers.IO) // 使用IO线程处理网络请求和数据处理
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { newsList ->
                                // 在主线程更新UI状态
                                withContext(Dispatchers.Main) {
                                    when (category) {
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
                                    
                                    // 加载完成后重置加载状态
                                    loadingState[category] = false
                                }
                            }
                        }
                        is Resource.Error -> {
                            withContext(Dispatchers.Main) {
                                _uiState.update {
                                    it.copy(
                                        isLoading = false, errorMessage = result.message
                                    )
                                }
                                // 错误时也需要重置加载状态
                                loadingState[category] = false
                            }
                        }
                        is Resource.Loading -> {
                            withContext(Dispatchers.Main) {
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
    }
    
    private fun refreshData(category: String) {
        // 刷新前清除错误状态
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                // 在后台线程执行数据处理
                val updatedList = when (category) {
                    "all" -> {
                        val shuffledMediaList = _uiState.value.newsList_all.toMutableList()
                        shuffledMediaList.shuffle()
                        shuffledMediaList.toList()
                    }
                    "technology" -> {
                        val shuffledMediaList =
                            _uiState.value.newsList_technology.toMutableList()
                        shuffledMediaList.shuffle()
                        shuffledMediaList.toList()
                    }
                    "business" -> {
                        val shuffledMediaList = _uiState.value.newsList_business.toMutableList()
                        shuffledMediaList.shuffle()
                        shuffledMediaList.toList()
                    }
                    "entertainment" -> {
                        val shuffledMediaList =
                            _uiState.value.newsList_entertainment.toMutableList()
                        shuffledMediaList.shuffle()
                        shuffledMediaList.toList()
                    }
                    "health" -> {
                        val shuffledMediaList = _uiState.value.newsList_health.toMutableList()
                        shuffledMediaList.shuffle()
                        shuffledMediaList.toList()
                    }
                    "science" -> {
                        val shuffledMediaList = _uiState.value.newsList_science.toMutableList()
                        shuffledMediaList.shuffle()
                        shuffledMediaList.toList()
                    }
                    "sports" -> {
                        val shuffledMediaList = _uiState.value.newsList_sports.toMutableList()
                        shuffledMediaList.shuffle()
                        shuffledMediaList.toList()
                    }
                    "politics" -> {
                        val shuffledMediaList = _uiState.value.newsList_politics.toMutableList()
                        shuffledMediaList.shuffle()
                        shuffledMediaList.toList()
                    }
                    else -> emptyList()
                }
                
                // 在主线程更新UI
                withContext(Dispatchers.Main) {
                    when (category) {
                        "all" -> _uiState.update {
                            it.copy(
                                isLoading = false, newsList_all = updatedList
                            )
                        }

                        "technology" -> _uiState.update {
                            it.copy(
                                isLoading = false, newsList_technology = updatedList
                            )
                        }

                        "business" -> _uiState.update {
                            it.copy(
                                isLoading = false, newsList_business = updatedList
                            )
                        }

                        "entertainment" -> _uiState.update {
                            it.copy(
                                isLoading = false, newsList_entertainment = updatedList
                            )
                        }

                        "health" -> _uiState.update {
                            it.copy(
                                isLoading = false, newsList_health = updatedList
                            )
                        }

                        "science" -> _uiState.update {
                            it.copy(
                                isLoading = false, newsList_science = updatedList
                            )
                        }

                        "sports" -> _uiState.update {
                            it.copy(
                                isLoading = false, newsList_sports = updatedList
                            )
                        }

                        "politics" -> _uiState.update {
                            it.copy(
                                isLoading = false, newsList_politics = updatedList
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadAllNews() {
        // 如果该分类正在加载，不重复请求
        if (loadingState["all"] == true) {
            return
        }
        
        // 标记为正在加载
        loadingState["all"] = true
        
        viewModelScope.launch {
            // 开始测量首次新闻加载时间
            StartupTracer.startStage(StartupTracer.Stages.FIRST_NEWS_LOAD)
            StartupTracer.startStage(StartupTracer.Stages.NETWORK_REQUEST)
            
            // 已经在init中设置了isLoading=true，这里不需要再设置
            
            newsRepository.getNewsList().flowOn(Dispatchers.IO) // 使用IO线程处理网络请求和数据处理
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                           StartupTracer.endStage(StartupTracer.Stages.NETWORK_REQUEST)
                           StartupTracer.startStage(StartupTracer.Stages.DATA_PROCESSING)
                           
                           result.data?.let { newsList ->
                               withContext(Dispatchers.Main) {
                                   // 只有当数据为空时才显示加载状态
                                   _uiState.update { 
                                       it.copy(
                                           isLoading = false, newsList_all = newsList
                                       )
                                   }
                               }
                               
                               // 标记数据已加载和加载完成
                               isDataLoaded = true
                               loadingState["all"] = false
                               
                               // 结束数据处理和首次加载计时
                               val dataProcessTime =
                                   StartupTracer.endStage(StartupTracer.Stages.DATA_PROCESSING)
                               val totalLoadTime =
                                   StartupTracer.endStage(StartupTracer.Stages.FIRST_NEWS_LOAD)
                               
                               Timber.d(
                                   "新闻数据加载完成 - 网络请求: ${
                                       StartupTracer.getStageDuration(
                                           StartupTracer.Stages.NETWORK_REQUEST
                                       )
                                   }ms, " + "数据处理: ${dataProcessTime}ms, 总耗时: ${totalLoadTime}ms"
                               )
                           }
                        }
                        is Resource.Error -> {
                            StartupTracer.markEvent("news_load_error")
                            withContext(Dispatchers.Main) {
                                _uiState.update {
                                    it.copy(
                                        isLoading = false, errorMessage = result.message
                                    )
                                }
                            }
                            loadingState["all"] = false
                        }
                        is Resource.Loading -> {
                            // 避免多次更新加载状态导致UI闪烁
                            // 不做任何操作，保持init中设置的加载状态
                        }
                    }
                }
        }
    }
    
    private fun loadNewsByCategory(category: String, isPreload: Boolean = false) {
        // 如果该分类正在加载，不重复请求
        if (loadingState[category] == true) {
            return
        }
        
        // 标记为正在加载
        loadingState[category] = true
        
        if (isPreload) {
            Timber.d("预加载分类: $category")
        }
        
        viewModelScope.launch {
            newsRepository.getNewsListByCategory(category = category)
                .flowOn(Dispatchers.IO) // 使用IO线程处理网络请求和数据处理
                .collect { result ->
                    withContext(Dispatchers.Main) { // 在主线程更新UI
                        when (result) {
                            is Resource.Success -> {
                                result.data?.let { newsList ->
                                    when (category) {
                                        "technology" -> {
                                            _uiState.update { 
                                                it.copy(
                                                    isLoading = false, newsList_technology = newsList
                                                )
                                            }
                                            StartupTracer.markEvent("${category}_loaded")
                                        }
                                        "business" -> {
                                            _uiState.update { 
                                                it.copy(
                                                    isLoading = false, newsList_business = newsList
                                                )
                                            }
                                            StartupTracer.markEvent("${category}_loaded")
                                        }
                                        "entertainment" -> {
                                            _uiState.update { 
                                                it.copy(
                                                    isLoading = false, newsList_entertainment = newsList
                                                )
                                            }
                                            StartupTracer.markEvent("${category}_loaded")
                                        }
                                        "health" -> {
                                            _uiState.update { 
                                                it.copy(
                                                    isLoading = false, newsList_health = newsList
                                                )
                                            }
                                            StartupTracer.markEvent("${category}_loaded")
                                        }
                                        "science" -> {
                                            _uiState.update { 
                                                it.copy(
                                                    isLoading = false, newsList_science = newsList
                                                )
                                            }
                                            StartupTracer.markEvent("${category}_loaded")
                                        }
                                        "sports" -> {
                                            _uiState.update { 
                                                it.copy(
                                                    isLoading = false, newsList_sports = newsList
                                                )
                                            }
                                            StartupTracer.markEvent("${category}_loaded")
                                        }
                                        "politics" -> {
                                            _uiState.update { 
                                                it.copy(
                                                    isLoading = false, newsList_politics = newsList
                                                )
                                            }
                                            StartupTracer.markEvent("${category}_loaded")
                                        }
                                        else -> {}
                                    }
                                    
                                    // 加载完成后重置加载状态
                                    loadingState[category] = false
                                }
                            }
                            is Resource.Error -> {
                                _uiState.update {
                                    it.copy(
                                        isLoading = false, errorMessage = result.message
                                    )
                                }
                                StartupTracer.markEvent("${category}_load_error")
                                loadingState[category] = false
                            }
                            is Resource.Loading -> {
                                // 预加载不更新UI加载状态，避免闪烁
                                if (!isPreload && !isDataLoaded) {
                                    _uiState.update { it.copy(isLoading = true) }
                                }
                            }
                        }
                    }
                }
        }
    }

    private companion object {
        private const val KEY_SELECTED_CATEGORY = "home_selected_category_id"

        private fun restoreCategoryId(savedStateHandle: SavedStateHandle): String {
            val raw = savedStateHandle.get<String>(KEY_SELECTED_CATEGORY)
            return normalizeCategoryId(raw)
        }

        private fun normalizeCategoryId(id: String?): String {
            if (id.isNullOrBlank()) return "all"
            val valid = NewsCategories.categories.any { it.id == id }
            return if (valid) id else "all"
        }
    }
}