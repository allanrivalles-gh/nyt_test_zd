package com.theathletic.hub.ui

import androidx.compose.animation.core.FloatExponentialDecaySpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.theathletic.data.SizedImages
import com.theathletic.hub.HubTabType
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.animation.ToolbarState
import com.theathletic.ui.animation.rememberToolbarState
import com.theathletic.ui.asString
import com.theathletic.ui.utility.conditional
import com.theathletic.ui.utility.getContrastColor
import com.theathletic.ui.utility.parseHexColor
import com.theathletic.ui.widgets.RemoteImageAsync
import com.theathletic.ui.widgets.TeamLogo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

sealed class HubUi(
    open val tabs: List<HubTab>,
    open val tabIndexes: Map<HubTabType, Int>,
    open val currentTab: HubTabType,
    open val showLoadingSpinner: Boolean
) {

    data class Team(
        val teamHeader: TeamHeader,
        override val tabs: List<HubTab>,
        override val tabIndexes: Map<HubTabType, Int>,
        override val currentTab: HubTabType,
        override val showLoadingSpinner: Boolean
    ) : HubUi(tabs, tabIndexes, currentTab, showLoadingSpinner) {
        data class TeamHeader(
            val teamLogos: SizedImages,
            val teamName: String,
            val currentStanding: String,
            val backgroundColor: String?,
            val isFollowed: Boolean
        )
    }

    data class League(
        val leagueHeader: LeagueHeader,
        override val tabs: List<HubTab>,
        override val tabIndexes: Map<HubTabType, Int>,
        override val currentTab: HubTabType,
        override val showLoadingSpinner: Boolean
    ) : HubUi(tabs, tabIndexes, currentTab, showLoadingSpinner) {
        data class LeagueHeader(
            val logoUrl: String,
            val name: String,
            val isFollowed: Boolean
        )
    }

    interface HubTabModule {
        @Composable
        fun Render(
            isActive: Boolean,
            fragmentManager: () -> FragmentManager
        )
    }

    data class HubTab(
        val type: HubTabType,
        val label: ResourceString,
        val module: HubTabModule
    )

    interface Interactor {
        fun onBackButtonClicked()
        fun onManageFollowClicked()
        fun onManageNotificationsClicked()
        fun onTabClicked(fromTab: HubTabType, toTab: HubTabType)
    }
}

/**
 * The Team Hub has a collapsing header. Compose has not got great support for these at the
 * moment so borrowed a lot from this article and adapted for this current use case:
 * https://medium.com/kotlin-and-kotlin-for-android/collapsing-toolbar-in-jetpack-compose-problem-solutions-and-alternatives-34c9c5986ea0
 */

private val ExpandedTeamHeaderHeight = 110.dp
private val ExpandedLeagueHeaderHeight = 90.dp
private val CollapsedHeaderHeight = 40.dp

private class SmoothNestedScrollConnection(
    private val coroutineScope: CoroutineScope,
    private val toolbarState: ToolbarState,
    private val listState: LazyListState
) : NestedScrollConnection {

    enum class HeaderState {
        Collapsing,
        Static,
        Expanding
    }

    private var headerState: HeaderState = HeaderState.Static

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        // The data binding feeds send positive and negative offsets together when scrolled slowly
        // causing the scrolling and header animation to stutter. Below helps smooth that out
        // by removing the ones that are not required for that direction of scrolling
        headerState = when {
            (headerState == HeaderState.Static && available.y < 0) -> HeaderState.Collapsing
            (headerState == HeaderState.Static && available.y > 0) -> HeaderState.Expanding
            (headerState == HeaderState.Collapsing && available.y > 0) -> HeaderState.Static
            (headerState == HeaderState.Expanding && available.y < 0) -> HeaderState.Static
            else -> headerState
        }

        return if (headerState != HeaderState.Static) {
            toolbarState.scrollTopLimitReached =
                listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
            toolbarState.scrollOffset = toolbarState.scrollOffset - available.y
            Offset(0f, toolbarState.consumed)
        } else {
            super.onPreScroll(available, source)
        }
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        if (available.y > 0) {
            coroutineScope.launch {
                animateDecay(
                    initialValue = toolbarState.height + toolbarState.offset,
                    initialVelocity = available.y,
                    animationSpec = FloatExponentialDecaySpec()
                ) { value, _ ->
                    toolbarState.scrollTopLimitReached =
                        listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
                    toolbarState.scrollOffset =
                        toolbarState.scrollOffset - (value - (toolbarState.height + toolbarState.offset))
                    if (toolbarState.scrollOffset == 0f) coroutineScope.coroutineContext.cancelChildren()
                }
            }
        }
        return super.onPostFling(consumed, available)
    }
}

