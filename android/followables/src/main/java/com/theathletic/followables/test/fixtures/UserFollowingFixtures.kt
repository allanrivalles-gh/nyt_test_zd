package com.theathletic.followables.test.fixtures

import com.theathletic.followable.FollowableId
import com.theathletic.followables.data.domain.UserFollowing

fun teamFollowingFixture(
    id: FollowableId = teamIdFixture(),
    name: String = "Lakers",
    shortName: String = "LAL",
    color: String = "hexColor"
) = userFollowingFixture(
    id = id,
    name = name,
    shortName = shortName,
    color = color
)

fun leagueFollowingFixture(
    id: FollowableId = leagueIdFixture(),
    name: String = "National Basketball Association",
    shortName: String = "NBA"
) = userFollowingFixture(
    id = id,
    name = name,
    shortName = shortName
)

fun authorFollowingFixture(
    id: FollowableId = authorIdFixture(),
    name: String = "Ronaldo de Assis Moreira",
    shortName: String = "R.Gauc",
) = userFollowingFixture(
    id = id,
    name = name,
    shortName = shortName
)

fun userFollowingFixture(
    id: FollowableId = teamIdFixture(),
    name: String = "Lakers",
    shortName: String = "LAL",
    imageUrl: String = "",
    color: String = ""
) = UserFollowing(id, name, shortName, imageUrl, color)