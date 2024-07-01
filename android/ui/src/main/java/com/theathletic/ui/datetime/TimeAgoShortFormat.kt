package com.theathletic.ui.datetime

import android.text.format.DateUtils
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.theathletic.datetime.Chronos
import com.theathletic.datetime.Datetime
import com.theathletic.ui.R
import com.theathletic.ui.utility.rememberKoin

/**
 * Dates using this formatter render as:
 *
 * Now
 * 2m ago
 * 5h ago
 * 3d ago
 * Mon, Nov 7
 * Nov 7, 2020
 *
 * It is a copy of [TimeAgoShortDateFormatter] but for use in @Composable functions.
 *
 * @param includeNowTag Use "Now" when time is <2 minutes from the current time.
 * @param includeTodayTag Use "Today" when time is within the last day
 * @param showUpdated Prefix the time string with "Updated".
 */
@Composable
fun Datetime.timeAgoShortFormat(
    includeNowTag: Boolean = false,
    includeTodayTag: Boolean = false,
    showUpdated: Boolean = false,
): String {
    val context = LocalContext.current
    val chronos = rememberKoin<Chronos>()
    val timeDiff = chronos.timeDiff(to = this)

    return when {
        includeNowTag && timeDiff.inMinutes < 2 -> stringResource(
            if (showUpdated) R.string.time_now_updated else R.string.plural_time_now
        )
        includeTodayTag && DateUtils.isToday(timeMillis) -> stringResource(
            if (showUpdated) R.string.global_date_updated_today else R.string.global_date_today,
        )
        timeDiff.inHours < 1 -> stringResource(
            if (showUpdated) R.string.time_minutes_ago_updated else R.string.time_minutes_ago,
            timeDiff.inMinutes
        )
        timeDiff.inHours < 24 -> stringResource(
            if (showUpdated) R.string.time_hours_ago_updated else R.string.time_hours_ago,
            timeDiff.inHours
        )
        timeDiff.inDays < 7 -> stringResource(
            if (showUpdated) R.string.time_days_ago_updated else R.string.time_days_ago,
            timeDiff.inDays
        )

        chronos.isThisYear(timeMillis) -> DateUtils.formatDateTime(
            context, timeMillis,
            DateUtils.FORMAT_SHOW_WEEKDAY or DateUtils.FORMAT_ABBREV_ALL or
                DateUtils.FORMAT_NO_YEAR or DateUtils.FORMAT_SHOW_DATE
        )

        else -> DateUtils.formatDateTime(
            context, timeMillis,
            DateUtils.FORMAT_SHOW_YEAR or
                DateUtils.FORMAT_ABBREV_ALL or DateUtils.FORMAT_SHOW_DATE
        )
    }
}