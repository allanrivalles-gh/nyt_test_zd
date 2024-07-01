package com.theathletic.profile.addfollowing

import androidx.compose.runtime.Composable
import com.theathletic.fragment.AthleticComposeFragment
import com.theathletic.profile.ui.AddFollowingScreen
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class AddFollowingFragment :
    AthleticComposeFragment<AddFollowingViewModel, AddFollowingContract.AddFollowingViewState>() {

    companion object {
        fun newInstance() = AddFollowingFragment()
    }

    override fun setupViewModel() = getViewModel<AddFollowingViewModel> { parametersOf(navigator) }

    @Composable
    override fun Compose(state: AddFollowingContract.AddFollowingViewState) {
        AddFollowingScreen(
            isLoading = state.isLoading,
            searchText = state.searchText,
            addedItems = state.addedItems,
            suggestedItems = state.searchableItems,
            interactor = viewModel
        )
    }
}