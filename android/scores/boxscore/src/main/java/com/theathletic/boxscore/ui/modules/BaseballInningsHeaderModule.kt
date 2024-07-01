package com.theathletic.boxscore.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.playbyplay.TeamScores
import com.theathletic.data.SizedImages
import com.theathletic.feed.ui.FeedModule
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asResourceString
import com.theathletic.ui.asString
import com.theathletic.ui.widgets.TeamLogo

data class BaseballInningsHeaderModule(
    val id: String,
    val title: ResourceString,
    val inningStats: ResourceString,
    val teamLogos: SizedImages,
    val awayTeamAlias: String,
    val homeTeamAlias: String,
    val awayTeamScore: String,
    val homeTeamScore: String
) : FeedModule {

    @Composable
    override fun Render() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AthTheme.colors.dark200)
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 6.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamLogo(
                teamUrls = teamLogos,
                preferredSize = 24.dp,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterVertically)
            )
            Row(horizontalArrangement = Arrangement.SpaceAround) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Text(
                        text = title.asString(),
                        style = AthTextStyle.Calibre.Utility.Medium.Large,
                        color = AthTheme.colors.dark800
                    )
                    Text(
                        text = inningStats.asString(),
                        style = AthTextStyle.Calibre.Utility.Regular.Small,
                        color = AthTheme.colors.dark500
                    )
                }
                TeamScores(
                    firstTeamAlias = awayTeamAlias,
                    secondTeamAlias = homeTeamAlias,
                    firstTeamScore = awayTeamScore,
                    secondTeamScore = homeTeamScore,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.End)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Preview
@Composable
fun BaseballInningsHeaderModulePreview() {
    BaseballInningsHeaderModule(
        id = "uniqueId",
        title = "Top 13".asResourceString(),
        inningStats = "1 Run, 2Hits".asResourceString(),
        teamLogos = emptyList(),
        awayTeamAlias = "NYM",
        homeTeamAlias = "PIT",
        awayTeamScore = "8",
        homeTeamScore = "7"
    ).Render()
}