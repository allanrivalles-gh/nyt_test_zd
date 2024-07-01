package com.theathletic.article.data.remote

import com.theathletic.article.data.local.InsiderEntity
import com.theathletic.fragment.Staff

fun Staff.toInsiderEntity(): InsiderEntity {
    return InsiderEntity(
        id = id,
        firstName = first_name,
        lastName = last_name,
        fullName = name,
        bio = bio.orEmpty(),
        role = full_description.orEmpty(),
        imageUrl = avatar_uri.orEmpty(),
        insiderImageUrl = insider_avatar_uri.orEmpty()
    )
}