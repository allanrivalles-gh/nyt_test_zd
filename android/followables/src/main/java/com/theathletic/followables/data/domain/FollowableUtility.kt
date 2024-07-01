package com.theathletic.followables.data.domain

import com.theathletic.entity.main.League

fun List<Followable>.filter(query: String): List<Followable> {
    return if (query.isBlank()) this else filter { it.filterText.contains(query, ignoreCase = true) }
}

fun List<Followable>.filterNot(list: List<Followable>) = filterNot { list.contains(it) }

fun formatName(
    followable: Followable,
    followableList: List<Followable>
) = when (followable) {
    is Followable.Team -> formatTeamName(followable, followableList)
    is Followable.League -> followable.name
    else -> followable.name
}

fun formatTeamName(
    team: Followable.Team,
    followableList: List<Followable>
): String {
    return followableList.filterIsInstance<Followable.League>().firstOrNull { league ->
        league.id == team.leagueId && league.isCollegeLeague
    }?.let { "${team.name} (${it.shortName})" } ?: team.name
}

val Followable.League.isCollegeLeague: Boolean
    get() = id.id == League.NCAA_FB.leagueId.toString() ||
        id.id == League.NCAA_BB.leagueId.toString() ||
        id.id == League.NCAA_WB.leagueId.toString()