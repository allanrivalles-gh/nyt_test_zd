package com.theathletic.profile.ui

import com.theathletic.datetime.Datetime
import com.theathletic.datetime.TimeCalculator
import com.theathletic.datetime.TimeDiff
import com.theathletic.datetime.TimeProvider
import com.theathletic.utility.ProfileBadgingPreferences
import java.util.concurrent.TimeUnit
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ProfileBadgerTest {

    private lateinit var profileBadger: ProfileBadger

    @Mock lateinit var preferences: ProfileBadgingPreferences
    @Mock lateinit var timeProvider: TimeProvider
    @Mock lateinit var timeCalculator: TimeCalculator

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        profileBadger = ProfileBadger(preferences, timeProvider, timeCalculator)
    }

    @Test
    fun `Discover Podcast badge shows 30 days after last event`() {
        whenever(preferences.podcastDiscoverBadgeLastClick).thenReturn(Datetime(0))
        whenever(timeCalculator.timeDiffFromNow(any()))
            .thenReturn(TimeDiff(TimeUnit.DAYS.toMillis(30)))

        assertTrue(profileBadger.shouldShowPodcastDiscoverBadge())
    }

    @Test
    fun `Discover Podcast badge does not show 29 days after last event`() {
        whenever(preferences.podcastDiscoverBadgeLastClick).thenReturn(Datetime(0))
        whenever(timeCalculator.timeDiffFromNow(any()))
            .thenReturn(TimeDiff(TimeUnit.DAYS.toMillis(29)))

        assertFalse(profileBadger.shouldShowPodcastDiscoverBadge())
    }

    @Test
    fun `Reset Discover Podcast badge updates preferences when reset called`() {
        val today = Datetime(System.currentTimeMillis())

        whenever(preferences.podcastDiscoverBadgeLastClick).thenReturn(Datetime(0))
        whenever(timeProvider.currentDatetime).thenReturn(today)
        whenever(timeCalculator.timeDiffFromNow(any()))
            .thenReturn(TimeDiff(TimeUnit.DAYS.toMillis(30)))

        assertTrue(profileBadger.shouldShowPodcastDiscoverBadge())

        profileBadger.resetPodcastDiscoverBadge()

        verify(preferences).podcastDiscoverBadgeLastClick = today
    }
}