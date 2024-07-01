package com.theathletic.brackets.data.remote

import com.theathletic.GetTournamentQuery
import com.theathletic.TournamentGamesSubscription
import com.theathletic.brackets.data.PlaceholderTeams
import com.theathletic.brackets.data.local.BracketsLocalModel
import com.theathletic.brackets.data.local.TournamentRound
import com.theathletic.brackets.data.local.TournamentRoundGame
import com.theathletic.brackets.data.local.TournamentRoundGroup
import com.theathletic.data.SizedImage
import com.theathletic.fragment.TournamentGame
import com.theathletic.fragment.TournamentPlaceholderGame
import com.theathletic.fragment.TournamentStage
import com.theathletic.fragment.TournamentTeam

fun GetTournamentQuery.Data.toLocalModel(): BracketsLocalModel {
    val unconnectedRounds = buildRounds(
        getTournament.extra_stages.map { it.fragments.tournamentStage },
        connected = false,
        offset = 0,
    )
    val connectedRounds = buildRounds(
        getTournament.stages.map { it.fragments.tournamentStage },
        connected = true,
        offset = unconnectedRounds.count(),
    )
    return BracketsLocalModel(unconnectedRounds.plus(connectedRounds))
}

fun TournamentGamesSubscription.Data.toLocalModel(
    placeholderTeams: TournamentStage.Placeholder_game_team?
) = liveScoreUpdates?.fragments?.tournamentGame?.let {
    buildRoundGame(it, placeholderTeams)
}

private fun buildRounds(
    stages: List<TournamentStage>,
    connected: Boolean,
    offset: Int,
): List<TournamentRound> {
    return stages.mapIndexed { index, stage ->
        TournamentRound(
            id = stage.id,
            title = stage.name,
            isLive = stage.live,
            type = stage.type,
            bracketRound = TournamentRound.BracketRound.values().firstOrNull { it.rawValue == offset + index + 1 },
            groups = stage.bracket_games
                .mapIndexedNotNull { index, game ->
                    val tournamentGame = game.fragments.tournamentGame
                    val tournamentPlaceholderGame = game.fragments.tournamentPlaceholderGame
                    when {
                        tournamentGame != null -> {
                            val placeholderTeams = stage.placeholder_game_teams?.getOrNull(index)
                            buildRoundGame(tournamentGame, placeholderTeams)
                        }
                        tournamentPlaceholderGame != null -> {
                            buildPlaceholderRoundGame(tournamentPlaceholderGame)
                        }
                        else -> null
                    }
                }
                .groupBy { it.conferenceName }
                .map { TournamentRoundGroup(it.key, it.value) },
            connected = connected,
        )
    }
}

private fun buildPlaceholderRoundGame(
    tournamentGame: TournamentPlaceholderGame
): TournamentRoundGame {
    val homeTeamName = tournamentGame.home_team_placeholder?.fragments?.tournamentPlaceholderTeam?.name
    val awayTeamName = tournamentGame.away_team_placeholder?.fragments?.tournamentPlaceholderTeam?.name
    return TournamentRoundGame(
        id = tournamentGame.id,
        placeholderTeams = PlaceholderTeams(homeTeam = homeTeamName ?: "", awayTeam = awayTeamName ?: ""),
        conferenceName = tournamentGame.conference,
        venueName = null,
        matchTimeDisplay = null,
        timeTbd = false,
        homeTeam = homeTeamName?.let { TournamentRoundGame.Team.Placeholder(it) },
        awayTeam = awayTeamName?.let { TournamentRoundGame.Team.Placeholder(it) },
        phase = TournamentRoundGame.Phase.PreGame,
        status = null,
        scheduledAt = null,
        startedAt = null,
        isPlaceholder = true,
    )
}

private fun buildRoundGame(
    tournamentGame: TournamentGame,
    placeholderTeams: TournamentStage.Placeholder_game_team?
): TournamentRoundGame {
    val phase = tournamentGame.status?.toTournamentRoundGamePhase(tournamentGame.scheduled_at)
    val homeTeam = tournamentGame.home_team?.fragments?.tournamentTeam
    val awayTeam = tournamentGame.away_team?.fragments?.tournamentTeam
    val homeTournamentTeam = buildTeam(homeTeam, placeholderTeams?.home_team_name)
    val awayTournamentTeam = buildTeam(awayTeam, placeholderTeams?.away_team_name)
    return TournamentRoundGame(
        id = tournamentGame.id,
        placeholderTeams = PlaceholderTeams(
            homeTeam = placeholderTeams?.home_team_name ?: "",
            awayTeam = placeholderTeams?.away_team_name ?: ""
        ),
        conferenceName = tournamentGame.asBasketballGame?.bracket?.name,
        venueName = tournamentGame.venue?.name,
        matchTimeDisplay = tournamentGame.match_time_display,
        timeTbd = tournamentGame.time_tbd ?: false,
        homeTeam = homeTournamentTeam,
        awayTeam = awayTournamentTeam,
        phase = phase,
        status = tournamentGame.status,
        scheduledAt = tournamentGame.scheduled_at,
        startedAt = tournamentGame.started_at,
        isPlaceholder = false,
    )
}

private fun buildTeam(tournamentTeam: TournamentTeam?, placeholderName: String?): TournamentRoundGame.Team? {
    val team = tournamentTeam?.team
    val alias = team?.alias
    if (tournamentTeam == null || team == null || alias == null) {
        if (placeholderName != null) {
            return TournamentRoundGame.Team.Placeholder(placeholderName)
        }

        return null
    }

    val availableLogos = team.logos.map {
        val logo = it.fragments.logoFragment
        SizedImage(logo.width, logo.height, logo.uri)
    }

    return TournamentRoundGame.Team.Confirmed(
        TournamentRoundGame.TeamData(
            team.id,
            availableLogos,
            alias,
            tournamentTeam.score,
            tournamentTeam.penalty_score,
            tournamentTeam.asBasketballGameTeam?.seed,
            tournamentTeam.asBasketballGameTeam?.current_record,
        )
    )
}