package com.theathletic.boxscore.data

import com.apollographql.apollo3.api.ApolloResponse
import com.google.common.truth.Truth.assertThat
import com.theathletic.GetBoxScoreFeedQuery
import com.theathletic.boxscore.data.local.BoxScoreLocalDataSource
import com.theathletic.boxscore.data.remote.BoxScoreApi
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.runTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.fail

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class BoxScoreRepositoryTest {

    @get:Rule val coroutineTestRule = CoroutineTestRule()

    @Mock private lateinit var mockBoxScoreLocalDataSource: BoxScoreLocalDataSource
    @Mock private lateinit var mockBoxScoreApi: BoxScoreApi
    private lateinit var boxScoreRepository: BoxScoreRepository

    @BeforeTest
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        boxScoreRepository = BoxScoreRepository(
            mockBoxScoreApi,
            mockBoxScoreLocalDataSource
        )
    }

    @Test
    fun `throws BoxScore exception when trying to fetch boxscore`() = runTest {
        whenever(mockBoxScoreApi.getBoxScoreFeed("gameId")).then { throw Exception("Error fetching box score for game Id: gameId with message Fail to get Box Score") }

        try {
            boxScoreRepository.fetchBoxScoreFeed("gameId")
            fail("Error fetching box score for game Id: gameId with message Fail to get Box Score")
        } catch (e: Exception) {
            println(e.message)
            assertThat(e.message).isNotNull()
            assertThat(e.message).isEqualTo("Error fetching box score for game Id: gameId with message Fail to get Box Score")
        }
    }

    @Test
    fun `stores boxscore data in memory when we fetch the boxscore with no errors`() = runTest {
        val response = ApolloResponse.Builder(GetBoxScoreFeedQuery("gameId"), UUID.randomUUID(), null).build()

        whenever(mockBoxScoreApi.getBoxScoreFeed("gameId")).thenReturn(response.data)

        try {
            boxScoreRepository.fetchBoxScoreFeed("gameId")
        } catch (e: Exception) {
            fail("No exception is thrown")
        }
    }
}