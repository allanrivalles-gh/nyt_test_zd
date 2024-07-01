package com.theathletic.gamedetail.data.local

import com.theathletic.AmericanFootballPlayUpdatesSubscription
import com.theathletic.BaseballPlayUpdatesSubscription
import com.theathletic.BasketballPlayUpdatesSubscription
import com.theathletic.GetAmericanFootballPlayByPlaysQuery
import com.theathletic.GetBaseballPlayByPlaysQuery
import com.theathletic.GetBasketballPlayByPlaysQuery
import com.theathletic.GetHockeyPlayByPlaysQuery
import com.theathletic.GetSoccerPlayByPlaysQuery
import com.theathletic.HockeyPlayUpdatesSubscription
import com.theathletic.SoccerPlayUpdatesSubscription
import com.theathletic.datetime.Datetime
import com.theathletic.fragment.AmericanFootballDrive
import com.theathletic.fragment.AmericanFootballPlay
import com.theathletic.fragment.AmericanFootballPlayByPlays
import com.theathletic.fragment.AmericanFootballPlayByPlaysTeam
import com.theathletic.fragment.BaseballGameEmbeddedPlay
import com.theathletic.fragment.BaseballGamePlay
import com.theathletic.fragment.BaseballPlayByPlays
import com.theathletic.fragment.BasketballPlayByPlays
import com.theathletic.fragment.BasketballPlayByPlaysTeam
import com.theathletic.fragment.HockeyPlayByPlays
import com.theathletic.fragment.HockeyPlayByPlaysTeam
import com.theathletic.fragment.SoccerPlayByPlays
import com.theathletic.fragment.SoccerPlaysFragment
import com.theathletic.gamedetail.data.remote.toLocal
import com.theathletic.gamedetail.data.remote.toLocalModel
import com.theathletic.gamedetail.data.remote.toStatusLocalModel
import com.theathletic.type.SoccerPlayType

data class PlayByPlayLocalModel(
    val id: String,
    val status: GameStatus,
    val awayTeam: GameDetailLocalModel.Team?,
    val homeTeam: GameDetailLocalModel.Team?,
    val awayTeamScores: List<GameDetailLocalModel.ScoreType>? = null,
    val homeTeamScores: List<GameDetailLocalModel.ScoreType>? = null,
    val plays: List<GameDetailLocalModel.Play>,
)

// BASKETBALL PLAYS

fun GetBasketballPlayByPlaysQuery.Data.toLocalModel(): PlayByPlayLocalModel? {
    return game.fragments.basketballPlayByPlays?.toLocalModel()
}

fun BasketballPlayUpdatesSubscription.Data.toLocalModel(): PlayByPlayLocalModel? {
    return liveScoreUpdates?.fragments?.basketballPlayByPlays?.toLocalModel()
}

private fun BasketballPlayByPlays.toLocalModel() =
    PlayByPlayLocalModel(
        id = id,
        awayTeam = away_team?.fragments?.basketballPlayByPlaysTeam?.team?.fragments?.teamLite?.toLocalModel(),
        homeTeam = home_team?.fragments?.basketballPlayByPlaysTeam?.team?.fragments?.teamLite?.toLocalModel(),
        awayTeamScores = away_team?.fragments?.basketballPlayByPlaysTeam?.scoring?.toBasketballScoring(),
        homeTeamScores = home_team?.fragments?.basketballPlayByPlaysTeam?.scoring?.toBasketballScoring(),
        status = status.toStatusLocalModel(),
        plays = play_by_play.mapNotNull { it.fragments.basketballPlayFragment?.toLocal() }
    )

private fun List<BasketballPlayByPlaysTeam.Scoring>.toBasketballScoring() =
    map { it.fragments.periodScoreFragment.toLocalModel() }

// HOCKEY PLAYS

fun GetHockeyPlayByPlaysQuery.Data.toLocalModel(): PlayByPlayLocalModel? {
    return game.fragments.hockeyPlayByPlays?.toLocalModel()
}

fun HockeyPlayUpdatesSubscription.Data.toLocalModel(): PlayByPlayLocalModel? {
    return liveScoreUpdates?.fragments?.hockeyPlayByPlays?.toLocalModel()
}

