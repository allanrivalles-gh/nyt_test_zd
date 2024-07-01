package com.theathletic.boxscore.ui.modules

import androidx.compose.runtime.Composable
import com.theathletic.boxscore.ui.KeyMoments
import com.theathletic.boxscore.ui.RecentMoments
import com.theathletic.boxscore.ui.SoccerMomentsUi
import com.theathletic.feed.ui.FeedModule
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.LocalFeedInteractor

data class KeyMomentsModule(
    val id: String,
    val keyMoments: List<SoccerMomentsUi>,
    val soccerPenaltyShootoutModule: FeedModule? = null
) : FeedModuleV2 {

    override val moduleId: String = "KeyMomentsModule:$id"

    @Composable
    override fun Render() {
        KeyMoments(
            keyMoments = keyMoments,
            soccerPenaltyShootoutModule = soccerPenaltyShootoutModule,
        )
    }
}

data class RecentMomentsModule(
    val id: String,
    val recentMoments: List<SoccerMomentsUi>,
    val soccerPenaltyShootoutModule: FeedModule? = null
) : FeedModuleV2 {

    override val moduleId: String = "RecentMomentsModule:$id"

    @Composable
    override fun Render() {
        val interactor = LocalFeedInteractor.current
        RecentMoments(
            recentMoments = recentMoments,
            soccerPenaltyShootoutModule = soccerPenaltyShootoutModule,
            onFullTimeLineClick = { interactor.send(SoccerMomentsUi.Interaction.OnFullTimeLineClick) }

        )
    }
}