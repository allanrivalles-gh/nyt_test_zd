package com.theathletic.gamedetail.data.remote

import com.theathletic.BaseballPlayerStatsUpdatesSubscription
import com.theathletic.GameSummaryUpdatesSubscription
import com.theathletic.GetAmericanFootballGameQuery
import com.theathletic.GetBaseballGameQuery
import com.theathletic.GetBaseballStatsQuery
import com.theathletic.GetBasketballGameQuery
import com.theathletic.GetGameSummaryQuery
import com.theathletic.GetHockeyGameQuery
import com.theathletic.GetPlayerStatsQuery
import com.theathletic.GetSoccerGameQuery
import com.theathletic.PlayerStatsUpdatesSubscription
import com.theathletic.data.SizedImage
import com.theathletic.datetime.Datetime
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.entity.main.FeedItem
import com.theathletic.entity.main.FeedItemStyle
import com.theathletic.entity.main.FeedItemType
import com.theathletic.entity.main.FeedResponse
import com.theathletic.entity.main.Sport
import com.theathletic.feed.FeedType
import com.theathletic.fragment.AmericanFootballGameFragment
import com.theathletic.fragment.AmericanFootballGameSummary
import com.theathletic.fragment.AmericanFootballGameSummaryTeam
import com.theathletic.fragment.AmericanFootballGameTeamFragment
import com.theathletic.fragment.BaseballBatterFragment
import com.theathletic.fragment.BaseballGameFragment
import com.theathletic.fragment.BaseballGameSummary
import com.theathletic.fragment.BaseballGameSummaryTeam
import com.theathletic.fragment.BaseballGameTeamFragment
import com.theathletic.fragment.BaseballInningPlayFragment
import com.theathletic.fragment.BaseballOutcomeFragment
import com.theathletic.fragment.BaseballPitcherFragment
import com.theathletic.fragment.BaseballPitchingFragment
import com.theathletic.fragment.BaseballPlayerFragment
import com.theathletic.fragment.BaseballPlayerStats
import com.theathletic.fragment.BaseballPlayerStatsPlayer
import com.theathletic.fragment.BaseballStatsGameTeamFragment
import com.theathletic.fragment.BaseballTeamPlayFragment
import com.theathletic.fragment.BasketballGameFragment
import com.theathletic.fragment.BasketballGameSummary
import com.theathletic.fragment.BasketballGameSummaryTeam
import com.theathletic.fragment.BasketballGameTeamFragment
import com.theathletic.fragment.BasketballPlayFragment
import com.theathletic.fragment.CardEvent
import com.theathletic.fragment.GameFeedItemFragment
import com.theathletic.fragment.GameOddsMarketFragment
import com.theathletic.fragment.GameOddsPriceFragment
import com.theathletic.fragment.GameStat
import com.theathletic.fragment.GameSummary
import com.theathletic.fragment.GameTicket
import com.theathletic.fragment.GameTicketLogo
import com.theathletic.fragment.GameTicketPrice
import com.theathletic.fragment.GoalEvent
import com.theathletic.fragment.Headshot
import com.theathletic.fragment.HockeyGameFragment
import com.theathletic.fragment.HockeyGameSummary
import com.theathletic.fragment.HockeyGameSummaryTeam
import com.theathletic.fragment.HockeyGameTeamFragment
import com.theathletic.fragment.HockeyPlaysFragment
import com.theathletic.fragment.HockeyShootoutPlayFragment
import com.theathletic.fragment.Injury
import com.theathletic.fragment.InningScoreFragment
import com.theathletic.fragment.KeyEvent
import com.theathletic.fragment.League
import com.theathletic.fragment.LineUp
import com.theathletic.fragment.PenaltyShotEvent
import com.theathletic.fragment.PeriodEvent
import com.theathletic.fragment.PeriodScoreFragment
import com.theathletic.fragment.Player
import com.theathletic.fragment.PlayerStats
import com.theathletic.fragment.PossessionFragment
import com.theathletic.fragment.RankedStat
import com.theathletic.fragment.RecentGameFragment
import com.theathletic.fragment.RecentGameTeamFragment
import com.theathletic.fragment.ScoresTopComment
import com.theathletic.fragment.ScoringPlayFragment
import com.theathletic.fragment.SeasonStats
import com.theathletic.fragment.SoccerGameFragment
import com.theathletic.fragment.SoccerGameSummary
import com.theathletic.fragment.SoccerGameSummaryTeam
import com.theathletic.fragment.SoccerGameTeamFragment
import com.theathletic.fragment.StartingPitcherFragment
import com.theathletic.fragment.SubstitutionEvent
import com.theathletic.fragment.Team
import com.theathletic.fragment.TeamLeader
import com.theathletic.fragment.TeamLite
import com.theathletic.fragment.TeamMember
import com.theathletic.fragment.TeamMemberBaseball
import com.theathletic.fragment.TimelineEvent
import com.theathletic.fragment.TopPerformer
import com.theathletic.fragment.WeatherFragment
import com.theathletic.gamedetail.data.local.AmericanFootballScoreType
import com.theathletic.gamedetail.data.local.CoverageDataType
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.GameLineUpAndStats
import com.theathletic.gamedetail.data.local.GameStatus
import com.theathletic.gamedetail.data.local.GameSummaryLocalModel
import com.theathletic.gamedetail.data.local.GameTicketCurrency
import com.theathletic.gamedetail.data.local.Handedness
import com.theathletic.gamedetail.data.local.PitcherState
import com.theathletic.gamedetail.data.local.PlayerPosition
import com.theathletic.gamedetail.data.local.StatisticCategory
import com.theathletic.gamedetail.data.local.toLocal
import com.theathletic.gamedetail.data.local.toLocalModel
import com.theathletic.gamedetail.data.local.toPlay
import com.theathletic.gamedetail.data.local.toPlays
import com.theathletic.liveblog.data.local.LiveBlogLinks
import com.theathletic.liveblog.data.remote.toLocal
import com.theathletic.scores.gamefeed.data.remote.toEntity
import com.theathletic.scores.remote.toLocalLeague
import com.theathletic.type.AmericanFootballPlayType
import com.theathletic.type.BaseballPitchOutcome
import com.theathletic.type.BasketballPlayType
import com.theathletic.type.CardType
import com.theathletic.type.FractionSeparator
import com.theathletic.type.GameCoverageDataType
import com.theathletic.type.GameStatCategory
import com.theathletic.type.GameStatGroup
import com.theathletic.type.GameStatusCode
import com.theathletic.type.GoalType
import com.theathletic.type.GradeStatus
import com.theathletic.type.HockeyPlayType
import com.theathletic.type.HockeyStrength
import com.theathletic.type.InjuryStatus
import com.theathletic.type.InningHalf
import com.theathletic.type.LeagueCode
import com.theathletic.type.PenaltyOutcome
import com.theathletic.type.Period
import com.theathletic.type.PlayerHand
import com.theathletic.type.Position
import com.theathletic.type.ScoreType
import com.theathletic.type.SoccerOfficialType
import com.theathletic.utility.safeLet

fun GetGameSummaryQuery.Data.toLocalModel(): GameSummaryLocalModel? {
    return game.fragments.gameSummary.toLocalModel()
}

fun GameSummaryUpdatesSubscription.Data.toLocalModel(): GameSummaryLocalModel? {
    return liveScoreUpdates?.fragments?.gameSummary?.toLocalModel()
}

fun GameSummary.toLocalModel(): GameSummaryLocalModel? {
    fragments.americanFootballGameSummary?.let { game ->
        return game.toAmericanFootballLocalModel(live_blog)
    }
    fragments.basketballGameSummary?.let { game ->
        return game.toBasketballLocalModel(live_blog)
    }
    fragments.baseballGameSummary?.let { game ->
        return game.toBaseballLocalModel(live_blog)
    }
    fragments.hockeyGameSummary?.let { game ->
        return game.toHockeyLocalModel(live_blog)
    }
    fragments.soccerGameSummary?.let { game ->
        return game.toSoccerLocalModel(live_blog)
    }
    return null
}

private fun AmericanFootballGameSummary.toAmericanFootballLocalModel(
    liveBlog: GameSummary.Live_blog?
): GameSummaryLocalModel {
    val possession = possession?.fragments?.possessionFragment?.toLocal()
    return GameSummaryLocalModel(
        id = id,
        scheduleAt = Datetime(scheduled_at ?: 0),
        isScheduledTimeTbd = time_tbd ?: false,
        sport = sport.toLocal(),
        league = league.fragments.league.toLocalModel(),
        coverage = coverage?.available_data?.mapNotNull { it.toLocal() } ?: listOf(CoverageDataType.ALL),
        permalink = permalink,
        status = status.toStatusLocalModel(),
        period = period_id.toLocal(),
        awayTeam = away_team?.fragments?.americanFootballGameSummaryTeam?.toLocalModel(possession),
        homeTeam = home_team?.fragments?.americanFootballGameSummaryTeam?.toLocalModel(possession),
        isAwayTeamTbd = away_team == null,
        isHomeTeamTbd = home_team == null,
        clock = clock,
        gameTitle = game_title,
        extras = null,
        liveBlog = liveBlog?.toLocalModel(),
        areCommentsDiscoverable = is_comments_discoverable,
        gradeStatus = grade_status.toLocalModel(),
        gameStatePrimary = game_status?.fragments?.gameState?.main,
        gameStateSecondary = game_status?.fragments?.gameState?.extra,
    )
}

private fun BasketballGameSummary.toBasketballLocalModel(
    liveBlog: GameSummary.Live_blog?
) = GameSummaryLocalModel(
    id = id,
    scheduleAt = Datetime(scheduled_at ?: 0),
    isScheduledTimeTbd = time_tbd ?: false,
    sport = sport.toLocal(),
    league = league.fragments.league.toLocalModel(),
    coverage = coverage?.available_data?.mapNotNull { it.toLocal() } ?: listOf(CoverageDataType.ALL),
    permalink = permalink,
    status = status.toStatusLocalModel(),
    period = period_id.toLocal(),
    awayTeam = away_team?.fragments?.basketballGameSummaryTeam?.toLocalModel(),
    homeTeam = home_team?.fragments?.basketballGameSummaryTeam?.toLocalModel(),
    isAwayTeamTbd = away_team == null,
    isHomeTeamTbd = home_team == null,
    clock = clock,
    gameTitle = game_title,
    extras = null,
    liveBlog = liveBlog?.toLocalModel(),
    areCommentsDiscoverable = is_comments_discoverable,
    gradeStatus = grade_status.toLocalModel(),
    gameStatePrimary = game_status?.fragments?.gameState?.main,
    gameStateSecondary = game_status?.fragments?.gameState?.extra,
)

private fun BaseballGameSummary.toBaseballLocalModel(
    liveBlog: GameSummary.Live_blog?
) = GameSummaryLocalModel(
    id = id,
    scheduleAt = Datetime(scheduled_at ?: 0),
    isScheduledTimeTbd = time_tbd ?: false,
    sport = sport.toLocal(),
    league = league.fragments.league.toLocalModel(),
    coverage = coverage?.available_data?.mapNotNull { it.toLocal() } ?: listOf(CoverageDataType.ALL),
    permalink = permalink,
    status = status.toStatusLocalModel(),
    period = period_id.toLocal(),
    awayTeam = away_team?.fragments?.baseballGameSummaryTeam?.toLocalModel(),
    homeTeam = home_team?.fragments?.baseballGameSummaryTeam?.toLocalModel(),
    isAwayTeamTbd = away_team == null,
    isHomeTeamTbd = home_team == null,
    clock = null,
    liveBlog = liveBlog?.toLocalModel(),
    gameTitle = game_title,
    extras = GameSummaryLocalModel.SportExtras.Baseball(
        outcome = outcome?.toLocalModel()
    ),
    areCommentsDiscoverable = is_comments_discoverable,
    gradeStatus = grade_status.toLocalModel(),
    gameStateSecondary = null,
    gameStatePrimary = null
)

private fun HockeyGameSummary.toHockeyLocalModel(
    liveBlog: GameSummary.Live_blog?
) = GameSummaryLocalModel(
    id = id,
    scheduleAt = Datetime(scheduled_at ?: 0),
    isScheduledTimeTbd = time_tbd ?: false,
    sport = sport.toLocal(),
    league = league.fragments.league.toLocalModel(),
    coverage = coverage?.available_data?.mapNotNull { it.toLocal() } ?: listOf(CoverageDataType.ALL),
    permalink = permalink,
    status = status.toStatusLocalModel(),
    period = period_id.toLocal(),
    awayTeam = away_team?.fragments?.hockeyGameSummaryTeam?.toLocalModel(),
    homeTeam = home_team?.fragments?.hockeyGameSummaryTeam?.toLocalModel(),
    isAwayTeamTbd = away_team == null,
    isHomeTeamTbd = home_team == null,
    clock = clock,
    gameTitle = game_title,
    extras = null,
    liveBlog = liveBlog?.toLocalModel(),
    areCommentsDiscoverable = is_comments_discoverable,
    gradeStatus = grade_status.toLocalModel(),
    gameStatePrimary = game_status?.fragments?.gameState?.main,
    gameStateSecondary = game_status?.fragments?.gameState?.extra
)

