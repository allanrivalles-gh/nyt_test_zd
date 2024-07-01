package com.theathletic.article

import com.google.common.truth.Truth.assertThat
import com.theathletic.article.data.ArticleRepository
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.remoteconfig.RemoteConfigRepository
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.test.runTest
import com.theathletic.user.IUserManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ArticleHasPaywallUseCaseTest {
    private lateinit var repository: ArticleRepository
    private lateinit var userDataRepository: IUserDataRepository
    private lateinit var remoteConfigRepository: RemoteConfigRepository
    private lateinit var userManager: IUserManager
    private lateinit var freeArticleTrackerRepository: FreeArticleTrackerRepository
    private lateinit var freeArticleTracker: FreeArticleTracker
    private lateinit var articleHasPaywall: ArticleHasPaywallUseCase
    private var onSnapshotChanged: ((FreeArticleTracker.Snapshot) -> Unit)? = null
    private var isTeaser = false
    private var subscriberScore: Double? = null

    @Before
    fun setup() {
        isTeaser = false
        subscriberScore = null
        onSnapshotChanged = null
        repository = mock {
            onBlocking { getArticle(any(), eq(false)) } doAnswer { invocation ->
                ArticleEntity(
                    articleId = invocation.getArgument(0),
                    isTeaser = isTeaser,
                    subscriberScore = subscriberScore,
                )
            }
        }
        userDataRepository = mock {
            on { isItemRead(any()) } doReturn false
        }
        remoteConfigRepository = mock {
            on { articleSubscriberScoreThreshold } doReturn flowOf(1.0)
            on { freeArticlesPerMonthCount } doReturn flowOf(2)
        }
        userManager = mock {
            on { isUserSubscribed() } doReturn false
            on { isUserLoggedIn() } doReturn true
        }
        freeArticleTracker = mock {
            on { isArticleFree(any(), any()) } doReturn false
            on { onSnapshotChanged } doAnswer { onSnapshotChanged }
        }
        doAnswer { onSnapshotChanged = it.getArgument(0) }.whenever(freeArticleTracker).onSnapshotChanged = any()
        freeArticleTrackerRepository = mock {
            on { getTracker() } doReturn freeArticleTracker
        }
        articleHasPaywall = ArticleHasPaywallUseCase(
            repository,
            userDataRepository,
            remoteConfigRepository,
            userManager,
            freeArticleTrackerRepository,
        )
    }

    @Test
    fun `returns false when article is null`() = runTest {
        val articleId = 1L

        whenever(repository.getArticle(articleId)).doReturn(null)

        val result = articleHasPaywall(articleId)

        assertThat(result).isFalse()
    }

    @Test
    fun `returns false if user is subscribed`() = runTest {
        whenever(userManager.isUserSubscribed()).doReturn(true)

        val result = articleHasPaywall(1)

        assertThat(result).isFalse()
    }

    @Test
    fun `returns false if article has been read by the user`() = runTest {
        whenever(userDataRepository.isItemRead(1)).doReturn(true)

        val result = articleHasPaywall(1)

        assertThat(result).isFalse()
    }

    @Test
    fun `returns false if article is a teaser`() = runTest {
        isTeaser = true

        val result = articleHasPaywall(1)

        assertThat(result).isFalse()
    }

    @Test
    fun `returns true if user is not logged in`() = runTest {
        whenever(userManager.isUserLoggedIn()).doReturn(false)

        val result = articleHasPaywall(1)

        assertThat(result).isTrue()
    }

    @Test
    fun `returns true if subscriberScore is not null and greater than or equal to articleSubscriberScoreThreshold`() = runTest {
        whenever(remoteConfigRepository.articleSubscriberScoreThreshold).doReturn(flowOf(0.5))
        subscriberScore = 0.6

        val result = articleHasPaywall(1)

        assertThat(result).isTrue()
    }

    @Test
    fun `returns false if article is free`() = runTest {
        val freeArticlesPerMonthCount = remoteConfigRepository.freeArticlesPerMonthCount.first()
        whenever(freeArticleTracker.isArticleFree(1, freeArticlesPerMonthCount)).doReturn(true)

        val result = articleHasPaywall(1)

        assertThat(result).isFalse()
    }

    @Test
    fun `returns true if article is not free`() = runTest {
        val freeArticlesPerMonthCount = remoteConfigRepository.freeArticlesPerMonthCount.first()
        whenever(freeArticleTracker.isArticleFree(1, freeArticlesPerMonthCount)).doReturn(false)

        val result = articleHasPaywall(1)

        assertThat(result).isTrue()
    }
}