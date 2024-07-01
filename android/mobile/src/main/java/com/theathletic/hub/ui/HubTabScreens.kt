package com.theathletic.hub.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.theathletic.R
import com.theathletic.boxscore.ui.standings.RelegationLegend
import com.theathletic.brackets.ui.BracketsScreen
import com.theathletic.brackets.ui.BracketsViewModel
import com.theathletic.entity.main.League
import com.theathletic.feed.FeedType
import com.theathletic.feed.ui.FeedFragment
import com.theathletic.feed.ui.FeedV2
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.followable.Followable
import com.theathletic.fragment.AthleticFragment
import com.theathletic.fragment.compose.rememberViewModel
import com.theathletic.hub.league.ui.HubAnalyticsViewType
import com.theathletic.hub.league.ui.LeagueHubAnalyticsHandler
import com.theathletic.hub.league.ui.LeagueHubStandingsContract
import com.theathletic.hub.league.ui.LeagueHubStandingsViewModel
import com.theathletic.hub.team.ui.TeamHubAnalyticsHandler
import com.theathletic.hub.team.ui.TeamHubRosterViewModel
import com.theathletic.hub.team.ui.TeamHubStandingsContract
import com.theathletic.hub.team.ui.TeamHubStandingsViewModel
import com.theathletic.hub.team.ui.TeamHubStatsViewModel
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.scores.ui.ScheduleScreen
import com.theathletic.scores.ui.ScheduleViewEvent
import com.theathletic.scores.ui.ScheduleViewModel
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.type.LeagueCode
import com.theathletic.ui.collectWithLifecycle
import com.theathletic.ui.observe
import com.theathletic.ui.utility.rememberKoin
import com.theathletic.ui.widgets.SwipeRefreshIndicator
import kotlin.math.abs
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private const val HUB_TAB_HOME = "Home"

@Composable
private fun HubFragmentWrapper(
    tabId: String,
    entityId: String,
    fragment: AthleticFragment,
    fragmentManager: () -> FragmentManager
) {
    val uniqueId = rememberSaveable(entityId) { abs("$tabId:$entityId".hashCode()) }
    var isAdded by rememberSaveable { mutableStateOf(false) }

    AndroidViewBinding(
        factory = { inflater, parent, attachToParent ->
            ViewBinding {
                inflater.inflate(R.layout.activity_fragment_base, parent, attachToParent).apply {
                    id = uniqueId
                }.also { ViewCompat.setNestedScrollingEnabled(it, true) }
            }
        },
        update = {
            if (!isAdded) {
                isAdded = true
                fragmentManager().beginTransaction()
                    .add(uniqueId, fragment)
                    .commit()
            }
        },
        modifier = Modifier.fillMaxSize(),
    )
}

data class HubHomeModule(
    val entityId: String,
    val leagueId: String?,
    val feedType: FeedType,
) : HubUi.HubTabModule {
    @Composable
    override fun Render(
        isActive: Boolean,
        fragmentManager: () -> FragmentManager
    ) {
        val fragment = FeedFragment.newInstance(feedType)

        var viewEventTriggered by remember { mutableStateOf(false) }
        if (viewEventTriggered.not()) {
            if (leagueId == null) {
                val analytics = rememberKoin<LeagueHubAnalyticsHandler>(LocalContext.current)
                analytics.trackViewTabOnLeagueHub(entityId, HubAnalyticsViewType.Home)
            } else {
                val analytics = rememberKoin<TeamHubAnalyticsHandler>(LocalContext.current)
                analytics.trackViewOfHomeFeed(entityId, leagueId)
            }
            viewEventTriggered = true
        }

        HubFragmentWrapper(
            tabId = HUB_TAB_HOME,
            entityId = entityId,
            fragment = fragment,
            fragmentManager = fragmentManager,
        )
    }
}

