package com.theathletic.hub.league.ui

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import com.theathletic.entity.main.League
import com.theathletic.feed.FeedType
import com.theathletic.fragment.AthleticComposeFragment
import com.theathletic.hub.HubTabType
import com.theathletic.hub.ui.LeagueHubScreen
import com.theathletic.profile.manage.UserTopicType
import com.theathletic.ui.observe
import com.theathletic.utility.getSerializableCompat
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

private const val EXTRA_FEED_TYPE = "extra_feed_type"
private const val EXTRA_INITIAL_TAB = "extra_initial_tab"

class LeagueHubFragment : AthleticComposeFragment<
    LeagueHubViewModel,
    LeagueHubContract.ViewState
    >() {

    companion object {
        fun newInstance(
            feedType: FeedType.League,
            initialTab: HubTabType,
        ) = LeagueHubFragment().apply {
            arguments = bundleOf(
                EXTRA_FEED_TYPE to feedType,
                EXTRA_INITIAL_TAB to initialTab
            )
        }
    }

    override fun setupViewModel() = getViewModel<LeagueHubViewModel> {
        val feedType =
            arguments?.getSerializableCompat(EXTRA_FEED_TYPE) ?: FeedType.League(League.UNKNOWN.leagueId)
        val hubTabType =
            arguments?.getSerializableCompat(EXTRA_INITIAL_TAB) ?: HubTabType.Home
        parametersOf(
            LeagueHubViewModel.Params(
                feedType = feedType,
                initialTab = hubTabType
            )
        )
    }

    @Composable
    override fun Compose(state: LeagueHubContract.ViewState) {
        activity?.supportFragmentManager?.let { fragmentManager ->
            LeagueHubScreen(
                leagueHub = state.leagueHub,
                interactor = viewModel,
                fragmentManager = { fragmentManager }
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apply {
            observe<LeagueHubContract.Event.NavigateClose>(viewLifecycleOwner) { navigator.finishActivity() }
            observe<LeagueHubContract.Event.NavigateToNotificationsSettings>(viewLifecycleOwner) {
                navigator.startUserTopicNotificationFragment(
                    entityId = it.legacyId,
                    type = UserTopicType.LEAGUE,
                    title = it.name
                )
            }
        }
    }
}