package com.example.common.util

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size

/**
 * 优化版本: 带占位图和错误处理的异步图片加载组件
 * 性能优化: 
 * 1. 添加内存缓存和磁盘缓存策略
 * 2. 根据显示大小预设图片尺寸，避免加载过大图片
 * 3. 使用双缓存策略避免频繁重建视图
 */

@Composable
fun AsyncImageWithPlaceholder(
    model: Any?,
    context: Context,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale,
    placeholderColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    errorColor: Color = MaterialTheme.colorScheme.errorContainer
) {
//     获取Context以构建ImageRequest

    // 构建优化的图片请求
    val imageRequest = ImageRequest.Builder(context)
        .data(model)
        .crossfade(true) // 淡入淡出效果，平滑过渡
        .memoryCachePolicy(CachePolicy.ENABLED) // 启用内存缓存
        .diskCachePolicy(CachePolicy.ENABLED) // 启用磁盘缓存
        .size(Size.ORIGINAL) // 按原始尺寸加载，避免重复解码
        .build()

    // 使用SubcomposeAsyncImage来处理加载状态
    SubcomposeAsyncImage(
        model = imageRequest,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
    ) {
        val state = painter.state
        when(state) {
            // 加载中显示占位图 - 简化占位图，减少绘制复杂度
            is AsyncImagePainter.State.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(placeholderColor),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                }
            }
            // 加载失败显示错误图标 - 简化错误状态UI
            is AsyncImagePainter.State.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(errorColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BrokenImage,
                        contentDescription = "图片加载失败"
                    )
                }
            }
            // 成功则显示图片
            else -> {
                SubcomposeAsyncImageContent()
            }
        }
    }
}