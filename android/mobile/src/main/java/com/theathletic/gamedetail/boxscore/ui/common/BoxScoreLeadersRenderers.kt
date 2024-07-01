package com.theathletic.gamedetail.boxscore.ui.common

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.TopLeaderPerformerUi
import com.theathletic.boxscore.ui.modules.TopPerformersModule
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.PlayerPosition
import com.theathletic.hub.team.data.local.TeamHubStatsLocalModel
import com.theathletic.hub.team.ui.modules.TeamHubTeamLeadersModule
import com.theathletic.themes.AthColor
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.utility.parseHexColor
import com.theathletic.utility.orShortDash
import java.util.concurrent.atomic.AtomicInteger

class BoxScoreLeadersRenderers @AutoKoin constructor(
    private val commonRenderers: BoxScoreCommonRenderers
) {

    fun createTopPerformersModule(game: GameDetailLocalModel): FeedModuleV2? = game.renderTopPerformersModule()

    @Deprecated("Use createTopPerformersModule(game: GameDetailLocalModel)")
    fun createTopPerformersModule(
        game: GameDetailLocalModel,
        pageOrder: AtomicInteger
    ): FeedModuleV2? {
        if (game.isGameInProgressOrCompleted.not()) return null
        pageOrder.getAndIncrement()
        return game.renderTopPerformersModule()
    }

    private fun GameDetailLocalModel.renderTopPerformersModule(): FeedModuleV2? {
        val firstTeamPerformers = firstTeam?.topPerformers
        val secondTeamPerformers = secondTeam?.topPerformers
        if (firstTeamPerformers.isNullOrEmpty() || secondTeamPerformers.isNullOrEmpty()) return null
        return TopPerformersModule(
            id = id,
            titleResId = R.string.box_score_top_performers_title,
            playerStats = renderStatsLeaders((firstTeamPerformers + secondTeamPerformers).groupBy { it.statLabel }),
            subtitle = null
        )
    }

    fun createTeamLeadersModule(game: GameDetailLocalModel): FeedModuleV2? = game.renderTeamLeadersModule()

    @Deprecated("Use createTeamLeadersModule(game: GameDetailLocalModel)")
    fun createTeamLeadersModule(
        game: GameDetailLocalModel,
        pageOrder: AtomicInteger
    ): FeedModuleV2? {
        if (game.isGameScheduled.not()) return null
        pageOrder.getAndIncrement()
        return game.renderTeamLeadersModule()
    }

    fun createTeamLeaderForTeamHubModule(model: TeamHubStatsLocalModel): TeamHubTeamLeadersModule {
        return TeamHubTeamLeadersModule(
            id = model.teamId,
            leaderGroups = model.toTeamLeaderModules()
        )
    }

    private fun GameDetailLocalModel.renderTeamLeadersModule(): FeedModuleV2? {
        val firstTeamLeaders = firstTeam?.teamLeaders
        val secondTeamLeaders = secondTeam?.teamLeaders
        if (firstTeamLeaders.isNullOrEmpty() || secondTeamLeaders.isNullOrEmpty()) return null
        return TopPerformersModule(
            id = id,
            titleResId = R.string.box_score_team_leaders_title,
            playerStats = renderStatsLeaders((firstTeamLeaders + secondTeamLeaders).groupBy { it.statLabel }),
            subtitle = seasonName
        )
    }

    private fun renderStatsLeaders(statsGroups: Map<String?, List<GameDetailLocalModel.StatLeader>>): List<TopLeaderPerformerUi.Category> {
        return statsGroups.entries.map {
            TopLeaderPerformerUi.Category(
                label = it.key.orShortDash(),
                players = it.value.mapIndexed { index, statLeader ->
                    statLeader.toPlayerDetails(index, it.value.lastIndex)
                }
            )
        }
    }

    private fun TeamHubStatsLocalModel.toTeamLeaderModules() =
        teamLeaders.map { group ->
            TeamHubTeamLeadersModule.Group(
                label = group.category.orEmpty(),
                players = group.leaders.mapIndexed { index, player ->
                    TeamHubTeamLeadersModule.Player(
                        name = player.displayName.orShortDash(),
                        position = player.position.alias,
                        headShots = player.headshots,
                        teamColor = primaryColor.parseHexColor(AthColor.Gray500),
                        teamLogos = teamLogos,
                        stats = player.stats.map { statistic ->
                            TeamHubTeamLeadersModule.PlayerStatistic(
                                label = player.statLabel,
                                value = statistic.toStatsValue(),
                            )
                        },
                        showDivider = index != group.leaders.lastIndex
                    )
                }
            )
        }

    private fun GameDetailLocalModel.StatLeader.toPlayerDetails(
        index: Int,
        lastIndex: Int
    ): TopLeaderPerformerUi.Player =
        TopLeaderPerformerUi.Player(
            teamLogoList = this.teamLogos.orEmpty(),
            details = this.toPlayerDetails,
            name = this.playerName.orShortDash(),
            headShotList = this.headshots,
            teamColor = this.primaryColor.parseHexColor(),
            showDivider = lastIndex != index,
            stats = this.stats.map {
                it.toPlayerStats()
            }
        )

    private fun GameDetailLocalModel.Statistic.toPlayerStats() =
        TopLeaderPerformerUi.PlayerStats(this.toStatsLabel(), this.toStatsValue())

    private val GameDetailLocalModel.StatLeader.toPlayerDetails: ResourceString
        get() = if (playerPosition == null || playerPosition == PlayerPosition.UNKNOWN) {
            StringWithParams(
                R.string.box_score_stats_leader_player_details_no_pos_formatter,
                teamAlias.orShortDash(),
                jerseyNumber.orShortDash()
            )
        } else {
            StringWithParams(
                R.string.box_score_stats_leader_player_details_formatter,
                playerPosition?.alias.orShortDash(),
                teamAlias.orShortDash(),
                jerseyNumber.orShortDash()
            )
        }

    private fun GameDetailLocalModel.Statistic.toStatsLabel() =
        headerLabel.orShortDash()

    private fun GameDetailLocalModel.Statistic.toStatsValue() =
        commonRenderers.formatStatisticValue(this).orShortDash()

    private val TeamHubStatsLocalModel.TeamLeaders.Player.statLabel: String
        get() = if (label.isNullOrEmpty().not()) {
            label.orShortDash()
        } else {
            shortLabel.orShortDash()
        }
}