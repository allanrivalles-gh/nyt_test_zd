package com.theathletic.boxscore.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentManager
import com.theathletic.boxscore.ui.GameDetailPreviewData.soccerRecentForm
import com.theathletic.boxscore.ui.playbyplay.BaseballOccupiedBases
import com.theathletic.components.AnimatedFadingText
import com.theathletic.data.SizedImages
import com.theathletic.scores.GameDetailTab
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWrapper
import com.theathletic.ui.asResourceString
import com.theathletic.ui.asString
import com.theathletic.ui.utility.conditional
import com.theathletic.ui.widgets.ResourceIcon
import com.theathletic.ui.widgets.SimpleShrinkToFitText
import com.theathletic.ui.widgets.TeamLogo

class GameDetailUi {

    data class TeamSummary(
        val teamId: String,
        val legacyId: Long,
        val isFollowable: Boolean,
        val name: ResourceString,
        val logoUrls: SizedImages,
        val score: Int?,
        val isWinner: Boolean,
        val currentRecord: String?,
        val currentRanking: String? = null,
        val showCurrentRanking: Boolean = false,
        val showCollegeCurrentRanking: Boolean = false
    )

    sealed class GameStatus {
        data class PregameStatus(
            val scheduledDate: String,
            val scheduledTime: ResourceString,
        ) : GameStatus()

        data class InGameStatus(
            val isGameDelayed: Boolean,
            val gameStatePrimary: String?,
            val gameStateSecondary: String?,
        ) : GameStatus()

        data class PostGameStatus(
            val gamePeriod: ResourceString,
            val scheduledDate: String
        ) : GameStatus()

        data class BaseballInGameStatus(
            val inningHalf: ResourceString,
            val occupiedBases: List<Int>,
            val status: ResourceString,
            val isGameDelayed: Boolean
        ) : GameStatus()

        data class SoccerPostGameStatus(
            val gamePeriod: ResourceString,
            val scheduledDate: String,
            val aggregate: ResourceString,
            val showAggregate: Boolean,
        ) : GameStatus()

        data class SoccerInGameStatus(
            val aggregate: ResourceString,
            val showAggregate: Boolean,
            val gameStatePrimary: String?,
            val isGameDelayed: Boolean,
        ) : GameStatus()
    }

    sealed class GameInfo {
        object Empty : GameInfo()

        data class RecentForm(
            val firstTeamRecentForm: List<SoccerRecentFormHeaderModel.SoccerRecentFormIcons> = emptyList(),
            val secondTeamRecentForm: List<SoccerRecentFormHeaderModel.SoccerRecentFormIcons> = emptyList(),
            val expectedGoals: SoccerRecentFormHeaderModel.ExpectedGoals = SoccerRecentFormHeaderModel.ExpectedGoals(),
            val isReverse: Boolean = false,
            val showRecentForm: Boolean = false
        ) : GameInfo()

        data class PostGameWinnerTitle(
            val title: ResourceString
        ) : GameInfo()
    }

    sealed class TeamStatus {
        data class HockeyPowerPlay(
            val inPowerPlay: Boolean,
        ) : TeamStatus()

        data class Timeouts(
            val remainingTimeouts: Int,
            val usedTimeouts: Int
        ) : TeamStatus()

        object Possession : TeamStatus()
    }

    data class Tab(
        val type: GameDetailTab,
        val label: ResourceString,
        val showIndicator: Boolean
    )

    interface Interactor {
        fun onBackButtonClicked()
        fun onTabClicked(tab: GameDetailTab)
        fun onTeamClicked(teamId: String, legacyId: Long, teamName: String)
        fun onShareClick(shareLink: String)
    }
}

@Composable
fun GameDetailScreen(
    title: ResourceString,
    gameTitle: ResourceString?,
    firstTeam: GameDetailUi.TeamSummary,
    secondTeam: GameDetailUi.TeamSummary,
    firstTeamStatus: List<GameDetailUi.TeamStatus>,
    secondTeamStatus: List<GameDetailUi.TeamStatus>,
    gameStatus: GameDetailUi.GameStatus,
    gameInfo: GameDetailUi.GameInfo?,
    shareLink: String,
    showShareLink: Boolean,
    tabs: List<GameDetailUi.Tab>,
    tabModules: List<TabModule>,
    fragmentManager: () -> FragmentManager,
    selectedTab: GameDetailTab = GameDetailTab.GAME,
    interactor: GameDetailUi.Interactor,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AthTheme.colors.dark100)
    ) {
        Toolbar(
            title = title.asString(),
            shareLink = shareLink,
            showShareLink = showShareLink,
            onBackClicked = {
                interactor.onBackButtonClicked()
            },
            onShareClicked = {
                interactor.onShareClick(it)
            }
        )
        ScoreHeader(
            firstTeam = firstTeam,
            secondTeam = secondTeam,
            firstTeamStatus = firstTeamStatus,
            secondTeamStatus = secondTeamStatus,
            gameStatus = gameStatus,
            gameTitle = gameTitle,
            onTeamClicked = { teamId, legacyId, teamName ->
                interactor.onTeamClicked(teamId, legacyId, teamName)
            },
        )

        GameInfo(gameInfo)

        GameSummaryTabLayout(
            tabs = tabs,
            tabModules = tabModules,
            fragmentManager = fragmentManager,
            selectedTab = selectedTab,
            onTabSelected = interactor::onTabClicked,
        )
    }
}

