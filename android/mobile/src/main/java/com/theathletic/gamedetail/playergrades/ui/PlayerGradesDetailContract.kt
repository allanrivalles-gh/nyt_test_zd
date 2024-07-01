package com.theathletic.gamedetail.playergrades.ui

import com.theathletic.boxscore.ui.playergrades.PlayerGradesDetailUi

interface PlayerGradesDetailContract {

    data class ViewState(
        val showSpinner: Boolean,
        val uiModel: PlayerGradesDetailUi?
    ) : com.theathletic.ui.ViewState

    sealed class Event : com.theathletic.utility.Event() {
        object NavigateClose : Event()
    }
}