private fun HockeyPlayByPlays.toLocalModel() =
    PlayByPlayLocalModel(
        id = id,
        awayTeam = away_team?.fragments?.hockeyPlayByPlaysTeam?.team?.fragments?.teamLite?.toLocalModel(),
        homeTeam = home_team?.fragments?.hockeyPlayByPlaysTeam?.team?.fragments?.teamLite?.toLocalModel(),
        awayTeamScores = away_team?.fragments?.hockeyPlayByPlaysTeam?.scoring?.toHockeyScoring(),
        homeTeamScores = home_team?.fragments?.hockeyPlayByPlaysTeam?.scoring?.toHockeyScoring(),
        status = status.toStatusLocalModel(),
        plays = play_by_play.mapNotNull { it.fragments.hockeyPlaysFragment.toLocal() }
    )

private fun List<HockeyPlayByPlaysTeam.Scoring>.toHockeyScoring() =
    map { it.fragments.periodScoreFragment.toLocalModel() }

// BASEBALL PLAYS

fun GetBaseballPlayByPlaysQuery.Data.toLocalModel(): PlayByPlayLocalModel? {
    return game.fragments.baseballPlayByPlays?.toLocalModel()
}

fun BaseballPlayUpdatesSubscription.Data.toLocalModel(): PlayByPlayLocalModel? {
    return liveScoreUpdates?.fragments?.baseballPlayByPlays?.toLocalModel()
}

private fun BaseballPlayByPlays.toLocalModel() = PlayByPlayLocalModel(
    id = id,
    awayTeam = away_team?.fragments?.baseballPlayByPlaysTeam?.team?.fragments?.teamLite?.toLocalModel(),
    homeTeam = home_team?.fragments?.baseballPlayByPlaysTeam?.team?.fragments?.teamLite?.toLocalModel(),
    awayTeamScores = away_team?.fragments?.baseballPlayByPlaysTeam?.scoring?.map {
        it.fragments.inningScoreFragment.toLocal()
    },
    homeTeamScores = home_team?.fragments?.baseballPlayByPlaysTeam?.scoring?.map {
        it.fragments.inningScoreFragment.toLocal()
    },
    status = status.toStatusLocalModel(),
    plays = play_by_play.mapNotNull { it.fragments.baseballGamePlay.toPlays() }
)

fun BaseballGamePlay.toPlays(): GameDetailLocalModel.BaseballPlay? {
    fragments.baseballLineupChangePlayFragment?.let { play ->
        return GameDetailLocalModel.BaseballLineUpChangePlay(
            id = play.id,
            description = play.description,
            headerLabel = play.header,
            inning = play.inning,
            inningHalf = play.inning_half?.toLocalModel(),
            occurredAt = Datetime(play.occurred_at)
        )
    }
    fragments.baseballPlayFragment?.let { play ->
        return GameDetailLocalModel.BaseballStandardPlay(
            id = play.id,
            description = play.description,
            headerLabel = play.header,
            inning = play.inning,
            inningHalf = play.inning_half?.toLocalModel(),
            occurredAt = Datetime(play.occurred_at),
            plays = play.plays.mapNotNull { it.fragments.baseballGameEmbeddedPlay.toPlays() }
        )
    }
    fragments.baseballTeamPlayFragment?.let { play ->
        return GameDetailLocalModel.BaseballTeamPlay(
            id = play.id,
            description = play.description,
            headerLabel = play.header,
            inning = play.inning,
            inningHalf = play.inning_half?.toLocalModel(),
            occurredAt = Datetime(play.occurred_at),
            awayTeamScore = play.away_score,
            homeTeamScore = play.home_score,
            team = play.team.fragments.teamLite.toLocalModel(),
            plays = play.plays.mapNotNull { it.fragments.baseballGameEmbeddedPlay.toPlays() }
        )
    }
    return null
}