@Composable
fun TeamHubScreen(
    teamHub: HubUi.Team,
    fragmentManager: () -> FragmentManager,
    interactor: HubUi.Interactor,
) {
    val coroutineScope = rememberCoroutineScope()
    val toolbarState = rememberToolbarState(
        with(LocalDensity.current) {
            CollapsedHeaderHeight.roundToPx()..ExpandedTeamHeaderHeight.roundToPx()
        }
    )
    val systemUiController = rememberSystemUiController()
    val listState = rememberLazyListState()

    val backgroundColor = teamHub.teamHeader.backgroundColor.toBackgroundColor()
    val foregroundColor = backgroundColor.getContrastColor()

    if (!teamHub.showLoadingSpinner) {
        systemUiController.setStatusBarColor(
            color = teamHub.teamHeader.backgroundColor.toBackgroundColor()
        )
    }

    val nestedScrollConnection = remember { SmoothNestedScrollConnection(coroutineScope, toolbarState, listState) }

    if (teamHub.showLoadingSpinner) {
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
        Box(modifier = Modifier.nestedScroll(nestedScrollConnection)) {
            Column {
                TeamHubHeader(
                    teamHeader = teamHub.teamHeader,
                    height = toolbarState.height,
                    foregroundColor = foregroundColor,
                    backgroundColor = backgroundColor,
                    collapsedScale = toolbarState.progress,
                    onBackButtonClicked = { interactor.onBackButtonClicked() },
                    onManageFollowClicked = { interactor.onManageFollowClicked() },
                    onManageNotificationsClicked = { interactor.onManageNotificationsClicked() },
                )
                HubTabLayout(
                    tabs = teamHub.tabs,
                    tabIndexes = teamHub.tabIndexes,
                    foregroundColor = foregroundColor,
                    backgroundColor = backgroundColor,
                    fragmentManager = fragmentManager,
                    currentTab = teamHub.currentTab,
                    onTabSelectionClick = { toTab ->
                        interactor.onTabClicked(
                            fromTab = teamHub.currentTab,
                            toTab = toTab
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun LeagueHubScreen(
    leagueHub: HubUi.League,
    fragmentManager: () -> FragmentManager,
    interactor: HubUi.Interactor,
) {
    val coroutineScope = rememberCoroutineScope()
    val toolbarState = rememberToolbarState(
        with(LocalDensity.current) {
            CollapsedHeaderHeight.roundToPx()..ExpandedLeagueHeaderHeight.roundToPx()
        }
    )
    val systemUiController = rememberSystemUiController()
    val listState = rememberLazyListState()

    if (!leagueHub.showLoadingSpinner) {
        systemUiController.setStatusBarColor(
            color = AthColor.Gray300
        )
    }

    val nestedScrollConnection = remember { SmoothNestedScrollConnection(coroutineScope, toolbarState, listState) }

    if (leagueHub.showLoadingSpinner) {
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
        Box(modifier = Modifier.nestedScroll(nestedScrollConnection)) {
            Column {
                LeagueHubHeader(
                    leagueHeader = leagueHub.leagueHeader,
                    height = toolbarState.height,
                    foregroundColor = AthColor.Gray800,
                    backgroundColor = AthColor.Gray300,
                    collapsedScale = toolbarState.progress,
                    onBackButtonClicked = { interactor.onBackButtonClicked() },
                    onManageFollowClicked = { interactor.onManageFollowClicked() },
                    onManageNotificationsClicked = { interactor.onManageNotificationsClicked() },
                )
                HubTabLayout(
                    tabs = leagueHub.tabs,
                    tabIndexes = leagueHub.tabIndexes,
                    foregroundColor = AthColor.Gray800,
                    backgroundColor = AthColor.Gray300,
                    fragmentManager = fragmentManager,
                    currentTab = leagueHub.currentTab,
                    onTabSelectionClick = { toTab ->
                        interactor.onTabClicked(
                            fromTab = leagueHub.currentTab,
                            toTab = toTab
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun String?.toBackgroundColor() = this.parseHexColor(AthColor.Gray500)

private fun Float.adjustAlpha(upper: Float, lower: Float) = when {
    this >= upper -> 1f
    this < lower -> 0f
    else -> this - lower
}

@Composable
private fun TeamHubHeader(
    teamHeader: HubUi.Team.TeamHeader,
    foregroundColor: Color,
    backgroundColor: Color,
    height: Float,
    collapsedScale: Float,
    onBackButtonClicked: () -> Unit,
    onManageFollowClicked: () -> Unit,
    onManageNotificationsClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(with(LocalDensity.current) { height.toDp() })
            .background(
                color = backgroundColor
            )
            .padding(horizontal = 8.dp)
            .padding(top = 12.dp, bottom = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackButtonClicked
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = foregroundColor,
                )
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .weight(1f)
            ) {
                TeamLogo(
                    teamUrls = teamHeader.teamLogos,
                    preferredSize = 32.dp,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(collapsedScale.adjustAlpha(0.9f, 0.5f))
                )
            }
            FollowMenu(
                teamHeader.isFollowed,
                foregroundColor,
                onManageFollowClicked,
                onManageNotificationsClicked,
            )
        }
        Text(
            text = teamHeader.teamName,
            style = AthTextStyle.Slab.Bold.Small,
            color = foregroundColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(BiasAlignment(0f, 0.45f * collapsedScale))
        )
        Text(
            text = teamHeader.currentStanding,
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            color = foregroundColor,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(collapsedScale.adjustAlpha(0.9f, 0.4f))
        )
    }
}

@Composable
private fun LeagueHubHeader(
    leagueHeader: HubUi.League.LeagueHeader,
    height: Float,
    foregroundColor: Color,
    backgroundColor: Color,
    collapsedScale: Float,
    onBackButtonClicked: () -> Unit,
    onManageFollowClicked: () -> Unit,
    onManageNotificationsClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(with(LocalDensity.current) { height.toDp() })
            .background(
                color = backgroundColor
            )
            .padding(horizontal = 8.dp)
            .padding(top = 12.dp, bottom = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackButtonClicked
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = foregroundColor,
                )
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .weight(1f)
            ) {
                RemoteImageAsync(
                    url = leagueHeader.logoUrl,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(collapsedScale.adjustAlpha(0.9f, 0.5f)),
                    error = R.drawable.ic_team_logo_placeholder
                )
            }
            FollowMenu(
                isFollowed = leagueHeader.isFollowed,
                foregroundColor = foregroundColor,
                onManageFollowClicked = onManageFollowClicked,
                onManageNotificationsClicked = onManageNotificationsClicked,
            )
        }
        Text(
            text = leagueHeader.name,
            style = AthTextStyle.Slab.Bold.Small,
            color = foregroundColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(BiasAlignment(0f, 0.8f * collapsedScale))
        )
    }
}

@Composable
private fun FollowMenu(
    isFollowed: Boolean,
    foregroundColor: Color,
    onManageFollowClicked: () -> Unit,
    onManageNotificationsClicked: () -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }

    if (isFollowed) {
        Box {
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(
                    Icons.Default.MoreHoriz,
                    contentDescription = null,
                    tint = foregroundColor,
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier
                    .background(color = AthTheme.colors.dark300)
                    .width(220.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showMenu = false
                            onManageFollowClicked()
                        }
                        .padding(vertical = 10.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.team_hub_unfollow_menu_label),
                        style = AthTextStyle.Calibre.Utility.Regular.ExtraLarge,
                        color = AthTheme.colors.dark800,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        tint = AthTheme.colors.dark800,
                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.CenterEnd)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showMenu = false
                            onManageNotificationsClicked()
                        }
                        .padding(vertical = 10.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.team_hub_notifications_menu_label),
                        style = AthTextStyle.Calibre.Utility.Regular.ExtraLarge,
                        color = AthTheme.colors.dark800,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = null,
                        tint = AthTheme.colors.dark800,
                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.CenterEnd)
                    )
                }
            }
        }
    } else {
        IconButton(onClick = { onManageFollowClicked() }) {
            Icon(
                Icons.Default.AddCircleOutline,
                contentDescription = null,
                tint = foregroundColor,
            )
        }
    }
}

@Composable
private fun HubTabLayout(
    tabs: List<HubUi.HubTab>,
    tabIndexes: Map<HubTabType, Int>,
    foregroundColor: Color,
    backgroundColor: Color,
    currentTab: HubTabType,
    onTabSelectionClick: (toTab: HubTabType) -> Unit,
    fragmentManager: () -> FragmentManager,
) {
    val selectedTabIndex by remember(currentTab) { mutableStateOf(tabIndexes[currentTab] ?: 0) }

    if (tabs.size > 1) {
        HubTabLayoutContent(selectedTabIndex, backgroundColor, foregroundColor, tabs, currentTab, onTabSelectionClick)
    } else {
        Spacer(
            modifier = Modifier
                .height(8.dp)
                .fillMaxWidth()
                .background(backgroundColor)
        )
    }

    if (tabs.isNotEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            tabs[selectedTabIndex].module.Render(
                isActive = true,
                fragmentManager = fragmentManager
            )
        }
    }
}

@Composable
private fun HubTabLayoutContent(
    selectedTabIndex: Int,
    backgroundColor: Color,
    foregroundColor: Color,
    tabs: List<HubUi.HubTab>,
    currentTab: HubTabType,
    onTabSelectionClick: (toTab: HubTabType) -> Unit
) {
    HubTabRowLayout(
        scrollableTabsLayout = {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                backgroundColor = backgroundColor,
                contentColor = foregroundColor,
                edgePadding = 0.dp,
            ) {
                HubTabs(
                    tabs = tabs,
                    currentTab = currentTab,
                    foregroundColor = foregroundColor,
                    requiresPadding = true,
                    onTabSelectedClick = onTabSelectionClick
                )
            }
        },
        fixedTabsLayout = {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                backgroundColor = backgroundColor,
                contentColor = foregroundColor,
            ) {
                HubTabs(
                    tabs = tabs,
                    currentTab = currentTab,
                    foregroundColor = foregroundColor,
                    onTabSelectedClick = onTabSelectionClick
                )
            }
        },
        tabs = {
            HubTabs(
                tabs = tabs,
                currentTab = currentTab,
                foregroundColor = foregroundColor,
                onTabSelectedClick = onTabSelectionClick
            )
        }
    )
}

