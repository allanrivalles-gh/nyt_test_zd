package com.theathletic.gamedetail.playergrades.ui

import com.theathletic.entity.main.Sport
import com.theathletic.feed.ui.FeedUiV2

interface PlayerGradesTabContract {

    data class ViewState(
        val showSpinner: Boolean,
        val feed: FeedUiV2,
    ) : com.theathletic.ui.ViewState

    sealed class Event : com.theathletic.utility.Event() {
        data class NavigateToPlayerGradesDetailScreen(
            val gameId: String,
            val playerId: String,
            val sport: Sport,
            val leagueId: String,
        ) : Event()
    }
}