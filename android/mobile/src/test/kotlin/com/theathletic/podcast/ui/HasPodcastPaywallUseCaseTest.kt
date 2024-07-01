package com.theathletic.podcast.ui

import com.google.common.truth.Truth.assertThat
import com.theathletic.utility.PaywallUtility
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import org.junit.Before

class HasPodcastPaywallUseCaseTest {
    private lateinit var hasPodcastPaywallUseCase: HasPodcastPaywallUseCase
    private val paywallUtility = mockk<PaywallUtility>(relaxed = true)

    @Before
    fun setUp() {
        hasPodcastPaywallUseCase = HasPodcastPaywallUseCase(paywallUtility)
    }

    @Test
    fun `returns false when podcast is a teaser and user should see paywall`() {
        every { paywallUtility.shouldUserSeePaywall() } returns true
        assertThat(hasPodcastPaywallUseCase.invoke(isTeaser = true)).isFalse()
    }

    @Test
    fun `returns false when podcast is not a teaser and user should see paywall`() {
        every { paywallUtility.shouldUserSeePaywall() } returns true
        assertThat(hasPodcastPaywallUseCase.invoke(isTeaser = false)).isTrue()
    }

    @Test
    fun `returns false when podcast is a teaser and user should not see paywall`() {
        every { paywallUtility.shouldUserSeePaywall() } returns false
        assertThat(hasPodcastPaywallUseCase.invoke(isTeaser = true)).isFalse()
    }

    @Test
    fun `returns true when podcast is not a teaser and user should not see paywall`() {
        every { paywallUtility.shouldUserSeePaywall() } returns false
        assertThat(hasPodcastPaywallUseCase.invoke(isTeaser = false)).isFalse()
    }
}