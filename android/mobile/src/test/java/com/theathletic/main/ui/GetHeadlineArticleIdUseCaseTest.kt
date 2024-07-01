package com.theathletic.main.ui

import com.google.common.truth.Truth.assertThat
import com.theathletic.article.data.ArticleRepository
import com.theathletic.test.runTest
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

private const val HEADLINE_ID = "C4H19XqLMif9"
private const val ARTICLE_ID = "123"

@RunWith(MockitoJUnitRunner::class)
class GetHeadlineArticleIdUseCaseTest {

    private val articlesRepository = mockk<ArticleRepository>(relaxed = true)
    private lateinit var getHeadlineArticleIdUseCase: GetHeadlineArticleIdUseCase

    @Before
    fun setUp() {
        getHeadlineArticleIdUseCase = GetHeadlineArticleIdUseCase(articlesRepository)
    }

    @Test
    fun `successfully returns article id string when api call is successful`() = runTest {
        coEvery { articlesRepository.getHeadlineArticleId(HEADLINE_ID) } returns ARTICLE_ID

        val result = getHeadlineArticleIdUseCase(HEADLINE_ID)
        assertThat(result).isEqualTo(Result.success(ARTICLE_ID))
    }

    @Test
    fun `returns failure with exception when returned id from api is blank`() = runTest {
        coEvery { articlesRepository.getHeadlineArticleId(HEADLINE_ID) } returns " "

        val result = getHeadlineArticleIdUseCase(HEADLINE_ID)
        val exception = Throwable("Id is null or blank for $HEADLINE_ID")
        assertThat(result.isFailure).isTrue()
        assertThat(exception.message).isEqualTo("Id is null or blank for $HEADLINE_ID")
    }

    @Test
    fun `returns failure with exception when returned id from api is null`() = runTest {
        coEvery { articlesRepository.getHeadlineArticleId(HEADLINE_ID) } returns null

        val result = getHeadlineArticleIdUseCase(HEADLINE_ID)
        val exception = Throwable("Id is null or blank for $HEADLINE_ID")
        assertThat(result.isFailure).isTrue()
        assertThat(exception.message).isEqualTo("Id is null or blank for $HEADLINE_ID")
    }

    @Test
    fun `returns failure with exception when api call fails`() = runTest {
        val throwableMessage = "Error"
        coEvery { articlesRepository.getHeadlineArticleId(HEADLINE_ID) } throws Throwable(throwableMessage)

        val result = getHeadlineArticleIdUseCase(HEADLINE_ID)
        val exception = Throwable(throwableMessage)
        assertThat(result.isFailure).isTrue()
        assertThat(exception.message).isEqualTo(throwableMessage)
    }
}