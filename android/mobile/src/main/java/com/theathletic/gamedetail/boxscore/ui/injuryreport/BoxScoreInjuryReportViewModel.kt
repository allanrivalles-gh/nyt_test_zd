package com.theathletic.gamedetail.boxscore.ui.injuryreport

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.gamedetail.boxscore.ui.injuryreport.BoxScoreInjuryReportContract.ViewState
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.scores.data.ScoresRepository
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.Transformer
import com.theathletic.utility.coroutines.collectIn

class BoxScoreInjuryReportViewModel @AutoKoin constructor(
    @Assisted private val params: Params,
    @Assisted private val navigator: ScreenNavigator,
    private val repository: ScoresRepository,
    transformer: BoxScoreInjuryReportTransformer
) : AthleticViewModel<BoxScoreInjuryReportState, ViewState>(),
    BoxScoreInjuryReportContract.Presenter,
    Transformer<BoxScoreInjuryReportState, ViewState> by transformer {

    data class Params(
        val id: String,
        val isFirstTeamSelected: Boolean
    )

    override val initialState by lazy {
        BoxScoreInjuryReportState(
            isFirstTeamSelected = params.isFirstTeamSelected
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun initialize() {
        repository.observeGame(params.id).collectIn(viewModelScope) { game ->
            updateState { copy(game = game) }
        }
    }

    override fun onBackButtonClicked() {
        navigator.finishActivity()
    }

    override fun onTeamSelected(firstTeamSelected: Boolean) {
        updateState { copy(isFirstTeamSelected = firstTeamSelected) }
    }
}

data class BoxScoreInjuryReportState(
    val game: GameDetailLocalModel? = null,
    val isFirstTeamSelected: Boolean = true
) : DataState