package com.theathletic.boxscore.ui.modules

import androidx.compose.ui.graphics.Color
import com.theathletic.themes.AthColor
import com.theathletic.ui.asResourceString

object StandingsTableModulePreviewData {

    val teamsColumnMock = listOf(
        StandingsTableModule.TeamColumnItem.Category(
            label = "Atlantic Coast Conference".asResourceString()
        ),
        StandingsTableModule.TeamColumnItem.Team(
            id = "teamId",
            alias = "HOU",
            logos = emptyList(),
            ranking = "1",
            showRanking = true,
            seeding = "",
            showSeeding = false,
            relegationColor = Color.Transparent,
            highlighted = true,
            isFollowable = true,
            dividerType = StandingsTableModule.DividerType.Standard,
        ),
        StandingsTableModule.TeamColumnItem.Team(
            id = "teamId",
            alias = "NYY",
            logos = emptyList(),
            ranking = "2",
            showRanking = true,
            seeding = "",
            showSeeding = false,
            relegationColor = Color.Transparent,
            highlighted = false,
            isFollowable = true,
            dividerType = StandingsTableModule.DividerType.SolidPlayoff,
        ),
        StandingsTableModule.TeamColumnItem.Team(
            id = "teamId",
            alias = "CLE",
            logos = emptyList(),
            ranking = "3",
            showRanking = true,
            seeding = "25",
            showSeeding = true,
            relegationColor = Color.Transparent,
            highlighted = false,
            isFollowable = true,
            dividerType = StandingsTableModule.DividerType.Standard,
        ),
        StandingsTableModule.TeamColumnItem.Team(
            id = "teamId",
            alias = "TB",
            logos = emptyList(),
            ranking = "4",
            showRanking = true,
            seeding = "",
            showSeeding = true,
            relegationColor = AthColor.GreenUser,
            highlighted = false,
            isFollowable = true,
            dividerType = StandingsTableModule.DividerType.DottedPlayOff,
        ),
        StandingsTableModule.TeamColumnItem.Team(
            id = "teamId",
            alias = "TOR",
            logos = emptyList(),
            ranking = "99",
            showRanking = true,
            seeding = "",
            showSeeding = false,
            relegationColor = Color.Transparent,
            highlighted = false,
            isFollowable = true,
            dividerType = StandingsTableModule.DividerType.Standard,
        ),
    )

    val statsColumnsMock = listOf(
        listOf(
            StandingsTableModule.StatsColumnItem.Label(
                text = "Win"
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "76",
                dividerType = StandingsTableModule.DividerType.Standard,
                highlighted = true,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "66",
                dividerType = StandingsTableModule.DividerType.SolidPlayoff,
                highlighted = false,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "69",
                dividerType = StandingsTableModule.DividerType.Standard,
                highlighted = false,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "68",
                dividerType = StandingsTableModule.DividerType.DottedPlayOff,
                highlighted = false,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "68",
                dividerType = StandingsTableModule.DividerType.Standard,
                highlighted = false,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
        ),
        listOf(
            StandingsTableModule.StatsColumnItem.Label(
                text = "Loss"
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "45",
                dividerType = StandingsTableModule.DividerType.Standard,
                highlighted = true,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "48",
                dividerType = StandingsTableModule.DividerType.SolidPlayoff,
                highlighted = false,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "57",
                dividerType = StandingsTableModule.DividerType.Standard,
                highlighted = false,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "55",
                dividerType = StandingsTableModule.DividerType.DottedPlayOff,
                highlighted = false,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "55",
                dividerType = StandingsTableModule.DividerType.Standard,
                highlighted = false,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
        ),
        listOf(
            StandingsTableModule.StatsColumnItem.Label(
                text = "%"
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = ".643",
                dividerType = StandingsTableModule.DividerType.Standard,
                highlighted = true,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = ".613",
                dividerType = StandingsTableModule.DividerType.SolidPlayoff,
                highlighted = false,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = ".537",
                dividerType = StandingsTableModule.DividerType.Standard,
                highlighted = false,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = ".556",
                dividerType = StandingsTableModule.DividerType.DottedPlayOff,
                highlighted = false,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = ".553",
                dividerType = StandingsTableModule.DividerType.Standard,
                highlighted = false,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
        ),
        listOf(
            StandingsTableModule.StatsColumnItem.Label(
                text = "GB"
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "-",
                dividerType = StandingsTableModule.DividerType.Standard,
                highlighted = true,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "-",
                dividerType = StandingsTableModule.DividerType.SolidPlayoff,
                highlighted = false,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "-",
                dividerType = StandingsTableModule.DividerType.Standard,
                highlighted = false,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "+1.5",
                dividerType = StandingsTableModule.DividerType.DottedPlayOff,
                highlighted = false,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "+1",
                dividerType = StandingsTableModule.DividerType.Standard,
                highlighted = false,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
        ),
        listOf(
            StandingsTableModule.StatsColumnItem.Label(
                text = "Strk"
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "W4",
                dividerType = StandingsTableModule.DividerType.Standard,
                highlighted = true,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Win,
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "W3",
                dividerType = StandingsTableModule.DividerType.SolidPlayoff,
                highlighted = false,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Win,
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "L1",
                dividerType = StandingsTableModule.DividerType.Standard,
                highlighted = false,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "W6",
                dividerType = StandingsTableModule.DividerType.DottedPlayOff,
                highlighted = false,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Loss,
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "L4",
                dividerType = StandingsTableModule.DividerType.Standard,
                highlighted = false,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Loss,
            ),
        ),
        listOf(
            StandingsTableModule.StatsColumnItem.Label(
                text = "L10"
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "6-4",
                dividerType = StandingsTableModule.DividerType.Standard,
                highlighted = true,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "4-6",
                dividerType = StandingsTableModule.DividerType.SolidPlayoff,
                highlighted = false,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "6-4",
                dividerType = StandingsTableModule.DividerType.Standard,
                highlighted = false,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "8-2",
                dividerType = StandingsTableModule.DividerType.DottedPlayOff,
                highlighted = false,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
            StandingsTableModule.StatsColumnItem.Statistic(
                value = "7-3",
                dividerType = StandingsTableModule.DividerType.Standard,
                highlighted = false,
                valueType = StandingsTableModule.StatsColumnItem.ValueType.Default,
            ),
        ),
    )
}