package com.theathletic.scores.utility

import android.text.format.DateUtils
import com.google.common.truth.Truth.assertThat
import com.theathletic.datetime.TimeProvider
import com.theathletic.utility.Throttler
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import kotlin.test.BeforeTest

@RunWith(MockitoJUnitRunner::class)
class ThrottlerTest {

    @Mock private lateinit var mockTimeProvider: TimeProvider

    private lateinit var throttler: Throttler<String>

    @BeforeTest
    fun setup() {
        throttler = Throttler(mockTimeProvider)
    }

    @Test
    fun `allowed to run when key is new`() {
        whenever(mockTimeProvider.currentTimeMs).thenReturn(0L)

        assertThat(throttler.willRun("groupId-1")).isTrue()
    }

    @Test
    fun `not allowed to run when request with same key is under minimum between allowing`() {
        whenever(mockTimeProvider.currentTimeMs).thenReturn(0L)
        assertThat(throttler.willRun("groupId-2")).isTrue()

        whenever(mockTimeProvider.currentTimeMs).thenReturn(DateUtils.MINUTE_IN_MILLIS * 3)
        assertThat(throttler.willRun("groupId-2")).isFalse()
    }

    @Test
    fun `allowed to run when request with same key is over minimum time between allowing`() {
        whenever(mockTimeProvider.currentTimeMs).thenReturn(0L)
        assertThat(throttler.willRun("groupId-3")).isTrue()

        whenever(mockTimeProvider.currentTimeMs).thenReturn(DateUtils.MINUTE_IN_MILLIS * 6)
        assertThat(throttler.willRun("groupId-3")).isTrue()
    }

    @Test
    fun `allowed to run when a new key from the current is used`() {
        whenever(mockTimeProvider.currentTimeMs).thenReturn(0L)
        assertThat(throttler.willRun("groupId-4")).isTrue()

        whenever(mockTimeProvider.currentTimeMs).thenReturn(DateUtils.MINUTE_IN_MILLIS * 3)
        assertThat(throttler.willRun("groupId-5")).isTrue()
    }
}