package com.theathletic.gamedetail.boxscore.ui.common

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.modules.TeamStatsModule
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.utility.orShortDash
import java.util.concurrent.atomic.AtomicInteger

class BoxScoreTeamStatsRenderers @AutoKoin constructor(
    private val commonRenderers: BoxScoreCommonRenderers
) {
    fun createTeamStatsModule(game: GameDetailLocalModel) = game.renderTeamStats()

    @Deprecated("Use createTeamStatsModule(game: GameDetailLocalModel)")
    fun createTeamStatsModule(
        game: GameDetailLocalModel,
        pageOrder: AtomicInteger
    ): FeedModuleV2? {
        if (game.isGameInProgressOrCompleted.not() || game.awayTeamHomeTeamStats.isEmpty()) return null
        pageOrder.getAndIncrement()
        return game.renderTeamStats()
    }

    private fun GameDetailLocalModel.renderTeamStats(): FeedModuleV2 {
        return TeamStatsModule(
            id = id,
            firstTeamLogos = firstTeam?.team?.logos ?: emptyList(),
            secondTeamLogos = secondTeam?.team?.logos ?: emptyList(),
            stats = awayTeamHomeTeamStats.mapNotNull { stat ->
                if (stat.first.referenceOnly.not()) stat.teamStatItem() else null
            }
        )
    }

    private fun Pair<GameDetailLocalModel.Statistic, GameDetailLocalModel.Statistic>.teamStatItem():
        TeamStatsModule.TeamStatsItem {
        val highlightPair = commonRenderers.compareStatValuesForLargest(this)
        return TeamStatsModule.TeamStatsItem(
            label = first.label,
            firstTeamValue = commonRenderers.formatStatisticValue(first).orShortDash(),
            firstTeamValueColor = if (highlightPair.first) R.color.ath_grey_30 else R.color.ath_grey_50,
            secondTeamValue = commonRenderers.formatStatisticValue(second).orShortDash(),
            secondTeamValueColor = if (highlightPair.second) R.color.ath_grey_30 else R.color.ath_grey_50,
            isChildStat = first.isChildStat
        )
    }
}