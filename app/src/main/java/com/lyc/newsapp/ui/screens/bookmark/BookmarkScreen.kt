package com.lyc.newsapp.ui.screens.bookmark

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.lyc.newsapp.domain.model.News

/**
 * 收藏夹界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkScreen(
    onArticleClick: (News) -> Unit
) {
//    // 获取收藏的文章
//    val savedArticles = remember { FakeNewsRepository.getSavedArticles() }
//    val savedArticlesState = remember { mutableStateListOf<News>().apply { addAll(savedArticles) } }
//
//    Scaffold(
//        topBar = {
//            SmallTopAppBar(
//                title = {
//                    Text(
//                        text = "我的收藏",
//                        style = MaterialTheme.typography.headlineSmall.copy(
//                            fontWeight = FontWeight.Bold
//                        )
//                    )
//                },
//                actions = {
//                    if (savedArticlesState.isNotEmpty()) {
//                        IconButton(onClick = { savedArticlesState.clear() }) {
//                            Icon(
//                                imageVector = Icons.Default.Delete,
//                                contentDescription = "清空收藏"
//                            )
//                        }
//                    }
//                },
//                colors = TopAppBarDefaults.smallTopAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.background
//                )
//            )
//        }
//    ) { paddingValues ->
//        if (savedArticlesState.isNotEmpty()) {
//            LazyColumn(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(paddingValues),
//                contentPadding = PaddingValues(16.dp),
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                items(
//                    items = savedArticlesState,
//                    key = { it.id }
//                ) { article ->
//                    NewsCard(
//                        news = article,
//                        onArticleClick = onArticleClick,
////                        onBookmarkClick = { _, _ ->
////                            // 从收藏列表中移除
////                            savedArticlesState.remove(article)
////                        }
//                    )
//                }
//
//                // 底部间距
//                item {
//                    Spacer(modifier = Modifier.height(80.dp))
//                }
//            }
//        } else {
//            // 空收藏状态
//            EmptyBookmarkState(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(paddingValues)
//            )
//        }
//    }
//}
//
///**
// * 空收藏状态
// */
//@Composable
//fun EmptyBookmarkState(
//    modifier: Modifier = Modifier
//) {
//    Column(
//        modifier = modifier,
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Icon(
//            imageVector = Icons.Default.Delete,
//            contentDescription = null,
//            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
//            modifier = Modifier.size(100.dp)
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Text(
//            text = "你还没有收藏任何文章",
//            style = MaterialTheme.typography.titleLarge,
//            textAlign = TextAlign.Center,
//            color = MaterialTheme.colorScheme.onSurfaceVariant
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Text(
//            text = "在阅读时点击收藏按钮将文章添加到这里",
//            style = MaterialTheme.typography.bodyMedium,
//            textAlign = TextAlign.Center,
//            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
//        )
//    }
//}
}