@Composable
private fun HubTabs(
    tabs: List<HubUi.HubTab>,
    currentTab: HubTabType,
    foregroundColor: Color,
    requiresPadding: Boolean = false,
    onTabSelectedClick: (toTab: HubTabType) -> Unit
) {
    tabs.forEachIndexed { _, tab ->
        Tab(
            selected = false,
            onClick = {
                onTabSelectedClick(tab.type)
            },
            text = {
                Text(
                    text = tab.label.asString(),
                    style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge,
                    color = foregroundColor,
                    overflow = TextOverflow.Clip,
                    maxLines = 1,
                    modifier = Modifier
                        .alpha(if (tab.type == currentTab) 1f else 0.75f),
                )
            },
            modifier = Modifier.conditional(requiresPadding) {
                padding(horizontal = 8.dp)
            }
        )
    }
}

@Composable
fun HubTabRowLayout(
    scrollableTabsLayout: @Composable @UiComposable () -> Unit,
    fixedTabsLayout: @Composable @UiComposable () -> Unit,
    tabs: @Composable @UiComposable () -> Unit,
) {

    SubcomposeLayout(Modifier.fillMaxWidth()) { constraints ->
        val tabRowWidth = constraints.maxWidth
        val tabMeasurables = subcompose("Tabs", tabs)
        val tabCount = tabMeasurables.size
        val tabWidth = (tabRowWidth / tabCount)

        val tabPlaceables = tabMeasurables.map {
            it.measure(constraints.copy(minWidth = tabWidth, maxWidth = tabWidth))
        }
        val tabRowHeight = tabPlaceables.maxByOrNull { it.height }?.height ?: 0

        val maxTabWidth = tabMeasurables.maxOfOrNull { it.maxIntrinsicWidth(tabRowHeight) } ?: tabWidth
        val useScrollableTabRow = maxTabWidth.times(tabCount) > tabRowWidth

        layout(tabRowWidth, tabRowHeight) {
            val hideConstraint = constraints.copy(minWidth = 0, minHeight = 0, maxWidth = 0, maxHeight = 0)
            val showConstraint = constraints.copy(
                minWidth = tabRowWidth,
                minHeight = tabRowHeight,
                maxWidth = tabRowWidth,
                maxHeight = tabRowHeight
            )

            subcompose("Scrollable", scrollableTabsLayout).forEach {
                if (useScrollableTabRow) {
                    it.measure(showConstraint).place(0, 0)
                } else {
                    it.measure(hideConstraint).place(0, 0)
                }
            }

            subcompose("Fixed", fixedTabsLayout).forEach {
                if (useScrollableTabRow) {
                    it.measure(hideConstraint).place(0, 0)
                } else {
                    it.measure(showConstraint).place(0, 0)
                }
            }
        }
    }
}

@Preview
@Composable
fun TeamHubPreview() {
    TeamHubScreen(
        teamHub = previewDataTeamHubHeader,
        interactor = PreviewDataTeamHubInteractor,
        fragmentManager = { PreviewDataTeamHubFragManager },
    )
}

@Preview
@Composable
private fun TeamHubPreview_Light() {
    AthleticTheme(lightMode = true) {
        TeamHubScreen(
            teamHub = previewDataTeamHubHeader,
            interactor = PreviewDataTeamHubInteractor,
            fragmentManager = { PreviewDataTeamHubFragManager },
        )
    }
}

@Preview
@Composable
private fun TeamHubPreview_Loading() {
    TeamHubScreen(
        teamHub = previewDataTeamHubHeader_loadingData,
        interactor = PreviewDataTeamHubInteractor,
        fragmentManager = { PreviewDataTeamHubFragManager },
    )
}