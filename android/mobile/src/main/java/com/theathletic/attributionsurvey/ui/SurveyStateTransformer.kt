package com.theathletic.attributionsurvey.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.ui.Transformer

class SurveyStateTransformer @AutoKoin constructor() :
    Transformer<SurveyState, SurveyContract.SurveyViewState> {
    override fun transform(data: SurveyState): SurveyContract.SurveyViewState {
        val header = SurveyHeaderUiModel(
            data.survey.headerText,
            data.survey.subheaderText
        )
        val listItems = listOf(header) + data.survey.surveyOptions
            .sortedBy { it.displayOrder }
            .mapIndexed { index, option ->
                SurveyEntryUiModel(
                    option.displayName,
                    index == data.selectedEntryIndex,
                    index
                )
            }

        return with(data.survey) {
            SurveyContract.SurveyViewState(
                this.ctaText,
                data.selectedEntryIndex >= 0,
                listItems
            )
        }
    }
}