fun BaseballGameEmbeddedPlay.toPlays(): GameDetailLocalModel.BaseballPlay? {
    fragments.baseballPlayWithoutPlays?.let { play ->
        return GameDetailLocalModel.BaseballStandardPlay(
            id = play.id,
            description = play.description,
            headerLabel = play.header,
            inning = play.inning,
            inningHalf = play.inning_half?.toLocalModel(),
            occurredAt = Datetime(play.occurred_at),
            plays = emptyList()
        )
    }
    fragments.baseballPitchPlay?.let { play ->
        return GameDetailLocalModel.BaseballPitchPlay(
            id = play.id,
            description = play.description,
            headerLabel = play.header,
            occurredAt = Datetime(play.occurred_at),
            bases = play.bases,
            hitZone = play.hit_zone,
            number = play.number,
            pitchDescription = play.pitch_description,
            pitchOutcome = play.pitch_outcome.toLocal(),
            pitchZone = play.pitch_zone
        )
    }
    return null
}

// AMERICAN FOOTBALL PLAYS

fun GetAmericanFootballPlayByPlaysQuery.Data.toLocalModel(): PlayByPlayLocalModel? {
    return game.fragments.americanFootballPlayByPlays?.toLocalModel()
}

fun AmericanFootballPlayUpdatesSubscription.Data.toLocalModel(): PlayByPlayLocalModel? {
    return liveScoreUpdates?.fragments?.americanFootballPlayByPlays?.toLocalModel()
}

private fun AmericanFootballPlayByPlays.toLocalModel() = PlayByPlayLocalModel(
    id = id,
    awayTeam = away_team?.fragments?.americanFootballPlayByPlaysTeam?.team?.fragments?.teamLite?.toLocalModel(),
    homeTeam = home_team?.fragments?.americanFootballPlayByPlaysTeam?.team?.fragments?.teamLite?.toLocalModel(),
    awayTeamScores = away_team?.fragments?.americanFootballPlayByPlaysTeam?.scoring?.toScoring(),
    homeTeamScores = home_team?.fragments?.americanFootballPlayByPlaysTeam?.scoring?.toScoring(),
    status = status.toStatusLocalModel(),
    plays = play_by_play.mapNotNull { it.fragments.americanFootballDrive?.toPlay() }
)

private fun List<AmericanFootballPlayByPlaysTeam.Scoring>.toScoring() =
    map { it.fragments.periodScoreFragment.toLocalModel() }

private fun AmericanFootballDrive.toPlay() = GameDetailLocalModel.AmericanFootballDrivePlay(
    id = id,
    description = description,
    headerLabel = header,
    occurredAt = Datetime(occurred_at),
    awayTeamScore = away_score,
    homeTeamScore = home_score,
    duration = duration,
    period = period_id.toLocal(),
    plays = plays.mapNotNull { it.fragments.americanFootballPlay?.toPlay() },
    team = team.fragments.teamLite.toLocalModel(),
    playCount = play_count,
    yards = yards
)

fun AmericanFootballPlay.toPlay() = GameDetailLocalModel.AmericanFootballPlay(
    id = id,
    description = description,
    headerLabel = header,
    occurredAt = Datetime(occurred_at),
    awayTeamScore = away_score,
    homeTeamScore = home_score,
    clock = clock,
    period = period_id?.toLocal(),
    isScoringPlay = scoring_play,
    playType = play_type.toLocal(),
    team = team?.fragments?.teamLite?.toLocalModel(),
    possession = possession?.fragments?.possessionFragment?.toLocal()
)

//  SOCCER PLAYS
fun GetSoccerPlayByPlaysQuery.Data.toLocalModel(): PlayByPlayLocalModel? {
    return game.fragments.soccerPlayByPlays?.toLocalModel()
}

fun SoccerPlayUpdatesSubscription.Data.toLocalModel(): PlayByPlayLocalModel? {
    return liveScoreUpdates?.fragments?.soccerPlayByPlays?.toLocalModel()
}

