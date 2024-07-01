package com.theathletic.scores.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.scores.R
import com.theathletic.scores.ui.gamecells.GameCell
import com.theathletic.scores.ui.gamecells.SectionFooter
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.LoadingState
import com.theathletic.ui.widgets.RoundedDropDownMenu

private const val KEY_GAMECELL = "Key_GameCell"
private const val KEY_TAB_BAR = "Key_TabBar"
private const val KEY_HEADER = "Key_Header"
private const val KEY_FOOTER = "Key_Footer"
private const val KEY_SKELETON = "Key_Skeleton"

@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel) {

    val viewState by viewModel.viewState.collectAsState()

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = viewState.fullLoadingState == LoadingState.RELOADING),
        onRefresh = { viewModel.refreshCurrentSchedule() }
    ) {
        when {
            viewState.fullLoadingState == LoadingState.INITIAL_LOADING -> ScheduleLoadingSkeleton(includeNavBar = true)
            viewState.showScheduleNoDataMessage -> NoDataAvailableMessage()
            viewState.showErrorLoadingDataMessage -> ErrorLoadingDataMessage()
            else -> ScheduleFeed(
                tabItems = viewState.scheduleTabs,
                feed = viewState.scheduleFeed,
                scheduleFilters = viewState.scheduleFilters?.map { it.label },
                selectedFilter = viewState.selectedFilter?.label,
                selectedTab = viewState.selectedTab,
                isLoading = viewState.partialLoadingState == LoadingState.INITIAL_LOADING,
                showFilters = viewState.showFilters,
                showNoGamesMessage = viewState.showNoGamesMessage,
                onNavigateToSchedule = { index, id -> viewModel.navigateToSchedule(index, id) },
                onNavigateToGame = { index, id, discussion -> viewModel.navigateToGame(index, id, discussion) },
                onTicketsClick = { url, provider -> viewModel.navigateToTicketsSite(url, provider) },
                onViewVisibilityChanged = { payload, visibility ->
                    viewModel.onViewVisibilityChanged(payload, visibility)
                },
                onFilterOptionSelected = { option, index -> viewModel.onFilterOptionSelected(option, index) }
            )
        }
    }
}

