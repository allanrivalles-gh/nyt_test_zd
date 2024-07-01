package com.theathletic.boxscore.ui.modules

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Top
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.BaseballCurrentInningPlayUiModel
import com.theathletic.boxscore.ui.BoxScorePreviewData.baseballCurrentInningPlayMock
import com.theathletic.boxscore.ui.CurrentInningUi
import com.theathletic.boxscore.ui.playbyplay.BaseballPitchPlay
import com.theathletic.feed.ui.FeedInteractor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.asString
import com.theathletic.ui.widgets.Headshot
import com.theathletic.ui.widgets.NodeType
import com.theathletic.ui.widgets.TimelineNode
import com.theathletic.utility.safeLet
import java.util.Locale

enum class IndicatorType {
    Balls, Strikes, Outs;

    val color: Color
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            Balls -> AthTheme.colors.green
            Strikes -> AthTheme.colors.red
            Outs -> AthTheme.colors.dark700
        }

    val maxIndicators: Int
        get() = when (this) {
            Balls -> 4
            Strikes -> 3
            Outs -> 3
        }

    val title: String
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            Balls -> stringResource(R.string.box_score_current_inning_indicator_balls)
            Strikes -> stringResource(R.string.box_score_current_inning_indicator_strikes)
            Outs -> stringResource(R.string.box_score_current_inning_indicator_outs)
        }.uppercase(Locale.getDefault())
}

@Composable
private fun RowScope.DrawCircle(circleColor: Color) {
    Canvas(
        modifier = Modifier
            .size(10.dp, 10.dp)
            .padding(end = 2.dp)
            .weight(1f, false),
        onDraw = {
            drawCircle(
                color = circleColor,
                radius = 10f
            )
        }
    )
}

@Composable
private fun StatusIndicator(type: IndicatorType, filledCount: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = type.title,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark700
        )
        Spacer(modifier = Modifier.width(5.dp))
        for (index in 1..type.maxIndicators) {
            val color = if (index <= filledCount) type.color else AthTheme.colors.dark300
            DrawCircle(color)
        }
    }
}

@Composable
fun InningStatusIndicator(playStatus: List<CurrentInningUi.CurrentPlayStatus>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(30.dp)
            .background(color = AthTheme.colors.dark200)
    ) {
        Divider(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .align(Alignment.TopStart),
            color = AthTheme.colors.dark300
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            playStatus.forEach { indicator ->
                StatusIndicator(
                    type = indicator.type,
                    filledCount = indicator.count
                )
            }
        }

        Divider(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .align(Alignment.BottomStart),
            color = AthTheme.colors.dark300
        )
    }
}

@Composable
internal fun PitcherBatterUi(
    pitcher: CurrentInningUi.PlayerSummary?,
    batter: CurrentInningUi.PlayerSummary?
) {
    Row(
        modifier = Modifier
            .background(color = AthTheme.colors.dark200)
            .height(IntrinsicSize.Min)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        pitcher?.let {
            PlayerDetailsComponent(pitcher, Modifier.weight(0.5f))
        }

        safeLet(pitcher, batter) { pr, br ->
            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 11.dp)
                    .width(1.dp),
                color = AthTheme.colors.dark300
            )
        }

        batter?.let {
            PlayerDetailsComponent(batter, Modifier.weight(0.5f))
        }
    }
}

@Composable
private fun PlayerDetailsComponent(
    playerSummary: CurrentInningUi.PlayerSummary,
    modifier: Modifier
) {
    Column(modifier = Modifier.then(modifier)) {
        Text(
            text = playerSummary.title.asString().uppercase(Locale.getDefault()),
            style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall,
            color = AthTheme.colors.dark500
        )

        Row(
            modifier = Modifier
                .padding(top = 10.dp)
                .wrapContentHeight()
        ) {

            Headshot(
                headshotsUrls = playerSummary.headshotList ?: emptyList(),
                teamUrls = emptyList(),
                teamColor = playerSummary.teamColor,
                preferredSize = 40.dp,
                modifier = Modifier
                    .size(40.dp, 40.dp)
                    .align(alignment = Top)
            )

            Column(modifier = Modifier.padding(start = 8.dp)) {
                Row {
                    Text(
                        text = playerSummary.name,
                        style = AthTextStyle.Calibre.Utility.Medium.Small,
                        color = AthTheme.colors.dark800,
                        modifier = Modifier.alignByBaseline()
                    )

                    Text(
                        text = playerSummary.playInfo.asString(),
                        style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                        color = AthTheme.colors.dark500,
                        modifier = Modifier
                            .alignByBaseline()
                            .padding(start = 4.dp)
                    )
                }
                Text(
                    text = playerSummary.stats.asString(),
                    style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall,
                    color = AthTheme.colors.dark500,
                    modifier = Modifier.padding(vertical = 3.dp)
                )
                Text(
                    text = playerSummary.lastPlay.asString(),
                    style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                    color = AthTheme.colors.dark500,
                    maxLines = 3
                )
            }
        }
    }
}

