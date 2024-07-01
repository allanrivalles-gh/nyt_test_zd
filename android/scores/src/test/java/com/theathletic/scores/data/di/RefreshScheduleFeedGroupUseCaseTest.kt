package com.theathletic.scores.data.di

import com.google.common.truth.Truth.assertThat
import com.theathletic.scores.data.ScheduleRepository
import com.theathletic.scores.di.RefreshScheduleFeedGroupUseCase
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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class RefreshScheduleFeedGroupUseCaseTest {

    @get:Rule val coroutineTestRule = CoroutineTestRule()

    @Mock private lateinit var mockRepository: ScheduleRepository
    private lateinit var refreshUseCase: RefreshScheduleFeedGroupUseCase

    @BeforeTest
    fun setUp() {
        refreshUseCase = RefreshScheduleFeedGroupUseCase(mockRepository)
    }

    @Test
    fun `failure result is returned when attempting to refresh a index that does not exist`() = runTest {
        whenever(mockRepository.getGroupIdForIndex("entityId", 5)).thenReturn(null)

        val result = refreshUseCase("entityId", false, 5, "filterId")
        assertThat(result.isFailure).isEqualTo(true)
    }

    @Test
    fun `success result is returned when refreshing a schedule and it is successful`() = runTest {
        whenever(mockRepository.getGroupIdForIndex("entityId", 5)).thenReturn("groupId")

        val result = refreshUseCase("entityId", false, 5, "filterId")
        assertThat(result.isSuccess).isEqualTo(true)
        assertThat(result.getOrNull()).isEqualTo(null)
    }

    @Test
    fun `failure Result is returned when refresging a schedule and an error occurs`() = runTest {
        whenever(mockRepository.getGroupIdForIndex("entityId", 5)).thenReturn("groupId")
        whenever(mockRepository.fetchScheduleFeedGroup("entityId", "groupId", "filterId")).then { throw Exception() }

        val result = refreshUseCase("entityId", false, 5, "filterId")
        assertThat(result.isFailure).isEqualTo(true)
    }
}