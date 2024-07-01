package com.theathletic.gamedetail.playergrades.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.R
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.playergrades.PlayerGradesDetailUi
import com.theathletic.boxscore.ui.playergrades.PlayerGradesInteraction
import com.theathletic.boxscore.ui.playergrades.PlayerGradesInteractor
import com.theathletic.entity.main.Sport
import com.theathletic.event.SnackbarEventRes
import com.theathletic.gamedetail.data.PlayerGradesRepository
import com.theathletic.gamedetail.data.local.PlayerGradesLocalModel
import com.theathletic.gamedetail.ui.GameDetailEvent
import com.theathletic.gamedetail.ui.GameDetailEventProducer
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.ComposeViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.LoadingState
import com.theathletic.ui.Transformer
import com.theathletic.utility.PhoneVibrator
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.launch
import timber.log.Timber

class PlayerGradesDetailViewModel @AutoKoin constructor(
    @Assisted private val params: Params,
    private val playerGradesRepository: PlayerGradesRepository,
    private val filterPlayerGradesUseCase: FilterPlayerGradesUseCase,
    private val gameDetailEventProducer: GameDetailEventProducer,
    private val phoneVibrator: PhoneVibrator,
    private val playerGradesAnalytics: PlayerGradesAnalyticsHandler,
    transformer: PlayerGradesDetailTransformer
) : AthleticViewModel<PlayerGradesDetailState, PlayerGradesDetailContract.ViewState>(),
    ComposeViewModel,
    DefaultLifecycleObserver,
    PlayerGradesInteractor,
    PlayerGradesAnalytics by playerGradesAnalytics,
    Transformer<PlayerGradesDetailState, PlayerGradesDetailContract.ViewState> by transformer {

    data class Params(
        val gameId: String,
        val playerId: String,
        val sport: Sport,
        val leagueId: String,
        val launchedFromGradesTab: Boolean,
    )

    override val initialState by lazy {
        PlayerGradesDetailState(
            loadingState = LoadingState.INITIAL_LOADING,
            sport = params.sport,
        )
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        initialize()
    }

    override fun initialize() {
        listenForPlayerGradeFlowUpdates()
        fetchPlayerGrades()
    }

    private fun listenForPlayerGradeFlowUpdates() {
        playerGradesRepository.observePlayerGrades(params.gameId).collectIn(viewModelScope) { data ->
            data?.let {
                val latestModel = it.filterAndOrderPlayers()
                if (state.initialPlayerSet.not()) {
                    val (index, isHomeTeam) = latestModel.findSelectedPlayer(params.playerId)
                    updateState {
                        copy(
                            playerGrades = latestModel,
                            initialPlayerSet = true,
                            isHomeTeam = isHomeTeam,
                            playerIndex = index
                        )
                    }
                    val teamId = if (isHomeTeam) it.homeTeam?.id else it.awayTeam?.id
                    trackPlayerGradesDetailView(teamId.orEmpty())
                } else {
                    updateState { copy(playerGrades = latestModel) }
                }
            }
        }
    }

    // todo: Adil handle the exceptions
    private fun fetchPlayerGrades() = viewModelScope.launch {
        try {
            playerGradesRepository.fetchPlayerGrades(params.gameId, params.sport)
        } catch (exception: PlayerGradesRepository.PlayerGradesException) {
            Timber.e(exception)
        } finally {
            updateState { copy(loadingState = LoadingState.FINISHED) }
        }
    }

    // Only show players with statistics
    private fun PlayerGradesLocalModel.filterAndOrderPlayers(): PlayerGradesLocalModel {
        val supportedAwayTeamPlayers = filterPlayerGradesUseCase(awayTeam?.players)
        val supportedHomeTeamPlayers = filterPlayerGradesUseCase(homeTeam?.players)
        return copy(
            awayTeam = awayTeam?.copy(players = supportedAwayTeamPlayers),
            homeTeam = homeTeam?.copy(players = supportedHomeTeamPlayers)
        )
    }

    private fun PlayerGradesLocalModel.findSelectedPlayer(playerId: String): Pair<Int, Boolean> {
        var isHomeTeam = false
        var index = awayTeam?.players?.indexOfFirst { it.playerId == playerId } ?: -1
        if (index == -1) {
            index = homeTeam?.players?.indexOfFirst { it.playerId == playerId } ?: -1
            if (index == -1) index = 0
            isHomeTeam = true
        }
        return Pair(index, isHomeTeam)
    }

    override fun send(interaction: PlayerGradesInteraction) {
        when (interaction) {
            is PlayerGradesDetailUi.Interactor.OnCloseButtonClick -> navigateBack()
            is PlayerGradesDetailUi.Interactor.OnGradingPlayer -> gradePlayer(interaction.grade)
            is PlayerGradesDetailUi.Interactor.OnShowAllPlayersClick -> navigateToGradesTab()
            is PlayerGradesDetailUi.Interactor.OnPlayerIndexChanged ->
                updateCurrentPlayer(interaction.index, interaction.toNext, interaction.viaClick)
        }
    }

    private fun updateCurrentPlayer(index: Int, toNext: Boolean, viaClick: Boolean) {
        updateState { copy(playerIndex = index) }
        trackPlayerGradeFlowNavigation(
            action = getAnalyticsNavigationAction(toNext, viaClick),
            gameId = params.gameId,
            leagueId = params.leagueId,
            fromGradeTab = params.launchedFromGradesTab
        )
    }

    private fun getAnalyticsNavigationAction(toNext: Boolean, viaClick: Boolean): PlayerGradeNavigationAction {
        return when {
            toNext && viaClick -> PlayerGradeNavigationAction.CLICK_NEXT
            toNext && viaClick.not() -> PlayerGradeNavigationAction.SWIPE_NEXT
            toNext.not() && viaClick -> PlayerGradeNavigationAction.CLICK_PREV
            else -> PlayerGradeNavigationAction.SWIPE_PREV
        }
    }

    private fun gradePlayer(grade: Int) {
        val currentPlayer = if (state.isHomeTeam) {
            state.playerGrades?.homeTeam?.players?.get(state.playerIndex)
        } else {
            state.playerGrades?.awayTeam?.players?.get(state.playerIndex)
        }
        currentPlayer?.let { player ->
            val currentGrade = currentPlayer.grading?.grade ?: 0
            currentPlayer.grading?.let { grading ->
                updatePlayerGradeStars(
                    grading.copy(grade = if (currentGrade != grade) grade else 0)
                )
            }
            var ungradingPlayer = false
            viewModelScope.launch {
                addPlayerForGradeSubmitting(player.playerId)
                if (currentGrade != grade) {
                    playerGradesRepository.gradePlayer(
                        gameId = params.gameId,
                        isHomeTeam = state.isHomeTeam,
                        playerId = player.playerId,
                        grade = grade
                    )
                } else {
                    ungradingPlayer = true
                    playerGradesRepository.ungradePlayer(
                        gameId = params.gameId,
                        isHomeTeam = state.isHomeTeam,
                        playerId = player.playerId,
                    )
                }
                    .onSuccess {
                        removePlayerForGradeSubmitting(player.playerId)
                        phoneVibrator.vibrate(PhoneVibrator.Duration.CLICK)
                        if (ungradingPlayer) {
                            sendEvent(SnackbarEventRes(R.string.player_grade_grade_deleted))
                            trackPlayerBeingUngraded(player.playerId)
                        } else {
                            trackPlayerBeingGraded(grade, player.playerId)
                        }
                    }
                    .onError {
                        fetchPlayerGrades()
                        removePlayerForGradeSubmitting(player.playerId)
                    }
            }
        }
    }

    private fun updatePlayerGradeStars(playerGrade: PlayerGradesLocalModel.Grading) {
        var homeTeam = state.playerGrades?.homeTeam
        var awayTeam = state.playerGrades?.awayTeam
        if (state.isHomeTeam) {
            homeTeam?.players?.toMutableList()?.let { players ->
                val player = players[state.playerIndex]
                players[state.playerIndex] = player.copy(grading = playerGrade)
                homeTeam = homeTeam?.copy(players = players)
            }
        } else {
            awayTeam?.players?.toMutableList()?.let { players ->
                val player = players[state.playerIndex]
                players[state.playerIndex] = player.copy(grading = playerGrade)
                awayTeam = awayTeam?.copy(players = players)
            }
        }
        updateState {
            copy(
                playerGrades = state.playerGrades?.copy(
                    homeTeam = homeTeam,
                    awayTeam = awayTeam
                )
            )
        }
    }

    private fun navigateBack() {
        // Refresh the Game and Grade tabs
        viewModelScope.launch {
            gameDetailEventProducer.emit(GameDetailEvent.PlayerGraded)
        }
        sendEvent(PlayerGradesDetailContract.Event.NavigateClose)
    }

    private fun navigateToGradesTab() {
        viewModelScope.launch {
            navigateBack()
            gameDetailEventProducer.emit(GameDetailEvent.SelectGradesTab)
        }
    }

    private fun addPlayerForGradeSubmitting(playerId: String) {
        updateState {
            copy(playersCurrentlySubmittingGrade = state.playersCurrentlySubmittingGrade + playerId)
        }
    }

    private fun removePlayerForGradeSubmitting(playerId: String) {
        updateState {
            copy(playersCurrentlySubmittingGrade = state.playersCurrentlySubmittingGrade - playerId)
        }
    }

    private fun getSelectedTeamsId(): String {
        return if (state.isHomeTeam) {
            state.playerGrades?.homeTeam?.id
        } else {
            state.playerGrades?.awayTeam?.id
        }.orEmpty()
    }

    private fun trackPlayerGradesDetailView(teamId: String) {
        trackPlayerGradeDetailsView(
            playerId = params.playerId,
            teamId = teamId,
            gameId = params.gameId,
            leagueId = params.leagueId,
            fromGradeTab = params.launchedFromGradesTab
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
            fromView = if (params.launchedFromGradesTab) PlayerGradeSourceView.GRADE_TAB_MODAL else PlayerGradeSourceView.GAME_TAB
        )
    }

    private fun trackPlayerBeingUngraded(
        playerId: String,
    ) {
        trackUngradingPlayerClick(
            gameId = params.gameId,
            leagueId = params.leagueId,
            teamId = getSelectedTeamsId(),
            playerId = playerId,
        )
    }
}

data class PlayerGradesDetailState(
    val loadingState: LoadingState,
    val sport: Sport,
    val playerGrades: PlayerGradesLocalModel? = null,
    val isHomeTeam: Boolean = true,
    val initialPlayerSet: Boolean = false,
    val playerIndex: Int = 0,
    val playersCurrentlySubmittingGrade: List<String> = mutableListOf()
) : DataState