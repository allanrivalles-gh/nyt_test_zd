package com.theathletic.datetime

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val GMT_FORMAT = "yyyy-MM-dd HH:mm:ss"
private const val TIMEZONE_GMT = "GMT"
private const val GMT_SHORT_FORMAT = "yyyy-MM-dd"

private val gmtFormatter: SimpleDateFormat
    get() = SimpleDateFormat(GMT_FORMAT, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone(TIMEZONE_GMT)
    }

fun Date.asGMTString(): String = gmtFormatter.format(this)

fun Long.asGMTString(): String = gmtFormatter.format(this)

@Suppress("ReturnCount")
fun parseDateFromGMT(date: String?): Date {
    date?.let {
        try {
            val originalFormat = SimpleDateFormat(GMT_FORMAT, Locale.getDefault())
            originalFormat.timeZone = TimeZone.getTimeZone(TIMEZONE_GMT)
            originalFormat.parse(it)?.let { parsedDate ->
                return parsedDate
            }
        } catch (ignored: Exception) {
        }
        try {
            val originalFormat = SimpleDateFormat(GMT_SHORT_FORMAT, Locale.getDefault())
            originalFormat.timeZone = TimeZone.getTimeZone(TIMEZONE_GMT)
            originalFormat.parse(it)?.let { parsedDate ->
                return parsedDate
            }
        } catch (ignored: Exception) {
        }
    }
    return Date().apply { time = 0 }
}

/*
    Converts a Datetime value to String with the format yyyy-MM-dd
 */
fun Datetime.asLocalShortString(): String =
    SimpleDateFormat(GMT_SHORT_FORMAT, Locale.getDefault()).format(timeMillis)