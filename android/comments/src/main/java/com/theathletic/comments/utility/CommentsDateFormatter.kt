package com.theathletic.comments.utility

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.comments.R
import com.theathletic.datetime.DateUtility
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.TimeDiff
import com.theathletic.datetime.TimeProvider
import com.theathletic.datetime.formatter.DateFormatter
import com.theathletic.datetime.formatter.DisplayFormat
import com.theathletic.ui.ResourceString

class CommentsDateFormatter @AutoKoin constructor(
    private val timeProvider: TimeProvider,
    private val dateUtility: DateUtility
) : DateFormatter {
    override fun format(formattable: Datetime): ResourceString {
        val timeDiff = TimeDiff.fromNow(formattable, timeProvider)
        val timeMillis = formattable.timeMillis

        return when {
            timeDiff.inMinutes < 2 -> ResourceString.StringWithParams(R.string.plural_time_now)
            timeDiff.inHours < 1 -> ResourceString.StringWithParams(R.string.global_time_m_span, timeDiff.inMinutes)
            timeDiff.inHours < 24 -> ResourceString.StringWithParams(R.string.global_time_h_span, timeDiff.inHours)
            timeDiff.inDays < 2 -> ResourceString.StringWithParams(R.string.global_date_yesterday)

            dateUtility.isThisYear(timeMillis) -> ResourceString.StringWrapper(
                dateUtility.formatGMTDate(Datetime(timeMillis), DisplayFormat.MONTH_DATE_SHORT)
            )

            else -> ResourceString.StringWrapper(
                dateUtility.formatGMTDate(Datetime(timeMillis), DisplayFormat.MONTH_DATE_YEAR_SHORT)
            )
        }
    }
}