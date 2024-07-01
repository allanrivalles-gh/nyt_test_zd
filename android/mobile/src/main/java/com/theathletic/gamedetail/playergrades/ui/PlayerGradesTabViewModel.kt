package com.theathletic.gamedetail.playergrades.ui

import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.playergrades.PlayerGradeMiniCardModel
import com.theathletic.entity.main.Sport
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedInteractor
import com.theathletic.gamedetail.data.PlayerGradesRepository
import com.theathletic.gamedetail.data.PlayerGradesSubscriptionManager
import com.theathletic.gamedetail.data.local.GameStatus
import com.theathletic.gamedetail.data.local.GradeStatus
import com.theathletic.gamedetail.data.local.PlayerGradesLocalModel
import com.theathletic.gamedetail.ui.GameDetailEvent
import com.theathletic.gamedetail.ui.GameDetailEventConsumer
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.ComposeViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.LoadingState
import com.theathletic.ui.Transformer
import com.theathletic.ui.widgets.buttons.TwoItemToggleButtonModule
import com.theathletic.utility.coroutines.collectIn
import com.theathletic.utility.safeLet
import kotlinx.coroutines.launch
import timber.log.Timber

class PlayerGradesTabViewModel @AutoKoin constructor(
    @Assisted private val params: Params,
    private val playerGradesRepository: PlayerGradesRepository,
    private val subscriptionManager: PlayerGradesSubscriptionManager,
    private val filterPlayerGradesUseCase: FilterPlayerGradesUseCase,
    private val gameDetailEventConsumer: GameDetailEventConsumer,
    private val playerGradesAnalytics: PlayerGradesAnalyticsHandler,
    transformer: PlayerGradesTabTransformer
) : AthleticViewModel<PlayerGradesTabState, PlayerGradesTabContract.ViewState>(),
    ComposeViewModel,
    FeedInteractor,
    PlayerGradesAnalytics by playerGradesAnalytics,
    Transformer<PlayerGradesTabState, PlayerGradesTabContract.ViewState> by transformer {

    data class Params(
        val gameId: String,
        val sport: Sport,
        val leagueId: String,
        val isGameInProgress: Boolean
    )

    override val initialState by lazy {
        PlayerGradesTabState(
            loadingState = LoadingState.INITIAL_LOADING,
            gameId = params.gameId,
            sport = params.sport,
        )
    }

    override fun initialize() {
        super.initialize()
        trackPlayerGradesTabView()
        listenForPlayerGradeFlowUpdates()
        fetchPlayerGrades()

        gameDetailEventConsumer.collectIn(viewModelScope) {
            if (it == GameDetailEvent.PlayerGraded) fetchPlayerGrades(isRefresh = true)
        }
    }

    override fun dispose() {
        subscriptionManager.pause()
        super.dispose()
    }

    private fun listenForPlayerGradeFlowUpdates() {
        playerGradesRepository.observePlayerGrades(params.gameId).collectIn(viewModelScope) { data ->
            data?.let {
                updateState { copy(playerGrades = it.filterAndOrderPlayers()) }
                if (!state.isSubscribingToUpdates &&
                    it.gameStatus == GameStatus.IN_PROGRESS &&
                    it.gradeStatus == GradeStatus.ENABLED
                ) {
                    updateState { copy(isSubscribingToUpdates = true) }
                    subscriptionManager.subscribeForUpdates(params.gameId, params.sport)
                }
            }
        }
    }

    fun fetchPlayerGrades(isRefresh: Boolean = false) = viewModelScope.launch {
        if (isRefresh) updateState { copy(loadingState = LoadingState.RELOADING) }
        try {
            playerGradesRepository.fetchPlayerGrades(params.gameId, params.sport)
        } catch (exception: PlayerGradesRepository.PlayerGradesException) {
            Timber.e(exception)
        } finally {
            updateState { copy(loadingState = LoadingState.FINISHED) }
        }
    }

    private fun PlayerGradesLocalModel.filterAndOrderPlayers(): PlayerGradesLocalModel {
        val supportedAwayTeamPlayers = filterPlayerGradesUseCase(awayTeam?.players)
        val supportedHomeTeamPlayers = filterPlayerGradesUseCase(homeTeam?.players)
        return copy(
            awayTeam = awayTeam?.copy(players = supportedAwayTeamPlayers),
            homeTeam = homeTeam?.copy(players = supportedHomeTeamPlayers)
        )
    }

    override fun send(interaction: FeedInteraction) {
        when (interaction) {
            is TwoItemToggleButtonModule.Interaction.TwoItemToggleClick ->
                updateState { copy(isFirstTeamSelected = interaction.isFirstItemSelected) }

            is PlayerGradeMiniCardModel.Interaction.OnGradePlayer ->
                gradePlayer(interaction.playerId, interaction.grade)

            is PlayerGradeMiniCardModel.Interaction.OnNavigateToPlayerGradeDetailScreen ->
                onNavigationToPlayerGradesDetailScreen(interaction.playerId)

            else -> { /* Not Handled */ }
        }
    }

    private fun onNavigationToPlayerGradesDetailScreen(playerId: String) {
        sendEvent(
            PlayerGradesTabContract.Event.NavigateToPlayerGradesDetailScreen(
                gameId = params.gameId,
                playerId = playerId,
                sport = params.sport,
                leagueId = params.leagueId,
            )
        )
        if (state.isFirstTeamSelected) {
            state.playerGrades?.getFirstTeam(params.sport)?.id
        } else {
            state.playerGrades?.getSecondTeam(params.sport)?.id
        }?.let { teamId ->
            trackToGradePlayerDetailsClick(
                playerId = playerId,
                teamId = teamId,
                gameId = params.gameId,
                leagueId = params.leagueId,
                fromGradeTab = true
            )
        }
    }

    private fun gradePlayer(playerId: String, grade: Int) {
        val (currentPlayer, index) = if (state.isFirstTeamSelected) {
            val players = state.playerGrades?.getFirstTeam(params.sport)?.players
            val index = players?.indexOfFirst { it.playerId == playerId }
            Pair(players?.getOrNull(index ?: -1), index)
        } else {
            val players = state.playerGrades?.getSecondTeam(params.sport)?.players
            val index = players?.indexOfFirst { it.playerId == playerId }
            Pair(players?.getOrNull(index ?: -1), index)
        }
        safeLet(currentPlayer, index) { safePlayer, safeIndex ->
            safePlayer.grading?.let { grading ->
                updatePlayerGradeStars(grading.copy(grade = grade), safeIndex)
            }
            updateState {
                copy(
                    playersCurrentlySubmittingGrade = state.playersCurrentlySubmittingGrade + playerId
                )
            }

            viewModelScope.launch {
                playerGradesRepository.gradePlayer(
                    gameId = params.gameId,
                    isHomeTeam = if (params.sport.homeTeamFirst) {
                        state.isFirstTeamSelected
                    } else {
                        state.isFirstTeamSelected.not()
                    },
                    playerId = playerId,
                    grade = grade
                )
                    .onSuccess {
                        setGradingAsSubmitted(playerId)
                        trackPlayerBeingGraded(grade, playerId)
                    }
                    .onError {
                        fetchPlayerGrades()
                        setGradingAsSubmitted(playerId)
                    }
            }
        }
    }

    private fun setGradingAsSubmitted(playerId: String) {
        updateState {
            copy(playersCurrentlySubmittingGrade = state.playersCurrentlySubmittingGrade - playerId)
        }
    }

    private fun updatePlayerGradeStars(
        playerGrade: PlayerGradesLocalModel.Grading,
        index: Int
    ) {
        var firstTeam = state.playerGrades?.getFirstTeam(params.sport)
        var secondTeam = state.playerGrades?.getSecondTeam((params.sport))
        if (state.isFirstTeamSelected) {
            firstTeam?.players?.toMutableList()?.let { players ->
                val player = players[index]
                players[index] = player.copy(grading = playerGrade)
                firstTeam = firstTeam?.copy(players = players)
            }
        } else {
            secondTeam?.players?.toMutableList()?.let { players ->
                val player = players[index]
                players[index] = player.copy(grading = playerGrade)
                secondTeam = secondTeam?.copy(players = players)
            }
        }
        updateState {
            copy(
                playerGrades = state.playerGrades?.copy(
                    homeTeam = if (sport.homeTeamFirst) firstTeam else secondTeam,
                    awayTeam = if (sport.homeTeamFirst) secondTeam else firstTeam,
                )
            )
        }
    }

    private fun getSelectedTeamsId(): String {
        return if (state.isFirstTeamSelected) {
            state.playerGrades?.getFirstTeam(params.sport)?.id
        } else {
            state.playerGrades?.getSecondTeam(params.sport)?.id
        }.orEmpty()
    }

    private fun trackPlayerGradesTabView() {
        trackGradeTabScreenView(
            gameId = params.gameId,
            leagueId = params.leagueId,
            isGameInProgress = params.isGameInProgress
        )
    }

    private fun trackPlayerBeingGraded(
        grade: Int,
        playerId: String,
    ) {
        trackGradingPlayerClick(
            grade = grade.toString(),
            gameId = params.gameId,
            leagueId = params.leagueId,
            teamId = getSelectedTeamsId(),
            playerId = playerId,
            fromView = PlayerGradeSourceView.GRADE_TAB_LIST
        )
    }
}

data class PlayerGradesTabState(
    val loadingState: LoadingState,
    val gameId: String,
    val sport: Sport,
    val playerGrades: PlayerGradesLocalModel? = null,
    val isFirstTeamSelected: Boolean = true,
    val playersCurrentlySubmittingGrade: List<String> = mutableListOf(),
    val isSubscribingToUpdates: Boolean = false
) : DataState