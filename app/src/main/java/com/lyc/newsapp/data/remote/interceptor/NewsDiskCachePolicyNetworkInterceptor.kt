package com.lyc.newsapp.data.remote.interceptor

import com.lyc.newsapp.data.remote.NewsApi
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 第三方接口常返回不可缓存头，导致 OkHttp [Cache] 无法落盘。
 * 在 **网络层** 为新闻 GET 成功响应写入可缓存的 Cache-Control，供离线/SWR 使用。
 */
@Singleton
class NewsDiskCachePolicyNetworkInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (request.method != GET) return response
        if (!request.url.host.equals(newsHost, ignoreCase = true)) return response
        if (!response.isSuccessful) return response
        return response.newBuilder()
            .removeHeader("Pragma")
            .header("Cache-Control", CACHE_CONTROL)
            .build()
    }

    private companion object {
        const val GET = "GET"
        private val newsHost = java.net.URI.create(NewsApi.BASE_URL).host ?: "newsdata.io"
        /** 短时新鲜窗口；过期后仍可由请求侧 max-stale + 离线回退使用磁盘副本 */
        const val CACHE_CONTROL = "public, max-age=300"
    }
}
