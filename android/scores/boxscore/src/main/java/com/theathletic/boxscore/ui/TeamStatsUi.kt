package com.theathletic.boxscore.ui

import androidx.annotation.ColorRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.modules.TeamStatsModule
import com.theathletic.data.SizedImages
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.UiModel
import com.theathletic.ui.widgets.TeamLogo

data class BoxScoreTeamStatsUiModel(
    val id: String,
    val firstTeamLogoUrlList: SizedImages,
    val secondTeamLogoUrlList: SizedImages,
    val statsItems: List<BoxScoreTeamStatsItem>
) : UiModel {
    override val stableId = "BoxScoreTeamStats:$id"

    data class BoxScoreTeamStatsItem(
        val firstTeamValue: String,
        @ColorRes val firstTeamValueColor: Int,
        val secondTeamValue: String,
        @ColorRes val secondTeamValueColor: Int,
        val statLabel: String,
        val isChildStat: Boolean
    )
}

@Composable
fun TeamStats(teamStats: BoxScoreTeamStatsUiModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
    ) {
        BoxScoreHeaderDivider()
        BoxScoreHeaderTitle(R.string.box_scores_team_stats_title)
        TeamStatsHeader(
            firstTeamLogos = teamStats.firstTeamLogoUrlList,
            secondTeamLogos = teamStats.secondTeamLogoUrlList
        )
        teamStats.statsItems.forEach {
            TeamStatsRow(
                label = it.statLabel,
                firstTeamValue = it.firstTeamValue,
                firstTeamValueColor = it.firstTeamValueColor,
                secondTeamValue = it.secondTeamValue,
                secondTeamValueColor = it.secondTeamValueColor,
                isChildStat = it.isChildStat
            )
        }
        BoxScoreFooterDivider(includeBottomBar = false)
    }
}

@Composable
fun TeamStats(
    firstTeamLogos: SizedImages,
    secondTeamLogos: SizedImages,
    stats: List<TeamStatsModule.TeamStatsItem>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
    ) {
        BoxScoreHeaderTitle(R.string.box_scores_team_stats_title)
        TeamStatsHeader(firstTeamLogos, secondTeamLogos)
        stats.forEach {
            TeamStatsRow(
                label = it.label,
                firstTeamValue = it.firstTeamValue,
                firstTeamValueColor = it.firstTeamValueColor,
                secondTeamValue = it.secondTeamValue,
                secondTeamValueColor = it.secondTeamValueColor,
                isChildStat = it.isChildStat
            )
        }
        BoxScoreFooterDivider(includeBottomBar = false)
    }
}

@Composable
private fun TeamStatsRowSlot(
    label: @Composable BoxScope.() -> Unit,
    teamOne: @Composable BoxScope.() -> Unit,
    teamTwo: @Composable BoxScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = AthTheme.colors.dark300
        )

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(modifier = Modifier.weight(1.5f)) { label() }
            Box(modifier = Modifier.weight(1f)) { teamOne() }
            Box(modifier = Modifier.weight(1f)) { teamTwo() }
        }
    }
}

@Composable
private fun TeamStatsHeader(
    firstTeamLogos: SizedImages,
    secondTeamLogos: SizedImages
) {
    TeamStatsRowSlot(
        label = {
            Text(
                text = stringResource(id = R.string.box_scores_team_stats_type_heading),
                color = AthTheme.colors.dark700,
                style = AthTextStyle.Calibre.Utility.Regular.Large,
            )
        },
        teamOne = {
            TeamLogo(
                teamUrls = firstTeamLogos,
                preferredSize = 30.dp,
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.Center)
            )
        },
        teamTwo = {
            TeamLogo(
                teamUrls = secondTeamLogos,
                preferredSize = 30.dp,
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.Center)
            )
        }
    )
}

@Composable
private fun TeamStatsRow(
    label: String,
    firstTeamValue: String,
    @ColorRes firstTeamValueColor: Int,
    secondTeamValue: String,
    @ColorRes secondTeamValueColor: Int,
    isChildStat: Boolean
) {
    TeamStatsRowSlot(
        label = {
            Text(
                text = label,
                color = if (isChildStat) AthTheme.colors.dark500 else AthTheme.colors.dark700,
                style = AthTextStyle.Calibre.Utility.Regular.Large,
                modifier = Modifier.padding(start = if (isChildStat) 16.dp else 0.dp),
                maxLines = 2
            )
        },
        teamOne = {
            Text(
                text = firstTeamValue,
                color = colorResource(id = firstTeamValueColor),
                style = AthTextStyle.Calibre.Utility.Regular.Large,
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center
            )
        },
        teamTwo = {
            Text(
                text = secondTeamValue,
                color = colorResource(id = secondTeamValueColor),
                style = AthTextStyle.Calibre.Utility.Regular.Large,
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center
            )
        }
    )
}

@Preview
@Composable
private fun TeamStatsHeader_Preview() {
    TeamStatsHeader(
        firstTeamLogos = BoxScorePreviewData.TeamStats.firstTeamLogoUrlList,
        secondTeamLogos = BoxScorePreviewData.TeamStats.secondTeamLogoUrlList
    )
}

@Preview
@Composable
private fun TeamStatsRow_Preview() {
    BoxScorePreviewData.TeamStats.statsItems.first().apply {
        TeamStatsRow(
            label = statLabel,
            firstTeamValue = firstTeamValue,
            firstTeamValueColor = firstTeamValueColor,
            secondTeamValue = secondTeamValue,
            secondTeamValueColor = secondTeamValueColor,
            isChildStat = isChildStat
        )
    }
}

@Preview
@Composable
private fun TeamStats_Preview() {
    TeamStats(BoxScorePreviewData.TeamStats)
}

@Preview
@Composable
private fun TeamStats_PreviewLight() {
    AthleticTheme(lightMode = true) {
        TeamStats(BoxScorePreviewData.TeamStats)
    }
}