package com.theathletic.hub.ui

import androidx.compose.ui.graphics.Color
import com.theathletic.ui.R
import com.theathletic.ui.asResourceString

object SortablePlayerValuesTablePreviewData {

    fun playerColumn() = listOf(
        SortablePlayerValuesTableUi.PlayerColumnItem.HeaderCell(
            titleResId = R.string.team_hub_player_stats_column_title
        ),
        SortablePlayerValuesTableUi.PlayerColumnItem.PlayerCell(
            name = "T.Maxey",
            jerseyNumber = "#12".asResourceString(),
            headshots = emptyList(),
            teamLogos = emptyList(),
            teamColor = Color.Cyan
        ),
        SortablePlayerValuesTableUi.PlayerColumnItem.PlayerCell(
            name = "D.Sands",
            jerseyNumber = "#39".asResourceString(),
            headshots = emptyList(),
            teamLogos = emptyList(),
            teamColor = Color.Cyan,
            showHeadshot = false
        ),
        SortablePlayerValuesTableUi.PlayerColumnItem.PlayerCell(
            name = "J.Realmuto",
            jerseyNumber = "#10".asResourceString(),
            headshots = emptyList(),
            teamLogos = emptyList(),
            teamColor = Color.Cyan
        ),
    )

    fun valueColumns() = listOf(
        listOf(
            SortablePlayerValuesTableUi.ValueColumnItem.HeaderCell(
                id = SortablePlayerValuesTableUi.CellId("Col1", "Col1"),
                title = "Pos".asResourceString(),
                order = SortablePlayerValuesTableUi.ColumnOrder.None,
                highlighted = false,
            ),
            SortablePlayerValuesTableUi.ValueColumnItem.ValueCell(
                value = "C".asResourceString(),
                highlighted = false,
            ),
            SortablePlayerValuesTableUi.ValueColumnItem.ValueCell(
                value = "1B".asResourceString(),
                highlighted = false,
            ),
            SortablePlayerValuesTableUi.ValueColumnItem.ValueCell(
                value = "SP".asResourceString(),
                highlighted = false,
            ),
        ),
        listOf(
            SortablePlayerValuesTableUi.ValueColumnItem.HeaderCell(
                id = SortablePlayerValuesTableUi.CellId("Col2", "Col2"),
                title = "Ht".asResourceString(),
                order = SortablePlayerValuesTableUi.ColumnOrder.Descending,
                highlighted = true,
                enableReordering = true,
            ),
            SortablePlayerValuesTableUi.ValueColumnItem.ValueCell(
                value = "6-2".asResourceString(),
                highlighted = true,
            ),
            SortablePlayerValuesTableUi.ValueColumnItem.ValueCell(
                value = "5-10".asResourceString(),
                highlighted = true,
            ),
            SortablePlayerValuesTableUi.ValueColumnItem.ValueCell(
                value = "6-1".asResourceString(),
                highlighted = true,
            ),
        ),
        listOf(
            SortablePlayerValuesTableUi.ValueColumnItem.HeaderCell(
                id = SortablePlayerValuesTableUi.CellId("Col3", "Col3"),
                title = "Wt".asResourceString(),
                order = SortablePlayerValuesTableUi.ColumnOrder.Ascending,
                highlighted = false,
            ),
            SortablePlayerValuesTableUi.ValueColumnItem.ValueCell(
                value = "190 lbs".asResourceString(),
                highlighted = false,
            ),
            SortablePlayerValuesTableUi.ValueColumnItem.ValueCell(
                value = "170 lbs".asResourceString(),
                highlighted = false,
            ),
            SortablePlayerValuesTableUi.ValueColumnItem.ValueCell(
                value = "245 lbs".asResourceString(),
                highlighted = false,
            ),
        ),
        listOf(
            SortablePlayerValuesTableUi.ValueColumnItem.HeaderCell(
                id = SortablePlayerValuesTableUi.CellId("Col4", "Col4"),
                title = "Dob".asResourceString(),
                order = SortablePlayerValuesTableUi.ColumnOrder.None,
                highlighted = false,
            ),
            SortablePlayerValuesTableUi.ValueColumnItem.ValueCell(
                value = "18-03-1991".asResourceString(),
                highlighted = false,
            ),
            SortablePlayerValuesTableUi.ValueColumnItem.ValueCell(
                value = "26-05-1993".asResourceString(),
                highlighted = false,
            ),
            SortablePlayerValuesTableUi.ValueColumnItem.ValueCell(
                value = "23-12-1995".asResourceString(),
                highlighted = false,
            ),
        ),
    )
}