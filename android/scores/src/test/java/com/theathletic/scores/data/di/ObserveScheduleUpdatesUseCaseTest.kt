package com.theathletic.scores.data.di

import com.theathletic.scores.data.ScheduleRepository
import com.theathletic.scores.di.ObserveScheduleUpdatesUseCase
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
class ObserveScheduleUpdatesUseCaseTest {

    @get:Rule val coroutineTestRule = CoroutineTestRule()

    @Mock private lateinit var mockScheduleRepository: ScheduleRepository
    private lateinit var observeUseCase: ObserveScheduleUpdatesUseCase

    @BeforeTest
    fun setUp() {
        observeUseCase = ObserveScheduleUpdatesUseCase(mockScheduleRepository)
    }

    @Test
    fun `emits schedule when a schedule update is available`() = runTest {
        val testFixture = scheduleFixture(emptyList())
        whenever(mockScheduleRepository.getSchedule(any())).thenReturn(flowOf(testFixture))

        val testFlow = testFlowOf(observeUseCase("key"))

        assertStream(testFlow).lastEvent().isEqualTo(testFixture)
    }
}