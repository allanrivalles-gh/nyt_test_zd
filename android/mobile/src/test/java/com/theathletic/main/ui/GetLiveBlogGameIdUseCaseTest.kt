package com.theathletic.main.ui

import com.google.common.truth.Truth
import com.theathletic.liveblog.data.LiveBlogRepository
import com.theathletic.liveblog.ui.nativeLiveBlogFixture
import com.theathletic.test.runTest
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

private const val LIVE_BLOG_ID = "aFTdWfPG1KFt"
private const val GAME_ID = "tuqhRwo0KQ1gvVjA"
@RunWith(MockitoJUnitRunner::class)
class GetLiveBlogGameIdUseCaseTest {
    private val liveBlogRepository = mockk<LiveBlogRepository>(relaxed = true)
    private lateinit var getLiveBlogGameIdUseCase: GetLiveBlogGameIdUseCase

    @Before
    fun setUp() {
        getLiveBlogGameIdUseCase = GetLiveBlogGameIdUseCase(liveBlogRepository)
    }

    @Test
    fun `successfully returns game id string when api call is successful`() = runTest {
        coEvery { liveBlogRepository.getLiveBlog(LIVE_BLOG_ID) } returns nativeLiveBlogFixture(
            liveBlogId = LIVE_BLOG_ID,
            gameId = GAME_ID,
            isGame = true
        )

        val result = getLiveBlogGameIdUseCase(LIVE_BLOG_ID)
        Truth.assertThat(result).isEqualTo(Result.success(GAME_ID))
    }

    @Test
    fun `successfully returns null string when live blog is not a game`() = runTest {
        coEvery { liveBlogRepository.getLiveBlog(LIVE_BLOG_ID) } returns nativeLiveBlogFixture(
            liveBlogId = LIVE_BLOG_ID,
            gameId = GAME_ID,
            isGame = false
        )

        val result = getLiveBlogGameIdUseCase(LIVE_BLOG_ID)
        Truth.assertThat(result).isEqualTo(Result.success(null))
    }

    @Test
    fun `returns failure with exception when api call fails`() = runTest {
        val throwableMessage = "Error"
        coEvery { liveBlogRepository.getLiveBlog(LIVE_BLOG_ID) } throws Throwable(throwableMessage)

        val result = getLiveBlogGameIdUseCase(LIVE_BLOG_ID)
        val exception = Throwable(throwableMessage)
        Truth.assertThat(result.isFailure).isTrue()
        Truth.assertThat(exception.message).isEqualTo(throwableMessage)
    }
}