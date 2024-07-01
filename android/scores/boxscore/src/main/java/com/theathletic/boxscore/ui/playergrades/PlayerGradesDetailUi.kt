@file:OptIn(ExperimentalPagerApi::class)

package com.theathletic.boxscore.ui.playergrades

import androidx.annotation.StringRes
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.theathletic.boxscore.ui.InGameInformationHeader
import com.theathletic.boxscore.ui.PostGameInformationHeader
import com.theathletic.data.SizedImages
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asString
import com.theathletic.ui.utility.conditional
import com.theathletic.ui.utility.isLightContrast
import com.theathletic.ui.utility.parseHexColor
import com.theathletic.ui.widgets.RemoteImageAsync
import com.theathletic.ui.widgets.ResourceIcon
import com.theathletic.ui.widgets.TeamLogo
import com.theathletic.ui.widgets.ViewportPagerIndicator
import com.theathletic.ui.widgets.getPreferred
import kotlinx.coroutines.launch

class PlayerGradesDetailUi(
    val teamBackground: String?,
    val teamLogos: SizedImages,
    val gameStatus: GameStatus,
    val players: List<Player>,
    val initialPlayerIndex: Int,
    val isLocked: Boolean
) {
    data class Player(
        val id: String,
        val name: String,
        val headshots: SizedImages,
        val details: ResourceString,
        val statisticsSummaryList: List<StatisticsSummary>,
        val statisticsFullList: List<StatisticsSummary>,
        val grade: PlayerGrade
    )

    data class GameStatus(
        val showLiveGameDetails: Boolean,
        val firstTeamLogos: SizedImages,
        val firstTeamScore: Int,
        val secondTeamLogos: SizedImages,
        val secondTeamScore: Int,
        val scheduledDate: String,
        val gameStatePrimary: String?,
        val gameStateSecondary: String?,
        val showGameStatePrimary: Boolean,
        val showGameStateSecondary: Boolean
    )

    data class StatisticsSummary(
        val label: String,
        val value: String
    )

    data class PlayerGrade(
        val state: GradingState,
        val grading: Int,
        val averageGrade: String,
        val totalGradings: Int
    )

    enum class GradingState {
        UNGRADED,
        GRADED,
        SUBMITTING,
        LOCKED_UNGRADED,
        LOCKED_GRADED,
    }

    sealed class Interactor : PlayerGradesInteraction {
        object OnCloseButtonClick : Interactor()
        data class OnGradingPlayer(val grade: Int) : Interactor()
        object OnShowAllPlayersClick : Interactor()
        data class OnPlayerIndexChanged(val index: Int, val toNext: Boolean, val viaClick: Boolean) : Interactor()
    }
}

private const val MAX_STARS = 5

val LocalPlayerGradesInteractor = staticCompositionLocalOf { EmptyPlayerGradesInteractor }
var navigationViaClick: Boolean = false

@Composable
fun PlayerGradesDetailScreen(
    showSpinner: Boolean,
    uiModel: PlayerGradesDetailUi?
) {
    val interactor = LocalPlayerGradesInteractor.current

    if (showSpinner || uiModel == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AthTheme.colors.dark200)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = AthTheme.colors.dark800
            )
        }
    } else {
        val pagerState = rememberPagerState(initialPage = uiModel.initialPlayerIndex)
        var previousPageIndex by remember { mutableStateOf(-1) }

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect {
                if (previousPageIndex != -1) {
                    interactor.send(
                        PlayerGradesDetailUi.Interactor.OnPlayerIndexChanged(
                            index = pagerState.currentPage,
                            toNext = pagerState.currentPage > previousPageIndex,
                            viaClick = navigationViaClick
                        )
                    )
                }
                previousPageIndex = pagerState.currentPage
                navigationViaClick = false
            }
        }

        PlayerGradeScreen(uiModel, pagerState)
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun PlayerGradeScreen(uiModel: PlayerGradesDetailUi, pagerState: PagerState) {
    val systemUiController = rememberSystemUiController()

    val background = uiModel.teamBackground.parseHexColor(AthTheme.colors.dark500)
    systemUiController.setStatusBarColor(color = background)
    val (contentColor, useDarkIcons) =
        if (background.isLightContrast()) {
            Pair(AthColor.Gray800, false)
        } else {
            Pair(AthColor.Gray100, true)
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AthTheme.colors.dark200),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            count = uiModel.players.size,
            state = pagerState,
            modifier = Modifier.weight(1f),
            key = { uiModel.players[it].id }
        ) { page ->
            PlayerGradePage(
                page = page,
                pagerState = pagerState,
                uiModel = uiModel,
                background = background,
                contentColor = contentColor,
                useDarkIcons = useDarkIcons
            )
        }
        PageIndicators(pagerState = pagerState)
    }
}

