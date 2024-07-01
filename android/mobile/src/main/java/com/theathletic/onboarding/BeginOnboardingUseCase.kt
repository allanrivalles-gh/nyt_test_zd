package com.theathletic.onboarding

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.utility.OnboardingPreferences

class BeginOnboardingUseCase @AutoKoin constructor(
    private val onboardingPreferences: OnboardingPreferences
) {
    operator fun invoke() {
        onboardingPreferences.isOnboarding = true
    }
}