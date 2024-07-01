package com.theathletic.hub.team.ui

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import com.theathletic.feed.FeedType
import com.theathletic.fragment.AthleticComposeFragment
import com.theathletic.hub.HubTabType
import com.theathletic.hub.ui.TeamHubScreen
import com.theathletic.profile.manage.UserTopicType
import com.theathletic.ui.observe
import com.theathletic.utility.getSerializableCompat
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

private const val EXTRA_FEED_TYPE = "extra_feed_type"
private const val EXTRA_INITIAL_TAB = "extra_initial_tab"

class TeamHubFragment : AthleticComposeFragment<
    TeamHubViewModel,
    TeamHubContract.ViewState
    >() {

    companion object {
        fun newInstance(
            feedType: FeedType.Team,
            initialTab: HubTabType,
        ) = TeamHubFragment().apply {
            arguments = bundleOf(
                EXTRA_FEED_TYPE to feedType,
                EXTRA_INITIAL_TAB to initialTab,
            )
        }
    }

    override fun setupViewModel() = getViewModel<TeamHubViewModel> {
        val feedType = arguments?.getSerializableCompat(EXTRA_FEED_TYPE) ?: FeedType.Team(-1)
        val initialTab = arguments?.getSerializableCompat(EXTRA_INITIAL_TAB) ?: HubTabType.Home
        parametersOf(
            TeamHubViewModel.Params(
                feedType = feedType,
                initialTab = initialTab
            ),
        )
    }

    @Composable
    override fun Compose(state: TeamHubContract.ViewState) {
        activity?.supportFragmentManager?.let { fragmentManager ->
            TeamHubScreen(
                teamHub = state.teamHub,
                interactor = viewModel,
                fragmentManager = { fragmentManager }
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apply {
            observe<TeamHubContract.Event.NavigateClose>(viewLifecycleOwner) { navigator.finishActivity() }
            observe<TeamHubContract.Event.NavigateToNotificationsSettings>(viewLifecycleOwner) {
                navigator.startUserTopicNotificationFragment(
                    entityId = it.legacyId,
                    type = UserTopicType.TEAM,
                    title = it.name
                )
            }
        }
    }
}