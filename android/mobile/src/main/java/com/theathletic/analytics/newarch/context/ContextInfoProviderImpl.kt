package com.theathletic.analytics.newarch.context

import com.theathletic.analytics.newarch.context.ContextInfoProvider.ContextInfo
import com.theathletic.user.IUserManager
import com.theathletic.utility.LocaleUtilityImpl

/**
 * Simple interface to decouple the source of context information (e.g. UserManager) from the AnalyticsTracker
 */
interface ContextInfoProvider {
    data class ContextInfo(
        val deviceId: String,
        val userId: String,
        val isSubscriber: String,
        val userAgent: String,
        val locale: String,
        val deepLinkParams: DeepLinkParams? = null
    )

    fun buildContextInfo(): ContextInfo
}

class ContextInfoProviderImpl(
    private val injectedUserAgent: String,
    private val contextPreferences: ContextInfoPreferences,
    private val userManager: IUserManager
) : ContextInfoProvider {
    override fun buildContextInfo(): ContextInfo {
        return ContextInfo(
            userManager.getDeviceId() ?: "missing_device_id",
            userManager.getCurrentUserId().toString(),
            if (userManager.isUserSubscribed()) "1" else "0",
            injectedUserAgent,
            LocaleUtilityImpl.acceptLanguage,
            contextPreferences.analyticsDeeplinkParameters
        )
    }
}