package com.theathletic.main.ui.listen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.theathletic.R
import com.theathletic.activity.BaseActivity
import com.theathletic.audio.ui.ListenTabContract
import com.theathletic.audio.ui.ListenTabViewModel
import com.theathletic.feed.ui.FeedV2
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.main.ui.MainNavigationEvent
import com.theathletic.main.ui.MainNavigationEventConsumer
import com.theathletic.main.ui.SecondaryNavigationTab
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.ui.observe
import com.theathletic.ui.utility.rememberKoin
import com.theathletic.ui.widgets.SwipeRefreshIndicator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainListenScreen(
    activity: () -> BaseActivity,
    navigator: () -> ScreenNavigator,
    mainNavEventConsumer: () -> MainNavigationEventConsumer,
    initialSelectedTabIndex: Int? = null,
    onInitialTabSelected: () -> Unit
) {
    val pagerState = rememberPagerState()
    val viewModel = koinViewModel<MainListenScreenViewModel>()
    val viewState by viewModel.viewState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onInitialSelectedTab(initialSelectedTabIndex)
        onInitialTabSelected()
    }

    LaunchedEffect(viewState.currentlySelectedTab) {
        pagerState.scrollToPage(viewState.currentlySelectedTab.position)
    }

    TrackScreenViewAnalytics(
        pagerState = pagerState,
        trackTabView = viewModel::trackTabView
    )

    Column(modifier = Modifier.fillMaxSize()) {
        ListenToolbar(
            pagerState = pagerState,
            trackItemClicked = viewModel::trackItemClicked
        )
        HorizontalPager(
            count = 2,
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            ListenTab(
                tabType = when (page) {
                    0 -> ListenTabContract.TabType.FOLLOWING
                    1 -> ListenTabContract.TabType.DISCOVER
                    else -> throw IllegalArgumentException("Only two tabs on Listen Screen")
                },
                isActive = pagerState.currentPage == page,
                activity = activity,
                navigator = navigator,
                mainNavEventConsumer = mainNavEventConsumer,
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun ListenToolbar(
    pagerState: PagerState,
    trackItemClicked: (Int) -> Unit
) {
    val buttons = listOf(
        stringResource(id = R.string.secondary_navigation_podcast_following),
        stringResource(id = R.string.secondary_navigation_podcast_discover),
    )

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
            )
        }
    ) {
        SecondaryNavigationTab(
            pageIndex = 0,
            text = buttons[0],
            pagerState = pagerState,
            onTabSelected = { trackItemClicked(0) }
        )
        SecondaryNavigationTab(
            pageIndex = 1,
            text = buttons[1],
            pagerState = pagerState,
            onTabSelected = { trackItemClicked(1) }
        )
    }
}

@Composable
private fun ListenTab(
    tabType: ListenTabContract.TabType,
    isActive: Boolean,
    activity: () -> BaseActivity,
    navigator: () -> ScreenNavigator,
    mainNavEventConsumer: () -> MainNavigationEventConsumer,
) {
    val viewModel = koinViewModel<ListenTabViewModel>(
        key = tabType.name,
        parameters = {
            parametersOf(ListenTabViewModel.Params(tabType = tabType), navigator())
        },
    )
    val delegate = rememberKoin<ListenTabDelegate>(
        activity(),
        navigator(),
        viewModel
    )

    val state by viewModel.viewState.collectAsState(initial = null)
    val viewState = state ?: return

    val listState = rememberLazyListState()

    val refreshData = remember(viewModel) { { viewModel.fetchData(true) } }

    ListenEventHandler(
        listState = listState,
        viewModel = viewModel,
        delegate = delegate,
    )

    if (isActive) {
        MainNavigationEventHandler(
            listState = listState,
            mainNavigationEventConsumer = mainNavEventConsumer(),
        )
    }

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
            onRefresh = refreshData
        ) {
            FeedV2(
                uiModel = viewState.feedUiModel,
                isVisible = isActive,
                listState = listState,
                onViewVisibilityChanged = { payload, percentVisible ->
                    viewModel.onViewVisibilityChanged(payload, percentVisible)
                }
            )
        }
    }
}

@Composable
private fun ListenEventHandler(
    listState: LazyListState,
    viewModel: ListenTabViewModel,
    delegate: ListenTabDelegate,
) {
    LaunchedEffect(Unit) {
        viewModel.observe<ListenTabContract.Event>(this) { event ->
            when (event) {
                is ListenTabContract.Event.ScrollToTopOfFeed -> launch {
                    listState.animateScrollToItem(0)
                }
                is ListenTabContract.Event.ShowPodcastEpisodeMenu ->
                    delegate.showPodcastEpisodeMenu(
                        episodeId = event.episodeId,
                        isFinished = event.isFinished,
                        isDownloaded = event.isDownloaded,
                    )
            }
        }
    }
}

@Composable
private fun MainNavigationEventHandler(
    listState: LazyListState,
    mainNavigationEventConsumer: MainNavigationEventConsumer,
) {
    LaunchedEffect(Unit) {
        mainNavigationEventConsumer.collect { event ->
            when (event) {
                MainNavigationEvent.ScrollToTopOfFeed -> launch {
                    listState.animateScrollToItem(0)
                }
                else -> {}
            }
        }
    }
}