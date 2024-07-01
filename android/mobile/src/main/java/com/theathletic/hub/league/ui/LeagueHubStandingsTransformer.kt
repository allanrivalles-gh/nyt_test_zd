package com.theathletic.hub.league.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.feed.ui.FeedUiV2
import com.theathletic.scores.standings.ui.ScoresStandingsRenderer
import com.theathletic.ui.LoadingState
import com.theathletic.ui.Transformer

class LeagueHubStandingsTransformer @AutoKoin constructor(
    private val scoresStandingsRenderer: ScoresStandingsRenderer
) :
    Transformer<LeagueHubStandingsState, LeagueHubStandingsContract.ViewState> {

    override fun transform(data: LeagueHubStandingsState): LeagueHubStandingsContract.ViewState {
        return LeagueHubStandingsContract.ViewState(
            showSpinner = data.loadingState.isFreshLoadingState,
            showEmptyState = data.isStandingsAvailable.not(),
            feedUiModel = FeedUiV2(
                modules = scoresStandingsRenderer.renderStandingsModules(
                    selectedGroupIndex = data.selectedGroupIndex,
                    selectedGroupName = data.selectedGroupName,
                    groupings = data.groupings,
                    league = data.league,
                    highlightedTeamId = null,
                    nonNavigableTeams = data.nonNavigableTeams,
                    isStandingsFilteringEnabled = data.isStandingsFilteringEnabled
                )
            ),
            relegationLegendItemsV2 = scoresStandingsRenderer.renderRelegationLegendItemsV2(
                data.league,
                data.groupings
            ),
        )
    }

    private val LeagueHubStandingsState.isStandingsAvailable: Boolean
        get() = loadingState != LoadingState.FINISHED || groupings.isNotEmpty()
}