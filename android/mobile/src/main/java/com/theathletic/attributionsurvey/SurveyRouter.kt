package com.theathletic.attributionsurvey

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.featureswitches.FeatureSwitch
import com.theathletic.featureswitches.FeatureSwitches
import com.theathletic.onboarding.data.OnboardingRepository
import com.theathletic.utility.AttributionPreferences
import timber.log.Timber

class SurveyRouter @AutoKoin constructor(
    private val preferences: AttributionPreferences,
    private val onboardingRepository: OnboardingRepository,
    private val featureSwitches: FeatureSwitches
) {

    suspend fun fetchSurveyIfQualified() {
        if (shouldFetchSurvey()) {
            onboardingRepository.fetchSurvey()
                .onSuccess { Timber.i("Fetched user attribution survey") }
                .onError { e -> Timber.e("Error fetching user attribution survey") }
        } else {
            Timber.d("User not qualified for survey")
        }
    }

    private fun shouldFetchSurvey(): Boolean {
        return isEligibleForSurvey()
    }

    fun shouldPresentSurvey(): Boolean {
        return isEligibleForSurvey() && preferences.hasMadePurchaseForSurvey
    }

    fun hasMadeSuccessfulPurchase() {
        preferences.hasMadePurchaseForSurvey = true
    }

    private fun isEligibleForSurvey(): Boolean {
        return preferences.hasBeenEligibleForSurvey &&
            !preferences.hasSeenAttributionSurvey &&
            featureSwitches.isFeatureEnabled(FeatureSwitch.ATTRIBUTION_SURVEY)
    }
}