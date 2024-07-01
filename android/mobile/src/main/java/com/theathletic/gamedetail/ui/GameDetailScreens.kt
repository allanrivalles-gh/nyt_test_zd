package com.theathletic.gamedetail.ui

import android.view.ViewGroup
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.theathletic.R
import com.theathletic.analytics.data.ClickSource
import com.theathletic.boxscore.ScrollToModule
import com.theathletic.boxscore.ui.TabModule
import com.theathletic.boxscore.ui.bottomsheet.BoxScoreModalSheet
import com.theathletic.comments.analytics.CommentsAnalyticsPayload
import com.theathletic.comments.analytics.CommentsLaunchAction
import com.theathletic.comments.ui.CommentsFragment
import com.theathletic.comments.ui.components.FlaggedCommentAlertDialog
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.data.ContentDescriptor
import com.theathletic.entity.main.Sport
import com.theathletic.extension.toDp
import com.theathletic.featureswitch.Features
import com.theathletic.feed.ui.Feed
import com.theathletic.feed.ui.FeedV2
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.fragment.compose.rememberViewModel
import com.theathletic.gamedetail.boxscore.ui.BoxScoreContract
import com.theathletic.gamedetail.boxscore.ui.BoxScoreStatsViewModel
import com.theathletic.gamedetail.boxscore.ui.BoxScoreViewEvent
import com.theathletic.gamedetail.boxscore.ui.BoxScoreViewModel
import com.theathletic.gamedetail.boxscore.ui.playbyplay.BoxScorePlayByPlayViewModel
import com.theathletic.gamedetail.data.local.GameStatus
import com.theathletic.gamedetail.playergrades.ui.PlayerGradesTabContract
import com.theathletic.gamedetail.playergrades.ui.PlayerGradesTabViewModel
import com.theathletic.liveblog.ui.Event
import com.theathletic.liveblog.ui.LiveBlogScreen
import com.theathletic.liveblog.ui.LiveBlogViewModel
import com.theathletic.liveblog.ui.LiveBlogWebViewScreen
import com.theathletic.liveblog.ui.LiveBlogWebViewViewModel
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.service.PodcastDownloadService
import com.theathletic.ui.DisplayPreferences
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asString
import com.theathletic.ui.collectWithLifecycle
import com.theathletic.ui.observe
import com.theathletic.ui.utility.rememberKoin
import com.theathletic.ui.widgets.ModalBottomSheetLayout
import com.theathletic.ui.widgets.SwipeRefreshIndicator
import kotlin.math.abs

