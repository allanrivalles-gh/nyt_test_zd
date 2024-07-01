package com.theathletic.gamedetail.boxscore.ui

import androidx.compose.runtime.Composable
import com.theathletic.boxscore.data.local.BasicHeader
import com.theathletic.boxscore.data.local.BoxScore
import com.theathletic.boxscore.data.local.BoxScoreModules
import com.theathletic.boxscore.data.local.Items
import com.theathletic.boxscore.data.local.LatestNewsModule
import com.theathletic.boxscore.data.local.ModuleHeader
import com.theathletic.boxscore.data.local.PodcastEpisode
import com.theathletic.boxscore.data.local.Section
import com.theathletic.boxscore.data.local.SectionType
import com.theathletic.datetime.Datetime
import com.theathletic.entity.main.League
import com.theathletic.entity.main.Sport
import com.theathletic.entity.settings.UserContentEdition
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.data.local.AmericanFootballPlayType
import com.theathletic.gamedetail.data.local.BaseballPitchOutcome
import com.theathletic.gamedetail.data.local.BasketballPlayType
import com.theathletic.gamedetail.data.local.GameArticlesLocalModel
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.GameLineUpAndStats
import com.theathletic.gamedetail.data.local.GameStatus
import com.theathletic.gamedetail.data.local.GradeStatus
import com.theathletic.gamedetail.data.local.HockeyPlayType
import com.theathletic.gamedetail.data.local.Period
import com.theathletic.gamedetail.data.local.StatisticCategory
import com.theathletic.podcast.ui.DownloadState
import com.theathletic.podcast.ui.PlaybackState
import com.theathletic.ui.LoadingState

internal const val GAME_ODDS_MODULE_ID = "GameOdds"
internal const val RECENT_GAMES_MODULE_ID = "RecentGames"
internal const val SCORING_MODULE_ID = "Scoring"
internal const val GAME_DETAILS_MODULE_ID = "GameDetails"
internal const val TEAM_STATS_MODULE_ID = "TeamStats"
internal const val RELATED_STORIES_MODULE_ID = "RelatedStories"
internal const val TOP_PERFORMERS_MODULE_ID = "TopPerformers"
internal const val TEAM_LEADERS_MODULE_ID = "TeamLeaders"
internal const val INJURY_REPORT_MODULE_ID = "InjuryReport"
internal const val SEASON_STATS_MODULE_ID = "SeasonStats"
internal const val TICKETS_MODULE_ID = "Tickets"
internal const val AMERICAN_FOOTBALL_SCORING_SUMMARY_MODULE_ID = "AmericanFootballScoringSummary"
internal const val AMERICAN_FOOTBALL_DOWN_DISTANCE_MODULE_ID = "AmericanFootballDownDistance"
internal const val AMERICAN_FOOTBALL_PLAY_BY_PLAY_MODULE_ID = "AmericanFootballPlayByPlay"
internal const val HOCKEY_RECENT_PLAYS_MODULE_ID = "HockeyRecentPlays"
internal const val HOCKEY_SCORING_SUMMARY_MODULE_ID = "HockeyScoringSummary"
internal const val BASKETBALL_PLAY_BY_PLAY_MODULE_ID = "BasketballPlayByPlay"
internal const val BASEBALL_CURRENT_INNINGS_MODULE_ID = "CurrentInning"
internal const val BASEBALL_PITCHER_WIN_LOSE_MODULE_ID = "PitcherWinLose"
internal const val BASEBALL_STARTING_PITCHER_MODULE_ID = "StartingPitcher"
internal const val SOCCER_PLAY_BY_PLAY_MODULE_ID = "SoccerPlayByPlay"
internal const val SOCCER_TIMELINE_MODULE_ID = "SoccerTimeline"
internal const val PLAYER_GRADES_MODULE_ID = "PlayerGrades"
internal const val LINE_UP_MODULE_ID = "LineUp"
internal const val TOP_COMMENTS_MODULE_ID = "TopComments"

