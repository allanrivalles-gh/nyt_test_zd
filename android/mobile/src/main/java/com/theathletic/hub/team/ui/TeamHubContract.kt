package com.theathletic.hub.team.ui

import com.theathletic.hub.ui.HubUi

interface TeamHubContract {

    interface Interaction :
        com.theathletic.presenter.Interactor,
        HubUi.Interactor

    data class ViewState(
        val teamHub: HubUi.Team,
    ) : com.theathletic.ui.ViewState

    sealed class Event : com.theathletic.utility.Event() {
        object NavigateClose : Event()
        data class NavigateToNotificationsSettings(val legacyId: Long, val name: String) : Event()
    }
}