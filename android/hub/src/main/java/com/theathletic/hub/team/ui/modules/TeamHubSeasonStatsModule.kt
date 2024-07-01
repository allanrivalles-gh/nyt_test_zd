package com.theathletic.hub.team.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.SeasonStatLabel
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R

data class TeamHubSeasonStatsModule(
    val id: String,
    val stats: List<SingleTeamStatsItem>
) : FeedModuleV2 {

    override val moduleId: String = "TeamHubSeasonStatsModule:$id"

    data class SingleTeamStatsItem(
        val value: String,
        val label: String,
        val isChildStat: Boolean
    )

    @Composable
    override fun Render() {
        TeamSeasonStats(stats = stats)
    }
}

@Composable
private fun TeamSeasonStats(
    stats: List<TeamHubSeasonStatsModule.SingleTeamStatsItem>
) {
    Spacer(modifier = Modifier.height(6.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
    ) {
        TeamStatsTableHeading(R.string.box_scores_season_stats_title)
        stats.forEachIndexed { index, stat ->
            TeamSeasonStatsRow(
                label = stat.label,
                value = stat.value,
                isChildStat = stat.isChildStat,
                showDivider = index != stats.lastIndex
            )
        }
    }
}

@Composable
private fun TeamSeasonStatsRow(
    label: String,
    value: String,
    isChildStat: Boolean,
    showDivider: Boolean,
) {
    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            SeasonStatLabel(
                label = label,
                isChildStat = isChildStat
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = value,
                    color = AthTheme.colors.dark700,
                    style = AthTextStyle.Calibre.Utility.Medium.Large,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.defaultMinSize(72.dp)
                        .align(Alignment.CenterEnd)
                )
            }
        }
        if (showDivider) {
            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = AthTheme.colors.dark300
            )
        }
    }
}

@Preview
@Composable
private fun TeamHubSeasonStatsModulePreview() {
    TeamHubSeasonStatsModule(
        id = "uniqueId",
        stats = TeamHubStatsPreviewData.createTeamSeasonStats()
    ).Render()
}