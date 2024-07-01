package com.theathletic.profile.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.TimeCalculator
import com.theathletic.datetime.TimeProvider
import com.theathletic.utility.ProfileBadgingPreferences

class ProfileBadger @AutoKoin constructor(
    private val preferences: ProfileBadgingPreferences,
    private val timeProvider: TimeProvider,
    private val timeCalculator: TimeCalculator
) {

    companion object {
        const val PODCAST_DISCOVER_SHOW_AFTER_DAYS = 30
    }

    fun shouldShowPodcastDiscoverBadge(): Boolean {
        val lastClickDatetime = preferences.podcastDiscoverBadgeLastClick
        val timeDiff = timeCalculator.timeDiffFromNow(lastClickDatetime)

        return timeDiff.inDays >= PODCAST_DISCOVER_SHOW_AFTER_DAYS
    }

    fun resetPodcastDiscoverBadge() {
        preferences.podcastDiscoverBadgeLastClick = timeProvider.currentDatetime
    }
}