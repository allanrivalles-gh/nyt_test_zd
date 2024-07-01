package com.theathletic.hub.game.data

import com.theathletic.GetGameSummaryQuery
import com.theathletic.datetime.Datetime
import com.theathletic.fragment.AmericanFootballGameSummary
import com.theathletic.fragment.AmericanFootballGameSummaryTeam
import com.theathletic.fragment.BaseballGameSummary
import com.theathletic.fragment.BaseballGameSummaryTeam
import com.theathletic.fragment.BasketballGameSummary
import com.theathletic.fragment.BasketballGameSummaryTeam
import com.theathletic.fragment.HockeyGameSummary
import com.theathletic.fragment.HockeyGameSummaryTeam
import com.theathletic.fragment.League
import com.theathletic.fragment.SoccerGameSummary
import com.theathletic.fragment.SoccerGameSummaryTeam
import com.theathletic.gamedetail.data.local.CoverageDataType
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.remote.toLocal
import com.theathletic.gamedetail.data.remote.toLocalModel
import com.theathletic.gamedetail.data.remote.toLocalStats
import com.theathletic.gamedetail.data.remote.toStatusLocalModel
import com.theathletic.gamedetail.data.remote.toTeamLiteLogos
import com.theathletic.hub.game.data.local.GameSummary
import com.theathletic.liveblog.data.local.LiveBlogLinks
import com.theathletic.scores.data.remote.toLocalLeague
import com.theathletic.type.GameCoverageDataType
import com.theathletic.type.HockeyStrength

fun GetGameSummaryQuery.Data.toDomain() = game.fragments.gameSummary.toDomain()

private fun com.theathletic.fragment.GameSummary.toDomain(): GameSummary? {
    val liveBlogLinks = live_blog?.toDomain()
    fragments.americanFootballGameSummary?.let { game -> return game.toDomain(liveBlogLinks) }
    fragments.basketballGameSummary?.let { game -> return game.toDomain(liveBlogLinks) }
    fragments.baseballGameSummary?.let { game -> return game.toDomain(liveBlogLinks) }
    fragments.hockeyGameSummary?.let { game -> return game.toDomain(liveBlogLinks) }
    fragments.soccerGameSummary?.let { game -> return game.toDomain(liveBlogLinks) }
    return null
}

private fun AmericanFootballGameSummary.toDomain(liveBlogLinks: LiveBlogLinks?): GameSummary {
    val possession = possession?.fragments?.possessionFragment?.toLocal()
    return GameSummary(
        id = id,
        scheduleAt = Datetime(scheduled_at ?: 0),
        isScheduledTimeTbd = time_tbd ?: false,
        sport = sport.toLocal(),
        league = league.fragments.league.toDomain(),
        coverage = coverage?.available_data?.map { it.toDomain() } ?: listOf(CoverageDataType.ALL),
        permalink = permalink,
        status = status.toStatusLocalModel(),
        period = period_id.toLocal(),
        firstTeam = away_team?.fragments?.americanFootballGameSummaryTeam?.toDomain(possession),
        secondTeam = home_team?.fragments?.americanFootballGameSummaryTeam?.toDomain(possession),
        isFirstTeamTbd = away_team == null,
        isSecondTeamTbd = home_team == null,
        clock = clock,
        gameTitle = game_title,
        baseballOutcome = null,
        soccerInfo = null,
        liveBlog = liveBlogLinks,
        areCommentsDiscoverable = is_comments_discoverable,
        gradeStatus = grade_status.toLocalModel(),
        gameStatePrimary = game_status?.fragments?.gameState?.main,
        gameStateSecondary = game_status?.fragments?.gameState?.extra,
    )
}

private fun BasketballGameSummary.toDomain(liveBlogLinks: LiveBlogLinks?) = GameSummary(
    id = id,
    scheduleAt = Datetime(scheduled_at ?: 0),
    isScheduledTimeTbd = time_tbd ?: false,
    sport = sport.toLocal(),
    league = league.fragments.league.toDomain(),
    coverage = coverage?.available_data?.map { it.toDomain() } ?: listOf(CoverageDataType.ALL),
    permalink = permalink,
    status = status.toStatusLocalModel(),
    period = period_id.toLocal(),
    firstTeam = away_team?.fragments?.basketballGameSummaryTeam?.toDomain(),
    secondTeam = home_team?.fragments?.basketballGameSummaryTeam?.toDomain(),
    isFirstTeamTbd = away_team == null,
    isSecondTeamTbd = home_team == null,
    clock = clock,
    gameTitle = game_title,
    baseballOutcome = null,
    soccerInfo = null,
    liveBlog = liveBlogLinks,
    areCommentsDiscoverable = is_comments_discoverable,
    gradeStatus = grade_status.toLocalModel(),
    gameStatePrimary = game_status?.fragments?.gameState?.main,
    gameStateSecondary = game_status?.fragments?.gameState?.extra,
)

