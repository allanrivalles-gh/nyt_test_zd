package com.theathletic.gamedetail.data.local

import com.theathletic.AmericanFootballPlayerGradesUpdatesSubscription
import com.theathletic.BaseballPlayerGradesUpdatesSubscription
import com.theathletic.BasketballPlayerGradesUpdatesSubscription
import com.theathletic.GetAmericanFootballPlayerGradesQuery
import com.theathletic.GetBaseballPlayerGradesQuery
import com.theathletic.GetBasketballPlayerGradesQuery
import com.theathletic.GetHockeyPlayerGradesQuery
import com.theathletic.GetSoccerPlayerGradesQuery
import com.theathletic.HockeyPlayerGradesUpdatesSubscription
import com.theathletic.SoccerPlayerGradesUpdatesSubscription
import com.theathletic.data.SizedImages
import com.theathletic.datetime.Datetime
import com.theathletic.entity.main.Sport
import com.theathletic.fragment.AmericanFootballPlayerGradesGame
import com.theathletic.fragment.BaseballPlayerGradesGame
import com.theathletic.fragment.BasketballPlayerGradesGame
import com.theathletic.fragment.GradablePlayer
import com.theathletic.fragment.HockeyPlayerGradesGame
import com.theathletic.fragment.PlayerGrade
import com.theathletic.fragment.PlayerGradesTeam
import com.theathletic.fragment.SoccerPlayerGradesGame
import com.theathletic.gamedetail.data.remote.toHeadshot
import com.theathletic.gamedetail.data.remote.toLocal
import com.theathletic.gamedetail.data.remote.toLocalModel
import com.theathletic.gamedetail.data.remote.toLocalStats
import com.theathletic.gamedetail.data.remote.toStatusLocalModel
import com.theathletic.gamedetail.data.remote.toTeamLiteLogos
import com.theathletic.type.GameStatGroup

data class PlayerGradesLocalModel(
    val gameId: String,
    val gameStatus: GameStatus,
    val gradeStatus: GradeStatus,
    val gameStatePrimary: String?,
    val gameStateSecondary: String?,
    val period: Period,
    val clock: String?,
    val scheduledAt: Datetime,
    val homeTeam: PlayerGradesTeam?,
    val awayTeam: PlayerGradesTeam?,
) {

    data class PlayerGradesTeam(
        val id: String,
        val name: String?,
        val alias: String?,
        val logos: SizedImages,
        val backgroundColor: String?,
        val score: Int,
        val players: List<Player>
    )

    data class Player(
        val playerId: String,
        val displayName: String,
        val headshots: SizedImages,
        val position: PlayerPosition,
        val jerseyNumber: String,
        val grading: Grading?,
        val summaryStatistics: List<GameDetailLocalModel.Statistic>,
        val defaultStatistics: List<GameDetailLocalModel.Statistic>,
        val extraStatistics: List<GameDetailLocalModel.Statistic>,
    )

    data class Grading(
        val playerId: String,
        val averageGradeDisplay: String,
        val grade: Int?,
        val totalGrades: Int,
        val order: Int,
        val updatedAt: Datetime,
    )

    fun getFirstTeam(sport: Sport) = when {
        sport.homeTeamFirst -> homeTeam
        else -> awayTeam
    }

    fun getSecondTeam(sport: Sport) = when {
        sport.homeTeamFirst -> awayTeam
        else -> homeTeam
    }
}

fun GetAmericanFootballPlayerGradesQuery.Data.toLocalModel(): PlayerGradesLocalModel? {
    return game.fragments.americanFootballPlayerGradesGame?.toLocalModel()
}

fun AmericanFootballPlayerGradesUpdatesSubscription.Data.toLocalModel(): PlayerGradesLocalModel? {
    return liveScoreUpdates?.fragments?.americanFootballPlayerGradesGame?.toLocalModel()
}

private fun AmericanFootballPlayerGradesGame.toLocalModel() =
    PlayerGradesLocalModel(
        gameId = id,
        gameStatus = status?.toStatusLocalModel() ?: GameStatus.UNKNOWN,
        period = period_id.toLocal(),
        clock = clock,
        scheduledAt = Datetime(scheduled_at ?: 0),
        gradeStatus = grade_status.toLocalModel(),
        homeTeam = home_team?.fragments?.playerGradesTeam?.toLocalModel(),
        awayTeam = away_team?.fragments?.playerGradesTeam?.toLocalModel(),
        gameStatePrimary = game_status?.fragments?.gameState?.main,
        gameStateSecondary = game_status?.fragments?.gameState?.extra
    )

fun GetSoccerPlayerGradesQuery.Data.toLocalModel(): PlayerGradesLocalModel? {
    return game.fragments.soccerPlayerGradesGame?.toLocalModel()
}

fun GetBasketballPlayerGradesQuery.Data.toLocalModel(): PlayerGradesLocalModel? {
    return game.fragments.basketballPlayerGradesGame?.toLocalModel()
}

fun GetHockeyPlayerGradesQuery.Data.toLocalModel(): PlayerGradesLocalModel? {
    return game.fragments.hockeyPlayerGradesGame?.toLocalModel()
}

fun GetBaseballPlayerGradesQuery.Data.toLocalModel(): PlayerGradesLocalModel? {
    return game.fragments.baseballPlayerGradesGame?.toLocalModel()
}

fun SoccerPlayerGradesUpdatesSubscription.Data.toLocalModel(): PlayerGradesLocalModel? {
    return liveScoreUpdates?.fragments?.soccerPlayerGradesGame?.toLocalModel()
}

