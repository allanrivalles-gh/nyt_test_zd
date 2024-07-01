package com.theathletic.links

import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LinkHelperTest {

    private lateinit var linkHelper: LinkHelper

    @Before
    fun setup() {
        linkHelper = LinkHelper()
    }

    @Test
    fun `valid deeplink returns true`() {
        assertTrue(linkHelper.isAthleticLink("theathletic://plans"))
    }

    @Test
    fun `valid the athletic url returns true`() {
        assertTrue(linkHelper.isAthleticLink("https://www.theathletic.com/testing"))
    }

    @Test
    fun `valid staging url returns true`() {
        assertTrue(linkHelper.isAthleticLink("https://staging2.theathletic.com/testing"))
    }

    @Test
    fun `non the athletic url returns false`() {
        assertFalse(linkHelper.isAthleticLink("https://www.google.com"))
    }
}