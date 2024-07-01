package com.theathletic.comments.game

import com.google.common.truth.Truth.assertThat
import com.theathletic.comments.game.data.teamThreadsFixture
import com.theathletic.comments.game.data.threadFixture
import com.theathletic.followable.Followable
import com.theathletic.followable.FollowableId
import com.theathletic.followable.analyticsId
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.runTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class AnalyticsGameTeamIdUseCaseTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule val coroutineTestRule = CoroutineTestRule()

    @Mock private lateinit var teamThreadsRepository: TeamThreadsRepository

    private lateinit var analyticsGameTeamIdUseCase: AnalyticsGameTeamIdUseCase

    @Before
    fun setUp() {
        analyticsGameTeamIdUseCase = AnalyticsGameTeamIdUseCase(teamThreadsRepository)
    }

    @Test
    fun `analytics team id returns team id if game uses single team space`() = runTest {
        val firstThread = FollowableId("testId", Followable.Type.TEAM)
        whenever(teamThreadsRepository.fetchTeamThreads(firstThread.id)).thenReturn(
            teamThreadsFixture(
                currentThread = threadFixture(teamId = "testId")
            )
        )

        val response = analyticsGameTeamIdUseCase(true, firstThread.id)
        assertThat(response).isEqualTo(firstThread.analyticsId)
    }

    @Test
    fun `analytics team id returns null if game uses single team space but team is not followed`() = runTest {
        val firstThread = FollowableId("testId", Followable.Type.TEAM)
        whenever(teamThreadsRepository.fetchTeamThreads(firstThread.id)).thenReturn(
            null
        )
        val response = analyticsGameTeamIdUseCase(true, firstThread.id)
        assertThat(response).isEqualTo(null)
    }

    @Test
    fun `analytics team id returns null if game uses multi team space`() = runTest {
        val firstThread = FollowableId("testId", Followable.Type.TEAM)

        val response = analyticsGameTeamIdUseCase(false, firstThread.id)
        assertThat(response).isEqualTo(null)
    }
}