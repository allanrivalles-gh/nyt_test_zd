package com.theathletic.gamedetail.boxscore.ui.football.stats

import com.theathletic.gamedetail.data.local.StatisticCategory
import com.theathletic.ui.UiModel
import com.theathletic.ui.binding.ParameterizedString

data class BoxScoreStatsCategoryGroupUiModel(
    val id: String,
    val category: StatisticCategory,
    val players: List<UiModel>,
    val stats: List<UiModel>
) : UiModel {
    override val stableId = "BoxScoreStatsCategoryGroup:$id-${category.name}"
}

data class BoxScoreStatsPlayerRowUiModel(
    val id: String,
    val category: StatisticCategory,
    val name: String,
    val position: String
) : UiModel {
    override val stableId = "BoxScoreStatsPlayerRow:$id-${category.name}"
}

data class BoxScoreStatsCategoryHeaderUiModel(
    val id: String,
    val category: StatisticCategory,
    val label: ParameterizedString
) : UiModel {
    override val stableId = "BoxScoreStatsCategoryHeader:$id-${category.name}"
}

data class BoxScoreStatsValuesRowUiModel(
    val id: String,
    val category: StatisticCategory,
    val values: List<UiModel>
) : UiModel {
    override val stableId = "BoxScoreStatsValuesRow:$id-${category.name}"
}

data class BoxScoreStatsValuesRowHeaderItemUiModel(
    val id: String,
    val category: StatisticCategory,
    val index: Int,
    val value: String
) : UiModel {
    override val stableId = "BoxScoreStatsValuesRowHeaderItem:$id-${category.name}-$index"
}

data class BoxScoreStatsValuesRowItemUiModel(
    val id: String,
    val category: StatisticCategory,
    val index: Int,
    val value: String
) : UiModel {
    override val stableId = "BoxScoreStatsValuesRowItem:$id-${category.name}-$index"
}