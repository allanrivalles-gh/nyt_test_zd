package com.theathletic.rooms.create.ui

import androidx.compose.runtime.Composable
import com.theathletic.fragment.AthleticComposeFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class LiveRoomCategoriesFragment : AthleticComposeFragment<
    LiveRoomCategoriesViewModel,
    LiveRoomCategoriesContract.ViewState
    >() {

    companion object {
        fun newInstance() = LiveRoomCategoriesFragment()
    }

    override fun setupViewModel() = getViewModel<LiveRoomCategoriesViewModel> {
        parametersOf(navigator)
    }

    @Composable
    override fun Compose(state: LiveRoomCategoriesContract.ViewState) {
        LiveRoomCategoriesScreen(
            uiModel = LiveRoomCategoriesUi(categories = state.categories),
            interactor = viewModel,
        )
    }
}