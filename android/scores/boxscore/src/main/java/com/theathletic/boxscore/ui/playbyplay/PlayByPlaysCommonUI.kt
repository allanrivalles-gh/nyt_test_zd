package com.theathletic.boxscore.ui.playbyplay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.theathletic.data.SizedImages
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.widgets.TeamCurtain
import com.theathletic.ui.widgets.TeamLogo

@Composable
fun Play(
    teamLogos: SizedImages,
    teamColor: String?,
    title: String?,
    description: String,
    clock: String,
    awayTeamAlias: String? = null,
    homeTeamAlias: String? = null,
    awayTeamScore: String? = null,
    homeTeamScore: String? = null,
    showScores: Boolean = false,
    showTeamCurtain: Boolean = false,
    showDivider: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
    ) {
        if (showTeamCurtain) {
            TeamCurtain(teamColor, modifier = Modifier.align(Alignment.TopStart))
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamLogo(
                    teamUrls = teamLogos,
                    preferredSize = 24.dp,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Top)
                )
                Row {
                    PlayTitleAndDescription(
                        title = title,
                        description = description,
                        clock = clock,
                        modifier = Modifier.weight(1f)
                    )
                    if (showScores) {
                        TeamScores(
                            firstTeamAlias = awayTeamAlias.orEmpty(),
                            secondTeamAlias = homeTeamAlias.orEmpty(),
                            firstTeamScore = awayTeamScore.orEmpty(),
                            secondTeamScore = homeTeamScore.orEmpty(),
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .align(Alignment.Top)
                                .wrapContentWidth(Alignment.End)
                        )
                    }
                }
            }
        }
        if (showDivider) {
            PlayDivider(modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

@Composable
fun PlayTitleAndDescription(
    title: String?,
    description: String,
    clock: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(start = 12.dp)
    ) {
        if (title.isNullOrEmpty().not()) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.orEmpty(),
                    style = AthTextStyle.Calibre.Utility.Medium.Large,
                    color = AthTheme.colors.dark700
                )
                Text(
                    text = clock,
                    style = AthTextStyle.Calibre.Utility.Regular.Small,
                    color = AthTheme.colors.dark500,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
        Text(
            text = description,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark500
        )
    }
}

@Composable
fun TeamCurtain(
    teamColor: String?,
    modifier: Modifier = Modifier
) {
    teamColor?.let { color ->
        TeamCurtain(
            teamColor = color,
            height = 70.dp,
            width = 56.dp,
            orientation = TeamCurtain.Orientation.LEFT,
            modifier = modifier
        )
    }
}

@Composable
fun PlayDivider(modifier: Modifier = Modifier) {
    Divider(
        color = AthTheme.colors.dark300,
        thickness = 1.dp,
        modifier = modifier
    )
}

@Composable
internal fun StoppagePlay(
    title: String,
    description: String,
    showDivider: Boolean
) {
    Column {
        Column(
            modifier = Modifier
                .background(AthTheme.colors.dark200)
                .fillMaxWidth()
                .padding(top = 10.dp, start = 52.dp, end = 16.dp, bottom = 12.dp)
        ) {
            Text(
                text = title,
                style = AthTextStyle.Calibre.Utility.Medium.Large,
                color = AthTheme.colors.dark700,
            )
            Text(
                text = description,
                style = AthTextStyle.Calibre.Utility.Regular.Small,
                color = AthTheme.colors.dark500,
            )
        }
        if (showDivider) {
            Divider(
                color = AthTheme.colors.dark300,
                thickness = 1.dp
            )
        }
    }
}

@Composable
internal fun TimeoutPlay(
    title: String,
    showDivider: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
            .padding(
                top = 12.dp,
                start = 16.dp,
                end = 16.dp
            )
    ) {
        Text(
            text = title,
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            color = AthTheme.colors.dark700,
            modifier = Modifier.padding(start = 36.dp, bottom = 12.dp)
        )
        if (showDivider) {
            Divider(
                color = AthTheme.colors.dark300,
                thickness = 1.dp
            )
        }
    }
}

@Composable
internal fun TeamScores(
    firstTeamAlias: String,
    secondTeamAlias: String,
    firstTeamScore: String,
    secondTeamScore: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.requiredWidth(IntrinsicSize.Min),
    ) {
        TeamScore(
            alias = firstTeamAlias,
            score = firstTeamScore,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        TeamScore(
            alias = secondTeamAlias,
            score = secondTeamScore
        )
    }
}

@Composable
private fun TeamScore(
    alias: String,
    score: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = score,
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            color = AthTheme.colors.dark800,
            softWrap = false
        )
        Text(
            text = alias,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark500,
            softWrap = false
        )
    }
}