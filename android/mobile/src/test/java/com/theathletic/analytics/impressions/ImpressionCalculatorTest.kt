package com.theathletic.analytics.impressions

import com.theathletic.datetime.TimeProvider
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class ImpressionCalculatorTest {

    private lateinit var impressionCalculator: ImpressionCalculator

    @Mock private lateinit var timeProvider: TimeProvider

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        impressionCalculator = ImpressionCalculator(timeProvider)
    }

    @Test
    fun `impression fires when showing 80% for half second`() {
        whenever(timeProvider.currentTimeMs).thenReturn(500, 1000)

        var firedImpression = false

        impressionCalculator.apply {
            configure({ _, startTime, endTime ->
                firedImpression = true
                assertEquals(500, startTime)
                assertEquals(1000, endTime)
            })

            onViewVisibilityChanged(payload, 0.8f)
            onViewVisibilityChanged(payload, 0.79f)
        }

        assertTrue(firedImpression)
    }

    @Test
    fun `impressions not fired when we show 80% for less than half second`() {
        whenever(timeProvider.currentTimeMs).thenReturn(500, 999)

        impressionCalculator.apply {
            configure({ _, _, _ -> throw IllegalStateException("Should not fire an impression") })

            onViewVisibilityChanged(payload, 0.8f)
            onViewVisibilityChanged(payload, 0.79f)
        }
    }

    @Test
    fun `impression fires with 50% visibility configuration`() {
        whenever(timeProvider.currentTimeMs).thenReturn(500, 1000)

        var firedImpression = false

        impressionCalculator.apply {
            configure(
                { _, _, _ -> firedImpression = true },
                percentVisibleForImpression = 0.5f
            )
            onViewVisibilityChanged(payload, 0.5f)
            onViewVisibilityChanged(payload, 0.49f)
        }

        assertTrue(firedImpression)
    }

    @Test
    fun `impression fires with 2 second time configuration`() {
        whenever(timeProvider.currentTimeMs).thenReturn(1000, 2000, 3000, 5000)

        var firedImpression = false

        impressionCalculator.apply {
            configure(
                { _, startTime, endTime ->
                    firedImpression = true
                    assertEquals(3000, startTime)
                    assertEquals(5000, endTime)
                },
                timeForImpressionMs = 2000L
            )

            onViewVisibilityChanged(payload, 0.8f)
            onViewVisibilityChanged(payload, 0.79f)
            onViewVisibilityChanged(payload, 0.8f)
            onViewVisibilityChanged(payload, 0.79f)
        }

        assertTrue(firedImpression)
    }

    @Test
    fun `impressions not fired when we show 79% for 1 second`() {
        whenever(timeProvider.currentTimeMs).thenReturn(0, 1500, 3000)

        impressionCalculator.apply {
            configure({ _, _, _ -> throw IllegalStateException("Should not fire an impression") })

            onViewVisibilityChanged(payload, 0.5f)
            onViewVisibilityChanged(payload, 0.79f)
            onViewVisibilityChanged(payload, 0.5f)
        }
    }

    @Test
    fun `impression only fires once when item does not leave the screen`() {
        whenever(timeProvider.currentTimeMs).thenReturn(0, 1000, 2000, 3000, 4000)

        var firedCount = 0

        impressionCalculator.apply {
            configure({ _, _, _ -> firedCount++ })

            onViewVisibilityChanged(payload, 0.1f)
            onViewVisibilityChanged(payload, 0.8f)
            onViewVisibilityChanged(payload, 0.1f)
            onViewVisibilityChanged(payload, 0.8f)
            onViewVisibilityChanged(payload, 0.1f)
        }

        assertEquals(1, firedCount)
    }

    @Test
    fun `impression fires again if the item left the screen and comes back`() {
        whenever(timeProvider.currentTimeMs).thenReturn(0, 1000, 2000, 3000, 4000)

        var firedCount = 0

        impressionCalculator.apply {
            configure({ _, _, _ -> firedCount++ })

            onViewVisibilityChanged(payload, 0.0f)
            onViewVisibilityChanged(payload, 0.8f)
            onViewVisibilityChanged(payload, 0.0f)
            onViewVisibilityChanged(payload, 0.8f)
            onViewVisibilityChanged(payload, 0.0f)
        }

        assertEquals(2, firedCount)
    }

    companion object {
        val payload = ImpressionPayload(
            "ObjectType",
            "ObjectId",
            "Element",
            0
        )
    }
}