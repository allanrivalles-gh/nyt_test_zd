package com.theathletic.followables

import com.theathletic.followables.data.FollowableRepository
import com.theathletic.followables.data.UserFollowingRepository
import com.theathletic.followables.data.domain.Filter
import com.theathletic.followables.data.domain.Followable
import com.theathletic.followables.data.domain.FollowableItem
import com.theathletic.followables.test.fixtures.authorFixture
import com.theathletic.followables.test.fixtures.authorIdFixture
import com.theathletic.followables.test.fixtures.leagueFixture
import com.theathletic.followables.test.fixtures.leagueIdFixture
import com.theathletic.followables.test.fixtures.teamFixture
import com.theathletic.followables.test.fixtures.teamIdFixture
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.assertStream
import com.theathletic.test.runTest
import com.theathletic.test.testFlowOf
import com.theathletic.utility.LogoUtility
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class ListFollowableUseCaseTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule val coroutineTestRule = CoroutineTestRule()

    private lateinit var listFollowableUseCase: ListFollowableUseCase
    @Mock lateinit var userFollowingRepository: UserFollowingRepository
    @Mock lateinit var followableRepository: FollowableRepository

    private val followableList = createTeamList("Lakers", "Golden State", "Arsenal", "Real Madrid") +
        createLeagueList(
            Pair("NBA", "National Basketball Association"),
            Pair("NFL", "National Football League"),
            Pair("EPL", "Premier League"),
            Pair("LIGA", "La Liga Primera Division"),
            Pair("UCL", "UEFA Champions League")
        ) +
        createAuthorList("Richard", "Laura")

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        whenever(followableRepository.followableStream).thenReturn(flowOf(followableList))

        whenever(userFollowingRepository.userFollowingStream).thenReturn(flowOf(emptyList()))

        listFollowableUseCase = ListFollowableUseCase(
            followableRepository,
            userFollowingRepository,
            LogoUtility
        )
    }

    @Test
    fun `return all followable item list when the filter query is empty and type is ALL`() = runTest {
        val testFlow = testFlowOf(listFollowableUseCase(Filter.Simple(query = "", type = Filter.Type.ALL)))

        coroutineTestRule.advanceTimeBy(301)

        assertStream(testFlow).hasReceivedExactly(followableList.mapToItem())
        testFlow.finish()
    }

    @Test
    fun `filter followable by name`() = runTest {
        val testFlow = testFlowOf(listFollowableUseCase(Filter.Simple(query = "Lakers", type = Filter.Type.ALL)))

        coroutineTestRule.advanceTimeBy(301)

        assertStream(testFlow)
            .hasReceivedExactly(createTeamList("Lakers").mapToItem())

        testFlow.finish()
    }

    @Test
    fun `filters followable by TEAM type`() = runTest {
        val testFlow = testFlowOf(listFollowableUseCase(Filter.Simple(query = "", type = Filter.Type.TEAM)))

        coroutineTestRule.advanceTimeBy(301)

        assertStream(testFlow)
            .hasReceivedExactly(createTeamList("Lakers", "Golden State", "Arsenal", "Real Madrid").mapToItem())
        testFlow.finish()
    }

    @Test
    fun `filters followable by LEAGUE type`() = runTest {
        val testFlow = testFlowOf(listFollowableUseCase(Filter.Simple(query = "", type = Filter.Type.LEAGUE)))

        coroutineTestRule.advanceTimeBy(301)

        assertStream(testFlow).hasReceivedExactly(
            createLeagueList(
                Pair("NBA", "National Basketball Association"),
                Pair("NFL", "National Football League"),
                Pair("EPL", "Premier League"),
                Pair("LIGA", "La Liga Primera Division"),
                Pair("UCL", "UEFA Champions League")
            ).mapToItem()
        )
        testFlow.finish()
    }

    @Test
    fun `filters followable by AUTHOR  type`() = runTest {
        val testFlow = testFlowOf(listFollowableUseCase(Filter.Simple(query = "", type = Filter.Type.AUTHOR)))

        coroutineTestRule.advanceTimeBy(301)

        assertStream(testFlow)
            .hasReceivedExactly(createAuthorList("Richard", "Laura").mapToItem())
        testFlow.finish()
    }

    @Test
    fun `filters followable by FollowableId Single type`() = runTest {
        val followableId = teamIdFixture("Lakers")
        val testFlow = testFlowOf(listFollowableUseCase(Filter.Single(followableId)))

        coroutineTestRule.advanceTimeBy(301)

        assertStream(testFlow)
            .hasReceivedExactly(createTeamList("Lakers").mapToItem())
        testFlow.finish()
    }

    @Test
    fun `models correct isFollowing status for unfollowed`() = runTest {
        val followableId = teamIdFixture("Lakers")
        val testFlow = testFlowOf(listFollowableUseCase(Filter.Single(followableId)))
        val expectedResult = createTeamList("Lakers").mapToItem().map { it.copy(isFollowing = false) }

        coroutineTestRule.advanceTimeBy(301)

        assertStream(testFlow).hasReceivedExactly(expectedResult)
        testFlow.finish()
    }

    @Test
    fun `models correct isFollowing status for followed`() = runTest {
        val followableId = teamIdFixture("Lakers")
        val followable = teamFixture(id = followableId)
        whenever(userFollowingRepository.userFollowingStream).thenReturn(flowOf(listOf(followable)))
        val testFlow = testFlowOf(listFollowableUseCase(Filter.Single(followableId)))
        val expectedResult = listOf(followable).mapToItem().map { it.copy(isFollowing = true) }

        coroutineTestRule.advanceTimeBy(301)

        assertStream(testFlow).hasReceivedExactly(expectedResult)
        testFlow.finish()
    }

    private fun createTeamList(vararg name: String) = name.map {
        teamFixture(id = teamIdFixture(id = it), name = it)
    }

    private fun createLeagueList(vararg league: Pair<String, String>) = league.map {
        leagueFixture(id = leagueIdFixture(id = it.first), name = it.second, displayName = it.first)
    }

    private fun createAuthorList(vararg name: String) = name.map {
        authorFixture(id = authorIdFixture(id = it), name = it)
    }

    private fun List<Followable>.mapToItem() = map { followable ->
        FollowableItem(followable.id, followable.name, "", false)
    }
}