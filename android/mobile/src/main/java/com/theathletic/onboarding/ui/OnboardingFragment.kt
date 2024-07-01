package com.theathletic.onboarding.ui

import androidx.compose.runtime.Composable
import com.theathletic.fragment.AthleticComposeFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class OnboardingFragment : AthleticComposeFragment<OnboardingViewModel, OnboardingContract.OnboardingViewState>() {

    companion object {
        fun newInstance() = OnboardingFragment()
    }

    override fun setupViewModel() = getViewModel<OnboardingViewModel> {
        parametersOf(navigator)
    }

    @Composable
    override fun Compose(state: OnboardingContract.OnboardingViewState) {
        OnboardingScreen(
            isLoading = state.isLoading,
            onboardingStep = state.onboardingStep,
            selectedTeamGroupIndex = state.selectedTeamsGroupIndex,
            teamsGroups = state.teamsGroups,
            searchItems = state.searchItems,
            followedItems = state.chosenItems,
            searchText = state.searchText,
            errorState = state.errorState,
            interactor = viewModel
        )
    }

    override fun onBackPressed(): Boolean {
        viewModel.onBackClick()
        return true
    }
}