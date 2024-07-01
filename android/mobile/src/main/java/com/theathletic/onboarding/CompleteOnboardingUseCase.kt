package com.theathletic.onboarding

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.followable.FollowableId
import com.theathletic.followables.data.UserFollowingRepository
import com.theathletic.onboarding.data.OnboardingRepository
import com.theathletic.user.data.remote.PrivacyAcknowledgmentScheduler
import com.theathletic.utility.OnboardingPreferences

class CompleteOnboardingUseCase @AutoKoin constructor(
    private val onboardingPreferences: OnboardingPreferences,
    private val userFollowingRepository: UserFollowingRepository,
    private val onboardingRepository: OnboardingRepository,
    private val privacyAcknowledgmentScheduler: PrivacyAcknowledgmentScheduler
) {
    suspend operator fun invoke() {
        saveFollowedItemsToServer(onboardingPreferences.chosenFollowables)
        saveFollowedPodcastsToServer(onboardingPreferences.chosenPodcasts)
        onboardingPreferences.apply {
            isOnboarding = false
            chosenFollowables = emptyList()
            chosenPodcasts = emptyList()
        }
        privacyAcknowledgmentScheduler.schedule()
    }

    private suspend fun saveFollowedItemsToServer(followedItems: List<FollowableId>) {
        // Note: Doing follows one-at-a-time makes it so the newly selected follows will automatically be merged
        // with existing ones in case the user who is onboarding logs into a pre-existing account
        followedItems.forEach() { userFollowingRepository.followItem(it) }
    }

    private suspend fun saveFollowedPodcastsToServer(podcastIds: List<String>) {
        onboardingRepository.saveFollowedPodcasts(podcastIds)
    }
}