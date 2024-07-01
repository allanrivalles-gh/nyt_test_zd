package com.theathletic.gamedetail.boxscore.ui.standings

import com.theathletic.followable.FollowableId
import com.theathletic.followable.FollowableType
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.followables.data.domain.Followable
import com.theathletic.hub.league.ui.StandingsFixture
import com.theathletic.scores.standings.ui.NonNavigableStandingsTeamsUseCase
import com.theathletic.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class NonNavigableStandingsTeamsUseCaseTest {

    private val standingsTeam = Followable.Team(
        id = FollowableId("01", FollowableType.TEAM),
        leagueId = FollowableId("01", FollowableType.LEAGUE),
        name = "name",
        displayName = "displayName",
        shortName = "shortName",
        searchText = "",
        color = "primaryColor",
        graphqlId = "A",
        url = ""
    )

    @Mock lateinit var followableRepository: FollowableRepository
    private lateinit var useCase: NonNavigableStandingsTeamsUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = NonNavigableStandingsTeamsUseCase(followableRepository)
    }

    @Test
    fun `when standings have teams that are not followable`() = runTest {
        whenever(followableRepository.getAllTeams()).thenReturn(listOf(standingsTeam))
        val nonNavigableTeams = useCase(StandingsFixture.standingsGroupingsFixture())
        assert(nonNavigableTeams.isNotEmpty())
    }

    @Test
    fun `when standings have teams that are followable`() = runTest {
        whenever(followableRepository.getAllTeams()).thenReturn(listOf(standingsTeam.copy(graphqlId = "team")))
        val nonNavigableTeams = useCase(StandingsFixture.standingsGroupingsFixture())
        assert(nonNavigableTeams.isEmpty())
    }
}