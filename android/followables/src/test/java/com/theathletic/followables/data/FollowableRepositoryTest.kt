package com.theathletic.followables.data

import com.google.common.truth.Truth
import com.theathletic.entity.main.League
import com.theathletic.followable.AuthorDao
import com.theathletic.followable.Followable
import com.theathletic.followable.LeagueDao
import com.theathletic.followable.TeamDao
import com.theathletic.followables.data.remote.FollowableFetcher
import com.theathletic.followables.data.remote.FollowableItemsFetcherV2
import com.theathletic.followables.data.remote.UnfollowFetcher
import com.theathletic.followables.data.remote.UserFollowingFetcher
import com.theathletic.followables.test.fixtures.authorFixture
import com.theathletic.followables.test.fixtures.authorIdFixture
import com.theathletic.followables.test.fixtures.leagueFixture
import com.theathletic.followables.test.fixtures.leagueIdFixture
import com.theathletic.followables.test.fixtures.localAuthorFixture
import com.theathletic.followables.test.fixtures.localLeagueFixture
import com.theathletic.followables.test.fixtures.localTeamFixture
import com.theathletic.followables.test.fixtures.teamFixture
import com.theathletic.followables.test.fixtures.teamIdFixture
import com.theathletic.repository.user.AuthorLocal
import com.theathletic.repository.user.FollowableDao
import com.theathletic.repository.user.LeagueLocal
import com.theathletic.repository.user.TeamLocal
import com.theathletic.repository.user.UserFollowingDao
import com.theathletic.scores.data.SupportedLeagues
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.assertStream
import com.theathletic.test.runTest
import com.theathletic.test.testFlowOf
import com.theathletic.utility.IPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import java.util.Date
import kotlin.test.assertEquals

private val testTeamFollowable = TeamLocal(
    id = Followable.Id(id = "1", type = Followable.Type.TEAM),
    name = "Test",
    shortName = "TST",
    url = "TSTL",
    searchText = "TST",
    colorScheme = TeamLocal.ColorScheme(primaryColor = "00FF00", iconContrastColor = "FF0000"),
    leagueId = Followable.Id(id = "1", type = Followable.Type.LEAGUE),
    displayName = "Tst",
    graphqlId = "graphqlid"
)
private val testLeagueFollowable = LeagueLocal(
    id = Followable.Id(id = "1", type = Followable.Type.LEAGUE),
    name = "Test League",
    shortName = "TSTL",
    sportType = "TEST",
    url = "TSTL",
    searchText = "TSTL",
    league = League.NCAA_FB,
    displayName = "TSTL",
)

private val testAuthorFollowable = AuthorLocal(
    id = Followable.Id(id = "1", type = Followable.Type.AUTHOR),
    name = "Test Author",
    shortName = "TSTA",
    searchText = "TSTA",
    imageUrl = "imageurl"
)

@OptIn(ExperimentalCoroutinesApi::class)
internal class FollowableRepositoryTest {

    @get:Rule val coroutineTestRule = CoroutineTestRule()

    @Mock private lateinit var followableItemsFetcherV2: FollowableItemsFetcherV2
    @Mock private lateinit var unfollowFetcher: UnfollowFetcher
    @Mock private lateinit var followableDao: FollowableDao
    @Mock private lateinit var userFollowingFetcher: UserFollowingFetcher
    @Mock private lateinit var followableFetcher: FollowableFetcher

    @Mock private lateinit var teamDao: TeamDao
    @Mock private lateinit var leagueDao: LeagueDao
    @Mock private lateinit var authorDao: AuthorDao
    @Mock private lateinit var userFollowingDao: UserFollowingDao

    private lateinit var followableRepository: FollowableRepository
    private lateinit var userFollowingRepository: UserFollowingRepository
    private lateinit var preferences: IPreferences

    class TestPreferences : IPreferences {
        override var kochavaArticleId: String? = null
        override var kochavaArticleDate: Date? = null
        override var articlesRatings: HashMap<String, Long> = hashMapOf()
        override var giftsPendingPaymentDataJson: String? = null
        override var accessToken: String? = null
        override var logGoogleSubLastToken: String? = null
        override var lastGoogleSubArticleId: Long? = null
        override var lastGoogleSubPodcastId: Long? = null
        override var lastDeclinedUpdateVersionCode: Int = 0
        override var hasSeenWebViewVersionNotice: Boolean? = false
        override var followablesOrder: Map<String, Int> = emptyMap()
        override val followablesOrderStateFlow: StateFlow<Map<String, Int>>
            get() = MutableStateFlow(followablesOrder)
        override var hasCustomFollowableOrder: Boolean = false
        override var pushTokenKey: String? = null
    }

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        preferences = TestPreferences()
        followableRepository = FollowableRepository(
            coroutineTestRule.dispatcherProvider,
            followableItemsFetcherV2,
            followableDao,
            teamDao,
            leagueDao,
            authorDao,
            SupportedLeagues()
        )

        userFollowingRepository = UserFollowingRepository(
            coroutineTestRule.dispatcherProvider,
            userFollowingFetcher,
            followableFetcher,
            unfollowFetcher,
            userFollowingDao,
            preferences
        )

