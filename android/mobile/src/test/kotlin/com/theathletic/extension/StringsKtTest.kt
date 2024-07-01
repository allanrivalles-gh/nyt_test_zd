package com.theathletic.extension

import com.theathletic.utility.toTitleCase
import org.junit.Assert.assertEquals
import org.junit.Test

internal class StringsKtTest {

    private val titleCaseConversions = listOf(
        "all lower case" to "All Lower Case",
        "ALL UPPER CASE" to "All Upper Case",
        "123 starts with number" to "123 Starts With Number",
        "123with numbers" to "123with Numbers",
        "RaNDoM cAPItaliZaTIon" to "Random Capitalization",
    )

    @Test
    fun `title case conversions`() {
        titleCaseConversions.map { (initial, expected) ->
            assertEquals(expected, initial.toTitleCase())
        }
    }
}