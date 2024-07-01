package com.theathletic.boxscore.data.di

import com.theathletic.boxscore.ObserveBoxScoreFeedUseCase
import com.theathletic.boxscore.data.BoxScoreFeedFixtures
import com.theathletic.boxscore.data.BoxScoreRepository
import com.theathletic.entity.authentication.UserData
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.assertStream
import com.theathletic.test.runTest
import com.theathletic.test.testFlowOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ObserveBoxScoreFeedUseCaseTest {
    @get:Rule val coroutineTestRule = CoroutineTestRule()

    @Mock private lateinit var mockBoxScoreRepository: BoxScoreRepository
    @Mock private lateinit var userDataRepository: IUserDataRepository
    private lateinit var observeBoxScoreFeedUseCase: ObserveBoxScoreFeedUseCase

    @BeforeTest
    fun setUp() {
        observeBoxScoreFeedUseCase = ObserveBoxScoreFeedUseCase(
            mockBoxScoreRepository,
            userDataRepository
        )
    }

    @Test
    fun `emits box score feed when a feed update is available`() = runTest {

        whenever(mockBoxScoreRepository.getBoxScoreFeed(any())).thenReturn(flowOf(BoxScoreFeedFixtures.boxScore))
        whenever(userDataRepository.userDataFlow).thenReturn(flowOf(null))

        val testFlow = testFlowOf(observeBoxScoreFeedUseCase("001"))

        assertStream(testFlow).lastEvent().isEqualTo(BoxScoreFeedFixtures.boxScore)
    }

    @Test
    fun `emits box score feed when a user data update is available`() = runTest {
        val gameId = "001"
        var userData = UserData().apply {
            articlesRead = arrayListOf(6)
        }

        whenever(mockBoxScoreRepository.getBoxScoreFeed(any())).thenReturn(flowOf(BoxScoreFeedFixtures.boxScore))
        whenever(userDataRepository.userDataFlow).thenReturn(flowOf(null))

        var testFlow = testFlowOf(observeBoxScoreFeedUseCase(gameId = gameId))

        // No article is read
        assertStream(testFlow).lastEvent().isEqualTo(BoxScoreFeedFixtures.boxScore)

        whenever(userDataRepository.userDataFlow).thenReturn(flowOf(userData))

        testFlow = testFlowOf(observeBoxScoreFeedUseCase(gameId = gameId))

        // one article is read, user data updated
        assertStream(testFlow).lastEvent().isEqualTo(BoxScoreFeedFixtures.boxScore)

        whenever(mockBoxScoreRepository.getBoxScoreFeed(any())).thenReturn(flowOf(BoxScoreFeedFixtures.boxScoreTwoArticles))

        testFlow = testFlowOf(observeBoxScoreFeedUseCase(gameId = gameId))

        // box score data updated (two articles)
        assertStream(testFlow).lastEvent().isEqualTo(BoxScoreFeedFixtures.boxScoreTwoArticles)

        userData = UserData().apply {
            articlesSaved = arrayListOf(6, 7)
        }

        whenever(userDataRepository.userDataFlow).thenReturn(flowOf(userData))

        testFlow = testFlowOf(observeBoxScoreFeedUseCase(gameId = gameId))

        // user data updated, both articles bookmarked
        assertStream(testFlow).lastEvent().isEqualTo(BoxScoreFeedFixtures.boxScoreTwoArticles)

        userData = UserData().apply {
            articlesSaved = arrayListOf(7)
        }

        whenever(userDataRepository.userDataFlow).thenReturn(flowOf(userData))

        testFlow = testFlowOf(observeBoxScoreFeedUseCase(gameId = gameId))

        // user data updated, one article bookmarked
        assertStream(testFlow).lastEvent().isEqualTo(BoxScoreFeedFixtures.boxScoreTwoArticles)
    }
}