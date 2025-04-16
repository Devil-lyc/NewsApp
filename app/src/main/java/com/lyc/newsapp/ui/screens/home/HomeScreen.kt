package com.lyc.newsapp.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

/**
 * 首页界面，显示新闻文章和分类
 */
@Composable
fun HomeScreen(
    onArticleClick: (News) -> Unit
) {
    // 状态
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val homeUiState = homeViewModel.uiState.collectAsState()
    var selectedCategory by remember { mutableStateOf(NewsCategories.categories.first()) }

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

    Scaffold(
        contentWindowInsets = WindowInsets.statusBars
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 分类选择器
            item {
                CategoryTabs(
                    categories = NewsCategories.categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { 
                        selectedCategory = it
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            when {
                // 有新闻数据时显示内容
                newsList.isNotEmpty() -> {
                    when(selectedCategory.id) {
                        "all" -> {
                            // 头条新闻
                            item {
                                Text(
                                    text = "今日要闻",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 20.dp)
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // 头条文章
                                HeadlineCard(
                                    news = newsList[0],
                                    onArticleClick = onArticleClick,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                            
                            // 其他头条
                            items(newsList.drop(1)) { news ->
                                NewsCard(
                                    news = news,
                                    onArticleClick = onArticleClick,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                            
                            // 推荐标题
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
                            }
                        }
                        else -> {
                            // 其他分类只显示普通列表
                            items(newsList) { news ->
                                NewsCard(
                                    news = news,
                                    onArticleClick = onArticleClick,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
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
                                    onClick = {  }
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
                                    onClick = {
                                        //TODO
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