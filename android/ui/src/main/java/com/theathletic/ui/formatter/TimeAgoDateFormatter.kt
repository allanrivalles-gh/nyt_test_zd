package com.theathletic.ui.formatter

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.Chronos
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.formatter.DateFormatterWithParams
import com.theathletic.datetime.formatter.DisplayFormat
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asResourceString
import com.theathletic.utility.datetime.DateUtilityImpl

/**
 * Dates using this formatter render as:
 *
 * Just Now
 * 2m ago
 * 5h ago
 * 3d ago
 * Nov 7
 * Nov 7, 2020
 */
class TimeAgoDateFormatter @AutoKoin constructor(
    private val chronos: Chronos
) : DateFormatterWithParams<TimeAgoDateFormatter.Params> {

    data class Params(
        /**
         * Use "Now" when time is <2 minutes from the current time.
         */
        val showAgo: Boolean = true,
    )

    fun format(datetime: Datetime) = format(datetime, Params())

    override fun format(datetime: Datetime, params: Params): ResourceString {
        val timeDiff = chronos.timeDiff(to = datetime)

        return when {
            timeDiff.inMinutes < 2 -> ResourceString.StringWithParams(R.string.time_ago_just_now)
            timeDiff.inHours < 1 ->
                ResourceString.StringWithParams(
                    if (params.showAgo) R.string.time_minutes_ago else R.string.global_time_m_span,
                    timeDiff.inMinutes
                )
            timeDiff.inHours < 24 ->
                ResourceString.StringWithParams(
                    if (params.showAgo) R.string.time_hours_ago else R.string.global_time_h_span,
                    timeDiff.inHours
                )
            timeDiff.inDays < 7 ->
                ResourceString.StringWithParams(
                    if (params.showAgo) R.string.time_days_ago else R.string.global_time_d_span,
                    timeDiff.inDays
                )

            chronos.isThisYear(datetime.timeMillis) -> DateUtilityImpl.formatGMTDate(
                datetime.timeMillis,
                DisplayFormat.MONTH_DATE_SHORT
            ).asResourceString()

            else -> DateUtilityImpl.formatGMTDate(
                datetime.timeMillis,
                DisplayFormat.MONTH_DATE_YEAR_SHORT
            ).asResourceString()
        }
    }
}