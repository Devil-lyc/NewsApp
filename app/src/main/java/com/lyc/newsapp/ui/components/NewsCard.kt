package com.lyc.newsapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lyc.newsapp.domain.model.News
import com.lyc.newsapp.ui.utils.AsyncImageWithPlaceholder
import com.lyc.newsapp.ui.utils.formatDate

/** 普通新闻文章卡片 */
@Composable
fun NewsCard(
    news: News,
    onArticleClick: (News) -> Unit,
    modifier: Modifier = Modifier
) {
   Card(
       modifier = modifier
                   .fillMaxWidth()
                   .clickable { onArticleClick(news) },
       elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
   ) {
       Row(
           verticalAlignment = Alignment.CenterVertically
       ) {
           // 文章信息
           Column(modifier = Modifier.weight(0.6f).padding(16.dp)) {
               // 标题
               Text(
                   text = news.title,
                   style = MaterialTheme.typography.titleMedium,
                   maxLines = 2,
                   overflow = TextOverflow.Ellipsis
               )
               Spacer(modifier = Modifier.height(4.dp))
               // 描述
               Text(
                   text = news.description,
                   style = MaterialTheme.typography.bodyMedium,
                   color = MaterialTheme.colorScheme.onSurfaceVariant,
                   maxLines = 2,
                   overflow = TextOverflow.Ellipsis
               )
               Spacer(modifier = Modifier.height(8.dp))
               // 底部信息栏
               Row(
                   modifier = Modifier.fillMaxWidth(),
                   horizontalArrangement = Arrangement.SpaceBetween,
                   verticalAlignment = Alignment.CenterVertically
               ) {
                   // 来源和日期
                   Column {
                       Text(
                           text = news.source_name,
                           style = MaterialTheme.typography.labelMedium,
                           color = MaterialTheme.colorScheme.primary
                       )
                       Text(
                           text = formatDate(news.publishedAt),
                           style = MaterialTheme.typography.labelSmall,
                           color = MaterialTheme.colorScheme.onSurfaceVariant
                       )
                   }
                   // 类别标签
                   CategoryChip(category = news.category)
//                   // 收藏按钮
//                   IconButton(
//                       onClick = {
//                           onBookmarkClick(article, !article.isSaved)
//                       }
//                   ) {
//                       Icon(
//                           imageVector = if (article.isSaved) Icons.Default.Bookmark
//                           else Icons.Default.BookmarkBorder,
//                           contentDescription = if (article.isSaved) "取消收藏" else "收藏",
//                           tint = if (article.isSaved) MaterialTheme.colorScheme.primary
//                           else MaterialTheme.colorScheme.onSurfaceVariant
//                       )
//                   }
               }
           }
           // 文章图片
           AsyncImageWithPlaceholder(
               model = news.imageUrl,
               contentDescription = news.title,
               modifier = Modifier.size(120.dp)
                   .padding(end = 10.dp)
                   .clip(MaterialTheme.shapes.medium),
               contentScale = ContentScale.Crop
           )
       }
   }
}

/** 头条新闻卡片，大图展示 */
@Composable
fun HeadlineCard(
    news: News,
    onArticleClick: (News) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable { onArticleClick(news) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column { // 头条图片
            AsyncImageWithPlaceholder(
                model = news.imageUrl,
                contentDescription = news.title,
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentScale = ContentScale.Crop
            )
            // 内容区域
            Column(modifier = Modifier.padding(16.dp)) {
                // 标题
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))

                // 描述
                Text(
                    text = news.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))

                // 底部信息栏
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 来源和日期
                    Column {
                        Text(
                            text = news.source_name,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 添加作者信息（如果有）
                            Text(
                                text = news.author,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = " • ",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatDate(news.publishedAt),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    // 类别标签
                    CategoryChip(category = news.category)
                }
            }
        }
    }
}

/** 分类标签 */
@Composable
fun CategoryChip(category: String) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = when (category) {
                        "technology" -> "科技"
                        "business" -> "财经"
                        "entertainment" -> "娱乐"
                        "health" -> "健康"
                        "science" -> "科学"
                        "sports" -> "体育"
                        "politics" -> "政治"
                        else -> "综合"
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
}
