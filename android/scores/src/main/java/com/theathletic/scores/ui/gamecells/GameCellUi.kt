package com.theathletic.scores.ui.gamecells

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.components.AnimateBaseballBase
import com.theathletic.data.SizedImages
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.scores.R
import com.theathletic.scores.ui.gamecells.GameCellPreviewData.baseballInGameCell
import com.theathletic.scores.ui.gamecells.GameCellPreviewData.cancelledGameCell
import com.theathletic.scores.ui.gamecells.GameCellPreviewData.ctaGameCell
import com.theathletic.scores.ui.gamecells.GameCellPreviewData.inGameCell
import com.theathletic.scores.ui.gamecells.GameCellPreviewData.inGameCellRedCard
import com.theathletic.scores.ui.gamecells.GameCellPreviewData.inGameCellWithPossession
import com.theathletic.scores.ui.gamecells.GameCellPreviewData.inGameCellWithRanking
import com.theathletic.scores.ui.gamecells.GameCellPreviewData.penaltyGoalsGameCell
import com.theathletic.scores.ui.gamecells.GameCellPreviewData.postGameCell
import com.theathletic.scores.ui.gamecells.GameCellPreviewData.postGameCellWithRanking
import com.theathletic.scores.ui.gamecells.GameCellPreviewData.preGameCell
import com.theathletic.scores.ui.gamecells.GameCellPreviewData.preGameCellWithRanking
import com.theathletic.scores.ui.gamecells.GameCellPreviewData.tbdGameCell
import com.theathletic.scores.ui.gamecells.GameCellTags.GameCellBaseballBases
import com.theathletic.scores.ui.gamecells.GameCellTags.GameCellRow
import com.theathletic.scores.ui.gamecells.GameCellTags.GameCellTeamRanking
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.utility.conditional
import com.theathletic.ui.widgets.ResourceIcon
import com.theathletic.ui.widgets.TeamLogo

data class GameCellModel(
    val gameId: String,
    val firstTeam: Team,
    val secondTeam: Team,
    val title: String,
    val showTitle: Boolean,
    val discussionLinkText: String?,
    val infoWidget: InfoWidget,
    val impressionPayload: ImpressionPayload,
    val showTeamRanking: Boolean
) {

    data class Team(
        val logo: SizedImages,
        val name: String,
        val teamDetails: TeamDetails,
        val ranking: String,
        val isDimmed: Boolean,
    )

    sealed class TeamDetails {
        // Todo (Mark): When redoing the game cells to be column based remove this and have a single type
        //  so pregames could display an icon for example in the future
        data class PreGame(
            val pregameLabel: String
        ) : TeamDetails()

        data class InAndPostGame(
            val score: String,
            val penaltyGoals: String?,
            val icon: EventIcon?,
            val isWinner: Boolean
        ) : TeamDetails()
    }

    sealed class InfoWidget(open val infos: List<GameInfo>) {
        data class LabelWidget(override val infos: List<GameInfo>) : InfoWidget(infos)
        data class BaseballWidget(override val infos: List<GameInfo>, val occupiedBases: List<Int>) : InfoWidget(infos)
    }

    sealed class GameInfo(open val value: String) {
        data class DateTimeStatus(override val value: String) : GameInfo(value)
        data class Default(override val value: String) : GameInfo(value)
        data class Live(override val value: String) : GameInfo(value)
        data class Situation(override val value: String) : GameInfo(value)
        data class Status(override val value: String) : GameInfo(value)
    }

    enum class EventIcon {
        POSSESSION,
        RED_CARD
    }

    interface Interaction {
        data class OnJoinDiscussionClick(
            val gameId: String,
        ) : FeedInteraction
    }
}

