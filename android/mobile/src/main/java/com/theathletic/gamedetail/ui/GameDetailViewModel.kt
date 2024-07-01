package com.theathletic.gamedetail.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ScrollToModule
import com.theathletic.comments.analytics.CommentsLaunchAction
import com.theathletic.comments.game.AnalyticsGameTeamIdUseCase
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.TimeProvider
import com.theathletic.featureswitches.FeatureSwitch
import com.theathletic.featureswitches.FeatureSwitches
import com.theathletic.feed.FeedType
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.gamedetail.data.local.CoverageDataType
import com.theathletic.gamedetail.data.local.GameStatus
import com.theathletic.gamedetail.data.local.GameSummaryLocalModel
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.scores.GameDetailTab
import com.theathletic.scores.GameDetailTabParamKey
import com.theathletic.scores.GameDetailTabParams
import com.theathletic.scores.data.ScoresRepository
import com.theathletic.share.ShareTitle
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.Transformer
import com.theathletic.user.IUserManager
import com.theathletic.utility.coroutines.collectIn
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber

class GameDetailViewModel @AutoKoin constructor(
    @Assisted val params: Params,
    @Assisted val navigator: ScreenNavigator,
    transformer: GameDetailTransformer,
    private val scoresRepository: ScoresRepository,
    private val followableRepository: FollowableRepository,
    private val timeProvider: TimeProvider,
    analyticsHandler: GameDetailsAnalyticsHandler,
    private val gameDetailEventConsumer: GameDetailEventConsumer,
    private val featureSwitches: FeatureSwitches,
    private val userManager: IUserManager,
    private val analyticsBoxScoreTeamIdUseCase: AnalyticsGameTeamIdUseCase,
) : AthleticViewModel<GameDetailComposeState, GameDetailContract.ViewState>(),
    GameDetailsAnalytics by analyticsHandler,
    DefaultLifecycleObserver,
    GameDetailContract.Presenter,
    Transformer<GameDetailComposeState, GameDetailContract.ViewState> by transformer {

    data class Params(
        val gameId: String,
        val commentId: String,
        val selectedTab: GameDetailTabParams,
        val scrollToModule: ScrollToModule,
        val view: String
    )

    override val initialState by lazy {
        GameDetailComposeState(selectedTab = params.selectedTab.initialTab, selectedTabExtras = params.selectedTab.extras, scrollToModule = params.scrollToModule)
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        if (params.commentId.isNotEmpty()) {
            selectDiscussionTabComment(params.commentId)
        }
        updateCurrentTeamId()
        loadGameData()
        gameDetailEventConsumer.collectIn(viewModelScope) {
            when (it) {
                is GameDetailEvent.SelectCommentInDiscussionTab -> {
                    selectDiscussionTabComment(it.commentId)
                }
                is GameDetailEvent.ReplyToCommentInDiscussionTab -> {
                    replyToDiscussionTabComment(commentId = it.commentId, parentId = it.parentId)
                }
                is GameDetailEvent.SelectPlayByPlayTab -> {
                    updateState { copy(selectedTab = GameDetailTab.PLAYS) }
                }
                GameDetailEvent.SelectGradesTab -> {
                    updateState { copy(selectedTab = GameDetailTab.GRADES) }
                }
                GameDetailEvent.SelectDiscussionTab -> {
                    updateState { copy(selectedTab = GameDetailTab.DISCUSS) }
                }
                GameDetailEvent.Refresh -> fetchGameData()
                else -> { /* do nothing */ }
            }
        }
        if (params.selectedTab.initialTab == GameDetailTab.DISCUSS) {
            trackNavigateToDiscussTab()
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

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        fetchGameData()
    }

    private fun fetchGameData() {
        viewModelScope.launch(fetchErrorHandler) {
            scoresRepository.fetchGameSummary(params.gameId)
        }
    }

    private fun loadGameData() {
        scoresRepository.getGameSummary(params.gameId).collectIn(viewModelScope) {
            it?.let { gameSummary ->
                val isFirstTeamNavigable = followableRepository.getTeam(gameSummary.firstTeam?.id.orEmpty()) != null
                val isSecondTeamNavigable = followableRepository.getTeam(gameSummary.secondTeam?.id.orEmpty()) != null

                updateState {
                    copy(
                        gameSummary = gameSummary,
                        isLoaded = true,
                        isFirstTeamNavigable = isFirstTeamNavigable,
                        isSecondTeamNavigable = isSecondTeamNavigable
                    )
                }
            }
        }
    }

    private fun selectDiscussionTabComment(commentId: String) {
        if (featureSwitches.isFeatureEnabled(FeatureSwitch.BOX_SCORE_DISCUSS_TAB_ENABLED) && userManager.isUserSubscribed()) {
            updateState {
                copy(
                    selectedTab = GameDetailTab.DISCUSS,
                    discussLaunchAction = CommentsLaunchAction.View(commentId)
                )
            }
        }
    }

    private fun replyToDiscussionTabComment(commentId: String, parentId: String) {
        if (featureSwitches.isFeatureEnabled(FeatureSwitch.BOX_SCORE_DISCUSS_TAB_ENABLED) && userManager.isUserSubscribed()) {
            updateState {
                copy(
                    selectedTab = GameDetailTab.DISCUSS,
                    discussLaunchAction = CommentsLaunchAction.Reply(commentId = commentId, parentId = parentId)
                )
            }
        }
    }

    private fun trackNavigateToDiscussTab() {
        viewModelScope.launch {
            trackNavigateToGameDiscussTab(
                view = params.view,
                gameId = params.gameId,
                leagueId = state.gameSummary?.league?.id.orEmpty(),
                teamId = state.currentTeamId
            )
        }
    }

    private fun isTeamSpecific() =
        state.gameSummary?.coverage?.contains(CoverageDataType.TEAM_SPECIFIC_COMMENTS) ?: false

    private val fetchErrorHandler = CoroutineExceptionHandler { _, error ->
        // todo: Update state if we do get an error
        Timber.e(error)
    }

    private fun shouldSubscribeForUpdates(game: GameSummaryLocalModel?): Boolean {
        return when (game?.status) {
            GameStatus.IN_PROGRESS -> true
            GameStatus.SCHEDULED -> isLeadingUpToGameStart(game.scheduleAt)
            else -> false
        }
    }

    private fun isLeadingUpToGameStart(scheduleAt: Datetime): Boolean {
        val now = timeProvider.currentTimeMs
        val prior = scheduleAt.timeMillis - TimeUnit.MINUTES.toMillis(30)
        val overlap = scheduleAt.timeMillis + TimeUnit.MINUTES.toMillis(30)
        return now in prior..overlap
    }

    override fun onBackButtonClicked() {
        navigator.finishActivity()
    }

    @SuppressWarnings("LongMethod")
    override fun onTabClicked(tab: GameDetailTab) {
        val previousTab = state.selectedTab
        updateState { copy(selectedTab = tab, selectedTabExtras = emptyMap()) }

        // Handle analytics for the tab change
        state.gameSummary?.let { gameSummary ->
            when (tab) {
                GameDetailTab.GAME -> trackGameTabClick(
                    status = gameSummary.status,
                    gameId = gameSummary.id,
                    leagueId = gameSummary.league.id,
                    blogId = gameSummary.liveBlog?.id.orEmpty(),
                    previousTab = previousTab,
                    teamId = state.currentTeamId,
                )
                GameDetailTab.PLAYER_STATS -> {
                    trackStatsTabClick(
                        status = gameSummary.status,
                        gameId = gameSummary.id,
                        leagueId = gameSummary.league.id,
                        blogId = gameSummary.liveBlog?.id.orEmpty(),
                        previousTab = previousTab,
                        teamId = state.currentTeamId,
                    )
                }
                GameDetailTab.DISCUSS -> {
                    trackDiscussTabClick(
                        status = gameSummary.status,
                        gameId = gameSummary.id,
                        leagueId = gameSummary.league.id,
                        blogId = gameSummary.liveBlog?.id.orEmpty(),
                        previousTab = previousTab,
                        teamId = state.currentTeamId,
                    )
                }
                GameDetailTab.PLAYS -> {
                    trackPlaysTabClick(
                        status = gameSummary.status,
                        gameId = gameSummary.id,
                        leagueId = gameSummary.league.id,
                        blogId = gameSummary.liveBlog?.id.orEmpty(),
                        previousTab = previousTab,
                        teamId = state.currentTeamId,
                    )
                }
                GameDetailTab.LIVE_BLOG -> {
                    trackLiveBlogTabClick(
                        status = gameSummary.status,
                        gameId = gameSummary.id,
                        leagueId = gameSummary.league.id,
                        blogId = gameSummary.liveBlog?.id.orEmpty(),
                        previousTab = previousTab,
                        teamId = state.currentTeamId,
                    )
                }
                GameDetailTab.GRADES -> {
                    trackGradesTabClick(
                        status = gameSummary.status,
                        gameId = gameSummary.id,
                        leagueId = gameSummary.league.id,
                        previousTab = previousTab,
                        teamId = state.currentTeamId,
                    )
                }
                else -> { /* track nothing */ }
            }
        }
    }

    override fun onTeamClicked(
        teamId: String,
        legacyId: Long,
        teamName: String
    ) {
        trackTeamNavigationToScheduleClick(
            status = state.gameSummary?.status ?: GameStatus.UNKNOWN,
            teamId = teamId,
            onGameTab = state.selectedTab == GameDetailTab.GAME
        )
        if (legacyId > 0) {
            navigator.startHubActivity(FeedType.Team(legacyId))
        }
    }

    override fun onShareClick(shareLink: String) {
        trackShare(params.gameId)
        navigator.startShareTextActivity(
            textToSend = shareLink,
            title = ShareTitle.DEFAULT
        )
    }
}

data class GameDetailComposeState(
    val gameSummary: GameSummaryLocalModel? = null,
    val selectedTab: GameDetailTab = GameDetailTab.GAME,
    val selectedTabExtras: Map<GameDetailTabParamKey, String?> = emptyMap(),
    val currentTeamId: String? = null,
    val isLoaded: Boolean = false,
    val isFirstTeamNavigable: Boolean = true,
    val isSecondTeamNavigable: Boolean = true,
    val discussLaunchAction: CommentsLaunchAction? = null,
    val scrollToModule: ScrollToModule = ScrollToModule.NONE
) : DataState