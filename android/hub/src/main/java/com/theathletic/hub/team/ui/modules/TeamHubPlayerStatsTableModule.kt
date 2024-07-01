package com.theathletic.hub.team.ui.modules

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.hub.ui.SortablePlayerValuesTable
import com.theathletic.hub.ui.SortablePlayerValuesTableUi
import com.theathletic.themes.AthTheme

data class TeamHubPlayerStatsTableModule(
    val id: String,
    @StringRes val headingResId: Int,
    val showHeading: Boolean,
    val statsTable: SortablePlayerValuesTableUi
) : FeedModuleV2 {

    override val moduleId: String = "TeamHubPlayerStatsTableModule:$id-$headingResId"

    @Composable
    override fun Render() {
        Column(modifier = Modifier.background(color = AthTheme.colors.dark200)) {
            Spacer(modifier = Modifier.height(6.dp))
            if (showHeading) SortableTableHeading(headingResId = headingResId)
            SortablePlayerValuesTable(playerTable = statsTable)
        }
    }
}