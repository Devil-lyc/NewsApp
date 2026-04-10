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
    /** 由拦截器写入的 [X-News-Cache-Policy]，如 offline-fallback、swr-stale-refresh */
    val cachePolicy: String? = null,
    val errorType: String? = null
)
