package com.theathletic.boxscore.ui.modules

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.theathletic.boxscore.ui.RecentPlays
import com.theathletic.data.SizedImages
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.ui.ResourceString

data class RecentPlaysModule(
    val id: String,
    val recentPlays: List<RecentPlay>
) : FeedModuleV2 {

    override val moduleId: String = "RecentPlaysModule:$id"

    sealed class RecentPlay {
        data class Timeout(
            val id: String,
            val title: String
        ) : RecentPlay()

        data class Stoppage(
            val id: String,
            val title: String,
            val description: String
        ) : RecentPlay()

        data class HockeyShootout(
            val id: String,
            val headshots: SizedImages,
            val teamLogos: SizedImages,
            val teamColor: Color,
            val playerName: String,
            val teamAlias: String,
            val description: String,
            val isGoal: Boolean
        ) : RecentPlay()

        data class Play(
            val id: String,
            val teamLogos: SizedImages,
            val title: String,
            val description: String,
            val clock: String,
            val awayTeamAlias: String,
            val homeTeamAlias: String,
            val awayTeamScore: String,
            val homeTeamScore: String,
            val showScores: Boolean
        ) : RecentPlay()

        data class AmericanFootballPlay(
            val title: String,
            val description: String,
            val possession: ResourceString,
            val showDivider: Boolean,
            val teamLogos: SizedImages,
            val teamColor: String?,
            val awayTeamAlias: String,
            val homeTeamAlias: String,
            val awayTeamScore: String,
            val homeTeamScore: String,
            val isScoringPlay: Boolean,
            val clock: String
        ) : RecentPlay()
    }

    interface Interaction {
        object OnFullPlayByPlayClick : FeedInteraction
    }

    @Composable
    override fun Render() {
        val interactor = LocalFeedInteractor.current

        RecentPlays(
            recentPlays = recentPlays,
            interactor = interactor
        )
    }
}