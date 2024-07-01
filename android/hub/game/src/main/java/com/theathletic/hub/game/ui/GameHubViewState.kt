package com.theathletic.hub.game.ui

import com.theathletic.boxscore.ui.GameDetailUi
import com.theathletic.boxscore.ui.TabModule
import com.theathletic.scores.GameDetailTab
import com.theathletic.ui.LoadingState
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asResourceString

data class GameHubViewState(
    val toolbarLabel: ResourceString = "-".asResourceString(),
    val firstTeam: GameDetailUi.TeamSummary = emptyTeamSummary,
    val secondTeam: GameDetailUi.TeamSummary = emptyTeamSummary,
    val firstTeamStatus: List<GameDetailUi.TeamStatus> = emptyList(),
    val secondTeamStatus: List<GameDetailUi.TeamStatus> = emptyList(),
    val gameStatus: GameDetailUi.GameStatus = emptyGameStatus,
    val gameInfo: GameDetailUi.GameInfo? = null,
    val tabItems: List<GameDetailUi.Tab> = emptyList(),
    val tabModules: List<TabModule> = emptyList(),
    val gameTitle: ResourceString? = null,
    val shareLink: String = "",
    val showShareLink: Boolean = false,
    val selectedTab: GameDetailTab = GameDetailTab.GAME,
    val selectedTabExtras: String? = null,
    val loadingState: LoadingState = LoadingState.INITIAL_LOADING
)

sealed interface GameHubViewEvent {
    object NavigateBack : GameHubViewEvent
}

private val emptyGameStatus = GameDetailUi.GameStatus.PregameStatus(
    scheduledDate = "-",
    scheduledTime = "-".asResourceString()
)

private val emptyTeamSummary = GameDetailUi.TeamSummary(
    teamId = "",
    legacyId = 0L,
    isFollowable = false,
    name = "".asResourceString(),
    logoUrls = emptyList(),
    score = null,
    isWinner = false,
    currentRecord = null,
    currentRanking = null
)