fun BasketballPlayerGradesUpdatesSubscription.Data.toLocalModel(): PlayerGradesLocalModel? {
    return liveScoreUpdates?.fragments?.basketballPlayerGradesGame?.toLocalModel()
}

fun HockeyPlayerGradesUpdatesSubscription.Data.toLocalModel(): PlayerGradesLocalModel? {
    return liveScoreUpdates?.fragments?.hockeyPlayerGradesGame?.toLocalModel()
}

fun BaseballPlayerGradesUpdatesSubscription.Data.toLocalModel(): PlayerGradesLocalModel? {
    return liveScoreUpdates?.fragments?.baseballPlayerGradesGame?.toLocalModel()
}

private fun SoccerPlayerGradesGame.toLocalModel() =
    PlayerGradesLocalModel(
        gameId = id,
        gameStatus = status?.toStatusLocalModel() ?: GameStatus.UNKNOWN,
        period = period_id.toLocal(),
        clock = match_time_display,
        scheduledAt = Datetime(scheduled_at ?: 0),
        gradeStatus = grade_status.toLocalModel(),
        homeTeam = home_team?.fragments?.playerGradesTeam?.toLocalModel(),
        awayTeam = away_team?.fragments?.playerGradesTeam?.toLocalModel(),
        gameStatePrimary = game_status?.fragments?.gameState?.main,
        gameStateSecondary = game_status?.fragments?.gameState?.extra
    )

private fun BasketballPlayerGradesGame.toLocalModel() =
    PlayerGradesLocalModel(
        gameId = id,
        gameStatus = status?.toStatusLocalModel() ?: GameStatus.UNKNOWN,
        period = period_id.toLocal(),
        clock = clock,
        scheduledAt = Datetime(scheduled_at ?: 0),
        gradeStatus = grade_status.toLocalModel(),
        homeTeam = home_team?.fragments?.playerGradesTeam?.toLocalModel(),
        awayTeam = away_team?.fragments?.playerGradesTeam?.toLocalModel(),
        gameStatePrimary = game_status?.fragments?.gameState?.main,
        gameStateSecondary = game_status?.fragments?.gameState?.extra
    )

private fun HockeyPlayerGradesGame.toLocalModel() =
    PlayerGradesLocalModel(
        gameId = id,
        gameStatus = status?.toStatusLocalModel() ?: GameStatus.UNKNOWN,
        period = period_id.toLocal(),
        clock = clock,
        scheduledAt = Datetime(scheduled_at ?: 0),
        gradeStatus = grade_status.toLocalModel(),
        homeTeam = home_team?.fragments?.playerGradesTeam?.toLocalModel(),
        awayTeam = away_team?.fragments?.playerGradesTeam?.toLocalModel(),
        gameStatePrimary = game_status?.fragments?.gameState?.main,
        gameStateSecondary = game_status?.fragments?.gameState?.extra
    )

private fun BaseballPlayerGradesGame.toLocalModel() =
    PlayerGradesLocalModel(
        gameId = id,
        gameStatus = status?.toStatusLocalModel() ?: GameStatus.UNKNOWN,
        period = period_id.toLocal(),
        clock = clock,
        scheduledAt = Datetime(scheduled_at ?: 0),
        gradeStatus = grade_status.toLocalModel(),
        homeTeam = home_team?.fragments?.playerGradesTeam?.toLocalModel(),
        awayTeam = away_team?.fragments?.playerGradesTeam?.toLocalModel(),
        gameStatePrimary = game_status?.fragments?.gameState?.main,
        gameStateSecondary = game_status?.fragments?.gameState?.extra
    )

private fun PlayerGradesTeam.toLocalModel(): PlayerGradesLocalModel.PlayerGradesTeam? {
    return team?.fragments?.teamLite?.let { team ->
        PlayerGradesLocalModel.PlayerGradesTeam(
            id = team.id,
            name = team.display_name,
            logos = team.logos.toTeamLiteLogos(),
            alias = team.alias,
            backgroundColor = team.color_primary,
            score = score ?: 0,
            players = line_up?.toLocalModel() ?: emptyList()
        )
    }
}

private fun PlayerGradesTeam.Line_up.toLocalModel() =
    players.map { it.fragments.gradablePlayer.toLocalModel() }.distinctBy { it.playerId }

fun GradablePlayer.toLocalModel() = PlayerGradesLocalModel.Player(
    playerId = player.id,
    displayName = display_name.orEmpty(),
    headshots = player.headshots.toPlayerGradesHeadshot(),
    jerseyNumber = jersey_number.orEmpty(),
    position = position.toLocal(),
    grading = grade?.fragments?.playerGrade?.toLocalModel(),
    summaryStatistics = grades_stats.mapNotNull { it.fragments.gameStat.toLocalStats(GameStatGroup.grades_summary) },
    defaultStatistics = grades_stats.mapNotNull { it.fragments.gameStat.toLocalStats(GameStatGroup.grades_default) },
    extraStatistics = grades_stats.mapNotNull { it.fragments.gameStat.toLocalStats(GameStatGroup.grades_extra) },
)

private fun List<GradablePlayer.Headshot>.toPlayerGradesHeadshot() =
    map { it.fragments.headshot.toHeadshot() }.sortedBy { it.height }

fun PlayerGrade.toLocalModel() = PlayerGradesLocalModel.Grading(
    playerId = player_id,
    averageGradeDisplay = avg_str,
    grade = grade,
    totalGrades = total,
    order = order ?: 0,
    updatedAt = Datetime(updated_at)
)