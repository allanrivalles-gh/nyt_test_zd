package com.theathletic.ui.formatter

import org.junit.Test
import kotlin.test.assertEquals

class CountFormatterTest {

    private val countFormatter = CountFormatter()

    @Test
    fun `test that the comment count formats correctly depending upon its value`() {
        assertEquals("", countFormatter.formatCommentCount(0))
        assertEquals("", countFormatter.formatCommentCount(-1))
        assertEquals("30", countFormatter.formatCommentCount(30))
        assertEquals("543", countFormatter.formatCommentCount(543))
        assertEquals("1.2k", countFormatter.formatCommentCount(1213))
    }

    @Test
    fun `test that the like count formats correctly depending upon its value`() {
        assertEquals("", countFormatter.formatLikesCount(0))
        assertEquals("", countFormatter.formatLikesCount(-1))
        assertEquals("10", countFormatter.formatLikesCount(10))
        assertEquals("234", countFormatter.formatLikesCount(234))
        assertEquals("3.5k", countFormatter.formatLikesCount(3467))
    }
}