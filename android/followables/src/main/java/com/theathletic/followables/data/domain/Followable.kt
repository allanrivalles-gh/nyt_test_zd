package com.theathletic.followables.data.domain

import com.theathletic.followable.FollowableId
import com.theathletic.utility.LogoUtility

sealed class Followable {
    abstract val id: FollowableId
    abstract val name: String
    abstract val shortName: String
    abstract val color: String
    abstract val filterText: String

    data class Team(
        override val id: FollowableId,
        override val name: String,
        override val shortName: String,
        override val color: String,
        val url: String = "",
        val displayName: String,
        val leagueId: FollowableId,
        val graphqlId: String,
        private val searchText: String
    ) : Followable() {
        override val filterText = "$name $shortName $displayName $searchText"
    }

    data class League(
        override val id: FollowableId,
        override val name: String,
        override val shortName: String,
        override val color: String = "",
        val url: String = "",
        val displayName: String,
        val sportType: String = "",
        val hasScores: Boolean = true,
        private val searchText: String
    ) : Followable() {
        override val filterText = "$name $shortName $displayName $searchText"
    }

    data class Author(
        override val id: FollowableId,
        override val name: String,
        override val shortName: String,
        override val color: String = "",
        val imageUrl: String,
        val url: String = "",
        private val searchText: String
    ) : Followable() {
        override val filterText = "$name $shortName $searchText"
    }
}

fun Followable.getImageUrl(logoUtility: LogoUtility) = when (this) {
    is Followable.Team -> logoUtility.getTeamLogoPath(id.id.toLongOrNull())
    is Followable.League -> logoUtility.getColoredLeagueLogoPath(id.id.toLongOrNull())
    is Followable.Author -> imageUrl
}