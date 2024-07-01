package com.theathletic.gamedetail.boxscore.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.modules.TwoItemToggleButtonModule
import com.theathletic.comments.game.AnalyticsGameTeamIdUseCase
import com.theathletic.entity.main.Sport
import com.theathletic.featureswitches.FeatureSwitches
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedInteractor
import com.theathletic.gamedetail.data.local.CoverageDataType
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.GameLineUpAndStats
import com.theathletic.scores.data.ScoresRepository
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.ComposeViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.LoadingState
import com.theathletic.ui.Transformer
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class BoxScoreStatsViewModel @AutoKoin constructor(
    @Assisted private val params: Params,
    private val repository: ScoresRepository,
    private val sorter: BoxScoreStatsSportSorter,
    private val analyticsHandler: BoxScoreAnalyticsHandler,
    private val featureSwitches: FeatureSwitches,
    private val analyticsBoxScoreTeamIdUseCase: AnalyticsGameTeamIdUseCase,
    transformer: BoxScoreStatsTransformer,
) : AthleticViewModel<BoxScoreStatsState, BoxScoreStatsContract.ViewState>(),
    BoxScoreStatsContract.Presenter,
    FeedInteractor,
    ComposeViewModel,
    DefaultLifecycleObserver,
    BoxScoreAnalytics by analyticsHandler,
    Transformer<BoxScoreStatsState, BoxScoreStatsContract.ViewState> by transformer {

    data class Params(
        val gameId: String,
        val sport: Sport,
        val isPostGame: Boolean
    )

    override val initialState by lazy {
        BoxScoreStatsState(
            loadingState = LoadingState.INITIAL_LOADING
        )
    }

    override fun initialize() {
        listenForRenderUpdates()
        fetchData()
    }

    // todo (Mark): Remove this when removing the FS also no longer required with new implementation
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        updateCurrentTeamId()
        listenForRenderUpdates()
    }

    private fun listenForRenderUpdates() {
        val gameFlow = repository.observeGame(params.gameId)
        val lineUpFlow = repository.getPlayerStats(params.gameId)
        gameFlow.combine(lineUpFlow) { game, lineUp ->
            state.copy(
                game = game,
                lineUpAndStats = lineUp
            )
        }.collectIn(viewModelScope) {
            updateState { it }
            state.lineUpAndStats?.let { stats -> sortPlayerStats(stats) }
            if (!state.hasViewEventBeenSent) logViewEvent(it.game)
            if (!state.isSubscribedToUpdates && !params.isPostGame) {
                updateState { copy(isSubscribedToUpdates = true) }
                viewModelScope.launch {
                    repository.subscribeToPlayerStatsUpdates(params.gameId, params.sport)
                }
            }
        }
    }

    private fun updateCurrentTeamId() {
        viewModelScope.launch {
            val currentTeamId = analyticsBoxScoreTeamIdUseCase(
                isTeamSpecificThreads = isTeamSpecific(),
                gameId = params.gameId
            )
            updateState {
                copy(currentTeamId = currentTeamId)
            }
        }
    }

    private fun isTeamSpecific() =
        state.game?.coverage?.contains(CoverageDataType.TEAM_SPECIFIC_COMMENTS) ?: false

    private fun fetchData() = viewModelScope.launch {
        listOfNotNull(
            repository.fetchGame(
                params.gameId,
                params.sport
            ),
            repository.fetchPlayerStats(
                params.gameId,
                params.sport,
                params.isPostGame
            )
        ).joinAll()
        updateState { copy(loadingState = LoadingState.FINISHED) }
    }

    private fun sortPlayerStats(stats: GameLineUpAndStats) {
        state.game?.sport?.let { sport ->
            sorter.get(sport)?.let { sorter ->
                if (state.firstTeamSelected) {
                    updateState {
                        copy(
                            firstTeamStats = sorter.sort(stats.firstTeamLineUp(sport))
                        )
                    }
                } else {
                    updateState {
                        copy(
                            secondTeamStats = sorter.sort(stats.secondTeamLineUp(sport))
                        )
                    }
                }
            }
        }
    }

    private fun sortPlayerStatsIfRequired(
        sport: Sport,
        stats: GameLineUpAndStats,
        firstTeamSelected: Boolean
    ) {
        sorter.get(sport)?.let { sorter ->
            if (firstTeamSelected && state.firstTeamStats == null) {
                updateState {
                    copy(firstTeamStats = sorter.sort(stats.firstTeamLineUp(sport)))
                }
            } else if (!firstTeamSelected && state.secondTeamStats == null) {
                updateState {
                    copy(secondTeamStats = sorter.sort(stats.secondTeamLineUp(sport)))
                }
            }
        }
    }

    fun onRefresh() {
        updateState { copy(loadingState = LoadingState.RELOADING) }
        fetchData()
    }

    override fun onTeamSwitchClick(firstTeamClick: Boolean) {
        state.game?.let { game ->
            state.lineUpAndStats?.let { lineUpAndStats ->
                sortPlayerStatsIfRequired(
                    game.sport,
                    lineUpAndStats,
                    firstTeamClick
                )
            }

            updateState {
                copy(firstTeamSelected = firstTeamClick)
            }

            val teamId = if (firstTeamClick) {
                game.firstTeam?.id.orEmpty()
            } else {
                game.secondTeam?.id.orEmpty()
            }
            trackTeamStatsToggleClick(
                status = game.status,
                gameId = game.id,
                leagueId = game.league.id,
                teamId = teamId
            )
        }
    }

    private fun logViewEvent(game: GameDetailLocalModel?) {
        game ?: return
        trackBoxScoreStatsView(
            status = game.status,
            gameId = game.id,
            leagueId = game.league.id,
            teamId = state.currentTeamId
        )
        updateState { copy(hasViewEventBeenSent = true) }
    }

    override fun send(interaction: FeedInteraction) {
        when (interaction) {
            is TwoItemToggleButtonModule.Interaction.TwoItemToggleClick ->
                onTeamSwitchClick(interaction.isFirstItemSelected)
        }
    }
}

data class BoxScoreStatsState(
    val loadingState: LoadingState,
    val game: GameDetailLocalModel? = null,
    val lineUpAndStats: GameLineUpAndStats? = null,
    val firstTeamStats: List<BoxScoreStatistics>? = null,
    val secondTeamStats: List<BoxScoreStatistics>? = null,
    val firstTeamSelected: Boolean = true,
    val hasViewEventBeenSent: Boolean = false,
    val isSubscribedToUpdates: Boolean = false,
    val currentTeamId: String? = null
) : DataState