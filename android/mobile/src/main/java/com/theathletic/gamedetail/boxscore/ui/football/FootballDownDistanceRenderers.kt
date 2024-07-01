package com.theathletic.gamedetail.boxscore.ui.football

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.modules.DownAndDistanceModule
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.orEmpty

class FootballDownDistanceRenderers @AutoKoin constructor() {

    fun createDownAndDistanceModule(
        game: GameDetailLocalModel,
    ): FeedModuleV2? {
        val extras = game.sportExtras as? GameDetailLocalModel.AmericanFootballExtras ?: return null
        return extras.possession?.driveInfo?.let { driveInfo ->
            DownAndDistanceModule(
                id = game.id,
                title = extras.possession?.toLabel().orEmpty(),
                subTitle =
                StringWithParams(
                    R.string.plays_american_football_drive_stats_subtitle,
                    driveInfo.playCount,
                    driveInfo.yards,
                    driveInfo.duration.orEmpty()
                ),
                teamLogos = extras.possession?.team?.logos ?: emptyList()
            )
        }
    }
}