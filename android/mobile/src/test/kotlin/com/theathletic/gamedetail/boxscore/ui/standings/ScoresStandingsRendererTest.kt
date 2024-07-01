package com.theathletic.gamedetail.boxscore.ui.standings

import com.google.common.truth.Truth.assertThat
import com.theathletic.boxscore.ui.modules.StandingsTableModule
import com.theathletic.entity.main.League
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.scores.standings.data.local.RelegationStatus
import com.theathletic.scores.standings.data.local.Standing
import com.theathletic.scores.standings.data.local.StandingsGroup
import com.theathletic.scores.standings.data.local.StandingsGroupHeader
import com.theathletic.scores.standings.data.local.StandingsGrouping
import com.theathletic.scores.standings.data.local.StandingsGroupingType
import com.theathletic.scores.standings.ui.ScoresStandingsRenderer
import com.theathletic.utility.LocaleUtility
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class ScoresStandingsRendererTest {
    private lateinit var scoresStandingsRenderer: ScoresStandingsRenderer
    private val localeUtility = mockk<LocaleUtility>(relaxed = true)

    private val columnsWithPTSColumnNeedRearrangement = mapOf(
        "played" to "GP",
        "won" to "W",
        "drawn" to "D",
        "lost" to "L",
        "for" to "GF",
        "against" to "GA",
        "difference" to "GD",
        "points" to "PTS",
        "form" to "Form"
    )

    private val columnsWithPTSColumnDoesNotNeedRearrangement = mapOf(
        "played" to "GP",
        "points" to "PTS",
        "won" to "W",
        "drawn" to "D",
        "lost" to "L",
        "for" to "GF",
        "against" to "GA",
        "difference" to "GD",
        "form" to "Form"
    )

    private val columnsWithNoPTSColumn = mapOf(
        "played" to "GP",
        "won" to "W",
        "drawn" to "D",
        "lost" to "L",
        "for" to "GF",
        "against" to "GA",
        "difference" to "GD",
        "form" to "Form"
    )

    @Before
    fun setUp() {
        scoresStandingsRenderer = ScoresStandingsRenderer(localeUtility)
    }

    @Test
    fun `Rearranges PTS column when sport is Soccer and PTS current position is greater than one`() {
        val standings = scoresStandingsRenderer.renderStandingsModules(
            selectedGroupIndex = 0,
            selectedGroupName = null,
            groupings = standingsGroupingsFixture(columnsWithPTSColumnNeedRearrangement),
            League.EPL,
            null,
            emptyList(),
            false
        )

        assertThat(standings).isNotNull()

        val standingsTableModule = standings[1] as? StandingsTableModule
        val label = standingsTableModule?.statsColumns?.get(1)?.first() as? StandingsTableModule.StatsColumnItem.Label
        assertThat(label).isEqualTo(StandingsTableModule.StatsColumnItem.Label("PTS"))
    }

    @Test
    fun `Does not rearranges PTS column when sport is not Soccer and PTS current position is greater than one`() {
        val standings = scoresStandingsRenderer.renderStandingsModules(
            selectedGroupIndex = 0,
            selectedGroupName = null,
            groupings = standingsGroupingsFixture(columnsWithPTSColumnNeedRearrangement),
            League.NFL,
            null,
            emptyList(),
            false
        )

        assertThat(standings).isNotNull()

        val standingsTableModule = standings[1] as? StandingsTableModule
        val label = standingsTableModule?.statsColumns?.get(1)?.first() as? StandingsTableModule.StatsColumnItem.Label
        assertThat(label).isNotEqualTo(StandingsTableModule.StatsColumnItem.Label("PTS"))
    }

    @Test
    fun `Does not rearranges PTS column when sport is Soccer and PTS current position is one`() {
        val standings = scoresStandingsRenderer.renderStandingsModules(
            selectedGroupIndex = 0,
            selectedGroupName = null,
            groupings = standingsGroupingsFixture(columnsWithPTSColumnDoesNotNeedRearrangement),
            League.EPL,
            null,
            emptyList(),
            false
        )

        assertThat(standings).isNotNull()

        val standingsTableModule = standings[1] as? StandingsTableModule
        val label = standingsTableModule?.statsColumns?.get(1)?.first() as? StandingsTableModule.StatsColumnItem.Label
        assertThat(label).isEqualTo(StandingsTableModule.StatsColumnItem.Label("PTS"))
    }

    @Test
    fun `Does not rearranges columns when sport is not Soccer and PTS column does not exists`() {
        val standings = scoresStandingsRenderer.renderStandingsModules(
            selectedGroupIndex = 0,
            selectedGroupName = null,
            groupings = standingsGroupingsFixture(columnsWithNoPTSColumn),
            League.EPL,
            null,
            emptyList(),
            false
        )

        assertThat(standings).isNotNull()

        val standingsTableModule = standings[1] as? StandingsTableModule
        val label = standingsTableModule?.statsColumns?.get(1)?.first() as? StandingsTableModule.StatsColumnItem.Label
        assertThat(label).isNotEqualTo(StandingsTableModule.StatsColumnItem.Label("PTS"))
    }

    private fun standingFixture(
        id: String
    ) = Standing(
        id = id,
        team = teamFixture(),
        rank = 1,
        relegationStatus = RelegationStatus.UNKNOWN,
        points = "24",
        played = "11",
        won = "6",
        lost = "4",
        drawn = "1",
        pointsFor = "24",
        pointsAgainst = "27",
        difference = "4",
        winPct = null,
        divRecord = null,
        confRecord = null,
        streak = null,
        lostOvertime = null,
        awayRecord = null,
        homeRecord = null,
        lastTenRecord = null,
        lastSix = "WDWDWW",
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

    private fun standingsGroupingsFixture(columns: Map<String, String>) = listOf(
        StandingsGrouping(
            id = "1",
            groupLabel = "SG1",
            showRank = true,
            type = StandingsGroupingType.LEAGUE,
            headers = listOf(
                StandingsGroupHeader(
                    id = "2",
                    headerName = "SGH1",
                    groupIds = listOf(
                        "SGH1-standings"
                    )
                )
            ),
            groups = standingsGroupsFixture(columns)
        )
    )

    private fun standingsGroupsFixture(columns: Map<String, String>) = listOf(
        StandingsGroup(
            id = "3",
            segments = emptyList(),
            name = null,
            columns = columns,
            standings = listOf(
                standingFixture("5"),
                standingFixture("6"),
                standingFixture("7"),
                standingFixture("8"),
                standingFixture("9"),
                standingFixture("10"),
                standingFixture("11"),
                standingFixture("12")
            )
        )
    )
}