object BoxScoreFixture {

    fun boxScoreStateFixture(
        gameStatus: GameStatus,
        sport: Sport,
        gradeStatus: GradeStatus = GradeStatus.DISABLED
    ) = BoxScoreState(
        loadingState = LoadingState.FINISHED,
        game = gameFixture(gameStatus, sport, gradeStatus),
        lineUpAndStats = lineUpAndStatsFixture(),
        articles = articlesFixture(),
        contentRegion = UserContentEdition.US
    )

    private fun gameFixture(gameStatus: GameStatus, sport: Sport, gradeStatus: GradeStatus) =
        GameDetailLocalModel(
            id = "kjhswd98e3dh",
            awayTeam = teamFixture(),
            homeTeam = teamFixture(),
            status = gameStatus,
            scheduleAt = Datetime(0),
            isScheduledTimeTbd = false,
            league = GameDetailLocalModel.League(
                legacyLeague = League.NFL,
                id = "shdjkshjdksh",
                alias = "FBL",
                displayName = "Football League"
            ),
            sport = sport,
            venue = "Local Stadium",
            venueCity = "Local Town",
            clock = null,
            period = Period.SECOND_QUARTER,
            permalink = null,
            coverage = emptyList(),
            events = emptyList(),
            timeline = emptyList(),
            awayTeamHomeTeamStats = listOf(teamStatsFixture("1"), teamStatsFixture("2")),
            awayTeamHomeTeamSeasonStats = listOf(seasonStatsFixture("1"), seasonStatsFixture("2")),
            sportExtras = sportExtrasFixture(sport),
            oddsPregame = listOf(gameOddsFixture("1"), gameOddsFixture("2")),
            broadcastNetwork = null,
            gradeStatus = gradeStatus,
            gameTicket = null,
            seasonName = "Post Season",
            topComments = listOf(
                GameDetailLocalModel.TopComment(
                    id = "0",
                    authorGameFlairs = emptyList(),
                    authorName = "",
                    authorUserLevel = 0,
                    avatarUrl = null,
                    comment = "",
                    commentedAt = 0,
                    likesCount = 0,
                    parentId = "0",
                    permalink = ""
                )
            ),
            areCommentsDiscoverable = false
        )

    private fun sportExtrasFixture(sport: Sport): GameDetailLocalModel.SportExtras? {
        return when (sport) {
            Sport.FOOTBALL -> GameDetailLocalModel.AmericanFootballExtras(
                possession = null,
                lastPlay = null,
                scoringPlays = emptyList(),
                recentPlays = listOf(
                    americanFootballPlayFixture("play1"),
                    americanFootballPlayFixture("play2"),
                    americanFootballPlayFixture("play3"),
                ),
                weather = null
            )
            Sport.HOCKEY -> GameDetailLocalModel.HockeyExtras(
                scoringPlays = listOf(
                    hockeyPlaysFixture("play_1"),
                    hockeyPlaysFixture("play_2"),
                    hockeyPlaysFixture("play_3"),
                ),
                recentPlays = listOf(
                    hockeyPlaysFixture("play_1"),
                    hockeyPlaysFixture("play_2"),
                    hockeyPlaysFixture("play_3"),
                ),
            )
            Sport.BASKETBALL -> GameDetailLocalModel.BasketballExtras(
                recentPlays = listOf(
                    basketballPlayFixture("play1"),
                    basketballPlayFixture("play2"),
                    basketballPlayFixture("play3"),
                )
            )
            Sport.BASEBALL -> GameDetailLocalModel.BaseballExtras(
                scoringPlays = listOf(),
                inningPlays = listOf(),
                currentInningPlays = listOf(baseballPitchPlayFixture("play1")),
                outcome = baseballOutcomeFixture(),
                pitching = baseballPitchingFixture(),
                inning = null,
                inningHalf = null
            )
            else -> null
        }
    }

