package com.lyc.newsapp.ui.screens.news

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lyc.newsapp.domain.model.News
import com.lyc.newsapp.ui.components.CategoryChip
import com.lyc.newsapp.ui.utils.AsyncImageWithPlaceholder
import com.lyc.newsapp.ui.utils.formatDate

/**
 * 文章详情页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    newsId: String,
    onNavigateUp: () -> Unit
) {
    val newsDetailViewModel = hiltViewModel<NewsDetailViewModel>()
    val newsDetailUiState = newsDetailViewModel.uiState.collectAsState()
    // 获取文章数据
    LaunchedEffect(key1 = newsId) {
        newsDetailViewModel.onEvent(NewsDetailUiEvents.LoadNewsDetail(newsId))
    }
    // 滚动状态
    val scrollState = rememberScrollState()
    val isBookmarked = remember { mutableStateOf(newsDetailUiState.value.isBookmarked) }
    
    Scaffold(
        topBar = {
            SmallTopAppBar(
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
                    IconButton(onClick = { isBookmarked.value = !isBookmarked.value }) {
                        Icon(
                            imageVector = if (isBookmarked.value) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (isBookmarked.value) "取消收藏" else "收藏"
                        )
                    }
                    IconButton(onClick = { /* 分享功能，暂不实现 */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "分享"
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        newsDetailUiState.value.news?.let { news ->
            NewsContent(
                news = news,
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
            )
        } ?: run {
            // 显示加载状态或占位符
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

/**
 * 文章内容
 */
@Composable
private fun NewsContent(
    news: News,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // 文章头图
        ArticleHeaderImage(news = news)
        
        // 文章内容
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 类别标签
            CategoryChip(category = news.category)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 文章标题
            Text(
                text = news.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 文章元数据
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = news.source_name,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = " • ${news.author}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = " • ${formatDate(news.publishedAt)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 文章内容
            Text(
                text = news.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 28.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 底部来源
            Text(
                text = "内容来源: ${news.source_name}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * 文章头部大图
 */
@Composable
private fun ArticleHeaderImage(
    news: News,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        // 文章图片
        AsyncImageWithPlaceholder(
            model = news.imageUrl,
            contentDescription = news.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // 渐变遮罩
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.5f)
                        ),
                        startY = 300f,
                        endY = 900f
                    )
                )
        )
    }
} 