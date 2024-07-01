package com.theathletic.links

import com.theathletic.analytics.IAnalytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Exposes

interface LinkAnalytics {
    fun trackDeeplink(url: String, isSuccess: Boolean)
    fun trackUniversalLink(url: String, isSuccess: Boolean)
}

@Exposes(LinkAnalytics::class)
internal class LinkAnalyticsImpl @AutoKoin constructor(
    val analytics: IAnalytics
) : LinkAnalytics {
    override fun trackDeeplink(url: String, isSuccess: Boolean) {
        if (isSuccess) {
            analytics.track(Event.Link.NavigateDeepLinkSuccess(url = url))
        } else {
            analytics.track(Event.Link.NavigateDeepLinkFailure(url = url))
        }
    }

    override fun trackUniversalLink(url: String, isSuccess: Boolean) {
        if (isSuccess) {
            analytics.track(Event.Link.NavigateUniversalLinkSuccess(url = url))
        } else {
            analytics.track(Event.Link.NavigateUniversalLinkFailure(url = url))
        }
    }
}