    private fun teamFixture() = GameDetailLocalModel.AmericanFootballGameTeam(
        id = "teamId",
        team = null,
        score = null,
        periodScore = emptyList(),
        lineUp = null,
        recentGames = listOf(recentGamesFixture("1"), recentGamesFixture("2")),
        currentRecord = null,
        teamLeaders = emptyList(),
        topPerformers = listOf(leaderFixture("1"), leaderFixture("2")),
        injuries = emptyList(),
        players = emptyList(),
        currentRanking = null,
        remainingTimeouts = 0,
        usedTimeouts = 0
    )

    private fun recentGamesFixture(id: String) =
        GameDetailLocalModel.RecentGame(
            id = id,
            period = Period.FULL_TIME,
            scheduleAt = Datetime(0),
            awayTeam = null,
            homeTeam = null
        )

    private fun gameOddsFixture(id: String) =
        GameDetailLocalModel.GameOddsTotals(
            id = id,
            balancedLine = true,
            bettingOpen = true,
            line = "3.3",
            betPeriod = "",
            direction = "under",
            price = GameDetailLocalModel.GameOddsPrice(
                oddsFraction = null,
                oddsDecimal = null,
                oddsUs = null
            )
        )

    private fun leaderFixture(id: String) =
        GameDetailLocalModel.StatLeader(
            id = id,
            playerName = null,
            jerseyNumber = null,
            playerPosition = null,
            headshots = emptyList(),
            statLabel = null,
            stats = emptyList(),
            teamAlias = null,
            teamLogos = null,
            primaryColor = null
        )

    private fun lineUpAndStatsFixture() = GameLineUpAndStats(
        awayTeamLineUp = null,
        homeTeamLineUp = null,
        teamStats = emptyList()
    )

    private fun teamStatsFixture(id: String) =
        Pair(
            GameDetailLocalModel.StringStatistic(
                id = "StringStatFirst-$id",
                category = StatisticCategory.DEFENSE,
                headerLabel = null,
                label = "label",
                type = "type",
                lessIsBest = false,
                isChildStat = false,
                referenceOnly = false,
                value = "first-$id",
                longHeaderLabel = null
            ),
            GameDetailLocalModel.StringStatistic(
                id = "StringStatSecond-$id",
                category = StatisticCategory.DEFENSE,
                headerLabel = null,
                label = "label",
                type = "type",
                lessIsBest = false,
                isChildStat = false,
                referenceOnly = false,
                value = "second-$id",
                longHeaderLabel = null
            )
        )

    private fun seasonStatsFixture(id: String) =
        Pair(
            GameDetailLocalModel.RankedStat(
                id = "RankedStatFirst-$id",
                parentStatType = null,
                parentStatCategory = null,
                rank = 1,
                statCategory = null,
                statHeaderLabel = null,
                statLabel = "label",
                statType = "type",
                statValue = "first-$id"
            ),
            GameDetailLocalModel.RankedStat(
                id = "RankedStatSecond-$id",
                parentStatType = null,
                parentStatCategory = null,
                rank = 2,
                statCategory = null,
                statHeaderLabel = null,
                statLabel = "label",
                statType = "type",
                statValue = "second-$id"
            )
        )

    private fun americanFootballPlayFixture(id: String) =
        GameDetailLocalModel.AmericanFootballPlay(
            id = id,
            description = id,
            headerLabel = null,
            occurredAt = Datetime(0),
            awayTeamScore = 1,
            homeTeamScore = 1,
            clock = "9:00",
            period = null,
            possession = null,
            isScoringPlay = false,
            playType = AmericanFootballPlayType.CONVERSION,
            team = null
        )

    private fun basketballPlayFixture(id: String) =
        GameDetailLocalModel.BasketballPlay(
            id = id,
            description = id,
            headerLabel = null,
            occurredAt = Datetime(0),
            awayTeamScore = 1,
            homeTeamScore = 1,
            clock = "9:00",
            period = Period.FIRST_PERIOD,
            playType = BasketballPlayType.JUMP_BALL,
            team = null
        )

