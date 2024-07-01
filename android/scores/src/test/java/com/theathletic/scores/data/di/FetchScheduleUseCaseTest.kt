package com.theathletic.scores.data.di

import com.google.common.truth.Truth.assertThat
import com.theathletic.entity.main.League
import com.theathletic.scores.data.ScheduleRepository
import com.theathletic.scores.di.FetchScheduleUseCase
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.runTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import kotlin.test.BeforeTest
import kotlin.test.Test

private val testLeague = League.NFL

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class FetchScheduleUseCaseTest {

    @get:Rule val coroutineTestRule = CoroutineTestRule()

    @Mock private lateinit var mockScheduleRepository: ScheduleRepository
    private lateinit var fetchUseCase: FetchScheduleUseCase

    @BeforeTest
    fun setUp() {
        fetchUseCase = FetchScheduleUseCase(mockScheduleRepository)
    }

    @Test
    fun `Failure Result is returned when requesting to fetch a team schedule and an error occurs`() = runTest {
        whenever(mockScheduleRepository.fetchTeamSchedule("teamId")).then { throw Exception() }

        val result = fetchUseCase("teamId", isLeague = false)
        assertThat(result.isFailure).isEqualTo(true)
    }

    @Test
    fun `Success Result is returned when requesting to fetch a team schedule and is successful`() = runTest {
        val result = fetchUseCase("teamId", isLeague = false)
        assertThat(result.isSuccess).isEqualTo(true)
    }

    @Test
    fun `Failure Result is returned when requesting to fetch a league schedule and an error occurs`() = runTest {
        whenever(mockScheduleRepository.fetchLeagueSchedule(testLeague)).then { throw Exception() }

        val result = fetchUseCase(testLeague.name, isLeague = true)
        assertThat(result.isFailure).isEqualTo(true)
    }

    @Test
    fun `Success Result is returned when requesting to fetch a league schedule and is successful`() = runTest {
        val result = fetchUseCase(testLeague.name, isLeague = true)
        assertThat(result.isSuccess).isEqualTo(true)
    }
}