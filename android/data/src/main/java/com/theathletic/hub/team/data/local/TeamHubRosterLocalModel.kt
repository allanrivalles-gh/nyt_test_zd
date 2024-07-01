package com.theathletic.hub.team.data.local

import com.theathletic.TeamRosterQuery
import com.theathletic.data.SizedImage
import com.theathletic.data.SizedImages
import com.theathletic.entity.main.Sport
import com.theathletic.fragment.PlayerRosterDetails
import com.theathletic.gamedetail.data.local.PlayerPosition
import com.theathletic.gamedetail.data.remote.toHeadshot
import com.theathletic.gamedetail.data.remote.toLocal

data class TeamHubRosterLocalModel(
    val teamDetails: TeamDetails,
    val roster: List<PlayerDetails>,
) {
    data class PlayerDetails(
        val id: String,
        val displayName: String?,
        val jerseyNumber: Int?,
        val headshots: SizedImages,
        val position: PlayerPosition,
        val height: Int?,
        val weight: Int?,
        val dateOfBirth: String?
    )

    data class TeamDetails(
        val id: String,
        val logos: SizedImages,
        val teamColor: String?,
        val sport: Sport,
    )
}

fun TeamRosterQuery.Data.toLocalModel(): TeamHubRosterLocalModel? {
    return teamv2?.let { team ->
        TeamHubRosterLocalModel(
            teamDetails = TeamHubRosterLocalModel.TeamDetails(
                id = team.id,
                sport = team.sport.toLocal(),
                logos = team.logos.toTeamLogos(),
                teamColor = team.color_primary,
            ),
            roster = team.members.map { it.fragments.playerRosterDetails.toLocalModel() }
        )
    }
}

private fun PlayerRosterDetails.toLocalModel() = TeamHubRosterLocalModel.PlayerDetails(
    id = id,
    displayName = display_name,
    position = position.toLocal(),
    jerseyNumber = jersey_number,
    headshots = headshots.toHeadshots(),
    height = height,
    weight = weight,
    dateOfBirth = birth_date
)

private fun List<PlayerRosterDetails.Headshot>.toHeadshots() =
    map { it.fragments.headshot.toHeadshot() }.sortedBy { it.height }

private fun List<TeamRosterQuery.Logo>.toTeamLogos() =
    map {
        SizedImage(
            width = it.fragments.logoFragment.width,
            height = it.fragments.logoFragment.height,
            uri = it.fragments.logoFragment.uri
        )
    }