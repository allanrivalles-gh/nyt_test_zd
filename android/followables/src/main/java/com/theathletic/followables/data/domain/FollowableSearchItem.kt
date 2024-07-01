package com.theathletic.followables.data.domain

import com.theathletic.followable.FollowableId

data class FollowableSearchItem(
    val followableId: FollowableId,
    val graphqlId: String,
    val name: String,
    val imageUrl: String,
    val isFollowing: Boolean
)