private fun SoccerPlayByPlays.toLocalModel() = PlayByPlayLocalModel(
    id = id,
    awayTeam = away_team?.fragments?.soccerPlayByPlaysTeam?.team?.fragments?.teamLite?.toLocalModel(),
    homeTeam = home_team?.fragments?.soccerPlayByPlaysTeam?.team?.fragments?.teamLite?.toLocalModel(),
    awayTeamScores = away_team?.fragments?.soccerPlayByPlaysTeam?.score?.toScoring(),
    homeTeamScores = home_team?.fragments?.soccerPlayByPlaysTeam?.score?.toScoring(),
    status = status.toStatusLocalModel(),
    plays = play_by_play.mapNotNull { it.fragments.soccerPlaysFragment.toLocal() }
)

private fun Int.toScoring() = listOf(
    GameDetailLocalModel.GameScore(
        score = this
    )
)

fun SoccerPlaysFragment.toLocal(): GameDetailLocalModel.SoccerPlay? {

    fragments.soccerPlayFragment?.let { play ->
        return GameDetailLocalModel.SoccerKeyPlay(
            id = play.id,
            homeTeamScore = play.home_score,
            awayTeamScore = play.away_score,
            description = play.description,
            playType = play.type.toLocal(),
            team = play.team?.fragments?.teamLite?.toLocalModel(),
            occurredAt = Datetime(play.occurred_at),
            gameTime = play.game_time,
            headerLabel = play.header,
            keyPlay = play.key_play,
            period = play.period_id.toLocal(),
            awayChancesCreated = play.away_chances_created,
            homeChancesCreated = play.home_chances_created
        )
    }
    fragments.soccerShootoutPlayFragment?.let { play ->
        return GameDetailLocalModel.SoccerShootoutPlay(
            id = play.id,
            description = play.description,
            headerLabel = play.header,
            gameTime = play.game_time,
            occurredAt = Datetime(play.occurred_at),
            team = play.team?.fragments?.teamLite?.toLocalModel(),
            playType = play.type.toLocal(),
            awayTeamScore = play.away_score,
            homeTeamScore = play.home_score,
            shooter = play.shooter?.fragments?.teamMember?.toLocalModel(),
            period = play.period_id.toLocal(),
            awayShootoutGoals = play.away_shootout_goals,
            homeShootoutGoals = play.home_shootout_goals,
        )
    }

    return null
}

