package com.theathletic.utility

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.featureswitches.FeatureSwitch
import com.theathletic.featureswitches.FeatureSwitches
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.user.IUserManager
import timber.log.Timber

class AppRatingEngine @AutoKoin(Scope.SINGLE) constructor(
    private val preferences: IPreferences,
    private val userManager: IUserManager,
    private val featureSwitches: FeatureSwitches,
    private val userDataRepository: IUserDataRepository
) {
    private var disabled: Boolean = false
    var shouldTryRatingDialogue: Boolean = false

    fun disable() {
        disabled = true
        onRatingTrigger()
    }

    fun onRatingTrigger() {
        shouldTryRatingDialogue = when {
            disabled -> false
            shouldTryRatingDialogue -> true
            !featureSwitches.isFeatureEnabled(FeatureSwitch.APP_RATING_ENABLED) -> false
            !userManager.isUserSubscribed() -> false
            hasRatingQualifier() -> true
            hasReadArticleCountQualifier() -> true
            else -> false
        }
    }

    private fun hasRatingQualifier(): Boolean {
        val count = preferences.articlesRatings.filterValues { it == 3L }.count()
        Timber.d("[rating] shouldTryRatingDialog with $count awesome ratings")
        return count == 1 || (count - 1) % 3 == 0
    }

    private fun hasReadArticleCountQualifier(): Boolean {
        val count = userDataRepository.totalReadArticleCount
        Timber.d("[rating] shouldTryRatingDialog with $count read articles")
        return count > 0 && count % 10 == 0
    }
}