package com.theathletic.brackets.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerDefaults
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.theathletic.brackets.data.local.TournamentRoundGame
import com.theathletic.brackets.navigation.BracketsNavigator
import com.theathletic.brackets.ui.components.BracketsTabRow
import com.theathletic.brackets.ui.components.HeaderRowUi
import com.theathletic.brackets.ui.components.LabelText
import com.theathletic.brackets.ui.components.MatchLayout
import com.theathletic.brackets.ui.components.RoundsPage
import com.theathletic.data.SizedImages
import com.theathletic.themes.AthTheme
import com.theathletic.ui.LoadingState
import com.theathletic.ui.collectWithLifecycle
import com.theathletic.ui.utility.rememberKoin
import com.theathletic.ui.widgets.SwipeRefreshIndicator
import dev.chrisbanes.snapper.ExperimentalSnapperApi

class BracketsUi {
    sealed interface Round {
        val groups: List<Group>

        data class Pre(
            override val groups: List<Group>
        ) : Round

        data class Initial(
            override val groups: List<Group>
        ) : Round

        data class Standard(
            override val groups: List<Group>
        ) : Round

        data class SemiFinal(
            override val groups: List<Group>
        ) : Round

        data class Final(
            override val groups: List<Group>
        ) : Round
    }

    data class Group(
        val label: String,
        val matches: List<Match>,
    )

    data class Match(
        val id: String,
        val dateAndTimeText: String,
        val firstTeam: Team,
        val secondTeam: Team,
        val hasBoxScore: Boolean,
        val phase: TournamentRoundGame.Phase?,
        val showConnector: Boolean = true
    )

    sealed interface Team {
        val name: String
        val logos: SizedImages

        data class PostGameTeam(
            override val name: String,
            override val logos: SizedImages,
            val score: String,
            val isWinner: Boolean?
        ) : Team

        data class PreGameTeam(
            override val name: String,
            override val logos: SizedImages,
            val seed: String,
            val record: String,
        ) : Team

        data class PlaceholderTeam(
            override val name: String = "",
            override val logos: SizedImages = emptyList()
        ) : Team

        fun isValidTeam() = this !is PlaceholderTeam
        fun isPlaceholderTeam() = this is PlaceholderTeam
    }

    companion object {
        val CONNECTOR_WIDTH = 1.dp
        const val ANIMATION_OFFSET = .9f
        val connectorColor
            @Composable
            @ReadOnlyComposable
            get() = AthTheme.colors.dark400
    }
}

@Composable
fun BracketsScreen(viewModel: BracketsViewModel) {
    val navigator = rememberKoin<BracketsNavigator>(LocalContext.current)
    viewModel.eventConsumer.collectWithLifecycle { event ->
        if (event is Event.NavigateToGameDetails) navigator.navigateToGameDetails(event.gameId)
    }
    val state by viewModel.uiState
    if (state.loadingState == LoadingState.INITIAL_LOADING) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AthTheme.colors.dark100)
        ) {
            CircularProgressIndicator(
                color = AthTheme.colors.dark700,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else {
        Bracket(
            rounds = state.rounds,
            tabs = state.tabs,
            currentTabIndex = state.currentTabIndex ?: 0,
            showRefreshSpinner = state.loadingState == LoadingState.RELOADING,
            onTabSelected = { index -> viewModel.onEvent(BracketsEvent.OnRoundSelected(index)) },
            onMatchClicked = { match -> viewModel.onEvent(BracketsEvent.OnMatchClicked(match)) },
            onPullToRefresh = { viewModel.onEvent(BracketsEvent.OnPullToRefresh) },
            onMatchReplayClicked = { matchId -> viewModel.onEvent(BracketsEvent.OnReplayMatchClicked(matchId)) },
        )
    }
}

@Suppress("LongMethod")
@OptIn(ExperimentalPagerApi::class, ExperimentalSnapperApi::class)
@Composable
private fun Bracket(
    rounds: List<BracketsUi.Round>,
    tabs: List<HeaderRowUi.BracketTab>,
    currentTabIndex: Int,
    showRefreshSpinner: Boolean,
    onTabSelected: (Int) -> Unit,
    onMatchClicked: (BracketsUi.Match) -> Unit,
    onPullToRefresh: () -> Unit,
    onMatchReplayClicked: (String) -> Unit,
) {
    val localDensity = LocalDensity.current
    val pagerState = rememberPagerState(currentTabIndex)
    var matchHalfHeight by remember { mutableStateOf(IntSize.Zero.height.dp) }
    var labelHalfHeight by remember { mutableStateOf(IntSize.Zero.height.dp) }

    LaunchedEffect(key1 = currentTabIndex) {
        pagerState.animateScrollToPage(currentTabIndex)
    }

    LaunchedEffect(pagerState.currentPage) {
        onTabSelected(pagerState.currentPage)
    }

    Column {
        BracketsTabRow(
            currentTabIndex = pagerState.currentPage,
            tabs = tabs,
            onTabSelectedClick = { index -> onTabSelected(index) }
        )
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = showRefreshSpinner),
            indicator = { swipeState, triggerDp ->
                SwipeRefreshIndicator(
                    state = swipeState,
                    refreshTriggerDistance = triggerDp
                )
            },
            onRefresh = onPullToRefresh,
        ) {
            HorizontalPager(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                count = rounds.size,
                verticalAlignment = Alignment.Top,
                contentPadding = PaddingValues(end = 64.dp),
                state = pagerState,
                flingBehavior = PagerDefaults.flingBehavior(
                    state = pagerState,
                    maximumFlingDistance = { 1f }
                ),
                key = { index -> index }
            ) { currentIndex ->
                val round = rounds[currentIndex]
                RoundsPage(
                    pagerState = pagerState,
                    round = round,
                    currentIndex = currentIndex,
                    matchHalfHeight = matchHalfHeight,
                    labelHalfHeight = labelHalfHeight,
                    label = { text ->
                        LabelText(
                            text = text,
                            // we want to measure the height of the label layout on the initial round,
                            // we are not guaranteed to have a pre-round
                            modifier = Modifier.onGloballyPositioned { coordinates ->
                                val currentHalfHeight = with(localDensity) { coordinates.size.height.toDp() / 2 }
                                if (labelHalfHeight < currentHalfHeight) {
                                    labelHalfHeight = currentHalfHeight
                                }
                            }
                        )
                    },
                    matchLayout = { match ->
                        MatchLayout(
                            match = match,
                            // we want to measure the height of the match layout on the initial round,
                            // we are not guaranteed to have a pre-round
                            modifier = Modifier.onGloballyPositioned { coordinates ->
                                val currentHalfHeight = with(localDensity) { coordinates.size.height.toDp() / 2 }
                                if (matchHalfHeight < currentHalfHeight) {
                                    matchHalfHeight = currentHalfHeight
                                }
                            },
                            onMatchClicked = onMatchClicked,
                            onMatchReplayClicked = onMatchReplayClicked
                        )
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun BracketsPreview() {
    Bracket(
        rounds = BracketsPreviewData.rounds,
        tabs = BracketsPreviewData.tabs,
        currentTabIndex = BracketsPreviewData.currentRoundIndex,
        showRefreshSpinner = false,
        onMatchClicked = {},
        onTabSelected = {},
        onPullToRefresh = {},
        onMatchReplayClicked = {},
    )
}