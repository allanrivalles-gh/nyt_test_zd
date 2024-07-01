package com.theathletic.boxscore.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentManager
import com.theathletic.scores.GameDetailTab
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.asResourceString

object GameDetailPreviewData {

    val firstTeamPreGame = GameDetailUi.TeamSummary(
        teamId = "teamId",
        legacyId = 1L,
        name = "SJ".asResourceString(),
        logoUrls = emptyList(),
        score = null,
        currentRecord = "(8-5)",
        isFollowable = true,
        isWinner = true
    )

    val secondTeamPreGame = GameDetailUi.TeamSummary(
        teamId = "teamId",
        legacyId = 1L,
        name = "NSH".asResourceString(),
        logoUrls = emptyList(),
        score = null,
        currentRecord = "(10-3)",
        isFollowable = true,
        isWinner = true
    )

    val firstTeamInGame = GameDetailUi.TeamSummary(
        teamId = "teamId",
        legacyId = 1L,
        name = "SJ".asResourceString(),
        logoUrls = emptyList(),
        score = 8,
        currentRecord = "(8-5)",
        isFollowable = true,
        isWinner = true
    )

    val secondTeamInGame = GameDetailUi.TeamSummary(
        teamId = "teamId",
        legacyId = 1L,
        name = "NSH".asResourceString(),
        logoUrls = emptyList(),
        score = 4,
        currentRecord = "(10-3)",
        isFollowable = true,
        isWinner = true
    )

    val firstTeamBasketball = GameDetailUi.TeamSummary(
        teamId = "teamId",
        legacyId = 1L,
        name = "BOS".asResourceString(),
        logoUrls = emptyList(),
        score = 104,
        currentRecord = "(8-5)",
        isFollowable = true,
        isWinner = false
    )

    val secondTeamBasketball = GameDetailUi.TeamSummary(
        teamId = "teamId",
        legacyId = 1L,
        name = "GSW".asResourceString(),
        logoUrls = emptyList(),
        score = 112,
        currentRecord = "(10-3)",
        isFollowable = true,
        isWinner = true
    )

    val firstTeamHockey = GameDetailUi.TeamSummary(
        teamId = "teamId",
        legacyId = 1L,
        name = "OTT".asResourceString(),
        logoUrls = emptyList(),
        score = 4,
        currentRecord = "(8-5)",
        isFollowable = true,
        isWinner = false
    )

    val secondTeamHockey = GameDetailUi.TeamSummary(
        teamId = "teamId",
        legacyId = 1L,
        name = "TOR".asResourceString(),
        logoUrls = emptyList(),
        score = 5,
        currentRecord = "(10-3)",
        isFollowable = true,
        isWinner = true
    )

    val firstTeamNFL = GameDetailUi.TeamSummary(
        teamId = "teamId",
        legacyId = 1L,
        name = "LAR".asResourceString(),
        logoUrls = emptyList(),
        score = 23,
        currentRecord = "(12-5)",
        isFollowable = true,
        isWinner = true
    )

    val secondTeamNFL = GameDetailUi.TeamSummary(
        teamId = "teamId",
        legacyId = 1L,
        name = "CIN".asResourceString(),
        logoUrls = emptyList(),
        score = 20,
        currentRecord = "(10-7)",
        isFollowable = true,
        isWinner = true
    )

    val firstTeamBaseball = GameDetailUi.TeamSummary(
        teamId = "teamId",
        legacyId = 1L,
        name = "CHI".asResourceString(),
        logoUrls = emptyList(),
        score = 4,
        currentRecord = "(12-5)",
        isFollowable = true,
        isWinner = true
    )

    val secondTeamBaseball = GameDetailUi.TeamSummary(
        teamId = "teamId",
        legacyId = 1L,
        name = "NYM".asResourceString(),
        logoUrls = emptyList(),
        score = 3,
        currentRecord = "(10-7)",
        isFollowable = true,
        isWinner = true
    )

    val firstTeamSoccer = GameDetailUi.TeamSummary(
        teamId = "teamId",
        legacyId = 1L,
        name = "MUN".asResourceString(),
        logoUrls = emptyList(),
        score = null,
        currentRecord = "5th in EPL",
        isFollowable = true,
        isWinner = false
    )

    val secondTeamSoccer = GameDetailUi.TeamSummary(
        teamId = "teamId",
        legacyId = 1L,
        name = "BOU".asResourceString(),
        logoUrls = emptyList(),
        score = null,
        currentRecord = "14th in EPL",
        isFollowable = true,
        isWinner = false
    )

