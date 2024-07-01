package com.theathletic.main.ui

import com.google.common.truth.Truth.assertThat
import com.theathletic.SlugToTopicQuery
import com.theathletic.article.data.ArticleRepository
import com.theathletic.article.data.TagFeed
import com.theathletic.test.runTest
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

private const val SLUG = "womens-world-cup"
private const val FEED_TYPENAME = "Topic"
private const val FEED_ID = "10730"
private const val FEED_TITLE = "Women's World Cup"

@RunWith(MockitoJUnitRunner::class)
class SlugToTagFeedUseCaseTest {

    private val articlesRepository = mockk<ArticleRepository>(relaxed = true)
    private lateinit var slugToTagFeedUseCase: SlugToTagFeedUseCase

    @Before
    fun setUp() {
        slugToTagFeedUseCase = SlugToTagFeedUseCase(articlesRepository)
    }

    @Test
    fun `successfully returns feed id and title when api call is successful`() = runTest {
        coEvery { articlesRepository.getTagFeedFromSlug(SLUG) } returns SlugToTopicQuery.SlugToTopic(
            __typename = FEED_TYPENAME,
            id = FEED_ID,
            title = FEED_TITLE,
        )

        val result = slugToTagFeedUseCase(SLUG)
        assertThat(result).isEqualTo(Result.success(TagFeed(FEED_ID.toLong(), FEED_TITLE)))
    }

    @Test
    fun `returns failure with exception when returned id from api is blank`() = runTest {
        coEvery { articlesRepository.getTagFeedFromSlug(SLUG) } returns SlugToTopicQuery.SlugToTopic(
            __typename = FEED_TYPENAME,
            id = "",
            title = "",
        )

        val result = slugToTagFeedUseCase(SLUG)
        val exception = Throwable("Id is null or blank for $SLUG")
        assertThat(result.isFailure).isTrue()
        assertThat(exception.message).isEqualTo("Id is null or blank for $SLUG")
    }

    @Test
    fun `returns failure with exception when returned id from api is null`() = runTest {
        coEvery { articlesRepository.getTagFeedFromSlug(SLUG) } returns null

        val result = slugToTagFeedUseCase(SLUG)
        val exception = Throwable("Id is null or blank for $SLUG")
        assertThat(result.isFailure).isTrue()
        assertThat(exception.message).isEqualTo("Id is null or blank for $SLUG")
    }

    @Test
    fun `returns failure with exception when api call fails`() = runTest {
        val throwableMessage = "Error"
        coEvery { articlesRepository.getTagFeedFromSlug(SLUG) } throws Throwable(throwableMessage)

        val result = slugToTagFeedUseCase(SLUG)
        val exception = Throwable(throwableMessage)
        assertThat(result.isFailure).isTrue()
        assertThat(exception.message).isEqualTo(throwableMessage)
    }
}