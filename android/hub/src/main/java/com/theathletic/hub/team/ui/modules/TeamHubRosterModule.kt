package com.theathletic.hub.team.ui.modules

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.hub.ui.SortablePlayerValuesTable
import com.theathletic.hub.ui.SortablePlayerValuesTablePreviewData
import com.theathletic.hub.ui.SortablePlayerValuesTableUi
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R

data class TeamHubRosterTableModule(
    val id: String,
    @StringRes val headingResId: Int,
    val showHeading: Boolean,
    val playerTable: SortablePlayerValuesTableUi,
) : FeedModuleV2 {

    override val moduleId: String = "TeamHubRosterTableModule:$id-$headingResId"

    @Composable
    override fun Render() {
        Column(modifier = Modifier.background(color = AthTheme.colors.dark200)) {
            Spacer(modifier = Modifier.height(6.dp))
            if (showHeading) SortableTableHeading(headingResId = headingResId)
            SortablePlayerValuesTable(playerTable = playerTable)
        }
    }
}

data class TeamHubRosterTableSpacerModule(
    val id: String
) : FeedModuleV2 {

    override val moduleId: String = "TeamHubRosterTableSpacerModule:$id"

    @Composable
    override fun Render() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(color = AthTheme.colors.dark200)
        )
    }
}

@Composable
fun SortableTableHeading(headingResId: Int) {
    Text(
        text = stringResource(headingResId),
        color = AthTheme.colors.dark700,
        style = AthTextStyle.Slab.Bold.Small,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 10.dp),
    )
}

@Preview
@Composable
private fun TeamHubRosterTableModulePreview() {
    TeamHubRosterTableModule(
        id = "uniqueId",
        headingResId = R.string.team_hub_roster_category_label_offense,
        showHeading = true,
        playerTable = SortablePlayerValuesTableUi(
            playerColumn = SortablePlayerValuesTablePreviewData.playerColumn(),
            valueColumns = SortablePlayerValuesTablePreviewData.valueColumns(),
        )
    ).Render()
}