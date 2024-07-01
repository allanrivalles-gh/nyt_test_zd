package com.theathletic.hub.team.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.modules.StandingsTableModule
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.FeedUiV2
import com.theathletic.scores.standings.ui.ScoresStandingsRenderer
import com.theathletic.ui.LoadingState
import com.theathletic.ui.Transformer

class TeamHubStandingsTransformer @AutoKoin constructor(
    private val scoresStandingsRenderer: ScoresStandingsRenderer
) :
    Transformer<TeamHubStandingsState, TeamHubStandingsContract.ViewState> {

    override fun transform(data: TeamHubStandingsState): TeamHubStandingsContract.ViewState {
        val standingsModules = scoresStandingsRenderer.renderStandingsModules(
            selectedGroupIndex = data.selectedGroupIndex,
            selectedGroupName = null,
            groupings = data.groupings,
            league = data.league,
            highlightedTeamId = data.highlightedTeamId,
            nonNavigableTeams = data.nonNavigableTeams,
            isStandingsFilteringEnabled = false
        )
        return TeamHubStandingsContract.ViewState(
            showSpinner = data.loadingState.isFreshLoadingState,
            showEmptyState = data.isStandingsAvailable.not(),
            feedUiModel = FeedUiV2(modules = standingsModules),
            initialIndex = standingsModules.getTeamIndex(),
            relegationLegendItemsV2 = scoresStandingsRenderer.renderRelegationLegendItemsV2(
                data.league,
                data.groupings
            ),
        )
    }

    private fun List<FeedModuleV2>.getTeamIndex(): Int {
        val index = indexOfFirst { module ->
            if (module is StandingsTableModule) {
                module.teamsColumn.any { it is StandingsTableModule.TeamColumnItem.Team && it.highlighted }
            } else {
                false
            }
        }
        return if (index == -1) 0 else index
    }

    private val TeamHubStandingsState.isStandingsAvailable: Boolean
        get() = loadingState != LoadingState.FINISHED || groupings.isNotEmpty()
}