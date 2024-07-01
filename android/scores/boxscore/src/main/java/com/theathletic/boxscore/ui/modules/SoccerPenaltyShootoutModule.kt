package com.theathletic.boxscore.ui.modules

import androidx.compose.runtime.Composable
import com.theathletic.boxscore.ui.playbyplay.SoccerPenaltyShootout
import com.theathletic.boxscore.ui.playbyplay.SoccerPenaltyShootoutUI
import com.theathletic.data.SizedImages
import com.theathletic.feed.ui.FeedModule

data class SoccerPenaltyShootoutModule(
    val id: String,
    val firstTeamName: String,
    val firstTeamLogos: SizedImages,
    val secondTeamName: String,
    val secondTeamLogos: SizedImages,
    val penaltyShots: List<SoccerPenaltyShootoutUI.PenaltyShot>,
) : FeedModule {

    @Composable
    override fun Render() {
        SoccerPenaltyShootout(
            firstTeamName = firstTeamName,
            firstTeamLogos = firstTeamLogos,
            secondTeamName = secondTeamName,
            secondTeamLogos = secondTeamLogos,
            penaltyShots = penaltyShots
        )
    }
}