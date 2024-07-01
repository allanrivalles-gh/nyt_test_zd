package com.theathletic.followables

import com.theathletic.followables.data.FollowableRepository
import com.theathletic.followables.data.UserFollowingRepository
import com.theathletic.followables.test.fixtures.authorFixture
import com.theathletic.followables.test.fixtures.authorFollowingFixture
import com.theathletic.followables.test.fixtures.leagueFixture
import com.theathletic.followables.test.fixtures.leagueFollowingFixture
import com.theathletic.followables.test.fixtures.leagueIdFixture
import com.theathletic.followables.test.fixtures.teamFixture
import com.theathletic.followables.test.fixtures.teamFollowingFixture
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.assertStream
import com.theathletic.test.runTest
import com.theathletic.test.testFlowOf
import com.theathletic.utility.LogoUtility
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class ObserveUserFollowingUseCaseTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule val coroutineTestRule = CoroutineTestRule()

    @Mock private lateinit var followableRepository: FollowableRepository
    @Mock private lateinit var userFollowingRepository: UserFollowingRepository
    private lateinit var observeUserFollowing: ObserveUserFollowingUseCase

    @Before
    fun setUp() {
        val league = leagueFixture(
            leagueIdFixture("ncaaId"),
            name = "NCAA",
            shortName = "NCAA"
        )
        runBlocking { whenever(followableRepository.getCollegeLeagues()).thenReturn(listOf(league)) }
        observeUserFollowing = ObserveUserFollowingUseCase(
            followableRepository,
            userFollowingRepository,
            LogoUtility
        )
    }

    @Test
    fun `emits user following item when the user is following a team`() = runTest {
        whenever(userFollowingRepository.userFollowingStream).then { flowOf(listOf(teamFixture())) }

        val testFlow = testFlowOf(observeUserFollowing())

        assertStream(testFlow).lastEvent().isEqualTo(listOf(teamFollowingFixture()))
    }

    @Test
    fun `emits user following league when the user is following a league`() = runTest {
        whenever(userFollowingRepository.userFollowingStream).then { flowOf(listOf(leagueFixture())) }

        val testFlow = testFlowOf(observeUserFollowing())

        assertStream(testFlow).lastEvent().isEqualTo(listOf(leagueFollowingFixture()))
    }

    @Test
    fun `emits user following author when the user is following a author`() = runTest {
        whenever(userFollowingRepository.userFollowingStream).then { flowOf(listOf(authorFixture())) }

        val testFlow = testFlowOf(observeUserFollowing())

        assertStream(testFlow).lastEvent().isEqualTo(listOf(authorFollowingFixture()))
    }

    @Test
    fun `user following college team name is followed by the league short name between parentheses`() =
        runTest {
            val team = teamFixture(
                name = "Virginia",
                leagueId = leagueIdFixture(id = "ncaaId")
            )
            whenever(userFollowingRepository.userFollowingStream).then { flowOf(listOf(team)) }

            val testFlow = testFlowOf(observeUserFollowing())

            assertStream(testFlow).lastEvent()
                .isEqualTo(
                    listOf(
                        teamFollowingFixture(
                            name = "Virginia (NCAA)",
                            shortName = "NCAA"
                        )
                    )
                )
        }

    @Test
    fun `user following college team short names becomes the league short name`() = runTest {
        val team = teamFixture(
            name = "Virginia",
            leagueId = leagueIdFixture(id = "ncaaId")
        )
        whenever(userFollowingRepository.userFollowingStream).then { flowOf(listOf(team)) }

        val testFlow = testFlowOf(observeUserFollowing())

        assertStream(testFlow).lastEvent()
            .isEqualTo(
                listOf(teamFollowingFixture(name = "Virginia (NCAA)", shortName = "NCAA"))
            )
    }
}