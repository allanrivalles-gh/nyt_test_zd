package com.theathletic.profile.following

import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import com.theathletic.fragment.AthleticComposeFragment
import com.theathletic.profile.manage.UserTopicId
import com.theathletic.profile.ui.ManageFollowingScreen
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class ManageFollowingFragment :
    AthleticComposeFragment<ManageFollowingViewModel, ManageFollowingContract.FollowingViewState>() {

    companion object {
        private const val ARG_TOPIC_ID = "topic_id"

        fun newInstance(
            autoFollowId: UserTopicId? = null
        ) = ManageFollowingFragment().apply {
            arguments = bundleOf(ARG_TOPIC_ID to autoFollowId)
        }
    }

    override fun setupViewModel() = getViewModel<ManageFollowingViewModel> {
        parametersOf(
            ManageFollowingViewModel.Params(
                view = "profile",
                autoFollowId = arguments?.getSerializable(ARG_TOPIC_ID) as? UserTopicId?
            ),
            navigator
        )
    }

    @Composable
    override fun Compose(state: ManageFollowingContract.FollowingViewState) {
        ManageFollowingScreen(
            followableItems = state.followingItems,
            viewMode = state.viewMode,
            showBackButton = true,
            interactor = viewModel
        )
    }
}