package com.theathletic.boxscore.ui.modules

import androidx.compose.runtime.Composable
import com.theathletic.boxscore.ui.BoxScoresScoreTableUiModel
import com.theathletic.boxscore.ui.ScoreTable
import com.theathletic.data.SizedImages
import com.theathletic.feed.ui.FeedModuleV2

data class ScoreTableModule(
    val id: String,
    val firstTeamName: String,
    val secondTeamName: String,
    val firstTeamLogoUrlList: SizedImages,
    val secondTeamLogoUrlList: SizedImages,
    val columns: List<BoxScoresScoreTableUiModel.ScoreTableColumn>,
    val totalsColumns: List<BoxScoresScoreTableUiModel.ScoreTableColumn>,
    val currentPeriodColumnIndex: Int,
    val scrollToInningIndex: Int = 0
) : FeedModuleV2 {

    override val moduleId: String = "ScoreTableModule:$id"

    @Composable
    override fun Render() {
        ScoreTable(
            firstTeamName = firstTeamName,
            secondTeamName = secondTeamName,
            firstTeamLogoUrlList = firstTeamLogoUrlList,
            secondTeamLogoUrlList = secondTeamLogoUrlList,
            columns = columns,
            totalsColumns = totalsColumns,
            currentPeriodColumnIndex = currentPeriodColumnIndex,
            showFooterDivider = false, // todo: Remove this as not needed when 100% FeedModule
            scrollToInningIndex = scrollToInningIndex
        )
    }
}