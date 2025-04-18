package com.lyc.newsapp.ui.screens.bookmark

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lyc.newsapp.R
import com.lyc.newsapp.domain.model.Favorite
import com.lyc.newsapp.domain.model.News
import com.lyc.newsapp.ui.components.LoginHint
import com.lyc.newsapp.ui.components.NewsCard
import com.lyc.newsapp.ui.screens.auth.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 收藏夹界面
 * 展示用户收藏的新闻列表，支持查看详情、取消收藏和清空收藏
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun BookmarkScreen(
    onArticleClick: (String) -> Unit,
    onNavigateToLogin: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel()
) {
    // 获取ViewModel
    val bookmarkViewModel = hiltViewModel<BookmarkViewModel>()
    val bookmarkUiState by bookmarkViewModel.uiState.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    
    // 对话框状态
    val showClearConfirmDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    // 使用完全独立的刷新状态
    var isRefreshing by remember { mutableStateOf(false) }
    
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { 
            coroutineScope.launch {
                isRefreshing = true
                bookmarkViewModel.getFavorites()
                // 确保刷新指示器显示足够时间，无论数据加载多快
                delay(1000)
                isRefreshing = false
            }
        }
    )
    
    // 使用LaunchedEffect在每次页面变为可见时刷新收藏列表
    LaunchedEffect(Unit) {
        bookmarkViewModel.onResume()
    }
    
    // 检查登录状态
    if (!authState.isLoggedIn) {
        // 未登录，显示提示
        LoginHint(
            message = "请先登录才能查看收藏内容",
            onLoginClick = onNavigateToLogin
        )
        return
    }
    
    // 顶部应用栏和内容区域
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.padding(start = 6.dp),
                        text = stringResource(R.string.my_bookmarks),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                actions = {
                    // 只有在有收藏时显示清空按钮
                    if (bookmarkUiState.favorites.isNotEmpty()) {
                        IconButton(onClick = { showClearConfirmDialog.value = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.clear_all)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        // 收藏内容区域（带下拉刷新）
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
        ) {
            when {
                // 加载中状态
                bookmarkUiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "正在加载收藏...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // 错误状态
                bookmarkUiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
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
                                text = bookmarkUiState.error ?: "未知错误",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { bookmarkViewModel.getFavorites() }
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
                
                // 有收藏内容
                bookmarkUiState.favorites.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 收藏列表
                        items(
                            items = bookmarkUiState.favorites,
                            key = { it.id }
                        ) { favorite ->
                            // 将Favorite转换为News对象显示
                            val news = News(
                                id = favorite.newsId,
                                title = favorite.title,
                                description = favorite.description,
                                content = "",
                                url = favorite.url,
                                imageUrl = favorite.imageUrl,
                                language = "",
                                category = favorite.category,
                                publishedAt = favorite.publishedAt,
                                author = favorite.author,
                                country = "",
                                nextPage = "",
                                source_name = favorite.sourceName,
                                source_icon = "",
                                source_id = "",
                                source_url = "",
                                ai_org = "",
                                ai_region = "",
                                ai_tag = "",
                                duplicate = false,
                                keywords = "",
                                pubDateTZ = "",
                                sentiment = "",
                                videoUrl = "",
                                isSaved = true
                            )
                            
                            // 使用NewsCard显示
                            NewsCard(
                                news = news,
                                onArticleClick = { 
                                    // 从收藏列表进入详情页时，传递true作为初始收藏状态
                                    onArticleClick(news.id) 
                                }
                            )
                        }
                        
                        // 底部间距
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
                
                // 空收藏状态
                else -> {
                    EmptyBookmarkState(
                        modifier = Modifier.fillMaxSize()
                    )
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
    
    // 清空确认对话框
    if (showClearConfirmDialog.value) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog.value = false },
            title = { Text("确认清空") },
            text = { Text("确定要清空所有收藏吗？此操作无法撤销。") },
            confirmButton = {
                Button(
                    onClick = {
                        bookmarkViewModel.clearAllFavorites()
                        showClearConfirmDialog.value = false
                    }
                ) {
                    Text("清空")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showClearConfirmDialog.value = false }
                ) {
                    Text("取消")
                }
            }
        )
    }
}

/**
 * 空收藏状态组件
 * 当用户没有收藏任何新闻时显示
 */
@Composable
fun EmptyBookmarkState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.BookmarkBorder,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
            modifier = Modifier.size(100.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(R.string.empty_bookmark),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(R.string.bookmark_hint),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}
