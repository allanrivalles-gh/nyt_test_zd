package com.theathletic.article.ui

import androidx.core.widget.NestedScrollView
import com.theathletic.ads.articles.AdsScrollBehavior
import kotlin.test.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.internal.verification.Times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ArticleReadCalculatorTest {

    private companion object {
        const val TOPPER_SIZE = 600
        const val SCREEN_SIZE = 1000
        const val ARTICLE_SIZE = 5000

        const val TOPPER_ID = "topper"
    }

    @Mock
    private lateinit var scrollView: NestedScrollView
    @Mock
    private lateinit var onArticleReadListener: ArticleReadCalculator.OnArticleReadListener
    @Mock
    private lateinit var adsScrollBehavior: AdsScrollBehavior

    private lateinit var articleReadCalculator: ArticleReadCalculator

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        whenever(scrollView.height).thenReturn(SCREEN_SIZE)

        articleReadCalculator = ArticleReadCalculator(onArticleReadListener, adsScrollBehavior, 10)
        articleReadCalculator.updateTopperSize(TOPPER_ID, TOPPER_SIZE)
        articleReadCalculator.articleSize = ARTICLE_SIZE
        articleReadCalculator.onScrollChange(
            scrollView = scrollView,
            scrollX = 0,
            scrollY = 0,
            oldScrollX = 0,
            oldScrollY = 0
        )
    }

    @Test
    fun `no scroll results in read percent equal to 5`() {
        assertEquals(5, articleReadCalculator.articleMaxReadPercent)
    }

    @Test
    fun `small scroll results in read percent equal to 15`() {
        articleReadCalculator.onScrollChange(
            scrollView = scrollView,
            scrollX = 0,
            scrollY = 500,
            oldScrollX = 0,
            oldScrollY = 0
        )

        assertEquals(15, articleReadCalculator.articleMaxReadPercent)
        verify(onArticleReadListener).onArticleRead()
    }

    @Test
    fun `small scroll and back to top results in read percent equal to 15`() {
        articleReadCalculator.onScrollChange(
            scrollView = scrollView,
            scrollX = 0,
            scrollY = 500,
            oldScrollX = 0,
            oldScrollY = 0
        )
        articleReadCalculator.onScrollChange(
            scrollView = scrollView,
            scrollX = 0,
            scrollY = 0,
            oldScrollX = 0,
            oldScrollY = 0
        )

        assertEquals(15, articleReadCalculator.articleMaxReadPercent)
        verify(onArticleReadListener).onArticleRead()
    }

    @Test
    fun `scroll past bottom results in read percent equal to 1000`() {
        articleReadCalculator.onScrollChange(
            scrollView = scrollView,
            scrollX = 0,
            scrollY = 10000,
            oldScrollX = 0,
            oldScrollY = 0
        )

        assertEquals(100, articleReadCalculator.articleMaxReadPercent)
        verify(onArticleReadListener).onArticleRead()
    }

    @Test
    fun `scroll past bottom and back to top results in read percent equal to 1000`() {
        articleReadCalculator.onScrollChange(
            scrollView = scrollView,
            scrollX = 0,
            scrollY = 10000,
            oldScrollX = 0,
            oldScrollY = 0
        )
        articleReadCalculator.onScrollChange(
            scrollView = scrollView,
            scrollX = 0,
            scrollY = 0,
            oldScrollX = 0,
            oldScrollY = 0
        )

        assertEquals(100, articleReadCalculator.articleMaxReadPercent)
        verify(onArticleReadListener).onArticleRead()
    }

    @Test
    fun `article size of zero returns 0 percent read`() {
        articleReadCalculator.articleSize = 0
        articleReadCalculator.onScrollChange(
            scrollView = scrollView,
            scrollX = 0,
            scrollY = 0,
            oldScrollX = 0,
            oldScrollY = 0
        )

        assertEquals(0, articleReadCalculator.articleMaxReadPercent)
        verify(onArticleReadListener, Times(0)).onArticleRead()
    }

    @Test
    fun `scroll to 15 percent of the article returns 15 percent article max read and article completed false`() {
        articleReadCalculator.onScrollChange(
            scrollView = scrollView,
            scrollX = 0,
            scrollY = 500,
            oldScrollX = 0,
            oldScrollY = 0
        )

        assertEquals(15, articleReadCalculator.articleMaxReadPercent)
        assertEquals(false, articleReadCalculator.isMarkAsCompleted)
    }

    @Test
    fun `scroll to 96 percent of the article returns 100 percent article max read and article completed true`() {
        articleReadCalculator.articleSize = 500
        articleReadCalculator.onScrollChange(
            scrollView = scrollView,
            scrollX = 0,
            scrollY = 480,
            oldScrollX = 0,
            oldScrollY = 0
        )

        assertEquals(100, articleReadCalculator.articleMaxReadPercent)
        assertEquals(true, articleReadCalculator.isMarkAsCompleted)
    }

    @Test
    fun `scroll to 98 percent of the article scroll back to 40 returns article completed true`() {
        articleReadCalculator.articleSize = 500
        articleReadCalculator.onScrollChange(
            scrollView = scrollView,
            scrollX = 0,
            scrollY = 475,
            oldScrollX = 0,
            oldScrollY = 0
        )
        articleReadCalculator.onScrollChange(
            scrollView = scrollView,
            scrollX = 0,
            scrollY = 200,
            oldScrollX = 0,
            oldScrollY = 0
        )

        assertEquals(true, articleReadCalculator.isMarkAsCompleted)
        verify(onArticleReadListener).onArticleCompleted()
    }
}