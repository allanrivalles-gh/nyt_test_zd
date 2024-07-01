package com.theathletic.datetime.formatter

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.TimeDiff
import com.theathletic.datetime.TimeProvider
import com.theathletic.feed.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams

/**
 * Dates using this formatter render as:
 *
 * Now
 * 1 min
 * 30 min
 * empty
 */
class LastActivityDateFormatter @AutoKoin(Scope.SINGLE) constructor(
    private val timeProvider: TimeProvider
) : DateFormatter {
    override fun format(formattable: Datetime): ResourceString {
        val timeDiff = TimeDiff.fromNow(formattable, timeProvider)

        return when {
            timeDiff.inMinutes < 1 -> StringWithParams(R.string.plural_time_now)
            timeDiff.inMinutes <= 30 -> StringWithParams(R.string.time_min, timeDiff.inMinutes)
            else -> StringWithParams(R.string.empty_string)
        }
    }
}