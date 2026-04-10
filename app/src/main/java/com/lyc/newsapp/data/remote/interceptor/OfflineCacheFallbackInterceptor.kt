package com.lyc.newsapp.data.remote.interceptor

import com.lyc.newsapp.data.remote.NewsApi
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 网络不可用时回退到磁盘缓存（仅 GET、仅新闻 API）。
 * 置于拦截器链最外层，捕获 [IOException] 后发起 `only-if-cached` 请求。
 */
@Singleton
class OfflineCacheFallbackInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.method != GET || !request.url.host.equals(NEWS_HOST, ignoreCase = true)) {
            return chain.proceed(request)
        }
        return try {
            chain.proceed(request)
        } catch (e: IOException) {
            val cacheOnly = request.newBuilder()
                .cacheControl(
                    CacheControl.Builder()
                        .onlyIfCached()
                        .maxStale(Int.MAX_VALUE, TimeUnit.SECONDS)
                        .build()
                )
                .build()
            val fallback = chain.proceed(cacheOnly)
            if (fallback.code == HTTP_GATEWAY_TIMEOUT) {
                fallback.close()
                throw e
            }
            fallback.newBuilder()
                .header(HEADER_CACHE_POLICY, POLICY_OFFLINE_FALLBACK)
                .build()
        }
    }

    private companion object {
        const val GET = "GET"
        private val NEWS_HOST = java.net.URI.create(NewsApi.BASE_URL).host ?: "newsdata.io"
        const val HTTP_GATEWAY_TIMEOUT = 504
        const val HEADER_CACHE_POLICY = "X-News-Cache-Policy"
        const val POLICY_OFFLINE_FALLBACK = "offline-fallback"
    }
}
