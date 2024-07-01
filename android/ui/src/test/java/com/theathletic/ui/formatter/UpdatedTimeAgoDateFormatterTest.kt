package com.theathletic.ui.formatter

import com.google.common.truth.Truth.assertThat
import com.theathletic.datetime.Chronos
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.TimeDiff
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class UpdatedTimeAgoDateFormatterTest {

    private lateinit var formatter: UpdatedTimeAgoDateFormatter

    private val chronos = mock(Chronos::class.java)

    @Before
    fun setUp() {
        formatter = UpdatedTimeAgoDateFormatter(chronos)
    }

    @Test
    fun `format date as Updated now when the last update was less than one minute ago`() {
        val timeDiff = TimeDiff(1.minutes.inWholeMilliseconds)
        whenever(chronos.timeDiff(to = Datetime(0))).thenReturn(timeDiff)

        val resourceString = formatter.format(Datetime(0)) as ResourceString.StringWithParams

        assertThat(resourceString.stringRes).isEqualTo(R.string.time_now_updated)
    }

    @Test
    fun `format date as Updated {2-59}m ago when the last update was less than one hour ago`() {
        val timeDiff = TimeDiff(44.minutes.inWholeMilliseconds)
        whenever(chronos.timeDiff(to = Datetime(0))).thenReturn(timeDiff)

        val resourceString = formatter.format(Datetime(0)) as ResourceString.StringWithParams

        assertThat((resourceString).stringRes).isEqualTo(R.string.time_minutes_ago_updated)
        assertThat((resourceString).parameters[0]).isEqualTo(44)
    }

    @Test
    fun `format date as Updated {1-24}h ago when the last update was less than 24 hours ago`() {
        val timeDiff = TimeDiff(12.hours.inWholeMilliseconds)
        whenever(chronos.timeDiff(to = Datetime(0))).thenReturn(timeDiff)

        val resourceString = formatter.format(Datetime(0)) as ResourceString.StringWithParams

        assertThat((resourceString).stringRes).isEqualTo(R.string.time_hours_ago_updated)
        assertThat((resourceString).parameters[0]).isEqualTo(12)
    }

    @Test
    fun `format date as Updated {1-7}d ago when the last update was less than 7 days ago`() {
        val timeDiff = TimeDiff(24.hours.inWholeMilliseconds)
        whenever(chronos.timeDiff(to = Datetime(0))).thenReturn(timeDiff)

        val resourceString = formatter.format(Datetime(0)) as ResourceString.StringWithParams

        assertThat((resourceString).stringRes).isEqualTo(R.string.time_days_ago_updated)
        assertThat((resourceString).parameters[0]).isEqualTo(1)
    }
}