private fun SoccerPlayType?.toLocal(): com.theathletic.gamedetail.data.local.SoccerPlayType {
    return when (this) {
        SoccerPlayType.corner -> com.theathletic.gamedetail.data.local.SoccerPlayType.CORNER
        SoccerPlayType.end_of_et -> com.theathletic.gamedetail.data.local.SoccerPlayType.END_OF_ET
        SoccerPlayType.end_of_first_et -> com.theathletic.gamedetail.data.local.SoccerPlayType.END_OF_FIRST_ET
        SoccerPlayType.end_of_game -> com.theathletic.gamedetail.data.local.SoccerPlayType.END_OF_GAME
        SoccerPlayType.end_of_half -> com.theathletic.gamedetail.data.local.SoccerPlayType.END_OF_HALF
        SoccerPlayType.end_of_regulation -> com.theathletic.gamedetail.data.local.SoccerPlayType.END_OF_REGULATION
        SoccerPlayType.foul -> com.theathletic.gamedetail.data.local.SoccerPlayType.FOUL
        SoccerPlayType.goal -> com.theathletic.gamedetail.data.local.SoccerPlayType.GOAL
        SoccerPlayType.injury_substitution -> com.theathletic.gamedetail.data.local.SoccerPlayType.INJURY_SUBSTITUTION
        SoccerPlayType.kickoff -> com.theathletic.gamedetail.data.local.SoccerPlayType.KICKOFF
        SoccerPlayType.offside -> com.theathletic.gamedetail.data.local.SoccerPlayType.OFFSIDE
        SoccerPlayType.own_goal -> com.theathletic.gamedetail.data.local.SoccerPlayType.OWN_GOAL
        SoccerPlayType.penalty_goal -> com.theathletic.gamedetail.data.local.SoccerPlayType.PENALTY_GOAL
        SoccerPlayType.penalty_shot_missed -> com.theathletic.gamedetail.data.local.SoccerPlayType.PENALTY_SHOT_MISSED
        SoccerPlayType.penalty_shot_saved -> com.theathletic.gamedetail.data.local.SoccerPlayType.PENALTY_SHOT_SAVED
        SoccerPlayType.penalty_kick_awarded -> com.theathletic.gamedetail.data.local.SoccerPlayType.PENALTY_KICK_AWARDED
        SoccerPlayType.player_retired -> com.theathletic.gamedetail.data.local.SoccerPlayType.PLAYER_RETIRED
        SoccerPlayType.red_card -> com.theathletic.gamedetail.data.local.SoccerPlayType.RED_CARD
        SoccerPlayType.second_yellow_card -> com.theathletic.gamedetail.data.local.SoccerPlayType.SECOND_YELLOW_CARD
        SoccerPlayType.shot_blocked -> com.theathletic.gamedetail.data.local.SoccerPlayType.SHOT_BLOCKED
        SoccerPlayType.shot_missed -> com.theathletic.gamedetail.data.local.SoccerPlayType.SHOT_MISSED
        SoccerPlayType.shot_saved -> com.theathletic.gamedetail.data.local.SoccerPlayType.SHOT_SAVED
        SoccerPlayType.start_penalty_shootout -> com.theathletic.gamedetail.data.local.SoccerPlayType.START_PENALTY_SHOOTOUT
        SoccerPlayType.stoppage_time -> com.theathletic.gamedetail.data.local.SoccerPlayType.STOPPAGE_TIME
        SoccerPlayType.substitution -> com.theathletic.gamedetail.data.local.SoccerPlayType.SUBSTITUTION
        SoccerPlayType.var_goal_awarded_cancelled -> com.theathletic.gamedetail.data.local.SoccerPlayType.VAR_GOAL_AWARDED_CANCELLED
        SoccerPlayType.var_goal_awarded_confirmed -> com.theathletic.gamedetail.data.local.SoccerPlayType.VAR_GOAL_AWARDED_CONFIRMED
        SoccerPlayType.var_goal_not_awarded_cancelled -> com.theathletic.gamedetail.data.local.SoccerPlayType.VAR_GOAL_NOT_AWARDED_CANCELLED
        SoccerPlayType.var_goal_not_awarded_confirmed -> com.theathletic.gamedetail.data.local.SoccerPlayType.VAR_GOAL_NOT_AWARDED_CONFIRMED
        SoccerPlayType.var_penalty_awarded_cancelled -> com.theathletic.gamedetail.data.local.SoccerPlayType.VAR_PENALTY_AWARDED_CANCELLED
        SoccerPlayType.var_penalty_awarded_confirmed -> com.theathletic.gamedetail.data.local.SoccerPlayType.VAR_PENALTY_AWARDED_CONFIRMED
        SoccerPlayType.var_penalty_not_awarded_cancelled -> com.theathletic.gamedetail.data.local.SoccerPlayType.VAR_PENALTY_NOT_AWARDED_CANCELLED
        SoccerPlayType.var_penalty_not_awarded_confirmed -> com.theathletic.gamedetail.data.local.SoccerPlayType.VAR_PENALTY_NOT_AWARDED_CONFIRMED
        SoccerPlayType.var_red_card_awarded_cancelled -> com.theathletic.gamedetail.data.local.SoccerPlayType.VAR_RED_CARD_NOT_AWARDED_CANCELLED
        SoccerPlayType.var_red_card_awarded_confirmed -> com.theathletic.gamedetail.data.local.SoccerPlayType.VAR_RED_CARD_AWARDED_CONFIRMED
        SoccerPlayType.var_red_card_not_awarded_cancelled -> com.theathletic.gamedetail.data.local.SoccerPlayType.VAR_RED_CARD_NOT_AWARDED_CANCELLED
        SoccerPlayType.var_red_card_not_awarded_confirmed -> com.theathletic.gamedetail.data.local.SoccerPlayType.VAR_RED_CARD_NOT_AWARDED_CONFIRMED
        SoccerPlayType.yellow_card -> com.theathletic.gamedetail.data.local.SoccerPlayType.YELLOW_CARD
        else -> com.theathletic.gamedetail.data.local.SoccerPlayType.UNKNOWN
    }
}