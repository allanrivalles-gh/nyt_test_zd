package com.theathletic.gamedetail.boxscore.ui

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

class BoxScoreStatsFragment :
    AthleticComposeFragment<
        BoxScoreStatsViewModel,
        BoxScoreStatsContract.ViewState
        >() {

    companion object {
        private const val EXTRA_GAME_ID = "extra_game_id"

        fun newInstance(
            gameId: String
        ): BoxScoreStatsFragment {
            return BoxScoreStatsFragment().apply {
                arguments = bundleOf(EXTRA_GAME_ID to gameId)
            }
        }
    }

    override fun setupViewModel() = getViewModel<BoxScoreStatsViewModel> {
        parametersOf(parameters)
    }

    private val parameters by lazy {
        BoxScoreStatsViewModel.Params(
            gameId = arguments?.getString(EXTRA_GAME_ID) ?: "",
            sport = Sport.UNKNOWN, // Not need for existing implementation
            isPostGame = false // Not need for existing implementation
        )
    }

    @Composable
    override fun Compose(state: BoxScoreStatsContract.ViewState) {
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
                onRefresh = { viewModel.onRefresh() }
            ) {
                Feed(
                    uiModel = state.feedUiModel,
                    isVisible = active,
                    listState = listState,
                    verticalSpacing = 0.dp,
                    onViewVisibilityChanged = { _, _ ->
                        // Impression tracking for Stats tab not supported
                    }
                )
            }
        }
    }
}