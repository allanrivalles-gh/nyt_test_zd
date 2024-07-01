package com.theathletic.analytics

import com.theathletic.ads.AdAnalytics
import com.theathletic.ads.bridge.data.local.AdEvent
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Exposes

@Exposes(AdAnalytics::class)
class AdAnalyticsHandler @AutoKoin constructor(
    val analytics: Analytics
) : AdAnalytics {

    override fun trackAdEvent(pageViewId: String, view: String, event: AdEvent) {
        when (event) {
            is AdEvent.AdRequest -> {
                analytics.track(Event.Ads.AdRequest(view = view, ad_view_id = pageViewId, pos = event.pos ?: ""))
            }
            is AdEvent.AdResponseSuccess -> {
                analytics.track(
                    Event.Ads.AdResponseSuccess(
                        view = view,
                        ad_view_id = pageViewId,
                        pos = event.pos ?: ""
                    )
                )
            }
            is AdEvent.AdResponseFail -> {
                analytics.track(
                    Event.Ads.AdResponseFailed(
                        view = view,
                        ad_view_id = pageViewId,
                        pos = event.pos ?: "",
                        error = event.error
                    )
                )
            }
            is AdEvent.AdNoFill -> {
                analytics.track(Event.Ads.AdNoFill(view = view, ad_view_id = pageViewId, pos = event.pos ?: ""))
            }
            else -> {
                // no-op
            }
        }
    }

    override fun trackAdPageView(pageViewId: String, view: String) {
        analytics.track(Event.Ads.AdPageView(view = view, ad_view_id = pageViewId))
    }
}