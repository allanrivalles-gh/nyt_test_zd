package com.theathletic.followable

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FollowableTest {
    @Test
    fun `should return null parsing invalid string`() {
        assertNull(Followable.Id.parse(""))
        assertNull(Followable.Id.parse("1"))
        assertNull(Followable.Id.parse("garbage:1"))
    }

    @Test
    fun `should return valid ID for valid string`() {
        val id = Followable.Id.parse("LEAGUE:1")
        assertNotNull(id)
        assertEquals(FollowableType.LEAGUE, id.type)
        assertEquals("1", id.id)
    }
}