data class GameTabModule(
    val gameId: String,
    val sport: Sport,
    val scrollToModule: ScrollToModule
) : TabModule {

    @SuppressWarnings("LongMethod")
    @Composable
    override fun Render(isActive: Boolean, fragmentManager: () -> FragmentManager) {
        val currentContext = LocalContext.current
        val navigator = rememberKoin<ScreenNavigator>(
            currentContext
        )

        val viewModel = rememberViewModel<BoxScoreViewModel>(
            BoxScoreViewModel.Params(gameId = gameId, sport = sport, scrollToModule = scrollToModule),
            navigator
        )

        val state by viewModel.viewState.collectAsState(initial = null)
        val viewState = state ?: return
        val listState = rememberLazyListState()
        val snackBarHostState = remember { SnackbarHostState() }

        ObserveViewEvents(
            snackBarHostState = snackBarHostState,
            navigator = navigator,
            viewModel = viewModel,
            listState = listState,
            scrollTo = viewState.scrollTo,
            finishedInitialLoading = viewState.finishedInitialLoading
        )

        ModalBottomSheetLayout(
            currentModal = viewState.boxScoreModalSheet,
            onDismissed = { viewModel.onBottomSheetModalDismissed() },
            modalSheetContent = { BoxScoreModalSheetContent(viewState, viewModel) }
        ) {
            CompositionLocalProvider(
                LocalFeedInteractor provides viewModel,
            ) {
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing = viewState.showSpinner),
                    indicator = { state, triggerDp ->
                        SwipeRefreshIndicator(
                            state = state,
                            refreshTriggerDistance = triggerDp
                        )
                    },
                    onRefresh = { viewModel.onRefresh() }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        FeedV2(
                            uiModel = viewState.feedUiModel,
                            isVisible = isActive,
                            listState = listState,
                            onViewVisibilityChanged = viewModel::onViewVisibilityChanged,
                            showLeadingDivider = true
                        )

                        SnackbarHost(
                            hostState = snackBarHostState,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .animateContentSize()
                        )

                        state?.commentFlagState?.let { flagState ->
                            FlaggedCommentAlertDialog(
                                selectedOption = flagState.selectedOption,
                                onDismissRequest = {
                                    viewModel.onCommentFlagStateChanged(flagState.copy(openDialog = false))
                                },
                                onConfirmClick = {
                                    viewModel.onCommentFlagStateChanged(flagState.copy(openDialog = false))
                                    viewModel.onFlagComment(flagState.commentId.orEmpty(), flagState.selectedOption)
                                },
                                onSelectedClick = { flagReason ->
                                    viewModel.onCommentFlagStateChanged(flagState.copy(selectedOption = flagReason))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ObserveViewEvents(
    snackBarHostState: SnackbarHostState,
    navigator: ScreenNavigator,
    viewModel: BoxScoreViewModel,
    listState: LazyListState,
    scrollTo: Int,
    finishedInitialLoading: Boolean
) {
    val context = LocalContext.current
    val resources = LocalContext.current.resources
    val offlineErrorMessage = stringResource(id = R.string.global_network_offline)
    viewModel.viewEvents.collectWithLifecycle { event ->
        when (event) {
            is BoxScoreViewEvent.DownloadPodcastEpisode -> {
                PodcastDownloadService.downloadFile(
                    context = context,
                    podcastEpisodeId = event.episodeId,
                    podcastEpisodeName = event.episodeTitle,
                    downloadUrl = event.downloadUrl
                )
            }
            is BoxScoreViewEvent.CancelPodcastEpisodeDownloading -> {
                PodcastDownloadService.cancelDownload(
                    context,
                    event.episodeId
                )
            }
            is BoxScoreViewEvent.ShowFeedbackMessage -> {
                val message = resources.getString(event.stringRes)
                snackBarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
            }
            BoxScoreViewEvent.ShowNetworkOfflineError -> {
                snackBarHostState.showSnackbar(
                    message = offlineErrorMessage,
                    duration = SnackbarDuration.Long
                )
            }
            BoxScoreViewEvent.ShowPaywall -> {
                navigator.startPlansActivity(ClickSource.PODCAST_PAYWALL)
            }
        }
    }

    LaunchedEffect(scrollTo, finishedInitialLoading) {
        if (scrollTo >= 0) {
            listState.animateScrollToItem(scrollTo)
        }
    }
}

@Composable
private fun BoxScoreModalSheetContent(
    viewState: BoxScoreContract.ViewState,
    viewModel: BoxScoreViewModel
) {
    when (val item = viewState.boxScoreModalSheet) {
        is BoxScoreContract.ModalSheetType.ArticleOptionsModalSheet -> {
            viewState.boxScoreModalSheetOptions?.let { options ->
                BoxScoreModalSheet(options) { selectedOption ->
                    viewModel.onArticleContextMenuItemSelected(
                        selectedOption = selectedOption,
                        articleId = item.articleId,
                        permalink = item.permalink
                    )
                }
            }
        }
        is BoxScoreContract.ModalSheetType.PodcastOptionsModalSheet -> {
            viewState.boxScoreModalSheetOptions?.let { options ->
                BoxScoreModalSheet(options) { selectedOption ->
                    viewModel.onPodcastContextMenuItemSelected(
                        selectedOption = selectedOption,
                        podcastId = item.podcastId,
                        episodeId = item.episodeId,
                        permalink = item.permalink
                    )
                }
            }
        }
        else -> { /* Do Nothing */ }
    }
}

data class PlayerStatsTabModule(
    val gameId: String,
    val sport: Sport,
    val isPostGame: Boolean
) : TabModule {

    @Composable
    override fun Render(isActive: Boolean, fragmentManager: () -> FragmentManager) {
        val viewModel = rememberViewModel<BoxScoreStatsViewModel>(
            BoxScoreStatsViewModel.Params(
                gameId = gameId,
                sport = sport,
                isPostGame = isPostGame
            )
        )

        val state by viewModel.viewState.collectAsState(initial = null)
        val viewState = state ?: return
        val listState = rememberLazyListState()

        CompositionLocalProvider(
            LocalFeedInteractor provides viewModel,
        ) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = viewState.showSpinner),
                indicator = { state, triggerDp ->
                    SwipeRefreshIndicator(
                        state = state,
                        refreshTriggerDistance = triggerDp
                    )
                },
                onRefresh = { viewModel.onRefresh() }
            ) {
                Feed(
                    uiModel = viewState.feedUiModel,
                    isVisible = isActive,
                    listState = listState,
                    verticalSpacing = 0.dp,
                    onViewVisibilityChanged = { _, _ ->
                        // Impression tracking for Stats tab not supported
                    }
                )
            }
        }
    }
}

data class PlaysTabModule(
    val gameId: String,
    val sport: Sport,
    val leagueId: String
) : TabModule {

    @Composable
    override fun Render(isActive: Boolean, fragmentManager: () -> FragmentManager) {
        val viewModel = rememberViewModel<BoxScorePlayByPlayViewModel>(
            BoxScorePlayByPlayViewModel.Params(
                id = gameId,
                sport = sport,
                leagueId = leagueId
            )
        )

        val state by viewModel.viewState.collectAsState(initial = null)
        val viewState = state ?: return
        val listState = rememberLazyListState()

        CompositionLocalProvider(
            LocalFeedInteractor provides viewModel,
        ) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = viewState.showSpinner),
                indicator = { state, triggerDp ->
                    SwipeRefreshIndicator(
                        state = state,
                        refreshTriggerDistance = triggerDp
                    )
                },
                onRefresh = { viewModel.fetchData(isRefresh = true) }
            ) {
                Feed(
                    uiModel = viewState.feedUiModel,
                    isVisible = isActive,
                    listState = listState,
                    onViewVisibilityChanged = { _, _ -> /* Not required */ },
                    verticalSpacing = 0.dp
                )
            }
        }
    }
}

