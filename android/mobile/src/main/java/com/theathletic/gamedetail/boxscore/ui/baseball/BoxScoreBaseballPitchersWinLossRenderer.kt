package com.theathletic.gamedetail.boxscore.ui.baseball

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.modules.PitcherWinLossModule
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreCommonRenderers
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.utility.parseHexColor
import com.theathletic.utility.orShortDash

class BoxScoreBaseballPitchersWinLossRenderer @AutoKoin constructor(
    private val commonRenderers: BoxScoreCommonRenderers
) {

    fun createPitcherWinLossModule(game: GameDetailLocalModel): FeedModuleV2? {
        return (game.sportExtras as? GameDetailLocalModel.BaseballExtras)?.pitching?.let { pitching ->
            PitcherWinLossModule(
                id = game.id,
                pitchers = listOfNotNull(
                    pitching.winPitcher?.toPitcherModule(StringWithParams(R.string.box_score_baseball_pitcher_win)),
                    pitching.lossPitcher?.toPitcherModule(StringWithParams(R.string.box_score_baseball_pitcher_loss)),
                    pitching.savePitcher?.toPitcherModule(StringWithParams(R.string.box_score_baseball_pitcher_save))
                )
            )
        }
    }

    private fun GameDetailLocalModel.BaseballPlayer.toPitcherModule(
        title: ResourceString
    ) = PitcherWinLossModule.Pitcher(
        title = title,
        stats = this.gameStats.toDisplay().orEmpty(),
        headshot = this.player.headshots,
        teamColor = this.player.teamColor.parseHexColor(),
        name = this.player.displayName.orShortDash()
    )

    private fun List<GameDetailLocalModel.Statistic>?.toDisplay(): String? {
        if (isNullOrEmpty()) return null
        return joinToString {
            commonRenderers.formatStatisticValue(it).orShortDash() + " " + it.headerLabel
        }
    }
}