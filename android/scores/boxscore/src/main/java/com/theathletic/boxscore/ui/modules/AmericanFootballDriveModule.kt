package com.theathletic.boxscore.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.playbyplay.TeamScores
import com.theathletic.data.SizedImages
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedModule
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asResourceString
import com.theathletic.ui.asString
import com.theathletic.ui.widgets.TeamLogo

data class AmericanFootballDriveModule(
    val id: String,
    val teamLogos: SizedImages,
    val title: String,
    val stats: ResourceString,
    val awayTeamAlias: String,
    val homeTeamAlias: String,
    val awayTeamScore: String,
    val homeTeamScore: String,
    val isExpanded: Boolean = false
) : FeedModule {

    @Composable
    override fun Render() {
        val interactor = LocalFeedInteractor.current

        DrivePlay(
            teamLogos = teamLogos,
            playTitle = title,
            playStats = stats,
            awayTeamAlias = awayTeamAlias,
            homeTeamAlias = homeTeamAlias,
            awayTeamScore = awayTeamScore,
            homeTeamScore = homeTeamScore,
            isExpanded = isExpanded,
            showDivider = !isExpanded,
            modifier = Modifier.clickable {
                interactor.send(Interaction.OnDriveExpandClick(id))
            }
        )
    }

    interface Interaction {
        data class OnDriveExpandClick(
            val id: String
        ) : FeedInteraction
    }
}

@Composable
private fun DrivePlay(
    modifier: Modifier = Modifier,
    teamLogos: SizedImages,
    playTitle: String,
    playStats: ResourceString,
    awayTeamAlias: String,
    homeTeamAlias: String,
    awayTeamScore: String,
    homeTeamScore: String,
    isExpanded: Boolean = false,
    showDivider: Boolean = true,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
            .padding(
                top = 12.dp,
                start = 16.dp,
                end = 16.dp
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 2.dp,
                    bottom = 12.dp
                )
        ) {
            ExpandedIcon(isExpanded)
            TeamLogo(
                teamUrls = teamLogos,
                preferredSize = 24.dp,
                modifier = Modifier
                    .size(24.dp)
                    .padding(start = 1.dp)
                    .align(Alignment.CenterVertically)
            )
            Row {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    TeamDetails(playTitle, playStats)
                }
                TeamScores(
                    firstTeamAlias = awayTeamAlias,
                    secondTeamAlias = homeTeamAlias,
                    firstTeamScore = awayTeamScore,
                    secondTeamScore = homeTeamScore,
                    modifier = Modifier
                        .align(Alignment.Top)
                        .wrapContentWidth(Alignment.End)
                )
            }
        }
        ShowDivider(showDivider)
    }
}

@Composable
private fun RowScope.ExpandedIcon(isExpanded: Boolean) {
    Icon(
        if (isExpanded) {
            Icons.Default.ExpandLess
        } else {
            Icons.Default.ExpandMore
        },
        contentDescription = null,
        tint = AthTheme.colors.dark800,
        modifier = Modifier.Companion
            .align(Alignment.CenterVertically)
            .padding(end = 16.dp)
    )
}

@Composable
private fun TeamDetails(playTitle: String, playStats: ResourceString) {
    Text(
        text = playTitle,
        style = AthTextStyle.Calibre.Utility.Medium.Large,
        color = AthTheme.colors.dark700
    )
    Text(
        text = playStats.asString(),
        style = AthTextStyle.Calibre.Utility.Regular.Small,
        color = AthTheme.colors.dark500,
    )
}

@Composable
private fun ShowDivider(showDivider: Boolean) {
    if (showDivider) {
        Divider(
            color = AthTheme.colors.dark300,
            thickness = 1.dp
        )
    }
}

@Preview
@Composable
fun AmericanFootballDriveModulePreview() {
    AmericanFootballDriveModule(
        id = "uniqueId",
        teamLogos = emptyList(),
        title = "Touchdown",
        stats = "17 Plays, 75 Yards, 11:18".asResourceString(),
        awayTeamAlias = "PHL",
        homeTeamAlias = "IND",
        awayTeamScore = "20",
        homeTeamScore = "16",
        isExpanded = false
    ).Render()
}

@Preview
@Composable
fun AmericanFootballDriveModulePreview_Light() {
    AthleticTheme(lightMode = true) {
        AmericanFootballDriveModule(
            id = "uniqueId",
            teamLogos = emptyList(),
            title = "Touchdown",
            stats = "17 Plays, 75 Yards, 11:18".asResourceString(),
            awayTeamAlias = "PHL",
            homeTeamAlias = "IND",
            awayTeamScore = "20",
            homeTeamScore = "16",
            isExpanded = true
        ).Render()
    }
}