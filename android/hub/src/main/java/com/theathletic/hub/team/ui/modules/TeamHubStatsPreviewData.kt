package com.theathletic.hub.team.ui.modules

import androidx.compose.ui.graphics.Color

object TeamHubStatsPreviewData {
    fun createLeaderGroups() = listOf(
        TeamHubTeamLeadersModule.Group(
            label = "Offense",
            players = listOf(
                TeamHubTeamLeadersModule.Player(
                    name = "RJ Barrett",
                    position = "PG",
                    headShots = emptyList(),
                    teamLogos = emptyList(),
                    teamColor = Color.Cyan,
                    stats = listOf(
                        TeamHubTeamLeadersModule.PlayerStatistic(
                            label = "PASSING YARDS",
                            value = "604"
                        )
                    ),
                    showDivider = true,
                ),
                TeamHubTeamLeadersModule.Player(
                    name = "RJ Barrett",
                    position = "PG",
                    headShots = emptyList(),
                    teamLogos = emptyList(),
                    teamColor = Color.Cyan,
                    stats = listOf(
                        TeamHubTeamLeadersModule.PlayerStatistic(
                            label = "RUSHING YARDS",
                            value = "255"
                        )
                    ),
                    showDivider = true,
                ),
                TeamHubTeamLeadersModule.Player(
                    name = "RJ Barrett",
                    position = "PG",
                    headShots = emptyList(),
                    teamLogos = emptyList(),
                    teamColor = Color.Cyan,
                    stats = listOf(
                        TeamHubTeamLeadersModule.PlayerStatistic(
                            label = "FG%",
                            value = "76.1"
                        ),
                        TeamHubTeamLeadersModule.PlayerStatistic(
                            label = "FG%",
                            value = "76.1"
                        ),
                    ),
                    showDivider = false,
                ),
            )
        )
    )

    fun createTeamSeasonStats() = listOf(
        TeamHubSeasonStatsModule.SingleTeamStatsItem(
            "27.1",
            "Points",
            false
        ),
        TeamHubSeasonStatsModule.SingleTeamStatsItem(
            "4.00",
            "Shots on Goal Per Game in the league",
            false
        ),
        TeamHubSeasonStatsModule.SingleTeamStatsItem(
            "359.9",
            "Pass Yards",
            true
        ),
        TeamHubSeasonStatsModule.SingleTeamStatsItem(
            "59.7",
            "Rush Yards",
            true
        ),
        TeamHubSeasonStatsModule.SingleTeamStatsItem(
            "2.1",
            "Turnovers",
            false
        ),
    )
}