        whenever(userFollowingDao.getFollowingTeamsDistinct()).thenReturn(
            MutableStateFlow(listOf(localTeamFixture()))
        )
        whenever(userFollowingDao.getFollowingLeaguesDistinct()).thenReturn(
            MutableStateFlow(listOf(localLeagueFixture()))
        )
        whenever(userFollowingDao.getFollowingAuthorsDistinct()).thenReturn(
            MutableStateFlow(listOf(localAuthorFixture()))
        )
    }

    @Test
    fun `Get followable from each type`() = runTest {
        whenever(followableDao.getTeam(Followable.Id("1", Followable.Type.TEAM))).thenReturn(testTeamFollowable)
        whenever(followableDao.getLeague(Followable.Id("1", Followable.Type.LEAGUE))).thenReturn(testLeagueFollowable)
        whenever(followableDao.getAuthor(Followable.Id("1", Followable.Type.AUTHOR))).thenReturn(testAuthorFollowable)

        val team = followableRepository.getFollowable(Followable.Id("1", Followable.Type.TEAM))
        val league = followableRepository.getFollowable(Followable.Id("1", Followable.Type.LEAGUE))
        val author = followableRepository.getFollowable(Followable.Id("1", Followable.Type.AUTHOR))

        assertEquals(team, testTeamFollowable)
        assertEquals(league, testLeagueFollowable)
        assertEquals(author, testAuthorFollowable)
    }

    @Test
    fun `Get team with both graphqlId and FollowableId overload`() = runTest {
        whenever(followableDao.getTeam(Followable.Id("1", Followable.Type.TEAM))).thenReturn(testTeamFollowable)
        whenever(followableDao.getTeam("graphqlid")).thenReturn(testTeamFollowable)

        val teamFollowableId = followableRepository.getTeam(Followable.Id("1", Followable.Type.TEAM))
        val teamGqlid = followableRepository.getTeam("graphqlid")

        assertEquals(teamFollowableId, testTeamFollowable)
        assertEquals(teamGqlid, testTeamFollowable)
    }

    @Test
    fun `user following is listed by teams, leagues and authors`() = runTest {
        val followableIds = listOf(
            teamIdFixture(),
            leagueIdFixture(),
            authorIdFixture()
        )
        stubFollowableEntities(followableIds)

        val testFlow = testFlowOf(userFollowingRepository.userFollowingStream)

        Truth.assertThat(preferences.followablesOrderStateFlow.value).isEmpty()
        assertStream(testFlow)
            .hasReceivedExactly(
                listOf(
                    teamFixture(),
                    leagueFixture(),
                    authorFixture()
                )
            )

        testFlow.finish()
    }

    @Test
    fun `order the followable list according to saved user ordering`() = runTest {
        val followableIds = listOf(
            teamIdFixture(id = "team1"),
            teamIdFixture(id = "team2"),
            leagueIdFixture(id = "league1"),
            leagueIdFixture(id = "league2"),
            authorIdFixture(id = "author1"),
            authorIdFixture(id = "author2")
        )
        val followableIdOrder = followableIds.reversed()

        saveFollowableOrder(followableIdOrder)
        stubFollowableEntities(followableIds)

        val testFlow = testFlowOf(userFollowingRepository.userFollowingStream)

        assertStream(testFlow).hasReceivedExactly(
            listOf(
                authorFixture(id = authorIdFixture("author2")),
                authorFixture(id = authorIdFixture("author1")),
                leagueFixture(id = leagueIdFixture("league2")),
                leagueFixture(id = leagueIdFixture("league1")),
                teamFixture(id = teamIdFixture("team2")),
                teamFixture(id = teamIdFixture("team1")),
            )
        )

        testFlow.finish()
    }

    @Test
    fun `followable with no order defined comes first`() = runTest {
        val followableIds = listOf(
            teamIdFixture(id = "team1"),
            teamIdFixture(id = "team2"),
            leagueIdFixture(id = "league1"),
            leagueIdFixture(id = "league2"),
            authorIdFixture(id = "author1"),
            authorIdFixture(id = "author2")
        )
        val followableIdOrder = listOf(
            leagueIdFixture(id = "league2"),
            teamIdFixture(id = "team2"),
            teamIdFixture(id = "team1"),
            leagueIdFixture(id = "league1"),
        )

        saveFollowableOrder(followableIdOrder)
        stubFollowableEntities(followableIds)

        val testFlow = testFlowOf(userFollowingRepository.userFollowingStream)

        assertStream(testFlow).hasReceivedExactly(
            listOf(
                authorFixture(id = authorIdFixture("author1")),
                authorFixture(id = authorIdFixture("author2")),
                leagueFixture(id = leagueIdFixture("league2")),
                teamFixture(id = teamIdFixture("team2")),
                teamFixture(id = teamIdFixture("team1")),
                leagueFixture(id = leagueIdFixture("league1")),
            )
        )

        testFlow.finish()
    }

    private fun saveFollowableOrder(followableIds: List<Followable.Id>) {
        val ordering = followableIds.withIndex().associate { it.value.toString() to it.index }
        userFollowingRepository.saveFollowablesReordering(ordering)
    }

    private fun stubFollowableEntities(ids: List<Followable.Id>) {
        val teams = ids.filter { it.type == Followable.Type.TEAM }.map { localTeamFixture(it) }
        val leagues =
            ids.filter { it.type == Followable.Type.LEAGUE }.map { localLeagueFixture(it) }
        val authors =
            ids.filter { it.type == Followable.Type.AUTHOR }.map { localAuthorFixture(it) }

        whenever(userFollowingDao.getFollowingTeamsDistinct()).thenReturn(flowOf(teams))
        whenever(userFollowingDao.getFollowingLeaguesDistinct()).thenReturn(flowOf(leagues))
        whenever(userFollowingDao.getFollowingAuthorsDistinct()).thenReturn(flowOf(authors))
    }
}