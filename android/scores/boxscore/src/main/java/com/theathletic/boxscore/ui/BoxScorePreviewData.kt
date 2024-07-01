package com.theathletic.boxscore.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.boxscore.ui.modules.BaseballPitchOutcomeType
import com.theathletic.boxscore.ui.modules.BaseballPlayModule
import com.theathletic.boxscore.ui.modules.DownAndDistanceModule
import com.theathletic.boxscore.ui.modules.GameDetailsModule
import com.theathletic.boxscore.ui.modules.GameOddsModule
import com.theathletic.boxscore.ui.modules.IndicatorType
import com.theathletic.boxscore.ui.modules.PitcherModule
import com.theathletic.boxscore.ui.modules.SlideStoriesLaunchModule
import com.theathletic.boxscore.ui.modules.TicketsModule
import com.theathletic.data.SizedImage
import com.theathletic.feed.compose.ui.components.articlePreviewData
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.ResourceString.StringWrapper
import com.theathletic.ui.asResourceString

@Suppress("LargeClass")
object BoxScorePreviewData {

    val GameDetailsItems = listOf(
        GameDetailsModule.DetailsItem(
            StringWrapper("Location"),
            StringWrapper("Melbourne Cricket Ground"),
            true
        ),
        GameDetailsModule.DetailsItem(
            StringWrapper("Location 2 "),
            StringWrapper("Referee\nAssistant\nAssistant\nFourth"),
            true
        )
    )

    val TeamStats = BoxScoreTeamStatsUiModel(
        id = "01",
        firstTeamLogoUrlList = listOf(
            SizedImage(
                72,
                72,
                "https://s3-us-west-2.amazonaws.com/theathletic-team-logos/team-logo-91-72x72.png"
            )
        ),
        secondTeamLogoUrlList = listOf(
            SizedImage(
                72,
                72,
                "https://s3-us-west-2.amazonaws.com/theathletic-team-logos/team-logo-87-72x72.png"
            )
        ),
        statsItems = listOf(
            BoxScoreTeamStatsUiModel.BoxScoreTeamStatsItem(
                "39-84",
                R.color.ath_grey_30,
                "38.85",
                R.color.ath_grey_50,
                "Field Goals",
                false
            ),
            BoxScoreTeamStatsUiModel.BoxScoreTeamStatsItem(
                "37",
                R.color.ath_grey_50,
                "44",
                R.color.ath_grey_30,
                "Rebounds",
                false
            ),
            BoxScoreTeamStatsUiModel.BoxScoreTeamStatsItem(
                "10",
                R.color.ath_grey_50,
                "12",
                R.color.ath_grey_30,
                "Offensive Rebounds",
                true
            ),
            BoxScoreTeamStatsUiModel.BoxScoreTeamStatsItem(
                "27",
                R.color.ath_grey_30,
                "22",
                R.color.ath_grey_50,
                "Assists",
                false
            )
        )
    )

    val fullPlayByPlayInteractor = object : BoxScoreRecentPlays.Interactor {
        override fun onFullPlayByPlayClick() {
            /* Do nothing */
        }
    }

    val playByPlayList = listOf(
        BoxScoreRecentPlays.Play(
            id = "uniqueId",
            teamLogos = emptyList(),
            teamColor = null,
            title = "Defensive Rebound",
            description = "Tre Jones defensive rebound",
            clock = "1:32",
            awayTeamAlias = "SAS",
            homeTeamAlias = "LAL",
            awayTeamScore = "37",
            homeTeamScore = "31",
            showScores = false,
            showDivider = true
        ),
        BoxScoreRecentPlays.Play(
            id = "uniqueId",
            teamLogos = emptyList(),
            teamColor = null,
            title = "Defensive Rebound",
            description = "Tre Jones defensive rebound",
            clock = "1:32",
            awayTeamAlias = "SAS",
            homeTeamAlias = "LAL",
            awayTeamScore = "37",
            homeTeamScore = "31",
            showScores = false,
            showDivider = true
        ),
        BoxScoreRecentPlays.Play(
            id = "uniqueId",
            teamLogos = emptyList(),
            teamColor = null,
            title = "Defensive Rebound",
            description = "Tre Jones defensive rebound",
            clock = "1:32",
            awayTeamAlias = "SAS",
            homeTeamAlias = "LAL",
            awayTeamScore = "37",
            homeTeamScore = "31",
            showScores = false,
            showDivider = true
        )
    )

