package com.theathletic.attributionsurvey.data.local

import com.theathletic.data.LocalModel

data class AttributionSurvey(
    val headerText: String,
    val subheaderText: String,
    val ctaText: String,
    val surveyOptions: List<AttributionSurveyOption>
) : LocalModel {
    val isEmpty: Boolean
        get() = surveyOptions.isEmpty()
}

data class AttributionSurveyOption(
    val displayName: String,
    val remoteKey: String,
    val displayOrder: Int
)

fun createEmptySurvey(): AttributionSurvey {
    val options = emptyList<AttributionSurveyOption>()
    return AttributionSurvey(
        "...",
        "...",
        "...",
        options
    )
}