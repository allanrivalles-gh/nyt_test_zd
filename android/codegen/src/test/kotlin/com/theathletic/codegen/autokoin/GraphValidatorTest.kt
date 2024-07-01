package com.theathletic.codegen.autokoin

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GraphValidatorTest {

    @Test
    fun `valid graph passes`() {
        val graph = mapOf(
            "a" to listOf("b"),
            "b" to listOf("c", "d"),
            "c" to listOf(),
            "d" to listOf()
        )

        val result = GraphValidator.validate(graph)

        assertEquals(GraphValidator.Result.Success, result)
    }

    @Test
    fun `circular dependency fails`() {
        val graph = mapOf(
            "a" to listOf("b"),
            "b" to listOf("c"),
            "c" to listOf("a")
        )

        val result = GraphValidator.validate(graph)

        assertTrue(result is GraphValidator.Result.Fail)
        assertEquals("Circular dependency", result.reason)
    }
}