    private fun hockeyPlaysFixture(id: String) =
        GameDetailLocalModel.HockeyStandardPlay(
            id = id,
            description = id,
            headerLabel = null,
            occurredAt = Datetime(0),
            period = Period.FIRST_PERIOD,
            awayTeamScore = 1,
            homeTeamScore = 1,
            team = null,
            clock = "09:00",
            type = HockeyPlayType.GOAL,
            awayShotsAtGoal = null,
            homeShotsAtGoal = null,
        )

    private fun baseballPitchingFixture() =
        GameDetailLocalModel.BaseballPitching(
            winPitcher = null,
            lossPitcher = null,
            savePitcher = null
        )

    private fun baseballOutcomeFixture(id: String = "OutcomeId") =
        GameDetailLocalModel.BaseballOutcome(
            id = id,
            inning = 1,
            inningHalf = null,
            balls = 0,
            strikes = 0,
            outs = 0,
            batter = null,
            pitcher = null,
            nextBatter = null,
            loadedBases = emptyList()
        )

    private fun baseballPitchPlayFixture(id: String) =
        GameDetailLocalModel.BaseballPitchPlay(
            id = id,
            description = id,
            headerLabel = null,
            occurredAt = Datetime(0),
            bases = emptyList(),
            hitZone = null,
            number = 1,
            pitchDescription = null,
            pitchOutcome = BaseballPitchOutcome.HIT,
            pitchZone = null
        )

    private fun articlesFixture() = listOf(
        GameArticlesLocalModel.GameArticle(
            id = "jdjdhdjdkjhywe76",
            title = "title",
            imageUrl = null,
            authors = listOf(
                GameArticlesLocalModel.GameArticleAuthor(
                    name = "John Smith",
                    displayOrder = 1
                )
            ),
            commentCount = 9
        )
    )

    data class TestFeedModule(override val moduleId: String) : FeedModuleV2 {
        @Composable
        override fun Render() {
        }
    }

    fun boxScoreFeedFixture() = BoxScore(
        id = "1",
        sections = listOf(
            boxScoreFeedSectionFixture(
                id = "2",
                type = SectionType.GAME,
                modules = listOf(
                    boxScoreFeedLatestNewsModuleFixture(
                        id = "3",
                        header = boxScoreFeedBasicHeaderFixture(
                            id = "4",
                            title = "Latest Newsroom"
                        ),
                        blocks = listOf(
                            boxScoreFeedPodcastItemFixture(
                                id = "5",
                                title = "Sample podcast",
                                playbackState = PlaybackState.None,
                                downloadState = DownloadState.NOT_DOWNLOADED,
                                timeElapsed = 0
                            )
                        )
                    )
                )
            )
        )
    )

    private fun boxScoreFeedSectionFixture(
        id: String,
        type: SectionType,
        modules: List<BoxScoreModules>
    ) = Section(
        id = id,
        type = type,
        modules = modules
    )

    private fun boxScoreFeedLatestNewsModuleFixture(
        id: String,
        header: ModuleHeader,
        blocks: List<Items>
    ) = LatestNewsModule(
        id = id,
        header = header,
        blocks = blocks
    )

    fun boxScoreFeedBasicHeaderFixture(
        id: String,
        title: String
    ) = BasicHeader(
        id = id,
        title = title
    )

    private fun boxScoreFeedPodcastItemFixture(
        id: String,
        title: String,
        playbackState: PlaybackState,
        downloadState: DownloadState,
        timeElapsed: Int
    ) = PodcastEpisode(
        id = id,
        permalink = "",
        commentCount = 10,
        podcastId = id,
        title = title,
        clips = emptyList(),
        mp3Url = "",
        podcastTitle = null,
        publishedAt = Datetime(0L),
        imageUrl = null,
        finished = false,
        episodeId = id,
        description = null,
        duration = 0,
        timeElapsed = timeElapsed,
        playbackState = playbackState,
        downloadState = downloadState
    )
}