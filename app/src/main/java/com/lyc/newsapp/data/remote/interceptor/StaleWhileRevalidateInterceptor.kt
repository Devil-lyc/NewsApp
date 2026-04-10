package com.lyc.newsapp.data.remote.interceptor

import android.os.SystemClock
import com.lyc.newsapp.data.remote.NewsApi
import com.lyc.newsapp.di.NewsHttpClientRef
import okhttp3.CacheControl
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 缓存优先：请求侧允许较长 [maxStale]，尽快命中 OkHttp 磁盘缓存；
 * 若本次响应仅来自缓存，则在后台发起一次网络刷新，供下次使用。
 */
@Singleton
class StaleWhileRevalidateInterceptor @Inject constructor(
    @NewsHttpClientRef private val clientRef: AtomicReference<OkHttpClient?>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!shouldApplySwr(request)) {
            return chain.proceed(request)
        }
        val maxStaleRequest = request.newBuilder()
            .cacheControl(
                CacheControl.Builder()
                    .maxStale(MAX_STALE_SECONDS, TimeUnit.SECONDS)
                    .build()
            )
            .build()
        val response = chain.proceed(maxStaleRequest)
        if (!response.isSuccessful) {
            return response
        }
        val cacheResp = response.cacheResponse
        val netResp = response.networkResponse
        if (cacheResp != null && netResp == null) {
            val tagged = response.newBuilder()
                .header(HEADER_CACHE_POLICY, POLICY_SWR_STALE_REFRESH)
                .build()
            scheduleBackgroundRefresh(request)
            return tagged
        }
        if (netResp != null) {
            return response.newBuilder()
                .header(HEADER_CACHE_POLICY, POLICY_NETWORK)
                .build()
        }
        return response
    }

    private fun shouldApplySwr(request: Request): Boolean {
        if (request.method != GET) return false
        if (!request.url.host.equals(newsHost, ignoreCase = true)) return false
        if (request.cacheControl.onlyIfCached) return false
        val cc = request.cacheControl
        if (cc.noStore || cc.noCache) return false
        return true
    }

    private fun scheduleBackgroundRefresh(template: Request) {
        val key = template.url.toString()
        val now = SystemClock.elapsedRealtime()
        val last = lastBackgroundAt[key]
        if (last != null && now - last < BACKGROUND_REFRESH_MIN_INTERVAL_MS) {
            return
        }
        lastBackgroundAt[key] = now
        val client = clientRef.get() ?: return
        val refresh = template.newBuilder()
            .cacheControl(CacheControl.FORCE_NETWORK)
            .removeHeader(HEADER_CACHE_POLICY)
            .build()
        client.newCall(refresh).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // 静默失败；下次前台请求或离线回退仍会使用磁盘缓存
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use { }
                }
            }
        )
    }

    private companion object {
        const val GET = "GET"
        private val newsHost = java.net.URI.create(NewsApi.BASE_URL).host ?: "newsdata.io"
        const val MAX_STALE_SECONDS = 7 * 24 * 60 * 60 // 7 天内的陈旧缓存可先返回
        const val BACKGROUND_REFRESH_MIN_INTERVAL_MS = 30_000L
        const val HEADER_CACHE_POLICY = "X-News-Cache-Policy"
        const val POLICY_SWR_STALE_REFRESH = "swr-stale-refresh"
        const val POLICY_NETWORK = "network"

        private val lastBackgroundAt = ConcurrentHashMap<String, Long>()
    }
}
