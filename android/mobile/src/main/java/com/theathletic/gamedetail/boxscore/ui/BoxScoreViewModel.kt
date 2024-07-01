package com.theathletic.gamedetail.boxscore.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import com.theathletic.R
import com.theathletic.adapter.main.PodcastPlayButtonController
import com.theathletic.analytics.data.ClickSource
import com.theathletic.analytics.impressions.ImpressionCalculator
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.article.ArticleHasPaywallUseCase
import com.theathletic.boxscore.FetchBoxScoreFeedUseCase
import com.theathletic.boxscore.ObserveBoxScoreFeedUseCase
import com.theathletic.boxscore.ObservePodcastStateUseCase
import com.theathletic.boxscore.ScrollToModule
import com.theathletic.boxscore.analytics.NewsroomAnalytics
import com.theathletic.boxscore.analytics.NewsroomAnalyticsHandler
import com.theathletic.boxscore.data.local.BoxScore
import com.theathletic.boxscore.data.local.BoxScorePodcastState
import com.theathletic.boxscore.data.local.PodcastEpisode
import com.theathletic.boxscore.ui.BoxScoreUiModel
import com.theathletic.boxscore.ui.RecentGamesUi
import com.theathletic.boxscore.ui.RelatedStoriesUi
import com.theathletic.boxscore.ui.SoccerMomentsUi
import com.theathletic.boxscore.ui.bottomsheet.BoxScoreMenuOption
import com.theathletic.boxscore.ui.modules.CurrentInningModule
import com.theathletic.boxscore.ui.modules.InjuryReportSummaryModule
import com.theathletic.boxscore.ui.modules.LatestNewsModule
import com.theathletic.boxscore.ui.modules.PlayerGradeCardModule
import com.theathletic.boxscore.ui.modules.PlayerGradeModule
import com.theathletic.boxscore.ui.modules.PlayerLineUpModule
import com.theathletic.boxscore.ui.modules.RecentPlaysModule
import com.theathletic.boxscore.ui.modules.SlideStoriesLaunchModule
import com.theathletic.boxscore.ui.modules.TicketsModule
import com.theathletic.boxscore.ui.modules.TopCommentsModule
import com.theathletic.boxscore.ui.playergrades.PlayerGradeMiniCardModel
import com.theathletic.boxscore.ui.toBoxScoreUi
import com.theathletic.comments.FlagReason
import com.theathletic.comments.LikeCommentsUseCase
import com.theathletic.comments.data.CommentsRepository
import com.theathletic.comments.data.LikeAction
import com.theathletic.comments.ui.LikeActionUiState
import com.theathletic.comments.ui.components.CommentsUi
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.TimeProvider
import com.theathletic.entity.main.Sport
import com.theathletic.entity.settings.UserContentEdition
import com.theathletic.event.SnackbarEventRes
import com.theathletic.extension.extLogError
import com.theathletic.featureswitches.FeatureSwitch
import com.theathletic.featureswitches.FeatureSwitches
import com.theathletic.feed.compose.MarkArticleAsReadUseCase
import com.theathletic.feed.compose.MarkArticleAsSavedUseCase
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedInteractor
import com.theathletic.followables.data.UserFollowingRepository
import com.theathletic.gamedetail.boxscore.SyncBoxScorePodcastUseCase
import com.theathletic.gamedetail.boxscore.ui.BoxScoreContract.ViewState
import com.theathletic.gamedetail.data.local.GameArticlesLocalModel
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.GameDetailLocalModel.TopComment
import com.theathletic.gamedetail.data.local.GameLineUpAndStats
import com.theathletic.gamedetail.data.local.GameStatus
import com.theathletic.gamedetail.data.local.PlayerGradesLocalModel
import com.theathletic.gamedetail.playergrades.ui.FilterPlayerGradesUseCase
import com.theathletic.gamedetail.playergrades.ui.PlayerGradesAnalytics
import com.theathletic.gamedetail.playergrades.ui.PlayerGradesAnalyticsHandler
import com.theathletic.gamedetail.ui.GameDetailEvent
import com.theathletic.gamedetail.ui.GameDetailEventConsumer
import com.theathletic.gamedetail.ui.GameDetailEventProducer
import com.theathletic.links.deep.DeeplinkEventProducer
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.podcast.analytics.PodcastNavigationSource
import com.theathletic.podcast.ui.HasPodcastPaywallUseCase
import com.theathletic.podcast.ui.PlaybackState
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.scores.data.ScoresRepository
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.ComposeViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.LoadingState
import com.theathletic.ui.Transformer
import com.theathletic.user.IUserManager
import com.theathletic.utility.NetworkManager
import com.theathletic.utility.PaywallUtility
import com.theathletic.utility.coroutines.collectIn
import com.theathletic.utility.safeLet
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import timber.log.Timber

