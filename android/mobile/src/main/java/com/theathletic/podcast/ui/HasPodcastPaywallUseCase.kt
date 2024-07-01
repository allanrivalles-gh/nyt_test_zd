package com.theathletic.podcast.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.utility.PaywallUtility

class HasPodcastPaywallUseCase @AutoKoin constructor(
    private val paywallUtility: PaywallUtility
) {
    operator fun invoke(isTeaser: Boolean) =
        isTeaser.not() && paywallUtility.shouldUserSeePaywall()
}