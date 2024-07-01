package com.theathletic.hub.league.ui

import com.theathletic.hub.ui.HubUi

interface LeagueHubContract {

    interface Interaction :
        com.theathletic.presenter.Interactor,
        HubUi.Interactor

    data class ViewState(
        val leagueHub: HubUi.League,
    ) : com.theathletic.ui.ViewState

    sealed class Event : com.theathletic.utility.Event() {
        object NavigateClose : Event()
        data class NavigateToNotificationsSettings(val legacyId: Long, val name: String) : Event()
    }
}