package com.example.newsdetail

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.ui.components.CategoryChip
import com.lyc.newsapp.ui.utils.formatDate

/**
 * 文章详情页面 - 直接使用WebView加载新闻URL
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    newsId: String,
    onNavigateUp: () -> Unit,
    initialBookmarkState: Boolean = false // 添加初始收藏状态参数
) {
    val newsDetailViewModel = hiltViewModel<NewsDetailViewModel>()
    val newsDetailUiState = newsDetailViewModel.uiState.collectAsState()
    
    // 根据传入的初始收藏状态设置ViewModel状态
    LaunchedEffect(initialBookmarkState) {
        if (initialBookmarkState) {
            newsDetailViewModel.onEvent(NewsDetailUiEvents.SetInitialBookmarkState(true))
        }
    }
    
    // 获取文章数据
    LaunchedEffect(key1 = newsId) {
        newsDetailViewModel.onEvent(NewsDetailUiEvents.LoadNewsDetail(newsId))
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    // 收藏按钮
                    IconButton(
                        onClick = { 
                            newsDetailViewModel.onEvent(NewsDetailUiEvents.ToggleBookmark)
                        }
                    ) {
                        Icon(
                            imageVector = if (newsDetailUiState.value.isBookmarked) 
                                            Icons.Default.Bookmark 
                                         else 
                                            Icons.Default.BookmarkBorder,
                            contentDescription = if (newsDetailUiState.value.isBookmarked) "取消收藏" else "收藏",
                            tint = if (newsDetailUiState.value.isBookmarked) 
                                        MaterialTheme.colorScheme.primary
                                   else 
                                        MaterialTheme.colorScheme.onSurface
                        )
                    }
                    // 分享按钮
                    IconButton(onClick = { /* 分享功能，暂不实现 */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "分享"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                windowInsets = WindowInsets.statusBars
            )
        },
        contentWindowInsets = WindowInsets.statusBars
    ) { paddingValues ->
    
        val news = newsDetailUiState.value.news
        
        when {
            // 加载中状态
            newsDetailUiState.value.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "正在加载文章内容...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // 错误状态
            newsDetailUiState.value.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
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
                            text = newsDetailUiState.value.error ?: "未知错误",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { 
                                newsDetailViewModel.onEvent(NewsDetailUiEvents.LoadNewsDetail(newsId))
                            }
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
            
            // 文章内容显示
            news != null -> {
                // WebView方式
                if (news.url.isNotEmpty()) {
                    AndroidView(
                        factory = { context ->
                            WebView(context).apply {
                                webViewClient = WebViewClient()
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                loadUrl(news.url)
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                } 
                // 本地内容展示
                else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        // 标题
                        Text(
                            text = news.title,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 图片
                        if (news.imageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = news.imageUrl,
                                contentDescription = news.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        // 分类标签
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // 来源和时间
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = news.source_name,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = " • ${formatDate(news.publishedAt)}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            // 分类标签
                            CategoryChip(category = news.category)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // 正文内容
                        Text(
                            text = news.content,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

