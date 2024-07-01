package com.theathletic.gamedetail.boxscore.ui.common

import com.theathletic.ui.UiModel

data class BoxScoreCommonTeamSwitcherUiModel(
    val id: String,
    val component: BoxScoreCommonRenderers.CommonRenderedComponent,
    val firstTeamName: String,
    val isFirstTeamSelected: Boolean,
    val secondTeamName: String,
) : UiModel {
    override val stableId = "BoxScoreCommonTeamSwitcher:$id-${component.name}"

    interface Interactor {
        fun onTeamSwitchClick(firstTeamClick: Boolean)
    }
}