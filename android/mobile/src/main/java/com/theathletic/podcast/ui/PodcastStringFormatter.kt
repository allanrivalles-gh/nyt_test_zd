package com.theathletic.podcast.ui

import android.content.Context
import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import java.util.concurrent.TimeUnit

class PodcastStringFormatter @AutoKoin(Scope.SINGLE) constructor(
    val context: Context
) {

    /**
     * Gives short duration in hours and minutes. Excludes hour if duration is under an hour.
     * e.g. 1h 22m
     * e.g. 33m
     */
    fun formatTinyPlayerDuration(durationSeconds: Long): String {
        val totalMinutes = TimeUnit.SECONDS.toMinutes(durationSeconds)

        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        return when {
            hours >= 1 -> context.getString(R.string.podcast_tiny_player_duration_over_hour, hours, minutes)
            else -> context.getString(R.string.podcast_tiny_player_duration_under_hour, minutes)
        }
    }
}