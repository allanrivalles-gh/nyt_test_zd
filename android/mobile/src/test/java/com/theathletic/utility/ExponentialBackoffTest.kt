package com.theathletic.utility

import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.runTest
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@RunWith(JUnit4::class)
class ExponentialBackoffTest {

    @get:Rule val coroutineTestRule = CoroutineTestRule()

    @Test
    fun `stops to invoke on reach the retry limit count (limit = 3)`() = runTest {
        val runBlock = spy<() -> Unit>()
        var backoffInterval = 100L
        val exponentialBackoff = exponentialBackoffFixture(interval = backoffInterval.milliseconds, retryCount = 3)

        repeat(20) {
            exponentialBackoff.runBlockingBackoff(false, runBlock)
            backoffInterval = advanceByTime(backoffInterval)
        }

        verify(runBlock, times(3)).invoke()
    }

    @Test
    fun `stops to invoke when the condition is satisfied`() = runTest {
        var condition = false
        var backoffInterval = 100L
        val runBlock = spy<() -> Unit>()
        val exponentialBackoff = exponentialBackoffFixture(interval = backoffInterval.milliseconds, retryCount = 3)

        repeat(20) { repeatCount -> // begins with zero
            if (repeatCount == 2) condition = true

            exponentialBackoff.runBlockingBackoff(condition, runBlock)
            backoffInterval = advanceByTime(backoffInterval)
        }

        verify(runBlock, times(2)).invoke()
    }

    @Test
    fun `do not execute if has a job running already`() = runTest {
        val runBlock = spy<() -> Unit>()
        val backoffInterval = 2000L
        val exponentialBackoff = exponentialBackoffFixture(
            interval = backoffInterval.milliseconds,
            intervalLimit = 2.seconds,
            retryCount = 10
        )

        repeat(10) { repeatCount -> // begins with zero

            // The job delay does not restart if it's running already
            // The action is invoked only when the time is advanced by 2+ seconds
            exponentialBackoff.runBlockingBackoff(false, runBlock)

            // Advance time only on pair repeat count
            if (isPair(repeatCount)) coroutineTestRule.advanceTimeBy(backoffInterval + 1)
        }

        verify(runBlock, times(5)).invoke()
    }

    @Test
    fun `use the interval limit if interval time is greater than the limit`() = runTest {
        val runBlock = spy<() -> Unit>()
        val backoffInterval = 4000L
        val exponentialBackoff = exponentialBackoffFixture(
            interval = backoffInterval.milliseconds,
            intervalLimit = 2.seconds,
            retryCount = 5
        )

        repeat(5) {
            exponentialBackoff.runBlockingBackoff(false, runBlock)
            coroutineTestRule.advanceTimeBy(2001)
        }

        verify(runBlock, times(5)).invoke()
    }

    private fun isPair(repeatCount: Int) = repeatCount % 2 == 0

    private fun advanceByTime(backoffTime: Long): Long {
        coroutineTestRule.advanceTimeBy(backoffTime + 1)
        return backoffTime * 2
    }

    private fun exponentialBackoffFixture(
        interval: Duration = 100.milliseconds,
        intervalLimit: Duration = 4.seconds,
        retryCount: Int = 3
    ): ExponentialBackoff {
        return ExponentialBackoff(
            backoffInterval = interval,
            backoffIntervalLimit = intervalLimit,
            retryCount = retryCount,
            dispatcherProvider = coroutineTestRule.dispatcherProvider
        )
    }
}