    val pregameStatusWithTVNetwork = GameDetailUi.GameStatus.PregameStatus(
        scheduledDate = "Sat, Oct 8",
        scheduledTime = "5:00 AM".asResourceString()
    )

    val pregameStatusWithoutTVNetwork = GameDetailUi.GameStatus.PregameStatus(
        scheduledDate = "Sat, Oct 8",
        scheduledTime = "5:00 AM".asResourceString()
    )

    val inGameInformation = GameDetailUi.GameStatus.InGameStatus(
        isGameDelayed = true,
        gameStatePrimary = "2ND",
        gameStateSecondary = "6:52"
    )

    val postGameInformation = GameDetailUi.GameStatus.PostGameStatus(
        gamePeriod = "Final".asResourceString(),
        scheduledDate = "Tue, Jun 14"
    )

    val baseballInGameInformation = GameDetailUi.GameStatus.BaseballInGameStatus(
        inningHalf = "TOP 4".asResourceString(),
        occupiedBases = listOf(1, 3),
        status = "2-3, 1 OUT".asResourceString(),
        isGameDelayed = false
    )

    val soccerPreGameInformation = GameDetailUi.GameStatus.PregameStatus(
        scheduledDate = "Wed, Jan 8",
        scheduledTime = "7:00 AM".asResourceString()
    )

    val emptyTeamStatus = emptyList<GameDetailUi.TeamStatus>()

    val hockeyTeamStatusNotInPowerPlay = listOf(
        GameDetailUi.TeamStatus.HockeyPowerPlay(inPowerPlay = false)
    )

    val hockeyTeamStatusInPowerPlay = listOf(
        GameDetailUi.TeamStatus.HockeyPowerPlay(inPowerPlay = true)
    )

    val basketballTeamStatus1UsedTimeout = listOf(
        GameDetailUi.TeamStatus.Timeouts(remainingTimeouts = 6, usedTimeouts = 1)
    )

    val basketballTeamStatus4UsedTimeouts = listOf(
        GameDetailUi.TeamStatus.Timeouts(remainingTimeouts = 3, usedTimeouts = 4)
    )

    val nflTeamStatusWithPossession = listOf(
        GameDetailUi.TeamStatus.Possession,
        GameDetailUi.TeamStatus.Timeouts(remainingTimeouts = 2, usedTimeouts = 1)
    )

    val nlfTeamStatusWithoutPossession = listOf(
        GameDetailUi.TeamStatus.Timeouts(remainingTimeouts = 3, usedTimeouts = 0)
    )

    val tabs = listOf(
        GameDetailUi.Tab(
            type = GameDetailTab.GAME,
            label = "Game".asResourceString(),
            showIndicator = false,
        ),
        GameDetailUi.Tab(
            type = GameDetailTab.PLAYER_STATS,
            label = "Stats".asResourceString(),
            showIndicator = true
        ),
        GameDetailUi.Tab(
            type = GameDetailTab.PLAYS,
            label = "Plays".asResourceString(),
            showIndicator = false
        )
    )

    val tabModules = listOf(
        DummyTabModule("Game Tab"),
        DummyTabModule("Stats Tab"),
        DummyTabModule("Plays Tab"),
    )

    val soccerRecentForm = GameDetailUi.GameInfo.RecentForm(
        expectedGoals = SoccerRecentFormHeaderModel.ExpectedGoals(
            firstTeamValue = "0.55",
            secondTeamValue = "1.45",
            showExpectedGoals = true
        ),
        firstTeamRecentForm = listOf(
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.WIN,
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.DRAW,
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.LOSS,
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.WIN,
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.DRAW
        ),
        secondTeamRecentForm = listOf(
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.DRAW,
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.DRAW,
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.LOSS,
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.LOSS,
            SoccerRecentFormHeaderModel.SoccerRecentFormIcons.WIN
        ),
        isReverse = false,
        showRecentForm = true
    )

    val interactor = object : GameDetailUi.Interactor {
        override fun onBackButtonClicked() {}
        override fun onTabClicked(tab: GameDetailTab) {}
        override fun onTeamClicked(teamId: String, legacyId: Long, teamName: String) {}
        override fun onShareClick(shareLink: String) {}
    }

    data class DummyTabModule(
        val label: String
    ) : TabModule {

        @Composable
        override fun Render(isActive: Boolean, fragmentManager: () -> FragmentManager) {
            Box {
                Text(
                    text = label,
                    style = AthTextStyle.Calibre.Utility.Regular.ExtraLarge,
                    color = AthTheme.colors.dark500,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

object PreviewDataGameDetailFragManager : FragmentManager()