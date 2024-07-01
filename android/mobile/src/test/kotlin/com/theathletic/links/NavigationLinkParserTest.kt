package com.theathletic.links

import com.theathletic.feed.FeedType
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

class NavigationLinkParserTest {
    private lateinit var navigationLinkParser: NavigationLinkParser

    @Before
    fun setUp() {
        navigationLinkParser = NavigationLinkParser()
    }

    @Test
    fun `parseDeeplink returns null if unknown`() {
        assertNull(navigationLinkParser.toFeedType("avada kedavra"))
    }

    @Test
    fun `parseDeeplink returns null if not in known format`() {
        assertNull(navigationLinkParser.toFeedType("theathletic.com://feed/league"))
    }

    @Test
    fun `parseDeeplink returns User FeedType`() {
        assertEquals(FeedType.User, navigationLinkParser.toFeedType("theathletic://feed/user"))
    }

    @Test
    fun `parseDeeplink returns Team FeedType`() {
        assertEquals(FeedType.Team(51), navigationLinkParser.toFeedType("theathletic://feed/team/51"))
    }

    @Test
    fun `parseDeeplink returns League FeedType`() {
        assertEquals(FeedType.League(3), navigationLinkParser.toFeedType("theathletic://feed/league/3"))
    }

    @Test
    fun `parseDeeplink returns Author FeedType`() {
        assertEquals(FeedType.Author(12), navigationLinkParser.toFeedType("theathletic://feed/author/12"))
    }

    @Test
    fun `parseDeeplink returns Category FeedType`() {
        val feedType = navigationLinkParser.toFeedType("theathletic://feed/category/4")
        assertTrue(feedType is FeedType.Category)
        assertEquals(4L, feedType?.id)
    }

    @Test
    fun `parseDeeplink returns Ink FeedType`() {
        val feedType = navigationLinkParser.toFeedType("theathletic://feed/ink/jordan")
        assertTrue(feedType is FeedType.Ink)
    }

    @Test
    fun `parseDeeplink returns Ink FeedType without name`() {
        val feedType = navigationLinkParser.toFeedType("theathletic://feed/ink")
        assertTrue(feedType is FeedType.Ink)
    }

    @Test
    fun `parseDeeplink returns League FeedType if generic league deeplink`() {
        assertEquals(FeedType.League(13), navigationLinkParser.toFeedType("theathletic.com://league/13"))
    }
}