    val seasonStats = BoxScoreSeasonStatsUiModel(
        firstTeamLogos = listOf(
            SizedImage(
                72,
                72,
                "https://s3-us-west-2.amazonaws.com/theathletic-team-logos/team-logo-91-72x72.png"
            )
        ),
        secondTeamLogos = listOf(
            SizedImage(
                72,
                72,
                "https://s3-us-west-2.amazonaws.com/theathletic-team-logos/team-logo-87-72x72.png"
            )
        ),
        statsItems = listOf(
            BoxScoreSeasonStatsUiModel.BoxScoreSeasonStatsItem(
                "27.1",
                StringWrapper("(14th)"),
                true,
                "29.4",
                StringWrapper("(1st)"),
                true,
                "Points",
                false
            ),
            BoxScoreSeasonStatsUiModel.BoxScoreSeasonStatsItem(
                "4.00",
                StringWrapper("(10th)"),
                true,
                "5.50",
                StringWrapper("(57th)"),
                true,
                "Shots on Goal Per Game in the league",
                false
            ),
            BoxScoreSeasonStatsUiModel.BoxScoreSeasonStatsItem(
                "359.9",
                StringWrapper("(0th)"),
                false,
                "220.9",
                StringWrapper("(0th)"),
                false,
                "Pass Yards",
                true
            ),
            BoxScoreSeasonStatsUiModel.BoxScoreSeasonStatsItem(
                "59.7",
                StringWrapper("(14th)"),
                true,
                "107.9",
                StringWrapper("(17th)"),
                true,
                "Rush Yards",
                true
            ),
            BoxScoreSeasonStatsUiModel.BoxScoreSeasonStatsItem(
                "2.1",
                StringWrapper("(14th)"),
                true,
                "1.2",
                StringWrapper("(3rd)"),
                true,
                "Turnovers",
                false
            )
        )
    )

    val teamLogo = listOf(
        SizedImage(
            72,
            72,
            "https://s3-us-west-2.amazonaws.com/theathletic-team-logos/team-logo-91-72x72.png"
        )
    )

    val scoresTable4QColumns = listOf(
        BoxScoresScoreTableUiModel.ScoreTableColumn(
            StringWrapper("1"),
            "0",
            "0"
        ),
        BoxScoresScoreTableUiModel.ScoreTableColumn(
            StringWrapper("2"),
            "12",
            "12"
        ),
        BoxScoresScoreTableUiModel.ScoreTableColumn(
            StringWrapper("3"),
            "4",
            "13"
        ),
        BoxScoresScoreTableUiModel.ScoreTableColumn(
            StringWrapper("4"),
            "-",
            "-"
        )
    )

    val scoresTable4QTotalColumns = listOf(
        BoxScoresScoreTableUiModel.ScoreTableColumn(
            StringWrapper("T"),
            "16",
            "25"
        )
    )

    val scoresTable3QColumns = listOf(
        BoxScoresScoreTableUiModel.ScoreTableColumn(
            StringWrapper("1"),
            "0",
            "0"
        ),
        BoxScoresScoreTableUiModel.ScoreTableColumn(
            StringWrapper("2"),
            "12",
            "12"
        ),
        BoxScoresScoreTableUiModel.ScoreTableColumn(
            StringWrapper("3"),
            "4",
            "13"
        ),
        BoxScoresScoreTableUiModel.ScoreTableColumn(
            StringWrapper("SO"),
            "0 (0-7)",
            "1 (1-7)"
        )
    )

    val scoresTable3QTotalColumns = listOf(
        BoxScoresScoreTableUiModel.ScoreTableColumn(
            StringWrapper("T"),
            "16",
            "25"
        )
    )

