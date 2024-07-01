package com.theathletic.boxscore.ui.modules

import androidx.compose.runtime.Composable
import com.theathletic.boxscore.ui.GameOdds
import com.theathletic.boxscore.ui.GameOddsUi
import com.theathletic.feed.ui.FeedModuleV2

data class GameOddsModule(
    val id: String,
    val firstTeamOdds: GameOddsUi.TeamOdds,
    val secondTeamOdds: GameOddsUi.TeamOdds,
) : FeedModuleV2 {
    override val moduleId: String = "GameOdds:$id"

    @Composable
    override fun Render() {
        GameOdds(
            firstTeamOdds = firstTeamOdds,
            secondTeamOdds = secondTeamOdds,
        )
    }
}