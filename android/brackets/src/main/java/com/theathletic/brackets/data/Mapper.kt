package com.theathletic.brackets.data

import com.theathletic.fragment.TournamentStage

fun PlaceholderTeams.toRemote() = TournamentStage.Placeholder_game_team(
    home_team_name = homeTeam,
    away_team_name = awayTeam
)

fun Map<String, PlaceholderTeams>.mapToRemote(): Map<String, TournamentStage.Placeholder_game_team> {
    return mapValues { it.value.toRemote() }
}