private fun BaseballGameSummary.toDomain(liveBlogLinks: LiveBlogLinks?) = GameSummary(
    id = id,
    scheduleAt = Datetime(scheduled_at ?: 0),
    isScheduledTimeTbd = time_tbd ?: false,
    sport = sport.toLocal(),
    league = league.fragments.league.toDomain(),
    coverage = coverage?.available_data?.map { it.toDomain() } ?: listOf(CoverageDataType.ALL),
    permalink = permalink,
    status = status.toStatusLocalModel(),
    period = period_id.toLocal(),
    firstTeam = away_team?.fragments?.baseballGameSummaryTeam?.toDomain(),
    secondTeam = home_team?.fragments?.baseballGameSummaryTeam?.toDomain(),
    isFirstTeamTbd = away_team == null,
    isSecondTeamTbd = home_team == null,
    clock = null,
    liveBlog = liveBlogLinks,
    gameTitle = game_title,
    baseballOutcome = outcome?.toDomain(),
    soccerInfo = null,
    areCommentsDiscoverable = is_comments_discoverable,
    gradeStatus = grade_status.toLocalModel(),
    gameStateSecondary = null,
    gameStatePrimary = null
)

private fun HockeyGameSummary.toDomain(liveBlogLinks: LiveBlogLinks?) = GameSummary(
    id = id,
    scheduleAt = Datetime(scheduled_at ?: 0),
    isScheduledTimeTbd = time_tbd ?: false,
    sport = sport.toLocal(),
    league = league.fragments.league.toDomain(),
    coverage = coverage?.available_data?.map { it.toDomain() } ?: listOf(CoverageDataType.ALL),
    permalink = permalink,
    status = status.toStatusLocalModel(),
    period = period_id.toLocal(),
    firstTeam = away_team?.fragments?.hockeyGameSummaryTeam?.toDomain(),
    secondTeam = home_team?.fragments?.hockeyGameSummaryTeam?.toDomain(),
    isFirstTeamTbd = away_team == null,
    isSecondTeamTbd = home_team == null,
    clock = clock,
    gameTitle = game_title,
    baseballOutcome = null,
    soccerInfo = null,
    liveBlog = liveBlogLinks,
    areCommentsDiscoverable = is_comments_discoverable,
    gradeStatus = grade_status.toLocalModel(),
    gameStatePrimary = game_status?.fragments?.gameState?.main,
    gameStateSecondary = game_status?.fragments?.gameState?.extra
)

// Soccer, first team is home team, opposite to other sports
private fun SoccerGameSummary.toDomain(liveBlogLinks: LiveBlogLinks?) = GameSummary(
    id = id,
    scheduleAt = Datetime(scheduled_at ?: 0),
    isScheduledTimeTbd = time_tbd ?: false,
    sport = sport.toLocal(),
    league = league.fragments.league.toDomain(),
    coverage = coverage?.available_data?.map { it.toDomain() } ?: listOf(CoverageDataType.ALL),
    permalink = permalink,
    status = status.toStatusLocalModel(),
    period = period_id.toLocal(),
    firstTeam = home_team?.fragments?.soccerGameSummaryTeam?.toDomain(),
    secondTeam = away_team?.fragments?.soccerGameSummaryTeam?.toDomain(),
    isFirstTeamTbd = home_team == null,
    isSecondTeamTbd = away_team == null,
    clock = clock,
    liveBlog = liveBlogLinks,
    gameTitle = game_title,
    baseballOutcome = null,
    soccerInfo = GameSummary.SoccerInfo(
        relatedGameScheduleAt = related_game?.scheduled_at?.let { Datetime(it) },
        aggregateWinnerName = aggregate_winner?.display_name
    ),
    areCommentsDiscoverable = is_comments_discoverable,
    gradeStatus = grade_status.toLocalModel(),
    gameStatePrimary = game_status?.fragments?.gameState?.main,
    gameStateSecondary = game_status?.fragments?.gameState?.extra
)

private fun AmericanFootballGameSummaryTeam.toDomain(
    possession: GameDetailLocalModel.Possession?
) = GameSummary.AmericanFootballTeam(
    id = team?.fragments?.teamLite?.id.orEmpty(),
    legacyId = team?.fragments?.teamLite?.legacy_team?.id?.toLong() ?: 0L,
    alias = team?.fragments?.teamLite?.alias.orEmpty(),
    displayName = team?.fragments?.teamLite?.display_name.orEmpty(),
    logos = team?.fragments?.teamLite?.logos?.toTeamLiteLogos() ?: emptyList(),
    score = score,
    currentRecord = current_record,
    currentRanking = current_ranking?.toString(),
    remainingTimeouts = remaining_timeouts,
    usedTimeouts = used_timeouts,
    hasPossession = id.endsWith(possession?.team?.id.orEmpty())
)

