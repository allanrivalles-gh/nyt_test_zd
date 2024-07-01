package com.theathletic.followables.data

import com.theathletic.followables.data.domain.Followable
import com.theathletic.repository.user.AuthorLocal
import com.theathletic.repository.user.LeagueLocal
import com.theathletic.repository.user.TeamLocal
import com.theathletic.repository.user.nameAbbreviation

fun TeamLocal.toDomain(): Followable.Team {
    return Followable.Team(
        id = id,
        name = name,
        shortName = shortName,
        searchText = searchText,
        url = url,
        displayName = displayName,
        leagueId = leagueId,
        color = colorScheme.iconContrastColor ?: "",
        graphqlId = graphqlId ?: ""
    )
}

fun LeagueLocal.toDomain() =
    Followable.League(
        id = id,
        name = name,
        shortName = shortName,
        searchText = searchText,
        url = url,
        displayName = displayName,
        sportType = sportType ?: "",
        hasScores = hasScores
    )

fun AuthorLocal.toDomain(): Followable.Author {
    return Followable.Author(
        id = id,
        name = name,
        shortName = nameAbbreviation(),
        imageUrl = imageUrl,
        searchText = searchText
    )
}