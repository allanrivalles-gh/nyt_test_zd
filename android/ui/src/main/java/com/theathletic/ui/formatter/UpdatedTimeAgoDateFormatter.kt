package com.theathletic.ui.formatter

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.Chronos
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.formatter.DateFormatter
import com.theathletic.datetime.formatter.DisplayFormat
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asResourceString
import com.theathletic.utility.datetime.DateUtilityImpl

/**
 * Dates using this formatter render as:
 *
 * Updated now
 * Updated 2m ago
 * Updated 5h ago
 * Updated 3d ago
 * Mon, Nov 7
 * Nov 7, 2020
 */
class UpdatedTimeAgoDateFormatter @AutoKoin constructor(private val chronos: Chronos) : DateFormatter {

    override fun format(datetime: Datetime): ResourceString {
        val timeDiff = chronos.timeDiff(to = datetime)

        return when {
            timeDiff.inMinutes < 2 -> ResourceString.StringWithParams(R.string.time_now_updated)
            timeDiff.inHours < 1 -> ResourceString.StringWithParams(R.string.time_minutes_ago_updated, timeDiff.inMinutes)
            timeDiff.inHours < 24 -> ResourceString.StringWithParams(R.string.time_hours_ago_updated, timeDiff.inHours)
            timeDiff.inDays < 7 -> ResourceString.StringWithParams(R.string.time_days_ago_updated, timeDiff.inDays)

            DateUtilityImpl.isThisYear(datetime.timeMillis) -> DateUtilityImpl.formatGMTDate(
                datetime.timeMillis,
                DisplayFormat.WEEKDAY_MONTH_DATE_SHORT
            ).asResourceString()

            else -> DateUtilityImpl.formatGMTDate(
                datetime.timeMillis,
                DisplayFormat.MONTH_DATE_YEAR_SHORT
            ).asResourceString()
        }
    }
}