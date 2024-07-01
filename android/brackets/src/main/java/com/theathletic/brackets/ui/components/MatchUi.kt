package com.theathletic.brackets.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.bracket.BuildConfig
import com.theathletic.bracket.R
import com.theathletic.brackets.ui.BracketsPreviewData
import com.theathletic.brackets.ui.BracketsUi
import com.theathletic.brackets.ui.BracketsUi.Companion.CONNECTOR_WIDTH
import com.theathletic.data.SizedImages
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.utility.conditional
import com.theathletic.ui.widgets.TeamLogo

@Composable
fun MatchLayout(
    match: BracketsUi.Match,
    modifier: Modifier = Modifier,
    connectorColor: Color = BracketsUi.connectorColor,
    onMatchClicked: (BracketsUi.Match) -> Unit,
    onMatchReplayClicked: (String) -> Unit,
) {
    val parentPadding = 16.dp
    Box(
        modifier = modifier
            .fillMaxWidth()
            .conditional(match.showConnector) {
                drawWithCache {
                    onDrawBehind {
                        val paddingHeight = parentPadding.toPx()
                        drawLine(
                            connectorColor,
                            Offset(paddingHeight, (size.height / 2) + paddingHeight),
                            Offset(0.dp.toPx(), (size.height / 2) + paddingHeight),
                            CONNECTOR_WIDTH.toPx()
                        )
                    }
                }
            }
            .padding(parentPadding)
            .background(AthTheme.colors.dark200)
    ) {
        Column(
            modifier = Modifier
                .conditional(match.hasBoxScore) {
                    clickable { onMatchClicked(match) }
                }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HeaderRow(
                dateAndTimeText = match.dateAndTimeText,
                matchId = match.id,
                onMatchReplayClicked = onMatchReplayClicked
            )
            TeamRow(team = match.firstTeam)
            TeamRow(team = match.secondTeam)
        }
    }
}

@Composable
private fun HeaderRow(
    dateAndTimeText: String,
    matchId: String,
    onMatchReplayClicked: (String) -> Unit,
) {
    Row {
        Text(
            text = dateAndTimeText,
            style = AthTextStyle.Calibre.Utility.Regular.ExtraLarge.copy(color = AthTheme.colors.dark700),
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (BuildConfig.DEBUG) {
            Icon(
                modifier = Modifier
                    .size(16.dp)
                    .clickable(onClick = { onMatchReplayClicked(matchId) }),
                imageVector = Icons.Default.Refresh,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun TeamRow(team: BracketsUi.Team) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (team) {
            is BracketsUi.Team.PostGameTeam ->
                PostMatchInfo(
                    logos = team.logos,
                    name = team.name,
                    score = team.score,
                    isWinner = team.isWinner
                )
            is BracketsUi.Team.PreGameTeam ->
                PreMatchInfo(
                    logos = team.logos,
                    ranking = team.seed,
                    name = team.name,
                    record = team.record
                )
            else -> PlaceholderMatchInfo(name = team.name)
        }
    }
}

@Composable
private fun RowScope.PostMatchInfo(
    logos: SizedImages,
    name: String,
    score: String,
    isWinner: Boolean?
) {
    val textColor = if (isWinner == false) {
        AthTheme.colors.dark500
    } else {
        AthTheme.colors.dark700
    }

    TeamLogo(
        teamUrls = logos,
        preferredSize = 20.dp,
        modifier = Modifier.size(20.dp)
    )

    Spacer(modifier = Modifier.width(8.dp))

    Text(
        text = name,
        style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge.copy(color = textColor),
        modifier = Modifier.weight(1f)
    )

    Text(
        text = score,
        style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge.copy(color = textColor),
    )
}

@Composable
private fun RowScope.PreMatchInfo(
    logos: SizedImages,
    ranking: String,
    name: String,
    record: String
) {
    TeamLogo(
        teamUrls = logos,
        preferredSize = 20.dp,
        modifier = Modifier.size(20.dp)
    )

    if (ranking.isNotEmpty()) {
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = ranking,
            modifier = Modifier.defaultMinSize(minWidth = 12.dp),
            textAlign = TextAlign.Start,
            style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall.copy(color = AthTheme.colors.dark500),
        )
        Spacer(modifier = Modifier.width(6.dp))
    } else {
        Spacer(modifier = Modifier.width(8.dp))
    }

    Text(
        text = name,
        style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge.copy(color = AthTheme.colors.dark700),
        modifier = Modifier.weight(1f)
    )

    Text(
        text = record,
        style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall.copy(color = AthTheme.colors.dark500),
    )
}

@Composable
private fun RowScope.PlaceholderMatchInfo(name: String) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .padding(2.dp)
            .background(AthTheme.colors.dark400, CircleShape)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
        modifier = Modifier.weight(1f),
        text = name.ifEmpty { stringResource(id = R.string.global_tbd) }.uppercase(),
        style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge.copy(color = AthTheme.colors.dark500),
    )
}

@Composable
@Preview
fun PreMatchUiPreview() {
    AthleticTheme(lightMode = true) {
        MatchLayout(
            match = BracketsPreviewData.preGameMatch,
            onMatchClicked = {},
            onMatchReplayClicked = {}
        )
    }
}

@Composable
@Preview
fun PostMatchUiPreview() {
    AthleticTheme(lightMode = true) {
        MatchLayout(
            match = BracketsPreviewData.postGameMatch,
            onMatchClicked = {},
            onMatchReplayClicked = {}
        )
    }
}

@Composable
@Preview
fun PlaceholderMatchUiPreview() {
    AthleticTheme(lightMode = true) {
        MatchLayout(
            match = BracketsPreviewData.placeholderMatch,
            onMatchClicked = {},
            onMatchReplayClicked = {}
        )
    }
}