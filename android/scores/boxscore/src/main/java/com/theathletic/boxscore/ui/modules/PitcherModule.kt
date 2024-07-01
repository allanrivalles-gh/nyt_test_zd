package com.theathletic.boxscore.ui.modules

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import com.theathletic.boxscore.ui.StartingPitchers
import com.theathletic.boxscore.ui.StartingPitchersUi
import com.theathletic.feed.ui.FeedModuleV2

data class PitcherModule(
    val id: String,
    @StringRes val titleId: Int,
    val awayTeamPitcher: StartingPitchersUi?,
    val homeTeamPitcher: StartingPitchersUi?
) : FeedModuleV2 {

    override val moduleId: String = "PitcherModule:$id"

    @Composable
    override fun Render() {
        StartingPitchers(
            titleId = titleId,
            awayTeamPitcher = awayTeamPitcher,
            homeTeamPitcher = homeTeamPitcher
        )
    }
}