package com.lyc.newsapp.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
@OptIn(ExperimentalMaterial3Api::class)
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
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        text = "新闻",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                actions = {
                    IconButton(onClick = { /* 通知功能，暂不实现 */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "通知"
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
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
                    onCategorySelected = { selectedCategory = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // 判断是否为"全部"分类且列表不为空，如果是则显示头条
            if (selectedCategory.id == "all" && newsList.isNotEmpty()) {
                // 头条新闻
                item {
                    Text(
                        text = "今日要闻",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

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
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
                // 分类文章列表
                items(newsList) { news ->
                    NewsCard(
                        news = news,
                        onArticleClick = onArticleClick,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
            // 如果列表为空，显示加载状态或空状态
            if (newsList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (homeUiState.value.isLoading) {
                            CircularProgressIndicator()
                        } else {
                            Text(
                                text = "暂无新闻",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
} 