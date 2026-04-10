package com.lyc.newsapp.data.remote.monitoring

data class NetworkOverviewMetric(
    val method: String,
    val host: String,
    val path: String,
    val code: Int?,
    val success: Boolean,
    val durationMs: Long,
    val cacheHit: Boolean,
    val fromNetwork: Boolean,
    val errorType: String? = null
)
