package com.lyc.newsapp.data.remote.interceptor

import android.os.SystemClock
import com.lyc.newsapp.data.remote.NewsApi
import com.lyc.newsapp.di.NewsHttpClientRef
import com.tencent.mmkv.MMKV
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 手动缓存策略（仅应用拦截器）：
 * 1) 优先返回本地缓存（包含 fetchedAt/expiresAt 时间戳）；
 * 2) 缓存过期时先返回旧缓存，再后台刷新；
 * 3) 无缓存时请求网络并回写缓存。
 */
@Singleton
class ManualNewsCacheInterceptor @Inject constructor(
    @NewsHttpClientRef private val clientRef: AtomicReference<OkHttpClient?>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!shouldApply(request) || request.header(HEADER_BYPASS_CACHE) == "1") {
            return chain.proceed(request)
        }

        val key = request.url.toString()
        val now = System.currentTimeMillis()
        val cachedBody = kv.decodeString("${key}_body")
        val fetchedAt = kv.decodeLong("${key}_fetchedAt", 0L)
        val expiresAt = kv.decodeLong("${key}_expiresAt", 0L)
        val contentType = kv.decodeString("${key}_contentType")

        if (!cachedBody.isNullOrEmpty()) {
            if (now <= expiresAt) {
                return buildCacheResponse(
                    request = request,
                    body = cachedBody,
                    contentType = contentType,
                    fetchedAt = fetchedAt,
                    expiresAt = expiresAt,
                    policy = POLICY_CACHE_HIT
                )
            }

            scheduleBackgroundRefresh(request)
            return buildCacheResponse(
                request = request,
                body = cachedBody,
                contentType = contentType,
                fetchedAt = fetchedAt,
                expiresAt = expiresAt,
                policy = POLICY_CACHE_STALE
            )
        }

        val networkResponse = chain.proceed(request)
        return cacheNetworkResponse(request, key, networkResponse, now)
    }

    private fun cacheNetworkResponse(
        request: Request,
        key: String,
        response: Response,
        now: Long
    ): Response {
        val bodyString = response.body?.string() ?: return response
        val contentType = response.body?.contentType()?.toString()
        if (response.isSuccessful) {
            val expiresAt = now + TTL_MS
            kv.encode("${key}_body", bodyString)
            kv.encode("${key}_fetchedAt", now)
            kv.encode("${key}_expiresAt", expiresAt)
            kv.encode("${key}_contentType", contentType)
            return response.newBuilder()
                .header(HEADER_CACHE_POLICY, POLICY_NETWORK)
                .header(HEADER_FETCHED_AT_MS, now.toString())
                .header(HEADER_EXPIRES_AT_MS, expiresAt.toString())
                .body(bodyString.toResponseBody(contentType?.toMediaTypeOrNull()))
                .build()
        }
        return response.newBuilder()
            .body(bodyString.toResponseBody(contentType?.toMediaTypeOrNull()))
            .build()
    }

    private fun buildCacheResponse(
        request: Request,
        body: String,
        contentType: String?,
        fetchedAt: Long,
        expiresAt: Long,
        policy: String
    ): Response {
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .header(HEADER_CACHE_POLICY, policy)
            .header(HEADER_FETCHED_AT_MS, fetchedAt.toString())
            .header(HEADER_EXPIRES_AT_MS, expiresAt.toString())
            .body(body.toResponseBody(contentType?.toMediaTypeOrNull()))
            .build()
    }

    private fun scheduleBackgroundRefresh(template: Request) {
        val key = template.url.toString()
        val now = SystemClock.elapsedRealtime()
        val last = lastRefreshAt[key]
        if (last != null && now - last < BACKGROUND_REFRESH_MIN_INTERVAL_MS) return
        lastRefreshAt[key] = now

        val client = clientRef.get() ?: return
        val refreshRequest = template.newBuilder()
            .header(HEADER_BYPASS_CACHE, "1")
            .build()
        client.newCall(refreshRequest).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) = Unit
                override fun onResponse(call: Call, response: Response) {
                    response.use { }
                }
            }
        )
    }

    private fun shouldApply(request: Request): Boolean {
        if (request.method != GET) return false
        if (!request.url.host.equals(newsHost, ignoreCase = true)) return false
        return true
    }

    private companion object {
        const val GET = "GET"
        private val newsHost = java.net.URI.create(NewsApi.BASE_URL).host ?: "newsdata.io"
        private val kv: MMKV by lazy { MMKV.mmkvWithID("news_manual_http_cache", MMKV.MULTI_PROCESS_MODE) }

        const val TTL_MS = 3 * 60 * 60 * 1000L
        const val BACKGROUND_REFRESH_MIN_INTERVAL_MS = 30_000L

        const val HEADER_BYPASS_CACHE = "X-News-Bypass-Manual-Cache"
        const val HEADER_CACHE_POLICY = "X-News-Cache-Policy"
        const val HEADER_FETCHED_AT_MS = "X-News-Fetched-At-Ms"
        const val HEADER_EXPIRES_AT_MS = "X-News-Expires-At-Ms"
        const val POLICY_CACHE_HIT = "manual-cache-hit"
        const val POLICY_CACHE_STALE = "manual-cache-stale-refreshing"
        const val POLICY_NETWORK = "manual-network"

        val lastRefreshAt = ConcurrentHashMap<String, Long>()
    }
}
