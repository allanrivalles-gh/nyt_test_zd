package com.theathletic.news

import android.content.Context
import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.datetime.TimeProvider
import com.theathletic.datetime.formatter.DisplayFormat
import com.theathletic.extension.extGetString
import com.theathletic.utility.datetime.DateUtilityImpl
import java.util.Date
import java.util.Locale

class NewsUtils @AutoKoin(Scope.SINGLE) constructor(
    private val context: Context,
    private val timeProvider: TimeProvider
) {

    companion object {
        private const val ONE_HOUR = 60
        private const val ONE_DAY = ONE_HOUR * 24
        private const val ONE_WEEK = ONE_DAY * 7
    }

    private enum class TimeUnitFormat {
        SHORT, // m,h,d
        LONG // minutes, hours, days
    }

    private fun calculateLastUpdated(
        updatedAt: String,
        timeFormat: TimeUnitFormat,
        appendAgo: Boolean,
        lowercase: Boolean = false
    ): String {
        val updated = (updatedAt.toLong())
        var diffInMins = (timeProvider.currentTimeMs - updated) / 1000 / 60
        if (diffInMins < 0) diffInMins = 0
        val agoText = if (appendAgo) " ${context.getString(R.string.news_container_age)}" else ""
        return when {

            // Minutes
            diffInMins < ONE_HOUR -> formatForMinutes(diffInMins, timeFormat, agoText, lowercase)

            // Hours
            diffInMins < ONE_DAY -> formatForHours(diffInMins, timeFormat, agoText, lowercase)

            // Days
            diffInMins < ONE_WEEK -> formatForDays(diffInMins, timeFormat, agoText, lowercase)

            // Over a week
            else -> {
                val formatted = DateUtilityImpl.formatGMTDate(Date(updated), DisplayFormat.MONTH_DATE_SHORT)
                if (lowercase) formatted else formatted.toUpperCase(Locale.getDefault())
            }
        }
    }

    private fun formatForMinutes(
        diffInMins: Long,
        timeFormat: TimeUnitFormat,
        agoText: String,
        lowercase: Boolean
    ): String {
        if (diffInMins == 0L) {
            return R.string.time_ago_just_now.extGetString()
        } else {
            val symbol = when (timeFormat) {
                TimeUnitFormat.SHORT -> {
                    R.string.global_time_m.extGetString().toUpperCase(Locale.getDefault())
                }

                TimeUnitFormat.LONG -> {
                    if (diffInMins == 1L) {
                        " " + R.string.global_time_minute.extGetString().toUpperCase(Locale.getDefault())
                    } else {
                        " " + R.string.global_time_minutes.extGetString().toUpperCase(Locale.getDefault())
                    }
                }
            }
            var formatted = "$diffInMins$symbol$agoText"
            if (lowercase) formatted = formatted.toLowerCase(Locale.getDefault())
            return formatted
        }
    }

    private fun formatForHours(
        diffInMins: Long,
        timeFormat: TimeUnitFormat,
        agoText: String,
        lowercase: Boolean
    ): String {
        val diffInHours = diffInMins / ONE_HOUR
        val symbol = when (timeFormat) {
            TimeUnitFormat.SHORT -> {
                R.string.global_time_h.extGetString().toUpperCase(Locale.getDefault())
            }

            TimeUnitFormat.LONG -> {
                if (diffInHours == 1L) {
                    " " + R.string.global_time_hour.extGetString().toUpperCase(Locale.getDefault())
                } else {
                    " " + R.string.global_time_hours.extGetString().toUpperCase(Locale.getDefault())
                }
            }
        }
        var formatted = "$diffInHours$symbol$agoText"
        if (lowercase) formatted = formatted.toLowerCase(Locale.getDefault())
        return formatted
    }

    private fun formatForDays(
        diffInMins: Long,
        timeFormat: TimeUnitFormat,
        agoText: String,
        lowercase: Boolean
    ): String {
        val diffInDays = diffInMins / ONE_DAY
        val symbol = when (timeFormat) {
            TimeUnitFormat.SHORT -> {
                R.string.global_day_d.extGetString().toUpperCase(Locale.getDefault())
            }

            TimeUnitFormat.LONG -> {
                if (diffInDays == 1L) {
                    " " + R.string.global_day_day.extGetString().toUpperCase(Locale.getDefault())
                } else {
                    " " + R.string.global_day_days.extGetString().toUpperCase(Locale.getDefault())
                }
            }
        }
        var formatted = "$diffInDays$symbol$agoText"
        if (lowercase) formatted = formatted.toLowerCase(Locale.getDefault())
        return formatted
    }

    fun formatShortAge(updatedAt: String, lowercase: Boolean = false): String {
        return calculateLastUpdated(updatedAt, TimeUnitFormat.SHORT, false, lowercase)
    }

    fun formatShortFormAge(updatedAt: String): String {
        return calculateLastUpdated(updatedAt, TimeUnitFormat.SHORT, true, lowercase = true)
    }
}