@Composable
private fun PlayRow(
    title: String,
    nodeType: NodeType
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(24.dp)
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimelineNode(
            nodeType = nodeType
        )
        Text(
            text = title,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark800,
            modifier = Modifier
                .wrapContentHeight()
                .padding(start = 26.dp)
                .padding(vertical = 8.dp)
        )
    }
}

@Composable
private fun SubPlayRow(
    baseballCurrentInningPitchPlayModule: BaseballPlayModule.PitchPlay,
    nodeType: NodeType
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(24.dp)
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimelineNode(
            nodeType = nodeType
        )

        Box(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .padding(start = 26.dp)
        ) {
            BaseballPitchPlay(
                title = baseballCurrentInningPitchPlayModule.title,
                description = baseballCurrentInningPitchPlayModule.description,
                hitZone = baseballCurrentInningPitchPlayModule.hitZone,
                occupiedBases = baseballCurrentInningPitchPlayModule.occupiedBases,
                pitchNumber = baseballCurrentInningPitchPlayModule.pitchNumber,
                pitchOutcomeColor = baseballCurrentInningPitchPlayModule.pitchOutcomeType.color,
                pitchZone = baseballCurrentInningPitchPlayModule.pitchZone
            )
        }
    }
}

@Composable
internal fun CurrentInningPlay(
    currentInning: List<CurrentInningUi.Play>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .background(color = AthTheme.colors.dark200)
            .padding(
                start = 26.dp,
                end = 16.dp,
                top = 18.dp
            )
    ) {
        val isSinglePlay = currentInning.size == 1
        currentInning.forEachIndexed { index, baseballCurrentInningPlay ->
            val nodeType =
                when (index) {
                    0 -> if (isSinglePlay) NodeType.SINGLE else NodeType.FIRST
                    currentInning.lastIndex -> if (baseballCurrentInningPlay.plays.isEmpty()) NodeType.LAST else NodeType.MIDDLE
                    else -> NodeType.MIDDLE
                }

            PlayRow(
                title = baseballCurrentInningPlay.title,
                nodeType = nodeType
            )
            baseballCurrentInningPlay.plays.forEach {
                SubPlayRow(
                    it,
                    if (isSinglePlay) NodeType.NONE else NodeType.SPACER
                )
            }
        }
    }
}

@Composable
internal fun FullPlayByPlays(interactor: FeedInteractor) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
            .padding(top = 15.dp)
            .clickable {
                interactor.send(CurrentInningModule.Interaction.OnFullPlayByPlayClick)
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Text(
            text = stringResource(id = R.string.box_score_recent_plays_full_play),
            style = AthTextStyle.Calibre.Utility.Regular.ExtraLarge,
            color = AthTheme.colors.dark500,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 10.dp)
        )

        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = AthTheme.colors.dark500,
            modifier = Modifier.padding(vertical = 10.dp)
        )
    }
}

@Composable
internal fun FullPlayByPlays(interactor: BaseballCurrentInningPlayUiModel.Interactor) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
            .padding(top = 15.dp)
            .clickable {
                interactor.onFullPlayByPlayClick()
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Text(
            text = stringResource(id = R.string.box_score_recent_plays_full_play),
            style = AthTextStyle.Calibre.Utility.Regular.ExtraLarge,
            color = AthTheme.colors.dark500,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 10.dp)
        )

        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = AthTheme.colors.dark500,
            modifier = Modifier.padding(vertical = 10.dp)
        )
    }
}

@Preview
@Composable
private fun GameIndicator_Preview() {
    if (baseballCurrentInningPlayMock.playStatus.isNotEmpty()) {
        InningStatusIndicator(baseballCurrentInningPlayMock.playStatus)
    }
}

@Preview
@Composable
private fun GameIndicator_PreviewLight() {
    AthleticTheme(lightMode = true) {
        if (baseballCurrentInningPlayMock.playStatus.isNotEmpty()) {
            InningStatusIndicator(baseballCurrentInningPlayMock.playStatus)
        }
    }
}

@Preview
@Composable
private fun PitcherBatter_Preview() {
    PitcherBatterUi(
        baseballCurrentInningPlayMock.pitcher,
        baseballCurrentInningPlayMock.batter
    )
}

@Preview
@Composable
private fun PitcherBatter_PreviewLight() {
    AthleticTheme(lightMode = true) {
        PitcherBatterUi(
            baseballCurrentInningPlayMock.pitcher,
            baseballCurrentInningPlayMock.batter
        )
    }
}

@Preview
@Composable
private fun CurrentInningPlay_Preview() {
    if (baseballCurrentInningPlayMock.currentInning.isNotEmpty()) {
        CurrentInningPlay(baseballCurrentInningPlayMock.currentInning)
    }
}

@Preview
@Composable
private fun CurrentInningPlay_PreviewLight() {
    AthleticTheme(lightMode = true) {
        if (baseballCurrentInningPlayMock.currentInning.isNotEmpty()) {
            CurrentInningPlay(baseballCurrentInningPlayMock.currentInning)
        }
    }
}