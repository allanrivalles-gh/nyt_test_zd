package com.theathletic.boxscore.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.BoxScorePreviewData.pitcherModuleMock
import com.theathletic.data.SizedImages
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asString
import com.theathletic.ui.widgets.Headshot
import com.theathletic.ui.widgets.TeamLogo

sealed class StartingPitchersUi(open val teamColor: Color, open val details: ResourceString) {
    data class PitcherStats(
        override val teamColor: Color,
        override val details: ResourceString,
        val name: ResourceString,
        val headshotList: SizedImages,
        val seasonStatsHeader: List<ResourceString>,
        val seasonStatsValues: List<String>,
        val isProbable: Boolean = false
    ) : StartingPitchersUi(teamColor, details)

    data class TBDPitcher(
        override val teamColor: Color,
        override val details: ResourceString,
        val teamLogo: SizedImages
    ) : StartingPitchersUi(teamColor, details)
}

@Composable
fun StartingPitchers(
    @StringRes titleId: Int,
    awayTeamPitcher: StartingPitchersUi?,
    homeTeamPitcher: StartingPitchersUi?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AthTheme.colors.dark200)
    ) {

        BoxScoreHeaderTitle(titleId)
        awayTeamPitcher?.let { PlayerRow(awayTeamPitcher) }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = AthTheme.colors.dark300
        )

        homeTeamPitcher?.let { PlayerRow(homeTeamPitcher) }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun PlayerRow(pitcher: StartingPitchersUi) {
    when (pitcher) {
        is StartingPitchersUi.PitcherStats -> {
            PlayerStatsRow(
                details = pitcher.details,
                name = pitcher.name,
                headshotList = pitcher.headshotList,
                seasonStatsHeader = pitcher.seasonStatsHeader,
                seasonStatsValues = pitcher.seasonStatsValues,
                teamColor = pitcher.teamColor,
                isProbable = pitcher.isProbable
            )
        }
        is StartingPitchersUi.TBDPitcher -> TBDPitcherRow(
            pitcher.teamLogo,
            pitcher.teamColor,
            pitcher.details
        )
    }
}

@Composable
private fun TBDPitcherRow(
    teamLogo: SizedImages,
    teamColor: Color,
    details: ResourceString
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(teamColor, shape = CircleShape),
        ) {
            TeamLogo(
                teamUrls = teamLogo,
                preferredSize = 56.dp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(56.dp)
            )
        }
        Text(
            text = stringResource(id = R.string.global_tbd),
            style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge,
            color = AthTheme.colors.dark800,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 16.dp)
        )

        Text(
            text = details.asString(),
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark500,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
private fun PlayerStatsRow(
    name: ResourceString,
    headshotList: SizedImages,
    details: ResourceString,
    seasonStatsHeader: List<ResourceString>,
    seasonStatsValues: List<String>,
    teamColor: Color,
    isProbable: Boolean
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Headshot(
            headshotsUrls = headshotList,
            teamUrls = emptyList(),
            teamColor = teamColor,
            preferredSize = 56.dp,
            modifier = Modifier
                .size(56.dp)
        )

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = name.asString(),
                    style = AthTextStyle.Calibre.Utility.Medium.Large,
                    color = AthTheme.colors.dark800,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 12.dp)
                )

                Text(
                    text = details.asString(),
                    style = AthTextStyle.Calibre.Utility.Medium.Small,
                    color = AthTheme.colors.dark500,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 4.dp)
                )

                ProbablePitcher(showText = isProbable)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                seasonStatsHeader.zip(seasonStatsValues) { header, value ->
                    StatsCell(header.asString(), value)
                }
            }
        }
    }
}

@Composable
private fun ProbablePitcher(showText: Boolean) {
    if (showText) {
        Text(
            text = stringResource(id = R.string.box_score_baseball_probable_pitchers),
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            color = AthTheme.colors.red,
            modifier = Modifier.padding(start = 5.dp)
        )
    }
}

@Composable
private fun StatsCell(
    header: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = value,
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            color = AthTheme.colors.dark700,
            textAlign = TextAlign.Center
        )

        Text(
            text = header,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark500,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Preview(device = Devices.PIXEL)
@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun SeasonStats_Preview() {
    pitcherModuleMock.Render()
}

@Preview
@Composable
private fun SeasonStats_PreviewLight() {
    AthleticTheme(lightMode = true) {
        pitcherModuleMock.Render()
    }
}