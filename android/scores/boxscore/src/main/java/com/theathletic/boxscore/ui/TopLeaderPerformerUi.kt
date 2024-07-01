package com.theathletic.boxscore.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.data.SizedImages
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.ResourceString
import com.theathletic.ui.UiModel
import com.theathletic.ui.asString
import com.theathletic.ui.widgets.Headshot

data class TopLeaderPerformerUiModel(
    val id: String,
    val subtitle: String?,
    val includeDivider: Boolean,
    val playerStats: List<TopLeaderPerformerUi.Category>,
    @StringRes val titleResId: Int,
) : UiModel {
    override val stableId = "TopLeaderPerformer:$id"
}

sealed class TopLeaderPerformerUi {
    data class Category(
        val label: String,
        val players: List<Player>
    )

    data class Player(
        val name: String,
        val details: ResourceString,
        val headShotList: SizedImages,
        val teamColor: Color,
        val teamLogoList: SizedImages,
        val stats: List<PlayerStats>,
        val showDivider: Boolean
    )

    data class PlayerStats(
        val statLabel: String,
        val statValue: String
    )
}

@Composable
fun TopLeaderPerformer(
    playerStats: List<TopLeaderPerformerUi.Category>,
    @StringRes titleResId: Int,
    includeDivider: Boolean,
    includeTopDivider: Boolean = true,
    subtitle: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AthTheme.colors.dark200)
    ) {
        if (includeTopDivider) BoxScoreHeaderDivider()

        BoxScoreHeaderTitle(titleResId, subtitle)

        playerStats.forEach {
            StatTitle(title = it.label)
            it.players.forEach { playerData ->
                PlayerRow(
                    stats = playerData.stats,
                    showDivider = playerData.showDivider,
                    teamColor = playerData.teamColor,
                    headShotList = playerData.headShotList,
                    name = playerData.name,
                    details = playerData.details,
                    teamLogoList = playerData.teamLogoList
                )
            }
        }

        BoxScoreFooterDivider(includeBottomBar = includeDivider)
    }
}

@Composable
private fun PlayerRow(
    name: String,
    details: ResourceString,
    headShotList: SizedImages,
    teamColor: Color,
    teamLogoList: SizedImages,
    stats: List<TopLeaderPerformerUi.PlayerStats>,
    showDivider: Boolean
) {
    Column(
        modifier = Modifier
            .padding(top = 8.dp)
            .padding(horizontal = 16.dp)
    ) {

        Row(
            verticalAlignment = CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {

            Headshot(
                headshotsUrls = headShotList,
                teamUrls = teamLogoList,
                teamColor = teamColor,
                preferredSize = 40.dp,
                modifier = Modifier
                    .size(40.dp, 40.dp)
            )

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = name,
                    style = AthTextStyle.Calibre.Utility.Medium.Large,
                    color = AthTheme.colors.dark700,
                )

                Text(
                    text = details.asString(),
                    style = AthTextStyle.Calibre.Utility.Regular.Small,
                    color = AthTheme.colors.dark500,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Row(
                modifier = Modifier
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                stats.forEach {
                    Box(modifier = Modifier.weight(1f)) {
                        PlayerStatsColumn(
                            label = it.statLabel,
                            stat = it.statValue
                        )
                    }
                }
            }
        }

        if (showDivider) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth(),
                color = AthTheme.colors.dark300
            )
        }
    }
}

@Composable
fun StatTitle(title: String) {
    Text(
        text = title,
        color = AthTheme.colors.dark500,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                top = 8.dp
            ),
        style = AthTextStyle.Calibre.Utility.Medium.Large,
        textAlign = TextAlign.Start
    )
}

@Composable
private fun PlayerStatsColumn(label: String, stat: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stat,
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            textAlign = TextAlign.Center,
            color = AthTheme.colors.dark700,
            maxLines = 1,
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .fillMaxWidth()
        )

        Text(
            text = label,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark500,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .padding(top = 4.dp)
                .fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun TopLeader_Preview() {
    BoxScorePreviewData.teamLeadersData.let {
        TopLeaderPerformer(
            playerStats = it.playerStats,
            includeDivider = it.includeDivider,
            titleResId = it.titleResId,
            subtitle = it.subtitle
        )
    }
}

@Preview
@Composable
private fun TopPerformer_Preview() {
    BoxScorePreviewData.teamLeadersData.let {
        TopLeaderPerformer(
            playerStats = it.playerStats,
            includeDivider = it.includeDivider,
            titleResId = it.titleResId,
            subtitle = it.subtitle
        )
    }
}

@Preview(device = Devices.PIXEL)
@Composable
private fun TopLeader_PreviewSmallDevice() {
    BoxScorePreviewData.teamLeadersData.let {
        TopLeaderPerformer(
            playerStats = it.playerStats,
            includeDivider = it.includeDivider,
            titleResId = it.titleResId,
            subtitle = it.subtitle
        )
    }
}

@Preview
@Composable
private fun TopLeader_PreviewLight() {
    AthleticTheme(lightMode = true) {
        BoxScorePreviewData.teamLeadersData.let {
            TopLeaderPerformer(
                playerStats = it.playerStats,
                includeDivider = it.includeDivider,
                titleResId = it.titleResId,
                subtitle = it.subtitle
            )
        }
    }
}