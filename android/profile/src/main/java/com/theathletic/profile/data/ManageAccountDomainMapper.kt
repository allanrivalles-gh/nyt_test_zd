package com.theathletic.profile.data

import com.theathletic.entity.user.UserEntity

fun UserEntity.toDomain(isUserSubscribed: Boolean) = ManageAccountUser(
    firstName = firstName,
    lastName = lastName,
    email = email,
    isFBLinked = isFbLinked != 0,
    isAnonymousAccount = isAnonymous,
    isUserSubscribed = isUserSubscribed
)