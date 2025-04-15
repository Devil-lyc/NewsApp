package com.lyc.newsapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lyc.newsapp.domain.model.Category

/** 水平滚动的分类选项卡 */
@Composable
fun CategoryTabs(
        categories: List<Category>,
        selectedCategory: Category,
        onCategorySelected: (Category) -> Unit,
        modifier: Modifier = Modifier
) {
    ScrollableTabRow(
        selectedTabIndex = categories.indexOf(selectedCategory),
        modifier = modifier,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = { tabPositions ->
            // 自定义指示器，使用底部指示条
            if (categories.isNotEmpty()) {
                val position = tabPositions[categories.indexOf(selectedCategory)]
                Box(
                    Modifier
                        .tabIndicatorOffset(position)
                        .fillMaxWidth()
                        .height(3.dp)
                        .padding(horizontal = 16.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                    )
                }
            },
            divider = {}
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory

            Tab(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/** 固定宽度的分类选项卡，适合顶部导航栏 */
@Composable
fun FixedCategoryTabs(
        categories: List<Category>,
        selectedCategory: Category,
        onCategorySelected: (Category) -> Unit
) {
    TabRow(
            selectedTabIndex = categories.indexOf(selectedCategory),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory

            Tab(
                    selected = isSelected,
                    onClick = { onCategorySelected(category) },
                    text = {
                        Text(text = category.name, style = MaterialTheme.typography.labelLarge)
                    }
            )
        }
    }
}
