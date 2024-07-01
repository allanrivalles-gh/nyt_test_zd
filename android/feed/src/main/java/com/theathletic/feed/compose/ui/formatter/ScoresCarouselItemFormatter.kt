package com.theathletic.feed.compose.ui.formatter

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.Chronos
import com.theathletic.datetime.DateUtility
import com.theathletic.datetime.formatter.DisplayFormat
import com.theathletic.feed.R
import com.theathletic.feed.compose.data.ScoresCarouselItem
import com.theathletic.scores.data.local.GameState
import com.theathletic.ui.ResourceString

internal class ScoresCarouselItemFormatter @AutoKoin constructor(
    private val chronos: Chronos,
    private val dateUtility: DateUtility,
) {
    fun formatFirstStatusString(game: ScoresCarouselItem.Game): ResourceString = game.run {
        when (state) {
            GameState.LIVE -> {
                if (statusDisplay.main != null) {
                    ResourceString.StringWrapper(statusDisplay.main)
                } else {
                    ResourceString.StringWithParams(R.string.scores_banner_status_live)
                }
            }
            else -> {
                scheduledAtDate ?: ResourceString.StringWrapper("")
            }
        }
    }

    fun formatSecondStatusString(game: ScoresCarouselItem.Game): ResourceString = game.run {
        when (state) {
            GameState.LIVE -> {
                ResourceString.StringWrapper(statusDisplay.extra ?: "")
            }
            GameState.FINAL -> {
                ResourceString.StringWrapper(
                    listOf(statusDisplay.main, statusDisplay.extra)
                        .mapNotNull { it }
                        .joinToString(" ")
                )
            }
            GameState.UPCOMING -> scheduledAtTime ?: ResourceString.StringWrapper("")
            else -> state.description ?: ResourceString.StringWrapper("")
        }
    }

    private val GameState.description: ResourceString?
        get() = when (this) {
            GameState.CANCELED -> R.string.game_detail_pre_game_canceled_label
            GameState.POSTPONED -> R.string.game_detail_pre_game_postponed_label
            GameState.SUSPENDED -> R.string.game_detail_pre_game_suspended_label
            GameState.IF_NECESSARY -> R.string.game_detail_pre_game_if_necessary_shorten_label
            GameState.DELAYED -> R.string.game_detail_delayed_label
            else -> null
        }?.let { ResourceString.StringWithParams(it) }

    private val ScoresCarouselItem.Game.scheduledAtTime: ResourceString?
        get() = if (timeTBD) {
            ResourceString.StringWithParams(R.string.global_tbd)
        } else {
            scheduledAt?.let { scheduledAt ->
                ResourceString.StringWrapper(
                    dateUtility.formatGMTDate(scheduledAt, DisplayFormat.HOURS_MINUTES)
                )
            }
        }

    private val ScoresCarouselItem.Game.scheduledAtDate: ResourceString?
        get() = scheduledAt?.let { scheduledAt ->
            when {
                chronos.isToday(scheduledAt.timeMillis) -> ResourceString.StringWithParams(
                    R.string.global_date_today
                )
                chronos.isWithinWeek(scheduledAt.timeMillis) -> ResourceString.StringWrapper(
                    dateUtility.formatGMTDate(scheduledAt, DisplayFormat.WEEKDAY_SHORT)
                )
                else -> ResourceString.StringWrapper(
                    dateUtility.formatGMTDate(scheduledAt, DisplayFormat.MONTH_DATE_SHORT)
                )
            }
        }
}