private fun SoccerGameSummary.toSoccerLocalModel(
    liveBlog: GameSummary.Live_blog?
) = GameSummaryLocalModel(
    id = id,
    scheduleAt = Datetime(scheduled_at ?: 0),
    isScheduledTimeTbd = time_tbd ?: false,
    sport = sport.toLocal(),
    league = league.fragments.league.toLocalModel(),
    coverage = coverage?.available_data?.mapNotNull { it.toLocal() } ?: listOf(CoverageDataType.ALL),
    permalink = permalink,
    status = status.toStatusLocalModel(),
    period = period_id.toLocal(),
    awayTeam = away_team?.fragments?.soccerGameSummaryTeam?.toLocalModel(),
    homeTeam = home_team?.fragments?.soccerGameSummaryTeam?.toLocalModel(),
    isAwayTeamTbd = away_team == null,
    isHomeTeamTbd = home_team == null,
    clock = clock,
    liveBlog = liveBlog?.toLocalModel(),
    gameTitle = game_title,
    extras = GameSummaryLocalModel.SportExtras.Soccer(
        relatedGameScheduleAt = related_game?.scheduled_at?.let { Datetime(it) },
        aggregateWinnerName = aggregate_winner?.display_name
    ),
    areCommentsDiscoverable = is_comments_discoverable,
    gradeStatus = grade_status.toLocalModel(),
    gameStatePrimary = game_status?.fragments?.gameState?.main,
    gameStateSecondary = game_status?.fragments?.gameState?.extra
)

