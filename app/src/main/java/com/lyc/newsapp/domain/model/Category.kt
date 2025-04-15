package com.lyc.newsapp.domain.model

/**
 * 新闻分类数据模型
 */
data class Category(
    val id: String,
    val name: String,
    val icon: Int? = null
)

/**
 * 预定义的新闻分类列表
 */
object NewsCategories {
    val categories = listOf(
        Category(id = "all", name = "推荐"),
        Category(id = "technology", name = "科技"),
        Category(id = "business", name = "财经"),
        Category(id = "entertainment", name = "娱乐"),
        Category(id = "health", name = "健康"),
        Category(id = "science", name = "科学"),
        Category(id = "sports", name = "体育"),
        Category(id = "politics", name = "政治")
    )
} 