data class DiscussTabModule constructor(
    val gameId: String,
    val title: ResourceString?,
    val commentsAnalyticsPayload: CommentsAnalyticsPayload,
    val hasTeamSpecificComments: Boolean,
    val launchAction: CommentsLaunchAction?
) : TabModule {
    @Composable
    override fun Render(isActive: Boolean, fragmentManager: () -> FragmentManager) {
        val uniqueId = rememberSaveable(gameId) { abs(gameId.hashCode()) }

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            // NOTE: We don't push comment ID changes into the fragment; instead, once it exists, it listens to
            // scroll-to-comment events via the GameDetailEventConsumer
            factory = { context ->
                val contentDescriptor = ContentDescriptor(
                    id = gameId,
                    title = title.asString(context, "")
                )
                val fragment = CommentsFragment.newInstance(
                    contentDescriptor = contentDescriptor,
                    type = if (hasTeamSpecificComments) CommentsSourceType.TEAM_SPECIFIC_THREAD else CommentsSourceType.GAME,
                    isEntryActive = false,
                    launchAction = launchAction,
                    analyticsPayload = commentsAnalyticsPayload,
                    clickSource = null
                )

                fragmentManager().findFragmentById(uniqueId)?.view?.also {
                    (it.parent as? ViewGroup)?.removeView(it)
                } ?: FragmentContainerView(context)
                    .apply { id = uniqueId }
                    .also {
                        fragmentManager().commit {
                            add(
                                uniqueId,
                                fragment
                            )
                        }
                    }
            }
        )
    }
}

