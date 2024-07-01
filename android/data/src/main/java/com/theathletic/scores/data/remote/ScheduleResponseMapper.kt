package com.theathletic.scores.data.remote

import com.theathletic.datetime.Datetime
import com.theathletic.fragment.GameLiteFragment
import com.theathletic.fragment.ScheduleGameTeam
import com.theathletic.scores.data.local.BoxScoreEntity
import com.theathletic.scores.data.local.GameCoverageType
import com.theathletic.scores.data.local.GameState
import com.theathletic.scores.remote.toLocalLeague
import com.theathletic.type.GameCoverageDataType
import com.theathletic.type.GameStatusCode

fun GameLiteFragment.toEntity(): BoxScoreEntity {
    return BoxScoreEntity(
        id = id,
        state = when (status) {
            GameStatusCode.scheduled -> GameState.UPCOMING
            GameStatusCode.in_progress -> GameState.LIVE
            GameStatusCode.final -> GameState.FINAL
            GameStatusCode.if_necessary -> GameState.IF_NECESSARY
            GameStatusCode.cancelled -> GameState.CANCELED
            GameStatusCode.delayed -> GameState.DELAYED
            GameStatusCode.postponed -> GameState.POSTPONED
            GameStatusCode.suspended -> GameState.SUSPENDED
            else -> GameState.UNKNOWN
        },
        scoreStatusText = match_time_display,
        leagueIds = listOf(league.id.toLocalLeague.leagueId),
        leagueDisplayName = league.alias,
        gameTime = Datetime(scheduled_at ?: 0),
        timeTBA = time_tbd ?: false,
        awayTeam = away_team?.fragments?.scheduleGameTeam?.toEntityTeam() ?: BoxScoreEntity.TeamStatus(isTbd = true),
        homeTeam = home_team?.fragments?.scheduleGameTeam?.toEntityTeam() ?: BoxScoreEntity.TeamStatus(isTbd = true),
        groupLabel = group,
        availableGameCoverage = coverage?.available_data?.map { it.toAvailableCoverage() } ?: emptyList()
    )
}

private fun ScheduleGameTeam.toEntityTeam(): BoxScoreEntity.TeamStatus? {
    val team = this.team ?: return null

    return BoxScoreEntity.TeamStatus(
        teamId = team.legacy_team?.id ?: team.id,
        name = team.display_name.orEmpty(),
        shortName = team.alias.orEmpty(),
        logo = team.logos.toPreferredSize(LogoSize.EXTRA_SMALL),
        score = score ?: 0,
        penaltyGoals = penalty_score ?: 0,
        record = null,
        details = null,
        ranking = toRanking(),
        currentRecord = current_record.orEmpty()
    )
}

fun List<ScheduleGameTeam.Logo2>.toPreferredSize(preferredSize: LogoSize): String {
    return sortedBy { it.fragments.logoFragment.width }
        .first { preferredSize.pixelSize <= it.fragments.logoFragment.width }
        .fragments.logoFragment.uri
}

fun ScheduleGameTeam.toRanking(): Int {
    asAmericanFootballGameTeam?.let { return it.current_ranking ?: 0 }
    asBasketballGameTeam?.let { return it.current_ranking ?: 0 }
    return 0
}

fun GameCoverageDataType?.toAvailableCoverage() =
    when (this) {
        GameCoverageDataType.all -> GameCoverageType.ALL
        GameCoverageDataType.comments -> GameCoverageType.COMMENTS
        GameCoverageDataType.comments_navigation -> GameCoverageType.COMMENTS_NAVIGATION
        GameCoverageDataType.discoverable_comments -> GameCoverageType.DISCOVERABLE_COMMENTS
        GameCoverageDataType.line_up -> GameCoverageType.LINE_UP
        GameCoverageDataType.live_blog -> GameCoverageType.LIVE_BLOG
        GameCoverageDataType.player_stats -> GameCoverageType.PLAYER_STATS
        GameCoverageDataType.plays -> GameCoverageType.PLAYS
        GameCoverageDataType.scores -> GameCoverageType.SCORES
        GameCoverageDataType.team_stats -> GameCoverageType.TEAM_STATS
        GameCoverageDataType.team_specific_comments -> GameCoverageType.TEAM_SPECIFIC_COMMENTS
        else -> GameCoverageType.UNKNOWN
    }

enum class LogoSize(val pixelSize: Int) {
    EXTRA_SMALL(72),
    SMALL(96),
    MEDIUM(168),
    LARGE(300)
}