@Composable
fun GameCell(
    gameId: String,
    title: String,
    showTitle: Boolean,
    firstTeam: GameCellModel.Team,
    secondTeam: GameCellModel.Team,
    discussionLinkText: String?,
    infoWidget: GameCellModel.InfoWidget,
    showDivider: Boolean,
    showTeamRanking: Boolean,
    onGameClicked: (String) -> Unit,
    onDiscussionLinkClicked: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .background(color = AthTheme.colors.dark200)
            .fillMaxWidth()
            .clickable { onGameClicked(gameId) }
            .padding(horizontal = 16.dp)
            .testTag(GameCellRow)
    ) {

        if (showTitle) {
            Text(
                text = title,
                style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                color = AthTheme.colors.dark500,
                modifier = Modifier.padding(vertical = 10.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(12.dp))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
        ) {
            Column(modifier = Modifier.weight(0.6f)) {
                StackedTeams(firstTeam, secondTeam, showTeamRanking)
            }
            GameCellVerticalDivider()
            Box(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .padding(start = 16.dp),
            ) {
                RenderInfoWidget(infoWidget)
            }
        }

        if (discussionLinkText != null) {
            ShowJoinDiscussion(
                linkText = discussionLinkText,
                onClicked = { onDiscussionLinkClicked(gameId) }
            )
        } else {
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (showDivider) GameCellHorizontalDivider()
    }
}

@Composable
private fun GameCellHorizontalDivider() {
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp),
        color = AthTheme.colors.dark300
    )
}

@Composable
private fun GameCellVerticalDivider() {
    Divider(
        modifier = Modifier
            .width(1.dp)
            .fillMaxHeight()
            .background(AthTheme.colors.dark300)
    )
}

@Composable
private fun StackedTeams(firstTeam: GameCellModel.Team, secondTeam: GameCellModel.Team, showTeamRanking: Boolean) {
    TeamWidget(
        logo = firstTeam.logo,
        name = firstTeam.name,
        rank = firstTeam.ranking,
        teamDetails = firstTeam.teamDetails,
        isDimmed = firstTeam.isDimmed,
        showTeamRanking = showTeamRanking
    )
    Spacer(modifier = Modifier.padding(vertical = 4.dp))
    TeamWidget(
        logo = secondTeam.logo,
        name = secondTeam.name,
        rank = secondTeam.ranking,
        teamDetails = secondTeam.teamDetails,
        isDimmed = secondTeam.isDimmed,
        showTeamRanking = showTeamRanking
    )
}

@Composable
fun RenderInfoWidget(infoWidget: GameCellModel.InfoWidget) {
    when (infoWidget) {
        is GameCellModel.InfoWidget.LabelWidget -> {
            RenderInfoLabels(infoWidget.infos)
        }
        is GameCellModel.InfoWidget.BaseballWidget -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RenderInfoLabels(
                    infos = infoWidget.infos,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .width(44.dp)
                        .padding(horizontal = 4.dp)
                ) {
                    BaseballOccupiedBases(
                        occupiedBases = infoWidget.occupiedBases,
                    )
                }
            }
        }
    }
}

@Composable
fun RenderInfoLabels(infos: List<GameCellModel.GameInfo>, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .then(modifier),
        verticalArrangement = Arrangement.Center
    ) {
        infos.map { it.ToLabel() }
    }
}

@Composable
private fun GameCellModel.GameInfo.ToLabel() {
    when (this) {
        is GameCellModel.GameInfo.DateTimeStatus -> {
            Text(
                text = this.value,
                style = AthTextStyle.Calibre.Utility.Medium.Small,
                color = AthTheme.colors.dark800,
            )
        }
        is GameCellModel.GameInfo.Default -> {
            Text(
                text = this.value,
                style = AthTextStyle.Calibre.Utility.Regular.Small,
                color = AthTheme.colors.dark500,
                lineHeight = 0.8.em
            )
        }
        is GameCellModel.GameInfo.Live -> AnimatedLiveText(value)
        is GameCellModel.GameInfo.Situation -> AnimatedSituationText(value)
        is GameCellModel.GameInfo.Status -> {
            Text(
                text = this.value,
                style = AthTextStyle.Calibre.Utility.Medium.Small,
                color = AthTheme.colors.dark800,
            )
        }
    }
}

@Composable
fun ShowJoinDiscussion(
    linkText: String,
    onClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClicked() }
            .padding(top = 10.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        val circleColor = AthTheme.colors.red
        Canvas(
            modifier = Modifier
                .padding(end = 6.dp)
                .weight(1f, false),
            onDraw = {
                drawCircle(
                    color = circleColor,
                    radius = 3.dp.toPx()
                )
            }
        )

        Text(
            text = linkText,
            color = AthTheme.colors.dark700,
            style = AthTextStyle.Calibre.Utility.Medium.Small
        )

        ResourceIcon(
            resourceId = R.drawable.ic_chalk_chevron_right,
            tint = AthTheme.colors.dark800,
            modifier = Modifier
                .padding(start = 6.dp)
                .size(size = 10.dp)
        )
    }
}

