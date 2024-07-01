package com.theathletic.boxscore.ui

import com.theathletic.boxscore.data.local.SectionType
import com.theathletic.feed.compose.ui.LayoutUiModel

interface ModuleHeaderUiModel {
    val id: String
}

interface BoxScoreModuleUiModel {
    val id: String
    val header: ModuleHeaderUiModel?
}

data class BoxScoreUiModel(val sections: List<SectionsUiModel>) {

    data class SectionsUiModel(
        val id: String,
        val type: SectionType,
        val modules: List<BoxScoreModuleUiModel>
    )

    data class LatestNewsUiModel(
        override val id: String,
        override val header: BasicHeaderUiModel?,
        val blocks: List<LayoutUiModel.Item>
    ) : BoxScoreModuleUiModel

    data class BasicHeaderUiModel(
        override val id: String,
        val title: String
    ) : ModuleHeaderUiModel
}