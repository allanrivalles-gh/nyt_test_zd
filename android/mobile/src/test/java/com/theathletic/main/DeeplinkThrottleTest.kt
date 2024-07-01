package com.theathletic.main

import com.google.common.truth.Truth.assertThat
import com.theathletic.datetime.DateUtility
import com.theathletic.datetime.TimeProvider
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

internal class DeeplinkThrottleTest {
    @Mock private lateinit var dateUtility: DateUtility
    @Mock private lateinit var timeProvider: TimeProvider
    private lateinit var deeplinkThrottle: DeeplinkThrottle
    private val currentTimeMillis = 250000L

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        whenever(timeProvider.currentTimeMs).thenReturn(currentTimeMillis)
        whenever(dateUtility.isInPastMoreThan(eq(0), anyOrNull())).thenReturn(true)
        deeplinkThrottle = DeeplinkThrottle(timeProvider, dateUtility)
    }

    @Test
    fun `isThrottled defaults to false`() {
        assertThat(deeplinkThrottle.isThrottled()).isFalse()
    }

    @Test
    fun `isThrottled is true if within time and not released`() {
        whenever(
            dateUtility.isInPastMoreThan(
                eq(currentTimeMillis),
                anyOrNull()
            )
        ).thenReturn(false)
        deeplinkThrottle.startThrottle()
        assertThat(deeplinkThrottle.isThrottled()).isTrue()
    }

    @Test
    fun `isThrottled is false if beyond time and not released`() {
        whenever(
            dateUtility.isInPastMoreThan(
                eq(currentTimeMillis),
                anyOrNull()
            )
        ).thenReturn(true)
        deeplinkThrottle.startThrottle()
        assertThat(deeplinkThrottle.isThrottled()).isFalse()
    }

    @Test
    fun `isThrottled is false if within time and released`() {
        whenever(
            dateUtility.isInPastMoreThan(
                eq(currentTimeMillis),
                anyOrNull()
            )
        ).thenReturn(false)
        deeplinkThrottle.startThrottle()
        deeplinkThrottle.releaseThrottle()
        assertThat(deeplinkThrottle.isThrottled()).isFalse()
    }
}