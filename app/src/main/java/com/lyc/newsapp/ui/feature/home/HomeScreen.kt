package com.lyc.newsapp.ui.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lyc.newsapp.domain.model.NewsCategories
import com.lyc.newsapp.ui.components.CategoryTabs
import com.lyc.newsapp.ui.components.NewsCard
import com.lyc.newsapp.ui.components.NewsListSkeleton
import com.lyc.newsapp.util.performance.StartupTracer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * 首页界面，显示新闻文章和分类
 * 性能优化点：
 * 1. 使用remember缓存和派生状态
 * 2. 使用LazyListState实现列表状态保存
 * 3. 延迟加载和按需加载
 * 4. 减少重组频率
 * 5. 使用骨架屏提前显示UI结构
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onArticleClick: (newsId:String) -> Unit
) {
    // 记录性能跟踪点
    StartupTracer.markEvent("home_screen_compose_start")

    // ViewModel和状态 - 延迟初始化以减少UI阻塞
    val coroutineScope = rememberCoroutineScope()

    // 使用LaunchedEffect处理初始状态设置，减少Composable函数体中的复杂计算
    var selectedCategory by remember { mutableStateOf(NewsCategories.categories.first()) }

    // 为每个分类创建独立的滚动状态 - 在主Composable外预先创建，提高渲染速度
    val allListState = rememberLazyListState()
    val technologyListState = rememberLazyListState()
    val businessListState = rememberLazyListState()
    val entertainmentListState = rememberLazyListState()
    val healthListState = rememberLazyListState()
    val scienceListState = rememberLazyListState()
    val sportsListState = rememberLazyListState()
    val politicsListState = rememberLazyListState()

    // 最小化滚动状态 - 预先创建所有需要的状态
    val scrollStates = remember {
        mapOf(
            "all" to allListState,
            "technology" to technologyListState,
            "business" to businessListState,
            "entertainment" to entertainmentListState,
            "health" to healthListState,
            "science" to scienceListState,
            "sports" to sportsListState,
            "politics" to politicsListState
        )
    }

    // 获取当前分类的滚动状态
    val currentScrollState = remember(selectedCategory.id) {
        scrollStates[selectedCategory.id] ?: allListState
    }

    // 使用延迟收集状态，减少初始渲染压力
    val homeUiState by homeViewModel.uiState.collectAsState()

    // 直接根据 selectedCategory 从 homeUiState 获取新闻列表
    val newsList = remember(selectedCategory.id, homeUiState) {
        when (selectedCategory.id) {
            "all" -> homeUiState.newsList_all
            "technology" -> homeUiState.newsList_technology
            "business" -> homeUiState.newsList_business
            "entertainment" -> homeUiState.newsList_entertainment
            "health" -> homeUiState.newsList_health
            "science" -> homeUiState.newsList_science
            "sports" -> homeUiState.newsList_sports
            "politics" -> homeUiState.newsList_politics
            else -> homeUiState.newsList_all
        }
    }

    // 刷新相关状态
    var isRefreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = {
        coroutineScope.launch {
            isRefreshing = true
            delay(1000)
            homeViewModel.dispatch(HomeIntent.Refresh(category = selectedCategory.id))
            isRefreshing = false
        }
    })

    // 底部加载逻辑 - 使用LaunchedEffect处理，避免UI线程计算
    val isAtBottom by remember(currentScrollState, newsList) {
        derivedStateOf {
            if (newsList.isEmpty()) return@derivedStateOf false

            val lastVisibleItem = currentScrollState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= newsList.size - 3
        }
    }

    LaunchedEffect(isAtBottom, newsList) {
        if (isAtBottom && newsList.isNotEmpty() && !homeUiState.isLoading) {
            val lastNews = newsList.lastOrNull()
            lastNews?.let {
                homeViewModel.dispatch(
                    HomeIntent.LoadNextPage(page = it.nextPage, category = selectedCategory.id)
                )
            }
        }
    }

    // 渲染完成标记 - 一次性执行
    LaunchedEffect(Unit) {
        // 小延迟确保UI完全渲染
        delay(100)
        StartupTracer.markEvent("home_screen_fully_rendered")
        Timber.d("HomeScreen完全渲染")
    }

    // UI结构
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 分类选项卡 - 尽早渲染，提供用户界面响应
            CategoryTabs(categories = NewsCategories.categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it })

            // 新闻列表区域
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState)
            ) {
                // 优先显示错误状态而不是加载状态
                if (homeUiState.errorMessage != null) {
                    // 显示错误UI
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "错误",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "加载失败",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = homeUiState.errorMessage ?: "未知错误",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(onClick = { homeViewModel.dispatch(HomeIntent.Refresh(category = selectedCategory.id)) }) {
                                Icon(
                                    imageVector = Icons.Default.Refresh, contentDescription = "重试"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "重试")
                            }
                        }
                    }
                } else if (homeUiState.isLoading && newsList.isEmpty()) {
                    // 加载中且没有数据时显示骨架屏
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NewsListSkeleton(
                            itemCount = 5, modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    // 有数据或非首次加载时显示实际内容
                    LazyColumn(
                        state = currentScrollState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        when {
                            // 有新闻文章
                            newsList.isNotEmpty() -> {
                                // 全部分类显示标题
                                if (selectedCategory.id == "all") {
                                    item {
                                        Text(
                                            text = "为你推荐",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 20.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }

                                // 使用key参数确保Compose可以正确跟踪列表项的身份
                                items(items = newsList, key = { it.id } // 使用唯一ID作为key
                                ) { news ->
                                    NewsCard(
                                        news = news,
                                        onArticleClick = onArticleClick,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }

                                // 如果是底部加载更多，添加一个底部加载指示器
                                if (homeUiState.isLoading && newsList.isNotEmpty()) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(30.dp), strokeWidth = 2.dp
                                            )
                                        }
                                    }
                                }
                            }

                            // 空状态(既不是loading也没有error，但列表为空)
                            else -> {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(600.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier.padding(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Info,
                                                contentDescription = "无内容",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text(
                                                text = "暂无${selectedCategory.name}新闻",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "请尝试切换其他分类或稍后再试",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(24.dp))
                                            OutlinedButton(onClick = {
                                                homeViewModel.dispatch(
                                                    HomeIntent.Refresh(category = selectedCategory.id)
                                                )
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Refresh,
                                                    contentDescription = "刷新"
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(text = "刷新")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 下拉刷新指示器
                PullRefreshIndicator(
                    refreshing = isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    // 记录组件完成的性能跟踪点
    StartupTracer.markEvent("home_screen_composed")
}