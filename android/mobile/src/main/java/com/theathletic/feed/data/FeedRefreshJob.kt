package com.theathletic.feed.data

import com.theathletic.ads.shouldDisplayAds
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.DateUtility
import com.theathletic.featureswitch.Features
import com.theathletic.feed.FeedType
import com.theathletic.main.DeeplinkThrottle
import com.theathletic.utility.FeedPreferences
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Job
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber

@Suppress("LongParameterList")
class FeedRefreshJob @AutoKoin constructor(
    private val dateUtility: DateUtility,
    private val feedPreferences: FeedPreferences,
    private val feedRepository: FeedRepository,
    private val deeplinkThrottle: DeeplinkThrottle,
    private val features: Features
) {
    private var fetchJob: Job? = null
    private var navJob: Job? = null

    companion object {
        const val REFRESH_TIME_MINUTES = 5L
    }

    suspend fun prefetchFeed(
        feedType: FeedType,
        timeoutMillis: Long
    ) {
        withTimeoutOrNull(timeoutMillis) {
            fetchFeed(feedType)
        }
    }

    suspend fun fetchFeed(
        feedType: FeedType
    ) {
        cancelJobs()
        Timber.d("[push]: fetchFeedAndNav($feedType)")
        val fetchRequest = feedRepository.fetchFeed(
            feedType = feedType,
            forceRefresh = true,
            page = 0,
            isAdsEnabled = feedType.shouldDisplayAds(features)
        )
        fetchJob = fetchRequest.apply { join() }
    }

    private fun cancelJobs() {
        if (fetchJob?.isActive == true) {
            fetchJob?.cancel()
        }
        if (navJob?.isActive == true) {
            navJob?.cancel()
        }
    }

    fun shouldRefreshFeed(
        feedType: FeedType,
        stalenessMillis: Long = TimeUnit.MINUTES.toMillis(REFRESH_TIME_MINUTES)
    ): Boolean {
        return when {
            features.shouldPreventFeedRefreshOnPush && deeplinkThrottle.isThrottled() -> false
            dateUtility.isInPastMoreThan(
                feedPreferences.getFeedLastFetchDate(feedType),
                stalenessMillis
            ) -> true
            else -> false
        }
    }
}