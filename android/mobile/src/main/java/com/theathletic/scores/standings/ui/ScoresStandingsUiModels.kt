package com.theathletic.scores.standings.ui

import androidx.annotation.ColorRes
import com.theathletic.data.SizedImages
import com.theathletic.ui.UiModel
import com.theathletic.ui.binding.ParameterizedString

data class ScoresStandingsGroupTitleUiModel(
    val id: String,
    val title: ParameterizedString,
    val selected: Boolean,
    val index: Int
) : UiModel {
    override val stableId = "ScoresStandingsGroupTitle:$id"

    interface Interactor {
        fun onGroupClick(index: Int)
    }
}

data class ScoresStandingsGroupUiModel(
    val id: String,
    val title: String,
    val rankAndTeamsList: List<UiModel>,
    val standingsList: List<UiModel>
) : UiModel {
    override val stableId = "ScoresStandingsGroupUiModel:$id"
}

data class ScoresStandingsRankAndTeamHeaderUiModel(
    val id: String,
    val label: ParameterizedString
) : UiModel {
    override val stableId = "ScoresStandingsRankAndTeamHeaderUiModel:$id"
}

data class ScoresStandingsRankAndTeamUiModel(
    val id: String,
    @ColorRes val relegationColor: Int,
    val rank: String,
    val teamId: String,
    val teamName: String,
    val isFollowable: Boolean = true,
    val logoUrlList: SizedImages?,
    val teamDisplayName: String,
    val showRank: Boolean,
    val showRelegation: Boolean,
    val ncaaRanking: String?,
    val showNcaaRanking: Boolean,
    val showHighlighted: Boolean,
    val showSolidPlayoffDivider: Boolean,
    val showDottedPlayoffDivider: Boolean,
    override val analyticsPayload: ScoresStandingsRowUiModel.AnalyticsPayload
) : UiModel, ScoresStandingsRowUiModel {
    override val stableId = "ScoresStandingsRankAndTeamUiModel:$id"
}

data class ScoreStandingsStatsHeaderUiModel(
    val id: String,
    val labels: List<String>,
    val extraColWidth: List<Boolean>
) : UiModel {
    override val stableId = "ScoreStandingsStatsHeaderUiModel:$id"
}

data class ScoreStandingsStatsRowUiModel(
    val id: String,
    val labels: List<StandingsLabelModel>,
    val teamId: String,
    val teamDisplayName: String,
    val showHighlighted: Boolean,
    val showSolidPlayoffDivider: Boolean,
    val showDottedPlayoffDivider: Boolean,
    val extraColWidth: List<Boolean>,
    override val analyticsPayload: ScoresStandingsRowUiModel.AnalyticsPayload
) : UiModel, ScoresStandingsRowUiModel {
    override val stableId = "ScoreStandingsStatsRowUiModel:$id"
}

interface ScoresStandingsRowUiModel {
    val analyticsPayload: AnalyticsPayload

    data class AnalyticsPayload(
        val teamId: String,
        val pageId: String,
        val index: Int
    )

    interface Interactor {
        fun onTeamRowClick(teamId: String, teamDisplayName: String, payload: AnalyticsPayload)
    }
}

data class ScoresStandingsRelegationLegendUiModel(
    @ColorRes val itemColorRes: Int,
    val itemLabel: ParameterizedString
) : UiModel {
    override val stableId = "ScoresStandingsRelegationLegend:$itemColorRes"
}

data class StandingsLabelModel(
    val labelText: String,
    @ColorRes val labelColour: Int
)