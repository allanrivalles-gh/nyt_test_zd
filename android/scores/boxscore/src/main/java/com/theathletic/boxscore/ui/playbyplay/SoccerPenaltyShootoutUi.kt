package com.theathletic.boxscore.ui.playbyplay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.BoxScoreFooterDivider
import com.theathletic.boxscore.ui.modules.SoccerPreviewData.penaltyShootout
import com.theathletic.data.SizedImages
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asString
import com.theathletic.ui.widgets.TeamLogo

data class SoccerPenaltyShootoutUI(
    val firstTeamName: String,
    val firstTeamLogos: SizedImages,
    val secondTeamName: String,
    val secondTeamLogos: SizedImages,
    val penaltyShots: List<PenaltyShot>
) {
    data class PenaltyShot(
        val firstTeamPlayerName: String,
        val firstPenaltyState: PenaltyState,
        val penaltyTitle: ResourceString,
        val secondTeamPlayerName: String,
        val secondPenaltyState: PenaltyState,
    )

    enum class PenaltyState {
        PENDING, SCORED, MISSED
    }
}

@Composable
internal fun SoccerPenaltyShootout(
    firstTeamName: String,
    firstTeamLogos: SizedImages,
    secondTeamName: String,
    secondTeamLogos: SizedImages,
    penaltyShots: List<SoccerPenaltyShootoutUI.PenaltyShot>
) {

    Column(
        modifier = Modifier
            .background(AthTheme.colors.dark200)
            .fillMaxWidth()
    ) {

        TeamsRow(
            firstTeamName = firstTeamName,
            firstTeamLogos = firstTeamLogos,
            secondTeamName = secondTeamName,
            secondTeamLogos = secondTeamLogos
        )

        penaltyShots.forEach { penalty ->
            PenaltyShootoutRow(
                firstTeamPlayerName = penalty.firstTeamPlayerName,
                firstPenaltyState = penalty.firstPenaltyState,
                penaltyTitle = penalty.penaltyTitle.asString(),
                secondTeamPlayerName = penalty.secondTeamPlayerName,
                secondPenaltyState = penalty.secondPenaltyState
            )
        }

        BoxScoreFooterDivider(false)
        Divider(
            color = AthTheme.colors.dark300,
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun TeamsRow(
    firstTeamName: String,
    firstTeamLogos: SizedImages,
    secondTeamName: String,
    secondTeamLogos: SizedImages
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TeamLogo(
            teamUrls = firstTeamLogos,
            preferredSize = 24.dp,
            modifier = Modifier
                .size(24.dp)
                .align(alignment = Alignment.CenterVertically)
        )

        Text(
            text = firstTeamName,
            style = AthTextStyle.Calibre.Utility.Medium.Small,
            color = AthTheme.colors.dark800,
            modifier = Modifier.padding(start = 12.dp)
        )

        Spacer(modifier = Modifier.weight(0.6f))

        Text(
            text = secondTeamName,
            style = AthTextStyle.Calibre.Utility.Medium.Small,
            color = AthTheme.colors.dark800,
            modifier = Modifier.padding(end = 12.dp)
        )

        TeamLogo(
            teamUrls = secondTeamLogos,
            preferredSize = 24.dp,
            modifier = Modifier
                .size(24.dp)
                .align(alignment = Alignment.CenterVertically)
        )
    }
}

@Composable
private fun PenaltyShootoutRow(
    firstTeamPlayerName: String,
    firstPenaltyState: SoccerPenaltyShootoutUI.PenaltyState,
    penaltyTitle: String,
    secondTeamPlayerName: String,
    secondPenaltyState: SoccerPenaltyShootoutUI.PenaltyState,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        PlayerName(
            name = firstTeamPlayerName
        )

        PenaltyIcon(firstPenaltyState)

        Text(
            text = penaltyTitle,
            style = AthTextStyle.Calibre.Utility.Medium.Small,
            color = AthTheme.colors.dark800,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(0.2f)
        )

        PenaltyIcon(secondPenaltyState)

        PlayerName(
            name = secondTeamPlayerName,
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun RowScope.PlayerName(name: String, textAlign: TextAlign = TextAlign.Start) {
    Text(
        text = name,
        style = AthTextStyle.Calibre.Utility.Regular.Small,
        color = AthTheme.colors.dark800,
        textAlign = textAlign,
        modifier = Modifier.weight(0.4f)
    )
}

@Composable
fun PenaltyIcon(state: SoccerPenaltyShootoutUI.PenaltyState) {
    when (state) {
        SoccerPenaltyShootoutUI.PenaltyState.SCORED -> ScoredGoalIcon()
        SoccerPenaltyShootoutUI.PenaltyState.MISSED -> MissedGoalIcon()
        else -> PendingIcon()
    }
}

@Composable
private fun MissedGoalIcon() {
    val circleColor = AthTheme.colors.red
    Box(
        modifier = Modifier
            .size(12.dp)
            .drawBehind {
                drawCircle(
                    color = circleColor,
                    radius = 5.dp.toPx(),
                    style = Stroke(6f)
                )
            }
    )
}

@Composable
private fun ScoredGoalIcon() {
    val circleColor = AthTheme.colors.green
    Box(
        modifier = Modifier
            .size(12.dp)
            .drawBehind {
                drawCircle(
                    color = circleColor,
                    radius = 6.dp.toPx()
                )
            }
    )
}

@Composable
private fun PendingIcon() {
    val circleColor = AthColor.Gray300
    Box(
        modifier = Modifier
            .size(12.dp)
            .drawBehind {
                drawCircle(
                    color = circleColor,
                    radius = 6.dp.toPx()
                )
            }
    )
}

@Preview
@Composable
private fun SeasonStats_Preview() {
    penaltyShootout.Render()
}

@Preview(device = Devices.PIXEL)
@Composable
private fun SeasonStats_PreviewSmallDevice() {
    penaltyShootout.Render()
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun SeasonStats_PreviewLargeDevice() {
    penaltyShootout.Render()
}

@Preview
@Composable
private fun SeasonStats_PreviewLight() {
    AthleticTheme(lightMode = true) {
        penaltyShootout.Render()
    }
}