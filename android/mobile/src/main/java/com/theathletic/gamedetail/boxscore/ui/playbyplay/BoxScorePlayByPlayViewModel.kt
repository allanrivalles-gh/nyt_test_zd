package com.theathletic.gamedetail.boxscore.ui.playbyplay

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.modules.AmericanFootballDriveModule
import com.theathletic.boxscore.ui.modules.BaseballPlayModule
import com.theathletic.boxscore.ui.modules.PlaysPeriodHeaderModule
import com.theathletic.boxscore.ui.modules.TwoItemToggleButtonModule
import com.theathletic.entity.main.Sport
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedInteractor
import com.theathletic.gamedetail.boxscore.ui.BoxScoreAnalytics
import com.theathletic.gamedetail.boxscore.ui.BoxScoreAnalyticsHandler
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.GameStatus
import com.theathletic.gamedetail.data.local.Period
import com.theathletic.gamedetail.data.local.PlayByPlayLocalModel
import com.theathletic.scores.data.ScoresRepository
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.ComposeViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.LoadingState
import com.theathletic.ui.Transformer
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.launch

class BoxScorePlayByPlayViewModel @AutoKoin constructor(
    @Assisted private val params: Params,
    private val scoresRepository: ScoresRepository,
    private val analyticsHandler: BoxScoreAnalyticsHandler,
    transformer: BoxScorePlayByPlayTransformer
) : AthleticViewModel<BoxScorePlayByPlayState, BoxScorePlayByPlayContract.ViewState>(),
    FeedInteractor,
    ComposeViewModel,
    DefaultLifecycleObserver,
    BoxScoreAnalytics by analyticsHandler,
    Transformer<BoxScorePlayByPlayState, BoxScorePlayByPlayContract.ViewState> by transformer {

    data class Params(
        val id: String,
        val sport: Sport,
        val leagueId: String
    )

    override val initialState by lazy {
        BoxScorePlayByPlayState(
            loadingState = LoadingState.INITIAL_LOADING,
            sport = params.sport,
            expandedPlays = emptyList()
        )
    }

    override fun initialize() {
        listenForRenderUpdates()
        fetchData()
    }

    // todo (Mark): Remove this when removing the FS also no longer required with new implementation
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        initialize()
    }

    private fun listenForRenderUpdates() {
        scoresRepository.getPlayByPlays(params.id)
            .collectIn(viewModelScope) {
                setInitialExpandedPeriod(it)
                updateState { copy(gamePlays = it) }
                if (!state.hasViewEventBeenSent) trackViewEvent(it)
                if (!state.isSubscribingToUpdates && it?.status == GameStatus.IN_PROGRESS) {
                    updateState { copy(isSubscribingToUpdates = true) }
                    startSubscriptionToPlayUpdates()
                }
            }
    }

    private fun startSubscriptionToPlayUpdates() {
        viewModelScope.launch {
            scoresRepository.subscribeForPlayUpdates(params.id, params.sport)
        }
    }

    fun fetchData(isRefresh: Boolean = false) = viewModelScope.launch {
        if (isRefresh) {
            updateState { copy(loadingState = LoadingState.RELOADING) }
        }
        scoresRepository.fetchPlayByPlays(params.id, params.sport)?.join()
        updateState { copy(loadingState = LoadingState.FINISHED) }
    }

    private fun onPeriodExpandClick(id: String) {
        val period = Period.valueOf(id)
        updateState {
            copy(
                hasExpandedPeriodBeenOverriddenByUser = true,
                currentExpandedPeriod = if (state.currentExpandedPeriod == period) null else period
            )
        }
    }

    private fun onPlaysViewOptionClick(firstItemSelected: Boolean) {
        updateState {
            copy(
                isFirstViewItemSelected = firstItemSelected
            )
        }
    }

    private fun onExpandPlayClick(id: String) {
        val expandedPlays = state.expandedPlays.toMutableList()
        if (expandedPlays.contains(id)) {
            expandedPlays.remove(id)
        } else {
            expandedPlays.add(id)
        }
        updateState { copy(expandedPlays = expandedPlays) }
    }

    override fun send(interaction: FeedInteraction) {
        when (interaction) {
            is PlaysPeriodHeaderModule.Interaction.OnPeriodExpandClick ->
                onPeriodExpandClick(interaction.id)
            is TwoItemToggleButtonModule.Interaction.TwoItemToggleClick -> {
                onPlaysViewOptionClick(interaction.isFirstItemSelected)

                state.gamePlays?.let { game ->
                    if (interaction.isFirstItemSelected) {
                        trackAllPlaysClicked(game.id, params.leagueId, game.status)
                    } else {
                        trackScoringPlaysClicked(game.id, params.leagueId, game.status)
                    }
                }
            }

            is BaseballPlayModule.Interaction.OnPlayExpandClick ->
                onExpandPlayClick(interaction.id)
            is AmericanFootballDriveModule.Interaction.OnDriveExpandClick ->
                onExpandPlayClick(interaction.id)
        }
    }

    private fun setInitialExpandedPeriod(gamePlays: PlayByPlayLocalModel?) {
        if (gamePlays == null || state.hasExpandedPeriodBeenOverriddenByUser) return
        when (state.sport) {
            Sport.BASKETBALL -> updateState {
                copy(
                    currentExpandedPeriod = gamePlays.plays
                        .filterIsInstance<GameDetailLocalModel.BasketballPlay>()
                        .firstOrNull()?.period
                )
            }
            Sport.HOCKEY -> updateState {
                copy(
                    currentExpandedPeriod = gamePlays.plays
                        .filterIsInstance<GameDetailLocalModel.HockeyPlay>()
                        .firstOrNull()?.period
                )
            }
            Sport.SOCCER -> updateState {
                copy(
                    currentExpandedPeriod = gamePlays.plays
                        .filterIsInstance<GameDetailLocalModel.SoccerPlay>()
                        .firstOrNull()?.period
                )
            }
            else -> { /* Do Nothing */
            }
        }
    }

    private fun trackViewEvent(game: PlayByPlayLocalModel?) {
        game ?: return
        trackBoxScorePlaysView(game.status, game.id, params.leagueId, "")
        updateState { copy(hasViewEventBeenSent = true) }
    }
}

data class BoxScorePlayByPlayState(
    val loadingState: LoadingState,
    val sport: Sport,
    val gamePlays: PlayByPlayLocalModel? = null,
    val isFirstViewItemSelected: Boolean = true,
    val currentExpandedPeriod: Period? = null,
    val expandedPlays: List<String>,
    val hasViewEventBeenSent: Boolean = false,
    val hasExpandedPeriodBeenOverriddenByUser: Boolean = false,
    val isSubscribingToUpdates: Boolean = false
) : DataState