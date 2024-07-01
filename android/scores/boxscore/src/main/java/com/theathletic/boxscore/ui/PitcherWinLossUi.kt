package com.theathletic.boxscore.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.BoxScorePreviewData.baseballPitcherWinLossMock
import com.theathletic.boxscore.ui.modules.PitcherWinLossModule
import com.theathletic.data.SizedImages
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.ResourceString
import com.theathletic.ui.UiModel
import com.theathletic.ui.asString
import com.theathletic.ui.widgets.Headshot

data class BaseballPitcherWinLossUiModel(
    val id: String,
    val pitchers: List<Pitcher>
) : UiModel {
    override val stableId = "BoxScoreBaseballPitcherWinLoss:$id"

    data class Pitcher(
        val title: ResourceString,
        val headshot: SizedImages,
        val name: String,
        val stats: String,
        val teamColor: Color
    )
}

@Deprecated("Use FeedModule version below")
@Composable
fun PitcherWinLoss(pitchersList: BaseballPitcherWinLossUiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(color = AthTheme.colors.dark200)
            .padding(vertical = 4.dp)
    ) {
        pitchersList.pitchers.forEachIndexed { index, pitcher ->

            PitcherDetails(
                title = pitcher.title,
                headshot = pitcher.headshot,
                name = pitcher.name,
                stats = pitcher.stats,
                teamColor = pitcher.teamColor,
                modifier = Modifier.weight(1f)
            )
            if (index < (pitchersList.pitchers.lastIndex)) {
                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp),
                    color = AthTheme.colors.dark300
                )
            }
        }
    }
}

@Composable
fun PitcherWinLoss(
    pitchers: List<PitcherWinLossModule.Pitcher>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(color = AthTheme.colors.dark200)
            .padding(vertical = 4.dp)
    ) {
        pitchers.forEachIndexed { index, pitcher ->
            PitcherDetails(
                title = pitcher.title,
                headshot = pitcher.headshot,
                name = pitcher.name,
                stats = pitcher.stats,
                teamColor = pitcher.teamColor,
                modifier = Modifier.weight(1f)
            )
            if (index < pitchers.lastIndex) {
                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp),
                    color = AthTheme.colors.dark300
                )
            }
        }
    }
}

@Composable
private fun PitcherDetails(
    title: ResourceString,
    headshot: SizedImages,
    name: String,
    stats: String,
    teamColor: Color,
    modifier: Modifier
) {
    Column(
        modifier = Modifier
            .background(color = AthTheme.colors.dark200)
            .padding(horizontal = 8.dp)
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(bottom = 10.dp),
            text = title.asString(),
            style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall,
            color = AthTheme.colors.dark500,
            textAlign = TextAlign.Center,
        )

        Headshot(
            headshotsUrls = headshot,
            teamUrls = emptyList(),
            teamColor = teamColor,
            preferredSize = 32.dp,
            modifier = Modifier
                .size(32.dp, 32.dp)
        )

        Text(
            text = name,
            style = AthTextStyle.Calibre.Utility.Medium.Small,
            color = AthTheme.colors.dark800,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp)
        )

        Text(
            text = stats,
            maxLines = 3,
            style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall,
            color = AthTheme.colors.dark600,
            textAlign = TextAlign.Center,
            softWrap = true,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview
@Composable
private fun PitcherWinLoss_Preview() {
    PitcherWinLoss(baseballPitcherWinLossMock)
}

@Preview
@Composable
private fun PitcherWinLossTwo_Preview() {
    PitcherWinLoss(baseballPitcherWinLossMock.copy(pitchers = baseballPitcherWinLossMock.pitchers.drop(1)))
}

@Preview(device = Devices.PIXEL)
@Composable
private fun PitcherWinLoss_Preview_SmallDevice() {
    PitcherWinLoss(baseballPitcherWinLossMock)
}

@Preview(device = Devices.PIXEL)
@Composable
private fun PitcherWinLossTwo_Preview_SmallDevice() {
    PitcherWinLoss(baseballPitcherWinLossMock.copy(pitchers = baseballPitcherWinLossMock.pitchers.drop(1)))
}

@Preview
@Composable
private fun PitcherWinLoss_PreviewLight() {
    AthleticTheme(lightMode = true) {
        PitcherWinLoss(baseballPitcherWinLossMock)
    }
}