package com.theathletic.utility.extensions

import kotlin.test.assertEquals
import org.junit.Test

class IterableExtensionsTest {

    @Test
    fun `distinctByConsecutive on empty list returns empty list`() {
        val list = emptyList<Int>()
        val newList = list.distinctByConsecutive { it }
        assertEquals(newList, emptyList())
    }

    @Test
    fun `distinctByConsecutive on single item lists returns single item list`() {
        val list = listOf(1)
        val newList = list.distinctByConsecutive { it }
        assertEquals(newList, listOf(1))
    }

    @Test
    fun `distinctByConsecutive on double item lists returns single item list`() {
        val list = listOf(1, 1)
        val newList = list.distinctByConsecutive { it }
        assertEquals(newList, listOf(1))
    }

    @Test
    fun `distinctByConsecutive does not filter not non-consecutive duplicates`() {
        val list = listOf(1, 2, 1)
        val newList = list.distinctByConsecutive { it }
        assertEquals(newList, listOf(1, 2, 1))
    }

    @Test
    fun `distinctByConsecutive complex example`() {
        val list = listOf(1, 1, 2, 1, 3, 4, 4, 2, 2, 4)
        val newList = list.distinctByConsecutive { it }
        assertEquals(newList, listOf(1, 2, 1, 3, 4, 2, 4))
    }
}