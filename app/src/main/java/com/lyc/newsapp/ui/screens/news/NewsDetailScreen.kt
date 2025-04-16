package com.lyc.newsapp.ui.screens.news

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
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
import com.lyc.newsapp.domain.model.News
import com.lyc.newsapp.ui.components.CategoryChip
import com.lyc.newsapp.ui.utils.formatDate

/**
 * 文章详情页面 - 直接使用WebView加载新闻URL
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
    
    val isBookmarked = remember { mutableStateOf(newsDetailUiState.value.isBookmarked) }
    
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 显示加载状态
            if (newsDetailUiState.value.isLoading) {
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
                            text = "正在加载...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } 
            // 显示错误状态
            else if (newsDetailUiState.value.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
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
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                newsDetailViewModel.onEvent(NewsDetailUiEvents.LoadNewsDetail(newsId))
                            }
                        ) {
                            Text(text = "重试")
                        }
                    }
                }
            }
            // 显示新闻内容
            else if (newsDetailUiState.value.news != null) {
                val news = newsDetailUiState.value.news!!
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 文章元数据
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        AsyncImage(
                            model = news.imageUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        // 类别标签
                        CategoryChip(category = news.category)
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // 文章标题
                        Text(
                            text = news.title,
                            style = MaterialTheme.typography.headlineSmall.copy(
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

//                            Text(
//                                text = " • ${news.author}",
//                                style = MaterialTheme.typography.labelLarge,
//                                color = MaterialTheme.colorScheme.onSurfaceVariant
//                            )

                            Text(
                                text = " • ${formatDate(news.publishedAt)}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = news.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                    }
                    
                    // WebView 内容
                    // Box(
                    //     modifier = Modifier
                    //         .fillMaxWidth()
                    //         .weight(1f)
                    // ) {
                    //     // 验证URL是否有效
                    //     if (news.url.isEmpty() || !news.url.startsWith("http")) {
                    //         Box(
                    //             modifier = Modifier.fillMaxSize(),
                    //             contentAlignment = Alignment.Center
                    //         ) {
                    //             Text(
                    //                 text = "无法加载新闻内容：无效的URL",
                    //                 style = MaterialTheme.typography.bodyLarge,
                    //                 textAlign = TextAlign.Center,
                    //                 modifier = Modifier.padding(16.dp)
                    //             )
                    //         }
                    //     } else {
                    //         // 加载WebView
                    //         var isWebViewLoading by remember { mutableStateOf(true) }
                    //         var webViewError by remember { mutableStateOf<String?>(null) }
                            
                    //         AndroidView(
                    //             factory = { context ->
                    //                 WebView(context).apply {
                    //                     settings.apply {
                    //                         javaScriptEnabled = true
                    //                         loadWithOverviewMode = true
                    //                         useWideViewPort = true
                    //                         domStorageEnabled = true
                    //                         setGeolocationEnabled(false)
                    //                         blockNetworkImage = false
                    //                         loadsImagesAutomatically = true
                    //                     }
                                        
                    //                     webViewClient = object : WebViewClient() {
                    //                         override fun onPageFinished(view: WebView?, url: String?) {
                    //                             super.onPageFinished(view, url)
                    //                             isWebViewLoading = false
                    //                         }
                                            
                    //                         override fun onReceivedError(
                    //                             view: WebView?,
                    //                             errorCode: Int,
                    //                             description: String?,
                    //                             failingUrl: String?
                    //                         ) {
                    //                             super.onReceivedError(view, errorCode, description, failingUrl)
                    //                             isWebViewLoading = false
                    //                             webViewError = description
                    //                         }
                    //                     }
                    //                 }
                    //             },
                    //             update = { webView ->
                    //                 webView.loadUrl(news.url)
                    //             }
                    //         )
                            
                    //         // 显示WebView的加载状态
                    //         if (isWebViewLoading) {
                    //             Box(
                    //                 modifier = Modifier.fillMaxSize(),
                    //                 contentAlignment = Alignment.Center
                    //             ) {
                    //                 CircularProgressIndicator()
                    //             }
                    //         }
                            
                    //         // 显示WebView的错误状态
                    //         if (webViewError != null) {
                    //             Box(
                    //                 modifier = Modifier.fillMaxSize(),
                    //                 contentAlignment = Alignment.Center
                    //             ) {
                    //                 Column(
                    //                     horizontalAlignment = Alignment.CenterHorizontally,
                    //                     verticalArrangement = Arrangement.Center,
                    //                     modifier = Modifier.padding(16.dp)
                    //                 ) {
                    //                     Icon(
                    //                         imageVector = Icons.Default.Warning,
                    //                         contentDescription = "错误",
                    //                         tint = MaterialTheme.colorScheme.error,
                    //                         modifier = Modifier.size(48.dp)
                    //                     )
                    //                     Spacer(modifier = Modifier.height(16.dp))
                    //                     Text(
                    //                         text = webViewError ?: "未知错误",
                    //                         style = MaterialTheme.typography.bodyMedium,
                    //                         textAlign = TextAlign.Center
                    //                     )
                    //                 }
                    //             }
                    //         }
                    //     }
                    // }
                }
            } 
            // 空状态（既不是加载中，也没有错误，也没有数据）
            else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "信息",
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "无法找到新闻",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "可能是网络问题或新闻已不可用",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                newsDetailViewModel.onEvent(NewsDetailUiEvents.LoadNewsDetail(newsId))
                            }
                        ) {
                            Text(text = "重试")
                        }
                    }
                }
            }
        }
    }
}

