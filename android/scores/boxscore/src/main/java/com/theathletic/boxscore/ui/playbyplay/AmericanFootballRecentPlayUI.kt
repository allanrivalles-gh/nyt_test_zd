package com.theathletic.boxscore.ui.playbyplay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import com.theathletic.data.SizedImages
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asResourceString
import com.theathletic.ui.widgets.TeamLogo

@Composable
internal fun AmericanFootballRecentPlay(
    title: String,
    description: String?,
    possession: ResourceString?,
    showDivider: Boolean,
    teamLogos: SizedImages,
    teamColor: String?,
    awayTeamAlias: String?,
    homeTeamAlias: String?,
    awayTeamScore: String?,
    homeTeamScore: String?,
    clock: String,
    isScoringPlay: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
    ) {
        if (isScoringPlay) TeamCurtain(teamColor, modifier = Modifier.align(Alignment.TopStart))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Column {
                    if (teamLogos.isNotEmpty()) {
                        TeamLogo(
                            teamUrls = teamLogos,
                            preferredSize = 23.dp,
                            modifier = Modifier.size(23.dp)
                        )
                    }

                    Text(
                        text = clock,
                        style = AthTextStyle.Calibre.Utility.Regular.Small,
                        color = AthTheme.colors.dark500,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                AmericanFootballPlayContent(
                    description, title, possession,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                )
                if (isScoringPlay) {
                    TeamScores(
                        firstTeamAlias = awayTeamAlias.orEmpty(),
                        secondTeamAlias = homeTeamAlias.orEmpty(),
                        firstTeamScore = awayTeamScore.orEmpty(),
                        secondTeamScore = homeTeamScore.orEmpty(),
                        modifier = Modifier
                            .align(Alignment.Top)
                            .wrapContentWidth(Alignment.End)
                            .weight(0.3f)
                    )
                }
            }
            if (showDivider) PlayDivider(modifier = Modifier.padding(top = 6.dp))
        }
    }
}

@Preview
@Composable
fun AmericanFootballRecentPlayPreview() {
    AmericanFootballRecentPlay(
        title = "Extra Point",
        description = "Jake Elliott makes the extra point.",
        possession = "1st & 10 at IND 46".asResourceString(),
        showDivider = true,
        teamLogos = emptyList(),
        teamColor = "6DC1FF",
        "PHL",
        "IND",
        "23",
        "46",
        isScoringPlay = true,
        clock = "12:23",
    )
}

@Preview
@Composable
fun AmericanFootballRecentNonScoringPlayPreview() {
    AmericanFootballRecentPlay(
        title = "Incomplete Pass",
        description = "(Shotgun) J. Brissett pass incomplete deep right to R.Grant (M. Jenkins).",
        possession = "1st & 10 at IND 46".asResourceString(),
        showDivider = true,
        emptyList(),
        teamColor = null,
        null,
        null,
        null,
        null,
        isScoringPlay = false,
        clock = "16:28"
    )
}