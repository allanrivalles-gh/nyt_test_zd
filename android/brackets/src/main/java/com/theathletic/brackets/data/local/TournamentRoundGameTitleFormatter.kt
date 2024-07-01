package com.theathletic.brackets.data.local

import android.content.Context
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Exposes
import com.theathletic.bracket.R
import com.theathletic.datetime.TimeProvider
import com.theathletic.datetime.formatter.DisplayFormat
import com.theathletic.type.GameStatusCode
import com.theathletic.utility.datetime.DateUtilityImpl
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

interface TournamentRoundGameTitleFormatter {
    fun format(game: TournamentRoundGame): String
}

// todo: Decide if we are going allow the passing of context or use ResourceString. Ticket:ATH-22054
@Exposes(TournamentRoundGameTitleFormatter::class)
class TournamentRoundGameTitleFormatterImpl @AutoKoin constructor(
    private val context: Context,
    private val timeProvider: TimeProvider
) : TournamentRoundGameTitleFormatter {
    override fun format(game: TournamentRoundGame): String {
        return game.title(context, timeProvider)
    }
}

private fun TournamentRoundGame.title(context: Context, timeProvider: TimeProvider): String {
    if (isPlaceholder) return context.getString(R.string.bracket_unscheduled_game_title)

    val phaseTitle = phase?.titleForGame(this, context, timeProvider)

    val parts = mutableListOf<String>()
    if (phaseTitle != null) parts.add(phaseTitle)
    if (venueName != null) parts.add(venueName)
    return parts.joinToString(" ")
}

private fun TournamentRoundGame.Phase.titleForGame(
    game: TournamentRoundGame,
    context: Context,
    timeProvider: TimeProvider,
): String? {
    return when (this) {
        TournamentRoundGame.Phase.PostGame -> game.matchTitleString(context)
        TournamentRoundGame.Phase.InGame -> {
            if (game.status == GameStatusCode.in_progress) {
                return game.matchTimeDisplay
            }

            return game.status?.title(context)
        }
        TournamentRoundGame.Phase.PreGame -> {
            val possibleParts = mutableListOf<String?>()
            if (!game.isGameToday(timeProvider)) {
                possibleParts.add(game.dayOfWeekString())
            }
            if (game.isGameUpcoming(timeProvider)) {
                possibleParts.add(game.dateString())
            } else {
                possibleParts.add(game.timeString(context))
            }
            val parts = possibleParts.filterNotNull()
            if (parts.isEmpty()) return null
            return parts.joinToString(" ")
        }
    }
}

private fun TournamentRoundGame.dateString(): String? {
    return scheduledAt?.let {
        DateUtilityImpl.formatGMTDate(it, DisplayFormat.MONTH_DATE_SHORT) + ","
    }
}

private fun TournamentRoundGame.matchTitleString(context: Context): String {
    val parts = mutableListOf<String>()
    val dateString = dateString()
    parts.add(context.getString(R.string.game_status_final_title))
    if (dateString != null) parts.add(dateString)
    return parts.joinToString(", ")
}

private fun TournamentRoundGame.timeString(context: Context): String? {
    if (timeTbd) return context.getString(R.string.global_tbd)

    return scheduledAt?.let {
        DateUtilityImpl.formatGMTDate(it, DisplayFormat.HOURS_MINUTES)
            // should not have a space between the time (0:00) and the period (AM or PM)
            .replaceFirst(" ", "")
    }
}

private fun TournamentRoundGame.dayOfWeekString(): String? {
    return scheduledAt?.let {
        DateUtilityImpl.formatGMTDate(it, DisplayFormat.WEEKDAY_SHORT) + ","
    }
}

private fun TournamentRoundGame.isGameToday(timeProvider: TimeProvider): Boolean {
    return scheduledAt?.let { timeProvider.currentDate.isSameDayAs(Date(it)) } ?: false
}

private fun TournamentRoundGame.isGameUpcoming(timeProvider: TimeProvider): Boolean {
    return scheduledAt?.let { scheduledAt ->
        val difference = scheduledAt - timeProvider.currentTimeMs
        TimeUnit.MILLISECONDS.toDays(difference) > 7
    } ?: false
}

private fun GameStatusCode.title(context: Context): String? {
    return when (this) {
        GameStatusCode.scheduled -> context.getString(R.string.game_status_scheduled_title)
        GameStatusCode.in_progress -> context.getString(R.string.game_status_in_progress_title)
        GameStatusCode.final -> context.getString(R.string.game_status_final_title)
        GameStatusCode.suspended -> context.getString(R.string.game_status_suspended_title)
        GameStatusCode.postponed -> context.getString(R.string.game_status_postponed_title)
        GameStatusCode.cancelled -> context.getString(R.string.game_status_cancelled_title)
        GameStatusCode.delayed -> context.getString(R.string.game_status_delayed_title)
        GameStatusCode.if_necessary -> context.getString(R.string.game_status_if_necessary_title)
        GameStatusCode.unnecessary -> context.getString(R.string.game_status_unnecessary_title)
        else -> null
    }
}

private fun Date.isSameDayAs(other: Date): Boolean {
    val cal0 = Calendar.getInstance()
    val cal1 = Calendar.getInstance()
    cal0.time = this
    cal1.time = other
    return cal0.equals(cal1, listOf(Calendar.ERA, Calendar.YEAR, Calendar.DAY_OF_YEAR))
}

private fun Calendar.equals(other: Calendar, fields: Iterable<Int>): Boolean {
    for (field in fields) {
        if (get(field) != other.get(field)) return false
    }
    return true
}