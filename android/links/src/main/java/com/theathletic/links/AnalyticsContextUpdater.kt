package com.theathletic.links

import com.theathletic.analytics.newarch.context.DeepLinkParams

interface AnalyticsContextUpdater {
    fun updateContext(deepLinkParams: DeepLinkParams? = null)
}