fun AmericanFootballGameSummaryTeam.toLocalModel(
    possession: GameDetailLocalModel.Possession?
) = GameSummaryLocalModel.AmericanFootballGameSummaryTeam(
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

fun BasketballGameSummaryTeam.toLocalModel() = GameSummaryLocalModel.BasketballGameSummaryTeam(
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

fun BaseballGameSummaryTeam.toLocalModel() = GameSummaryLocalModel.BaseballGameSummaryTeam(
    id = team?.fragments?.teamLite?.id.orEmpty(),
    legacyId = team?.fragments?.teamLite?.legacy_team?.id?.toLong() ?: 0L,
    alias = team?.fragments?.teamLite?.alias.orEmpty(),
    displayName = team?.fragments?.teamLite?.display_name.orEmpty(),
    logos = team?.fragments?.teamLite?.logos?.toTeamLiteLogos() ?: emptyList(),
    score = score,
    currentRecord = current_record
)

fun HockeyGameSummaryTeam.toLocalModel() = GameSummaryLocalModel.HockeyGameSummaryTeam(
    id = team?.fragments?.teamLite?.id.orEmpty(),
    legacyId = team?.fragments?.teamLite?.legacy_team?.id?.toLong() ?: 0L,
    alias = team?.fragments?.teamLite?.alias.orEmpty(),
    displayName = team?.fragments?.teamLite?.display_name.orEmpty(),
    logos = team?.fragments?.teamLite?.logos?.toTeamLiteLogos() ?: emptyList(),
    score = score,
    currentRecord = current_record,
    strength = strength?.toLocal() ?: com.theathletic.gamedetail.data.local.HockeyStrength.UNKNOWN
)

fun SoccerGameSummaryTeam.toLocalModel() = GameSummaryLocalModel.SoccerGameSummaryTeam(
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

fun GetAmericanFootballGameQuery.Data.toLocalModel(): GameDetailLocalModel? {
    return game.fragments.americanFootballGameFragment?.toLocalModel()
}

fun GetBasketballGameQuery.Data.toLocalModel(): GameDetailLocalModel? {
    return game.fragments.basketballGameFragment?.toLocalModel()
}

fun GetHockeyGameQuery.Data.toLocalModel(): GameDetailLocalModel? {
    return game.fragments.hockeyGameFragment?.toLocalModel()
}

fun GetBaseballGameQuery.Data.toLocalModel(): GameDetailLocalModel? {
    return game.fragments.baseballGameFragment?.toLocalModel()
}

fun GetSoccerGameQuery.Data.toLocalModel(): GameDetailLocalModel? {
    return game.fragments.soccerGameFragment?.toLocalModel()
}

fun PlayerStatsUpdatesSubscription.Data.toLocalModel() =
    liveScoreUpdates?.fragments?.playerStats?.toLocalModel()

fun BaseballPlayerStatsUpdatesSubscription.Data.toLocalModel() =
    liveScoreUpdates?.fragments?.baseballPlayerStats?.toLocalModel()

fun GetPlayerStatsQuery.Data.toLocalModel() =
    game.fragments.playerStats.toLocalModel()

private fun PlayerStats.toLocalModel() = GameLineUpAndStats(
    awayTeamLineUp = away_team?.line_up?.fragments?.lineUp?.toLocalModel(),
    homeTeamLineUp = home_team?.line_up?.fragments?.lineUp?.toLocalModel(),
    teamStats = mapGameStats(
        awayTeamStats = null,
        homeTeamStats = null,
        homeTeamFirst = sport.toLocal().homeTeamFirst
    )
)

private fun BaseballPlayerStats.toLocalModel() = GameLineUpAndStats(
    awayTeamLineUp = away_team?.fragments?.baseballPlayerStatsPlayer?.toLineUp(),
    homeTeamLineUp = home_team?.fragments?.baseballPlayerStatsPlayer?.toLineUp(),
    teamStats = mapGameStats(
        awayTeamStats = null,
        homeTeamStats = null,
        homeTeamFirst = sport.toLocal().homeTeamFirst
    )
)

fun GetBaseballStatsQuery.Data.toLocalModel(): GameLineUpAndStats {
    val awayTeam = game.fragments.baseballStatsFragment?.away_team?.fragments?.baseballStatsGameTeamFragment
    val homeTeam = game.fragments.baseballStatsFragment?.home_team?.fragments?.baseballStatsGameTeamFragment

    return GameLineUpAndStats(
        awayTeamLineUp = awayTeam?.players?.toLineUp(),
        homeTeamLineUp = homeTeam?.players?.toLineUp(),
        teamStats = mapGameStats(
            awayTeamStats = awayTeam?.stats?.mapNotNull { it.fragments.gameStat.toLocalStats() },
            homeTeamStats = homeTeam?.stats?.mapNotNull { it.fragments.gameStat.toLocalStats() },
            homeTeamFirst = Sport.BASEBALL.homeTeamFirst
        )
    )
}

data class GameDetailsRemoteModel(
    val gameDetailLocalModel: GameDetailLocalModel?,
    val gameFeedLocalModel: FeedResponse?
)

private fun GameSummary.Live_blog.toLocalModel(): LiveBlogLinks {
    return fragments.liveBlogLinksFragment.toLocal()
}

fun GameFeedItemFragment.toGameFeedItem(index: Int, gameId: String): FeedItem? {
    asLiveBlog?.let { layout ->
        return feedItem(
            gameId = gameId,
            style = FeedItemStyle.GAME_FEED_LIVE_BLOG_HEADER,
            position = index.toLong(),
            entities = listOf(layout.fragments.liveBlogLiteFragment.toEntity())
        )
    }

    asLiveBlogPost?.let { layout ->
        return feedItem(
            gameId = gameId,
            style = FeedItemStyle.GAME_FEED_LIVE_BLOG_POST,
            position = index.toLong(),
            entities = listOf(layout.fragments.liveBlogPostLiteFragment.toEntity())
        )
    }

    return null
}

private fun feedItem(
    gameId: String,
    style: FeedItemStyle,
    position: Long,
    entities: List<AthleticEntity>,
): FeedItem {
    return FeedItem().also {
        it.feedId = FeedType.GameFeed(gameId).compositeId
        it.pageIndex = position
        it.page = 0
        it.itemType = FeedItemType.ROW
        it.style = style
        it.entities = entities
    }
}

fun AmericanFootballGameFragment.toLocalModel() = GameDetailLocalModel(
    id = id,
    awayTeam = away_team?.fragments?.americanFootballGameTeamFragment?.toLocal(),
    homeTeam = home_team?.fragments?.americanFootballGameTeamFragment?.toLocal(),
    status = status.toStatusLocalModel(),
    scheduleAt = Datetime(scheduled_at ?: 0),
    isScheduledTimeTbd = time_tbd ?: false,
    venue = venue?.name,
    venueCity = venue?.city,
    league = league.fragments.league.toLocalModel(),
    sport = sport.toLocal(),
    clock = clock,
    period = period_id.toLocal(),
    permalink = permalink,
    coverage = coverage?.available_data?.mapNotNull { it.toLocal() } ?: listOf(CoverageDataType.ALL),
    events = emptyList(),
    timeline = emptyList(),
    awayTeamHomeTeamStats = mapGameStats(
        awayTeamStats = away_team?.fragments?.americanFootballGameTeamFragment?.toTeamStats(),
        homeTeamStats = home_team?.fragments?.americanFootballGameTeamFragment?.toTeamStats(),
        homeTeamFirst = sport.toLocal().homeTeamFirst
    ),
    awayTeamHomeTeamSeasonStats = mapSeasonStats(
        awayTeamStats = away_team?.fragments?.americanFootballGameTeamFragment?.toSeasonStats(),
        homeTeamStats = home_team?.fragments?.americanFootballGameTeamFragment?.toSeasonStats(),
        homeTeamFirst = sport.toLocal().homeTeamFirst
    ),
    sportExtras = GameDetailLocalModel.AmericanFootballExtras(
        possession = possession?.fragments?.possessionFragment?.toLocal(),
        lastPlay = american_football_last_play,
        scoringPlays = scoring_plays.map { it.fragments.scoringPlayFragment.toLocal() },
        weather = weather?.fragments?.weatherFragment?.toLocalModel(),
        recentPlays = recent_plays.mapNotNull { it.fragments.americanFootballPlay?.toPlay() }
    ),
    oddsPregame = odds_pregame.mapNotNull { it.fragments.gameOddsMarketFragment.toGameOdds() },
    broadcastNetwork = broadcast_network,
    gradeStatus = grade_status.toLocalModel(),
    gameTicket = tickets?.fragments?.gameTicket?.toLocalModel(),
    seasonName = season_stats.fragments.seasonStats.toLocal(sport, league.fragments.league),
    topComments = top_comments.map { it.fragments.scoresTopComment.toLocalModel() },
    areCommentsDiscoverable = is_comments_discoverable
)

private fun SeasonStats.toLocal(sport: com.theathletic.type.Sport, league: League): String? {
    val seasonYear = if (season.active == false) season.name else null
    val seasonPhase = if (sport == com.theathletic.type.Sport.soccer &&
        league.id != LeagueCode.mls
    ) {
        league.display_name
    } else {
        season_type?.name
    }

    return listOfNotNull(seasonYear, seasonPhase).joinToString(separator = " ").ifEmpty { null }
}

private fun GameTicket.toLocalModel(): GameDetailLocalModel.GameTicket {
    return this.let { ticket ->
        GameDetailLocalModel.GameTicket(
            logoDarkMode = logos_dark_mode.map { it.fragments.gameTicketLogo.toLocal() },
            logoLightMode = logos_light_mode.map { it.fragments.gameTicketLogo.toLocal() },
            minPrice = min_price.map { it.fragments.gameTicketPrice.toLocal() },
            url = uri,
            provider = provider
        )
    }
}

private fun GameTicketPrice.toLocal() = GameDetailLocalModel.GameTicketPrice(
    amount = this.amount,
    // todo: Adil - setting it to USD for default, till backend gets more currency types
    currency = GameTicketCurrency.USD
)

private fun GameTicketLogo.toLocal() = SizedImage(
    height = this.height,
    uri = this.uri,
    width = this.width
)

fun SoccerGameFragment.toLocalModel() = GameDetailLocalModel(
    id = id,
    awayTeam = away_team?.fragments?.soccerGameTeamFragment?.toLocal(),
    homeTeam = home_team?.fragments?.soccerGameTeamFragment?.toLocal(),
    status = status.toStatusLocalModel(),
    scheduleAt = Datetime(scheduled_at ?: 0),
    isScheduledTimeTbd = time_tbd ?: false,
    venue = venue?.name.orEmpty(),
    venueCity = venue?.city.orEmpty(),
    league = league.fragments.league.toLocalModel(),
    sport = sport.toLocal(),
    clock = clock,
    period = period_id.toLocal(),
    permalink = permalink,
    coverage = coverage?.available_data?.mapNotNull { it.toLocal() } ?: listOf(CoverageDataType.ALL),
    events = key_events.mapNotNull { it.fragments.keyEvent.toLocal() }
        .sortedWith { event1: GameDetailLocalModel.GameEvent, event2: GameDetailLocalModel.GameEvent ->
            event1.occurredAt.compareTo(event2.occurredAt)
        },
    timeline = timeline.mapNotNull { it.fragments.timelineEvent.toLocal() }
        .filter { includeEventInTimeline(it) }
        .sortedWith { event1: GameDetailLocalModel.TimelineEvent, event2: GameDetailLocalModel.TimelineEvent ->
            event1.occurredAt.compareTo(event2.occurredAt)
        },
    awayTeamHomeTeamStats = mapGameStats(
        awayTeamStats = away_team?.fragments?.soccerGameTeamFragment?.toTeamStats(),
        homeTeamStats = home_team?.fragments?.soccerGameTeamFragment?.toTeamStats(),
        homeTeamFirst = sport.toLocal().homeTeamFirst
    ),
    awayTeamHomeTeamSeasonStats = mapSeasonStats(
        awayTeamStats = away_team?.fragments?.soccerGameTeamFragment?.toSeasonStats(),
        homeTeamStats = home_team?.fragments?.soccerGameTeamFragment?.toSeasonStats(),
        homeTeamFirst = sport.toLocal().homeTeamFirst
    ),
    oddsPregame = odds_pregame.mapNotNull { it.fragments.gameOddsMarketFragment.toGameOdds() },
    broadcastNetwork = null,
    gradeStatus = grade_status.toLocalModel(),
    sportExtras = GameDetailLocalModel.SoccerExtras(
        matchOfficials = officials.map { it.toLocal() },
        keyMoments = key_plays.mapNotNull { it.fragments.soccerPlaysFragment.toLocal() },
        recentMoments = recent_plays.mapNotNull { it.fragments.soccerPlaysFragment.toLocal() }
    ),
    gameTicket = tickets?.fragments?.gameTicket?.toLocalModel(),
    seasonName = season_stats.fragments.seasonStats.toLocal(sport, league.fragments.league),
    topComments = top_comments.map { it.fragments.scoresTopComment.toLocalModel() },
    areCommentsDiscoverable = is_comments_discoverable
)

fun BasketballGameFragment.toLocalModel() = GameDetailLocalModel(
    id = id,
    awayTeam = away_team?.fragments?.basketballGameTeamFragment?.toLocal(),
    homeTeam = home_team?.fragments?.basketballGameTeamFragment?.toLocal(),
    status = status.toStatusLocalModel(),
    scheduleAt = Datetime(scheduled_at ?: 0),
    isScheduledTimeTbd = time_tbd ?: false,
    venue = venue?.name.orEmpty(),
    venueCity = venue?.city.orEmpty(),
    league = league.fragments.league.toLocalModel(),
    sport = sport.toLocal(),
    clock = clock,
    period = period_id.toLocal(),
    permalink = permalink,
    coverage = coverage?.available_data?.mapNotNull { it.toLocal() } ?: listOf(CoverageDataType.ALL),
    events = emptyList(),
    timeline = emptyList(),
    awayTeamHomeTeamStats = mapGameStats(
        awayTeamStats = away_team?.fragments?.basketballGameTeamFragment?.toTeamStats(),
        homeTeamStats = home_team?.fragments?.basketballGameTeamFragment?.toTeamStats(),
        homeTeamFirst = sport.toLocal().homeTeamFirst
    ),
    awayTeamHomeTeamSeasonStats = mapSeasonStats(
        awayTeamStats = away_team?.fragments?.basketballGameTeamFragment?.toSeasonStats(),
        homeTeamStats = home_team?.fragments?.basketballGameTeamFragment?.toSeasonStats(),
        homeTeamFirst = sport.toLocal().homeTeamFirst
    ),
    oddsPregame = odds_pregame.mapNotNull { it.fragments.gameOddsMarketFragment.toGameOdds() },
    broadcastNetwork = broadcast_network,
    gradeStatus = grade_status.toLocalModel(),
    gameTicket = tickets?.fragments?.gameTicket?.toLocalModel(),
    sportExtras = GameDetailLocalModel.BasketballExtras(
        recentPlays = recent_plays.map { it.fragments.basketballPlayFragment.toLocal() }
    ),
    seasonName = season_stats.fragments.seasonStats.toLocal(sport, league.fragments.league),
    topComments = top_comments.map { it.fragments.scoresTopComment.toLocalModel() },
    areCommentsDiscoverable = is_comments_discoverable
)

fun HockeyGameFragment.toLocalModel() = GameDetailLocalModel(
    id = id,
    awayTeam = away_team?.fragments?.hockeyGameTeamFragment?.toLocal(),
    homeTeam = home_team?.fragments?.hockeyGameTeamFragment?.toLocal(),
    status = status.toStatusLocalModel(),
    scheduleAt = Datetime(scheduled_at ?: 0),
    isScheduledTimeTbd = time_tbd ?: false,
    venue = venue?.name.orEmpty(),
    venueCity = venue?.city.orEmpty(),
    league = league.fragments.league.toLocalModel(),
    sport = sport.toLocal(),
    clock = clock,
    period = period_id.toLocal(),
    permalink = permalink,
    coverage = coverage?.available_data?.mapNotNull { it.toLocal() } ?: listOf(CoverageDataType.ALL),
    events = emptyList(),
    timeline = emptyList(),
    awayTeamHomeTeamStats = mapGameStats(
        awayTeamStats = away_team?.fragments?.hockeyGameTeamFragment?.toTeamStats(),
        homeTeamStats = home_team?.fragments?.hockeyGameTeamFragment?.toTeamStats(),
        homeTeamFirst = sport.toLocal().homeTeamFirst
    ),
    awayTeamHomeTeamSeasonStats = mapSeasonStats(
        awayTeamStats = away_team?.fragments?.hockeyGameTeamFragment?.toSeasonStats(),
        homeTeamStats = home_team?.fragments?.hockeyGameTeamFragment?.toSeasonStats(),
        homeTeamFirst = sport.toLocal().homeTeamFirst
    ),
    oddsPregame = odds_pregame.mapNotNull { it.fragments.gameOddsMarketFragment.toGameOdds() },
    broadcastNetwork = null,
    seasonName = season_stats.fragments.seasonStats.toLocal(sport, league.fragments.league),
    gradeStatus = grade_status.toLocalModel(),
    gameTicket = tickets?.fragments?.gameTicket?.toLocalModel(),
    sportExtras = GameDetailLocalModel.HockeyExtras(
        scoringPlays = scoring_plays.mapNotNull { it.fragments.hockeyPlaysFragment.toLocal() },
        recentPlays = recent_plays.mapNotNull { it.fragments.hockeyPlaysFragment.toLocal() }
    ),
    topComments = top_comments.map { it.fragments.scoresTopComment.toLocalModel() },
    areCommentsDiscoverable = is_comments_discoverable
)

fun BaseballGameFragment.toLocalModel() = GameDetailLocalModel(
    id = id,
    awayTeam = away_team?.fragments?.baseballGameTeamFragment?.toLocal(),
    homeTeam = home_team?.fragments?.baseballGameTeamFragment?.toLocal(),
    status = status.toStatusLocalModel(),
    scheduleAt = Datetime(scheduled_at ?: 0),
    isScheduledTimeTbd = time_tbd ?: false,
    venue = venue?.name.orEmpty(),
    venueCity = venue?.city.orEmpty(),
    league = league.fragments.league.toLocalModel(),
    sport = sport.toLocal(),
    clock = clock,
    period = period_id.toLocal(),
    permalink = permalink,
    coverage = coverage?.available_data?.mapNotNull { it.toLocal() } ?: listOf(CoverageDataType.ALL),
    events = emptyList(),
    timeline = emptyList(),
    seasonName = season_stats.fragments.seasonStats.toLocal(sport, league.fragments.league),
    awayTeamHomeTeamStats = mapGameStats(
        awayTeamStats = away_team?.fragments?.baseballGameTeamFragment?.toTeamStats(),
        homeTeamStats = home_team?.fragments?.baseballGameTeamFragment?.toTeamStats(),
        homeTeamFirst = sport.toLocal().homeTeamFirst
    ),
    awayTeamHomeTeamSeasonStats = mapSeasonStats(
        awayTeamStats = away_team?.fragments?.baseballGameTeamFragment?.toSeasonStats(),
        homeTeamStats = home_team?.fragments?.baseballGameTeamFragment?.toSeasonStats(),
        homeTeamFirst = sport.toLocal().homeTeamFirst
    ),
    oddsPregame = odds_pregame.mapNotNull { it.fragments.gameOddsMarketFragment.toGameOdds() },
    broadcastNetwork = null,
    gradeStatus = grade_status.toLocalModel(),
    gameTicket = tickets?.fragments?.gameTicket?.toLocalModel(),
    sportExtras = GameDetailLocalModel.BaseballExtras(
        scoringPlays = scoring_plays.map { it.fragments.baseballTeamPlayFragment.toScoringPlay() },
        inningPlays = inning_plays.mapNotNull { it.fragments.baseballInningPlayFragment.toInningsPlay() },
        currentInningPlays = current_inning_plays.mapNotNull { it.fragments.baseballGamePlay.toPlays() },
        outcome = outcome?.fragments?.baseballOutcomeFragment?.toLocal(),
        pitching = pitching?.fragments?.baseballPitchingFragment?.toLocal(),
        inning = inning,
        inningHalf = inning_half?.toLocalModel()
    ),
    topComments = top_comments.map { it.fragments.scoresTopComment.toLocalModel() },
    areCommentsDiscoverable = is_comments_discoverable
)

private fun AmericanFootballGameTeamFragment.toLocal(): GameDetailLocalModel.AmericanFootballGameTeam {
    val team = team?.fragments?.team?.toLocalModel()
    return GameDetailLocalModel.AmericanFootballGameTeam(
        id = id,
        team = team,
        score = score,
        periodScore = scoring.map { it.fragments.periodScoreFragment.toLocalModel() },
        lineUp = null,
        currentRecord = current_record,
        currentRanking = current_ranking,
        remainingTimeouts = remaining_timeouts ?: 0,
        usedTimeouts = used_timeouts ?: 0,
        recentGames = last_games.map { it.fragments.recentGameFragment.toLocal() },
        teamLeaders = stat_leaders.map { it.fragments.teamLeader.toLocal(team) },
        topPerformers = top_performers.map { it.fragments.topPerformer.toLocal(team) },
        injuries = this.team?.fragments?.team?.injuries?.map { it.toLocal() } ?: emptyList(),
        players = line_up?.players?.map {
            it.fragments.gradablePlayer.toLocalModel()
        } ?: emptyList()
    )
}

private fun BasketballGameTeamFragment.toLocal(): GameDetailLocalModel.BasketballGameTeam {
    val team = team?.fragments?.team?.toLocalModel()
    return GameDetailLocalModel.BasketballGameTeam(
        id = id,
        team = team,
        score = score,
        periodScore = scoring.map { it.fragments.periodScoreFragment.toLocalModel() },
        lineUp = null,
        recentGames = last_games.map { it.fragments.recentGameFragment.toLocal() },
        currentRecord = current_record,
        currentRanking = current_ranking,
        remainingTimeouts = remaining_timeouts ?: 0,
        usedTimeouts = used_timeouts ?: 0,
        teamLeaders = stat_leaders.map { it.fragments.teamLeader.toLocal(team) },
        topPerformers = top_performers.map { it.fragments.topPerformer.toLocal(team) },
        injuries = this.team?.fragments?.team?.injuries?.map { it.toLocal() } ?: emptyList(),
        players = line_up?.players?.map {
            it.fragments.gradablePlayer.toLocalModel()
        } ?: emptyList()
    )
}

private fun HockeyGameTeamFragment.toLocal(): GameDetailLocalModel.HockeyGameTeam {
    val team = team?.fragments?.team?.toLocalModel()
    return GameDetailLocalModel.HockeyGameTeam(
        id = id,
        team = team,
        score = score,
        periodScore = scoring.map { it.fragments.periodScoreFragment.toLocalModel() },
        lineUp = null,
        recentGames = last_games.map { it.fragments.recentGameFragment.toLocal() },
        currentRecord = current_record,
        strength = strength?.toLocal(),
        teamLeaders = stat_leaders.map { it.fragments.teamLeader.toLocal(team) },
        topPerformers = top_performers.map { it.fragments.topPerformer.toLocal(team) },
        injuries = this.team?.fragments?.team?.injuries?.map { it.toLocal() } ?: emptyList(),
        players = line_up?.players?.map {
            it.fragments.gradablePlayer.toLocalModel()
        } ?: emptyList()
    )
}

private fun BaseballGameTeamFragment.toLocal(): GameDetailLocalModel.BaseballGameTeam {
    val team = team?.fragments?.team?.toLocalModel()
    return GameDetailLocalModel.BaseballGameTeam(
        id = id,
        team = team,
        score = score,
        totalRuns = runs,
        totalHits = hits,
        totalErrors = errors,
        periodScore = emptyList(),
        lineUp = null,
        recentGames = last_games.map { it.fragments.recentGameFragment.toLocal() },
        currentRecord = current_record,
        startingPitcher = starting_pitcher?.toLocal(),
        pitcherState = this.toPitcherState(),
        inningScores = scoring.map { it.fragments.inningScoreFragment.toLocal() },
        teamLeaders = stat_leaders.map { it.fragments.teamLeader.toLocal(team) },
        topPerformers = top_performers.map { it.fragments.topPerformer.toLocal(team) },
        injuries = this.team?.fragments?.team?.injuries?.map { it.toLocal() } ?: emptyList(),
        players = line_up?.players?.map {
            it.fragments.gradablePlayer.toLocalModel()
        } ?: emptyList()
    )
}

private fun BaseballGameTeamFragment.toPitcherState(): PitcherState {
    return if (starting_pitcher_confirmed == true) {
        PitcherState.CONFIRMED
    } else {
        PitcherState.PROBABLE
    }
}

private fun SoccerGameTeamFragment.toLocal(): GameDetailLocalModel.SoccerGameTeam {
    val team = team?.fragments?.team?.toLocalModel()
    return GameDetailLocalModel.SoccerGameTeam(
        id = id,
        team = team,
        score = score,
        penaltyScore = penalty_score,
        periodScore = emptyList(),
        lineUp = line_up?.fragments?.lineUp?.toLocalModel(),
        recentGames = last_games.map { it.fragments.recentGameFragment.toLocal() },
        currentRecord = current_record,
        teamLeaders = stat_leaders.map { it.fragments.teamLeader.toLocal(team) },
        topPerformers = top_performers.map { it.fragments.topPerformer.toLocal(team) },
        injuries = this.team?.fragments?.team?.injuries?.map { it.toLocal() },
        expectedGoals = this.expected_goals?.fragments?.gameStat?.toLocalStats(),
        players = line_up?.gradable_players?.map {
            it.fragments.gradablePlayer.toLocalModel()
        } ?: emptyList()
    )
}

private fun BaseballGameTeamFragment.Starting_pitcher.toLocal() = GameDetailLocalModel.BaseballPlayer(
    id = id,
    player = player.fragments.startingPitcherFragment.toLocalModel(),
    gameStats = emptyList(),
    seasonAvg = null
)

private fun AmericanFootballGameTeamFragment.toTeamStats(): List<GameDetailLocalModel.Statistic>? {
    return stats?.let { list ->
        list.mapNotNull { it.fragments.gameStat.toLocalStats() }
    }
}

private fun BasketballGameTeamFragment.toTeamStats(): List<GameDetailLocalModel.Statistic>? {
    return stats?.let { list ->
        list.mapNotNull { it.fragments.gameStat.toLocalStats() }
    }
}

private fun HockeyGameTeamFragment.toTeamStats(): List<GameDetailLocalModel.Statistic>? {
    return stats?.let { list ->
        list.mapNotNull { it.fragments.gameStat.toLocalStats() }
    }
}

private fun BaseballGameTeamFragment.toTeamStats(): List<GameDetailLocalModel.Statistic>? {
    return stats?.let { list ->
        list.mapNotNull { it.fragments.gameStat.toLocalStats() }
    }
}

private fun SoccerGameTeamFragment.toTeamStats(): List<GameDetailLocalModel.Statistic>? {
    return stats?.let { list ->
        list.mapNotNull { it.fragments.gameStat.toLocalStats() }
    }
}

private fun AmericanFootballGameTeamFragment.toSeasonStats(): List<GameDetailLocalModel.RankedStat> {
    return season_stats.let { list ->
        list.map { it.fragments.rankedStat.toRankedStat() }
    }
}

private fun SoccerGameTeamFragment.toSeasonStats(): List<GameDetailLocalModel.RankedStat> {
    return season_stats.let { list ->
        list.map { it.fragments.rankedStat.toRankedStat() }
    }
}

private fun HockeyGameTeamFragment.toSeasonStats(): List<GameDetailLocalModel.RankedStat> {
    return season_stats.let { list ->
        list.map { it.fragments.rankedStat.toRankedStat() }
    }
}

private fun BaseballGameTeamFragment.toSeasonStats(): List<GameDetailLocalModel.RankedStat> {
    return season_stats.let { list ->
        list.map { it.fragments.rankedStat.toRankedStat() }
    }
}

private fun BasketballGameTeamFragment.toSeasonStats(): List<GameDetailLocalModel.RankedStat> {
    return season_stats.let { list ->
        list.map { it.fragments.rankedStat.toRankedStat() }
    }
}

private fun RankedStat.toRankedStat() = GameDetailLocalModel.RankedStat(
    id = id,
    parentStatType = parent_stat_type,
    parentStatCategory = parent_stat_category,
    rank = rank,
    statCategory = stat_category,
    statHeaderLabel = stat_header_label,
    statLabel = stat_label,
    statType = stat_type,
    statValue = stat_value
)

fun Team.toLocalModel() = GameDetailLocalModel.Team(
    id = id,
    alias = alias.orEmpty(),
    name = name.orEmpty(),
    logos = logos.toLogos(),
    displayName = display_name.orEmpty(),
    primaryColor = color_primary,
    accentColor = color_accent,
    currentRanking = current_ranking
)

private fun League.toLocalModel() = GameDetailLocalModel.League(
    legacyLeague = id.toLocalLeague,
    id = id.rawValue,
    alias = alias,
    displayName = display_name
)

fun TeamMember.toLocalModel() = GameDetailLocalModel.GenericTeamMember(
    id = id,
    country = country.orEmpty(),
    displayName = display_name,
    firstName = first_name,
    lastName = last_name,
    position = role?.position?.toLocal()
)

private fun TeamMemberBaseball.toLocalModel() = GameDetailLocalModel.BaseballTeamMember(
    id = id,
    country = null,
    displayName = display_name,
    firstName = null,
    lastName = null,
    position = position.toLocal(),
    headshots = headshots.toBaseballHeadshot(),
    batHandedness = bat_hand.toLocal(),
    throwHandedness = throw_hand.toLocal(),
    seasonStats = emptyList(),
    teamColor = role?.team?.color_primary
)

private fun StartingPitcherFragment.toLocalModel() = GameDetailLocalModel.BaseballTeamMember(
    id = id,
    country = null,
    displayName = display_name,
    firstName = null,
    lastName = null,
    position = null,
    headshots = headshots.toStartingPitcherHeadshots(),
    batHandedness = bat_hand.toLocal(),
    throwHandedness = throw_hand.toLocal(),
    seasonStats = season_stats.mapNotNull { it.fragments.gameStat.toLocalStats() },
    teamColor = null
)

private fun includeEventInTimeline(event: GameDetailLocalModel.TimelineEvent): Boolean {
    return when {
        event.period == com.theathletic.gamedetail.data.local.Period.PRE_GAME -> false
        event is GameDetailLocalModel.PeriodEvent &&
            event.period == com.theathletic.gamedetail.data.local.Period.EXTRA_TIME_SECOND_HALF -> false
        else -> true
    }
}

private fun TimelineEvent.toLocal(): GameDetailLocalModel.TimelineEvent? {
    fragments.cardEvent?.let { return it.toLocal() }
    fragments.goalEvent?.let { return it.toLocal() }
    fragments.substitutionEvent?.let { return it.toLocal() }
    fragments.penaltyShotEvent?.let { return it.toLocal() }
    fragments.periodEvent?.let { return it.toLocal() }
    return null
}

private fun KeyEvent.toLocal(): GameDetailLocalModel.GameEvent? {
    fragments.cardEvent?.let { return it.toLocal() }
    fragments.goalEvent?.let { return it.toLocal() }
    fragments.substitutionEvent?.let { return it.toLocal() }
    return null
}

private fun CardEvent.toLocal() = GameDetailLocalModel.CardEvent(
    id = id,
    team = team.fragments.team.toLocalModel(),
    occurredAt = Datetime(occurred_at),
    matchTimeDisplay = match_time_display,
    period = period_id.toLocal(),
    cardedPlayer = carded_player.fragments.teamMember.toLocalModel(),
    cardType = card_type.toLocal()
)

private fun GoalEvent.toLocal() = GameDetailLocalModel.GoalEvent(
    id = id,
    team = team.fragments.team.toLocalModel(),
    occurredAt = Datetime(occurred_at),
    matchTimeDisplay = match_time_display,
    period = period_id.toLocal(),
    scorer = goal_scorer.fragments.teamMember.toLocalModel(),
    goalType = goal_type.toLocal()
)

private fun SubstitutionEvent.toLocal() = GameDetailLocalModel.SubstitutionEvent(
    id = id,
    team = team.fragments.team.toLocalModel(),
    occurredAt = Datetime(occurred_at),
    matchTimeDisplay = match_time_display,
    period = period_id.toLocal(),
    playerOn = player_on.fragments.teamMember.toLocalModel(),
    playerOff = player_off.fragments.teamMember.toLocalModel()
)

private fun PenaltyShotEvent.toLocal(): GameDetailLocalModel.PenaltyShotEvent {
    return GameDetailLocalModel.PenaltyShotEvent(
        id = id,
        team = team.fragments.teamLite.toLocalModel(),
        occurredAt = Datetime(occurred_at),
        matchTimeDisplay = match_time_display,
        period = period_id.toLocal(),
        penaltyTaker = penalty_taker.fragments.teamMember.toLocalModel(),
        outcome = outcome.toLocal()
    )
}

private fun PeriodEvent.toLocal() = GameDetailLocalModel.PeriodEvent(
    id = id,
    occurredAt = Datetime(occurred_at),
    period = period_id.toLocal()
)

private fun LineUp.toLocalModel() = GameDetailLocalModel.LineUp(
    formation = formation,
    formationImage = image_uri,
    manager = manager,
    players = players.map { it.fragments.player.toLocalModel() }
)

private fun BaseballPlayerStatsPlayer.toLineUp() = GameDetailLocalModel.LineUp(
    formation = null,
    formationImage = null,
    manager = null,
    players = players.map { it.fragments.baseballPlayerFragment.toLocal() }
)

private fun List<BaseballStatsGameTeamFragment.Player>.toLineUp() = GameDetailLocalModel.LineUp(
    formation = null,
    formationImage = null,
    manager = null,
    players = map { it.fragments.baseballPlayerFragment.toLocal() }
)

private fun BaseballPlayerFragment.toLocal() = GameDetailLocalModel.Player(
    id = id,
    displayName = display_name,
    jerseyNumber = null,
    position = position.toLocal(),
    regularPosition = PlayerPosition.UNKNOWN,
    place = Int.MAX_VALUE,
    starter = false,
    playerOrder = order,
    outcome = pitching_outcome,
    statistics = stats.mapNotNull { it.fragments.gameStat.toLocalStats() }
        .sortedBy { it.type }
)

private fun Player.toLocalModel() = GameDetailLocalModel.Player(
    id = id,
    displayName = display_name,
    jerseyNumber = jersey_number,
    position = position.toLocal(),
    regularPosition = regular_position.toLocal(),
    place = place?.toInt() ?: Int.MAX_VALUE, // Substitutes have a null so keep them at the bottom
    starter = starter ?: false,
    playerOrder = 0,
    outcome = null,
    captain = captain ?: false,
    statistics = stats.mapNotNull { it.fragments.gameStat.toLocalStats() }
        .sortedBy { it.type }
)

private fun RecentGameFragment.toLocal() = GameDetailLocalModel.RecentGame(
    id = id,
    scheduleAt = Datetime(scheduled_at ?: 0),
    period = period_id.toLocal(),
    awayTeam = away_team?.fragments?.recentGameTeamFragment?.toLocal(),
    homeTeam = home_team?.fragments?.recentGameTeamFragment?.toLocal()
)

private fun RecentGameTeamFragment.toLocal() = GameDetailLocalModel.RecentGameTeam(
    id = id,
    score = score,
    alias = team?.fragments?.team?.alias.orEmpty(),
    logos = team?.fragments?.team?.logos?.toLogos() ?: emptyList()
)

private fun SoccerGameFragment.Official.toLocal() = GameDetailLocalModel.SoccerOfficial(
    name = this.name,
    officialType = this.type.toLocal()
)

fun GameStatusCode?.toStatusLocalModel(): GameStatus {
    this ?: return GameStatus.UNKNOWN
    return when (this) {
        GameStatusCode.cancelled -> GameStatus.CANCELED
        GameStatusCode.final -> GameStatus.FINAL
        GameStatusCode.in_progress -> GameStatus.IN_PROGRESS
        GameStatusCode.if_necessary -> GameStatus.IF_NECESSARY
        GameStatusCode.postponed -> GameStatus.POSTPONED
        GameStatusCode.scheduled -> GameStatus.SCHEDULED
        GameStatusCode.suspended -> GameStatus.SUSPENDED
        GameStatusCode.delayed -> GameStatus.DELAYED
        else -> GameStatus.UNKNOWN
    }
}

fun Position?.toLocal(): PlayerPosition {
    this ?: return PlayerPosition.UNKNOWN
    return when (this) {
        // Soccer
        Position.goalkeeper -> PlayerPosition.GOALKEEPER
        Position.defender -> PlayerPosition.DEFENDER
        Position.wing_back -> PlayerPosition.WING_BACK
        Position.defensive_midfielder -> PlayerPosition.DEFENSIVE_MIDFIELDER
        Position.midfielder -> PlayerPosition.MIDFIELDER
        Position.attacking_midfielder -> PlayerPosition.ATTACKING_MIDFIELDER
        Position.attacker -> PlayerPosition.ATTACKER
        Position.striker -> PlayerPosition.STRIKER
        Position.substitute -> PlayerPosition.SUBSTITUTE
        // American Football
        Position.center -> PlayerPosition.CENTER
        Position.cornerback -> PlayerPosition.CORNER_BACK
        Position.defensive_back -> PlayerPosition.DEFENSIVE_BACK
        Position.defensive_end -> PlayerPosition.DEFENSIVE_END
        Position.defensive_lineman -> PlayerPosition.DEFENSIVE_LINEMAN
        Position.defensive_tackle -> PlayerPosition.DEFENSIVE_TACKLE
        Position.free_safety -> PlayerPosition.FREE_SAFETY
        Position.fullback -> PlayerPosition.FULLBACK
        Position.inside_linebacker -> PlayerPosition.INSIDE_LINEBACKER
        Position.kicker -> PlayerPosition.KICKER
        Position.linebacker -> PlayerPosition.LINEBACKER
        Position.long_snapper -> PlayerPosition.LONG_SNAPPER
        Position.middle_linebacker -> PlayerPosition.MIDDLE_LINEBACKER
        Position.nose_tackle -> PlayerPosition.NOSE_TACKLE
        Position.offensive_guard -> PlayerPosition.OFFENSIVE_GUARD
        Position.offensive_lineman -> PlayerPosition.OFFENSIVE_LINEMAN
        Position.offensive_tackle -> PlayerPosition.OFFENSIVE_TACKLE
        Position.outside_linebacker -> PlayerPosition.OUTSIDE_LINEBACKER
        Position.punter -> PlayerPosition.PUNTER
        Position.quarterback -> PlayerPosition.QUARTERBACK
        Position.running_back -> PlayerPosition.RUNNING_BACK
        Position.safety -> PlayerPosition.SAFETY
        Position.strong_safety -> PlayerPosition.STRONG_SAFETY
        Position.tight_end -> PlayerPosition.TIGHT_END
        Position.wide_receiver -> PlayerPosition.WIDE_RECEIVER
        // Basketball
        Position.point_guard -> PlayerPosition.POINT_GUARD
        Position.power_forward -> PlayerPosition.POWER_FORWARD
        Position.shooting_guard -> PlayerPosition.SHOOTING_GUARD
        Position.small_forward -> PlayerPosition.SMALL_FORWARD
        Position.forward_center -> PlayerPosition.FORWARD_CENTER
        Position.forward_guard -> PlayerPosition.FORWARD_GUARD
        Position.guard -> PlayerPosition.GUARD
        Position.guard_forward -> PlayerPosition.GUARD_FORWARD
        // Hockey
        Position.goalie -> PlayerPosition.GOALIE
        Position.defense -> PlayerPosition.DEFENSE
        Position.forward -> PlayerPosition.FORWARD
        Position.left_wing -> PlayerPosition.LEFT_WING
        Position.right_wing -> PlayerPosition.RIGHT_WING
        // Baseball
        Position.catcher -> PlayerPosition.CATCHER
        Position.center_field -> PlayerPosition.CENTER_FIELD
        Position.designated_hitter -> PlayerPosition.DESIGNATED_HITTER
        Position.first_base -> PlayerPosition.FIRST_BASE
        Position.left_field -> PlayerPosition.LEFT_FIELD
        Position.pinch_hitter -> PlayerPosition.PINCH_HITTER
        Position.pinch_runner -> PlayerPosition.PINCH_RUNNER
        Position.pitcher -> PlayerPosition.PITCHER
        Position.relief_pitcher -> PlayerPosition.RELIEF_PITCHER
        Position.right_field -> PlayerPosition.RIGHT_FIELD
        Position.second_base -> PlayerPosition.SECOND_BASE
        Position.shortstop -> PlayerPosition.SHORTSTOP
        Position.starting_pitcher -> PlayerPosition.STARTING_PITCHER
        Position.third_base -> PlayerPosition.THIRD_BASE
        // Staff
        Position.head_coach -> PlayerPosition.HEAD_COACH
        Position.coach -> PlayerPosition.COACH
        Position.offensive_coordinator -> PlayerPosition.OFFENSIVE_COORDINATOR
        Position.defensive_coordinator -> PlayerPosition.DEFENSIVE_COORDINATOR
        // Unknown
        else -> PlayerPosition.UNKNOWN
    }
}

private fun CardType?.toLocal(): com.theathletic.gamedetail.data.local.CardType {
    return when (this) {
        CardType.yc -> com.theathletic.gamedetail.data.local.CardType.YELLOW
        CardType.y2c -> com.theathletic.gamedetail.data.local.CardType.YELLOW_2ND
        CardType.rc -> com.theathletic.gamedetail.data.local.CardType.RED
        else -> com.theathletic.gamedetail.data.local.CardType.UNKNOWN
    }
}

private fun GoalType?.toLocal(): com.theathletic.gamedetail.data.local.GoalType {
    return when (this) {
        GoalType.g -> com.theathletic.gamedetail.data.local.GoalType.GOAL
        GoalType.og -> com.theathletic.gamedetail.data.local.GoalType.OWN_GOAL
        GoalType.pg -> com.theathletic.gamedetail.data.local.GoalType.PENALTY_GOAL
        else -> com.theathletic.gamedetail.data.local.GoalType.UNKNOWN
    }
}

private fun PenaltyOutcome.toLocal(): com.theathletic.gamedetail.data.local.PenaltyOutcome {
    return when (this) {
        PenaltyOutcome.missed -> com.theathletic.gamedetail.data.local.PenaltyOutcome.MISSED
        PenaltyOutcome.saved -> com.theathletic.gamedetail.data.local.PenaltyOutcome.SAVED
        PenaltyOutcome.scored -> com.theathletic.gamedetail.data.local.PenaltyOutcome.SCORED
        else -> com.theathletic.gamedetail.data.local.PenaltyOutcome.UNKNOWN
    }
}

fun GameStat.toLocalStats(filter: GameStatGroup? = null): GameDetailLocalModel.Statistic? {
    fragments.decimalGameStat?.let { stat ->
        if (filter != null && stat.stat_groups.contains(filter).not()) return null
        return GameDetailLocalModel.DecimalStatistic(
            id = stat.id,
            category = stat.stat_category.toLocal(),
            headerLabel = stat.stat_header_label,
            longHeaderLabel = stat.stat_long_header_label,
            label = stat.stat_label,
            type = stat.stat_type,
            decimalValue = stat.decimal_value,
            lessIsBest = stat.less_is_best ?: false,
            stringValue = stat.string_value,
            isChildStat = stat.parent_stat_type != null,
            referenceOnly = stat.reference_only ?: false
        )
    }
    fragments.integerGameStat?.let { stat ->
        if (filter != null && stat.stat_groups.contains(filter).not()) return null
        return GameDetailLocalModel.IntegerStatistic(
            id = stat.id,
            category = stat.stat_category.toLocal(),
            headerLabel = stat.stat_header_label,
            longHeaderLabel = stat.stat_long_header_label,
            label = stat.stat_label,
            type = stat.stat_type,
            intValue = stat.int_value,
            lessIsBest = stat.less_is_best ?: false,
            isChildStat = stat.parent_stat_type != null,
            referenceOnly = stat.reference_only ?: false
        )
    }
    fragments.percentageGameStat?.let { stat ->
        if (filter != null && stat.stat_groups.contains(filter).not()) return null
        return GameDetailLocalModel.PercentageStatistic(
            id = stat.id,
            category = stat.stat_category.toLocal(),
            headerLabel = stat.stat_header_label,
            longHeaderLabel = stat.stat_long_header_label,
            label = stat.stat_label,
            type = stat.stat_type,
            decimalValue = stat.decimal_value,
            lessIsBest = stat.less_is_best ?: false,
            stringValue = stat.string_value,
            isChildStat = stat.parent_stat_type != null,
            referenceOnly = stat.reference_only ?: false
        )
    }
    fragments.stringGameStat?.let { stat ->
        if (filter != null && stat.stat_groups.contains(filter).not()) return null
        return GameDetailLocalModel.StringStatistic(
            id = stat.id,
            category = stat.stat_category.toLocal(),
            headerLabel = stat.stat_header_label,
            longHeaderLabel = stat.stat_long_header_label,
            label = stat.stat_label,
            type = stat.stat_type,
            value = stat.string_value,
            lessIsBest = false, // Will always be false for this type, no backend field for StringGameStat
            isChildStat = stat.parent_stat_type != null,
            referenceOnly = stat.reference_only ?: false
        )
    }
    fragments.fractionGameStat?.let { stat ->
        if (filter != null && stat.stat_groups.contains(filter).not()) return null
        return GameDetailLocalModel.FractionStatistic(
            id = stat.id,
            category = stat.stat_category.toLocal(),
            headerLabel = stat.stat_header_label,
            label = stat.stat_label,
            longHeaderLabel = stat.stat_long_header_label,
            type = stat.stat_type,
            denominatorValue = stat.denominator_value,
            numeratorValue = stat.numerator_value,
            separator = stat.separator.toLocal(),
            lessIsBest = stat.less_is_best ?: false,
            isChildStat = stat.parent_stat_type != null,
            referenceOnly = stat.reference_only ?: false
        )
    }
    fragments.timeGameStat?.let { stat ->
        if (filter != null && stat.stat_groups.contains(filter).not()) return null
        return GameDetailLocalModel.TimeStatistic(
            id = stat.id,
            category = stat.stat_category.toLocal(),
            headerLabel = stat.stat_header_label,
            longHeaderLabel = stat.stat_long_header_label,
            label = stat.stat_label,
            type = stat.stat_type,
            hours = stat.hours_value ?: 0,
            minutes = stat.minutes_value ?: 0,
            seconds = stat.seconds_value ?: 0,
            stringValue = stat.string_value,
            lessIsBest = stat.less_is_best ?: false,
            isChildStat = stat.parent_stat_type != null,
            referenceOnly = stat.reference_only ?: false
        )
    }
    return null
}

fun Period?.toLocal(): com.theathletic.gamedetail.data.local.Period {
    return when (this) {
        Period.kick_off -> com.theathletic.gamedetail.data.local.Period.KICK_OFF
        Period.first_half -> com.theathletic.gamedetail.data.local.Period.FIRST_HALF
        Period.second_half -> com.theathletic.gamedetail.data.local.Period.SECOND_HALF
        Period.extra_time_first_half -> com.theathletic.gamedetail.data.local.Period.EXTRA_TIME_FIRST_HALF
        Period.extra_time_second_half -> com.theathletic.gamedetail.data.local.Period.EXTRA_TIME_SECOND_HALF
        Period.penalty_shootout -> com.theathletic.gamedetail.data.local.Period.PENALTY_SHOOTOUT
        Period.first_quarter -> com.theathletic.gamedetail.data.local.Period.FIRST_QUARTER
        Period.second_quarter -> com.theathletic.gamedetail.data.local.Period.SECOND_QUARTER
        Period.third_quarter -> com.theathletic.gamedetail.data.local.Period.THIRD_QUARTER
        Period.fourth_quarter -> com.theathletic.gamedetail.data.local.Period.FOURTH_QUARTER
        Period.half_time -> com.theathletic.gamedetail.data.local.Period.HALF_TIME
        Period.full_time -> com.theathletic.gamedetail.data.local.Period.FULL_TIME
        Period.full_time_ot -> com.theathletic.gamedetail.data.local.Period.FULL_TIME_OT
        Period.full_time_ot_2 -> com.theathletic.gamedetail.data.local.Period.FULL_TIME_OT_2
        Period.full_time_ot_3 -> com.theathletic.gamedetail.data.local.Period.FULL_TIME_OT_3
        Period.full_time_ot_4 -> com.theathletic.gamedetail.data.local.Period.FULL_TIME_OT_4
        Period.full_time_ot_5 -> com.theathletic.gamedetail.data.local.Period.FULL_TIME_OT_5
        Period.full_time_ot_6 -> com.theathletic.gamedetail.data.local.Period.FULL_TIME_OT_6
        Period.full_time_ot_7 -> com.theathletic.gamedetail.data.local.Period.FULL_TIME_OT_7
        Period.full_time_ot_8 -> com.theathletic.gamedetail.data.local.Period.FULL_TIME_OT_8
        Period.full_time_ot_9 -> com.theathletic.gamedetail.data.local.Period.FULL_TIME_OT_9
        Period.full_time_ot_10 -> com.theathletic.gamedetail.data.local.Period.FULL_TIME_OT_10
        Period.over_time -> com.theathletic.gamedetail.data.local.Period.OVER_TIME
        Period.over_time_2 -> com.theathletic.gamedetail.data.local.Period.OVER_TIME_2
        Period.over_time_3 -> com.theathletic.gamedetail.data.local.Period.OVER_TIME_3
        Period.over_time_4 -> com.theathletic.gamedetail.data.local.Period.OVER_TIME_4
        Period.over_time_5 -> com.theathletic.gamedetail.data.local.Period.OVER_TIME_5
        Period.over_time_6 -> com.theathletic.gamedetail.data.local.Period.OVER_TIME_6
        Period.over_time_7 -> com.theathletic.gamedetail.data.local.Period.OVER_TIME_7
        Period.over_time_8 -> com.theathletic.gamedetail.data.local.Period.OVER_TIME_8
        Period.over_time_9 -> com.theathletic.gamedetail.data.local.Period.OVER_TIME_9
        Period.over_time_10 -> com.theathletic.gamedetail.data.local.Period.OVER_TIME_10
        Period.pre_game -> com.theathletic.gamedetail.data.local.Period.PRE_GAME
        Period.first_period -> com.theathletic.gamedetail.data.local.Period.FIRST_PERIOD
        Period.second_period -> com.theathletic.gamedetail.data.local.Period.SECOND_PERIOD
        Period.third_period -> com.theathletic.gamedetail.data.local.Period.THIRD_PERIOD
        Period.shootout -> com.theathletic.gamedetail.data.local.Period.SHOOTOUT
        Period.full_time_so -> com.theathletic.gamedetail.data.local.Period.FULL_TIME_SO
        else -> com.theathletic.gamedetail.data.local.Period.UNKNOWN
    }
}

private fun GameStatCategory?.toLocal(): StatisticCategory {
    return when (this) {
        GameStatCategory.advanced -> StatisticCategory.ADVANCED
        GameStatCategory.batting -> StatisticCategory.BATTING
        GameStatCategory.defense -> StatisticCategory.DEFENSE
        GameStatCategory.efficiency_fourthdown -> StatisticCategory.EFFICIENCY_FOURTH_DOWN
        GameStatCategory.efficiency_goaltogo -> StatisticCategory.EFFICIENCY_GOAL_TO_GO
        GameStatCategory.efficiency_redzone -> StatisticCategory.EFFICIENCY_RED_ZONE
        GameStatCategory.efficiency_thirddown -> StatisticCategory.EFFICIENCY_THIRD_DOWN
        GameStatCategory.extra_points_conversions -> StatisticCategory.EXTRA_POINTS_CONVERSIONS
        GameStatCategory.extra_points_kicks -> StatisticCategory.EXTRA_POINTS_KICKS
        GameStatCategory.field_goals -> StatisticCategory.FIELD_GOALS
        GameStatCategory.first_downs -> StatisticCategory.FIRST_DOWNS
        GameStatCategory.fumbles -> StatisticCategory.FUMBLES
        GameStatCategory.int_returns -> StatisticCategory.INT_RETURNS
        GameStatCategory.interceptions -> StatisticCategory.INTERCEPTIONS
        GameStatCategory.kick_returns -> StatisticCategory.KICK_RETURNS
        GameStatCategory.kicking -> StatisticCategory.KICKING
        GameStatCategory.kickoffs -> StatisticCategory.KICKOFFS
        GameStatCategory.misc_returns -> StatisticCategory.MISC_RETURNS
        GameStatCategory.passing -> StatisticCategory.PASSING
        GameStatCategory.penalties -> StatisticCategory.PENALTIES
        GameStatCategory.pitching -> StatisticCategory.PITCHING
        GameStatCategory.punt_returns -> StatisticCategory.PUNT_RETURNS
        GameStatCategory.punts -> StatisticCategory.PUNTS
        GameStatCategory.receiving -> StatisticCategory.RECEIVING
        GameStatCategory.rushing -> StatisticCategory.RUSHING
        GameStatCategory.standard -> StatisticCategory.STANDARD
        GameStatCategory.summary -> StatisticCategory.SUMMARY
        GameStatCategory.touchdowns -> StatisticCategory.TOUCHDOWNS
        else -> StatisticCategory.UNKNOWN
    }
}

private fun mapGameStats(
    awayTeamStats: List<GameDetailLocalModel.Statistic>?,
    homeTeamStats: List<GameDetailLocalModel.Statistic>?,
    homeTeamFirst: Boolean
): List<Pair<GameDetailLocalModel.Statistic, GameDetailLocalModel.Statistic>> {
    val teamPairs = mutableListOf<Pair<GameDetailLocalModel.Statistic, GameDetailLocalModel.Statistic>>()
    homeTeamStats?.forEach { homeStat ->
        val awayStat = awayTeamStats?.find { it.type == homeStat.type }
        safeLet(homeStat, awayStat) { safeHome, safeAway ->
            teamPairs.add(
                Pair(
                    if (homeTeamFirst) safeHome else safeAway,
                    if (homeTeamFirst) safeAway else safeHome
                )
            )
        }
    }
    return teamPairs
}

private fun mapSeasonStats(
    awayTeamStats: List<GameDetailLocalModel.RankedStat>?,
    homeTeamStats: List<GameDetailLocalModel.RankedStat>?,
    homeTeamFirst: Boolean
): List<Pair<GameDetailLocalModel.RankedStat, GameDetailLocalModel.RankedStat>> {
    val teamPairs = mutableListOf<Pair<GameDetailLocalModel.RankedStat, GameDetailLocalModel.RankedStat>>()
    if (awayTeamStats == null && homeTeamStats == null) return teamPairs

    val statsOne = homeTeamStats ?: awayTeamStats
    val statsTwo = if (homeTeamStats == null) null else awayTeamStats
    val switched = homeTeamStats == null
    return processSeasonStats(statsOne, statsTwo, switched, homeTeamFirst)
}

private fun processSeasonStats(
    statsOne: List<GameDetailLocalModel.RankedStat>?,
    statsTwo: List<GameDetailLocalModel.RankedStat>?,
    switched: Boolean,
    homeTeamFirst: Boolean
): List<Pair<GameDetailLocalModel.RankedStat, GameDetailLocalModel.RankedStat>> {
    val teamPairs = mutableListOf<Pair<GameDetailLocalModel.RankedStat, GameDetailLocalModel.RankedStat>>()

    statsOne?.forEach { statOne ->
        val statTwo = statsTwo?.find {
            it.statCategory == statOne.statCategory && it.statType == statOne.statType
        } ?: statOne.copy(
            statValue = "-",
            rank = 0
        )
        safeLet(statOne, statTwo) { safeOne, safeTwo ->
            val first = if (homeTeamFirst) safeOne else safeTwo
            val second = if (homeTeamFirst) safeTwo else safeOne
            teamPairs.add(
                Pair(
                    if (switched) second else first,
                    if (switched) first else second
                )
            )
        }
    }
    return teamPairs
}

fun com.theathletic.type.Sport.toLocal(): Sport {
    return when (this) {
        com.theathletic.type.Sport.american_football -> Sport.FOOTBALL
        com.theathletic.type.Sport.baseball -> Sport.BASEBALL
        com.theathletic.type.Sport.basketball -> Sport.BASKETBALL
        com.theathletic.type.Sport.boxing -> Sport.BOXING
        com.theathletic.type.Sport.football -> Sport.FOOTBALL
        com.theathletic.type.Sport.golf -> Sport.GOLF
        com.theathletic.type.Sport.hockey -> Sport.HOCKEY
        com.theathletic.type.Sport.mma -> Sport.MMA
        com.theathletic.type.Sport.soccer -> Sport.SOCCER
        else -> Sport.UNKNOWN
    }
}

fun PossessionFragment.toLocal() = GameDetailLocalModel.Possession(
    down = down,
    locationTeam = location_team?.fragments?.teamLite?.toLocalModel(),
    locationYardLine = location_yardline,
    team = team?.fragments?.teamLite?.toLocalModel(),
    yards = yfd,
    goalToGo = goal_to_go ?: false,
    driveInfo = drive_info?.toLocal()
)

private fun PossessionFragment.Drive_info.toLocal() = GameDetailLocalModel.DriveInfo(
    duration = duration,
    playCount = play_count,
    yards = yards
)

private fun GameOddsMarketFragment.toGameOdds(): GameDetailLocalModel.GameOdds? {
    fragments.gameOddsMoneylineMarketFragment?.let {
        return GameDetailLocalModel.GameOddsMoneyLine(
            id = it.id,
            balancedLine = it.balanced_line ?: false,
            bettingOpen = it.betting_open ?: false,
            betPeriod = it.bet_period,
            line = it.line,
            price = it.price.fragments.gameOddsPriceFragment.toLocal(),
            team = it.team?.fragments?.team?.toLocalModel()
        )
    }
    fragments.gameOddsSpreadMarketFragment?.let {
        return GameDetailLocalModel.GameOddsSpread(
            id = it.id,
            balancedLine = it.balanced_line ?: false,
            bettingOpen = it.betting_open ?: false,
            betPeriod = it.bet_period,
            line = it.line,
            price = it.price.fragments.gameOddsPriceFragment.toLocal(),
            team = it.team?.fragments?.team?.toLocalModel()
        )
    }
    fragments.gameOddsTotalsMarketFragment?.let {
        return GameDetailLocalModel.GameOddsTotals(
            id = it.id,
            balancedLine = it.balanced_line ?: false,
            bettingOpen = it.betting_open ?: false,
            betPeriod = it.bet_period,
            line = it.line,
            price = it.price.fragments.gameOddsPriceFragment.toLocal(),
            direction = it.direction
        )
    }
    return null
}

fun GameOddsPriceFragment.toLocal() = GameDetailLocalModel.GameOddsPrice(
    oddsFraction = fraction_odds,
    oddsDecimal = decimal_odds,
    oddsUs = us_odds
)

private fun ScoringPlayFragment.toLocal() = GameDetailLocalModel.ScoringPlay(
    id = id,
    awayTeamScore = away_score,
    homeTeamScore = home_score,
    clock = clock,
    description = description,
    headerLabel = header,
    occurredAt = Datetime(occurred_at),
    period = period_id.toLocal(),
    plays = plays,
    yards = yards,
    team = team.fragments.teamLite.toLocalModel(),
    scoreType = score_type.toLocal()
)

fun HockeyPlaysFragment.toLocal(): GameDetailLocalModel.HockeyPlay? {
    fragments.hockeyTeamPlayFragment?.let { play ->
        return GameDetailLocalModel.HockeyTeamPlay(
            id = play.id,
            awayTeamScore = play.away_score,
            homeTeamScore = play.home_score,
            clock = play.game_time.orEmpty(),
            description = play.description,
            headerLabel = play.header,
            occurredAt = Datetime(play.occurred_at),
            period = play.period_id.toLocal(),
            team = play.team.fragments.teamLite.toLocalModel(),
            strength = play.strength.toLocal(),
            type = play.type.toLocal()
        )
    }
    fragments.hockeyShootoutPlayFragment?.let { play ->
        return GameDetailLocalModel.HockeyShootoutPlay(
            id = play.id,
            awayTeamScore = play.away_score,
            homeTeamScore = play.home_score,
            description = play.description,
            headerLabel = play.header,
            occurredAt = Datetime(play.occurred_at),
            period = play.period_id.toLocal(),
            team = play.team.fragments.teamLite.toLocalModel(),
            awayShootoutGoals = play.away_shootout_goals,
            awayShootoutShots = play.away_shootout_shots,
            homeShootoutGoals = play.home_shootout_goals,
            homeShootoutShots = play.home_shootout_shots,
            type = play.type.toLocal(),
            playerHeadshots = play.shooter?.headshots?.toShootoutHeadshots(),
            playerHeadshotUri = play.shooter?.headshots?.firstOrNull()?.fragments?.headshot?.uri
        )
    }
    fragments.hockeyPlayFragment?.let { play ->
        return GameDetailLocalModel.HockeyStandardPlay(
            id = play.id,
            description = play.description,
            headerLabel = play.header,
            occurredAt = Datetime(play.occurred_at),
            clock = play.clock,
            period = play.period_id.toLocal(),
            type = play.type.toLocal(),
            team = null,
            awayTeamScore = play.away_score,
            homeTeamScore = play.home_score,
            awayShotsAtGoal = play.away_sog,
            homeShotsAtGoal = play.home_sog
        )
    }
    return null
}

private fun ScoreType.toLocal(): AmericanFootballScoreType {
    return when (this) {
        ScoreType.field_goal -> AmericanFootballScoreType.FIELD_GOAL
        ScoreType.safety -> AmericanFootballScoreType.SAFETY
        ScoreType.touchdown -> AmericanFootballScoreType.TOUCHDOWN
        else -> AmericanFootballScoreType.UNKNOWN
    }
}

private fun BaseballTeamPlayFragment.toScoringPlay() = GameDetailLocalModel.BaseballTeamPlay(
    id = id,
    awayTeamScore = away_score,
    homeTeamScore = home_score,
    description = description,
    headerLabel = header,
    occurredAt = Datetime(occurred_at),
    team = team.fragments.teamLite.toLocalModel(),
    inning = inning,
    inningHalf = inning_half?.toLocalModel(),
    plays = emptyList()
)

private fun BaseballInningPlayFragment.toInningsPlay(): GameDetailLocalModel.BaseballInningPlay? {
    fragments.baseballLineupChangePlayFragment?.let { play ->
        return GameDetailLocalModel.BaseballInningPlay(
            id = play.id,
            description = play.description,
            occurredAt = Datetime(play.occurred_at),
            isLineUpChange = true
        )
    }
    fragments.baseballPlayFragment?.let { play ->
        return GameDetailLocalModel.BaseballInningPlay(
            id = play.id,
            description = play.description,
            occurredAt = Datetime(play.occurred_at),
            isLineUpChange = false
        )
    }
    fragments.baseballTeamPlayFragment?.let { play ->
        return GameDetailLocalModel.BaseballInningPlay(
            id = play.id,
            description = play.description,
            occurredAt = Datetime(play.occurred_at),
            isLineUpChange = false
        )
    }
    return null
}

fun BasketballPlayFragment.toLocal() = GameDetailLocalModel.BasketballPlay(
    id = id,
    awayTeamScore = away_score,
    homeTeamScore = home_score,
    clock = clock,
    description = description,
    headerLabel = header,
    occurredAt = Datetime(occurred_at),
    period = period_id.toLocal(),
    team = team?.fragments?.teamLite?.toLocalModel(),
    playType = type.toLocal()
)

private fun BaseballOutcomeFragment.toLocal() = GameDetailLocalModel.BaseballOutcome(
    id = id,
    inning = inning ?: 0,
    inningHalf = inning_half?.toLocalModel(),
    balls = balls ?: 0,
    strikes = strikes ?: 0,
    outs = outs ?: 0,
    batter = batter?.fragments?.baseballBatterFragment?.toLocal(),
    pitcher = pitcher?.fragments?.baseballPitcherFragment?.toLocal(),
    nextBatter = next_batter?.fragments?.baseballBatterFragment?.toLocal(),
    loadedBases = runners.map { it.ending_base }
)

private fun BaseballPitchingFragment.toLocal() = GameDetailLocalModel.BaseballPitching(
    winPitcher = win?.fragments?.baseballPitcherFragment?.toLocal(),
    lossPitcher = loss?.fragments?.baseballPitcherFragment?.toLocal(),
    savePitcher = save?.fragments?.baseballPitcherFragment?.toLocal()
)

private fun WeatherFragment?.toLocalModel(): GameDetailLocalModel.Weather? {
    return this?.let { weather ->
        GameDetailLocalModel.Weather(
            id = weather.id,
            outlook = weather.outlook,
            tempCelsius = weather.temp_celsius,
            tempFahrenheit = weather.temp_fahrenheit
        )
    }
}

fun PeriodScoreFragment.toLocalModel() = GameDetailLocalModel.PeriodScore(
    id = id,
    period = period_id.toLocal(),
    scoreDisplay = score_str
)

private fun BaseballPitcherFragment.toLocal() = GameDetailLocalModel.BaseballPlayer(
    id = id,
    player = player.fragments.teamMemberBaseball.toLocalModel(),
    gameStats = game_stats.mapNotNull { it.fragments.gameStat.toLocalStats() },
    seasonAvg = null
)

private fun BaseballBatterFragment.toLocal() = GameDetailLocalModel.BaseballPlayer(
    id = id,
    player = player.fragments.teamMemberBaseball.toLocalModel(),
    gameStats = game_stats.mapNotNull { it.fragments.gameStat.toLocalStats() },
    seasonAvg = season_avg?.fragments?.gameStat?.toLocalStats()
)

fun InningScoreFragment.toLocal() = GameDetailLocalModel.InningScore(
    id = id,
    inning = inning,
    runs = runs ?: 0,
    hits = hits ?: 0,
    errors = errors ?: 0
)

private fun BasketballPlayType.toLocal(): com.theathletic.gamedetail.data.local.BasketballPlayType {
    return when (this) {
        BasketballPlayType.challengereview -> com.theathletic.gamedetail.data.local.BasketballPlayType.CHALLENGE_REVIEW
        BasketballPlayType.challengetimeout -> com.theathletic.gamedetail.data.local.BasketballPlayType.CHALLENGE_TIMEOUT
        BasketballPlayType.clearpathfoul -> com.theathletic.gamedetail.data.local.BasketballPlayType.CLEAR_PATH_FOUL
        BasketballPlayType.deadball -> com.theathletic.gamedetail.data.local.BasketballPlayType.DEAD_BALL
        BasketballPlayType.defaultviolation -> com.theathletic.gamedetail.data.local.BasketballPlayType.DEFAULT_VIOLATION
        BasketballPlayType.defensivegoaltending -> com.theathletic.gamedetail.data.local.BasketballPlayType.DEFENSIVE_GOAL_TENDING
        BasketballPlayType.defensivethreeseconds -> com.theathletic.gamedetail.data.local.BasketballPlayType.DEFENSIVE_THREE_SECONDS
        BasketballPlayType.delay -> com.theathletic.gamedetail.data.local.BasketballPlayType.DELAY
        BasketballPlayType.doublelane -> com.theathletic.gamedetail.data.local.BasketballPlayType.DOUBLE_LANE
        BasketballPlayType.ejection -> com.theathletic.gamedetail.data.local.BasketballPlayType.EJECTION
        BasketballPlayType.endperiod -> com.theathletic.gamedetail.data.local.BasketballPlayType.END_PERIOD
        BasketballPlayType.flagrantone -> com.theathletic.gamedetail.data.local.BasketballPlayType.FLAG_RANT_ONE
        BasketballPlayType.flagranttwo -> com.theathletic.gamedetail.data.local.BasketballPlayType.FLAG_RANT_TWO
        BasketballPlayType.freethrowmade -> com.theathletic.gamedetail.data.local.BasketballPlayType.FREE_THROW_MADE
        BasketballPlayType.freethrowmiss -> com.theathletic.gamedetail.data.local.BasketballPlayType.FREE_THROW_MISS
        BasketballPlayType.jumpball -> com.theathletic.gamedetail.data.local.BasketballPlayType.JUMP_BALL
        BasketballPlayType.jumpballviolation -> com.theathletic.gamedetail.data.local.BasketballPlayType.JUMP_BALL_VIOLATION
        BasketballPlayType.kickball -> com.theathletic.gamedetail.data.local.BasketballPlayType.KICK_BALL
        BasketballPlayType.lane -> com.theathletic.gamedetail.data.local.BasketballPlayType.LANE
        BasketballPlayType.lineupchange -> com.theathletic.gamedetail.data.local.BasketballPlayType.LINE_UP_CHANGE
        BasketballPlayType.minortechnicalfoul -> com.theathletic.gamedetail.data.local.BasketballPlayType.MINOR_TECHNICAL_FOUL
        BasketballPlayType.offensivefoul -> com.theathletic.gamedetail.data.local.BasketballPlayType.OFFENSIVE_FOUL
        BasketballPlayType.officialtimeout -> com.theathletic.gamedetail.data.local.BasketballPlayType.OFFICIAL_TIMEOUT
        BasketballPlayType.openinbound -> com.theathletic.gamedetail.data.local.BasketballPlayType.OPEN_INBOUND
        BasketballPlayType.opentip -> com.theathletic.gamedetail.data.local.BasketballPlayType.OPEN_TIP
        BasketballPlayType.personalfoul -> com.theathletic.gamedetail.data.local.BasketballPlayType.PERSONAL_FOUL
        BasketballPlayType.possession -> com.theathletic.gamedetail.data.local.BasketballPlayType.POSSESSION
        BasketballPlayType.requestreview -> com.theathletic.gamedetail.data.local.BasketballPlayType.REQUEST_REVIEW
        BasketballPlayType.review -> com.theathletic.gamedetail.data.local.BasketballPlayType.REVIEW
        BasketballPlayType.rebound -> com.theathletic.gamedetail.data.local.BasketballPlayType.REBOUND
        BasketballPlayType.shootingfoul -> com.theathletic.gamedetail.data.local.BasketballPlayType.SHOOTING_FOUL
        BasketballPlayType.stoppage -> com.theathletic.gamedetail.data.local.BasketballPlayType.STOPPAGE
        BasketballPlayType.teamtimeout -> com.theathletic.gamedetail.data.local.BasketballPlayType.TEAM_TIMEOUT
        BasketballPlayType.technicalfoul -> com.theathletic.gamedetail.data.local.BasketballPlayType.TECHNICAL_FOUL
        BasketballPlayType.threepointmade -> com.theathletic.gamedetail.data.local.BasketballPlayType.THREE_POINT_MADE
        BasketballPlayType.threepointmiss -> com.theathletic.gamedetail.data.local.BasketballPlayType.THREE_POINT_MISS
        BasketballPlayType.turnover -> com.theathletic.gamedetail.data.local.BasketballPlayType.TURNOVER
        BasketballPlayType.tvtimeout -> com.theathletic.gamedetail.data.local.BasketballPlayType.TV_TIMEOUT
        BasketballPlayType.twopointmade -> com.theathletic.gamedetail.data.local.BasketballPlayType.TWO_POINT_MADE
        BasketballPlayType.twopointmiss -> com.theathletic.gamedetail.data.local.BasketballPlayType.TWO_POINT_MISS
        else -> com.theathletic.gamedetail.data.local.BasketballPlayType.UNKNOWN
    }
}

private fun FractionSeparator?.toLocal(): com.theathletic.gamedetail.data.local.FractionSeparator {
    return when (this) {
        FractionSeparator.dash -> com.theathletic.gamedetail.data.local.FractionSeparator.DASH
        else -> com.theathletic.gamedetail.data.local.FractionSeparator.SLASH // default separator
    }
}

private fun HockeyStrength.toLocal(): com.theathletic.gamedetail.data.local.HockeyStrength {
    return when (this) {
        HockeyStrength.even -> com.theathletic.gamedetail.data.local.HockeyStrength.EVEN
        HockeyStrength.powerplay -> com.theathletic.gamedetail.data.local.HockeyStrength.POWERPLAY
        HockeyStrength.shorthanded -> com.theathletic.gamedetail.data.local.HockeyStrength.SHORTHANDED
        else -> com.theathletic.gamedetail.data.local.HockeyStrength.UNKNOWN
    }
}

private fun SoccerOfficialType.toLocal(): com.theathletic.gamedetail.data.local.SoccerOfficialType {
    return when (this) {
        SoccerOfficialType.assistant_referee -> com.theathletic.gamedetail.data.local.SoccerOfficialType.ASSISTANT_REFEREE
        SoccerOfficialType.fourth_official -> com.theathletic.gamedetail.data.local.SoccerOfficialType.FOURTH_OFFICIAL
        SoccerOfficialType.referee -> com.theathletic.gamedetail.data.local.SoccerOfficialType.REFEREE
        SoccerOfficialType.assistant_var -> com.theathletic.gamedetail.data.local.SoccerOfficialType.ASSISTANT_VAR
        SoccerOfficialType.`var` -> com.theathletic.gamedetail.data.local.SoccerOfficialType.VAR
        else -> com.theathletic.gamedetail.data.local.SoccerOfficialType.UNKNOWN
    }
}

private fun HockeyPlayType.toLocal(): com.theathletic.gamedetail.data.local.HockeyPlayType {
    return when (this) {
        HockeyPlayType.awardedgoal -> com.theathletic.gamedetail.data.local.HockeyPlayType.AWARDED_GOAL
        HockeyPlayType.challenge -> com.theathletic.gamedetail.data.local.HockeyPlayType.CHALLENGE
        HockeyPlayType.emptynetgoal -> com.theathletic.gamedetail.data.local.HockeyPlayType.EMPTY_NET_GOAL
        HockeyPlayType.endperiod -> com.theathletic.gamedetail.data.local.HockeyPlayType.END_PERIOD
        HockeyPlayType.endshootoutperiod -> com.theathletic.gamedetail.data.local.HockeyPlayType.END_SHOOTOUT_PERIOD
        HockeyPlayType.evenstrength -> com.theathletic.gamedetail.data.local.HockeyPlayType.EVEN_STRENGTH
        HockeyPlayType.faceoff -> com.theathletic.gamedetail.data.local.HockeyPlayType.FACE_OFF
        HockeyPlayType.gamesetup -> com.theathletic.gamedetail.data.local.HockeyPlayType.GAME_SETUP
        HockeyPlayType.giveaway -> com.theathletic.gamedetail.data.local.HockeyPlayType.GIVE_AWAY
        HockeyPlayType.goal -> com.theathletic.gamedetail.data.local.HockeyPlayType.GOAL
        HockeyPlayType.goaliechange -> com.theathletic.gamedetail.data.local.HockeyPlayType.GOALIE_CHANGE
        HockeyPlayType.hit -> com.theathletic.gamedetail.data.local.HockeyPlayType.HIT
        HockeyPlayType.owngoal -> com.theathletic.gamedetail.data.local.HockeyPlayType.OWN_GOAL
        HockeyPlayType.penalty -> com.theathletic.gamedetail.data.local.HockeyPlayType.PENALTY
        HockeyPlayType.penaltygoal -> com.theathletic.gamedetail.data.local.HockeyPlayType.PENALTY_GOAL
        HockeyPlayType.penaltyshotmissed -> com.theathletic.gamedetail.data.local.HockeyPlayType.PENALTY_SHOT_MISSED
        HockeyPlayType.penaltyshotsaved -> com.theathletic.gamedetail.data.local.HockeyPlayType.PENALTY_SHOT_SAVED
        HockeyPlayType.powerplay -> com.theathletic.gamedetail.data.local.HockeyPlayType.POWERPLAY
        HockeyPlayType.shootoutgoal -> com.theathletic.gamedetail.data.local.HockeyPlayType.SHOOTOUT_GOAL
        HockeyPlayType.shootoutshotmissed -> com.theathletic.gamedetail.data.local.HockeyPlayType.SHOOTOUT_SHOT_MISSED
        HockeyPlayType.shootoutshotsaved -> com.theathletic.gamedetail.data.local.HockeyPlayType.SHOOTOUT_SHOT_SAVED
        HockeyPlayType.shotmissed -> com.theathletic.gamedetail.data.local.HockeyPlayType.SHOT_MISSED
        HockeyPlayType.shotsaved -> com.theathletic.gamedetail.data.local.HockeyPlayType.SHOT_SAVED
        HockeyPlayType.startshootoutperiod -> com.theathletic.gamedetail.data.local.HockeyPlayType.START_SHOOTOUT_PERIOD
        HockeyPlayType.stoppage -> com.theathletic.gamedetail.data.local.HockeyPlayType.STOPPAGE
        HockeyPlayType.substitution -> com.theathletic.gamedetail.data.local.HockeyPlayType.SUBSTITUTION
        HockeyPlayType.substitutions -> com.theathletic.gamedetail.data.local.HockeyPlayType.SUBSTITUTIONS
        HockeyPlayType.takeaway -> com.theathletic.gamedetail.data.local.HockeyPlayType.TAKEAWAY
        HockeyPlayType.teamtimeout -> com.theathletic.gamedetail.data.local.HockeyPlayType.TEAM_TIMEOUT
        HockeyPlayType.tvtimeout -> com.theathletic.gamedetail.data.local.HockeyPlayType.TV_TIMEOUT
        else -> com.theathletic.gamedetail.data.local.HockeyPlayType.UNKNOWN
    }
}

fun InningHalf.toLocalModel(): com.theathletic.gamedetail.data.local.InningHalf {
    return when (this) {
        InningHalf.bottom -> com.theathletic.gamedetail.data.local.InningHalf.BOTTOM
        InningHalf.middle -> com.theathletic.gamedetail.data.local.InningHalf.MIDDLE
        InningHalf.over -> com.theathletic.gamedetail.data.local.InningHalf.OVER
        InningHalf.top -> com.theathletic.gamedetail.data.local.InningHalf.TOP
        else -> com.theathletic.gamedetail.data.local.InningHalf.UNKNOWN
    }
}

private fun PlayerHand?.toLocal(): Handedness {
    return when (this) {
        PlayerHand.right -> Handedness.RIGHT
        PlayerHand.left -> Handedness.LEFT
        else -> Handedness.UNKNOWN
    }
}

private fun GameCoverageDataType.toLocal(): CoverageDataType {
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

private fun TeamLeader.toLocal(team: GameDetailLocalModel.Team?) = GameDetailLocalModel.StatLeader(
    id = player.id,
    playerName = player.full_name,
    jerseyNumber = player.role?.jersey_number.toString(),
    playerPosition = player.role?.position.toLocal(),
    headshots = player.headshots.toStatLeaderHeadshots(),
    statLabel = stats_label,
    stats = stats.mapNotNull { it.fragments.gameStat.toLocalStats() },
    teamAlias = team?.alias,
    teamLogos = team?.logos,
    primaryColor = team?.primaryColor
)

private fun TopPerformer.toLocal(team: GameDetailLocalModel.Team?) = GameDetailLocalModel.StatLeader(
    id = player.id,
    playerName = player.display_name,
    jerseyNumber = player.jersey_number,
    playerPosition = player.position.toLocal(),
    headshots = player.player.headshots.toTopPerformerHeadshots(),
    statLabel = stats_label,
    stats = stats.mapNotNull { it.fragments.gameStat.toLocalStats() },
    teamAlias = team?.alias,
    teamLogos = team?.logos,
    primaryColor = team?.primaryColor
)

private fun Team.Injury.toLocal() = GameDetailLocalModel.Injury(
    injury = fragments.injury.injury,
    comment = fragments.injury.comment,
    playerName = fragments.injury.player.display_name,
    playerPosition = fragments.injury.player.position.toLocal(),
    headshots = fragments.injury.player.headshots.toInjuryHeadshots(),
    status = fragments.injury.status.toLocal()
)

private fun InjuryStatus.toLocal(): com.theathletic.gamedetail.data.local.InjuryStatus {
    return when (this) {
        InjuryStatus.d7 -> com.theathletic.gamedetail.data.local.InjuryStatus.D7
        InjuryStatus.d10 -> com.theathletic.gamedetail.data.local.InjuryStatus.D10
        InjuryStatus.d15 -> com.theathletic.gamedetail.data.local.InjuryStatus.D15
        InjuryStatus.d60 -> com.theathletic.gamedetail.data.local.InjuryStatus.D60
        InjuryStatus.day -> com.theathletic.gamedetail.data.local.InjuryStatus.DAY
        InjuryStatus.day_to_day -> com.theathletic.gamedetail.data.local.InjuryStatus.DAY_TO_DAY
        InjuryStatus.doubtful -> com.theathletic.gamedetail.data.local.InjuryStatus.DOUBTFUL
        InjuryStatus.out -> com.theathletic.gamedetail.data.local.InjuryStatus.OUT
        InjuryStatus.out_for_season -> com.theathletic.gamedetail.data.local.InjuryStatus.OUT_FOR_SEASON
        InjuryStatus.out_indefinitely -> com.theathletic.gamedetail.data.local.InjuryStatus.OUT_INDEFINITELY
        InjuryStatus.questionable -> com.theathletic.gamedetail.data.local.InjuryStatus.QUESTIONABLE
        else -> com.theathletic.gamedetail.data.local.InjuryStatus.UNKNOWN
    }
}

private fun List<Team.Logo>.toLogos() = map {
    SizedImage(
        width = it.fragments.logoFragment.width,
        height = it.fragments.logoFragment.height,
        uri = it.fragments.logoFragment.uri
    )
}.sortedBy { it.height }

fun List<TeamLite.Logo>.toTeamLiteLogos() = map {
    SizedImage(
        width = it.fragments.logoFragment.width,
        height = it.fragments.logoFragment.height,
        uri = it.fragments.logoFragment.uri
    )
}.sortedBy { it.height }

private fun List<TeamMemberBaseball.Headshot>.toBaseballHeadshot() =
    map { it.fragments.headshot.toHeadshot() }.sortedBy { it.height }

private fun List<StartingPitcherFragment.Headshot>.toStartingPitcherHeadshots() =
    map { it.fragments.headshot.toHeadshot() }.sortedBy { it.height }

private fun List<TeamLeader.Headshot>.toStatLeaderHeadshots() =
    map { it.fragments.headshot.toHeadshot() }.sortedBy { it.height }

private fun List<TopPerformer.Headshot>.toTopPerformerHeadshots() =
    map { it.fragments.headshot.toHeadshot() }.sortedBy { it.height }

private fun List<Injury.Headshot>.toInjuryHeadshots() =
    map { it.fragments.headshot.toHeadshot() }.sortedBy { it.height }

private fun List<HockeyShootoutPlayFragment.Headshot>.toShootoutHeadshots() =
    map { it.fragments.headshot.toHeadshot() }.sortedBy { it.height }

private fun BaseballGameSummary.Outcome.toLocalModel() =
    GameSummaryLocalModel.SportExtras.Baseball.BaseballOutcome(
        balls = balls,
        inning = inning,
        inningHalf = inning_half?.toLocalModel(),
        outs = outs,
        strikes = strikes,
        occupiedBases = runners.map { it.ending_base }
    )

fun Headshot.toHeadshot() = SizedImage(
    width = width,
    height = height,
    uri = uri
)

fun BaseballPitchOutcome?.toLocal(): com.theathletic.gamedetail.data.local.BaseballPitchOutcome {
    return when (this) {
        BaseballPitchOutcome.ball -> com.theathletic.gamedetail.data.local.BaseballPitchOutcome.BALL
        BaseballPitchOutcome.dead_ball -> com.theathletic.gamedetail.data.local.BaseballPitchOutcome.DEAD_BALL
        BaseballPitchOutcome.hit -> com.theathletic.gamedetail.data.local.BaseballPitchOutcome.HIT
        BaseballPitchOutcome.strike -> com.theathletic.gamedetail.data.local.BaseballPitchOutcome.STRIKE
        else -> com.theathletic.gamedetail.data.local.BaseballPitchOutcome.UNKNOWN
    }
}

fun AmericanFootballPlayType?.toLocal(): com.theathletic.gamedetail.data.local.AmericanFootballPlayType {
    return when (this) {
        AmericanFootballPlayType.conversion ->
            com.theathletic.gamedetail.data.local.AmericanFootballPlayType.CONVERSION
        AmericanFootballPlayType.extra_point ->
            com.theathletic.gamedetail.data.local.AmericanFootballPlayType.EXTRA_POINT
        AmericanFootballPlayType.field_goal ->
            com.theathletic.gamedetail.data.local.AmericanFootballPlayType.FIELD_GOAL
        AmericanFootballPlayType.game_over ->
            com.theathletic.gamedetail.data.local.AmericanFootballPlayType.GAME_OVER
        AmericanFootballPlayType.kickoff ->
            com.theathletic.gamedetail.data.local.AmericanFootballPlayType.KICK_OFF
        AmericanFootballPlayType.pass ->
            com.theathletic.gamedetail.data.local.AmericanFootballPlayType.PASS
        AmericanFootballPlayType.penalty ->
            com.theathletic.gamedetail.data.local.AmericanFootballPlayType.PENALTY
        AmericanFootballPlayType.period_end ->
            com.theathletic.gamedetail.data.local.AmericanFootballPlayType.PERIOD_END
        AmericanFootballPlayType.punt ->
            com.theathletic.gamedetail.data.local.AmericanFootballPlayType.PUNT
        AmericanFootballPlayType.rush ->
            com.theathletic.gamedetail.data.local.AmericanFootballPlayType.RUSH
        AmericanFootballPlayType.timeout ->
            com.theathletic.gamedetail.data.local.AmericanFootballPlayType.TIMEOUT
        AmericanFootballPlayType.tv_timeout ->
            com.theathletic.gamedetail.data.local.AmericanFootballPlayType.TV_TIMEOUT
        AmericanFootballPlayType.two_minute_warning ->
            com.theathletic.gamedetail.data.local.AmericanFootballPlayType.TWO_MINUTE_WARNING
        else -> com.theathletic.gamedetail.data.local.AmericanFootballPlayType.UNKNOWN
    }
}

fun TeamLite.toLocalModel() = GameDetailLocalModel.Team(
    id = id,
    alias = alias.orEmpty(),
    name = name.orEmpty(),
    logos = logos.map {
        SizedImage(
            width = it.fragments.logoFragment.width,
            height = it.fragments.logoFragment.height,
            uri = it.fragments.logoFragment.uri
        )
    }.sortedBy { it.height },
    displayName = display_name.orEmpty(),
    primaryColor = color_primary,
    accentColor = color_accent,
    currentRanking = null
)

fun GradeStatus?.toLocalModel() =
    when (this) {
        null -> com.theathletic.gamedetail.data.local.GradeStatus.UNSUPPORTED
        GradeStatus.disabled -> com.theathletic.gamedetail.data.local.GradeStatus.DISABLED
        GradeStatus.enabled -> com.theathletic.gamedetail.data.local.GradeStatus.ENABLED
        GradeStatus.locked -> com.theathletic.gamedetail.data.local.GradeStatus.LOCKED
        else -> com.theathletic.gamedetail.data.local.GradeStatus.UNKNOWN
    }

fun ScoresTopComment.toLocalModel() = GameDetailLocalModel.TopComment(
    id = id,
    authorName = author_name,
    authorGameFlairs = author_game_flairs.filterNotNull().map { it.toLocalModel() },
    authorUserLevel = author_user_level,
    avatarUrl = avatar_url,
    comment = comment,
    commentMetadata = comment_metadata,
    commentedAt = commented_at,
    likesCount = likes_count,
    parentId = parent_id,
    permalink = comment_permalink.orEmpty()
)

fun ScoresTopComment.Author_game_flair.toLocalModel() = GameDetailLocalModel.AuthorGameFlair(
    name,
    icon_contrast_color
)