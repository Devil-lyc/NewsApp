package com.lyc.newsapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lyc.newsapp.core.util.formatDate
import com.lyc.newsapp.domain.model.News

/** 普通新闻文章卡片 */
@Composable
fun NewsCard(
    news: News,
    onArticleClick: (newsId:String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onArticleClick(news.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        FlatNewsCardLayout(
            title = {
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.layoutId("title")
                )
            },
            description = {
                Text(
                    text = news.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.layoutId("description")
                )
            },
            source = {
                Text(
                    text = news.source_name,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.layoutId("source")
                )
            },
            date = {
                Text(
                    text = formatDate(news.publishedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.layoutId("date")
                )
            },
            category = {
                CategoryChip(
                    category = news.category,
                    modifier = Modifier.layoutId("category")
                )
            },
            image = {
                AsyncImageWithPlaceholder(
                    model = news.imageUrl,
                    contentDescription = news.title,
                    modifier = Modifier
                        .layoutId("image")
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }
        )
    }
}

@Composable
private fun FlatNewsCardLayout(
    title: @Composable () -> Unit,
    description: @Composable () -> Unit,
    source: @Composable () -> Unit,
    date: @Composable () -> Unit,
    category: @Composable () -> Unit,
    image: @Composable () -> Unit
) {
    val horizontalPadding = 16.dp
    val verticalPadding = 12.dp
    val contentGap = 12.dp
    val textGapSmall = 4.dp
    val textGapMedium = 8.dp
    val imageSize = 120.dp

    Layout(
        content = {
            title()
            description()
            source()
            date()
            category()
            image()
        }
    ) { measurables, constraints ->
        val hp = horizontalPadding.roundToPx()
        val vp = verticalPadding.roundToPx()
        val gap = contentGap.roundToPx()
        val textGap1 = textGapSmall.roundToPx()
        val textGap2 = textGapMedium.roundToPx()
        val img = imageSize.roundToPx()

        val titleMeasurable = measurables.first { it.layoutId == "title" }
        val descriptionMeasurable = measurables.first { it.layoutId == "description" }
        val sourceMeasurable = measurables.first { it.layoutId == "source" }
        val dateMeasurable = measurables.first { it.layoutId == "date" }
        val categoryMeasurable = measurables.first { it.layoutId == "category" }
        val imageMeasurable = measurables.first { it.layoutId == "image" }

        val availableWidth = constraints.maxWidth - hp * 2
        val textWidth = (availableWidth - img - gap).coerceAtLeast(0)

        val imagePlaceable = imageMeasurable.measure(
            androidx.compose.ui.unit.Constraints.fixed(img, img)
        )
        val titlePlaceable = titleMeasurable.measure(
            androidx.compose.ui.unit.Constraints(maxWidth = textWidth)
        )
        val descriptionPlaceable = descriptionMeasurable.measure(
            androidx.compose.ui.unit.Constraints(maxWidth = textWidth)
        )
        val sourcePlaceable = sourceMeasurable.measure(
            androidx.compose.ui.unit.Constraints(maxWidth = textWidth)
        )
        val datePlaceable = dateMeasurable.measure(
            androidx.compose.ui.unit.Constraints(maxWidth = textWidth)
        )
        val categoryPlaceable = categoryMeasurable.measure(
            androidx.compose.ui.unit.Constraints(maxWidth = textWidth)
        )

        val sourceDateHeight = sourcePlaceable.height + datePlaceable.height
        val bottomRowHeight = maxOf(sourceDateHeight, categoryPlaceable.height)

        val textContentHeight = titlePlaceable.height +
            textGap1 +
            descriptionPlaceable.height +
            textGap2 +
            bottomRowHeight

        val bodyHeight = maxOf(textContentHeight, imagePlaceable.height)
        val layoutHeight = vp * 2 + bodyHeight

        layout(constraints.maxWidth, layoutHeight) {
            val leftX = hp
            val textTopY = vp
            val imageX = constraints.maxWidth - hp - imagePlaceable.width
            val imageY = vp + (bodyHeight - imagePlaceable.height) / 2
            val bottomY = textTopY + titlePlaceable.height + textGap1 + descriptionPlaceable.height + textGap2

            imagePlaceable.placeRelative(imageX, imageY)

            titlePlaceable.placeRelative(leftX, textTopY)
            descriptionPlaceable.placeRelative(leftX, textTopY + titlePlaceable.height + textGap1)
            sourcePlaceable.placeRelative(leftX, bottomY)
            datePlaceable.placeRelative(leftX, bottomY + sourcePlaceable.height)
            categoryPlaceable.placeRelative(
                x = leftX + textWidth - categoryPlaceable.width,
                y = bottomY + (bottomRowHeight - categoryPlaceable.height) / 2
            )
        }
    }
}

/** 头条新闻卡片，大图展示 */
@Composable
fun HeadlineCard(
    news: News,
    onArticleClick: (newsId:String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable { onArticleClick(news.id) },
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
fun CategoryChip(
    category: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
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
