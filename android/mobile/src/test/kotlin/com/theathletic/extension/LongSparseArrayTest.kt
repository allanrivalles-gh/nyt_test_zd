package com.theathletic.extension

import androidx.collection.LongSparseArray
import org.junit.Assert.assertEquals
import org.junit.Test

class LongSparseArrayTest {
    @Test
    fun first_ReturnsExpectedValue() {
        val array: LongSparseArray<String> = LongSparseArray()

        array.append(5, "5")
        array.append(10, "10")
        array.append(20, "20")

        assertEquals("5", array.first { it == "5" })
        assertEquals("10", array.first { it == "10" })
        assertEquals("20", array.first { it == "20" })
    }

    @Test(expected = NoSuchElementException::class)
    fun first_ThrowException_WhenElementNotFound() {
        val array: LongSparseArray<String> = LongSparseArray()

        array.append(5, "5")
        array.append(10, "10")
        array.append(20, "20")

        array.first { it == "8" }
    }

    @Test(expected = NoSuchElementException::class)
    fun first_ThrowException_WhenEmpty() {
        val array: LongSparseArray<String> = LongSparseArray()
        array.first { it == "8" }
    }

    @Test
    fun firstOrNull_ReturnsExpectedValue() {
        val array: LongSparseArray<String> = LongSparseArray()

        array.append(5, "5")
        array.append(10, "10")
        array.append(20, "20")

        assertEquals("5", array.firstOrNull { it == "5" })
        assertEquals("10", array.firstOrNull { it == "10" })
        assertEquals("20", array.firstOrNull { it == "20" })
    }

    @Test
    fun firstOrNull_ReturnNull_WhenElementNotFound() {
        val array: LongSparseArray<String> = LongSparseArray()

        array.append(5, "5")
        array.append(10, "10")
        array.append(20, "20")

        assertEquals(null, array.firstOrNull { it == "8" })
    }

    @Test
    fun firstOrNull_ReturnNull_WhenEmpty() {
        val array: LongSparseArray<String> = LongSparseArray()
        assertEquals(null, array.firstOrNull { it == "8" })
    }
}