    val scoresTableBaseballColumns = listOf(
        BoxScoresScoreTableUiModel.ScoreTableColumn(
            StringWrapper("1"),
            "0",
            "0"
        ),
        BoxScoresScoreTableUiModel.ScoreTableColumn(
            StringWrapper("2"),
            "12",
            "12"
        ),
        BoxScoresScoreTableUiModel.ScoreTableColumn(
            StringWrapper("3"),
            "4",
            "13"
        ),
        BoxScoresScoreTableUiModel.ScoreTableColumn(
            StringWrapper("4"),
            "-",
            "-"
        ),
        BoxScoresScoreTableUiModel.ScoreTableColumn(
            StringWrapper("5"),
            "0",
            "0"
        ),
        BoxScoresScoreTableUiModel.ScoreTableColumn(
            StringWrapper("6"),
            "12",
            "12"
        ),
        BoxScoresScoreTableUiModel.ScoreTableColumn(
            StringWrapper("7"),
            "4",
            "13"
        ),
        BoxScoresScoreTableUiModel.ScoreTableColumn(
            StringWrapper("8"),
            "-",
            "-"
        ),
        BoxScoresScoreTableUiModel.ScoreTableColumn(
            StringWrapper("9"),
            "-",
            "-"
        ),
        BoxScoresScoreTableUiModel.ScoreTableColumn(
            StringWrapper("10"),
            "12",
            "12"
        ),
        BoxScoresScoreTableUiModel.ScoreTableColumn(
            StringWrapper("11"),
            "4",
            "13"
        )
    )

    val scoresTableBaseballTotalColumns = listOf(
        BoxScoresScoreTableUiModel.ScoreTableColumn(
            StringWrapper("R"),
            "16",
            "25"
        ),
        BoxScoresScoreTableUiModel.ScoreTableColumn(
            StringWrapper("H"),
            "16",
            "25"
        ),
        BoxScoresScoreTableUiModel.ScoreTableColumn(
            StringWrapper("E"),
            "16",
            "25"
        )
    )

    val baseballCurrentInningPlayMock = BaseballCurrentInningPlayUiModel(
        id = "001",
        pitcher = CurrentInningUi.PlayerSummary(
            playInfo = StringWrapper("RHP"),
            headshotList = emptyList(),
            name = "Dr. Smith",
            lastPlay = StringWithParams(
                R.string.box_score_baseball_current_inning_pitcher_strikes,
                "27",
                "12"
            ),
            teamColor = Color.Red,
            title = StringWithParams(R.string.box_score_baseball_pitching),
            stats = StringWrapper("0.0IP, 0 ER, 0 K")
        ),
        batter = CurrentInningUi.PlayerSummary(
            playInfo = StringWrapper("C"),
            headshotList = emptyList(),
            name = "M. Pérez",
            lastPlay = StringWithParams(
                R.string.box_score_baseball_current_inning_next_batter,
                "B. Nimmo",
                "CF"
            ),
            teamColor = Color.Yellow,
            title = StringWithParams(R.string.box_score_baseball_batting),
            stats = StringWithParams(
                R.string.box_score_baseball_current_inning_batter_stats,
                "0",
                "3",
                ".375"
            )
        ),
        playStatus = listOf(
            CurrentInningUi.CurrentPlayStatus(IndicatorType.Balls, 2),
            CurrentInningUi.CurrentPlayStatus(IndicatorType.Strikes, 1),
            CurrentInningUi.CurrentPlayStatus(IndicatorType.Outs, 2)
        ),
        currentInning = listOf(
            CurrentInningUi.Play(
                title = "M. Perez at bat.",
                plays = listOf(
                    BaseballPlayModule.PitchPlay(
                        title = "Fly Out",
                        description = "91mph four seam FB",
                        pitchNumber = 4,
                        pitchOutcomeType = BaseballPitchOutcomeType.HIT,
                        occupiedBases = listOf(1, 3),
                        hitZone = 15,
                        pitchZone = 7
                    ),
                    BaseballPlayModule.PitchPlay(
                        title = "Ball",
                        description = "86mph breaking ball",
                        pitchNumber = 1,
                        pitchOutcomeType = BaseballPitchOutcomeType.BALL,
                        occupiedBases = listOf(1),
                        hitZone = 15,
                        pitchZone = 7
                    ),
                    BaseballPlayModule.PitchPlay(
                        title = "Strike Looking",
                        description = "92mph slider",
                        pitchNumber = 2,
                        pitchOutcomeType = BaseballPitchOutcomeType.STRIKE,
                        occupiedBases = emptyList(),
                        hitZone = 35,
                        pitchZone = 1
                    ),
                    BaseballPlayModule.PitchPlay(
                        title = "Ball",
                        description = "89mph curve",
                        pitchNumber = 1,
                        pitchOutcomeType = BaseballPitchOutcomeType.BALL,
                        occupiedBases = listOf(1),
                        hitZone = 15,
                        pitchZone = 7
                    )
                )
            ),
            CurrentInningUi.Play(
                title = "Keplinger singled up the middle.",
                plays = emptyList()
            ),
            CurrentInningUi.Play(
                title = "Newman flied out to right.",
                plays = emptyList()
            ),
            CurrentInningUi.Play(
                title = "Brubaker grounded out to shortstop.",
                plays = emptyList()
            )
        )
    )

