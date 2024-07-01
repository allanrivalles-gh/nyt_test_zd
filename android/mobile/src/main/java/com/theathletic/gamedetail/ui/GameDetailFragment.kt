package com.theathletic.gamedetail.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.os.bundleOf
import com.theathletic.boxscore.ScrollToModule
import com.theathletic.boxscore.ui.GameDetailScreen
import com.theathletic.comments.ui.LocalCommentsTabSelected
import com.theathletic.fragment.AthleticComposeFragment
import com.theathletic.scores.GameDetailTab
import com.theathletic.scores.GameDetailTabParams
import com.theathletic.utility.getSerializableCompat
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

private const val EXTRA_GAME_ID = "extra_game_id"
private const val EXTRA_COMMENT_ID = "extra_comment_id"
private const val EXTRA_SELECTED_TAB_PARAMS = "extra_selected_tab_params"
private const val EXTRA_SOURCE = "extra_source"
private const val EXTRA_START_ON_SEASON_STATS = "extra_start_on_season_stats"

class GameDetailFragment : AthleticComposeFragment<
    GameDetailViewModel,
    GameDetailContract.ViewState
    >() {

    companion object {
        fun newInstance(
            gameId: String?,
            commentId: String?,
            selectedTabParams: GameDetailTabParams?,
            scrollToModule: ScrollToModule,
            view: String?,
        ) = GameDetailFragment().apply {
            arguments = bundleOf(
                EXTRA_GAME_ID to gameId,
                EXTRA_COMMENT_ID to commentId,
                EXTRA_SELECTED_TAB_PARAMS to selectedTabParams,
                EXTRA_SOURCE to view,
                EXTRA_START_ON_SEASON_STATS to scrollToModule
            )
        }
    }

    override fun setupViewModel() = getViewModel<GameDetailViewModel> {
        parametersOf(
            GameDetailViewModel.Params(
                gameId = arguments?.getString(EXTRA_GAME_ID) ?: "",
                commentId = arguments?.getString(EXTRA_COMMENT_ID) ?: "",
                selectedTab = arguments?.getParcelable(EXTRA_SELECTED_TAB_PARAMS)
                    ?: GameDetailTabParams(GameDetailTab.GAME, emptyMap()),
                scrollToModule = arguments?.getSerializableCompat(EXTRA_START_ON_SEASON_STATS) ?: ScrollToModule.NONE,
                view = arguments?.getString(EXTRA_SOURCE) ?: ""
            ),
            navigator
        )
    }

    @Composable
    override fun Compose(state: GameDetailContract.ViewState) {
        activity?.supportFragmentManager?.let { fragmentManager ->
            val isDiscussTabSelected = state.selectedTab == GameDetailTab.DISCUSS
            CompositionLocalProvider(LocalCommentsTabSelected provides isDiscussTabSelected) {
                GameDetailScreen(
                    title = state.toolbarLabel,
                    firstTeam = state.firstTeam,
                    secondTeam = state.secondTeam,
                    gameStatus = state.gameStatus,
                    firstTeamStatus = state.firstTeamStatus,
                    secondTeamStatus = state.secondTeamStatus,
                    shareLink = state.shareLink,
                    showShareLink = state.showShareLink,
                    tabs = state.tabItems,
                    tabModules = state.tabModules,
                    fragmentManager = { fragmentManager },
                    interactor = viewModel,
                    gameTitle = state.gameTitle,
                    gameInfo = state.gameInfo,
                    selectedTab = state.selectedTab,
                )
            }
        } ?: activity?.finish()
    }
}