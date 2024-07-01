package com.theathletic.datetime.formatter

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.TimeDiff
import com.theathletic.datetime.TimeProvider
import com.theathletic.ui.binding.ParameterizedString
import com.theathletic.ui.binding.asParameterized
import com.theathletic.utility.datetime.DateUtilityImpl

/**
 * Dates using this formatter render as:
 *
 * Now
 * 2m ago
 * 5h ago
 * 3d ago
 * Mon, Nov 7
 * Nov 7, 2020
 */
class TimeAgoShortDateFormatter @AutoKoin(Scope.SINGLE) constructor(
    private val timeProvider: TimeProvider
) : DateFormatterWithParams<TimeAgoShortDateFormatter.Params> {

    data class Params(
        /**
         * Use "Now" when time is <2 minutes from the current time.
         */
        val includeNowTag: Boolean = true,

        /**
         * Prefix the time string with "Updated".
         */
        val showUpdated: Boolean = false
    )

    fun format(datetime: Datetime) = format(datetime, Params())

    override fun format(
        datetime: Datetime,
        params: Params
    ): ParameterizedString {
        val timeDiff = TimeDiff.fromNow(datetime, timeProvider)

        return when {
            timeDiff.inMinutes < 2 && params.includeNowTag -> ParameterizedString(
                if (params.showUpdated) R.string.time_now_updated else R.string.plural_time_now
            )
            timeDiff.inHours < 1 -> ParameterizedString(
                if (params.showUpdated) R.string.time_minutes_ago_updated else R.string.time_minutes_ago,
                timeDiff.inMinutes
            )
            timeDiff.inHours < 24 -> ParameterizedString(
                if (params.showUpdated) R.string.time_hours_ago_updated else R.string.time_hours_ago,
                timeDiff.inHours
            )
            timeDiff.inDays < 7 -> ParameterizedString(
                if (params.showUpdated) R.string.time_days_ago_updated else R.string.time_days_ago,
                timeDiff.inDays
            )

            DateUtilityImpl.isThisYear(datetime.timeMillis) -> DateUtilityImpl.formatGMTDate(
                datetime.timeMillis,
                DisplayFormat.WEEKDAY_MONTH_DATE_SHORT
            ).asParameterized()

            else -> DateUtilityImpl.formatGMTDate(
                datetime.timeMillis,
                DisplayFormat.MONTH_DATE_YEAR_SHORT
            ).asParameterized()
        }
    }

    fun format(gmtString: String?): ParameterizedString {
        return format(Datetime(DateUtilityImpl.parseDateFromGMT(gmtString).time))
    }
}