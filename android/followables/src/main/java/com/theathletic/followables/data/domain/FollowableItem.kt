package com.theathletic.followables.data.domain

import com.theathletic.followable.FollowableId

data class FollowableItem(
    val followableId: FollowableId,
    val name: String,
    val imageUrl: String,
    val isFollowing: Boolean
)