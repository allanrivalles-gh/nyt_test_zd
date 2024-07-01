package com.theathletic.gamedetail.boxscore.ui.injuryreport

import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import com.theathletic.boxscore.ui.InjuryReportScreen
import com.theathletic.fragment.AthleticComposeFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class BoxScoreInjuryReportFragment : AthleticComposeFragment<
    BoxScoreInjuryReportViewModel,
    BoxScoreInjuryReportContract.ViewState
    >() {

    companion object {
        private const val EXTRA_GAME_ID = "extra_game_id"
        private const val EXTRA_FIRST_TEAM_SELECTED = "extra_first_team_selected"

        fun newInstance(
            gameId: String?,
            isFirstTeamSelected: Boolean
        ) = BoxScoreInjuryReportFragment().apply {
            arguments = bundleOf(
                EXTRA_GAME_ID to gameId,
                EXTRA_FIRST_TEAM_SELECTED to isFirstTeamSelected
            )
        }
    }

    override fun setupViewModel() = getViewModel<BoxScoreInjuryReportViewModel> {
        parametersOf(
            BoxScoreInjuryReportViewModel.Params(
                id = arguments?.getString(EXTRA_GAME_ID) ?: "",
                isFirstTeamSelected = arguments?.getBoolean(EXTRA_FIRST_TEAM_SELECTED) ?: true
            ),
            navigator
        )
    }

    @Composable
    override fun Compose(state: BoxScoreInjuryReportContract.ViewState) {
        InjuryReportScreen(
            gameDetails = state.gameDetails,
            firstTeam = state.firstTeam,
            secondTeam = state.secondTeam,
            firstTeamInjuries = state.firstTeamInjuries,
            secondTeamInjuries = state.secondTeamInjuries,
            isFirstTeamSelected = state.isFirstTeamSelected,
            interactor = viewModel
        )
    }
}