@Composable
private fun GameInfo(gameInfo: GameDetailUi.GameInfo?) {
    when (gameInfo) {
        is GameDetailUi.GameInfo.RecentForm -> {
            SoccerRecentFormHeader(
                expectedGoals = gameInfo.expectedGoals,
                firstTeamRecentForms = gameInfo.firstTeamRecentForm,
                secondTeamRecentForms = gameInfo.secondTeamRecentForm,
                isReverse = gameInfo.isReverse,
                showRecentForm = gameInfo.showRecentForm
            )
        }
        is GameDetailUi.GameInfo.PostGameWinnerTitle -> {
            FinalGameStatus(gameInfo.title)
        }
        else -> {
            /* Do nothing */
        }
    }
}

@Composable
private fun Toolbar(
    title: String,
    shareLink: String,
    showShareLink: Boolean,
    onBackClicked: () -> Unit,
    onShareClicked: (shareLink: String) -> Unit,
) {
    Box(
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
    ) {
        IconButton(
            onClick = onBackClicked,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = null,
                tint = AthTheme.colors.dark800
            )
        }
        Text(
            text = title,
            style = AthTextStyle.Slab.Bold.Medium,
            fontSize = 24.sp,
            maxLines = 1,
            color = AthTheme.colors.dark800,
            modifier = Modifier.align(Alignment.Center)
        )
        if (showShareLink) {
            IconButton(
                onClick = { onShareClicked(shareLink) },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = null,
                    tint = AthTheme.colors.dark800
                )
            }
        }
    }
}