@Composable
private fun PlayerGradePage(
    page: Int,
    pagerState: PagerState,
    uiModel: PlayerGradesDetailUi,
    background: Color,
    contentColor: Color,
    useDarkIcons: Boolean,
) {
    Column {
        val currentPlayer = uiModel.players[page]
        PlayerGradeHeader(
            background = background,
            contentColor = contentColor,
            name = currentPlayer.name,
            details = currentPlayer.details.asString()
        )
        HeadshotAndStats(
            page = page,
            pagerState = pagerState,
            uiModel = uiModel,
            currentPlayer = currentPlayer,
            background = background,
            useDarkIcons = useDarkIcons,
            modifier = Modifier.weight(0.72f, fill = true)
        )
        Box(
            modifier = Modifier.weight(0.28f, fill = true),
            contentAlignment = Alignment.TopCenter
        ) {
            PlayerGrading(currentPlayer.grade)
        }
    }
}

@Composable
private fun HeadshotAndStats(
    page: Int,
    pagerState: PagerState,
    uiModel: PlayerGradesDetailUi,
    currentPlayer: PlayerGradesDetailUi.Player,
    background: Color,
    useDarkIcons: Boolean,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(value = false) }
    val statsExpandedPercentage = remember { Animatable(0f) }
    val headshotExpandedPercentage = remember { Animatable(1f) }
    LaunchedEffect(isExpanded) {
        // The 2 launches run the animations together
        launch {
            statsExpandedPercentage.animateTo(if (isExpanded) 1f else 0f)
        }
        launch {
            headshotExpandedPercentage.animateTo(if (isExpanded) 0f else 1f)
        }
    }
    // Reset expand state after page is navigated away from
    if (pagerState.currentPage != page) isExpanded = false

    Column(modifier = modifier) {
        Box(modifier = Modifier.weight(0.8f)) {
            PlayerHeadshot(
                headshots = currentPlayer.headshots,
                teamLogos = uiModel.teamLogos,
                background = background,
                useDarkIcons = useDarkIcons,
                isFirstPlayer = pagerState.currentPage == 0,
                isLastPlayer = pagerState.currentPage == uiModel.players.lastIndex,
                expandedPercent = headshotExpandedPercentage.value,
                pagerState = pagerState
            )
        }
        Column {
            GameStatus(
                showLiveGameDetails = uiModel.gameStatus.showLiveGameDetails,
                firstTeamLogo = uiModel.gameStatus.firstTeamLogos,
                firstTeamScore = uiModel.gameStatus.firstTeamScore,
                secondTeamLogo = uiModel.gameStatus.secondTeamLogos,
                secondTeamScore = uiModel.gameStatus.secondTeamScore,
                gameStatePrimary = uiModel.gameStatus.gameStatePrimary,
                gameStateSecondary = uiModel.gameStatus.gameStateSecondary,
                scheduledDate = uiModel.gameStatus.scheduledDate,
                showGameStatePrimary = uiModel.gameStatus.showGameStatePrimary,
                showGameStateSecondary = uiModel.gameStatus.showGameStateSecondary
            )
            if (statsExpandedPercentage.value > 0) {
                StatisticsFullSection(
                    statisticsList = currentPlayer.statisticsFullList,
                    expandedPercent = statsExpandedPercentage.value,
                    onCollapseStatsClick = { isExpanded = false }
                )
            }
            if (statsExpandedPercentage.value < 0.25f) {
                StatisticsSummarySection(
                    statisticsSummaryList = currentPlayer.statisticsSummaryList,
                    onExpandStatsClick = { isExpanded = true }
                )
            }
        }
    }
}

@Composable
private fun PlayerGradeHeader(
    background: Color,
    contentColor: Color,
    name: String,
    details: String,
) {
    val interactor = LocalPlayerGradesInteractor.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
    ) {
        IconButton(
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.TopStart)
                .padding(start = 4.dp),
            onClick = { interactor.send(PlayerGradesDetailUi.Interactor.OnCloseButtonClick) }
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(28.dp)
            )
        }
        Column(
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = name,
                color = contentColor,
                style = AthTextStyle.Slab.Bold.Medium,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = details,
                color = contentColor,
                style = AthTextStyle.Calibre.Utility.Regular.ExtraLarge,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .align(Alignment.CenterHorizontally)
                    .alpha(0.7F)
            )
        }
    }
}

