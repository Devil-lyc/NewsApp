package com.lyc.newsapp.ui.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 将日期格式化为友好显示的形式
 * 
 * 如：
 * - 1小时前
 * - 昨天
 * - 2天前 
 * - 2023年10月1日
 */
fun formatDate(date: String): String {
    val parsedDate = parseDate(date) ?: return "未知时间"
    
    val currentTime = System.currentTimeMillis()
    val diffInMillis = currentTime - parsedDate.time
    val diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis)
    return when {
        diffInSeconds < 60 -> "刚刚"
        diffInSeconds < 3600 -> "${diffInSeconds / 60}分钟前"
        diffInSeconds < 86400 -> "${diffInSeconds / 3600}小时前"
        diffInSeconds < 604800 -> "${diffInSeconds / 86400}天前"
        else -> {
            val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
            dateFormat.format(parsedDate)
        }
    }
}

/**
 * 尝试多种格式解析日期字符串
 */
private fun parseDate(dateString: String): Date? {
    val dateFormats = arrayOf(
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "EEE MMM dd HH:mm:ss z yyyy", // Sun Mar 30 11:08:29 GMT 2025
        "yyyy-MM-dd HH:mm:ss",
        "yyyy/MM/dd HH:mm:ss",
        "EEE, dd MMM yyyy HH:mm:ss Z"
    )
    
    for (format in dateFormats) {
        try {
            val sdf = SimpleDateFormat(format, Locale.ENGLISH)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.parse(dateString)
        } catch (e: Exception) {
            // 尝试下一种格式
        }
    }
    
    return null // 所有格式都无法解析
} 