@Composable
private fun ScoreHeader(
    firstTeam: GameDetailUi.TeamSummary,
    secondTeam: GameDetailUi.TeamSummary,
    firstTeamStatus: List<GameDetailUi.TeamStatus>,
    secondTeamStatus: List<GameDetailUi.TeamStatus>,
    gameStatus: GameDetailUi.GameStatus,
    gameTitle: ResourceString?,
    onTeamClicked: (teamId: String, legacyId: Long, teamName: String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
            .padding(
                top = 10.dp,
                bottom = 16.dp,
                start = 10.dp,
                end = 10.dp
            )
    ) {
        Column {
            if (gameTitle != null) {
                Text(
                    text = gameTitle.asString(),
                    textAlign = TextAlign.Center,
                    style = AthTextStyle.Calibre.Utility.Regular.Small,
                    color = AthTheme.colors.dark500,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                TeamDetails(
                    team = firstTeam,
                    isFirstTeam = true,
                    teamNameContent = { firstTeamStatus.toTeamName() },
                    teamStatusContent = { firstTeamStatus.toTeamDetail() },
                    onTeamClicked = onTeamClicked,
                )

                ScoresAndGameStatus(
                    firstTeam = firstTeam,
                    secondTeam = secondTeam,
                    firstTeamStatus = firstTeamStatus,
                    secondTeamStatus = secondTeamStatus,
                    gameStatus = gameStatus,
                    modifier = Modifier.weight(0.6f)
                )

                TeamDetails(
                    team = secondTeam,
                    isFirstTeam = false,
                    teamNameContent = { secondTeamStatus.toTeamName() },
                    teamStatusContent = { secondTeamStatus.toTeamDetail() },
                    onTeamClicked = onTeamClicked,
                )
            }
        }
    }
}

@Composable
private fun ScoresAndGameStatus(
    firstTeam: GameDetailUi.TeamSummary,
    secondTeam: GameDetailUi.TeamSummary,
    firstTeamStatus: List<GameDetailUi.TeamStatus>,
    secondTeamStatus: List<GameDetailUi.TeamStatus>,
    gameStatus: GameDetailUi.GameStatus,
    modifier: Modifier
) {
    var scoreFontSize by remember { mutableStateOf(AthTextStyle.Calibre.Headline.Regular.ExtraLarge.fontSize) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        TeamScore(
            team = firstTeam,
            fontSize = scoreFontSize,
            onFontSizeChanged = { newFontSize -> scoreFontSize = newFontSize },
            modifier = Modifier.weight(0.3f)
        ) {
            firstTeamStatus.toScoreSection()
        }
        GameStatus(modifier = Modifier.weight(0.3f)) {
            gameStatus.ToGameStatusHeader()
        }
        TeamScore(
            team = secondTeam,
            fontSize = scoreFontSize,
            onFontSizeChanged = { newFontSize -> scoreFontSize = newFontSize },
            modifier = Modifier.weight(0.3f)
        ) {
            secondTeamStatus.toScoreSection()
        }
    }
}

@Composable
private fun List<GameDetailUi.TeamStatus>.toTeamName(): List<Unit> {
    return mapNotNull { teamStatus ->
        when (teamStatus) {
            is GameDetailUi.TeamStatus.Possession -> {
                PossessionIndicator()
            }
            else -> null
        }
    }
}

@Composable
private fun List<GameDetailUi.TeamStatus>.toTeamDetail(): List<Unit> {
    return mapNotNull { teamStatus ->
        when (teamStatus) {
            is GameDetailUi.TeamStatus.HockeyPowerPlay -> PowerPlayIndicator(
                inPowerPlay = teamStatus.inPowerPlay
            )
            else -> null
        }
    }
}

@Composable
private fun List<GameDetailUi.TeamStatus>.toScoreSection(): List<Unit> {
    return mapNotNull { teamStatus ->
        when (teamStatus) {
            is GameDetailUi.TeamStatus.Timeouts -> TimeoutsIndicator(
                remainingTimeouts = teamStatus.remainingTimeouts,
                usedTimeouts = teamStatus.usedTimeouts
            )
            else -> null
        }
    }
}

@Composable
private fun GameDetailUi.GameStatus.ToGameStatusHeader() {
    when (this) {
        is GameDetailUi.GameStatus.PregameStatus -> PregameInformationHeader(
            gameInfo = this
        )
        is GameDetailUi.GameStatus.InGameStatus -> InGameInformationHeader(
            primaryTitle = gameStatePrimary.orEmpty(),
            showPrimaryTitle = gameStatePrimary != null,
            secondaryTitle = gameStateSecondary.orEmpty(),
            showSecondaryTitle = gameStateSecondary != null,
            isGameDelayed = isGameDelayed,
            fillMaxWidth = false
        )
        is GameDetailUi.GameStatus.PostGameStatus -> PostGameInformationHeader(
            gamePeriod = gamePeriod,
            scheduledDate = scheduledDate,
        )
        is GameDetailUi.GameStatus.BaseballInGameStatus -> BaseballInGameInformationHeader(
            gameInfo = this
        )
        is GameDetailUi.GameStatus.SoccerInGameStatus -> SoccerInGameInformationHeader(
            gameInfo = this
        )
        is GameDetailUi.GameStatus.SoccerPostGameStatus -> SoccerPostGameInformationHeader(
            gameInfo = this
        )
    }
}

@Composable
private fun PregameInformationHeader(
    gameInfo: GameDetailUi.GameStatus.PregameStatus
) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically)
    ) {
        SimpleShrinkToFitText(
            text = gameInfo.scheduledDate,
            style = AthTextStyle.Calibre.Utility.Regular.Small.copy(color = AthTheme.colors.dark500),
            maxLines = 1
        )

        Text(
            text = gameInfo.scheduledTime.asString(),
            style = AthTextStyle.Calibre.Headline.Medium.Small,
            color = AthTheme.colors.dark700
        )
    }
}

@Composable
fun InGameInformationHeader(
    primaryTitle: String,
    showPrimaryTitle: Boolean,
    fillMaxWidth: Boolean = false,
    secondaryTitle: String?,
    showSecondaryTitle: Boolean = true,
    isGameDelayed: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .conditional(fillMaxWidth) {
                this.fillMaxWidth()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DelayedGameLabel(isGameDelayed)
        if (showPrimaryTitle) {
            Text(
                text = primaryTitle.uppercase(),
                style = AthTextStyle.Calibre.Utility.Medium.Large,
                color = AthTheme.colors.dark700
            )
        }

        if (showSecondaryTitle) AnimatedMatchTime(secondaryTitle.orEmpty())
    }
}

@Composable
fun PostGameInformationHeader(
    gamePeriod: ResourceString,
    scheduledDate: String
) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 2.dp,
            alignment = Alignment.CenterVertically
        )
    ) {
        SimpleShrinkToFitText(
            text = scheduledDate,
            style = AthTextStyle.Calibre.Utility.Regular.Small.copy(color = AthTheme.colors.dark500),
            maxLines = 1
        )

        Text(
            text = gamePeriod.asString().uppercase(),
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            color = AthTheme.colors.dark700
        )
    }
}

