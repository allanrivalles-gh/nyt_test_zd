package com.theathletic.boxscore.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedModule
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asResourceString
import com.theathletic.ui.asString

data class PlaysPeriodHeaderModule(
    val id: String,
    val expanded: Boolean,
    val title: ResourceString,
    val periodData: ResourceString,
    val firstTeamAlias: String,
    val secondTeamAlias: String,
    val firstTeamScore: String,
    val secondTeamScore: String,
    val showSubtitle: Boolean = true,
) : FeedModule {

    @Composable
    override fun Render() {
        val interactor = LocalFeedInteractor.current

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AthTheme.colors.dark200)
                .clickable {
                    interactor.send(Interaction.OnPeriodExpandClick(id))
                }
                .padding(
                    top = 22.dp,
                    start = 16.dp,
                    end = 16.dp
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (expanded) {
                        Icons.Default.ExpandLess
                    } else {
                        Icons.Default.ExpandMore
                    },
                    contentDescription = null,
                    tint = AthTheme.colors.dark800
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PeriodLabel(
                        title = title.asString(),
                        subtitle = periodData,
                        showSubtitle = showSubtitle
                    )
                    TeamScores(
                        firstTeamAlias = firstTeamAlias,
                        secondTeamAlias = secondTeamAlias,
                        firstTeamScore = firstTeamScore,
                        secondTeamScore = secondTeamScore,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.End)
                            .align(Alignment.CenterVertically)
                    )
                }
            }
            Divider(
                color = AthTheme.colors.dark300,
                thickness = 1.dp
            )
        }
    }

    interface Interaction {
        data class OnPeriodExpandClick(
            val id: String
        ) : FeedInteraction
    }
}

@Composable
private fun PeriodLabel(
    title: String,
    subtitle: ResourceString,
    showSubtitle: Boolean,
) {
    Column(
        modifier = Modifier.padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            style = AthTextStyle.Slab.Bold.Small,
            color = AthTheme.colors.dark800
        )
        if (showSubtitle) {
            Text(
                text = subtitle.asString(),
                style = AthTextStyle.Calibre.Utility.Regular.Small,
                color = AthTheme.colors.dark500
            )
        }
    }
}

@Preview
@Composable
fun PlaysPeriodHeaderPreview() {
    PlaysPeriodHeaderModule(
        id = "uniqueId",
        expanded = false,
        title = "1st Quarter".asResourceString(),
        periodData = "SAS: 26 LAL: 24".asResourceString(),
        firstTeamAlias = "SAS",
        secondTeamAlias = "LAL",
        firstTeamScore = "37",
        secondTeamScore = "31"
    ).Render()
}

@Preview
@Composable
fun PlaysPeriodHeaderPreview_Light() {
    AthleticTheme(lightMode = true) {
        PlaysPeriodHeaderModule(
            id = "uniqueId",
            expanded = true,
            title = "1st Quarter".asResourceString(),
            periodData = "SAS: 26 LAL: 24".asResourceString(),
            firstTeamAlias = "SAS",
            secondTeamAlias = "LAL",
            firstTeamScore = "37",
            secondTeamScore = "31"
        ).Render()
    }
}

@Preview
@Composable
fun PlaysPeriodHeaderNoSubtitlePreview_Light() {
    AthleticTheme(lightMode = true) {
        PlaysPeriodHeaderModule(
            id = "uniqueId",
            expanded = true,
            title = "1st Quarter".asResourceString(),
            periodData = "SAS: 26 LAL: 24".asResourceString(),
            firstTeamAlias = "SAS",
            secondTeamAlias = "LAL",
            firstTeamScore = "37",
            secondTeamScore = "31",
            showSubtitle = false
        ).Render()
    }
}