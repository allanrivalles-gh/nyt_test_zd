package com.theathletic.attributionsurvey.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.attributionsurvey.data.local.AttributionSurvey
import com.theathletic.attributionsurvey.data.local.AttributionSurveyOption
import com.theathletic.attributionsurvey.data.local.createEmptySurvey
import com.theathletic.onboarding.data.OnboardingRepository
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.Transformer
import com.theathletic.utility.AttributionPreferences
import kotlinx.coroutines.launch
import timber.log.Timber

class SurveyViewModel @AutoKoin constructor(
    private val transformer: SurveyStateTransformer,
    private val onboardingRepository: OnboardingRepository,
    private val preferences: AttributionPreferences,
    private val analytics: Analytics,
    private val analyticsContext: SurveyAnalyticsContext
) : AthleticViewModel<SurveyState, SurveyContract.SurveyViewState>(),
    Transformer<SurveyState, SurveyContract.SurveyViewState> by transformer,
    SurveyContract.SurveyInteractor {
    override val initialState by lazy {
        SurveyState(createEmptySurvey())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun initialize() {
        val safeSurvey = onboardingRepository.getLocalSurvey()
        if (safeSurvey != null && !safeSurvey.isEmpty) {
            analytics.track(
                Event.AttributionSurvey.View(
                    getAnalyticsReferralId(),
                    getAnalyticsReferralType(),
                    analyticsContext.navigationSource
                )
            )
            preferences.hasSeenAttributionSurvey = true
            viewModelScope.launch {
                onboardingRepository.setHasSeenAttributionSurvey()
                    .onError { e -> Timber.e(e, "Error recording that user has seen attribution survey") }
            }
            updateState { copy(survey = safeSurvey) }
        } else {
            sendEvent(SurveyContract.Event.FinishEvent)
        }
    }

    override fun onEntryClick(index: Int) {
        updateState {
            copy(
                selectedEntryIndex = index
            )
        }
        val options = state.survey.surveyOptions
        if (index in options.indices) {
            analytics.track(
                Event.AttributionSurvey.SelectOption(
                    getAnalyticsReferralId(),
                    getAnalyticsReferralType(),
                    analyticsContext.navigationSource,
                    options[index].displayOrder.toString(),
                    options[index].remoteKey
                )
            )
        }
    }

    override fun onSubmitClick() {
        getSelectedSurveyOption()?.let { selectedEntry ->
            viewModelScope.launch {
                onboardingRepository.submitSurveySelection(
                    selectedEntry.remoteKey,
                    selectedEntry.displayOrder
                )
                    .onSuccess {
                        analytics.track(
                            Event.AttributionSurvey.Submit(
                                getAnalyticsReferralId(),
                                getAnalyticsReferralType(),
                                analyticsContext.navigationSource,
                                selectedEntry.displayOrder.toString(),
                                selectedEntry.remoteKey
                            )
                        )
                    }
                    .onError { e -> Timber.e(e, "Error saving survey results") }
            }
        }
        sendEvent(SurveyContract.Event.FinishEvent)
    }

    override fun onDismissClick() {
        analytics.track(
            Event.AttributionSurvey.Exit(
                getAnalyticsReferralId(),
                getAnalyticsReferralType(),
                analyticsContext.navigationSource
            )
        )
        sendEvent(SurveyContract.Event.FinishEvent)
    }

    private fun getAnalyticsReferralId(): String {
        val referralId = analyticsContext.referralObjectId
        return if (referralId > -1) referralId.toString() else ""
    }

    private fun getAnalyticsReferralType(): String {
        val referralId = analyticsContext.referralObjectId
        return if (referralId > -1) analyticsContext.referralObjectType else ""
    }

    private fun getSelectedSurveyOption(): AttributionSurveyOption? {
        return state.survey.surveyOptions.getOrNull(state.selectedEntryIndex)
    }
}

data class SurveyState(
    val survey: AttributionSurvey,
    val selectedEntryIndex: Int = -1
) : DataState