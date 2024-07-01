package com.theathletic.boxscore.ui.playbyplay

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.data.SizedImages
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.ResourceIcon
import com.theathletic.ui.widgets.TeamLogo

@Composable
fun SoccerEventPlay(
    teamLogos: SizedImages,
    title: String,
    description: String,
    clock: String,
    @DrawableRes eventIcon: Int,
    showDivider: Boolean,
) {
    SoccerPlay(
        clock = clock,
        description = description,
        teamLogos = teamLogos,
        showDivider = showDivider,
        title = title,
        showDetails = true,
        details = {
            EventIcon(
                eventIcon = eventIcon
            )
        },
        teamColor = null
    )
}

@Composable
fun SoccerStandardPlay(
    teamLogos: SizedImages,
    title: String,
    description: String,
    clock: String,
    showDivider: Boolean,
) {
    SoccerPlay(
        clock = clock,
        description = description,
        teamLogos = teamLogos,
        teamColor = null,
        showDivider = showDivider,
        title = title,
        showDetails = false,
    )
}

@Composable
fun SoccerGoalPlay(
    teamLogos: SizedImages,
    teamColor: String?,
    title: String,
    description: String,
    clock: String,
    showDivider: Boolean,
    awayTeamAlias: String,
    homeTeamAlias: String,
    awayTeamScore: String,
    homeTeamScore: String,
    modifier: Modifier = Modifier
) {
    SoccerPlay(
        clock = clock,
        description = description,
        teamLogos = teamLogos,
        teamColor = teamColor,
        showDivider = showDivider,
        title = title,
        showDetails = true,
        details = {
            TeamScores(
                secondTeamScore = awayTeamScore,
                firstTeamScore = homeTeamScore,
                secondTeamAlias = awayTeamAlias,
                firstTeamAlias = homeTeamAlias,
                modifier = modifier
            )
        }
    )
}

@Composable
fun SoccerPlay(
    teamLogos: SizedImages,
    teamColor: String?,
    title: String,
    description: String,
    clock: String,
    showDetails: Boolean = false,
    details: @Composable BoxScope.() -> Unit = {},
    showDivider: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
    ) {
        TeamCurtain(
            teamColor = teamColor,
            modifier = Modifier.align(Alignment.TopStart)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                TeamLogo(teamLogos)
                Row {
                    PlayTitleAndDescription(
                        title = title,
                        description = description,
                        clock = clock,
                        modifier = Modifier.weight(1f)
                    )
                    if (showDetails) {
                        Box {
                            details()
                        }
                    }
                }
            }
            if (showDivider) PlayDivider()
        }
    }
}

@Composable
private fun RowScope.TeamLogo(teamLogos: SizedImages) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .align(Alignment.Top)
    ) {
        if (teamLogos.isNotEmpty()) {
            TeamLogo(
                teamUrls = teamLogos,
                preferredSize = 24.dp,
                modifier = Modifier
                    .size(24.dp)

            )
        }
    }
}

@Composable
private fun EventIcon(@DrawableRes eventIcon: Int) {
    ResourceIcon(
        resourceId = eventIcon,
        modifier = Modifier
            .padding(
                horizontal = 7.dp,
                vertical = 5.dp
            )
            .size(24.dp)
    )
}

@Composable
fun NoKeyMoments() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(color = AthTheme.colors.dark200),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.box_score_timeline_no_key_moments_title),
            style = AthTextStyle.Calibre.Headline.Medium.Small,
            color = AthTheme.colors.dark800,
        )

        Text(
            text = stringResource(id = R.string.box_score_timeline_no_key_moments_description),
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark500,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(224.dp)
                .padding(top = 4.dp)
        )
    }
}

@Preview
@Composable
private fun SeasonStats_Preview() {
    NoKeyMoments()
}

@Preview
@Composable
private fun SeasonStats_PreviewLight() {
    AthleticTheme(lightMode = true) {
        NoKeyMoments()
    }
}