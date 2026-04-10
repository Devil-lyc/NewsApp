package com.lyc.newsapp.data.remote.monitoring

import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

interface NetworkMetricsReporter {
    fun reportOverview(metric: NetworkOverviewMetric)
}

@Singleton
class TimberNetworkMetricsReporter @Inject constructor() : NetworkMetricsReporter {
    override fun reportOverview(metric: NetworkOverviewMetric) {
        Timber.tag("NetOverview").d(
            "method=%s host=%s path=%s code=%s success=%s durationMs=%d cacheHit=%s fromNetwork=%s cachePolicy=%s errorType=%s",
            metric.method,
            metric.host,
            metric.path,
            metric.code?.toString() ?: "null",
            metric.success,
            metric.durationMs,
            metric.cacheHit,
            metric.fromNetwork,
            metric.cachePolicy ?: "null",
            metric.errorType ?: "null"
        )
    }
}