@Suppress("LargeClass")
class BoxScoreViewModel @AutoKoin constructor(
    @Assisted private val params: Params,
    @Assisted private val navigator: ScreenNavigator,
    private val repository: ScoresRepository,
    private val articleHasPaywall: ArticleHasPaywallUseCase,
    private val impressionCalculator: ImpressionCalculator,
    private val analyticsHandler: BoxScoreAnalyticsHandler,
    private val playerGradesAnalyticsHandler: PlayerGradesAnalyticsHandler,
    private val newsroomAnalyticsHandler: NewsroomAnalyticsHandler,
    private val gameDetailEventProducer: GameDetailEventProducer,
    private val gameDetailEventConsumer: GameDetailEventConsumer,
    private val timeProvider: TimeProvider,
    private val userFollowingRepository: UserFollowingRepository,
    private val filterPlayerGradesUseCase: FilterPlayerGradesUseCase,
    private val fetchBoxScoreFeedUseCase: FetchBoxScoreFeedUseCase,
    private val observeBoxScoreFeedUseCase: ObserveBoxScoreFeedUseCase,
    private val observePodcastStateUseCase: ObservePodcastStateUseCase,
    private val userManager: IUserManager,
    private val deeplinkEventProducer: DeeplinkEventProducer,
    private val markArticleAsRead: MarkArticleAsReadUseCase,
    private val markArticleAsSaved: MarkArticleAsSavedUseCase,
    private val toggleFollowPodcastSeriesUseCase: ToggleFollowPodcastSeriesUseCase,
    private val podcastPlayButtonController: PodcastPlayButtonController,
    private val syncBoxScorePodcastUseCase: SyncBoxScorePodcastUseCase,
    private val getPodcastEpisodeDetailsUseCase: GetPodcastEpisodeDetailsUseCase,
    private val observePodcastDownloadStateChangeUseCase: ObservePodcastDownloadStateChangeUseCase,
    private val deleteDownloadedPodcastEpisodeUseCase: DeleteDownloadedPodcastEpisodeUseCase,
    private val isPodcastEpisodeDownloadingUseCase: IsPodcastEpisodeDownloadingUseCase,
    private val hasPodcastPaywallUseCase: HasPodcastPaywallUseCase,
    private val likeCommentsUseCase: LikeCommentsUseCase,
    private val commentsRepository: CommentsRepository,
    private val userDataRepository: IUserDataRepository,
    private val paywallUtility: PaywallUtility,
    private val networkManager: NetworkManager,
    featureSwitches: FeatureSwitches,
    transformer: BoxScoreTransformer
) : AthleticViewModel<BoxScoreState, ViewState>(),
    BoxScoreContract.Presenter,
    FeedInteractor,
    ComposeViewModel,
    DefaultLifecycleObserver,
    BoxScoreAnalytics by analyticsHandler,
    PodcastPlayButtonController.Callback,
    PlayerGradesAnalytics by playerGradesAnalyticsHandler,
    NewsroomAnalytics by newsroomAnalyticsHandler,
    Transformer<BoxScoreState, ViewState> by transformer {

    data class Params(
        val gameId: String,
        val sport: Sport,
        val scrollToModule: ScrollToModule
    )

    private val isBoxScoreNewsRoomEnabled = featureSwitches.isFeatureEnabled(FeatureSwitch.BOX_SCORE_LATEST_NEWS)

    private val _viewEvents = MutableSharedFlow<BoxScoreViewEvent>()
    val viewEvents = _viewEvents.asSharedFlow()

    override val initialState by lazy {
        BoxScoreState(
            loadingState = LoadingState.INITIAL_LOADING,
            contentRegion = userManager.getUserContentEdition(),
            scrollToModule = params.scrollToModule
        )
    }

    override fun initialize() {
        initializeImpressionTracking()
        listenForRenderUpdates()
        fetchData()

        gameDetailEventConsumer.collectIn(viewModelScope) {
            if (it == GameDetailEvent.PlayerGraded) refresh()
        }
    }

    private fun initializeImpressionTracking() {
        impressionCalculator.configure({ payload, startTime, endTime ->
            payload.impress(startTime, endTime)
        })
    }

    private fun listenForRenderUpdates() {
        if (isBoxScoreNewsRoomEnabled) {
            observeBoxScoreFeedUpdates()
        }
        repository.getGameArticles(params.gameId).collectIn(viewModelScope) { articles ->
            updateState { copy(articles = articles?.articles) }
        }

        repository.observeGame(params.gameId).collectIn(viewModelScope) { game ->
            onGameUpdated(game)
        }
    }

    private suspend fun onGameUpdated(game: GameDetailLocalModel?) {
        if (game?.awayTeam !is GameDetailLocalModel.GameDetailsTeam) {
            if (state.articles == null) {
                loadGameArticle(game?.league)
            }

            if (game != null) {
                if (state.isTeamFollowedChecked.not()) {
                    checkFollowedTeams(game)
                }
                updateState {
                    copy(game = game)
                }
            }
            if (!state.subscribedToUpdates && shouldSubscribeForUpdates(game)) {
                updateState { copy(subscribedToUpdates = true) }
                viewModelScope.launch {
                    repository.subscribeToAllGameUpdates(params.gameId, params.sport)
                        .onFailure { updateState { copy(subscribedToUpdates = false) } }
                }
            }
            if (!state.hasViewEventBeenSent) logViewEvent(game)
            if (state.finishedInitialLoading.not()) updateState { copy(finishedInitialLoading = true) }
        }
    }

    private fun observePodcastDownloadStateChanges() {
        observePodcastDownloadStateChangeUseCase(params.gameId).onEach {
            updateState {
                copy(
                    boxScoreUi = it?.toBoxScoreUi(),
                    boxScore = it
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun observePodcastState() {
        val podcastStateSharedFlow: SharedFlow<BoxScorePodcastState?> =
            observePodcastStateUseCase(gameId = params.gameId).shareIn(
                viewModelScope,
                SharingStarted.WhileSubscribed()
            )

        podcastStateSharedFlow.collectIn(viewModelScope) { boxScorePlayState ->
            boxScorePlayState?.let {
                updateState {
                    copy(
                        boxScoreUi = it.boxScore?.toBoxScoreUi(),
                        boxScore = it.boxScore
                    )
                }
            }
        }

        // this is done for the collection of playback state change to log the play analytics
        podcastStateSharedFlow.distinctUntilChangedBy { it?.playbackState }
            .collectIn(viewModelScope) { boxScorePlayState ->
                boxScorePlayState?.let {
                    trackPodcastPlaybackStateChange(it.podcastEpisodeId, it.playbackState)
                }
            }
    }

    private fun observeBoxScoreFeedUpdates() {
        observeBoxScoreFeedUseCase(params.gameId).onEach {
            it?.let { boxScore ->
                val boxScoreWithExtras = syncBoxScorePodcastUseCase(params.gameId, boxScore)
                updateState {
                    copy(
                        boxScoreUi = boxScoreWithExtras.boxScore.toBoxScoreUi(),
                        boxScore = boxScoreWithExtras.boxScore
                    )
                }

                // Only trigger podcast states observables if news room contains podcasts
                // also invoke observables once using the flag isObservingPodcastStates
                if (boxScoreWithExtras.hasPodcastEpisodes && state.isObservingPodcastStates.not()) {
                    observePodcastState()
                    observePodcastDownloadStateChanges()
                    updateState { copy(isObservingPodcastStates = true) }
                }
            }
        }.launchIn(viewModelScope)
    }

    private suspend fun checkFollowedTeams(game: GameDetailLocalModel) {
        val teams = userFollowingRepository.getFollowingTeams().firstOrNull()

        teams?.let {
            val firstTeamId = game.firstTeam?.team?.id
            val secondTeamId = game.secondTeam?.team?.id
            safeLet(firstTeamId, secondTeamId) { firstId, secondId ->
                val firstTeam = teams.find { it.graphqlId == firstId }
                val secondTeam = teams.find { it.graphqlId == secondId }
                val showFirstTeam = when {
                    firstTeam != null -> true
                    secondTeam != null -> false
                    else -> true
                }
                updateState {
                    copy(
                        selectFirstTeamAsDefault = showFirstTeam,
                        isTeamFollowedChecked = true
                    )
                }
            }
        }
    }

    private fun fetchData() = viewModelScope.launch {
        if (isBoxScoreNewsRoomEnabled) fetchBoxScoreFeedUseCase.invoke(params.gameId)
        repository.fetchGame(params.gameId, params.sport)?.join()
        updateState { copy(loadingState = LoadingState.FINISHED) }
    }

    private fun refresh() {
        updateState { copy(loadingState = LoadingState.RELOADING) }
        fetchData()
    }

    private fun loadGameArticle(league: GameDetailLocalModel.League?) {
        league?.legacyLeague?.leagueId?.let { id ->
            repository.fetchGameArticles(params.gameId, id)
        }
    }

    override fun onArticleClick(
        analyticsPayload: RelatedStoriesUi.RelatedStoriesAnalyticsPayload
    ) {
        viewModelScope.launch {
            val id = try {
                analyticsPayload.articleId.toLong()
            } catch (e: Exception) {
                Timber.e(e, "Article Id cannot be converted to a Long")
                return@launch
            }

            navigateToArticleScreen(id)
            analyticsPayload.click(state.game?.status ?: GameStatus.UNKNOWN)
        }
    }

    private suspend fun navigateToArticleScreen(articleId: Long) {
        if (articleHasPaywall(articleId)) {
            navigator.startArticlePaywallActivity(articleId, ClickSource.GAME_DETAIL)
        } else {
            navigator.startArticleActivity(articleId, ClickSource.GAME_DETAIL)
        }
    }

    private fun onRecentGameClick(id: String) {
        navigator.startGameDetailMvpActivity(id)
    }

    private fun onSlideStoriesClick(id: String) {
        navigator.startSlideStories(storiesId = id)
    }

    override fun onInjuryReportFullReportClick(gameId: String, isFirstTeamSelected: Boolean) {
        navigator.startInjuryReportActivity(gameId, isFirstTeamSelected)
    }

    override fun onFullPlayByPlayClick() {
        viewModelScope.launch {
            gameDetailEventProducer.emit(GameDetailEvent.SelectPlayByPlayTab)
        }
        state.game?.let { game ->
            trackFullPlayByPlaysClicked(game.id, game.league.id)
        }
    }

    fun onRefresh() {
        viewModelScope.launch {
            gameDetailEventProducer.emit(GameDetailEvent.Refresh)
            refresh()
        }
    }

    override fun onViewVisibilityChanged(payload: ImpressionPayload, pctVisible: Float) {
        impressionCalculator.onViewVisibilityChanged(payload, pctVisible)
    }

    private fun logViewEvent(game: GameDetailLocalModel?) {
        game ?: return
        if (game.sport == Sport.SOCCER) {
            trackScreenView(params.gameId)
        } else {
            trackBoxScoreGameView(game.status, game.id, game.league.id, "")
        }
        updateState { copy(hasViewEventBeenSent = true) }
    }

    private fun logRecentGameClick(isFirstTeamClick: Boolean) {
        val game = state.game ?: return
        trackRecentGamesClick(
            gameId = game.id,
            leagueId = game.league.id,
            teamId = if (isFirstTeamClick) {
                game.firstTeam?.id.orEmpty()
            } else {
                game.secondTeam?.id.orEmpty()
            }
        )
    }

    @SuppressWarnings("LongMethod")
    override fun send(interaction: FeedInteraction) {
        when (interaction) {
            is RecentPlaysModule.Interaction.OnFullPlayByPlayClick,
            is CurrentInningModule.Interaction.OnFullPlayByPlayClick -> onFullPlayByPlayClick()
            is InjuryReportSummaryModule.Interaction.OnShowFullInjuryReportClick ->
                onInjuryReportFullReportClick(
                    interaction.id,
                    interaction.isFirstTeamSelected
                )
            is PlayerLineUpModule.Interaction.OnLineUpExpandClick ->
                onLineUpExpandClick(interaction.playerId)
            is RelatedStoriesUi.Interaction.OnArticleClick ->
                onArticleClick(interaction.analyticsPayload)
            is RecentGamesUi.Interaction.OnRecentGameClick ->
                onRecentGameClick(interaction.id)
            is SoccerMomentsUi.Interaction.OnFullTimeLineClick ->
                onFullPlayByPlayClick()
            is PlayerGradeCardModule.Interaction.PlayerGradeDetailsClick -> {
                onStartPlayerGradesDetail(playerId = interaction.playerId)
                trackLaunchOfPlayerGradesDetailClick(interaction.playerId)
            }
            is PlayerGradeModule.Interaction.OnPlayerGradesClick -> {
                if (interaction.isLocked) {
                    onPlayerGradesClick()
                    state.game?.let { trackViewAllPlayerGradesClick(it.id, it.league.id) }
                } else {
                    state.game.firstPlayerOfSelectedTeam()?.let { player ->
                        onStartPlayerGradesDetail(playerId = player.playerId)
                    }
                    state.game?.let { trackGradePlayersClick(it.id, it.league.id) }
                }
            }
            is PlayerGradeMiniCardModel.Interaction.OnNavigateToPlayerGradeDetailScreen -> {
                onStartPlayerGradesDetail(interaction.playerId)
            }
            is PlayerGradeModule.Interaction.OnTeamToggled -> {
                updateState { copy(isFirstTeamSelected = interaction.isFirstTeamSelected) }
                trackPlayerGradesTeamSwitchClick(interaction.isFirstTeamSelected)
            }
            is TicketsModule.Interaction.TicketLinkClick -> {
                navigator.openLink(interaction.url)
                trackTicketsLinkClicked(state.game)
            }
            is LatestNewsModule.Interaction.LatestNewsArticle -> {
                deeplinkEventProducer.tryEmit(interaction.permalink)
                trackNewsroomArticleClicked(interaction.articleId)
            }
            is LatestNewsModule.Interaction.ArticleLongClick -> {
                showArticleContextMenu(interaction)
            }
            is LatestNewsModule.Interaction.PodcastClick -> {
                podcastEpisodeClicked(interaction.episodeId.toLong())
                trackNewsroomPodcastEpisodeClicked(interaction.episodeId)
            }
            is LatestNewsModule.Interaction.PodcastPlayControl -> {
                viewModelScope.launch {
                    val podcast = getPodcastFromBoxScore(interaction)
                    podcast?.episodeId?.let {
                        updateState { copy(currentPodcastEpisodeId = podcast.episodeId) }
                        podcastPlayButtonController.onPodcastPlayClick(
                            episodeId = podcast.episodeId.toLong(),
                            callback = this@BoxScoreViewModel,
                        )
                    }
                }
            }
            is LatestNewsModule.Interaction.PodcastOptionsMenu -> {
                showPodcastContextMenu(
                    podcastId = interaction.podcastId,
                    episodeId = interaction.episodeId,
                    permalink = interaction.permalink
                )
                trackPodcastMenuClick(interaction.episodeId)
            }
            is TopCommentsModule.Interaction.OnCommentClick -> {
                navigateToCommentInDiscussion(interaction.commentId)
            }
            is TopCommentsModule.Interaction.OnLikeClick -> {
                likeComment(interaction.commentId)
            }
            is TopCommentsModule.Interaction.OnReplyClick -> {
                replyToCommentInDiscussion(
                    commentId = interaction.commentId,
                    parentId = interaction.parentId
                )
            }
            is TopCommentsModule.Interaction.OnFlagClick -> {
                flagComment(interaction.commentId)
            }
            is TopCommentsModule.Interaction.OnShareClick -> {
                shareComment(interaction.permalink)
            }
            is TopCommentsModule.Interaction.OnJoinDiscussionClick -> {
                selectDiscussionTab()
            }
            is SlideStoriesLaunchModule.Interaction.SlideStoryClick -> {
                onSlideStoriesClick(interaction.id)
            }
        }
    }

    private fun trackPodcastMenuClick(episodeId: String) {
        state.game?.let { game ->
            newsroomAnalyticsHandler.trackPodcastEpisodeMenuClick(
                gameStatus = game.status,
                podcastEpisodeId = episodeId,
                gameId = game.id
            )
        }
    }

    private fun trackPodcastPlaybackStateChange(
        podcastId: String,
        newPlaybackState: PlaybackState
    ) {
        val currentPodcastEpisodeId = state.currentPodcastEpisodeId
        if (currentPodcastEpisodeId != null && currentPodcastEpisodeId == podcastId) {
            if (newPlaybackState == PlaybackState.Playing) {
                trackListenToPodcast(currentPodcastEpisodeId)
            }
        }
    }

    private fun trackListenToPodcast(episodeId: String) {
        state.game?.let { game ->
            newsroomAnalyticsHandler.trackPodcastEpisodePlay(
                gameStatus = game.status,
                podcastEpisodeId = episodeId,
                gameId = game.id
            )
        }
    }

    private fun trackNewsroomPodcastEpisodeClicked(episodeId: String) {
        state.game?.let { game ->
            newsroomAnalyticsHandler.trackPodcastEpisodeClick(
                gameStatus = game.status,
                podcastEpisodeId = episodeId,
                gameId = game.id
            )
        }
    }

    private fun trackNewsroomArticleClicked(articleId: String) {
        state.game?.let { game ->
            newsroomAnalyticsHandler.trackArticleContentClick(
                gameStatus = game.status,
                postId = articleId,
                gameId = game.id
            )
        }
    }

    private fun getPodcastFromBoxScore(interaction: LatestNewsModule.Interaction.PodcastPlayControl): PodcastEpisode? {
        return state.boxScore?.sections
            ?.flatMap { it.modules }
            ?.flatMap { it.blocks }
            ?.filterIsInstance<PodcastEpisode>()
            ?.find { it.episodeId == interaction.episodeId }
    }

    private fun showArticleContextMenu(interaction: LatestNewsModule.Interaction.ArticleLongClick) {
        updateState {
            copy(
                boxScoreModalSheetOptions = listOf(
                    if (interaction.isRead) BoxScoreMenuOption.ARTICLE_UNREAD else BoxScoreMenuOption.ARTICLE_READ,
                    if (interaction.isBookmarked) BoxScoreMenuOption.ARTICLE_UNSAVE else BoxScoreMenuOption.ARTICLE_SAVE,
                    BoxScoreMenuOption.SHARE
                )
            )
        }

        showModal(
            BoxScoreContract.ModalSheetType.ArticleOptionsModalSheet(
                articleId = interaction.articleId.toLong(),
                isRead = interaction.isRead,
                isBookmarked = interaction.isBookmarked,
                permalink = interaction.permalink
            )
        )
    }

    private fun showPodcastContextMenu(
        podcastId: String,
        episodeId: String,
        permalink: String
    ) {
        viewModelScope.launch {
            val isPodcastEpisodeDownloading = isPodcastEpisodeDownloadingUseCase(episodeId.toLong())
            val podcastEpisodeDetails = getPodcastEpisodeDetailsUseCase(podcastId.toLong(), episodeId.toLong())
            updateState {
                copy(
                    boxScoreModalSheetOptions = listOf(
                        BoxScoreMenuOption.SHARE,
                        BoxScoreMenuOption.PODCAST_SERIES_DETAILS,
                        if (podcastEpisodeDetails?.isFollowed == true) {
                            BoxScoreMenuOption.PODCAST_UNFOLLOW_SERIES
                        } else {
                            BoxScoreMenuOption.PODCAST_FOLLOW_SERIES
                        },
                        if (podcastEpisodeDetails?.isDownloaded == true) {
                            BoxScoreMenuOption.PODCAST_REMOVE_DOWNLOAD
                        } else if (isPodcastEpisodeDownloading) {
                            BoxScoreMenuOption.PODCAST_CANCEL_DOWNLOAD
                        } else {
                            BoxScoreMenuOption.PODCAST_DOWNLOAD
                        }
                    )
                )
            }

            showModal(
                BoxScoreContract.ModalSheetType.PodcastOptionsModalSheet(
                    podcastId = podcastId.toLong(),
                    episodeId = episodeId.toLong(),
                    permalink = permalink
                )
            )
        }
    }

    private fun trackTicketsLinkClicked(game: GameDetailLocalModel?) {
        safeLet(game?.id, game?.gameTicket) { gameId, ticket ->
            analyticsHandler.trackTicketsBuyClicked(gameId, ticket.provider)
        }
    }

    private fun trackPlayerGradesTeamSwitchClick(isFirstTeamSelected: Boolean) {
        state.game?.let { game ->
            game.getTeamId(isFirstTeamSelected)?.let { teamId ->
                trackPlayerGradeTeamSwitchClick(
                    leagueId = game.league.id,
                    teamId = teamId,
                    gameId = game.id
                )
            }
        }
    }

    private fun trackLaunchOfPlayerGradesDetailClick(playerId: String) {
        state.game?.let { game ->
            game.getTeamId(state.isFirstTeamSelected)?.let { teamId ->
                trackToGradePlayerDetailsClick(
                    playerId = playerId,
                    teamId = teamId,
                    gameId = game.id,
                    leagueId = game.league.id,
                    fromGradeTab = false
                )
            }
        }
    }

    private fun GameDetailLocalModel.getTeamId(isFirstTeam: Boolean): String? =
        if (isFirstTeam) firstTeam?.team?.id else secondTeam?.team?.id

    private fun onLineUpExpandClick(id: String) {
        val expandedLineUps = state.expandedLineUpPlayers.toMutableList()
        if (expandedLineUps.contains(id)) {
            expandedLineUps.remove(id)
        } else {
            expandedLineUps.add(id)
        }
        updateState { copy(expandedLineUpPlayers = expandedLineUps) }
    }

    private fun shouldSubscribeForUpdates(game: GameDetailLocalModel?): Boolean {
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

    private fun onStartPlayerGradesDetail(playerId: String) {
        state.game?.let { game ->
            navigator.startPlayerGradesDetailActivity(
                gameId = game.id,
                playerId = playerId,
                sport = game.sport,
                leagueId = game.league.id
            )
        }
    }

    private fun onPlayerGradesClick() {
        viewModelScope.launch {
            gameDetailEventProducer.emit(GameDetailEvent.SelectGradesTab)
        }
    }

    private fun GameDetailLocalModel?.firstPlayerOfSelectedTeam(): PlayerGradesLocalModel.Player? {
        val selectedTeam = if (state.isFirstTeamSelected) {
            state.game?.firstTeam
        } else {
            state.game?.secondTeam
        }
        return filterPlayerGradesUseCase.invoke(selectedTeam?.players).firstOrNull()
    }

    fun onBottomSheetModalDismissed() {
        showModal(null)
        updateState { copy(boxScoreModalSheetOptions = null) }
    }

    private fun showModal(modal: BoxScoreContract.ModalSheetType?) {
        updateState { copy(boxScoreModalSheet = modal) }
    }

    private fun podcastEpisodeClicked(episodeId: Long) {
        navigator.startPodcastEpisodeDetailActivity(episodeId, PodcastNavigationSource.BOX_SCORE)
    }

    fun onArticleContextMenuItemSelected(
        selectedOption: BoxScoreMenuOption,
        articleId: Long,
        permalink: String
    ) {
        viewModelScope.launch {
            when (selectedOption) {
                BoxScoreMenuOption.ARTICLE_UNSAVE -> markArticleAsSaved(articleId, false)
                BoxScoreMenuOption.ARTICLE_SAVE -> markArticleAsSaved(articleId, true)
                BoxScoreMenuOption.ARTICLE_UNREAD -> markArticleAsRead(articleId, false)
                BoxScoreMenuOption.ARTICLE_READ -> markArticleAsRead(articleId, true)
                BoxScoreMenuOption.SHARE -> navigator.startShareTextActivity(permalink)
                else -> { /* Do Nothing */ }
            }
            onBottomSheetModalDismissed()
        }
    }

    fun onPodcastContextMenuItemSelected(
        selectedOption: BoxScoreMenuOption,
        podcastId: Long,
        episodeId: Long,
        permalink: String
    ) {
        viewModelScope.launch {
            when (selectedOption) {
                BoxScoreMenuOption.SHARE -> navigator.startShareTextActivity(permalink)
                BoxScoreMenuOption.PODCAST_SERIES_DETAILS ->
                    navigator.startPodcastDetailActivity(podcastId, PodcastNavigationSource.BOX_SCORE)
                BoxScoreMenuOption.PODCAST_FOLLOW_SERIES,
                BoxScoreMenuOption.PODCAST_UNFOLLOW_SERIES -> {
                    trackPodcastFollowUnfollow(
                        episodeId = episodeId.toString(),
                        isFollow = selectedOption == BoxScoreMenuOption.PODCAST_FOLLOW_SERIES
                    )
                    toggleFollowPodcastSeriesUseCase(podcastId)
                }
                BoxScoreMenuOption.PODCAST_DOWNLOAD -> {
                    downloadPodcastEpisode(podcastId, episodeId)
                }
                BoxScoreMenuOption.PODCAST_REMOVE_DOWNLOAD -> {
                    deleteDownloadedPodcastEpisodeUseCase(episodeId)
                }
                BoxScoreMenuOption.PODCAST_CANCEL_DOWNLOAD -> {
                    cancelPodcastDownload(episodeId)
                }
                else -> { /* Do Nothing */ }
            }
            onBottomSheetModalDismissed()
        }
    }

    private suspend fun cancelPodcastDownload(episodeId: Long) {
        _viewEvents.emit(BoxScoreViewEvent.CancelPodcastEpisodeDownloading(episodeId))
    }

    private suspend fun downloadPodcastEpisode(podcastId: Long, episodeId: Long) {
        val podcastEpisode = getPodcastEpisodeDetailsUseCase(podcastId, episodeId)
        podcastEpisode?.let { episode ->
            when {
                hasPodcastPaywallUseCase(podcastEpisode.isTeaser) -> {
                    _viewEvents.emit(BoxScoreViewEvent.ShowPaywall)
                }
                networkManager.isOffline() -> {
                    showNetworkOfflineError()
                }
                else -> {
                    _viewEvents.emit(
                        BoxScoreViewEvent.DownloadPodcastEpisode(
                            episodeId = episodeId,
                            episodeTitle = episode.title,
                            downloadUrl = episode.downloadUrl
                        )
                    )

                    state.game?.let { game ->
                        newsroomAnalyticsHandler.trackPodcastDownload(
                            gameStatus = game.status,
                            podcastEpisodeId = episodeId.toString(),
                            gameId = game.id
                        )
                    }
                }
            }
        }
    }

    private fun trackPodcastFollowUnfollow(episodeId: String, isFollow: Boolean) {
        state.game?.let { game ->
            trackPodcastFollowUnfollow(
                gameStatus = game.status,
                podcastEpisodeId = episodeId,
                gameId = game.id,
                isFollow = isFollow
            )
        }
    }

    private fun navigateToCommentInDiscussion(commentId: String) {
        viewModelScope.launch {
            gameDetailEventProducer.emit(GameDetailEvent.SelectCommentInDiscussionTab(commentId))
        }
    }

    private fun likeComment(commentId: String) {
        updateState { copy(likeActionUiState = likeActionUiState.disable(commentId)) }
        if (isCommentInteractionAllowed()) {
            viewModelScope.launch {
                val comment = state.game?.topComments?.firstOrNull { it.id == commentId } ?: return@launch
                val hasUserLiked = userDataRepository.isCommentLiked(commentId.toLong())
                val response = likeCommentsUseCase(hasUserLiked = hasUserLiked, commentId = commentId)
                response.onSuccess {
                    updateCommentLikeState(comment, commentId)
                }.onFailure {
                    updateState { copy(likeActionUiState = likeActionUiState.enable(commentId)) }
                }
            }
        } else {
            updateState { copy(likeActionUiState = likeActionUiState.enable(commentId)) }
            showInteractionRequirements()
        }
    }

    private fun updateCommentLikeState(comment: TopComment, commentId: String) {
        val likeAction = comment.hasUserLiked.toLikeAction()
        updateState {
            copy(
                game = game?.copy(
                    topComments = game.topComments.addLike(commentId, likeAction)
                ),
                likeActionUiState = likeActionUiState.enable(commentId)
            )
        }
    }

    private fun replyToCommentInDiscussion(commentId: String, parentId: String) {
        if (isCommentInteractionAllowed()) {
            viewModelScope.launch {
                gameDetailEventProducer.emit(
                    GameDetailEvent.ReplyToCommentInDiscussionTab(commentId = commentId, parentId = parentId)
                )
            }
        } else {
            showInteractionRequirements()
        }
    }

    private fun flagComment(commentId: String) {
        when {
            userManager.isCodeOfConductAccepted().not() -> navigator.showCodeOfConduct()
            userDataRepository.isCommentFlagged(commentId.toLong()) -> {
                sendEvent(SnackbarEventRes(R.string.global_comment_already_flagged))
            }
            else -> {
                updateState {
                    copy(
                        commentFlagState = CommentsUi.FlagState(
                            openDialog = true,
                            selectedOption = FlagReason.NONE,
                            commentId = commentId
                        )
                    )
                }
            }
        }
    }

    private fun shareComment(permalink: String) {
        viewModelScope.launch {
            navigator.startShareTextActivity(permalink)
        }
    }

    private fun selectDiscussionTab() {
        if (isCommentInteractionAllowed()) {
            viewModelScope.launch {
                gameDetailEventProducer.emit(GameDetailEvent.SelectDiscussionTab)
            }
        } else {
            showInteractionRequirements()
        }
    }

    private fun List<TopComment>.addLike(commentId: String, likeAction: LikeAction) = toMutableList().apply {
        forEachIndexed { index, comment ->
            if (commentId == comment.id) {
                set(
                    index,
                    comment.updateLikes(likeAction)
                )
                return@forEachIndexed
            }
        }
    }

    private fun TopComment.updateLikes(likeAction: LikeAction) = when (likeAction) {
        LikeAction.LIKE -> copy(hasUserLiked = true, likesCount = likesCount.inc())
        LikeAction.UNLIKE -> copy(hasUserLiked = false, likesCount = likesCount.dec())
    }

    private fun isCommentInteractionAllowed(): Boolean {
        return paywallUtility.shouldUserSeePaywall().not() &&
            userManager.isCodeOfConductAccepted()
    }

    fun onCommentFlagStateChanged(flagState: CommentsUi.FlagState) {
        updateState { copy(commentFlagState = flagState) }
    }

    fun onFlagComment(commentId: String, flagType: FlagReason) {
        viewModelScope.launch {
            commentsRepository.flagComment(commentId, flagType)
                .onSuccess {
                    userDataRepository.markCommentFlagged(commentId.toLong(), true)
                    _viewEvents.emit(BoxScoreViewEvent.ShowFeedbackMessage(R.string.comments_flag_snackbar_success))
                }
                .onError {
                    it.extLogError()
                    _viewEvents.emit(BoxScoreViewEvent.ShowFeedbackMessage(R.string.global_error))
                }
            _viewEvents.emit(BoxScoreViewEvent.ShowFeedbackMessage(R.string.comments_flag_snackbar_success))
        }
    }

    private fun showInteractionRequirements() {
        when {
            paywallUtility.shouldUserSeePaywall() -> navigator.startPlansActivity(ClickSource.PAYWALL)
            userManager.isCodeOfConductAccepted().not() -> navigator.startCodeOfConductSheetActivityForResult()
        }
    }

    private fun Boolean.toLikeAction(): LikeAction = if (this) LikeAction.UNLIKE else LikeAction.LIKE

    override fun showPayWall() {
        navigator.startPlansActivity(ClickSource.GAME_DETAIL)
    }

    override fun showNetworkOfflineError() {
        viewModelScope.launch {
            _viewEvents.emit(BoxScoreViewEvent.ShowNetworkOfflineError)
        }
    }
}

data class BoxScoreState(
    val loadingState: LoadingState,
    val game: GameDetailLocalModel? = null,
    val boxScoreUi: BoxScoreUiModel? = null,
    val boxScore: BoxScore? = null,
    val lineUpAndStats: GameLineUpAndStats? = null,
    val articles: List<GameArticlesLocalModel.GameArticle>? = null,
    val timelineExpanded: Boolean = false,
    val lineUpFirstTeamSelected: Boolean = true,
    val lastGamesFirstTeamSelected: Boolean = true,
    val expandedLineUpPlayers: List<String> = emptyList(),
    val hasViewEventBeenSent: Boolean = false,
    val subscribedToUpdates: Boolean = false,
    val selectFirstTeamAsDefault: Boolean = true,
    val isTeamFollowedChecked: Boolean = false,
    val isFirstTeamSelected: Boolean = true,
    val contentRegion: UserContentEdition,
    val boxScoreModalSheet: BoxScoreContract.ModalSheetType? = null,
    val boxScoreModalSheetOptions: List<BoxScoreMenuOption>? = null,
    val currentPodcastEpisodeId: String? = null,
    val snackBarMessage: String? = null,
    val isObservingPodcastStates: Boolean = false,
    val likeActionUiState: LikeActionUiState = LikeActionUiState(),
    val commentFlagState: CommentsUi.FlagState? = null,
    val scrollToModule: ScrollToModule = ScrollToModule.NONE,
    val finishedInitialLoading: Boolean = false,
) : DataState