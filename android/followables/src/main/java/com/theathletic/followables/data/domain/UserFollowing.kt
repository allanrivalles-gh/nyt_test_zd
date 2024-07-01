package com.theathletic.followables.data.domain

import com.theathletic.followable.FollowableId

data class UserFollowing(
    val id: FollowableId,
    val name: String,
    val shortName: String,
    val imageUrl: String,
    val color: String
)