private fun BasketballGameSummaryTeam.toDomain() = GameSummary.BasketballTeam(
    id = team?.fragments?.teamLite?.id.orEmpty(),
    legacyId = team?.fragments?.teamLite?.legacy_team?.id?.toLong() ?: 0L,
    alias = team?.fragments?.teamLite?.alias.orEmpty(),
    displayName = team?.fragments?.teamLite?.display_name.orEmpty(),
    logos = team?.fragments?.teamLite?.logos?.toTeamLiteLogos() ?: emptyList(),
    score = score,
    currentRecord = current_record,
    currentRanking = current_ranking?.toString(),
    remainingTimeouts = remaining_timeouts,
    usedTimeouts = used_timeouts
)

private fun BaseballGameSummaryTeam.toDomain() = GameSummary.BaseballTeam(
    id = team?.fragments?.teamLite?.id.orEmpty(),
    legacyId = team?.fragments?.teamLite?.legacy_team?.id?.toLong() ?: 0L,
    alias = team?.fragments?.teamLite?.alias.orEmpty(),
    displayName = team?.fragments?.teamLite?.display_name.orEmpty(),
    logos = team?.fragments?.teamLite?.logos?.toTeamLiteLogos() ?: emptyList(),
    score = score,
    currentRecord = current_record
)

private fun HockeyGameSummaryTeam.toDomain() = GameSummary.HockeyTeam(
    id = team?.fragments?.teamLite?.id.orEmpty(),
    legacyId = team?.fragments?.teamLite?.legacy_team?.id?.toLong() ?: 0L,
    alias = team?.fragments?.teamLite?.alias.orEmpty(),
    displayName = team?.fragments?.teamLite?.display_name.orEmpty(),
    logos = team?.fragments?.teamLite?.logos?.toTeamLiteLogos() ?: emptyList(),
    score = score,
    currentRecord = current_record,
    strength = strength?.toDomain() ?: com.theathletic.gamedetail.data.local.HockeyStrength.UNKNOWN
)

private fun SoccerGameSummaryTeam.toDomain() = GameSummary.SoccerTeam(
    id = team?.fragments?.teamLite?.id.orEmpty(),
    legacyId = team?.fragments?.teamLite?.legacy_team?.id?.toLong() ?: 0L,
    alias = team?.fragments?.teamLite?.alias.orEmpty(),
    displayName = team?.fragments?.teamLite?.display_name.orEmpty(),
    logos = team?.fragments?.teamLite?.logos?.toTeamLiteLogos() ?: emptyList(),
    score = score,
    currentRecord = current_record,
    currentRanking = current_standing,
    aggregateScore = aggregate_score,
    lastSix = last_six.orEmpty(),
    expectedGoals = expected_goals?.fragments?.gameStat?.toLocalStats(),
    penaltyScore = penalty_score
)

private fun GameCoverageDataType.toDomain(): CoverageDataType {
    return when (this) {
        GameCoverageDataType.line_up -> CoverageDataType.LINE_UP
        GameCoverageDataType.player_stats -> CoverageDataType.PLAYER_STATS
        GameCoverageDataType.scores -> CoverageDataType.SCORES
        GameCoverageDataType.team_stats -> CoverageDataType.TEAM_STATS
        GameCoverageDataType.plays -> CoverageDataType.PLAYS
        GameCoverageDataType.comments -> CoverageDataType.COMMENTS
        GameCoverageDataType.team_specific_comments -> CoverageDataType.TEAM_SPECIFIC_COMMENTS
        GameCoverageDataType.comments_navigation -> CoverageDataType.COMMENTS_NAVIGATION
        else -> CoverageDataType.ALL
    }
}

private fun League.toDomain() = GameDetailLocalModel.League(
    legacyLeague = id.toLocalLeague,
    id = id.rawValue,
    alias = alias,
    displayName = display_name
)

private fun BaseballGameSummary.Outcome.toDomain() =
    GameSummary.BaseballOutcome(
        balls = balls,
        inning = inning,
        inningHalf = inning_half?.toLocalModel(),
        outs = outs,
        strikes = strikes,
        occupiedBases = runners.map { it.ending_base }
    )

private fun HockeyStrength.toDomain(): com.theathletic.gamedetail.data.local.HockeyStrength {
    return when (this) {
        HockeyStrength.even -> com.theathletic.gamedetail.data.local.HockeyStrength.EVEN
        HockeyStrength.powerplay -> com.theathletic.gamedetail.data.local.HockeyStrength.POWERPLAY
        HockeyStrength.shorthanded -> com.theathletic.gamedetail.data.local.HockeyStrength.SHORTHANDED
        else -> com.theathletic.gamedetail.data.local.HockeyStrength.UNKNOWN
    }
}

private fun com.theathletic.fragment.GameSummary.Live_blog.toDomain() = LiveBlogLinks(
    id = fragments.liveBlogLinksFragment.id,
    permalink = fragments.liveBlogLinksFragment.permalink,
    linkForEmbed = fragments.liveBlogLinksFragment.permalinkForEmbed,
)