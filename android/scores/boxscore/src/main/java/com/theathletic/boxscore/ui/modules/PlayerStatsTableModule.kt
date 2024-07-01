package com.theathletic.boxscore.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.feed.ui.FeedModule
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asString

data class PlayerStatsTableModule(
    val id: String,
    val playerColumn: List<PlayerColumnItem>,
    val statisticColumns: List<List<StatisticColumnItem>>
) : FeedModule {

    interface PlayerColumnItem

    data class Category(
        val label: ResourceString
    ) : PlayerColumnItem

    data class Player(
        val playerName: String,
        val position: String
    ) : PlayerColumnItem

    interface StatisticColumnItem

    data class StatisticLabel(
        val label: String
    ) : StatisticColumnItem

    data class StatisticValue(
        val value: String
    ) : StatisticColumnItem

    @Composable
    override fun Render() {
        Column(modifier = Modifier.background(color = AthTheme.colors.dark200)) {
            Row {
                // Player fixed column
                PlayersColumn(playerColumn)
                StatisticsColumns(statisticColumns)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PlayersColumn(playerColumn: List<PlayerStatsTableModule.PlayerColumnItem>) {
    Column(
        modifier = Modifier
            .padding(start = 16.dp)
            .width(150.dp)
    ) {
        playerColumn.forEach { item ->
            when (item) {
                is PlayerStatsTableModule.Category -> item.ToCompose()
                is PlayerStatsTableModule.Player -> item.ToCompose()
            }
        }
    }
}

@Composable
private fun StatisticsColumns(statisticColumns: List<List<PlayerStatsTableModule.StatisticColumnItem>>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        statisticColumns.forEach { column ->
            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .defaultMinSize(minWidth = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                column.forEach { item ->
                    when (item) {
                        is PlayerStatsTableModule.StatisticLabel -> item.ToCompose()
                        is PlayerStatsTableModule.StatisticValue -> item.ToCompose()
                        else -> { /* Not Supported */
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerStatsTableModule.Category.ToCompose() {
    Column {
        Text(
            text = label.asString().uppercase(),
            style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall,
            color = AthTheme.colors.dark600,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        Divider(
            color = AthTheme.colors.dark300,
            thickness = 1.dp
        )
    }
}

@Composable
private fun PlayerStatsTableModule.Player.ToCompose() {
    Column {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
        ) {
            Text(
                text = playerName,
                style = AthTextStyle.Calibre.Utility.Medium.Small,
                color = AthTheme.colors.dark800,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.alignByBaseline()
            )
            Text(
                text = position,
                style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall,
                color = AthTheme.colors.dark500,
                maxLines = 1,
                modifier = Modifier
                    .alignByBaseline()
                    .padding(start = 2.dp, end = 6.dp)
            )
        }
        Divider(
            color = AthTheme.colors.dark300,
            thickness = 1.dp
        )
    }
}

@Composable
private fun PlayerStatsTableModule.StatisticLabel.ToCompose() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall,
            color = AthTheme.colors.dark600,
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 6.dp)
                .align(Alignment.CenterHorizontally)
        )
        Divider(
            color = AthTheme.colors.dark300,
            thickness = 1.dp
        )
    }
}

@Composable
private fun PlayerStatsTableModule.StatisticValue.ToCompose() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = value,
            style = AthTextStyle.Calibre.Utility.Medium.Small,
            color = AthTheme.colors.dark800,
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 6.dp)
                .align(Alignment.CenterHorizontally)
        )
        Divider(
            color = AthTheme.colors.dark300,
            thickness = 1.dp
        )
    }
}

@Preview
@Composable
fun PlayerStatsTableModulePreview() {
    PlayerStatsTableModule(
        id = "unique-id",
        playerColumn = PlayerStatsTableModulePreviewData.players,
        statisticColumns = PlayerStatsTableModulePreviewData.statistics
    ).Render()
}

@Preview
@Composable
fun PlayerStatsTableModulePreview_Light() {
    AthleticTheme(lightMode = true) {
        PlayerStatsTableModule(
            id = "unique-id",
            playerColumn = PlayerStatsTableModulePreviewData.players,
            statisticColumns = PlayerStatsTableModulePreviewData.statistics
        ).Render()
    }
}