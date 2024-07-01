package com.theathletic.gamedetail.playergrades.ui

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.os.bundleOf
import com.theathletic.boxscore.ui.playergrades.LocalPlayerGradesInteractor
import com.theathletic.boxscore.ui.playergrades.PlayerGradesDetailScreen
import com.theathletic.entity.main.Sport
import com.theathletic.fragment.AthleticComposeFragment
import com.theathletic.ui.observe
import com.theathletic.utility.getSerializableCompat
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

private const val EXTRA_GAME_ID = "extra_game_id"
private const val EXTRA_PLAYER_ID = "extra_player_id"
private const val EXTRA_SPORT = "extra_sport"
private const val EXTRA_LEAGUE = "extra_league"
private const val EXTRA_GRADES_TAB = "extra_from_grades_tab"

class PlayerGradesDetailFragment : AthleticComposeFragment<
    PlayerGradesDetailViewModel,
    PlayerGradesDetailContract.ViewState
    >() {

    companion object {
        fun newInstance(
            gameId: String,
            playerId: String,
            sport: Sport,
            leagueId: String,
            launchedFromGradesTab: Boolean,
        ) = PlayerGradesDetailFragment().apply {
            arguments = bundleOf(
                EXTRA_GAME_ID to gameId,
                EXTRA_PLAYER_ID to playerId,
                EXTRA_SPORT to sport,
                EXTRA_LEAGUE to leagueId,
                EXTRA_GRADES_TAB to launchedFromGradesTab
            )
        }
    }

    override fun setupViewModel() = getViewModel<PlayerGradesDetailViewModel> {
        parametersOf(
            PlayerGradesDetailViewModel.Params(
                gameId = arguments?.getString(EXTRA_GAME_ID).orEmpty(),
                playerId = arguments?.getString(EXTRA_PLAYER_ID).orEmpty(),
                sport = arguments?.getSerializableCompat(EXTRA_SPORT) ?: Sport.UNKNOWN,
                leagueId = arguments?.getString(EXTRA_LEAGUE).orEmpty(),
                launchedFromGradesTab = arguments?.getBoolean(EXTRA_GRADES_TAB) ?: false
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apply {
            observe<PlayerGradesDetailContract.Event.NavigateClose>(viewLifecycleOwner) { navigator.finishActivity() }
        }
    }

    @Composable
    override fun Compose(state: PlayerGradesDetailContract.ViewState) {
        CompositionLocalProvider(
            LocalPlayerGradesInteractor provides viewModel
        ) {
            PlayerGradesDetailScreen(
                showSpinner = state.showSpinner,
                uiModel = state.uiModel,
            )
        }
    }
}