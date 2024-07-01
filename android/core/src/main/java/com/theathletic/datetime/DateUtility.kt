package com.theathletic.datetime

import com.theathletic.datetime.formatter.DisplayFormat
import java.util.Date

/**
 * Core DateUtility class that allows you to parse and format dates
 * in a generic format. For more specialized date formatting, consider
 * adding a DateFormatter implementation.
 */
interface DateUtility {
    fun isInFutureMoreThan(date: String?, milliseconds: Long): Boolean
    fun isInFutureMoreThan(date: Date?, milliseconds: Long): Boolean
    fun isInPastMoreThan(timeStamp: Long, milliseconds: Long): Boolean
    fun parseDateFromGMT(date: String?): Date
    fun formatGMTDate(datetime: Datetime, format: DisplayFormat): String
    fun formatGMTTimeAgo(dateString: String, includeNowTag: Boolean = true, short: Boolean = false): String
    fun formatGMTTimeAgo(date: Date, includeNowTag: Boolean = true, short: Boolean = false): String
    fun getStartOfDay(): Datetime
    @Deprecated(
        "This method is deprecated. Use `chronos.isThisYear(milliseconds)` instead.",
        ReplaceWith(
            expression = "chronos.isThisYear(milliseconds)",
            imports = ["com.theathletic.datetime.Chronos"]
        )
    )
    fun isThisYear(milliseconds: Long): Boolean
    fun getCurrentLocalDate(): String
}