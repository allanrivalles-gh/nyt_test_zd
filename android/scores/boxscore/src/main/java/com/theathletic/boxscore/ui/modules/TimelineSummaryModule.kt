package com.theathletic.boxscore.ui.modules

import androidx.compose.runtime.Composable
import com.theathletic.boxscore.ui.TimelineSummary
import com.theathletic.boxscore.ui.TimelineSummaryModel
import com.theathletic.feed.ui.FeedModuleV2

data class TimelineSummaryModule(
    val id: String,
    val expectedGoals: TimelineSummaryModel.ExpectedGoals,
    val timelineSummary: List<TimelineSummaryModel.SummaryItem>,
) : FeedModuleV2 {

    override val moduleId: String = "TimelineSummaryModule:$id"

    @Composable
    override fun Render() {
        TimelineSummary(
            expectedGoals = expectedGoals,
            timelineSummaryItems = timelineSummary
        )
    }
}