package com.theathletic.scores.data.di

import com.google.common.truth.Truth.assertThat
import com.theathletic.scores.data.ScheduleRepository
import com.theathletic.scores.di.FetchScheduleFeedGroupUseCase
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
class FetchScheduleFeedGroupUseCaseTest {

    @get:Rule val coroutineTestRule = CoroutineTestRule()

    @Mock private lateinit var mockRepository: ScheduleRepository
    private lateinit var fetchUseCase: FetchScheduleFeedGroupUseCase

    @BeforeTest
    fun setUp() {
        fetchUseCase = FetchScheduleFeedGroupUseCase(mockRepository)
    }

    @Test
    fun `Failure Result is returned when requesting to fetch schedule group and an error occurs`() = runTest {
        whenever(mockRepository.getScheduleFeedGroup("entityId", "groupId", "filterId")).thenReturn(null)
        whenever(mockRepository.fetchScheduleFeedGroup("entityId", "groupId", "filterId")).then { throw Exception() }

        val result = fetchUseCase("entityId", false, "groupId", "filterId")
        assertThat(result.isFailure).isEqualTo(true)
    }

    @Test
    fun `Success Result is returned when requesting to fetch schedule with no group and is successful`() = runTest {
        whenever(mockRepository.getScheduleFeedGroup("entityId", "groupId", "filterId")).thenReturn(null)

        val result = fetchUseCase("entityId", false, "groupId", "filterId")
        assertThat(result.isSuccess).isEqualTo(true)
        assertThat(result.getOrNull()).isEqualTo(null)
    }

    @Test
    fun `Success Result is returned when returning existing schedule group`() = runTest {
        val testFixture = getScheduleWithAValidGroup()
        whenever(mockRepository.getScheduleFeedGroup("entityId", "groupId", "filterId")).thenReturn(testFixture)

        val result = fetchUseCase("entityId", false, "groupId", "filterId")
        assertThat(result.isSuccess).isEqualTo(true)
        assertThat(result.getOrNull()).isEqualTo(testFixture)
    }

    @Test
    fun `Success Result is returned when returning existing schedule group with null filter ID`() = runTest {
        val testFixture = getScheduleWithAValidGroup()
        whenever(mockRepository.getScheduleFeedGroup("entityId", "groupId", null)).thenReturn(testFixture)

        val result = fetchUseCase("entityId", false, "groupId", null)
        assertThat(result.isSuccess).isEqualTo(true)
        assertThat(result.getOrNull()).isEqualTo(testFixture)
    }

    @Test
    fun `Failure Result is returned when requesting to fetch schedule with different filter Id`() = runTest {
        val result = fetchUseCase("entityId", false, "groupId", "filterId2")
        assertThat(result.isSuccess).isEqualTo(true)
        assertThat(result.getOrNull()).isEqualTo(null)
    }

    private fun getScheduleWithAValidGroup() = scheduleGroupFixture(
        sections = listOf(
            scheduleSectionFixture(
                games = listOf(
                    scheduleGameFixture(
                        team1 = scheduleTeamFixture("Team1"),
                        team2 = scheduleTeamFixture("Team2")
                    )
                )
            )
        )
    )

    private fun getScheduleWithAnEmptyGroup() = scheduleGroupFixture(
        sections = listOf(scheduleSectionFixture(games = emptyList()))
    )
}