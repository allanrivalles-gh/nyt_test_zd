package com.theathletic.boxscore.ui.modules

import androidx.compose.runtime.Composable
import com.theathletic.boxscore.ui.CurrentInning
import com.theathletic.boxscore.ui.CurrentInningUi
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.LocalFeedInteractor

data class CurrentInningModule(
    val id: String,
    val batter: CurrentInningUi.PlayerSummary?,
    val pitcher: CurrentInningUi.PlayerSummary?,
    val currentInning: List<CurrentInningUi.Play>,
    val playStatus: List<CurrentInningUi.CurrentPlayStatus>
) : FeedModuleV2 {

    override val moduleId: String = "CurrentInningModule:$id"

    interface Interaction {
        object OnFullPlayByPlayClick : FeedInteraction
    }

    @Composable
    override fun Render() {
        val interactor = LocalFeedInteractor.current

        CurrentInning(
            batter = batter,
            pitcher = pitcher,
            currentInning = currentInning,
            playStatus = playStatus,
            interactor = interactor
        )
    }
}