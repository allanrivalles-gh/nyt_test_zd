package com.theathletic.onboarding

import com.theathletic.followable.Followable
import com.theathletic.followables.data.UserFollowingRepository
import com.theathletic.onboarding.data.OnboardingRepository
import com.theathletic.test.runTest
import com.theathletic.user.data.remote.PrivacyAcknowledgmentScheduler
import com.theathletic.utility.OnboardingPreferences
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class CompleteOnboardingUseCaseTest {

    private val onboardingPreferences = mockk<OnboardingPreferences>(relaxed = true)
    private val userFollowingRepository = mockk<UserFollowingRepository>(relaxed = true)
    private val onboardingRepository = mockk<OnboardingRepository>(relaxed = true)
    private val privacyAcknowledgmentScheduler = mockk<PrivacyAcknowledgmentScheduler>(relaxed = true)

    private lateinit var useCase: CompleteOnboardingUseCase

    @Before
    fun setup() {
        useCase = CompleteOnboardingUseCase(
            onboardingPreferences = onboardingPreferences,
            userFollowingRepository = userFollowingRepository,
            onboardingRepository = onboardingRepository,
            privacyAcknowledgmentScheduler = privacyAcknowledgmentScheduler
        )
    }

    @Test
    fun `should save chosen teams and leagues to server`() = runTest {
        val teamId = Followable.Id("1", Followable.Type.TEAM)
        val leagueId = Followable.Id("1", Followable.Type.LEAGUE)
        every { onboardingPreferences.chosenFollowables } returns listOf(teamId, leagueId)
        useCase()

        coVerify(exactly = 1) { userFollowingRepository.followItem(teamId) }
        coVerify(exactly = 1) { userFollowingRepository.followItem(leagueId) }
    }

    @Test
    fun `should save chosen podcasts to server`() = runTest {
        val podcastId = "1"
        every { onboardingPreferences.chosenPodcasts } returns listOf(podcastId)
        useCase()

        coVerify(exactly = 1) { onboardingRepository.saveFollowedPodcasts(listOf(podcastId)) }
    }

    @Test
    fun `should clear onboarding state`() = runTest {
        useCase()

        verify { onboardingPreferences.isOnboarding = false }
    }
}