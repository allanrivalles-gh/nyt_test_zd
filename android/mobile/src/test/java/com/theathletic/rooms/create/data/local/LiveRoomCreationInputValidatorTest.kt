package com.theathletic.rooms.create.data.local

import com.theathletic.rooms.create.ui.LiveRoomTagType
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Before
import org.junit.Test

internal class LiveRoomCreationInputValidatorTest {

    lateinit var inputValidator: LiveRoomCreationInputValidator

    @Before
    fun setup() {
        inputValidator = LiveRoomCreationInputValidator()
    }

    @Test
    fun `returns false when title is empty`() {
        val input = LiveRoomCreationInput(
            title = "",
            description = "description",
            tags = setOf(TAG),
        )
        assertFalse(inputValidator.isValid(input))
    }

    @Test
    fun `returns false when title is too long`() {
        val input = LiveRoomCreationInput(
            title = "a".repeat(76),
            description = "description",
            tags = setOf(TAG),
        )
        assertFalse(inputValidator.isValid(input))
    }

    @Test
    fun `returns false when description is too short`() {
        val input = LiveRoomCreationInput(
            title = "title",
            description = "",
            tags = setOf(TAG),
        )
        assertFalse(inputValidator.isValid(input))
    }

    @Test
    fun `returns false when description is too long`() {
        val input = LiveRoomCreationInput(
            title = "title",
            description = "a".repeat(501),
            tags = setOf(TAG),
        )
        assertFalse(inputValidator.isValid(input))
    }

    @Test
    fun `returns false when tags is empty`() {
        val input = LiveRoomCreationInput(
            title = "title",
            description = "description",
            tags = emptySet(),
        )
        assertFalse(inputValidator.isValid(input))
    }

    @Test
    fun `returns true when everything is valid`() {
        val input = LiveRoomCreationInput(
            title = "a".repeat(75),
            description = "a".repeat(500),
            tags = setOf(TAG)
        )
        assertTrue(inputValidator.isValid(input))
    }

    companion object {
        val TAG = LiveRoomTagOption(
            id = "1",
            type = LiveRoomTagType.TEAM,
            title = "Team1",
            name = "Team1",
            shortname = "TM1",
        )
    }
}