@Composable
private fun ScheduleFeed(
    tabItems: List<ScoresFeedUI.DayTabItem>,
    feed: List<ScoresFeedUI.FeedGroup>,
    scheduleFilters: List<String>?,
    selectedTab: Int,
    isLoading: Boolean,
    selectedFilter: String?,
    showFilters: Boolean,
    showNoGamesMessage: Boolean,
    onNavigateToSchedule: (Int, String) -> Unit,
    onNavigateToGame: (Int, String, Boolean) -> Unit,
    onTicketsClick: (String, String) -> Unit,
    onViewVisibilityChanged: (ImpressionPayload, Float) -> Unit,
    onFilterOptionSelected: (String, Int) -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(color = AthTheme.colors.dark100)
    ) {
        item(key = KEY_TAB_BAR) {
            Spacer(modifier = Modifier.height(4.dp))
            if (tabItems.isNotEmpty()) {
                ScoresDayTabBar(
                    tabItems = tabItems,
                    selectedTabIndex = selectedTab,
                    onTabClicked = onNavigateToSchedule
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
        scheduleFeedItems(
            feed = feed,
            selectedFilter = selectedFilter,
            scheduleFilters = scheduleFilters,
            isListLoading = isLoading,
            showFilters = showFilters,
            onNavigateToGame = onNavigateToGame,
            onTicketsClick = onTicketsClick,
            onFilterOptionSelected = onFilterOptionSelected
        )
    }

    if (showNoGamesMessage) {
        DisplayNoGamesMessage()
    }

    if (feed.isNotEmpty() && isLoading.not()) {
        ScoresFeedImpressionTracker(
            listState = listState,
            listItems = feed,
            onViewVisibilityChanged = onViewVisibilityChanged
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@SuppressWarnings("LongMethod")
private fun LazyListScope.scheduleFeedItems(
    feed: List<ScoresFeedUI.FeedGroup>,
    scheduleFilters: List<String>?,
    selectedFilter: String?,
    isListLoading: Boolean,
    showFilters: Boolean,
    onNavigateToGame: (Int, String, Boolean) -> Unit,
    onTicketsClick: (String, String) -> Unit,
    onFilterOptionSelected: (String, Int) -> Unit
) {
    var isFilterAdded = false
    feed.forEachIndexed { feedIndex, feedGroup ->
        val groupKey = feedGroup.header.id
        if (feedGroup.header.title != null) {
            stickyHeader(key = "$KEY_HEADER-$groupKey") {
                ScoresHeader(
                    group = feedGroup,
                    onLeagueSectionClicked = { _, _ -> /* todo Handle when implementing SLP */ }
                )
            }
        }
        if (feedGroup.widget != null) item { FeedWidget(feedGroup.widget, onTicketsClick) }

        if (isFilterAdded.not()) {
            addFilter(
                scheduleFilters,
                selectedFilter,
                showFilters,
                feedGroup.games.isEmpty(),
                onFilterOptionSelected,
            )
            isFilterAdded = true
        }
        if (isListLoading.not()) {
            itemsIndexed(
                feedGroup.games,
                key = { _, game -> "$KEY_GAMECELL-$feedIndex:${game.gameId}" }
            ) { index, game ->
                GameCell(
                    gameId = game.gameId,
                    title = game.title,
                    showTitle = game.showTitle,
                    firstTeam = game.firstTeam,
                    secondTeam = game.secondTeam,
                    infoWidget = game.infoWidget,
                    discussionLinkText = game.discussionLinkText,
                    showDivider = feedGroup.games.lastIndex != index,
                    showTeamRanking = game.showTeamRanking,
                    onGameClicked = { onNavigateToGame(index, game.gameId, false) },
                    onDiscussionLinkClicked = { onNavigateToGame(index, game.gameId, true) }
                )
            }
        } else {
            item(key = KEY_SKELETON) { ScheduleLoadingSkeleton(includeNavBar = false) }
        }
        if (feedGroup.footer?.leagueId != null) {
            item(key = "$KEY_FOOTER-$groupKey") {
                SectionFooter(
                    label = feedGroup.footer.label,
                    onClick = { /* todo Handle when implementing SLP */ }
                )
            }
        }
    }
    // add filter for if no games
    if (feed.isEmpty()) {
        addFilter(
            scheduleFilters,
            selectedFilter,
            showFilters,
            true,
            onFilterOptionSelected,
        )
    }
}

@Composable
fun DisplayNoGamesMessage() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(id = R.string.scores_schedule_no_game_on_this_date_message),
            color = AthTheme.colors.dark500,
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

private fun LazyListScope.addFilter(
    scheduleFilters: List<String>?,
    selectedFilter: String?,
    showFilters: Boolean,
    noScheduleGames: Boolean,
    onFilterOptionSelected: (String, Int) -> Unit,
) {
    if (showFilters) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = AthTheme.colors.dark200)
                    .padding(
                        start = 16.dp,
                        top = 16.dp,
                        bottom = if (noScheduleGames) 8.dp else 4.dp
                    )
            ) {
                RoundedDropDownMenu(
                    options = scheduleFilters ?: emptyList(),
                    selectedOption = selectedFilter.orEmpty(),
                    onOptionSelected = { option, index ->
                        onFilterOptionSelected(option, index)
                    }
                )
            }
        }
    }
}

@Composable
fun NoDataAvailableMessage() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_empty_feed),
                contentDescription = null,
                colorFilter = ColorFilter.tint(AthTheme.colors.dark500),
                modifier = Modifier.padding(bottom = 18.dp)
            )
            Text(
                text = stringResource(id = R.string.scores_schedule_no_game_information_message),
                color = AthTheme.colors.dark500,
                style = AthTextStyle.Calibre.Utility.Regular.Large,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun ErrorLoadingDataMessage() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(id = R.string.global_error),
            color = AthTheme.colors.dark500,
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
        )
    }
}