@Composable
fun SoccerPostGameInformationHeader(
    gameInfo: GameDetailUi.GameStatus.SoccerPostGameStatus
) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = gameInfo.scheduledDate,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark500
        )
        Text(
            text = gameInfo.gamePeriod.asString().uppercase(),
            style = AthTextStyle.Calibre.Headline.Medium.Small,
            color = AthTheme.colors.dark800
        )
        if (gameInfo.showAggregate) {
            Text(
                text = gameInfo.aggregate.asString(),
                style = AthTextStyle.Calibre.Utility.Regular.Small,
                color = AthTheme.colors.dark500
            )
        }
    }
}

@Composable
fun BaseballInGameInformationHeader(
    gameInfo: GameDetailUi.GameStatus.BaseballInGameStatus
) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DelayedGameLabel(gameInfo.isGameDelayed)
        AnimatedBaseballInnings(gameInfo.inningHalf.asString().uppercase())
        BaseballOccupiedBases(
            occupiedBases = gameInfo.occupiedBases,
            modifier = Modifier.padding(vertical = 2.dp)
        )
        AnimatedBaseballBallsStrikesAndOuts(gameInfo.status.asString().uppercase())
    }
}

@Composable
fun SoccerInGameInformationHeader(
    gameInfo: GameDetailUi.GameStatus.SoccerInGameStatus
) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DelayedGameLabel(gameInfo.isGameDelayed)
        AnimatedSoccerMatchTime(gameInfo.gameStatePrimary.orEmpty())
        if (gameInfo.showAggregate) {
            Text(
                text = gameInfo.aggregate.asString(),
                style = AthTextStyle.Calibre.Utility.Regular.Small,
                color = AthTheme.colors.dark500
            )
        }
    }
}

@Composable
private fun DelayedGameLabel(isGameDelayed: Boolean) {
    if (isGameDelayed.not()) return
    Text(
        text = stringResource(id = R.string.game_detail_delay_label).uppercase(),
        style = AthTextStyle.Calibre.Utility.Medium.Large,
        color = AthTheme.colors.red
    )
}

@Composable
private fun TeamDetails(
    team: GameDetailUi.TeamSummary,
    isFirstTeam: Boolean,
    modifier: Modifier = Modifier,
    teamNameContent: @Composable BoxScope.() -> Unit,
    teamStatusContent: @Composable ColumnScope.() -> Unit,
    onTeamClicked: (teamId: String, legacyId: Long, teamName: String) -> Unit,
) {
    val teamName = team.name.asString()
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TeamLogo(
            teamUrls = team.logoUrls,
            preferredSize = 32.dp,
            modifier = Modifier
                .size(32.dp)
                .conditional(team.isFollowable) {
                    clickable {
                        onTeamClicked(
                            team.teamId,
                            team.legacyId,
                            teamName,
                        )
                    }
                }
        )
        Row(
            modifier = Modifier.padding(top = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            TeamDetailsNameContent(
                showContent = isFirstTeam,
                content = teamNameContent
            )
            TeamName(
                name = teamName,
                ranking = team.currentRanking.orEmpty(),
                showRanking = team.showCollegeCurrentRanking
            )
            TeamDetailsNameContent(
                showContent = !isFirstTeam,
                content = teamNameContent
            )
        }
        CurrentRankOrRecord(team)
        Column(
            content = teamStatusContent,
            horizontalAlignment = Alignment.CenterHorizontally
        )
    }
}

@Composable
private fun CurrentRankOrRecord(team: GameDetailUi.TeamSummary) {
    val value = when {
        team.showCurrentRanking -> team.currentRanking
        team.currentRecord != "(0-0-0)" -> team.currentRecord
        else -> null
    }

    if (value != null) {
        Text(
            text = value,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark500,
            modifier = Modifier
                .padding(top = 2.dp)
        )
    }
}

@Composable
private fun TeamDetailsNameContent(
    showContent: Boolean,
    content: @Composable BoxScope.() -> Unit,
) {
    if (showContent) {
        Box(
            modifier = Modifier.width(12.dp),
            content = content
        )
    } else {
        Spacer(modifier = Modifier.width(12.dp))
    }
}

@Composable
private fun TeamScore(
    team: GameDetailUi.TeamSummary,
    modifier: Modifier = Modifier,
    onFontSizeChanged: (newFontSize: TextUnit) -> Unit,
    fontSize: TextUnit,
    teamStatusContent: @Composable() (ColumnScope.() -> Unit)
) {
    team.score?.let { score ->
        Column(
            modifier = modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedScoreText(
                score.toString(),
                team.isWinner,
                onFontSizeChanged,
                fontSize
            )
            Column(
                content = teamStatusContent,
                horizontalAlignment = Alignment.CenterHorizontally
            )
        }
    }
}

@Composable
private fun GameStatus(
    modifier: Modifier = Modifier,
    gameStatusContent: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier,
        content = gameStatusContent,
        contentAlignment = Alignment.Center
    )
}

