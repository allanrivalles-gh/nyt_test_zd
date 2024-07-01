package com.theathletic.datetime.formatter

/**
 * Enum to represent the different display formats, which should be localized using DateUtils
 */
enum class DisplayFormat {
    WEEKDAY_LONG_MONTH_LONG_DATE_LONG_YEAR, // Monday, November 30th, 2019
    WEEKDAY_MONTH_DATE_SHORT, // Mon, Nov 7
    WEEKDAY_MONTH_DATE_ABBREVIATED, // Mon, 11/7
    WEEKDAY_SHORT_MONTH_DATE_LONG, // Mon, November 7
    WEEKDAY_SHORT, // Tue
    WEEKDAY_SHORT_HOURS_MINUTES, // Tue 7:05 PM
    WEEKDAY_FULL, // Tuesday
    MONTH_DATE_YEAR, // November 7, 2018
    MONTH_DATE_YEAR_SHORT, // Nov 7, 2018
    MONTH_DATE_SHORT, // Nov 7
    MONTH_SHORT, // Nov
    MONTH_DATE_LONG, // November 7
    HOURS_MINUTES, // 7:05 PM
    DAY_OF_MONTH, // 7
    YEAR_LONG, // 2021
    LOCALIZED_DATE // 01/23/99 or 23/01/99
}