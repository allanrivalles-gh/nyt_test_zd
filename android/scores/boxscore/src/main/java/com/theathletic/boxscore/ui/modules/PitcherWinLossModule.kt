package com.theathletic.boxscore.ui.modules

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.theathletic.boxscore.ui.PitcherWinLoss
import com.theathletic.data.SizedImages
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.ui.ResourceString

data class PitcherWinLossModule(
    val id: String,
    val pitchers: List<Pitcher>
) : FeedModuleV2 {

    override val moduleId: String = "PitcherWinLossModule:$id"

    data class Pitcher(
        val title: ResourceString,
        val headshot: SizedImages,
        val name: String,
        val stats: String,
        val teamColor: Color
    )

    @Composable
    override fun Render() {
        PitcherWinLoss(pitchers = pitchers)
    }
}