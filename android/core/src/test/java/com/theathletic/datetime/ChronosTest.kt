package com.theathletic.datetime

import com.google.common.truth.Truth.assertThat
import com.theathletic.test.FixedTimeProvider
import org.junit.Before
import org.junit.Test
import java.util.TimeZone
import kotlin.time.Duration.Companion.days

class ChronosTest {

    private val date_2022_12_31 = 1672444800000
    private val date_2023_01_01 = 1672531200000
    private val date_2023_01_01_12_00 = 1672574400000
    private val date_2023_01_02 = 1672617600000
    private val date_2023_01_06 = 1672963200000
    private val date_2023_01_08 = 1673136000000

    private val timeProvider = FixedTimeProvider()

    @Before
    fun setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Test
    fun `date time is in this year if it is in the same year compared to the current date time`() {
        val chronos = Chronos(timeProvider)
        timeProvider.currentTimeMs = date_2023_01_01

        val isThisYear = chronos.isThisYear(date_2023_01_02)

        assertThat(isThisYear).isTrue()
    }

    @Test
    fun `date time is not in this year if it is a different year compared to the current date time`() {
        val chronos = Chronos(timeProvider)
        timeProvider.currentTimeMs = date_2023_01_01

        val isThisYear = chronos.isThisYear(date_2022_12_31)

        assertThat(isThisYear).isFalse()
    }

    @Test
    fun `calculate the time diff between two given dates`() {
        val chronos = Chronos(timeProvider)

        val timeDiff = chronos.timeDiff(from = Datetime(date_2023_01_01), to = Datetime(date_2022_12_31))

        assertThat(timeDiff).isEqualTo(TimeDiff(1.days.inMillis))
    }

    @Test
    fun `calculate the time diff from a given date to the current date time`() {
        val chronos = Chronos(timeProvider)
        timeProvider.currentTimeMs = date_2023_01_02

        val timeDiff = chronos.timeDiff(from = Datetime(date_2023_01_01))

        assertThat(timeDiff).isEqualTo(TimeDiff(1.days.inMillis))
    }

    @Test
    fun `calculate the time diff from the current date time Cto the given date time`() {
        val chronos = Chronos(timeProvider)
        timeProvider.currentTimeMs = date_2023_01_01

        val timeDiff = chronos.timeDiff(to = Datetime(date_2023_01_02))

        assertThat(timeDiff).isEqualTo(TimeDiff(1.days.inMillis))
    }

    @Test
    fun `time diff calculation does not return negative values`() {
        val chronos = Chronos(timeProvider)

        val timeDiff = chronos.timeDiff(from = Datetime(date_2022_12_31), to = Datetime(date_2023_01_02))

        assertThat(timeDiff).isEqualTo(TimeDiff(2.days.inMillis))
    }

    @Test
    fun `isWithinWeek returns true if date is less than one week ahead`() {
        val chronos = Chronos(timeProvider.apply { currentTimeMs = date_2023_01_01 })
        val isWithinWeek = chronos.isWithinWeek(date_2023_01_06)

        assertThat(isWithinWeek).isTrue()
    }

    @Test
    fun `isWithinWeek returns true if date is less than one week ahead in the following year`() {
        val chronos = Chronos(timeProvider.apply { currentTimeMs = date_2022_12_31 })
        val isWithinWeek = chronos.isWithinWeek(date_2023_01_02)

        assertThat(isWithinWeek).isTrue()
    }

    @Test
    fun `isWithinWeek returns false if date is more than one week ahead`() {
        val chronos = Chronos(timeProvider.apply { currentTimeMs = date_2023_01_01 })
        val isWithinWeek = chronos.isWithinWeek(date_2023_01_08)

        assertThat(isWithinWeek).isFalse()
    }

    @Test
    fun `isToday return true if date is on the same date`() {
        val chronos = Chronos(timeProvider.apply { currentTimeMs = date_2023_01_01 })
        val isToday = chronos.isToday(date_2023_01_01_12_00)

        assertThat(isToday).isTrue()
    }

    @Test
    fun `isToday returns false if date is not on the same date even if it is less than one day away`() {
        val chronos = Chronos(timeProvider.apply { currentTimeMs = date_2023_01_01_12_00 })
        val isToday = chronos.isToday(date_2023_01_02)

        assertThat(isToday).isFalse()
    }
}