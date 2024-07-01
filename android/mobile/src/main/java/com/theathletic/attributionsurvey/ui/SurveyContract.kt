package com.theathletic.attributionsurvey.ui

import com.theathletic.presenter.Interactor
import com.theathletic.ui.UiModel
import com.theathletic.ui.ViewState

interface SurveyContract {

    interface SurveyInteractor : Interactor {
        fun onSubmitClick()
        fun onDismissClick()
        fun onEntryClick(index: Int)
    }

    data class SurveyViewState(
        val cta: String = "",
        val ctaEnabled: Boolean = false,
        val listModels: List<UiModel> = emptyList()
    ) : ViewState

    sealed class Event : com.theathletic.utility.Event() {
        object FinishEvent : Event()
    }
}