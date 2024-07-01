package com.theathletic.comments.utility

import com.google.common.truth.Truth.assertThat
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.runTest
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DwellEventsEmitterTest {
    @get:Rule val coroutineTestRule = CoroutineTestRule()

    private lateinit var emitter: DwellEventsEmitter

    @Before
    fun setup() {
        emitter = DwellEventsEmitter()
    }

    @Test
    fun `emits events at the right times`() = runTest {
        val emitTimes = mutableListOf<Int>()

        launch(coroutineTestRule.dispatcher) {
            emitter.start { seconds -> emitTimes.add(seconds) }
        }

        // for some reason, if we don't skip this extra 1ms, the events don't fire
        coroutineTestRule.advanceTimeBy(1)
        assertThat(emitTimes).isEmpty()

        coroutineTestRule.advanceTimeBy(3000) // Simulate passing of 3 seconds
        assertThat(emitTimes).containsExactly(3)

        coroutineTestRule.advanceTimeBy(2000) // Simulate passing of 2 more seconds
        assertThat(emitTimes).containsExactly(3, 5)

        coroutineTestRule.advanceTimeBy(15000) // Simulate passing of 15 more seconds
        assertThat(emitTimes).containsExactly(3, 5, 20)
    }
}