    val baseballCurrentInningInteractor = object : BaseballCurrentInningPlayUiModel.Interactor {
        override fun onFullPlayByPlayClick() {
            /* Do nothing */
        }
    }

    val baseballCurrentInningMultiPlayMock = BaseballCurrentInningPlayUiModel(
        id = "001",
        pitcher = CurrentInningUi.PlayerSummary(
            playInfo = StringWrapper("RHP"),
            headshotList = emptyList(),
            name = "Dr. Smith",
            lastPlay = StringWithParams(
                R.string.box_score_baseball_current_inning_pitcher_strikes,
                "27",
                "12"
            ),
            teamColor = Color.Red,
            title = StringWithParams(R.string.box_score_baseball_pitching),
            stats = StringWrapper("0.0IP, 0 ER, 0 K")
        ),
        batter = CurrentInningUi.PlayerSummary(
            playInfo = StringWrapper("C"),
            headshotList = emptyList(),
            name = "M. Pérez",
            lastPlay = StringWithParams(
                R.string.box_score_baseball_current_inning_next_batter,
                "Hamilton Ponting",
                "CF"
            ),
            teamColor = Color.Yellow,
            title = StringWithParams(R.string.box_score_baseball_batting),
            stats = StringWithParams(
                R.string.box_score_baseball_current_inning_batter_stats,
                "0",
                "3",
                ".375"
            )
        ),
        playStatus = listOf(
            CurrentInningUi.CurrentPlayStatus(IndicatorType.Balls, 4),
            CurrentInningUi.CurrentPlayStatus(IndicatorType.Strikes, 0),
            CurrentInningUi.CurrentPlayStatus(IndicatorType.Outs, 2)
        ),
        currentInning = listOf(
            CurrentInningUi.Play(
                title = "M. Perez at bat.",
                plays = listOf(
                    BaseballPlayModule.PitchPlay(
                        title = "Fly Out",
                        description = "91mph four seam FB",
                        pitchNumber = 4,
                        pitchOutcomeType = BaseballPitchOutcomeType.HIT,
                        occupiedBases = listOf(1, 3),
                        hitZone = 15,
                        pitchZone = 7
                    ),
                    BaseballPlayModule.PitchPlay(
                        title = "Ball",
                        description = "86mph breaking ball",
                        pitchNumber = 1,
                        pitchOutcomeType = BaseballPitchOutcomeType.BALL,
                        occupiedBases = listOf(1),
                        hitZone = 15,
                        pitchZone = 7
                    ),
                    BaseballPlayModule.PitchPlay(
                        title = "Strike Looking",
                        description = "92mph slider",
                        pitchNumber = 2,
                        pitchOutcomeType = BaseballPitchOutcomeType.STRIKE,
                        occupiedBases = emptyList(),
                        hitZone = 35,
                        pitchZone = 1
                    ),
                    BaseballPlayModule.PitchPlay(
                        title = "Ball",
                        description = "89mph curve",
                        pitchNumber = 1,
                        pitchOutcomeType = BaseballPitchOutcomeType.BALL,
                        occupiedBases = listOf(1),
                        hitZone = 15,
                        pitchZone = 7
                    )
                )
            ),
            CurrentInningUi.Play(
                title = "Cesar Hernadez grounds out to shallow infield, Hoy Park to Josh VanMeter",
                plays = listOf(
                    BaseballPlayModule.PitchPlay(
                        title = "Ball",
                        description = "86mph breaking ball",
                        pitchNumber = 1,
                        pitchOutcomeType = BaseballPitchOutcomeType.BALL,
                        occupiedBases = listOf(1),
                        hitZone = 15,
                        pitchZone = 7
                    ),
                    BaseballPlayModule.PitchPlay(
                        title = "Strike Looking",
                        description = "92mph slider",
                        pitchNumber = 2,
                        pitchOutcomeType = BaseballPitchOutcomeType.STRIKE,
                        occupiedBases = emptyList(),
                        hitZone = 35,
                        pitchZone = 1
                    )
                )
            ),
            CurrentInningUi.Play(
                title = "Newman flied out to right.",
                plays = emptyList()
            ),
            CurrentInningUi.Play(
                title = "Brubaker grounded out to shortstop.",
                plays = emptyList()
            )
        )
    )

