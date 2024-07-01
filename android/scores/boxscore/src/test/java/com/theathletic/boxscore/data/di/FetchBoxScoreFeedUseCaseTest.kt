package com.theathletic.boxscore.data.di

import com.google.common.truth.Truth.assertThat
import com.theathletic.boxscore.FetchBoxScoreFeedUseCase
import com.theathletic.boxscore.data.BoxScoreRepository
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.runTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import kotlin.test.BeforeTest

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class FetchBoxScoreFeedUseCaseTest {

    @get:Rule val coroutineTestRule = CoroutineTestRule()

    @Mock private lateinit var mockBoxScoreRepository: BoxScoreRepository
    private lateinit var fetchBoxScoreFeedUseCase: FetchBoxScoreFeedUseCase

    @BeforeTest
    fun setUp() {
        fetchBoxScoreFeedUseCase = FetchBoxScoreFeedUseCase(mockBoxScoreRepository)
    }

    @Test
    fun `exception thrown when fetching box score returns a failure`() = runTest {
        whenever(mockBoxScoreRepository.fetchBoxScoreFeed("gameId")).then { throw Exception() }

        val result = fetchBoxScoreFeedUseCase("gameId")
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `success is returned when requesting to fetch a box score feed`() = runTest {
        val result = fetchBoxScoreFeedUseCase("gameId")
        assertThat(result.isSuccess).isTrue()
    }
}