package com.theathletic.news.repository

import com.theathletic.fragment.User
import com.theathletic.news.NewsImage
import com.theathletic.news.Staff

fun mapApolloUserToUser(user: User): com.theathletic.news.User {
    return when (user.__typename) {
        "Staff" -> user.asStaff!!.fragments.staff.toLocalModel()
        else -> com.theathletic.news.UserImpl(
            id = user.id,
            fullName = user.name
        )
    }
}

fun com.theathletic.fragment.Staff.toLocalModel() = Staff(
    id = id,
    fullName = name,
    firstName = first_name,
    lastName = last_name,
    avatarUrl = avatar_uri,
    description = description,
    fullDescription = full_description,
    leagueId = league_id,
    leagueAvatarUri = league_avatar_uri,
    role = role.rawValue,
    teamId = team_id,
    teamAvatarUri = team_avatar_uri,
    bio = bio
)

fun com.theathletic.fragment.NewsImage.toLocalModel() = NewsImage(
    imageWidth = image_width,
    imageHeight = image_height,
    imageUrl = image_uri,
    thumbnailWidth = thumbnail_width,
    thumbnailHeight = thumbnail_height,
    thumbnailUrl = thumbnail_uri
)