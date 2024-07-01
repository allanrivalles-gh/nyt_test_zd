package com.theathletic.onboarding.ui

import com.theathletic.ui.ViewState

interface OnboardingContract {

    interface Interactor : OnboardingUi.Interactor

    data class OnboardingViewState(
        val isLoading: Boolean,
        val onboardingStep: OnboardingUi.OnboardingStep,
        val selectedTeamsGroupIndex: Int,
        val teamsGroups: List<OnboardingUi.OnboardingTeamsGroup>,
        val searchItems: List<OnboardingUi.OnboardingItem>,
        val chosenItems: List<OnboardingUi.OnboardingItem>,
        val searchText: String,
        val errorState: OnboardingUi.ErrorState?
    ) : ViewState
}