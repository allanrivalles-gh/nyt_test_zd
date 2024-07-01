package com.theathletic.gamedetail.ui

import com.theathletic.boxscore.ui.GameDetailUi
import com.theathletic.boxscore.ui.TabModule
import com.theathletic.presenter.Interactor
import com.theathletic.scores.GameDetailTab
import com.theathletic.ui.ResourceString

interface GameDetailContract {

    interface Presenter :
        Interactor,
        GameDetailUi.Interactor

    data class ViewState(
        val toolbarLabel: ResourceString,
        val firstTeam: GameDetailUi.TeamSummary,
        val secondTeam: GameDetailUi.TeamSummary,
        val firstTeamStatus: List<GameDetailUi.TeamStatus>,
        val secondTeamStatus: List<GameDetailUi.TeamStatus>,
        val gameStatus: GameDetailUi.GameStatus,
        val gameInfo: GameDetailUi.GameInfo?,
        val tabItems: List<GameDetailUi.Tab>,
        val tabModules: List<TabModule>,
        val gameTitle: ResourceString?,
        val shareLink: String,
        val showShareLink: Boolean,
        val selectedTab: GameDetailTab,
    ) : com.theathletic.ui.ViewState
}