@Composable
private fun TeamWidget(
    logo: SizedImages,
    name: String,
    rank: String,
    isDimmed: Boolean,
    teamDetails: GameCellModel.TeamDetails,
    showTeamRanking: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TeamDetails(
            logo = logo,
            name = name,
            rank = rank,
            isDimmed = isDimmed,
            teamDetails = teamDetails,
            showTeamRanking = showTeamRanking,
            modifier = Modifier.weight(0.7f)
        )

        when (teamDetails) {
            is GameCellModel.TeamDetails.InAndPostGame -> {
                ShowTeamScoreDetails(
                    score = teamDetails.score,
                    penaltyGoals = teamDetails.penaltyGoals,
                    isDimmed = isDimmed,
                    isWinner = teamDetails.isWinner,
                )
            }
            is GameCellModel.TeamDetails.PreGame -> {
                ShowTeamPreGameDetails(label = teamDetails.pregameLabel)
            }
        }
    }
}

@Composable
private fun BoxScope.ShowWinningIndicator(isWinningTeam: Boolean, modifier: Modifier = Modifier) {
    if (isWinningTeam) {
        ResourceIcon(
            resourceId = R.drawable.winner_indicator,
            tint = AthTheme.colors.dark800,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(16.dp)
                .padding(start = 8.dp)
                .align(Alignment.CenterEnd)
        )
    }
}

@Composable
private fun ShowTeamPreGameDetails(
    label: String
) {
    Box {
        Text(
            text = label,
            style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
            color = AthTheme.colors.dark500,
            modifier = Modifier
                .padding(
                    start = 8.dp,
                    end = 16.dp
                )
        )
    }
}

