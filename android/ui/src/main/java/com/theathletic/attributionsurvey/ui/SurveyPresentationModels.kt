package com.theathletic.attributionsurvey.ui

import com.theathletic.ui.UiModel

data class SurveyEntryUiModel(
    val title: String,
    val isSelected: Boolean = false,
    val index: Int
) : UiModel {
    override val stableId = title
}

data class SurveyHeaderUiModel(
    val title: String,
    val subtitle: String
) : UiModel {
    override val stableId = title
}