    val teamLeadersData = TopLeaderPerformerUiModel(
        id = "mockId",
        titleResId = R.string.box_score_team_leaders_title,
        subtitle = "Post Season",
        includeDivider = true,
        playerStats = listOf(
            TopLeaderPerformerUi.Category(
                label = "Points",
                players = listOf(
                    TopLeaderPerformerUi.Player(
                        teamColor = Color.Red,
                        name = "S. Curry",
                        showDivider = true,
                        details = StringWithParams(
                            R.string.box_score_stats_leader_player_details_no_pos_formatter,
                            "PG",
                            "GSW # 30"
                        ),
                        headShotList = emptyList(),
                        teamLogoList = emptyList(),
                        stats = listOf(
                            TopLeaderPerformerUi.PlayerStats("H-AB", "2-5"),
                            TopLeaderPerformerUi.PlayerStats("RBI", "0"),
                            TopLeaderPerformerUi.PlayerStats("BB", "0")
                        )
                    ),
                    TopLeaderPerformerUi.Player(
                        teamColor = Color.DarkGray,
                        name = "S. Curry",
                        showDivider = false,
                        details = StringWithParams(
                            R.string.box_score_stats_leader_player_details_no_pos_formatter,
                            "PG",
                            "GSW # 30"
                        ),
                        headShotList = emptyList(),
                        teamLogoList = emptyList(),
                        stats = listOf(
                            TopLeaderPerformerUi.PlayerStats("H-AB", "2-4"),
                            TopLeaderPerformerUi.PlayerStats("RBI", "3"),
                            TopLeaderPerformerUi.PlayerStats("BB", "0")
                        )
                    )
                )
            ),
            TopLeaderPerformerUi.Category(
                label = "Rebounds",
                players = listOf(
                    TopLeaderPerformerUi.Player(
                        teamColor = Color.Yellow,
                        name = "S. Curry",
                        showDivider = true,
                        details = StringWithParams(
                            R.string.box_score_stats_leader_player_details_no_pos_formatter,
                            "PG",
                            "GSW # 30"
                        ),
                        headShotList = emptyList(),
                        teamLogoList = emptyList(),
                        stats = listOf(
                            TopLeaderPerformerUi.PlayerStats("K", "7"),
                            TopLeaderPerformerUi.PlayerStats("BB", "2"),
                            TopLeaderPerformerUi.PlayerStats("FT", "4")
                        )
                    ),
                    TopLeaderPerformerUi.Player(
                        teamColor = Color.Magenta,
                        name = "S. Curry",
                        showDivider = false,
                        details = StringWithParams(
                            R.string.box_score_stats_leader_player_details_no_pos_formatter,
                            "PG",
                            "GSW # 30"
                        ),
                        headShotList = emptyList(),
                        teamLogoList = emptyList(),
                        stats = listOf(
                            TopLeaderPerformerUi.PlayerStats("K", "7"),
                            TopLeaderPerformerUi.PlayerStats("BB", "2"),
                            TopLeaderPerformerUi.PlayerStats("FT", "4")
                        )
                    )
                )
            ),

            TopLeaderPerformerUi.Category(
                label = "Assists",
                players = listOf(
                    TopLeaderPerformerUi.Player(
                        teamColor = Color.Cyan,
                        name = "S. Curry",
                        showDivider = true,
                        details = StringWithParams(
                            R.string.box_score_stats_leader_player_details_no_pos_formatter,
                            "PG",
                            "GSW # 30"
                        ),
                        headShotList = emptyList(),
                        teamLogoList = emptyList(),
                        stats = listOf(
                            TopLeaderPerformerUi.PlayerStats("PTS", "1"),
                            TopLeaderPerformerUi.PlayerStats("FG%", "24.56"),
                            TopLeaderPerformerUi.PlayerStats("FT", "82")
                        )
                    ),
                    TopLeaderPerformerUi.Player(
                        teamColor = Color.Blue,
                        name = "S. Curry",
                        showDivider = false,
                        details = StringWithParams(
                            R.string.box_score_stats_leader_player_details_no_pos_formatter,
                            "PG",
                            "GSW # 30"
                        ),
                        headShotList = emptyList(),
                        teamLogoList = emptyList(),
                        stats = listOf(
                            TopLeaderPerformerUi.PlayerStats("PTS", "82"),
                            TopLeaderPerformerUi.PlayerStats("FG%", "0"),
                            TopLeaderPerformerUi.PlayerStats("FT", "8.96")
                        )
                    )
                )
            )
        )
    )

