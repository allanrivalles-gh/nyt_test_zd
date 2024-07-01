package com.theathletic.scores.data.di

import com.theathletic.datetime.TimeProvider
import com.theathletic.scores.data.ScheduleRepository
import com.theathletic.scores.di.ScheduleUpdatesSubscriptionManager
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
class ScheduleUpdatesSubscriptionManagerTest {

    @get:Rule val coroutineTestRule = CoroutineTestRule()

    @Mock private lateinit var mockTimeProvider: TimeProvider
    @Mock private lateinit var mockScheduleRepository: ScheduleRepository

    private lateinit var subscriptionManager: ScheduleUpdatesSubscriptionManager

    @BeforeTest
    fun setUp() {
        subscriptionManager = ScheduleUpdatesSubscriptionManager(
            coroutineTestRule.dispatcherProvider,
            mockTimeProvider,
            mockScheduleRepository
        )
    }

    @Test
    fun `correct ids are extracted from the schedule for games that require updates`() = runTest {
        whenever(mockScheduleRepository.getScheduleFeedGroup(key = "testId", groupId = "groupId", filterId = null))
            .thenReturn(scheduleWithSomeGamesThatHaveSubscriptionUpdates())

        subscriptionManager.subscribeForUpdates(entityKey = "testId", groupId = "groupId", filterId = null, forceSubscriptionCheck = false)

        // todo: looking at extracting this functionality out into a UC in the VM as per my comment in the PR so this
        //  will go most likely.
    }

    private fun scheduleWithSomeGamesThatHaveSubscriptionUpdates() =
        scheduleGroupFixture(
            sections = listOf(
                scheduleSectionFixture(
                    games = scheduleGamesWithSomeWillUpdateGames()
                )
            )
        )
}