@Composable
private fun TeamName(name: String, ranking: String, showRanking: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (showRanking) {
            Text(
                text = ranking,
                style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                color = AthTheme.colors.dark500,
                modifier = Modifier.padding(end = 4.dp)
            )
        }

        Text(
            text = name,
            style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge,
            color = AthTheme.colors.dark700
        )
    }
}

@Composable
private fun FinalGameStatus(title: ResourceString) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
    ) {
        Divider(color = AthTheme.colors.dark300, thickness = 1.dp)
        Text(
            text = title.asString(),
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark500,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
        Divider(color = AthTheme.colors.dark300, thickness = 1.dp)
    }
}

@Composable
private fun PowerPlayIndicator(inPowerPlay: Boolean) {
    Box(modifier = Modifier.heightIn(min = 16.dp)) {
        if (inPowerPlay) {
            ResourceIcon(
                resourceId = R.drawable.ic_power_play,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun TimeoutsIndicator(
    remainingTimeouts: Int,
    usedTimeouts: Int
) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        for (i in 1..remainingTimeouts) TimeoutIndicator(isUsed = false)
        for (i in 1..usedTimeouts) TimeoutIndicator(isUsed = true)
    }
}

@Composable
private fun TimeoutIndicator(isUsed: Boolean) {
    val background = if (isUsed) AthTheme.colors.dark500 else AthTheme.colors.yellow
    Canvas(modifier = Modifier.size(4.dp)) {
        drawCircle(
            color = background,
            radius = 2.dp.toPx()
        )
    }
}

@Composable
private fun PossessionIndicator() {
    val circleColor = AthTheme.colors.green
    Box(
        modifier = Modifier
            .size(12.dp)
            .drawBehind {
                drawCircle(
                    color = circleColor,
                    radius = 3.dp.toPx()
                )
            }
    )
}

@Composable
private fun GameSummaryTabLayout(
    tabs: List<GameDetailUi.Tab>,
    tabModules: List<TabModule>,
    selectedTab: GameDetailTab,
    onTabSelected: (GameDetailTab) -> Unit,
    fragmentManager: () -> FragmentManager,
) {
    val currentTabIndex = tabs.indexOfFirst { it.type == selectedTab }
    if (currentTabIndex >= 0) {
        when {
            tabs.size > 4 -> // Have a scrolling tab layout when more than 4 tabs
                ScrollableTabRow(
                    selectedTabIndex = currentTabIndex,
                    backgroundColor = AthTheme.colors.dark200,
                    edgePadding = 0.dp,
                    indicator = { tabPositions ->
                        if (currentTabIndex in tabPositions.indices) {
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[currentTabIndex]),
                                color = AthTheme.colors.dark800
                            )
                        }
                    }
                ) {
                    GameSummaryTabs(
                        tabs = tabs,
                        currentTabIndex = currentTabIndex,
                        onTabSelected = onTabSelected
                    )
                }

            tabs.size > 1 -> // Have a fixed tab layout when there are 2 to 4 tabs
                TabRow(
                    selectedTabIndex = currentTabIndex,
                    backgroundColor = AthTheme.colors.dark200,
                    indicator = { tabPositions ->
                        if (currentTabIndex in tabPositions.indices) {
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[currentTabIndex]),
                                color = AthTheme.colors.dark800
                            )
                        }
                    }
                ) {
                    GameSummaryTabs(
                        tabs = tabs,
                        currentTabIndex = currentTabIndex,
                        onTabSelected = onTabSelected
                    )
                }

            else -> { /* Show no tabs when only one tab*/ }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            tabModules[currentTabIndex].Render(
                isActive = true,
                fragmentManager = fragmentManager,
            )
        }
    }
}

@Composable
private fun GameSummaryTabs(
    tabs: List<GameDetailUi.Tab>,
    currentTabIndex: Int,
    onTabSelected: (GameDetailTab) -> Unit
) {
    tabs.forEachIndexed { index, tab ->
        GameSummaryTab(
            index = index,
            label = tab.label.asString(),
            showIndicator = tab.showIndicator,
            onTabSelected = onTabSelected,
            type = tab.type,
            currentTabIndex = currentTabIndex
        )
    }
}