    val baseballPitcherWinLossMock = BaseballPitcherWinLossUiModel(
        id = "0001",
        pitchers = listOf(
            BaseballPitcherWinLossUiModel.Pitcher(
                title = StringWrapper("WIN"),
                name = "A. Chapman",
                teamColor = Color.Red,
                headshot = emptyList(),
                stats = "2.1 IP, 1 ER, 5 K, 44 P, 5.4 A, 6.2 B"
            ),
            BaseballPitcherWinLossUiModel.Pitcher(
                title = StringWrapper("LOSS"),
                name = "A. Chapman",
                teamColor = Color.Green,
                headshot = emptyList(),
                stats = "2.1 IP, 1 ER, 5 K, 44 P, 5.4 A, 6.2 B"
            ),
            BaseballPitcherWinLossUiModel.Pitcher(
                title = StringWrapper("SAVE"),
                name = "A. Chapman",
                teamColor = Color.Blue,
                headshot = emptyList(),
                stats = "2.1 IP, 1 ER, 5 K, 44 P, 5.4 A, 6.2 B"
            )
        )
    )

    val pitcherModuleMock = PitcherModule(
        titleId = R.string.box_score_baseball_starting_pitchers_title,
        id = "009",
        awayTeamPitcher = StartingPitchersUi.PitcherStats(
            name = StringWrapper("John Rambo"),
            details = StringWrapper("LHC"),
            teamColor = Color.Red,
            headshotList = emptyList(),
            seasonStatsHeader = listOf(
                "G".asResourceString(),
                "W-L".asResourceString(),
                "ERA".asResourceString(),
                "K".asResourceString(),
                "WHIP".asResourceString(),
                "IP".asResourceString()
            ),
            seasonStatsValues = listOf(
                "1",
                "12",
                "8",
                "23",
                "23",
                "1234"
            )
        ),
        homeTeamPitcher = StartingPitchersUi.PitcherStats(
            name = StringWrapper("Tom Hanks"),
            details = StringWrapper("SHC"),
            headshotList = emptyList(),
            teamColor = Color.Green,
            seasonStatsHeader = listOf(
                "G".asResourceString(),
                "W-L".asResourceString(),
                "ERA".asResourceString(),
                "K".asResourceString(),
                "WHIP".asResourceString(),
                "IP".asResourceString()
            ),
            seasonStatsValues = listOf(
                "100",
                "1",
                "80",
                "230",
                "2",
                "123"
            )
        )
    )

    val gameOddsMock = GameOddsModule(
        id = "0004",
        firstTeamOdds = GameOddsUi.TeamOdds(
            totalUsOdds = "-120",
            spreadLine = StringWrapper("-1.5"),
            spreadUsOdds = "-120",
            totalDirection = StringWrapper("O 11"),
            moneyUsOdds = "-185",
            label = "STL",
            logoUrlList = emptyList()
        ),
        secondTeamOdds = GameOddsUi.TeamOdds(
            totalUsOdds = "-100",
            spreadLine = StringWrapper("+1.5"),
            spreadUsOdds = "+100",
            totalDirection = StringWrapper("U 11"),
            moneyUsOdds = "+150",
            label = "COL",
            logoUrlList = emptyList()
        ),
    )

    val relatedStoriesInteractorMock = object : RelatedStoriesUi.Interactor {
        override fun onArticleClick(
            analyticsPayload: RelatedStoriesUi.RelatedStoriesAnalyticsPayload
        ) {
            /* Do nothing */
        }
    }