@Composable
private fun ShowTeamScoreDetails(
    score: String,
    penaltyGoals: String?,
    isDimmed: Boolean,
    isWinner: Boolean
) {
    Box {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AnimatedScoreText(score, isDimmed)
            AnimatedPenaltyScoreText(penaltyGoals, isDimmed)
            Spacer(modifier = Modifier.width(14.dp))
        }
        ShowWinningIndicator(isWinner, modifier = Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
private fun TeamDetails(
    logo: SizedImages,
    name: String,
    rank: String,
    isDimmed: Boolean,
    teamDetails: GameCellModel.TeamDetails,
    showTeamRanking: Boolean,
    modifier: Modifier = Modifier
) {
    val isLightTheme = MaterialTheme.colors.isLight
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TeamLogo(
            teamUrls = logo,
            preferredSize = 24.dp,
            modifier = Modifier
                .size(24.dp)
                .conditional(isDimmed) { alpha(if (isLightTheme) 0.3f else 0.4f) }
        )

        if (showTeamRanking) {
            ShowTeamRank(rank)
        }

        Text(
            text = name,
            style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge,
            color = if (isDimmed) AthTheme.colors.dark500 else AthTheme.colors.dark800,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.conditional(rank.isEmpty()) { padding(start = 8.dp) }
        )

        if (teamDetails is GameCellModel.TeamDetails.InAndPostGame) {
            teamDetails.icon?.let { icon -> ShowEventIcon(icon = icon) }
        }
    }
}

@Composable
private fun ShowEventIcon(icon: GameCellModel.EventIcon) {
    when (icon) {
        GameCellModel.EventIcon.POSSESSION -> {
            ResourceIcon(
                resourceId = R.drawable.americanfootball_possession,
                tint = AthTheme.colors.dark800,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
        GameCellModel.EventIcon.RED_CARD -> {
            ResourceIcon(
                resourceId = R.drawable.ic_soccer_card_red,
                modifier = Modifier
                    .padding(start = 6.dp)
                    .size(size = 10.dp)
            )
        }
    }
}

@Composable
private fun ShowTeamRank(rank: String) {
    if (rank.isNotEmpty()) {
        Text(
            text = rank,
            style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
            color = AthTheme.colors.dark500,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(start = 6.dp, end = 4.dp)
                .defaultMinSize(minWidth = 12.dp)
                .testTag(GameCellTeamRanking)
        )
    } else if (rank.isEmpty()) {
        Spacer(modifier = Modifier.width(14.dp))
    }
}

@Composable
private fun BaseballOccupiedBases(
    occupiedBases: List<Int>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(
                width = 36.dp,
                height = 27.dp
            )
            .testTag(GameCellBaseballBases)
    ) {
        AnimateBaseballBase(
            occupied = occupiedBases.isNotEmpty() && occupiedBases.contains(3),
            size = 17.dp,
            modifier = Modifier.align(Alignment.BottomStart)
        )
        AnimateBaseballBase(
            occupied = occupiedBases.isNotEmpty() && occupiedBases.contains(2),
            size = 17.dp,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        AnimateBaseballBase(
            occupied = occupiedBases.isNotEmpty() && occupiedBases.contains(1),
            size = 17.dp,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

@Composable
private fun AnimatedScoreText(score: String, isDimmed: Boolean) {
    AnimatedContent(
        targetState = score,
        transitionSpec = { scoreEntryExitTransition() }
    ) { value ->
        Text(
            text = value,
            style = AthTextStyle.Calibre.Headline.Medium.Small,
            textAlign = TextAlign.End,
            color = if (isDimmed) AthTheme.colors.dark500 else AthTheme.colors.dark800,
            modifier = Modifier
                .defaultMinSize(minWidth = 34.dp)
                .padding(start = 4.dp)
        )
    }
}

@Composable
private fun AnimatedPenaltyScoreText(penalties: String?, isDimmed: Boolean) {
    var visible by remember { mutableStateOf(false) }
    visible = penalties != null

    AnimatedVisibility(
        visible = visible,
        enter = penaltyScoreInitialEnterTransition()
    ) {
        AnimatedContent(
            targetState = penalties,
            transitionSpec = {
                scoreEntryExitTransition()
            }
        ) { value ->
            Text(
                text = value ?: "",
                style = AthTextStyle.Calibre.Utility.Regular.Large,
                textAlign = TextAlign.End,
                color = if (isDimmed) AthTheme.colors.dark500 else AthTheme.colors.dark800,
                modifier = Modifier
                    .defaultMinSize(minWidth = 26.dp)
                    .padding(start = 2.dp)
            )
        }
    }
}

@Composable
private fun AnimatedSituationText(text: String) {
    AnimatedContent(
        targetState = text,
        transitionSpec = { situationTextEntryExitTransition() }
    ) { value ->
        Text(
            text = value,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark800,
            modifier = Modifier.padding(end = 8.dp)
        )
    }
}

@Composable
private fun AnimatedLiveText(text: String) {
    AnimatedContent(
        targetState = text,
        transitionSpec = { situationTextEntryExitTransition() }
    ) { value ->
        Text(
            text = value,
            style = AthTextStyle.Calibre.Utility.Medium.Small,
            color = AthTheme.colors.red,
            modifier = Modifier
                .padding(end = 8.dp)
                .testTag(GameCellTags.GameCellLiveText)
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun scoreEntryExitTransition(): ContentTransform =
    // Incoming/Entry score text
    slideInVertically(animationSpec = tween(durationMillis = 500)) { -it } +
        fadeIn(animationSpec = tween(durationMillis = 1200)) with
        // Outgoing/Exit score text
        fadeOut(animationSpec = tween(durationMillis = 300)) +
            slideOutVertically(animationSpec = tween(durationMillis = 500)) { it }

private fun penaltyScoreInitialEnterTransition(): EnterTransition =
    expandHorizontally(animationSpec = tween(durationMillis = 500), expandFrom = Alignment.Start) +
        fadeIn(animationSpec = tween(durationMillis = 1200), initialAlpha = 0f)

@OptIn(ExperimentalAnimationApi::class)
private fun situationTextEntryExitTransition(): ContentTransform =
    // Incoming/Entry score text
    slideInHorizontally(animationSpec = tween(durationMillis = 1200)) { 40 } +
        fadeIn(animationSpec = tween(durationMillis = 1800)) with
        // Outgoing/Exit score text
        fadeOut(animationSpec = tween(durationMillis = 400)) +
            slideOutHorizontally(animationSpec = tween(durationMillis = 800)) { -40 }

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun ScoreCell_PreGame_PreviewLargeDevice() {
    GameCell(
        gameId = "gameId",
        title = preGameCell.title,
        showTitle = preGameCell.showTitle,
        firstTeam = preGameCell.firstTeam.copy(ranking = "12"),
        secondTeam = preGameCell.secondTeam,
        infoWidget = preGameCell.infoWidget,
        discussionLinkText = preGameCell.discussionLinkText,
        showDivider = true,
        showTeamRanking = true,
        onGameClicked = {},
        onDiscussionLinkClicked = {},
    )
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun ScoreCell_PreGameWithRanking_PreviewLargeDevice() {
    GameCell(
        gameId = "gameId",
        title = preGameCellWithRanking.title,
        showTitle = preGameCellWithRanking.showTitle,
        firstTeam = preGameCellWithRanking.firstTeam,
        secondTeam = preGameCellWithRanking.secondTeam,
        infoWidget = preGameCellWithRanking.infoWidget,
        discussionLinkText = preGameCellWithRanking.discussionLinkText,
        showDivider = true,
        showTeamRanking = true,
        onGameClicked = {},
        onDiscussionLinkClicked = {},
    )
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun ScoreCell_InGame_PreviewLargeDevice() {
    GameCell(
        gameId = "gameId",
        title = inGameCell.title,
        showTitle = inGameCell.showTitle,
        firstTeam = inGameCell.firstTeam,
        secondTeam = inGameCell.secondTeam,
        infoWidget = inGameCell.infoWidget,
        discussionLinkText = inGameCell.discussionLinkText,
        showDivider = true,
        showTeamRanking = false,
        onGameClicked = {},
        onDiscussionLinkClicked = {},
    )
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun ScoreCell_InGameWithPossession_PreviewLargeDevice() {
    GameCell(
        gameId = "gameId",
        title = inGameCellWithPossession.title,
        showTitle = inGameCellWithPossession.showTitle,
        firstTeam = inGameCellWithPossession.firstTeam,
        secondTeam = inGameCellWithPossession.secondTeam,
        infoWidget = inGameCellWithPossession.infoWidget,
        discussionLinkText = inGameCellWithPossession.discussionLinkText,
        showDivider = true,
        showTeamRanking = false,
        onGameClicked = {},
        onDiscussionLinkClicked = {},
    )
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun ScoreCell_InGameWithRanking_PreviewLargeDevice() {
    GameCell(
        gameId = "gameId",
        title = inGameCellWithRanking.title,
        showTitle = inGameCellWithRanking.showTitle,
        firstTeam = inGameCellWithRanking.firstTeam,
        secondTeam = inGameCellWithRanking.secondTeam,
        infoWidget = inGameCellWithRanking.infoWidget,
        discussionLinkText = inGameCellWithRanking.discussionLinkText,
        showDivider = true,
        showTeamRanking = false,
        onGameClicked = {},
        onDiscussionLinkClicked = {},
    )
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun ScoreCell_BaseballInGameWith_PreviewLargeDevice() {
    GameCell(
        gameId = "gameId",
        title = baseballInGameCell.title,
        showTitle = baseballInGameCell.showTitle,
        firstTeam = baseballInGameCell.firstTeam,
        secondTeam = baseballInGameCell.secondTeam,
        infoWidget = baseballInGameCell.infoWidget,
        discussionLinkText = baseballInGameCell.discussionLinkText,
        showDivider = true,
        showTeamRanking = false,
        onGameClicked = {},
        onDiscussionLinkClicked = {},
    )
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun ScoreCell_InGameWithRedCard_PreviewLargeDevice() {
    GameCell(
        gameId = "gameId",
        title = inGameCellRedCard.title,
        showTitle = inGameCellRedCard.showTitle,
        firstTeam = inGameCellRedCard.firstTeam,
        secondTeam = inGameCellRedCard.secondTeam,
        infoWidget = inGameCellRedCard.infoWidget,
        discussionLinkText = inGameCellRedCard.discussionLinkText,
        showDivider = true,
        showTeamRanking = false,
        onGameClicked = {},
        onDiscussionLinkClicked = {},
    )
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun ScoreCell_PostGameWithTitle_PreviewLargeDevice() {
    GameCell(
        gameId = "gameId",
        title = postGameCell.title,
        showTitle = postGameCell.showTitle,
        firstTeam = postGameCell.firstTeam,
        secondTeam = postGameCell.secondTeam,
        infoWidget = postGameCell.infoWidget,
        discussionLinkText = postGameCell.discussionLinkText,
        showDivider = true,
        showTeamRanking = false,
        onGameClicked = {},
        onDiscussionLinkClicked = {},
    )
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun ScoreCell_PostGameWithTitleAndRanking_PreviewLargeDevice() {
    GameCell(
        gameId = "gameId",
        title = postGameCellWithRanking.title,
        showTitle = postGameCellWithRanking.showTitle,
        firstTeam = postGameCellWithRanking.firstTeam,
        secondTeam = postGameCellWithRanking.secondTeam,
        infoWidget = postGameCellWithRanking.infoWidget,
        discussionLinkText = postGameCellWithRanking.discussionLinkText,
        showDivider = true,
        showTeamRanking = false,
        onGameClicked = {},
        onDiscussionLinkClicked = {},
    )
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun ScoreCell_TBDGame_PreviewLargeDevice() {
    GameCell(
        gameId = "gameId",
        title = tbdGameCell.title,
        showTitle = tbdGameCell.showTitle,
        firstTeam = tbdGameCell.firstTeam,
        secondTeam = tbdGameCell.secondTeam,
        infoWidget = tbdGameCell.infoWidget,
        discussionLinkText = tbdGameCell.discussionLinkText,
        showDivider = true,
        showTeamRanking = false,
        onGameClicked = {},
        onDiscussionLinkClicked = {},
    )
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun ScoreCell_CancelledGame_PreviewLargeDevice() {
    GameCell(
        gameId = "gameId",
        title = cancelledGameCell.title,
        showTitle = cancelledGameCell.showTitle,
        firstTeam = cancelledGameCell.firstTeam,
        secondTeam = cancelledGameCell.secondTeam,
        infoWidget = cancelledGameCell.infoWidget,
        discussionLinkText = cancelledGameCell.discussionLinkText,
        showDivider = true,
        showTeamRanking = false,
        onGameClicked = {},
        onDiscussionLinkClicked = {},
    )
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun ScoreCell_CTAGame_PreviewLargeDevice() {
    GameCell(
        gameId = "gameId",
        title = ctaGameCell.title,
        showTitle = ctaGameCell.showTitle,
        firstTeam = ctaGameCell.firstTeam,
        secondTeam = ctaGameCell.secondTeam,
        infoWidget = ctaGameCell.infoWidget,
        discussionLinkText = ctaGameCell.discussionLinkText,
        showDivider = true,
        showTeamRanking = false,
        onGameClicked = {},
        onDiscussionLinkClicked = {},
    )
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun ScoreCell_Penalty_Goals_PreviewLargeDevice() {
    GameCell(
        gameId = "gameId",
        title = penaltyGoalsGameCell.title,
        showTitle = penaltyGoalsGameCell.showTitle,
        firstTeam = penaltyGoalsGameCell.firstTeam,
        secondTeam = penaltyGoalsGameCell.secondTeam,
        infoWidget = penaltyGoalsGameCell.infoWidget,
        discussionLinkText = penaltyGoalsGameCell.discussionLinkText,
        showDivider = true,
        showTeamRanking = false,
        onGameClicked = {},
        onDiscussionLinkClicked = {},
    )
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun ScoreCell_InGameWithPossession_PreviewLargeDeviceLight() {
    AthleticTheme(lightMode = true) {
        GameCell(
            gameId = "gameId",
            title = inGameCellWithPossession.title,
            showTitle = inGameCellWithPossession.showTitle,
            firstTeam = inGameCellWithPossession.firstTeam,
            secondTeam = inGameCellWithPossession.secondTeam,
            infoWidget = inGameCellWithPossession.infoWidget,
            discussionLinkText = inGameCellWithPossession.discussionLinkText,
            showDivider = true,
            showTeamRanking = false,
            onGameClicked = {},
            onDiscussionLinkClicked = {},
        )
    }
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun ScoreCell_PostGameWithTitleAndRanking_PreviewLargeDeviceLight() {
    AthleticTheme(lightMode = true) {
        GameCell(
            gameId = "gameId",
            title = postGameCellWithRanking.title,
            showTitle = postGameCellWithRanking.showTitle,
            firstTeam = postGameCellWithRanking.firstTeam,
            secondTeam = postGameCellWithRanking.secondTeam,
            infoWidget = postGameCellWithRanking.infoWidget,
            discussionLinkText = postGameCellWithRanking.discussionLinkText,
            showDivider = true,
            showTeamRanking = false,
            onGameClicked = {},
            onDiscussionLinkClicked = {},
        )
    }
}

object GameCellTags {
    const val GameCellTeamRanking = "GameCellTeamRanking"
    const val GameCellRow = "GameCellRow"
    const val GameCellBaseballBases = "GameCellBaseballBases"
    const val GameCellLiveText = "GameCellLiveText"
}