package com.theathletic.scores.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.theathletic.data.SizedImages
import com.theathletic.hub.HubTabType
import com.theathletic.scores.navigation.ScoresFeedNavigator
import com.theathletic.scores.ui.gamecells.GameCellModel
import com.theathletic.themes.AthTheme
import com.theathletic.ui.ResourceString
import com.theathletic.ui.animation.ViewSlideAnimation
import com.theathletic.ui.collectWithLifecycle
import com.theathletic.ui.utility.rememberKoin
import kotlinx.coroutines.delay

sealed class ScoresFeedUI {
    data class DayTabItem(
        val id: String,
        val labelTop: ResourceString,
        val labelBottom: ResourceString,
        val payload: AnalyticsPayload? = null
    )

    data class ScheduleFilter(
        val id: String,
        val isDefault: Boolean,
        val label: String
    )

    data class FeedGroup(
        val header: SectionHeader,
        val games: List<GameCellModel>,
        val footer: SectionFooter?,
        val widget: FeedWidget?
    )

    data class SectionHeader(
        val id: String,
        val title: String?,
        val subTitle: String?,
        val canNavigate: Boolean,
        val leagueId: Long?,
        val index: Int,
    )

    data class SectionFooter(
        val id: String,
        val label: String,
        val leagueId: Long?,
        val index: Int,
    )

    interface FeedWidget

    data class GameTicketsWidget(
        val logosDark: SizedImages,
        val logosLight: SizedImages,
        val text: String,
        val uri: String,
        val provider: String
    ) : FeedWidget

    data class AnalyticsPayload(
        val slate: String
    )
}

@Suppress("LongMethod")
@Composable
fun ScoresFeedScreen(
    viewModel: ScoresFeedViewModel,
    onSearchBarClick: () -> Unit,
    animateSearchBar: Boolean = false
) {
    val navigator = rememberKoin<ScoresFeedNavigator>(LocalContext.current)
    viewModel.eventConsumer.collectWithLifecycle { event ->
        when (event) {
            is ScoresFeedContract.Event.NavigateToHub ->
                navigator.navigateToHubActivity(event.feedType, HubTabType.Schedule)
            is ScoresFeedContract.Event.NavigateToGame ->
                navigator.navigateToGame(event.gameId, event.showDiscussion, "scores")
        }
    }

    val state by viewModel.viewState.collectAsState(initial = null)
    val viewState = state ?: return

    // We are using two search bars here, the duplicate search bar is used for the animation sake
    // As we want to show search bar sliding down from the previous search screen
    // therefore using a duplicate search bar to slide down
    // once the animation is completed we show the rest of the content and hide the duplicate
    // search bar

    var showFeedList by remember { mutableStateOf(false) }
    var showDuplicateSearchBar by remember { mutableStateOf(true) }

    if (viewState.isLoadingFullFeed) {
        ScoresFeedPlaceholder()
    } else {
        ScoresFeed(
            viewState,
            viewModel,
            onSearchBarClick,
            showFeedList = showFeedList,
            showDuplicateSearchBar = showDuplicateSearchBar,
            animateSearchBar = animateSearchBar,
            loadingDayFeed = viewState.isLoadingDayFeed
        )
    }

    LaunchedEffect(Unit) {
        delay(550)
        showFeedList = true
        showDuplicateSearchBar = false
    }
}

@Composable
private fun ScoresFeed(
    viewState: ScoresFeedContract.ViewState,
    viewModel: ScoresFeedViewModel,
    onSearchBarClick: () -> Unit,
    animateSearchBar: Boolean,
    showDuplicateSearchBar: Boolean,
    showFeedList: Boolean,
    loadingDayFeed: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AthTheme.colors.dark200)
    ) {
        if (animateSearchBar) {
            DuplicateSearchBar(showDuplicateSearchBar, onSearchBarClick, animateSearchBar)
        }

        if (showFeedList || animateSearchBar.not()) {
            ScoresPageHeader(
                navItems = viewState.navigationItems,
                onLeagueItemClick = { id, index ->
                    viewModel.onEvent(ScoresFeedContract.Interaction.OnNavItemClicked(id, index))
                }
            )
            ScoresFeedList(
                listItems = viewState.dayFeed,
                loadingDayFeed = loadingDayFeed,
                onLeagueSectionClicked = { leagueId, index ->
                    viewModel.onEvent(ScoresFeedContract.Interaction.OnLeagueSectionClicked(leagueId, index))
                },
                onAllGamesClicked = { leagueId, index ->
                    viewModel.onEvent(ScoresFeedContract.Interaction.OnAllGamesClicked(leagueId, index))
                },
                onGameClicked = { gameId ->
                    viewModel.onEvent(ScoresFeedContract.Interaction.OnGameClicked(gameId))
                },
                onDiscussionLinkClicked = { gameId, leagueId ->
                    viewModel.onEvent(ScoresFeedContract.Interaction.OnDiscussionLinkClicked(gameId, leagueId))
                },
                onViewVisibilityChanged = { impressionPayload, pctVisible ->
                    viewModel.onViewVisibilityChanged(impressionPayload, pctVisible)
                },
                onRefresh = {
                    viewModel.onEvent(ScoresFeedContract.Interaction.OnPullToRefresh)
                },
                collapsableHeader = {
                    CollapsingHeader(onSearchBarClick, viewState, viewModel)
                }
            )
        }
    }
}

@Composable
private fun CollapsingHeader(
    onSearchBarClick: () -> Unit,
    viewState: ScoresFeedContract.ViewState,
    viewModel: ScoresFeedViewModel
) {
    ScoreSearchBar(onSearchBarClick)
    Divider(
        modifier = Modifier
            .height(4.dp),
        color = AthTheme.colors.dark100
    )
    ScoresDayTabBar(
        tabItems = viewState.dayTabList,
        selectedTabIndex = viewState.selectedDayIndex,
        onTabClicked = { index, dayId ->
            viewModel.onEvent(ScoresFeedContract.Interaction.OnTabClicked(index, dayId))
        }
    )
}

@Composable
private fun DuplicateSearchBar(
    showDuplicateSearchBar: Boolean,
    onSearchBarClick: () -> Unit,
    animateSearchBar: Boolean
) {
    ViewSlideAnimation(
        view = {
            if (showDuplicateSearchBar) {
                ScoreSearchBar(
                    onSearchBarClick,
                    backgroundColor = Color.Transparent
                )
            }
        },
        slideDown = true,
        performAnimation = animateSearchBar
    )
}