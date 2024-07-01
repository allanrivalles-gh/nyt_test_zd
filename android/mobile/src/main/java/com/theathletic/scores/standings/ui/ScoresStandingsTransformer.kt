package com.theathletic.scores.standings.ui

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.extension.toStringOrEmpty
import com.theathletic.feed.ui.FeedUiV2
import com.theathletic.ui.LoadingState
import com.theathletic.ui.Transformer
import com.theathletic.ui.binding.ParameterizedString
import com.theathletic.utility.LogoUtility

class ScoresStandingsTransformer @AutoKoin constructor(
    private val scoresStandingsRenderer: ScoresStandingsRenderer
) : Transformer<ScoresStandingsState, ScoresStandingsContract.ViewState> {

    override fun transform(data: ScoresStandingsState): ScoresStandingsContract.ViewState {
        return ScoresStandingsContract.ViewState(
            showSpinner = data.loadingState.isFreshLoadingState,
            title = data.leagueLabel.toStringOrEmpty(),
            logoUrl = LogoUtility.getColoredLeagueLogoPath(data.league.leagueId),
            seasonLabel = data.seasonName,
            groupsTitleList = scoresStandingsRenderer.renderGroupingsList(
                data.standings.groupings,
                data.selectedGroupIndex
            ),
            standingsGroupList = scoresStandingsRenderer.renderStandingsGroup(
                data.standings.groupings,
                data.selectedGroupIndex,
                data.league,
                data.highlightedTeamId
            ),
            relegationLegendItems = scoresStandingsRenderer.renderRelegationLegendItems(
                data.standings.groupings
            ),
            autoNavigationIndex = if (data.selectedGroupIndex > -1) data.selectedGroupIndex else 0,
            feedUiModel = FeedUiV2(
                modules = scoresStandingsRenderer.renderStandingsModules(
                    selectedGroupIndex = data.selectedGroupIndex,
                    selectedGroupName = null,
                    groupings = data.standings.groupings,
                    league = data.league,
                    highlightedTeamId = data.highlightedTeamId,
                    nonNavigableTeams = data.nonNavigableTeams,
                    isStandingsFilteringEnabled = false
                )
            ),
            relegationLegendItemsV2 = scoresStandingsRenderer.renderRelegationLegendItemsV2(
                data.league,
                data.standings.groupings
            ),
            showEmptyState = data.isStandingsAvailable.not()
        )
    }

    private val ScoresStandingsState.seasonName: ParameterizedString?
        get() = when {
            standings.seasonName.isEmpty() -> null
            else -> ParameterizedString(R.string.scores_standings_season_label, standings.seasonName)
        }

    private val ScoresStandingsState.isStandingsAvailable: Boolean
        get() = loadingState != LoadingState.FINISHED || standings.groupings.isNotEmpty()
}