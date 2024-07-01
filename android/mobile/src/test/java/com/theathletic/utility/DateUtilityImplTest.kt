package com.theathletic.utility

import android.content.Context
import android.os.LocaleList
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.theathletic.datetime.formatter.DisplayFormat
import com.theathletic.datetime.formatter.DisplayFormat.HOURS_MINUTES
import com.theathletic.datetime.formatter.DisplayFormat.MONTH_DATE_LONG
import com.theathletic.datetime.formatter.DisplayFormat.MONTH_DATE_SHORT
import com.theathletic.datetime.formatter.DisplayFormat.MONTH_DATE_YEAR_SHORT
import com.theathletic.datetime.formatter.DisplayFormat.WEEKDAY_LONG_MONTH_LONG_DATE_LONG_YEAR
import com.theathletic.datetime.formatter.DisplayFormat.WEEKDAY_MONTH_DATE_SHORT
import com.theathletic.datetime.formatter.DisplayFormat.WEEKDAY_SHORT_MONTH_DATE_LONG
import com.theathletic.utility.datetime.DateUtilityImpl
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
class DateUtilityImplTest {
    private val testDateAfternoon = "2019-02-05 17:23:59"

    data class DateTestAssertion(
        val format: DisplayFormat,
        val language: String,
        val country: String,
        val expectedFormatResult: String
    )

    private val testCases = listOf(
        DateTestAssertion(WEEKDAY_MONTH_DATE_SHORT, "en", "US", "Tue, Feb 5"),
        DateTestAssertion(WEEKDAY_MONTH_DATE_SHORT, "en", "GB", "Tue, 5 Feb"),
        DateTestAssertion(WEEKDAY_MONTH_DATE_SHORT, "fr", "CA", "mar. 5 févr."),
        DateTestAssertion(WEEKDAY_SHORT_MONTH_DATE_LONG, "en", "US", "Tue, February 5"),
        DateTestAssertion(WEEKDAY_SHORT_MONTH_DATE_LONG, "en", "GB", "Tue, 5 February"),
        DateTestAssertion(WEEKDAY_SHORT_MONTH_DATE_LONG, "fr", "CA", "mar. 5 février"),
        DateTestAssertion(MONTH_DATE_YEAR_SHORT, "en", "US", "Feb 5, 2019"),
        DateTestAssertion(MONTH_DATE_YEAR_SHORT, "en", "GB", "5 Feb 2019"),
        DateTestAssertion(MONTH_DATE_YEAR_SHORT, "fr", "CA", "5 févr. 2019"),
        DateTestAssertion(MONTH_DATE_SHORT, "en", "US", "Feb 5"),
        DateTestAssertion(MONTH_DATE_SHORT, "en", "GB", "5 Feb"),
        DateTestAssertion(MONTH_DATE_SHORT, "fr", "CA", "5 févr."),
        DateTestAssertion(MONTH_DATE_LONG, "en", "US", "February 5"),
        DateTestAssertion(MONTH_DATE_LONG, "en", "GB", "5 February"),
        DateTestAssertion(MONTH_DATE_LONG, "fr", "CA", "5 février"),
        DateTestAssertion(HOURS_MINUTES, "en", "US", "5:23 PM"),
        DateTestAssertion(HOURS_MINUTES, "en", "GB", "5:23 pm"),
        DateTestAssertion(HOURS_MINUTES, "fr", "CA", "5 h 23 p.m.")
    )

    @Test
    fun testAllDateFormats() {
        for (testCase in testCases) {
            validateDateAssertion(testDateAfternoon, testCase)
        }
    }

    @Test
    @Config(qualifiers = "en-rGB")
    fun `format long weekday, month and year for en-GB`() {
        assertThat(
            DateUtilityImpl
                .formatGMTDateString(testDateAfternoon, WEEKDAY_LONG_MONTH_LONG_DATE_LONG_YEAR)
        ).isEqualTo("Tuesday, 5 February 2019")
    }

    @Test
    @Config(qualifiers = "en-rUS")
    fun `format long weekday, month and year for en-US`() {
        assertThat(
            DateUtilityImpl.formatGMTDateString(
                testDateAfternoon, WEEKDAY_LONG_MONTH_LONG_DATE_LONG_YEAR
            )
        ).isEqualTo("Tuesday, February 5, 2019")
    }

    @Test
    @Config(qualifiers = "fr-rCA")
    fun `format long weekday, month and year for fr-CA`() {
        assertThat(
            DateUtilityImpl.formatGMTDateString(
                testDateAfternoon, WEEKDAY_LONG_MONTH_LONG_DATE_LONG_YEAR
            )
        ).isEqualTo("mardi 5 février 2019")
    }

    @Test
    fun formatPodcastTrackDuration_RoundsUp_LessThan1Minute() {
        assertThat(
            DateUtilityImpl.formatPodcastTrackDuration(TimeUnit.MINUTES.toMillis(0))
        ).isEqualTo("1 MIN")

        assertThat(
            DateUtilityImpl.formatPodcastTrackDuration(TimeUnit.SECONDS.toMillis(15))
        ).isEqualTo("1 MIN")

        assertThat(
            DateUtilityImpl.formatPodcastTrackDuration(TimeUnit.SECONDS.toMillis(45))
        ).isEqualTo("1 MIN")
    }