@Composable
private fun GameSummaryTab(
    index: Int,
    label: String,
    type: GameDetailTab,
    showIndicator: Boolean,
    currentTabIndex: Int,
    onTabSelected: (GameDetailTab) -> Unit
) {
    Tab(
        selected = index == currentTabIndex,
        onClick = {
            onTabSelected(type)
        }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showIndicator) {
                val circleColor = AthTheme.colors.red
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .padding(end = 6.dp)
                        .drawBehind {
                            drawCircle(
                                color = circleColor,
                                radius = 3.dp.toPx()
                            )
                        }
                )
            }
            Text(
                text = label,
                style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge,
                color = if (index == currentTabIndex) {
                    AthTheme.colors.dark700
                } else {
                    AthTheme.colors.dark400
                }
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AnimatedScoreText(
    score: String,
    isWinner: Boolean,
    onFontSizeChanged: (newFontSize: TextUnit) -> Unit,
    fontSize: TextUnit
) {
    AnimatedContent(
        targetState = score,
        transitionSpec = { scoreEntryExitTransition() }
    ) { value ->
        key(fontSize) {
            val color = if (isWinner) AthTheme.colors.dark700 else AthTheme.colors.dark500
            SimpleShrinkToFitText(
                text = value,
                style = AthTextStyle.Calibre.Headline.Regular.ExtraLarge.copy(
                    color = color,
                    fontSize = fontSize
                ),
                maxLines = 1,
                textAlign = TextAlign.Center,
                onFontSizeChanged = onFontSizeChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun AnimatedMatchTime(text: String) {
    AnimatedFadingText(text) { value ->
        Box(modifier = Modifier.defaultMinSize(minWidth = 48.dp)) {
            Text(
                text = value,
                style = AthTextStyle.Calibre.Utility.Medium.Large,
                textAlign = TextAlign.Center,
                color = AthTheme.colors.red,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun AnimatedSoccerMatchTime(text: String) {
    AnimatedFadingText(text) { value ->
        Box(modifier = Modifier.defaultMinSize(minWidth = 40.dp)) {
            Text(
                text = value,
                style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall.copy(fontSize = 20.sp),
                color = AthTheme.colors.red,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun AnimatedBaseballInnings(text: String) {
    AnimatedFadingText(text) { value ->
        Box(modifier = Modifier.defaultMinSize(minWidth = 50.dp)) {
            Text(
                text = value,
                style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall,
                color = AthTheme.colors.red,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun AnimatedBaseballBallsStrikesAndOuts(text: String) {
    AnimatedFadingText(text) { value ->
        Box(modifier = Modifier.defaultMinSize(minWidth = 60.dp)) {
            Text(
                text = value,
                style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall,
                color = AthTheme.colors.dark800,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun scoreEntryExitTransition(): ContentTransform =
    // Incoming/Entry score text
    slideInVertically(animationSpec = tween(durationMillis = 800)) { -it } +
        fadeIn(animationSpec = tween(durationMillis = 1800)) with
        // Outgoing/Exit score text
        fadeOut(animationSpec = tween(durationMillis = 250)) +
            slideOutVertically(animationSpec = tween(durationMillis = 800)) { it }

@Preview
@Composable
private fun GameDetailScreenPreview_PreGame() {
    GameDetailScreen(
        title = "SJ @ NSH".asResourceString(),
        firstTeam = GameDetailPreviewData.firstTeamPreGame,
        secondTeam = GameDetailPreviewData.secondTeamPreGame,
        gameStatus = GameDetailPreviewData.pregameStatusWithTVNetwork,
        firstTeamStatus = GameDetailPreviewData.emptyTeamStatus,
        secondTeamStatus = GameDetailPreviewData.emptyTeamStatus,
        shareLink = "rwrtwywuj",
        showShareLink = true,
        tabs = GameDetailPreviewData.tabs,
        tabModules = GameDetailPreviewData.tabModules,
        fragmentManager = { PreviewDataGameDetailFragManager },
        interactor = GameDetailPreviewData.interactor,
        gameTitle = StringWrapper(""),
        gameInfo = GameDetailUi.GameInfo.Empty
    )
}

@Preview
@Composable
private fun GameDetailScreenPreview_PreGameLight() {
    AthleticTheme(lightMode = true) {
        GameDetailScreen(
            title = "SJ @ NSH".asResourceString(),
            firstTeam = GameDetailPreviewData.firstTeamPreGame,
            secondTeam = GameDetailPreviewData.secondTeamPreGame,
            gameStatus = GameDetailPreviewData.pregameStatusWithoutTVNetwork,
            firstTeamStatus = GameDetailPreviewData.emptyTeamStatus,
            secondTeamStatus = GameDetailPreviewData.emptyTeamStatus,
            shareLink = "rwrtwywuj",
            showShareLink = true,
            tabs = GameDetailPreviewData.tabs,
            tabModules = GameDetailPreviewData.tabModules,
            fragmentManager = { PreviewDataGameDetailFragManager },
            interactor = GameDetailPreviewData.interactor,
            gameTitle = StringWrapper(""),
            gameInfo = GameDetailUi.GameInfo.Empty
        )
    }
}

@Preview
@Composable
private fun GameDetailScreenPreview_InGame() {
    GameDetailScreen(
        title = "SJ @ NSH".asResourceString(),
        firstTeam = GameDetailPreviewData.firstTeamInGame,
        secondTeam = GameDetailPreviewData.secondTeamInGame,
        gameStatus = GameDetailPreviewData.inGameInformation,
        firstTeamStatus = GameDetailPreviewData.emptyTeamStatus,
        secondTeamStatus = GameDetailPreviewData.emptyTeamStatus,
        shareLink = "rwrtwywuj",
        showShareLink = true,
        tabs = GameDetailPreviewData.tabs,
        tabModules = GameDetailPreviewData.tabModules,
        fragmentManager = { PreviewDataGameDetailFragManager },
        interactor = GameDetailPreviewData.interactor,
        gameTitle = StringWrapper(""),
        gameInfo = GameDetailUi.GameInfo.Empty
    )
}

@Preview(device = Devices.PIXEL)
@Composable
private fun GameDetailScreenPreview_InGameLight() {
    AthleticTheme(lightMode = true) {
        GameDetailScreen(
            title = "SJ @ NSH".asResourceString(),
            firstTeam = GameDetailPreviewData.firstTeamInGame,
            secondTeam = GameDetailPreviewData.secondTeamInGame,
            gameStatus = GameDetailPreviewData.inGameInformation.copy(isGameDelayed = false),
            firstTeamStatus = GameDetailPreviewData.emptyTeamStatus,
            secondTeamStatus = GameDetailPreviewData.emptyTeamStatus,
            shareLink = "rwrtwywuj",
            showShareLink = true,
            tabs = GameDetailPreviewData.tabs,
            tabModules = GameDetailPreviewData.tabModules,
            fragmentManager = { PreviewDataGameDetailFragManager },
            interactor = GameDetailPreviewData.interactor,
            gameTitle = StringWrapper(""),
            gameInfo = GameDetailUi.GameInfo.Empty
        )
    }
}

@Preview(device = Devices.PIXEL)
@Composable
private fun GameDetailScreenPreview_PostGameBasketball_Pixel() {
    GameDetailScreen(
        title = "BOS @ GSW".asResourceString(),
        firstTeam = GameDetailPreviewData.firstTeamBasketball,
        secondTeam = GameDetailPreviewData.secondTeamBasketball,
        gameStatus = GameDetailPreviewData.postGameInformation,
        firstTeamStatus = GameDetailPreviewData.basketballTeamStatus1UsedTimeout,
        secondTeamStatus = GameDetailPreviewData.basketballTeamStatus4UsedTimeouts,
        shareLink = "rwrtwywuj",
        showShareLink = false,
        tabs = GameDetailPreviewData.tabs,
        tabModules = GameDetailPreviewData.tabModules,
        fragmentManager = { PreviewDataGameDetailFragManager },
        interactor = GameDetailPreviewData.interactor,
        gameTitle = StringWrapper(""),
        gameInfo = GameDetailUi.GameInfo.Empty
    )
}

@Preview
@Composable
private fun GameDetailScreenPreview_PostGameBasketball() {
    GameDetailScreen(
        title = "BOS @ GSW".asResourceString(),
        firstTeam = GameDetailPreviewData.firstTeamBasketball,
        secondTeam = GameDetailPreviewData.secondTeamBasketball,
        gameStatus = GameDetailPreviewData.postGameInformation,
        firstTeamStatus = GameDetailPreviewData.basketballTeamStatus1UsedTimeout,
        secondTeamStatus = GameDetailPreviewData.basketballTeamStatus4UsedTimeouts,
        shareLink = "rwrtwywuj",
        showShareLink = false,
        tabs = GameDetailPreviewData.tabs,
        tabModules = GameDetailPreviewData.tabModules,
        fragmentManager = { PreviewDataGameDetailFragManager },
        interactor = GameDetailPreviewData.interactor,
        gameTitle = StringWrapper(""),
        gameInfo = GameDetailUi.GameInfo.Empty
    )
}

@Preview
@Composable
private fun GameDetailScreenPreview_InGameHockey() {
    GameDetailScreen(
        title = "OTT @ TOR".asResourceString(),
        firstTeam = GameDetailPreviewData.firstTeamHockey,
        secondTeam = GameDetailPreviewData.secondTeamHockey,
        gameStatus = GameDetailPreviewData.inGameInformation,
        firstTeamStatus = GameDetailPreviewData.hockeyTeamStatusNotInPowerPlay,
        secondTeamStatus = GameDetailPreviewData.hockeyTeamStatusInPowerPlay,
        shareLink = "rwrtwywuj",
        showShareLink = false,
        tabs = GameDetailPreviewData.tabs,
        tabModules = GameDetailPreviewData.tabModules,
        fragmentManager = { PreviewDataGameDetailFragManager },
        interactor = GameDetailPreviewData.interactor,
        gameTitle = StringWrapper(""),
        gameInfo = GameDetailUi.GameInfo.Empty
    )
}

@Preview
@Composable
private fun GameDetailScreenPreview_InGameNFL() {
    GameDetailScreen(
        title = "LAR @ CIN".asResourceString(),
        firstTeam = GameDetailPreviewData.firstTeamNFL,
        secondTeam = GameDetailPreviewData.secondTeamNFL,
        gameStatus = GameDetailPreviewData.inGameInformation,
        firstTeamStatus = GameDetailPreviewData.nflTeamStatusWithPossession,
        secondTeamStatus = GameDetailPreviewData.nlfTeamStatusWithoutPossession,
        shareLink = "rwrtwywuj",
        showShareLink = false,
        tabs = GameDetailPreviewData.tabs,
        tabModules = GameDetailPreviewData.tabModules,
        fragmentManager = { PreviewDataGameDetailFragManager },
        interactor = GameDetailPreviewData.interactor,
        gameTitle = StringWrapper(""),
        gameInfo = GameDetailUi.GameInfo.Empty
    )
}

@Preview(device = Devices.PIXEL)
@Composable
private fun GameDetailScreenPreview_InGameBaseball_Pixel() {
    GameDetailScreen(
        title = "LAR @ CIN".asResourceString(),
        firstTeam = GameDetailPreviewData.firstTeamBaseball,
        secondTeam = GameDetailPreviewData.secondTeamBaseball,
        gameStatus = GameDetailPreviewData.baseballInGameInformation,
        firstTeamStatus = GameDetailPreviewData.emptyTeamStatus,
        secondTeamStatus = GameDetailPreviewData.emptyTeamStatus,
        shareLink = "rwrtwywuj",
        showShareLink = true,
        tabs = GameDetailPreviewData.tabs,
        tabModules = GameDetailPreviewData.tabModules,
        fragmentManager = { PreviewDataGameDetailFragManager },
        interactor = GameDetailPreviewData.interactor,
        gameTitle = StringWrapper(""),
        gameInfo = GameDetailUi.GameInfo.Empty
    )
}

@Preview
@Composable
private fun GameDetailScreenPreview_InGameBaseball() {
    GameDetailScreen(
        title = "LAR @ CIN".asResourceString(),
        firstTeam = GameDetailPreviewData.firstTeamBaseball,
        secondTeam = GameDetailPreviewData.secondTeamBaseball,
        gameStatus = GameDetailPreviewData.baseballInGameInformation,
        firstTeamStatus = GameDetailPreviewData.emptyTeamStatus,
        secondTeamStatus = GameDetailPreviewData.emptyTeamStatus,
        shareLink = "rwrtwywuj",
        showShareLink = true,
        tabs = GameDetailPreviewData.tabs,
        tabModules = GameDetailPreviewData.tabModules,
        fragmentManager = { PreviewDataGameDetailFragManager },
        interactor = GameDetailPreviewData.interactor,
        gameTitle = StringWrapper(""),
        gameInfo = GameDetailUi.GameInfo.Empty
    )
}

@Preview
@Composable
private fun GameDetailScreenPreview_PreGameSoccer() {
    GameDetailScreen(
        title = "MUN v BOU".asResourceString(),
        firstTeam = GameDetailPreviewData.firstTeamSoccer,
        secondTeam = GameDetailPreviewData.secondTeamSoccer,
        gameStatus = GameDetailPreviewData.soccerPreGameInformation,
        firstTeamStatus = GameDetailPreviewData.emptyTeamStatus,
        secondTeamStatus = GameDetailPreviewData.emptyTeamStatus,
        shareLink = "rwrtwywuj",
        showShareLink = true,
        tabs = GameDetailPreviewData.tabs,
        tabModules = GameDetailPreviewData.tabModules,
        fragmentManager = { PreviewDataGameDetailFragManager },
        interactor = GameDetailPreviewData.interactor,
        gameTitle = StringWrapper(""),
        gameInfo = soccerRecentForm
    )
}

@Preview
@Composable
private fun GameDetailScreenPreview_PreGameSoccerNoRecentForm() {
    GameDetailScreen(
        title = "MUN v BOU".asResourceString(),
        firstTeam = GameDetailPreviewData.firstTeamSoccer,
        secondTeam = GameDetailPreviewData.secondTeamSoccer,
        gameStatus = GameDetailPreviewData.soccerPreGameInformation,
        firstTeamStatus = GameDetailPreviewData.emptyTeamStatus,
        secondTeamStatus = GameDetailPreviewData.emptyTeamStatus,
        shareLink = "rwrtwywuj",
        showShareLink = true,
        tabs = GameDetailPreviewData.tabs,
        tabModules = GameDetailPreviewData.tabModules,
        fragmentManager = { PreviewDataGameDetailFragManager },
        interactor = GameDetailPreviewData.interactor,
        gameTitle = StringWrapper(""),
        gameInfo = soccerRecentForm.copy(firstTeamRecentForm = emptyList(), showRecentForm = false)
    )
}