package com.lyc.newsapp.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lyc.newsapp.domain.model.News
import com.lyc.newsapp.domain.model.NewsCategories
import com.lyc.newsapp.ui.components.NewsCard
import com.lyc.newsapp.ui.components.CategoryTabs
import com.lyc.newsapp.ui.components.HeadlineCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 首页界面，显示新闻文章和分类
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    onArticleClick: (newsId: String) -> Unit
) {
    // 状态

    val homeViewModel = hiltViewModel<HomeViewModel>()
    val onEvent = homeViewModel::onEvent
    val homeUiState = homeViewModel.uiState.collectAsState()
    var selectedCategory by remember { mutableStateOf(NewsCategories.categories.first()) }
    val coroutineScope = rememberCoroutineScope()

    val newsList = when(selectedCategory.id){
        "all" -> homeUiState.value.newsList_all
        "technology" -> homeUiState.value.newsList_technology
        "business" -> homeUiState.value.newsList_business
        "entertainment" -> homeUiState.value.newsList_entertainment
        "health" -> homeUiState.value.newsList_health
        "science" -> homeUiState.value.newsList_science
        "sports" -> homeUiState.value.newsList_sports
        "politics" -> homeUiState.value.newsList_politics
        else -> homeUiState.value.newsList_all
    }
    
    // 使用完全独立的刷新状态
    var isRefreshing by remember { mutableStateOf(false) }
    
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { 
            coroutineScope.launch {
                isRefreshing = true
                delay(1000)
                onEvent(HomeUiEvents.onRefresh(category = selectedCategory.id))
                // 确保刷新指示器显示足够时间，无论数据加载多快
                isRefreshing = false
            }
        }
    )
    
    Scaffold(
        
    ) { paddingValues ->
        // 分类选项卡和新闻列表
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 分类选项卡
            CategoryTabs(
                categories = NewsCategories.categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )
            
            // 新闻列表（带下拉刷新）
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when {
                        // 有新闻文章
                        newsList.isNotEmpty() -> {
                            // 全部分类显示头条
                            if (selectedCategory.id == "all") {
                                // 头条新闻
//                                item {
//                                    // 只有在列表非空时显示头条
//                                    if (newsList.isNotEmpty()) {
//                                        val headline = newsList.first()
//
//                                        Text(
//                                            text = "头条新闻",
//                                            style = MaterialTheme.typography.titleLarge.copy(
//                                                fontWeight = FontWeight.Bold
//                                            ),
//                                            modifier = Modifier.padding(horizontal = 20.dp)
//                                        )
//
//                                        Spacer(modifier = Modifier.height(8.dp))
//
//                                        HeadlineCard(
//                                            news = headline,
//                                            onArticleClick = onArticleClick,
//                                            modifier = Modifier.padding(horizontal = 16.dp)
//                                        )
//
//                                        Spacer(modifier = Modifier.height(24.dp))
//                                    }
//                                }
                                
                                // 其余推荐新闻
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
                                items(newsList) { news ->
                                    NewsCard(
                                        news = news,
                                        onArticleClick = onArticleClick,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                    if(newsList.indexOf(news) == newsList.lastIndex){
                                        onEvent(HomeUiEvents.onPageChange(category = selectedCategory.id, page = news.nextPage))
                                    }
                                }
                            }
                            else {
                                // 其他分类只显示普通列表
                                items(newsList) { news ->
                                    NewsCard(
                                        news = news,
                                        onArticleClick = onArticleClick,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                    if(newsList.indexOf(news) == newsList.lastIndex && !homeUiState.value.isLoading){
                                        onEvent(HomeUiEvents.onPageChange(category = selectedCategory.id, page = news.nextPage))
                                    }
                                }
                            }
                        }
                        
                        // 加载中状态
                        homeUiState.value.isLoading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(600.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator()
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "正在加载${selectedCategory.name}分类的新闻...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                        
                        // 错误状态
                        homeUiState.value.errorMessage != null -> {
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
                                            text = homeUiState.value.errorMessage ?: "未知错误",
                                            style = MaterialTheme.typography.bodyMedium,
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(24.dp))
                                        Button(
                                            onClick = { onEvent(HomeUiEvents.onRefresh(category = selectedCategory.id)) }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "重试"
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = "重试")
                                        }
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
                                        OutlinedButton(
                                            onClick = {  onEvent(HomeUiEvents.onRefresh(category = selectedCategory.id)) }
                                        ) {
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
}

// 创建一个扩展函数来处理刷新功能
// 由于无法直接访问HomeViewModel的私有方法，这里通过反射来调用
private fun refreshData(viewModel: HomeViewModel) {
    try {
        val method = HomeViewModel::class.java.getDeclaredMethod("load")
        method.isAccessible = true
        method.invoke(viewModel)
    } catch (e: Exception) {
        // 失败时什么都不做
    }
} 