data class LiveBlogTabModule(
    val gameId: String,
    val liveBlogId: String?,
    val initialPostId: String?,
    val leagueId: String,
    val status: GameStatus
) : TabModule {
    @Composable
    override fun Render(isActive: Boolean, fragmentManager: () -> FragmentManager) {
        val features = rememberKoin<Features>()
        val screenNavigator = rememberKoin<ScreenNavigator>(LocalContext.current)
        if (features.isLiveBlogWebViewEnabled) {
            RenderWebView(screenNavigator = screenNavigator)
        } else {
            RenderNative(screenNavigator = screenNavigator)
        }
    }

    @Composable
    private fun RenderWebView(screenNavigator: ScreenNavigator) {
        liveBlogId?.let { liveBlogId ->
            val coroutineScope = rememberCoroutineScope()
            val viewModel = rememberKoin<LiveBlogWebViewViewModel>(
                LiveBlogWebViewViewModel.Params(
                    liveBlogId = liveBlogId,
                    initialPostId = initialPostId,
                    screenWidth = LocalConfiguration.current.screenWidthDp,
                    screenHeight = LocalConfiguration.current.screenHeightDp
                )
            )
            viewModel.observe<Event>(coroutineScope) { event ->
                when (event) {
                    Event.OnBackClick -> screenNavigator.finishActivity()
                    is Event.OnShareClick -> screenNavigator.startShareTextActivity(event.permalink)
                }
            }
            LiveBlogWebViewScreen(
                viewModel = viewModel,
                refreshable = true,
                showToolbar = false,
            )
        }
    }

    @Composable
    private fun RenderNative(screenNavigator: ScreenNavigator) {
        val context = LocalContext.current
        val displayPreferences = rememberKoin<DisplayPreferences>()

        val viewModel = rememberViewModel<LiveBlogViewModel>(
            LiveBlogViewModel.Params(
                liveBlogId = liveBlogId ?: "",
                initialPostId = initialPostId ?: "",
                isDayMode = displayPreferences.shouldDisplayDayMode(context),
                screenWidth = context.resources.displayMetrics.widthPixels.toDp,
                screenHeight = context.resources.displayMetrics.widthPixels.toDp,
                status = status,
                leagueId = leagueId,
                gameId = gameId,
                isViewEventFromBoxScore = true,
            ),
            screenNavigator,
        )

        val state by viewModel.viewState.collectAsState(initial = null)
        val viewState = state ?: return
        val listState = rememberLazyListState()

        SwipeRefresh(
            onRefresh = viewModel::refreshLiveBlog,
            state = rememberSwipeRefreshState(isRefreshing = viewState.isLoading),
            indicator = { swipeState, triggerDp ->
                SwipeRefreshIndicator(
                    state = swipeState,
                    refreshTriggerDistance = triggerDp
                )
            },
        ) {
            LiveBlogScreen(
                isLoading = viewState.isLoading,
                liveBlog = viewState.liveBlog,
                initialPostIndex = viewState.initialPostIndex,
                contentTextSize = viewState.contentTextSize,
                stagedPostsCount = viewState.stagedPostsCount,
                listState = listState,
                interactor = viewModel,
                showToolbar = false,
            )
        }
    }
}

data class PlayerGradeTabModule(
    val gameId: String,
    val sport: Sport,
    val leagueId: String,
    val isGameInProgress: Boolean
) : TabModule {
    @Composable
    override fun Render(isActive: Boolean, fragmentManager: () -> FragmentManager) {
        val navigator = rememberKoin<ScreenNavigator>(
            LocalContext.current
        )

        val viewModel = rememberViewModel<PlayerGradesTabViewModel>(
            PlayerGradesTabViewModel.Params(
                gameId = gameId,
                sport = sport,
                leagueId = leagueId,
                isGameInProgress = isGameInProgress
            )
        )

        LaunchedEffect(Unit) {
            viewModel.observe<PlayerGradesTabContract.Event.NavigateToPlayerGradesDetailScreen>(this) { event ->
                navigator.startPlayerGradesDetailActivity(
                    gameId = event.gameId,
                    playerId = event.playerId,
                    sport = event.sport,
                    leagueId = event.leagueId,
                    launchedFromGradesTab = true
                )
            }
        }

        val state by viewModel.viewState.collectAsState(initial = null)
        val viewState = state ?: return
        val listState = rememberLazyListState()

        CompositionLocalProvider(
            LocalFeedInteractor provides viewModel,
        ) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = viewState.showSpinner),
                indicator = { state, triggerDp ->
                    SwipeRefreshIndicator(
                        state = state,
                        refreshTriggerDistance = triggerDp
                    )
                },
                onRefresh = { viewModel.fetchPlayerGrades(isRefresh = true) }
            ) {
                FeedV2(
                    uiModel = viewState.feed,
                    isVisible = isActive,
                    listState = listState,
                    onViewVisibilityChanged = { _, _ -> /* todo be implemented */ },
                    verticalSpacing = 0.dp
                )
            }
        }
    }
}