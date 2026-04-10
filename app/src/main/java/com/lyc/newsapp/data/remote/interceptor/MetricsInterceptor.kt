package com.lyc.newsapp.data.remote.interceptor

import android.os.SystemClock
import com.lyc.newsapp.data.remote.monitoring.NetworkOverviewMetric
import com.lyc.newsapp.data.remote.monitoring.TimberNetworkMetricsReporter
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetricsInterceptor @Inject constructor(
    private val reporter: TimberNetworkMetricsReporter
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startMs = SystemClock.elapsedRealtime()

        var response: Response? = null
        var error: Throwable? = null

        try {
            response = chain.proceed(request)
            return response
        } catch (t: Throwable) {
            error = t
            throw t
        } finally {
            val duration = SystemClock.elapsedRealtime() - startMs
            val path = request.url.encodedPath.ifEmpty { "/" }

            reporter.reportOverview(
                NetworkOverviewMetric(
                    method = request.method,
                    host = request.url.host,
                    path = path,
                    code = response?.code,
                    success = response?.isSuccessful == true,
                    durationMs = duration,
                    cacheHit = response?.cacheResponse != null,
                    fromNetwork = response?.networkResponse != null,
                    cachePolicy = response?.header("X-News-Cache-Policy"),
                    errorType = error?.javaClass?.simpleName
                )
            )
        }
    }
}