@Suppress("LongMethod")
@Composable
private fun PlayerHeadshot(
    headshots: SizedImages,
    teamLogos: SizedImages,
    background: Color,
    useDarkIcons: Boolean,
    isFirstPlayer: Boolean,
    isLastPlayer: Boolean,
    expandedPercent: Float,
    pagerState: PagerState
) {
    val rememberScope = rememberCoroutineScope()

    val arrowVisibilityAlpha = if (pagerState.currentPageOffset == 0f) 1f else 0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(expandedPercent)
            .background(background)
    ) {
        PlayerGradeHeadshot(
            headshotsUrls = headshots,
            teamUrls = teamLogos,
            preferredSize = 240.dp
        )
        Column(
            modifier = Modifier
                .padding(12.dp)
                .alpha(arrowVisibilityAlpha)
                .align(Alignment.BottomStart)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .conditional(isFirstPlayer.not()) {
                        clickable {
                            navigationViaClick = true
                            rememberScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage.dec())
                            }
                        }
                    }
            ) {
                ResourceIcon(
                    resourceId = if (useDarkIcons) {
                        R.drawable.ic_scroll_left_circled_dark
                    } else {
                        R.drawable.ic_scroll_left_circled_light
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .alpha(if (isFirstPlayer) 0.3f else 1.0f)
                        .align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
        }
        Column(
            modifier = Modifier
                .padding(12.dp)
                .alpha(arrowVisibilityAlpha)
                .align(Alignment.BottomEnd)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .conditional(isLastPlayer.not()) {
                        clickable {
                            navigationViaClick = true
                            rememberScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage.inc())
                            }
                        }
                    }
            ) {
                ResourceIcon(
                    resourceId = if (useDarkIcons) {
                        R.drawable.ic_scroll_right_circled_dark
                    } else {
                        R.drawable.ic_scroll_right_circled_light
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .alpha(if (isLastPlayer) 0.3f else 1.0f)
                        .align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@SuppressWarnings("LongMethod")
@Composable
private fun GameStatus(
    showLiveGameDetails: Boolean,
    firstTeamLogo: SizedImages,
    firstTeamScore: Int,
    secondTeamLogo: SizedImages,
    secondTeamScore: Int,
    scheduledDate: String,
    gameStatePrimary: String?,
    gameStateSecondary: String?,
    showGameStatePrimary: Boolean,
    showGameStateSecondary: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamLogo(
                teamUrls = firstTeamLogo,
                preferredSize = 24.dp,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = firstTeamScore.toString(),
                style = AthTextStyle.Calibre.Headline.SemiBold.Large,
                fontSize = 32.sp,
                color = AthTheme.colors.dark800
            )
            Box(
                modifier = Modifier
                    .width(intrinsicSize = IntrinsicSize.Max)
                    .padding(horizontal = 16.dp)
            ) {
                if (showLiveGameDetails) {
                    InGameInformationHeader(
                        primaryTitle = gameStatePrimary.orEmpty(),
                        showPrimaryTitle = showGameStatePrimary,
                        secondaryTitle = gameStateSecondary,
                        showSecondaryTitle = showGameStateSecondary,
                        fillMaxWidth = true
                    )
                } else {
                    PostGameInformationHeader(
                        gamePeriod = ResourceString.StringWithParams(R.string.core_raw_parameterized_string, gameStatePrimary.orEmpty()),
                        scheduledDate = scheduledDate
                    )
                }
            }
            Text(
                text = secondTeamScore.toString(),
                style = AthTextStyle.Calibre.Headline.SemiBold.Large,
                fontSize = 32.sp,
                color = AthTheme.colors.dark800
            )
            Spacer(modifier = Modifier.width(8.dp))
            TeamLogo(
                teamUrls = secondTeamLogo,
                preferredSize = 24.dp,
                modifier = Modifier.size(24.dp)
            )
        }
        Divider(
            color = AthTheme.colors.dark300,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun StatisticsSummaryItem(
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = AthTextStyle.Calibre.Headline.SemiBold.Large,
            fontSize = 32.sp,
            color = AthTheme.colors.dark700
        )
        Text(
            text = label.replace(" ", "\n").uppercase(),
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark500,
            softWrap = true,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StatisticsSummarySection(
    statisticsSummaryList: List<PlayerGradesDetailUi.StatisticsSummary>,
    onExpandStatsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            statisticsSummaryList.take(4).forEach { statsSummary ->
                StatisticsSummaryItem(
                    label = statsSummary.label,
                    value = statsSummary.value
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Box(modifier = Modifier.clickable(onClick = onExpandStatsClick)) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.player_grades_show_more_stats),
                    style = AthTextStyle.Calibre.Utility.Regular.Large,
                    color = AthTheme.colors.dark500,
                    modifier = Modifier.padding(bottom = 2.dp, end = 4.dp)
                )
                Icon(
                    Icons.Default.ExpandLess,
                    contentDescription = null,
                    tint = AthTheme.colors.dark700,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Divider(
            color = AthTheme.colors.dark300,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun StatisticsFullItem(
    label: String,
    value: String
) {
    Box {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = AthTextStyle.Calibre.Utility.Regular.Large,
                color = AthTheme.colors.dark700,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value,
                style = AthTextStyle.Calibre.Utility.Regular.Large,
                color = AthTheme.colors.dark800,
                textAlign = TextAlign.Center,
                modifier = Modifier.defaultMinSize(80.dp)
            )
        }
        Divider(
            color = AthTheme.colors.dark300,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun StatisticsFullSection(
    statisticsList: List<PlayerGradesDetailUi.StatisticsSummary>,
    expandedPercent: Float,
    onCollapseStatsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(expandedPercent)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .alpha(expandedPercent)
                .clickable(onClick = onCollapseStatsClick)
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.player_grades_show_less_stats),
                    style = AthTextStyle.Calibre.Utility.Regular.Large,
                    color = AthTheme.colors.dark500,
                    modifier = Modifier.padding(bottom = 2.dp, end = 4.dp)
                )
                Icon(
                    Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = AthTheme.colors.dark700,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(state = rememberScrollState())
            ) {
                statisticsList.map {
                    StatisticsFullItem(it.label, it.value)
                }
            }
            ShadowedListBoundary(
                isAtTop = true,
                modifier = Modifier.align(Alignment.TopCenter)
            )
            ShadowedListBoundary(
                isAtTop = false,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ShadowedListBoundary(
    isAtTop: Boolean,
    modifier: Modifier = Modifier
) {
    val gradient = listOf(Color.Transparent, AthTheme.colors.dark200)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(16.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isAtTop) gradient.reversed() else gradient
                )
            )
            .then(modifier)
    )
}

@Composable
private fun PlayerGrading(playerGrade: PlayerGradesDetailUi.PlayerGrade) {
    when (playerGrade.state) {
        PlayerGradesDetailUi.GradingState.LOCKED_UNGRADED,
        PlayerGradesDetailUi.GradingState.LOCKED_GRADED -> PlayerGradingLocked(playerGrade)
        else -> PlayerGradingUnlocked(playerGrade)
    }
}

@Composable
private fun PlayerGradingUnlocked(playerGrade: PlayerGradesDetailUi.PlayerGrade) {
    val interactor = LocalPlayerGradesInteractor.current

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(18.dp))
        GradingStateDescription(playerGrade.state)
        Spacer(modifier = Modifier.height(12.dp))
        GradeBar(grading = playerGrade.grading)
        when (playerGrade.state) {
            PlayerGradesDetailUi.GradingState.GRADED -> {
                Spacer(modifier = Modifier.height(12.dp))
                AverageGrading(
                    averageGrade = playerGrade.averageGrade,
                    totalGradings = playerGrade.totalGradings,
                    hasGrades = playerGrade.totalGradings > 0
                )
            }

            PlayerGradesDetailUi.GradingState.UNGRADED -> {
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier.clickable(
                        onClick = {
                            interactor.send(PlayerGradesDetailUi.Interactor.OnShowAllPlayersClick)
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.player_grades_see_all_grades),
                            style = AthTextStyle.Calibre.Utility.Regular.Large,
                            color = AthTheme.colors.dark500,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = AthTheme.colors.dark700,
                            modifier = Modifier
                                .size(26.dp)
                                .padding(start = 6.dp)
                        )
                    }
                }
            }

            else -> Spacer(modifier = Modifier.height(24.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun PlayerGradingLocked(playerGrade: PlayerGradesDetailUi.PlayerGrade) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(28.dp))
        AverageGrading(
            averageGrade = playerGrade.averageGrade,
            totalGradings = playerGrade.totalGradings,
            isLocked = true,
            hasGrades = playerGrade.totalGradings > 0
        )
        if (playerGrade.state == PlayerGradesDetailUi.GradingState.LOCKED_GRADED) {
            Spacer(modifier = Modifier.height(12.dp))
            GradeBar(grading = playerGrade.grading, isLocked = true)
            Text(
                text = stringResource(R.string.player_grade_your_grades, playerGrade.grading),
                style = AthTextStyle.Calibre.Utility.Regular.Small,
                color = AthTheme.colors.dark500,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun GradingStateDescription(state: PlayerGradesDetailUi.GradingState) {
    when (state) {
        PlayerGradesDetailUi.GradingState.GRADED ->
            GradingStateDescriptionLabelAndIcon(
                labelResId = R.string.player_grades_grade_submitted,
                icon = Icons.Default.CheckCircle
            )

        else ->
            GradingStateDescriptionLabelAndIcon(
                labelResId = R.string.player_grades_grade_performance,
                icon = null
            )
    }
}

@Composable
private fun GradingStateDescriptionLabelAndIcon(
    @StringRes labelResId: Int,
    icon: ImageVector?
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(labelResId),
            style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall.copy(fontSize = 20.sp),
            color = AthTheme.colors.dark700,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        if (icon != null) {
            Icon(
                icon,
                contentDescription = null,
                tint = AthTheme.colors.dark700,
                modifier = Modifier
                    .size(24.dp)
                    .padding(start = 6.dp)
            )
        }
    }
}

@Composable
private fun GradeBar(
    grading: Int,
    isLocked: Boolean = false
) {
    val interactor = LocalPlayerGradesInteractor.current
    var interactionSource = remember { MutableInteractionSource() }

    val size = if (isLocked) 16.dp else 40.dp
    Row {
        repeat(grading) { index ->
            Icon(
                Icons.Default.Grade,
                contentDescription = null,
                tint = AthTheme.colors.dark700,
                modifier = Modifier
                    .size(size)
                    .conditional(isLocked.not()) {
                        clickable(
                            interactionSource.also { interactionSource = it },
                            indication = null
                        ) {
                            interactor.send(PlayerGradesDetailUi.Interactor.OnGradingPlayer(index.inc()))
                        }
                    }
            )
        }
        repeat(MAX_STARS - grading) { index ->
            Icon(
                Icons.Outlined.Grade,
                contentDescription = null,
                tint = AthTheme.colors.dark700,
                modifier = Modifier
                    .size(size)
                    .conditional(isLocked.not()) {
                        clickable(
                            interactionSource.also { interactionSource = it },
                            indication = null
                        ) {
                            interactor.send(PlayerGradesDetailUi.Interactor.OnGradingPlayer(grading + index.inc()))
                        }
                    }
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun AverageGrading(
    averageGrade: String,
    totalGradings: Int,
    isLocked: Boolean = false,
    hasGrades: Boolean
) {
    val iconSize = if (isLocked) 24.dp else 14.dp
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Grade,
                contentDescription = null,
                tint = AthTheme.colors.yellow,
                modifier = Modifier
                    .size(iconSize)
                    .padding(end = 2.dp)
            )
            val (averageDisplay, averageColor) = averageGrade.formatAverageGrade(hasGrades)
            Text(
                text = averageDisplay,
                style = if (isLocked) {
                    AthTextStyle.Calibre.Headline.Regular.ExtraLarge
                } else {
                    AthTextStyle.Calibre.Utility.Regular.ExtraSmall.copy(fontSize = 20.sp)
                },
                color = averageColor,
            )
        }
        Text(
            text = pluralStringResource(
                id = R.plurals.plural_average_grades,
                count = totalGradings,
                totalGradings
            ),
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark500,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun PlayerGradeHeadshot(
    headshotsUrls: SizedImages,
    teamUrls: SizedImages,
    preferredSize: Dp
) {
    var showTeamLogoOnError by remember { mutableStateOf(false) }
    var currentImageUrl by remember { mutableStateOf("") }

    val teamUrl = teamUrls.getPreferred(preferredSize)?.uri.orEmpty()
    val imageUrl = if (headshotsUrls.isNotEmpty()) {
        headshotsUrls.getPreferred(preferredSize)?.uri ?: ""
    } else {
        ""
    }

    if (imageUrl.isEmpty()) showTeamLogoOnError = true

    // Reset error state when a new headshot is to be shown
    if (currentImageUrl != imageUrl) {
        currentImageUrl = imageUrl
        showTeamLogoOnError = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (showTeamLogoOnError.not()) {
            RemoteImageAsync(
                url = currentImageUrl,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                onError = {
                    showTeamLogoOnError = true
                }
            )
        } else {
            val circleColor = AthTheme.colors.dark100
            Box(
                modifier = Modifier
                    .fillMaxSize(0.6f)
                    .align(Alignment.Center)
                    .drawBehind {
                        drawCircle(
                            color = circleColor,
                            alpha = 0.1f,
                        )
                    }
            ) {
                RemoteImageAsync(
                    url = teamUrl,
                    modifier = Modifier
                        .fillMaxSize(0.6f)
                        .align(Alignment.Center)
                )
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun PageIndicators(pagerState: PagerState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
    ) {
        ViewportPagerIndicator(
            pagerState = pagerState,
            indicatorCount = 5,
            indicatorSize = 10.dp,
            space = 6.dp,
            activeColor = AthTheme.colors.dark600,
            inactiveColor = AthTheme.colors.dark300,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Preview(name = "Player Grades Detail Screen - Dark Theme - Ungraded")
@Composable
private fun PlayerGradesDetailScreenPreview() {
    PlayerGradesDetailScreen(
        showSpinner = false,
        uiModel = PlayerGradesDetailPreviewData.getPlayerGrades(PlayerGradesDetailUi.GradingState.UNGRADED)
    )
}

@Preview(name = "Player Grades Detail Screen - Dark Theme - Graded")
@Composable
private fun PlayerGradesDetailScreenPreview_Graded() {
    PlayerGradesDetailScreen(
        showSpinner = false,
        uiModel = PlayerGradesDetailPreviewData.getPlayerGrades(PlayerGradesDetailUi.GradingState.GRADED)
    )
}

@Preview(name = "Player Grades Detail Screen - Dark Theme - Locked and Ungraded")
@Composable
private fun PlayerGradesDetailScreenPreview_LockedAndUngraded() {
    PlayerGradesDetailScreen(
        showSpinner = false,
        uiModel = PlayerGradesDetailPreviewData.getPlayerGrades(PlayerGradesDetailUi.GradingState.LOCKED_UNGRADED)
    )
}

@Preview(name = "Player Grades Detail Screen - Dark Theme - Locked and Graded")
@Composable
private fun PlayerGradesDetailScreenPreview_LockedAndGraded() {
    PlayerGradesDetailScreen(
        showSpinner = false,
        uiModel = PlayerGradesDetailPreviewData.getPlayerGrades(PlayerGradesDetailUi.GradingState.LOCKED_GRADED)
    )
}

@Preview(name = "Player Grades Detail Screen - Light Theme - Ungraded", device = Devices.NEXUS_5)
@Composable
private fun PlayerGradesDetailScreenPreview_Light() {
    AthleticTheme(lightMode = true) {
        PlayerGradesDetailScreen(
            showSpinner = false,
            uiModel = PlayerGradesDetailPreviewData.getPlayerGrades(PlayerGradesDetailUi.GradingState.UNGRADED)
        )
    }
}

@Preview(name = "Player Grades Detail Screen - Light Theme - Graded", device = Devices.NEXUS_5)
@Composable
private fun PlayerGradesDetailScreenPreview_LightGraded() {
    AthleticTheme(lightMode = true) {
        PlayerGradesDetailScreen(
            showSpinner = false,
            uiModel = PlayerGradesDetailPreviewData.getPlayerGrades(PlayerGradesDetailUi.GradingState.GRADED)
        )
    }
}

@Preview(name = "Player Grades Detail Screen - Light Theme - Locked and Graded", device = Devices.NEXUS_5)
@Composable
private fun PlayerGradesDetailScreenPreview_LightDeleted() {
    AthleticTheme(lightMode = true) {
        PlayerGradesDetailScreen(
            showSpinner = false,
            uiModel = PlayerGradesDetailPreviewData.getPlayerGrades(PlayerGradesDetailUi.GradingState.LOCKED_GRADED)
        )
    }
}

@Preview(name = "Player Grades Detail Screen - Dark Theme - Loading")
@Composable
private fun PlayerGradesDetailScreenPreview_Loading() {
    PlayerGradesDetailScreen(
        showSpinner = true,
        uiModel = null
    )
}