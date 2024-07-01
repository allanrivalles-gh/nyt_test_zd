package com.theathletic.utility.formatters

object StringFormatUtility {
    fun formatHoursMinutesSecondsTime(hours: Int, minutes: Int, seconds: Int): String {
        return (String.format("%d:%02d:%02d", hours, minutes, seconds))
    }

    fun formatHoursMinutesSecondsTimeRemaining(hours: Int, minutes: Int, seconds: Int): String {
        return (String.format("-%d:%02d:%02d", hours, minutes, seconds))
    }
}