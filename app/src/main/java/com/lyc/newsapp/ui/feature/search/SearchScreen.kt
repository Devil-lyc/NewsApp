package com.lyc.newsapp.ui.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lyc.newsapp.ui.components.NewsCard

/** 搜索界面 */
@Composable
fun SearchScreen(
    onArticleClick: (newsId:String) -> Unit
) {
    val searchViewModel = hiltViewModel<SearchViewModel>()
    val searchUiState = searchViewModel.uiState.collectAsState()
    val query = searchUiState.value.query


    // 热门搜索词
    val hotSearchTerms = remember {
        listOf(
            Triple("数字人民币", "256万热度", 1),
            Triple("碳中和", "198万热度", 2),
            Triple("半导体芯片", "163万热度", 3),
            Triple("量子通信", "129万热度", 4),
            Triple("太空旅游", "95万热度", 5)
        )
    }

    // 键盘控制器
    val keyboardController = LocalSoftwareKeyboardController.current

    // 执行搜索
    val performSearch = {
        if (query.isNotBlank()) {
            searchViewModel.dispatch(SearchIntent.SubmitSearch(query))
            keyboardController?.hide()
        }
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                tonalElevation = 0.dp
            ) {
                Box(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.statusBars)
                ) {
                    SearchBar(
                        query = query,
                        onQueryChange = { searchViewModel.dispatch(SearchIntent.QueryChanged(it)) },
                        onSearch = { performSearch() },
                        onClear = {
                            searchViewModel.dispatch(SearchIntent.ClearQuery)
                        }
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.statusBars
    ) { paddingValues ->
        val showHotSearch = query.isBlank() &&
            !searchUiState.value.isLoading &&
            searchUiState.value.error == null &&
            searchUiState.value.searchResults.isEmpty()

        when {
            // 初始搜索页面
            showHotSearch -> {
                Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
                    // 热门搜索
                    Text(
                        text = "热门搜索", 
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 热门搜索词组
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        hotSearchTerms.forEach { (term, hotness, rank) ->
                            HotSearchItem(
                                rank = rank,
                                text = term,
                                hotness = hotness,
                                onClick = {
                                    searchViewModel.dispatch(SearchIntent.QueryChanged(term))
                                    performSearch()
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            
            // 搜索中状态
            searchUiState.value.isLoading -> {
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
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "正在搜索「${query}」...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // 错误状态
            searchUiState.value.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "搜索时出现错误",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = searchUiState.value.error ?: "未知错误",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = {
                            searchViewModel.dispatch(SearchIntent.ClearError)
                        }) {
                            Text("关闭")
                        }
                    }
                }
            }
            
            // 无搜索结果
            searchUiState.value.searchResults.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "没有找到相关结果",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "尝试使用其他关键词搜索",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // 有搜索结果
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = searchUiState.value.searchResults,
                        key = { it.id }
                    ) { news ->
                        NewsCard(
                            news = news,
                            onArticleClick = onArticleClick,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

/** 搜索栏 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
        query: String,
        onQueryChange: (String) -> Unit,
        onSearch: () -> Unit,
        onClear: () -> Unit,
        modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth().height(75.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "搜索新闻",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.6f
                        )
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "搜索")
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = onClear) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "清除")
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}
@Composable
fun HotSearchItem(
    rank: Int,
    text: String,
    hotness: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 排名指示器
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    when (rank) {
                        1, 2, 3 -> MaterialTheme.colorScheme.primaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                )
        ) {
            Text(
                text = rank.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (rank <= 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 搜索词
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )

        // 热度
        Text(
            text = hotness,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
