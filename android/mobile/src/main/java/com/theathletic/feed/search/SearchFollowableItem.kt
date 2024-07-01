package com.theathletic.feed.search

import com.theathletic.entity.main.League
import com.theathletic.followable.Followable
import com.theathletic.followable.FollowableId
import com.theathletic.followable.legacyId
import com.theathletic.repository.user.TeamLocal

data class SearchFollowableItem(
    val followableId: FollowableId,
    val graphqlId: String,
    val name: String,
    val imageUrl: String,
    val isFollowing: Boolean
)

fun FollowableId.getLeagueCode() = League.parseFromId(legacyId)

fun Followable.toSearchItem() = SearchFollowableItem(
    followableId = id,
    graphqlId = if (this is TeamLocal) graphqlId.orEmpty() else "",
    name = name,
    imageUrl = "",
    isFollowing = false,
)