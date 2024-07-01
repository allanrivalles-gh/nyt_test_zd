package com.theathletic.boxscore.ui

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.data.SizedImages
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asString
import com.theathletic.ui.widgets.TeamLogo

data class BoxScoreSeasonStatsUiModel(
    val firstTeamLogos: SizedImages,
    val secondTeamLogos: SizedImages,
    val statsItems: List<BoxScoreSeasonStatsItem>,
    val headerSubtitle: String? = null
) {
    data class BoxScoreSeasonStatsItem(
        val firstTeamValue: String,
        val firstTeamRank: ResourceString,
        val showFirstTeamRank: Boolean,
        val secondTeamValue: String,
        val secondTeamRank: ResourceString,
        val showSecondTeamRank: Boolean,
        val statLabel: String,
        val isChildStat: Boolean
    )
}

@Composable
fun SeasonStats(
    firstTeamLogos: SizedImages,
    secondTeamLogos: SizedImages,
    stats: List<BoxScoreSeasonStatsUiModel.BoxScoreSeasonStatsItem>,
    headerSubtitle: String?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
    ) {
        BoxScoreHeaderTitle(
            R.string.box_scores_season_stats_title,
            subtitle = headerSubtitle
        )
        SeasonStatsHeader(firstTeamLogos, secondTeamLogos)
        stats.forEachIndexed { index, item ->
            SeasonStatsRow(
                label = item.statLabel,
                firstTeamValue = item.firstTeamValue,
                firstTeamRank = item.firstTeamRank,
                showFirstTeamRank = item.showFirstTeamRank,
                secondTeamValue = item.secondTeamValue,
                secondTeamRank = item.secondTeamRank,
                showSecondTeamRank = item.showSecondTeamRank,
                isChildStat = item.isChildStat,
                showDivider = index != stats.lastIndex
            )
        }
        BoxScoreFooterDivider(includeBottomBar = false)
    }
}

@Composable
private fun SeasonStatsHeader(
    firstTeamLogos: SizedImages,
    secondTeamLogos: SizedImages
) {
    SeasonStatsRowSlot(
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
        },
        true
    )
}

@Composable
private fun SeasonStatsRow(
    label: String,
    firstTeamValue: String,
    firstTeamRank: ResourceString,
    showFirstTeamRank: Boolean,
    secondTeamValue: String,
    secondTeamRank: ResourceString,
    showSecondTeamRank: Boolean,
    isChildStat: Boolean,
    showDivider: Boolean
) {
    SeasonStatsRowSlot(
        label = {
            SeasonStatLabel(
                label = label,
                isChildStat = isChildStat,
            )
        },
        teamOne = {
            SeasonStat(
                value = firstTeamValue,
                rank = firstTeamRank.asString(),
                showRank = showFirstTeamRank,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        },
        teamTwo = {
            SeasonStat(
                value = secondTeamValue,
                rank = secondTeamRank.asString(),
                showRank = showSecondTeamRank,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        },
        showDivider
    )
}

@Composable
fun SeasonStatLabel(
    label: String,
    isChildStat: Boolean,
) {
    Text(
        text = label,
        color = if (isChildStat) AthTheme.colors.dark500 else AthTheme.colors.dark700,
        style = AthTextStyle.Calibre.Utility.Regular.Large,
        modifier = Modifier.padding(start = if (isChildStat) 16.dp else 0.dp),
        maxLines = 2
    )
}

@Composable
fun SeasonStat(
    value: String,
    rank: String,
    showRank: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = value,
            color = AthTheme.colors.dark700,
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .alignByBaseline()
        )
        if (showRank) {
            Text(
                text = rank,
                color = AthTheme.colors.dark500,
                style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .alignByBaseline()
            )
        }
    }
}

@Composable
private fun SeasonStatsRowSlot(
    label: @Composable BoxScope.() -> Unit,
    teamOne: @Composable BoxScope.() -> Unit,
    teamTwo: @Composable BoxScope.() -> Unit,
    showDivider: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(modifier = Modifier.weight(2f)) { label() }
            Box(modifier = Modifier.weight(1f)) { teamOne() }
            Box(modifier = Modifier.weight(1f)) { teamTwo() }
        }

        if (showDivider) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                color = AthTheme.colors.dark300
            )
        }
    }
}

@Preview
@Composable
private fun SeasonStats_Preview() {
    SeasonStats(
        firstTeamLogos = BoxScorePreviewData.seasonStats.firstTeamLogos,
        secondTeamLogos = BoxScorePreviewData.seasonStats.secondTeamLogos,
        stats = BoxScorePreviewData.seasonStats.statsItems,
        headerSubtitle = BoxScorePreviewData.seasonStats.headerSubtitle,
    )
}

@Preview
@Composable
private fun SeasonStats_PreviewLight() {
    AthleticTheme(lightMode = true) {
        SeasonStats(
            firstTeamLogos = BoxScorePreviewData.seasonStats.firstTeamLogos,
            secondTeamLogos = BoxScorePreviewData.seasonStats.secondTeamLogos,
            stats = BoxScorePreviewData.seasonStats.statsItems,
            headerSubtitle = BoxScorePreviewData.seasonStats.headerSubtitle,
        )
    }
}