    val relatedStoriesMock = listOf(
        RelatedStoriesUi.Article(
            "1",
            showCommentCount = true,
            imageUrl = "",
            commentCount = "20",
            authors = StringWithParams(R.string.box_score_related_stories_authors, "Tom", "Jerry"),
            title = "Kris Bryant on Cubs expectations, opportunity with Rockies and more: ‘The expectation now is to win’",
            gameId = "100",
            analyticsPayload = RelatedStoriesUi.RelatedStoriesAnalyticsPayload(
                gameId = "",
                pageOrder = 1,
                leagueId = "",
                articlePosition = 1,
                articleId = ""
            ),
            impressionPayload = ImpressionPayload(
                pageOrder = 1,
                container = "",
                element = "",
                hIndex = 1L,
                objectId = "",
                objectType = "",
                parentObjectId = "",
                parentObjectType = "",
                vIndex = 1L
            )
        ),
        RelatedStoriesUi.Article(
            "2",
            showCommentCount = false,
            imageUrl = "",
            commentCount = "0",
            authors = StringWithParams(R.string.box_score_related_stories_authors, "Scooby", "Do"),
            title = "‘Meaningful celebration has to come with reflection’: Jackie Robinson’s son on the legacy for the next generation",
            gameId = "200",
            analyticsPayload = RelatedStoriesUi.RelatedStoriesAnalyticsPayload(
                gameId = "",
                pageOrder = 1,
                leagueId = "",
                articlePosition = 1,
                articleId = ""
            ),
            impressionPayload = ImpressionPayload(
                pageOrder = 1,
                container = "",
                element = "",
                hIndex = 1L,
                objectId = "",
                objectType = "",
                parentObjectId = "",
                parentObjectType = "",
                vIndex = 1L
            )
        ),
        RelatedStoriesUi.Article(
            "2",
            showCommentCount = true,
            imageUrl = "",
            commentCount = "999",
            authors = StringWithParams(R.string.box_score_related_stories_authors, "Heckle", "Jackle"),
            title = "In an emotional homecoming, Hunter Greene sets records at Dodger Stadium",
            gameId = "300",
            analyticsPayload = RelatedStoriesUi.RelatedStoriesAnalyticsPayload(
                gameId = "",
                pageOrder = 1,
                leagueId = "",
                articlePosition = 1,
                articleId = ""
            ),
            impressionPayload = ImpressionPayload(
                pageOrder = 1,
                container = "",
                element = "",
                hIndex = 1L,
                objectId = "",
                objectType = "",
                parentObjectId = "",
                parentObjectType = "",
                vIndex = 1L
            )
        )
    )

