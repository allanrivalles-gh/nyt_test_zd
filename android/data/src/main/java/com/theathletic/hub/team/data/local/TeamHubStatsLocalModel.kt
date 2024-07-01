package com.theathletic.hub.team.data.local

import com.theathletic.TeamStatsQuery
import com.theathletic.data.SizedImage
import com.theathletic.data.SizedImages
import com.theathletic.entity.main.Sport
import com.theathletic.fragment.SeasonStatsPlayer
import com.theathletic.fragment.StatLeadersTeam
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.PlayerPosition
import com.theathletic.gamedetail.data.remote.toHeadshot
import com.theathletic.gamedetail.data.remote.toLocal
import com.theathletic.gamedetail.data.remote.toLocalStats
import com.theathletic.utility.safeLet

data class TeamHubStatsLocalModel(
    val teamId: String,
    val sport: Sport,
    val primaryColor: String?,
    val teamLogos: SizedImages,
    val teamLeaders: List<TeamLeaders>,
    val seasonStats: List<GameDetailLocalModel.Statistic>,
    val playerStats: List<PlayerStats>
) {
    data class TeamLeaders(
        val id: String,
        val category: String?,
        val leaders: List<Player>
    ) {

        data class Player(
            val id: String,
            val displayName: String?,
            val headshots: SizedImages,
            val position: PlayerPosition,
            val stats: List<GameDetailLocalModel.Statistic>,
            val label: String?,
            val shortLabel: String?,
        )
    }

    data class PlayerStats(
        val id: String,
        val displayName: String?,
        val headshots: SizedImages,
        val jerseyNumber: Int?,
        val position: PlayerPosition,
        val stats: List<GameDetailLocalModel.Statistic>
    )
}

fun TeamStatsQuery.Data.toLocalModel(): TeamHubStatsLocalModel? {
    return safeLet(teamStats, teamv2) { stats, team ->
        TeamHubStatsLocalModel(
            teamId = team.id,
            sport = team.sport.toLocal(),
            primaryColor = team.color_primary,
            teamLogos = team.logos.toTeamLogos(),
            teamLeaders = team.fragments.statLeadersTeam.stat_leaders.map { it.toLocalModel() },
            seasonStats = stats.season_stats.mapNotNull { it.fragments.gameStat.toLocalStats() },
            playerStats = stats.members.map { it.fragments.seasonStatsPlayer.toLocalModel() }
        )
    }
}

private fun StatLeadersTeam.Stat_leader.toLocalModel() = TeamHubStatsLocalModel.TeamLeaders(
    id = id,
    category = stats_category,
    leaders = leaders.map { it.toLocalModel() }
)

private fun StatLeadersTeam.Leader.toLocalModel() = TeamHubStatsLocalModel.TeamLeaders.Player(
    id = id,
    displayName = player.display_name,
    headshots = player.headshots.toLeaderHeadshot(),
    position = player.position.toLocal(),
    stats = stats.mapNotNull { it.fragments.gameStat.toLocalStats() },
    label = stats_label,
    shortLabel = stats_short_label
)

private fun SeasonStatsPlayer.toLocalModel() = TeamHubStatsLocalModel.PlayerStats(
    id = id,
    displayName = display_name,
    headshots = headshots.toPlayerHeadshot(),
    jerseyNumber = jersey_number,
    position = position.toLocal(),
    stats = season_stats.mapNotNull { it.fragments.gameStat.toLocalStats() }
)

private fun List<StatLeadersTeam.Headshot>.toLeaderHeadshot() =
    map { it.fragments.headshot.toHeadshot() }.sortedBy { it.height }

private fun List<SeasonStatsPlayer.Headshot>.toPlayerHeadshot() =
    map { it.fragments.headshot.toHeadshot() }.sortedBy { it.height }

private fun List<TeamStatsQuery.Logo>.toTeamLogos() =
    map {
        SizedImage(
            width = it.fragments.logoFragment.width,
            height = it.fragments.logoFragment.height,
            uri = it.fragments.logoFragment.uri
        )
    }