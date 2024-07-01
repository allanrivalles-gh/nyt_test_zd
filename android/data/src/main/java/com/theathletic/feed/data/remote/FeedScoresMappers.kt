package com.theathletic.feed.data.remote

import com.theathletic.datetime.Datetime
import com.theathletic.fragment.FeedGame
import com.theathletic.fragment.ScheduleGameTeam
import com.theathletic.scores.data.local.BoxScoreEntity
import com.theathletic.scores.data.local.GameState
import com.theathletic.scores.data.remote.LogoSize
import com.theathletic.scores.data.remote.toAvailableCoverage
import com.theathletic.scores.data.remote.toPreferredSize
import com.theathletic.scores.data.remote.toRanking
import com.theathletic.scores.remote.toLocalLeague
import com.theathletic.type.GameCoverageDataType
import com.theathletic.type.GameStatusCode

fun FeedGame.toEntity() = BoxScoreEntity(
    id = id,
    graphGameId = id,
    state = status.toStatus(),
    leagueIds = listOf(league.id.toLocalLeague.leagueId),
    scoreStatusText = match_time_display,
    gameTime = Datetime(scheduled_at ?: 0),
    timeTBA = time_tbd ?: false,
    awayTeam = away_team?.fragments?.scheduleGameTeam?.toEntityTeam() ?: BoxScoreEntity.TeamStatus(isTbd = true),
    homeTeam = home_team?.fragments?.scheduleGameTeam?.toEntityTeam() ?: BoxScoreEntity.TeamStatus(isTbd = true),
    requestStatsWhenLive = requestLiveStatus(),
    availableGameCoverage = coverage?.available_data?.map { it.toAvailableCoverage() } ?: emptyList()
)

private fun FeedGame.requestLiveStatus() = (
    coverage?.available_data == null ||
        coverage?.available_data?.contains(GameCoverageDataType.team_stats) == true ||
        coverage?.available_data?.contains(GameCoverageDataType.all) == true
    )

private fun GameStatusCode?.toStatus() =
    when (this) {
        GameStatusCode.scheduled -> GameState.UPCOMING
        GameStatusCode.in_progress -> GameState.LIVE
        GameStatusCode.final -> GameState.FINAL
        GameStatusCode.if_necessary -> GameState.IF_NECESSARY
        GameStatusCode.cancelled -> GameState.CANCELED
        GameStatusCode.delayed -> GameState.DELAYED
        GameStatusCode.postponed -> GameState.POSTPONED
        GameStatusCode.suspended -> GameState.SUSPENDED
        else -> GameState.UPCOMING
    }

private fun ScheduleGameTeam.toEntityTeam(): BoxScoreEntity.TeamStatus? {
    return team?.let { team ->
        BoxScoreEntity.TeamStatus(
            teamId = team.legacy_team?.id ?: team.id,
            shortName = team.alias.orEmpty(),
            name = team.display_name.orEmpty(),
            logo = team.logos.toPreferredSize(LogoSize.EXTRA_SMALL),
            score = score ?: 0,
            penaltyGoals = penalty_score ?: 0,
            ranking = toRanking(),
            currentRecord = current_record.orEmpty()
        )
    }
}