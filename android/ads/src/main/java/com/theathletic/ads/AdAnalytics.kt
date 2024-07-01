package com.theathletic.ads

import com.theathletic.ads.bridge.data.local.AdEvent

interface AdAnalytics {
    fun trackAdEvent(pageViewId: String, view: String, event: AdEvent)
    fun trackAdPageView(pageViewId: String, view: String)
}