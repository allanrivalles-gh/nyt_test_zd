package com.theathletic.feed.compose

import com.theathletic.ads.AdAnalytics
import com.theathletic.ads.BuildConfig
import com.theathletic.ads.repository.AdsRepository
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.feed.compose.data.FeedType
import com.theathletic.feed.compose.ui.ads.FeedAdsPage

internal class TrackAdsAnalyticsUseCase @AutoKoin constructor(
    private val adAnalytics: AdAnalytics,
    private val adsRepository: AdsRepository,
) {
    suspend operator fun invoke(page: FeedAdsPage) {
        val analyticsView = page.feedType.adsAnalyticsView
        if (analyticsView == null) {
            if (BuildConfig.DEBUG) {
                TODO("It is necessary to add support for the feed type ${page.feedType}.")
            }
            return
        }

        adAnalytics.trackAdPageView(pageViewId = page.pageViewId, view = analyticsView)

        adsRepository.observeAdEvents(pageViewId = page.pageViewId).collect { adEvent ->
            adAnalytics.trackAdEvent(pageViewId = page.pageViewId, view = analyticsView, event = adEvent)
        }
    }
}

internal val FeedType.adsAnalyticsView: String?
    get() = when (this) {
        FeedType.LEAGUE -> "leagues"
        FeedType.TEAM -> "teams"
        FeedType.AUTHOR -> "author"
        FeedType.DISCOVER -> "front_page"
        FeedType.FOLLOWING -> "home"
        FeedType.PLAYER, FeedType.TAG, FeedType.TEAM_PODCASTS -> null
    }