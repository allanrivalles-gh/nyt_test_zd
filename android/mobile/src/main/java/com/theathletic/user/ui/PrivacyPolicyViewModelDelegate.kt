package com.theathletic.user.ui

import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.TimeCalculator
import com.theathletic.datetime.TimeProvider
import com.theathletic.featureswitches.FeatureSwitch
import com.theathletic.featureswitches.FeatureSwitches
import com.theathletic.user.IUserManager
import com.theathletic.user.data.remote.PrivacyAcknowledgmentScheduler
import com.theathletic.utility.LocaleUtility
import com.theathletic.utility.PrivacyPreferences
import com.theathletic.utility.PrivacyRegion

@Suppress("LongParameterList")
class PrivacyPolicyViewModelDelegate @AutoKoin constructor(
    private val featureSwitches: FeatureSwitches,
    private val localeUtility: LocaleUtility,
    private val preferences: PrivacyPreferences,
    private val timeCalculator: TimeCalculator,
    private val userManager: IUserManager,
    private val timeProvider: TimeProvider,
    private val privacyAcknowledgmentScheduler: PrivacyAcknowledgmentScheduler,
    private val analytics: Analytics
) : PrivacyInteractor {
    fun shouldPresentPrivacyRefresh(): Boolean {
        return featureSwitches.isFeatureEnabled(FeatureSwitch.PRIVACY_REFRESH_DIALOG_ENABLED) &&
            hasNotSeenPrivacyRefreshInPastDay() &&
            userHasNotAcceptedPrivacyPolicy()
    }

    private fun hasNotSeenPrivacyRefreshInPastDay(): Boolean {
        return timeCalculator.timeDiffFromNow(preferences.privacyPolicyUpdateLastRequestedDate).inDays > 0
    }

    private fun userHasNotAcceptedPrivacyPolicy(): Boolean {
        return userManager.getCurrentUser()?.let {
            it.privacyPolicy == false && it.termsAndConditions == false
        } ?: true
    }

    val privacyRegion: PrivacyRegion
        get() = localeUtility.privacyRegion

    override fun onPrivacyAccepted() {
        privacyAcknowledgmentScheduler.schedule()
        analytics.track(Event.User.PrivacyAcknowledgment("click"))
    }

    override fun didDisplayPrivacyDialog() {
        preferences.privacyPolicyUpdateLastRequestedDate = Datetime(timeProvider.currentTimeMs)
        analytics.track(Event.User.PrivacyAcknowledgment("view"))
    }
}

interface PrivacyInteractor {
    fun onPrivacyAccepted()
    fun didDisplayPrivacyDialog()
}