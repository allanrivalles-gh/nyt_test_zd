package com.theathletic.ui.formatter

import androidx.annotation.IntRange
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.theathletic.datetime.Chronos
import com.theathletic.datetime.Datetime
import com.theathletic.test.FixedTimeProvider
import com.theathletic.ui.asString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar

@RunWith(AndroidJUnit4::class)
class TimeAgoDateFormatterTest {

    private lateinit var formatter: TimeAgoDateFormatter
    private lateinit var timeProvider: FixedTimeProvider

    @get:Rule val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        timeProvider = FixedTimeProvider()
        formatter = TimeAgoDateFormatter(Chronos(timeProvider))
    }

    @Test
    fun `format date as 'Just now' when the last update was less than two minutes ago`() = composeTestRule.setContent {
        val oneMinuteAgo = timeProvider.currentTimeMs.add(minutes = -1)

        val formatted = formatter.format(Datetime(oneMinuteAgo)).asString()

        assertThat(formatted).isEqualTo("Just Now")
    }

    @Test
    fun `format date as '{2-59}m ago' when the last update was less than one hour ago`() = composeTestRule.setContent {
        val fortyFourMinutesAgo = timeProvider.currentTimeMs.add(minutes = -44)

        val formatted = formatter.format(Datetime(fortyFourMinutesAgo)).asString()

        assertThat(formatted).isEqualTo("44m ago")
    }

    @Test
    fun `format date as '{2-59}m' when the last update was less than one hour ago`() = composeTestRule.setContent {
        val fortyFourMinutesAgo = timeProvider.currentTimeMs.add(minutes = -44)

        val formatted = formatter.format(Datetime(fortyFourMinutesAgo), TimeAgoDateFormatter.Params(showAgo = false)).asString()

        assertThat(formatted).isEqualTo("44m")
    }

    @Test
    fun `format date as '{1-24}h ago' when the last update was less than 24 hours ago`() = composeTestRule.setContent {
        val twelveHoursAgo = timeProvider.currentTimeMs.add(hours = -12)

        val formatted = formatter.format(Datetime(twelveHoursAgo)).asString()

        assertThat(formatted).isEqualTo("12h ago")
    }

    @Test
    fun `format date as '{1-24}h' when the last update was less than 24 hours ago`() = composeTestRule.setContent {
        val twelveHoursAgo = timeProvider.currentTimeMs.add(hours = -12)

        val formatted = formatter.format(Datetime(twelveHoursAgo), TimeAgoDateFormatter.Params(showAgo = false)).asString()

        assertThat(formatted).isEqualTo("12h")
    }

    @Test
    fun `format date as '{1-7}d ago' when the last update was less than 7 days ago`() = composeTestRule.setContent {
        val twoDaysAgo = timeProvider.currentTimeMs.add(days = -2)

        val formatted = formatter.format(Datetime(twoDaysAgo)).asString()

        assertThat(formatted).isEqualTo("2d ago")
    }

    @Test
    fun `format date as '{1-7}d' when the last update was less than 7 days ago`() = composeTestRule.setContent {
        val twoDaysAgo = timeProvider.currentTimeMs.add(days = -2)

        val formatted = formatter.format(Datetime(twoDaysAgo), TimeAgoDateFormatter.Params(showAgo = false)).asString()

        assertThat(formatted).isEqualTo("2d")
    }

    @Test
    fun `format with month and date when the last update was in the current year`() = composeTestRule.setContent {
        val november28th = dateInMillisOf(month = Calendar.NOVEMBER, dayOfMonth = 28)
        val eightDaysAhead = november28th.add(days = 8)
        timeProvider.currentTimeMs = eightDaysAhead
        val formatted = formatter.format(Datetime(november28th)).asString()

        assertThat(formatted).isEqualTo("Nov 28")
    }

    @Test
    fun `format with month, date and year when the last update was before the current year`() = composeTestRule.setContent {
        val november28th2022 = dateInMillisOf(year = 2022, month = Calendar.NOVEMBER, dayOfMonth = 28)
        val nextYear = november28th2022.add(years = 1)
        timeProvider.currentTimeMs = nextYear
        val formatted = formatter.format(Datetime(november28th2022)).asString()

        assertThat(formatted).isEqualTo("Nov 28, 2022")
    }
}

private fun dateInMillisOf(
    @IntRange(1) year: Int? = null,
    @IntRange(1, 12) month: Int? = null,
    @IntRange(1) dayOfMonth: Int? = null
) = Calendar.getInstance().apply {
    year?.also { set(Calendar.YEAR, it) }
    month?.also { set(Calendar.MONTH, it) }
    dayOfMonth?.also { set(Calendar.DAY_OF_MONTH, it) }
}.timeInMillis

private fun Long.add(
    minutes: Int? = null,
    hours: Int? = null,
    days: Int? = null,
    years: Int? = null,
): Long {
    return Calendar.getInstance().apply {
        timeInMillis = this@add
        minutes?.also { add(Calendar.MINUTE, it) }
        hours?.also { add(Calendar.HOUR, it) }
        days?.also { add(Calendar.DAY_OF_MONTH, it) }
        years?.also { add(Calendar.YEAR, it) }
    }.timeInMillis
}