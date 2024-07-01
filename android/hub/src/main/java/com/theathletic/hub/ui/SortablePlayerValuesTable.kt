package com.theathletic.hub.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.data.SizedImages
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asString
import com.theathletic.ui.widgets.Headshot
import com.theathletic.ui.widgets.ResourceIcon

data class SortablePlayerValuesTableUi(
    val playerColumn: List<PlayerColumnItem>,
    val valueColumns: List<List<ValueColumnItem>>,
) {
    sealed class PlayerColumnItem {
        data class HeaderCell(
            @StringRes val titleResId: Int
        ) : PlayerColumnItem()

        data class PlayerCell(
            val name: String,
            val jerseyNumber: ResourceString,
            val headshots: SizedImages,
            val teamLogos: SizedImages,
            val teamColor: Color,
            val showHeadshot: Boolean = true,
        ) : PlayerColumnItem()
    }

    sealed class ValueColumnItem {
        data class HeaderCell(
            val id: CellId,
            val title: ResourceString,
            val order: ColumnOrder = ColumnOrder.None,
            val highlighted: Boolean,
            val enableReordering: Boolean = true,
        ) : ValueColumnItem()

        data class ValueCell(
            val value: ResourceString,
            val highlighted: Boolean,
        ) : ValueColumnItem()
    }

    data class CellId(
        val category: String,
        val type: String
    )

    enum class ColumnOrder {
        None,
        Descending,
        Ascending
    }

    sealed class Interaction : FeedInteraction {
        data class OnColumnSortClick(val id: CellId, val order: ColumnOrder) : Interaction()
    }
}

private val HeaderHeight = 32.dp
private val PlayerValuesHeight = 48.dp

@Composable
fun SortablePlayerValuesTable(
    playerTable: SortablePlayerValuesTableUi
) {
    Column(modifier = Modifier.background(color = AthTheme.colors.dark200)) {
        Row {
            PlayersColumn(playerTable.playerColumn)
            StatisticsColumns(playerTable.valueColumns)
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun PlayersColumn(playerColumn: List<SortablePlayerValuesTableUi.PlayerColumnItem>) {
    Column(modifier = Modifier.width(170.dp)) {
        playerColumn.forEach { item ->
            when (item) {
                is SortablePlayerValuesTableUi.PlayerColumnItem.HeaderCell -> item.Render()
                is SortablePlayerValuesTableUi.PlayerColumnItem.PlayerCell -> item.Render()
            }
        }
    }
}

@Composable
private fun StatisticsColumns(valueColumns: List<List<SortablePlayerValuesTableUi.ValueColumnItem>>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        valueColumns.forEach { column ->
            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .defaultMinSize(minWidth = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                column.forEach { item ->
                    when (item) {
                        is SortablePlayerValuesTableUi.ValueColumnItem.HeaderCell -> item.Render()
                        is SortablePlayerValuesTableUi.ValueColumnItem.ValueCell -> item.Render()
                    }
                }
            }
        }
    }
}

@Composable
private fun SortablePlayerValuesTableUi.PlayerColumnItem.HeaderCell.Render() {
    Box(modifier = Modifier.height(HeaderHeight)) {
        Text(
            text = stringResource(id = titleResId).uppercase(),
            style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
            color = AthTheme.colors.dark500,
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.CenterStart)
        )
        Divider(
            modifier = Modifier.align(Alignment.BottomCenter),
            color = AthTheme.colors.dark300,
            thickness = 1.dp,
        )
    }
}

@Composable
private fun SortablePlayerValuesTableUi.PlayerColumnItem.PlayerCell.Render() {
    Column {
        Row(
            modifier = Modifier
                .padding(start = 16.dp)
                .height(PlayerValuesHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showHeadshot) {
                Headshot(
                    headshotsUrls = headshots,
                    teamUrls = teamLogos,
                    teamColor = teamColor,
                    preferredSize = 32.dp,
                    modifier = Modifier
                        .size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Column {
                Text(
                    text = name,
                    style = AthTextStyle.Calibre.Utility.Regular.Small,
                    color = AthTheme.colors.dark800,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = jerseyNumber.asString(),
                    style = AthTextStyle.Calibre.Utility.Regular.Small,
                    color = AthTheme.colors.dark500
                )
            }
        }
        Divider(
            color = AthTheme.colors.dark300,
            thickness = 1.dp
        )
    }
}

@Composable
private fun SortablePlayerValuesTableUi.ValueColumnItem.HeaderCell.Render() {
    val interactor = LocalFeedInteractor.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(highlighted.toBackgroundColor())
            .height(HeaderHeight)
            .clickable(enabled = enableReordering) {
                interactor.send(
                    SortablePlayerValuesTableUi.Interaction.OnColumnSortClick(id, order)
                )
            }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title.asString().uppercase(),
                style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                color = AthTheme.colors.dark500,
            )
            if (enableReordering) SortIndicators(order = order)
        }
        Divider(
            modifier = Modifier.align(Alignment.BottomCenter),
            color = highlighted.toDividerColor(),
            thickness = 1.dp,
        )
    }
}

@Composable
private fun SortablePlayerValuesTableUi.ValueColumnItem.ValueCell.Render() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(highlighted.toBackgroundColor()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .height(PlayerValuesHeight)
                .padding(horizontal = 6.dp)
        ) {
            Text(
                text = value.asString(),
                style = AthTextStyle.Calibre.Utility.Regular.Small,
                color = AthTheme.colors.dark800,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Divider(
            color = highlighted.toDividerColor(),
            thickness = 1.dp,
        )
    }
}

@Composable
private fun Boolean.toBackgroundColor() =
    if (this) AthTheme.colors.dark300 else AthTheme.colors.dark200

@Composable
private fun Boolean.toDividerColor() =
    if (this) AthTheme.colors.dark300 else AthTheme.colors.dark300

@Composable
private fun SortIndicators(order: SortablePlayerValuesTableUi.ColumnOrder) {
    Column(
        modifier = Modifier.width(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ResourceIcon(
            resourceId = R.drawable.ic_sort_ascending_indicator,
            tint = when (order) {
                SortablePlayerValuesTableUi.ColumnOrder.Ascending -> AthTheme.colors.dark800
                else -> AthTheme.colors.dark400
            },
        )
        Spacer(modifier = Modifier.height(1.dp))
        ResourceIcon(
            resourceId = R.drawable.ic_sort_descending_indicator,
            tint = when (order) {
                SortablePlayerValuesTableUi.ColumnOrder.Descending -> AthTheme.colors.dark800
                else -> AthTheme.colors.dark400
            },
        )
    }
}

@Preview
@Composable
private fun SortablePlayerValuesTablePreview() {
    SortablePlayerValuesTable(
        SortablePlayerValuesTableUi(
            playerColumn = SortablePlayerValuesTablePreviewData.playerColumn(),
            valueColumns = SortablePlayerValuesTablePreviewData.valueColumns(),
        )
    )
}

@Preview
@Composable
private fun SortablePlayerValuesTablePreview_Light() {
    AthleticTheme(lightMode = true) {
        SortablePlayerValuesTable(
            SortablePlayerValuesTableUi(
                playerColumn = SortablePlayerValuesTablePreviewData.playerColumn(),
                valueColumns = SortablePlayerValuesTablePreviewData.valueColumns(),
            )
        )
    }
}