data class HubScheduleFeedModule(
    val folowableId: Followable.Id
) : HubUi.HubTabModule {

    @Composable
    override fun Render(
        isActive: Boolean,
        fragmentManager: () -> FragmentManager
    ) {
        val navigator = rememberKoin<ScreenNavigator>(LocalContext.current)
        val viewModel = koinViewModel<ScheduleViewModel>(
            parameters = { parametersOf(ScheduleViewModel.Params(folowableId)) }
        )

        viewModel.viewEvents.collectWithLifecycle { event ->
            when (event) {
                is ScheduleViewEvent.NavigateToGame -> navigator.navigateToGame(event.gameId, event.showDiscussion, "scores")
                is ScheduleViewEvent.NavigateToTicketingSite -> navigator.navigateToExternalLink(event.url)
            }
        }
        ScheduleScreen(viewModel)
    }
}

@Suppress("LongMethod")
data class TeamHubStandingsModule(
    val teamId: String,
    val league: League,
    val hasAutoScrolled: Boolean
) : HubUi.HubTabModule {
    @Composable
    override fun Render(
        isActive: Boolean,
        fragmentManager: () -> FragmentManager
    ) {
        val navigator = rememberKoin<ScreenNavigator>(
            LocalContext.current
        )

        val viewModel = koinViewModel<TeamHubStandingsViewModel>(
            parameters = { parametersOf(TeamHubStandingsViewModel.Params(teamId = teamId), navigator) }
        )

        val state by viewModel.viewState.collectAsState(initial = null)
        val viewState = state ?: return
        val listState = rememberLazyListState()

        CompositionLocalProvider(
            LocalFeedInteractor provides viewModel,
        ) {

            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing = viewState.showSpinner),
                    indicator = { state, triggerDp ->
                        SwipeRefreshIndicator(
                            state = state,
                            refreshTriggerDistance = triggerDp
                        )
                    },
                    onRefresh = { viewModel.onRefresh() },
                    modifier = Modifier.weight(1f)
                ) {
                    if (viewState.showEmptyState) {
                        EmptyStateScreen(
                            titleResId = R.string.team_hub_standings_empty_title,
                            subTextResId = R.string.team_hub_standings_empty_subtitle,
                        )
                    } else {
                        FeedV2(
                            uiModel = viewState.feedUiModel,
                            isVisible = isActive,
                            listState = listState,
                            onViewVisibilityChanged = { _, _ -> },
                            bottomContentPadding = 0.dp,
                            verticalSpacing = 0.dp,
                        )
                    }
                }
                if (viewState.relegationLegendItemsV2.isNotEmpty()) {
                    RelegationLegend(relegationItems = viewState.relegationLegendItemsV2)
                }
            }
            if (hasAutoScrolled.not()) {
                LaunchedEffect(viewState.initialIndex) {
                    performAutoScroll(
                        listState,
                        viewState
                    ) { viewModel.autoScrollCompleted() }
                }
            }
        }
    }
}

private suspend fun performAutoScroll(
    listState: LazyListState,
    viewState: TeamHubStandingsContract.ViewState,
    onAutoScrollComplete: () -> Unit
) {
    val visibleItemCount = listState.layoutInfo.visibleItemsInfo.size
    // This is for the edge case where selected team is partial visible
    val shouldAutoScroll = (visibleItemCount > 1) && (viewState.initialIndex >= visibleItemCount - 1)
    if (shouldAutoScroll) {
        listState.animateScrollToItem(viewState.initialIndex)
        onAutoScrollComplete()
    }
}

