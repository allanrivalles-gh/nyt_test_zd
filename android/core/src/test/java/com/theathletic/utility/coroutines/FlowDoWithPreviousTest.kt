package com.theathletic.utility.coroutines

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class FlowDoWithPreviousTest {

    private lateinit var flow: MutableStateFlow<Int>

    @Before
    fun setUp() {
        flow = MutableStateFlow(1)
    }

    @Test
    fun `sending one just calls doWithPrevious once with null`() = runBlockingTest {
        val pairs = mutableListOf<Pair<Int?, Int>>()

        val job = flow
            .doWithPrevious { old, new -> pairs.add(old to new) }
            .collectIn(this) {
                // Do nothing
            }

        assertEquals(expected = 1, actual = pairs.size)
        job.cancel()
    }

    @Test
    fun `sending multiple calls doWithPrevious with the proper values`() = runBlockingTest {
        val pairs = mutableListOf<Pair<Int?, Int>>()

        val job = flow
            .doWithPrevious { old, new -> pairs.add(old to new) }
            .collectIn(this) {
                // Do nothing
            }

        flow.value = 2
        flow.value = 3
        flow.value = 4

        assertTrue(pairs.size == 4)
        assertEquals(expected = 4, actual = pairs.size)
        assertEquals(expected = null to 1, actual = pairs[0])
        assertEquals(expected = 1 to 2, actual = pairs[1])
        assertEquals(expected = 2 to 3, actual = pairs[2])
        assertEquals(expected = 3 to 4, actual = pairs[3])
        job.cancel()
    }

    @Test
    fun `doWithPrevious stops getting called after job is cancelled`() = runBlockingTest {
        val pairs = mutableListOf<Pair<Int?, Int>>()

        val job = flow
            .doWithPrevious { old, new -> pairs.add(old to new) }
            .collectIn(this) {
                // Do nothing
            }

        flow.value = 2
        flow.value = 3

        job.cancel()

        flow.value = 4

        assertEquals(expected = 3, actual = pairs.size)
    }
}