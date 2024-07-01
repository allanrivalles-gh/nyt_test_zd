package com.theathletic.feed.compose.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.TimeProvider
import com.theathletic.scores.data.local.GameState
import java.util.concurrent.TimeUnit

class GameSubscriptionHelper @AutoKoin constructor(
    private val timeProvider: TimeProvider,
) {
    fun isGameLiveOrAboutToStart(state: GameState, scheduledAt: Datetime?): Boolean {
        scheduledAt ?: return false
        return when (state) {
            GameState.LIVE -> true
            GameState.UPCOMING -> isLeadingUpToGameStart(scheduledAt)
            else -> false
        }
    }

    private fun isLeadingUpToGameStart(scheduleAt: Datetime): Boolean {
        val now = timeProvider.currentTimeMs
        val prior = scheduleAt.timeMillis - TimeUnit.MINUTES.toMillis(15)
        val overlap = scheduleAt.timeMillis + TimeUnit.MINUTES.toMillis(30)
        return now in prior..overlap
    }
}