data class LeagueHubStandingsModule(
    val league: League
) : HubUi.HubTabModule {
    @Composable
    override fun Render(
        isActive: Boolean,
        fragmentManager: () -> FragmentManager
    ) {
        val navigator = rememberKoin<ScreenNavigator>(
            LocalContext.current
        )

        val viewModel = koinViewModel<LeagueHubStandingsViewModel>(
            parameters = { parametersOf(LeagueHubStandingsViewModel.Params(league = league)) }
        )

        LaunchedEffect(Unit) {
            viewModel.observe<LeagueHubStandingsContract.Event.NavigateToTeamHub>(this) { event ->
                navigator.startHubActivity(FeedType.Team(event.legacyTeamId))
            }
        }

        val state by viewModel.viewState.collectAsState(initial = null)
        val viewState = state ?: return
        val listState = rememberLazyListState()

        CompositionLocalProvider(
            LocalFeedInteractor provides viewModel,
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing = viewState.showSpinner),
                    indicator = { state, triggerDp ->
                        SwipeRefreshIndicator(
                            state = state,
                            refreshTriggerDistance = triggerDp
                        )
                    },
                    onRefresh = { viewModel.onRefresh() },
                    modifier = Modifier.weight(1f)
                ) {
                    if (viewState.showEmptyState) {
                        EmptyStateScreen(
                            titleResId = R.string.team_hub_standings_empty_title,
                            subTextResId = R.string.league_hub_standings_empty_subtitle,
                        )
                    } else {
                        FeedV2(
                            uiModel = viewState.feedUiModel,
                            isVisible = isActive,
                            listState = listState,
                            onViewVisibilityChanged = { _, _ -> },
                            bottomContentPadding = 0.dp,
                            verticalSpacing = 0.dp,
                        )
                    }
                }
                if (viewState.relegationLegendItemsV2.isNotEmpty()) {
                    RelegationLegend(relegationItems = viewState.relegationLegendItemsV2)
                }
            }
        }
    }
}

data class TeamHubStatsModule(
    val teamId: String,
    val leagueId: String,
) : HubUi.HubTabModule {
    @Composable
    override fun Render(
        isActive: Boolean,
        fragmentManager: () -> FragmentManager
    ) {
        val viewModel = rememberViewModel<TeamHubStatsViewModel>(
            TeamHubStatsViewModel.Params(
                teamId = teamId,
                leagueId = leagueId,
            ),
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
                if (viewState.showEmptyState) {
                    EmptyStateScreen(
                        titleResId = R.string.team_hub_stats_empty_title,
                        subTextResId = R.string.team_hub_stats_empty_subtitle,
                    )
                } else {
                    FeedV2(
                        uiModel = viewState.feedUiModel,
                        isVisible = isActive,
                        listState = listState,
                        onViewVisibilityChanged = { _, _ -> },
                        verticalSpacing = 0.dp,
                    )
                }
            }
        }
    }
}

data class TeamHubRosterModule(
    val teamId: String,
    val leagueId: String,
) : HubUi.HubTabModule {
    @Composable
    override fun Render(
        isActive: Boolean,
        fragmentManager: () -> FragmentManager
    ) {
        val viewModel = rememberViewModel<TeamHubRosterViewModel>(
            TeamHubRosterViewModel.Params(
                teamId = teamId,
                leagueId = leagueId,
            ),
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
                if (viewState.showEmptyState) {
                    EmptyStateScreen(
                        titleResId = R.string.team_hub_roster_empty_title,
                        subTextResId = R.string.team_hub_roster_empty_subtitle,
                    )
                } else {
                    FeedV2(
                        uiModel = viewState.feedUiModel,
                        isVisible = isActive,
                        listState = listState,
                        onViewVisibilityChanged = { _, _ -> },
                        verticalSpacing = 0.dp
                    )
                }
            }
        }
    }
}

data class TeamHubBracketsModule(
    val leagueCode: LeagueCode,
    val seasonId: String?,
) : HubUi.HubTabModule {
    @Composable
    override fun Render(
        isActive: Boolean,
        fragmentManager: () -> FragmentManager
    ) {
        BracketsScreen(
            viewModel = koinViewModel(
                parameters = { parametersOf(BracketsViewModel.Params(leagueCode, seasonId)) }
            ),
        )
    }
}

@Composable
private fun EmptyStateScreen(
    @StringRes titleResId: Int,
    @StringRes subTextResId: Int,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 80.dp)
                .align(Alignment.Center)
        ) {
            Text(
                text = stringResource(id = titleResId),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                color = AthTheme.colors.dark800,
                style = AthTextStyle.Calibre.Utility.Regular.Large,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = subTextResId),
                modifier = Modifier.fillMaxWidth(),
                color = AthTheme.colors.dark500,
                style = AthTextStyle.Calibre.Utility.Regular.Small,
                textAlign = TextAlign.Center
            )
        }
    }
}