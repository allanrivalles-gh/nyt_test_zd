package com.theathletic.feed.data.remote

import com.theathletic.article.data.ArticleRepository
import com.theathletic.article.data.remote.SingleArticleFetcher
import com.theathletic.datetime.DateUtility
import com.theathletic.datetime.TimeProvider
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.runTest
import com.theathletic.utility.INetworKManager
import java.util.Date
import java.util.concurrent.TimeUnit
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FeedArticlePrefetcherTest {

    @Mock private lateinit var articleRepository: ArticleRepository
    @Mock private lateinit var articleFetcher: SingleArticleFetcher
    @Mock private lateinit var networkManager: INetworKManager
    @Mock private lateinit var dateUtility: DateUtility
    @Mock private lateinit var timeProvider: TimeProvider

    @get:Rule val coroutineTestRule = CoroutineTestRule()

    private lateinit var prefetcher: FeedArticlePrefetcher

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        whenever(networkManager.isOnline()).thenReturn(true)
        whenever(networkManager.isOnMobileData()).thenReturn(false)

        whenever(timeProvider.currentTimeMs).thenReturn(TimeUnit.DAYS.toMillis(4))

        whenever(dateUtility.parseDateFromGMT(any())).thenReturn(Date(TimeUnit.DAYS.toMillis(2)))

        prefetcher = FeedArticlePrefetcher(
            articleRepository,
            articleFetcher,
            networkManager,
            dateUtility,
            timeProvider,
            coroutineTestRule.dispatcherProvider
        )
    }

    @Test
    fun `prefetcher does nothing when there is no internet connection`() = runTest {
        whenever(networkManager.isOnline()).thenReturn(false)

        prefetcher.prefetch(listOf(ARTICLE1))

        verify(articleRepository, never()).getArticle(any(), any())
    }

    @Test
    fun `prefetcher does nothing when connected to mobile data`() = runTest {
        whenever(networkManager.isOnMobileData()).thenReturn(true)

        prefetcher.prefetch(listOf(ARTICLE1))

        verify(articleRepository, never()).getArticle(any(), any())
    }

    @Test
    fun `calling with the same article only fetches it once`() = runTest {
        prefetcher.prefetch(listOf(ARTICLE1))
        prefetcher.prefetch(listOf(ARTICLE1))

        verify(articleRepository, times(1)).getArticle(1)
    }

    @Test
    fun `when entity doesn't exist, fetch from network`() = runTest {
        whenever(articleRepository.getArticle(1)).thenReturn(null)

        prefetcher.prefetch(listOf(ARTICLE1))
        coroutineTestRule.advanceTimeBy(1001L)

        verify(articleFetcher).fetchRemote(eq(SingleArticleFetcher.Params(1)))
    }

    @Test
    fun `when entity exists but is outdated, fetch from network`() = runTest {
        whenever(articleRepository.getArticle(1)).thenReturn(ARTICLE1_ENTITY_OLD)

        prefetcher.prefetch(listOf(ARTICLE1))
        coroutineTestRule.advanceTimeBy(1001L)

        verify(articleFetcher).fetchRemote(eq(SingleArticleFetcher.Params(1)))
    }

    @Test
    fun `when an up-to-date entity exists, don't fetch`() = runTest {
        whenever(articleRepository.getArticle(1)).thenReturn(ARTICLE1)

        prefetcher.prefetch(listOf(ARTICLE1))

        verify(articleFetcher, never()).fetchRemote(any())
    }

    @Test
    fun `can fetch multiple articles`() = runTest {
        whenever(articleRepository.getArticle(1)).thenReturn(null)
        whenever(articleRepository.getArticle(2)).thenReturn(null)

        prefetcher.prefetch(listOf(ARTICLE1, ARTICLE2))
        coroutineTestRule.advanceTimeBy(2001L)

        verify(articleFetcher).fetchRemote(eq(SingleArticleFetcher.Params(1)))
        verify(articleFetcher).fetchRemote(eq(SingleArticleFetcher.Params(2)))
    }

    companion object {
        val ARTICLE1 = ArticleEntity().apply {
            articleId = 1
        }
        val ARTICLE2 = ArticleEntity().apply {
            articleId = 2
        }
        val ARTICLE1_ENTITY_OLD = ArticleEntity().apply {
            articleId = 1
        }
    }
}