    @Suppress("LongMethod")
    @Composable
    fun getRecentGamesMockData(): BoxScoreRecentGamesUiModel {
        return BoxScoreRecentGamesUiModel(
            includeDivider = true,
            id = "012",
            teams = RecentGamesUi.Teams(
                firstTeamName = "Tigers",
                secondTeamName = "Guardians"
            ),

            firstTeamRecentGames = listOf(
                RecentGamesUi.RecentGame(
                    id = "001",
                    teamId = "007",
                    date = "Sept 15, 2022",
                    firstTeamScore = "123",
                    secondTeamScore = "321",
                    isFirstTeamWinners = true,
                    opponentLogoUrlList = emptyList(),
                    opponentTeamAlias = StringWrapper("@CWS"),
                    result = StringWrapper("W/OT"),
                    resultColor = AthTheme.colors.green,
                    isSecondTeamWinners = false
                ),
                RecentGamesUi.RecentGame(
                    id = "002",
                    teamId = "007",
                    date = "Aug 15, 2022",
                    firstTeamScore = "123",
                    secondTeamScore = "3",
                    isFirstTeamWinners = false,
                    opponentLogoUrlList = emptyList(),
                    opponentTeamAlias = StringWrapper("CWS"),
                    result = StringWrapper("L"),
                    resultColor = AthTheme.colors.red,
                    isSecondTeamWinners = true
                ),
                RecentGamesUi.RecentGame(
                    id = "003",
                    teamId = "007",
                    date = "Aug 15, 2022",
                    firstTeamScore = "123",
                    secondTeamScore = "321",
                    isFirstTeamWinners = false,
                    opponentLogoUrlList = emptyList(),
                    opponentTeamAlias = StringWrapper("TA"),
                    result = StringWrapper("W"),
                    resultColor = AthTheme.colors.green,
                    isSecondTeamWinners = true
                ),
                RecentGamesUi.RecentGame(
                    id = "004",
                    teamId = "007",
                    date = "Aug 15, 2022",
                    firstTeamScore = "(9) 2",
                    secondTeamScore = "2 (10)",
                    isFirstTeamWinners = true,
                    opponentLogoUrlList = emptyList(),
                    opponentTeamAlias = StringWrapper("CWS"),
                    result = StringWrapper("L"),
                    resultColor = AthTheme.colors.red,
                    isSecondTeamWinners = false
                ),
                RecentGamesUi.RecentGame(
                    id = "005",
                    teamId = "007",
                    date = "Aug 15, 2022",
                    firstTeamScore = "45",
                    secondTeamScore = "45",
                    isFirstTeamWinners = false,
                    opponentLogoUrlList = emptyList(),
                    opponentTeamAlias = StringWrapper("CWS"),
                    result = StringWrapper("T"),
                    resultColor = AthTheme.colors.dark500,
                    isSecondTeamWinners = false
                )
            ),
            secondTeamRecentGames = listOf(
                RecentGamesUi.RecentGame(
                    id = "001",
                    teamId = "007",
                    date = "Aug 15, 2022",
                    firstTeamScore = "123",
                    secondTeamScore = "321",
                    isFirstTeamWinners = true,
                    opponentLogoUrlList = emptyList(),
                    opponentTeamAlias = StringWrapper("CWS"),
                    result = StringWrapper("W"),
                    resultColor = AthTheme.colors.green,
                    isSecondTeamWinners = false
                ),
                RecentGamesUi.RecentGame(
                    id = "002",
                    teamId = "007",
                    date = "Aug 15, 2022",
                    firstTeamScore = "123",
                    secondTeamScore = "3",
                    isFirstTeamWinners = false,
                    opponentLogoUrlList = emptyList(),
                    opponentTeamAlias = StringWrapper("CWS"),
                    result = StringWrapper("L"),
                    resultColor = AthTheme.colors.red,
                    isSecondTeamWinners = true
                ),
                RecentGamesUi.RecentGame(
                    id = "003",
                    teamId = "007",
                    date = "Aug 15, 2022",
                    firstTeamScore = "123",
                    secondTeamScore = "321",
                    isFirstTeamWinners = false,
                    opponentLogoUrlList = emptyList(),
                    opponentTeamAlias = StringWrapper("TA"),
                    result = StringWrapper("W"),
                    resultColor = AthTheme.colors.green,
                    isSecondTeamWinners = true
                ),
                RecentGamesUi.RecentGame(
                    id = "004",
                    teamId = "007",
                    date = "Aug 15, 2022",
                    firstTeamScore = "(9) 2",
                    secondTeamScore = "2 (10)",
                    isFirstTeamWinners = true,
                    opponentLogoUrlList = emptyList(),
                    opponentTeamAlias = StringWrapper("CWS"),
                    result = StringWrapper("L"),
                    resultColor = AthTheme.colors.red,
                    isSecondTeamWinners = false
                ),
                RecentGamesUi.RecentGame(
                    id = "005",
                    teamId = "007",
                    date = "Aug 15, 2022",
                    firstTeamScore = "45",
                    secondTeamScore = "45",
                    isFirstTeamWinners = false,
                    opponentLogoUrlList = emptyList(),
                    opponentTeamAlias = StringWrapper("CWS"),
                    result = StringWrapper("T"),
                    resultColor = AthTheme.colors.dark500,
                    isSecondTeamWinners = false
                )
            )
        )
    }

    val downAndDistanceMockModule = DownAndDistanceModule(
        teamLogos = emptyList(),
        id = "001",
        subTitle = StringWithParams(R.string.plays_american_football_drive_stats_subtitle, 5, 19, "13:45"),
        title = StringWithParams(R.string.box_score_last_play_1st_down_with_goal, "TB", 5)
    )

    val ticketsModule = TicketsModule(
        id = "001",
        ticket = TicketsUiModel(
            title = StringWithParams(R.string.box_score_game_details_tickets, "$35"),
            ticketUrlLink = "",
            vendorImageLight = emptyList(),
            vendorImageDark = emptyList()
        )
    )

    val latestNewsArticleOnlyMock = BoxScoreUiModel.LatestNewsUiModel(
        id = "001",
        blocks = listOf(articlePreviewData(), articlePreviewData()),
        header = BoxScoreUiModel.BasicHeaderUiModel(
            id = "002",
            "Latest News"
        )
    )

    val slideStoriesLaunchModule = SlideStoriesLaunchModule(
        id = "001",
        uiModel = SlideStoriesLaunchUiModel(id = "qwerty123333")
    )
}