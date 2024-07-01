package com.theathletic.gamedetail.boxscore.ui.playbyplay

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.theathletic.entity.main.Sport
import com.theathletic.feed.ui.Feed
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.fragment.AthleticComposeFragment
import com.theathletic.ui.widgets.SwipeRefreshIndicator
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class BoxScorePlayByPlayFragment : AthleticComposeFragment<
    BoxScorePlayByPlayViewModel,
    BoxScorePlayByPlayContract.ViewState
    >() {

    companion object {
        private const val EXTRA_GAME_ID = "extra_game_id"
        private const val EXTRA_SPORT = "extra_sport"

        fun newInstance(
            gameId: String,
            sport: Sport
        ) = BoxScorePlayByPlayFragment().apply {
            arguments = bundleOf(
                EXTRA_GAME_ID to gameId,
                EXTRA_SPORT to sport
            )
        }
    }

    override fun setupViewModel() = getViewModel<BoxScorePlayByPlayViewModel> {
        parametersOf(
            BoxScorePlayByPlayViewModel.Params(
                id = arguments?.getString(EXTRA_GAME_ID) ?: "",
                sport = arguments?.getSerializable(EXTRA_SPORT) as? Sport ?: Sport.UNKNOWN,
                leagueId = ""
            )
        )
    }

    @Composable
    override fun Compose(state: BoxScorePlayByPlayContract.ViewState) {
        val active by isResumed.collectAsState(initial = false)
        val listState = rememberLazyListState()

        CompositionLocalProvider(
            LocalFeedInteractor provides viewModel,
        ) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = state.showSpinner),
                indicator = { state, triggerDp ->
                    SwipeRefreshIndicator(
                        state = state,
                        refreshTriggerDistance = triggerDp
                    )
                },
                onRefresh = { viewModel.fetchData(isRefresh = true) }
            ) {
                Feed(
                    uiModel = state.feedUiModel,
                    isVisible = active,
                    listState = listState,
                    onViewVisibilityChanged = { _, _ -> /* Not required */ },
                    verticalSpacing = 0.dp
                )
            }
        }
    }
}