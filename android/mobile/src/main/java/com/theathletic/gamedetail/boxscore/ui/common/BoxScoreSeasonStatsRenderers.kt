package com.theathletic.gamedetail.boxscore.ui.common

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.BoxScoreSeasonStatsUiModel
import com.theathletic.boxscore.ui.formatters.OrdinalFormatter
import com.theathletic.boxscore.ui.modules.SeasonStatsModule
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.hub.team.data.local.TeamHubStatsLocalModel
import com.theathletic.hub.team.ui.modules.TeamHubSeasonStatsModule
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.utility.orShortDash
import com.theathletic.utility.transformIfNotEmptyElseNull
import java.util.concurrent.atomic.AtomicInteger

class BoxScoreSeasonStatsRenderers @AutoKoin constructor(
    private val ordinalFormatter: OrdinalFormatter,
    private val commonRenderers: BoxScoreCommonRenderers,
) {

    fun createSeasonStatsModule(game: GameDetailLocalModel): FeedModuleV2 = game.seasonStats()

    @Deprecated("Use createSeasonStatsModule(game: GameDetailLocalModel)")
    fun createSeasonStatsModule(
        game: GameDetailLocalModel,
        pageOrder: AtomicInteger
    ): FeedModuleV2? {
        if (!game.isGameScheduled) return null
        return game.awayTeamHomeTeamSeasonStats.transformIfNotEmptyElseNull {
            pageOrder.getAndIncrement()
            game.seasonStats()
        }
    }

    private fun GameDetailLocalModel.seasonStats() = SeasonStatsModule(
        id = id,
        seasonStats = BoxScoreSeasonStatsUiModel(
            firstTeamLogos = firstTeam?.team?.logos ?: emptyList(),
            secondTeamLogos = secondTeam?.team?.logos ?: emptyList(),
            statsItems = awayTeamHomeTeamSeasonStats.map { stat ->
                stat.seasonStatItem()
            },
            headerSubtitle = seasonName
        )
    )

    private fun Pair<GameDetailLocalModel.RankedStat, GameDetailLocalModel.RankedStat>.seasonStatItem() =
        BoxScoreSeasonStatsUiModel.BoxScoreSeasonStatsItem(
            firstTeamValue = first.statValue,
            firstTeamRank = StringWithParams(
                R.string.box_scores_season_stats_rank,
                ordinalFormatter.format(first.rank)
            ),
            showFirstTeamRank = first.rank != 0,
            secondTeamValue = second.statValue,
            secondTeamRank = StringWithParams(
                R.string.box_scores_season_stats_rank,
                ordinalFormatter.format(second.rank)
            ),
            showSecondTeamRank = first.rank != 0,
            statLabel = first.statLabel,
            isChildStat = first.parentStatCategory != null
        )

    fun createTeamSeasonStatsModule(
        model: TeamHubStatsLocalModel
    ): TeamHubSeasonStatsModule {
        return TeamHubSeasonStatsModule(
            id = model.teamId,
            stats = model.seasonStats.map { statistic ->
                TeamHubSeasonStatsModule.SingleTeamStatsItem(
                    value = commonRenderers.formatStatisticValue(statistic).orShortDash(),
                    label = statistic.label,
                    isChildStat = statistic.isChildStat,
                )
            }
        )
    }
}