    @Test
    fun formatPodcastTrackDuration_GreaterThan1Minute() {
        assertThat(
            DateUtilityImpl.formatPodcastTrackDuration(TimeUnit.MINUTES.toMillis(1))
        ).isEqualTo("1 MIN")

        assertThat(
            DateUtilityImpl.formatPodcastTrackDuration(
                TimeUnit.MINUTES.toMillis(1) + TimeUnit.SECONDS.toMillis(20)
            )
        ).isEqualTo("1 MIN")

        assertThat(
            DateUtilityImpl.formatPodcastTrackDuration(
                TimeUnit.MINUTES.toMillis(1) + TimeUnit.SECONDS.toMillis(40)
            )
        ).isEqualTo("1 MIN")

        assertThat(
            DateUtilityImpl.formatPodcastTrackDuration(
                TimeUnit.MINUTES.toMillis(5) + TimeUnit.SECONDS.toMillis(20)
            )
        ).isEqualTo("5 MIN")
    }

    @Test
    fun formatPodcastTrackDuration_GreaterThan1Hour() {
        assertThat(
            DateUtilityImpl.formatPodcastTrackDuration(TimeUnit.MINUTES.toMillis(87))
        ).isEqualTo("87 MIN")

        assertThat(
            DateUtilityImpl.formatPodcastTrackDuration(TimeUnit.MINUTES.toMillis(130))
        ).isEqualTo("130 MIN")
    }

    @Test
    fun formatPodcastTrackTimeSpan_LesserThan10Minutes() {
        assertThat(
            DateUtilityImpl.formatPodcastTrackTimeSpan(0, TimeUnit.MINUTES.toMillis(1))
        ).isEqualTo("0:00-1:00")

        assertThat(
            DateUtilityImpl.formatPodcastTrackTimeSpan(
                TimeUnit.MINUTES.toMillis(1), TimeUnit.MINUTES.toMillis(2)
            )
        ).isEqualTo("1:00-2:00")

        assertThat(
            DateUtilityImpl.formatPodcastTrackTimeSpan(
                TimeUnit.SECONDS.toMillis(30),
                TimeUnit.MINUTES.toMillis(1) + TimeUnit.SECONDS.toMillis(30)
            )
        ).isEqualTo("0:30-1:30")
    }

    @Test
    fun formatPodcastTrackTimeSpan_GreaterThan10Minutes() {
        assertThat(
            DateUtilityImpl.formatPodcastTrackTimeSpan(
                TimeUnit.MINUTES.toMillis(10), TimeUnit.MINUTES.toMillis(12)
            )
        ).isEqualTo("10:00-12:00")
    }

    @Test
    fun formatPodcastTrackTimeSpan_GreaterThan1Hour() {
        assertThat(
            DateUtilityImpl.formatPodcastTrackTimeSpan(
                TimeUnit.HOURS.toMillis(1) + TimeUnit.MINUTES.toMillis(10),
                TimeUnit.HOURS.toMillis(1) + TimeUnit.MINUTES.toMillis(12)
            )
        ).isEqualTo("1:10:00-1:12:00")
    }

    @Test
    fun formatPodcastTrackTimeSpan_ValueSwap() {
        assertThat(
            DateUtilityImpl.formatPodcastTrackTimeSpan(
                TimeUnit.MINUTES.toMillis(2), TimeUnit.MINUTES.toMillis(1)
            )
        ).isEqualTo("1:00-2:00")
    }

    @Test
    @Ignore("Now the framework is returning a Long and not a string")
    fun formatPodcastDurationHHmmss_GreaterThan1Hour() {
        assertThat(TimeUnit.SECONDS.toMillis(3723)).isEqualTo("01:02:03")
    }

    @Test
    @Ignore("Now the framework is returning a Long and not a string")
    fun formatPodcastDurationHHmmss_LessThan1Hour() {
        assertThat(TimeUnit.SECONDS.toMillis(123)).isEqualTo("02:03")
    }

    private fun validateDateAssertion(testDateString: String, testAssertion: DateTestAssertion) {
        setLocale(testAssertion.language, testAssertion.country)
        assertThat(
            DateUtilityImpl.formatGMTDateString(testDateString, testAssertion.format)
        ).isEqualTo(testAssertion.expectedFormatResult)
    }

    /**
     * Sets a test locale. Note the timezone is fixed at UTC, as we are just testing formatting.
     */
    private fun setLocale(language: String, country: String) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        val locale = Locale(language, country)
        Locale.setDefault(locale)
        val res = ApplicationProvider.getApplicationContext<Context>().resources
        val config = res.configuration
        config.setLocales(LocaleList.forLanguageTags(String.format("%s-%s", language, country)))
        res.updateConfiguration(config, res.displayMetrics)
    }
}