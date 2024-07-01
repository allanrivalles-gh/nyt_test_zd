package com.theathletic.profile

import com.theathletic.entity.user.UserEntity
import com.theathletic.extension.toInt
import com.theathletic.profile.data.ManageAccountUser

fun manageAccountUserFixture(
    firstName: String? = "First",
    lastName: String? = "Last",
    email: String? = "test@test.com",
    isFBLinked: Boolean = false,
    isAnonymousAccount: Boolean = false,
    isUserSubscribed: Boolean = false,
) = ManageAccountUser(
    firstName = firstName,
    lastName = lastName,
    email = email,
    isFBLinked = isFBLinked,
    isAnonymousAccount = isAnonymousAccount,
    isUserSubscribed = isUserSubscribed,
)

fun userEntityFixture(
    userFirstName: String? = "First",
    userLastName: String? = "Last",
    userEmail: String? = "test@test.com",
    isUserFbLinked: Boolean = false,
    isAnonymousAccount: Boolean = false,
): UserEntity {
    return UserEntity().apply {
        firstName = userFirstName
        lastName = userLastName
        email = userEmail
        isFbLinked = isUserFbLinked.toInt()
        isAnonymous = isAnonymousAccount
    }
}