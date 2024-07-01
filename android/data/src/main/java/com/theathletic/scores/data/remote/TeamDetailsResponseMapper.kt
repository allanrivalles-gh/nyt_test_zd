package com.theathletic.scores.data.remote

import com.theathletic.GetTeamDetailsQuery
import com.theathletic.data.SizedImage
import com.theathletic.gamedetail.data.remote.toLocal
import com.theathletic.scores.data.local.TeamDetailsLocalModel
import com.theathletic.scores.remote.toLocalLeague
import com.theathletic.type.TeamType

fun GetTeamDetailsQuery.Data.toLocal() = teamv2?.let { team ->
    val league = team.league.firstOrNull { it.is_primary == true } ?: team.league.first()
    TeamDetailsLocalModel(
        id = team.id,
        legacyId = team.legacy_team?.id?.toLong() ?: -1L,
        league = league.id.toLocalLeague,
        isPrimaryLeague = league.is_primary ?: false,
        logoUrls = team.logos.toLogos(),
        sport = team.sport.toLocal(),
        currentStanding = team.current_standing,
        type = team.type.toLocalModel()
    )
}

private fun List<GetTeamDetailsQuery.Logo>.toLogos() = map {
    SizedImage(
        width = it.fragments.logoFragment.width,
        height = it.fragments.logoFragment.height,
        uri = it.fragments.logoFragment.uri
    )
}.sortedBy { it.height }

private fun TeamType.toLocalModel() = when (this) {
    TeamType.allstar -> TeamDetailsLocalModel.TeamType.ALL_STAR
    TeamType.club -> TeamDetailsLocalModel.TeamType.CLUB
    TeamType.national -> TeamDetailsLocalModel.TeamType.NATIONAL
    else -> TeamDetailsLocalModel.TeamType.UNKNOWN
}