package com.theathletic.hub.league.ui

import com.google.common.truth.Truth.assertThat
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.scores.standings.data.local.RelegationStatus
import com.theathletic.scores.standings.data.local.Standing
import com.theathletic.scores.standings.data.local.StandingsGroup
import com.theathletic.scores.standings.data.local.StandingsGrouping
import com.theathletic.scores.standings.data.local.StandingsGroupingType
import org.junit.Test

class FilterStandingsUseCaseTest {

    private val useCase = FilterStandingsUseCase()

    @Test
    fun `when a standings group contains empty standings then that group gets filtered out`() {
        val filtered = useCase(StandingsFixture.standingsGroupingsFixture())
        assertThat(filtered.size).isEqualTo(4)
        assertThat(filtered.map { it.id }).isEqualTo(
            listOf(
                "Grouping_1",
                "Grouping_2",
                "Grouping_3",
                "Grouping_5",
            )
        )
    }
}

object StandingsFixture {

    fun standingsGroupingsFixture() = listOf(
        standingsGroupingFixture(
            id = "Grouping_1",
            standingGroups = listOf(
                standingGroupFixture(
                    listOf(standingFixture("s1"))
                )
            )
        ),
        standingsGroupingFixture(
            "Grouping_2",
            standingGroups = listOf(
                standingGroupFixture(
                    listOf(standingFixture("s2"))
                ),
                standingGroupFixture(
                    listOf(standingFixture("s3"))
                ),
            )
        ),
        standingsGroupingFixture(
            "Grouping_3",
            standingGroups = listOf(
                standingGroupFixture(
                    listOf(standingFixture("s4"))
                ),
                standingGroupFixture(
                    emptyList()
                ),
            )
        ),
        standingsGroupingFixture(
            "Grouping_4",
            standingGroups = listOf(
                standingGroupFixture(emptyList())
            )
        ),
        standingsGroupingFixture(
            "Grouping_5",
            standingGroups = listOf(
                standingGroupFixture(
                    listOf(standingFixture("s5"))
                )
            )
        ),
    )

    private fun standingsGroupingFixture(
        id: String,
        standingGroups: List<StandingsGroup>
    ) =
        StandingsGrouping(
            id = id,
            type = StandingsGroupingType.CONFERENCE,
            groups = standingGroups,
            headers = null,
            showRank = false,
            groupLabel = id
        )

    private fun standingGroupFixture(
        standings: List<Standing>
    ) = StandingsGroup(
        id = "id",
        name = "name",
        columns = emptyMap(),
        segments = emptyList(),
        standings = standings
    )

    private fun standingFixture(
        id: String
    ) =
        Standing(
            id = id,
            team = teamFixture(),
            rank = 1,
            relegationStatus = RelegationStatus.UNKNOWN,
            points = null,
            played = null,
            won = null,
            lost = null,
            drawn = null,
            pointsFor = null,
            pointsAgainst = null,
            difference = null,
            winPct = null,
            divRecord = null,
            confRecord = null,
            streak = null,
            lostOvertime = null,
            awayRecord = null,
            homeRecord = null,
            lastTenRecord = null,
            lastSix = null,
            gamesBehind = null,
            eliminationNumber = null
        )

    private fun teamFixture() =
        GameDetailLocalModel.Team(
            id = "team",
            alias = "Alias",
            name = "Name",
            displayName = "Display Name",
            logos = emptyList(),
            primaryColor = null,
            accentColor = null,
            currentRanking = null
        )
}