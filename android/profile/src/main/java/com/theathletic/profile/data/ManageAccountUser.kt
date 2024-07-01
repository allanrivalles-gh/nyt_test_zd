package com.theathletic.profile.data

data class ManageAccountUser(
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val isFBLinked: Boolean,
    val isAnonymousAccount: Boolean,
    val isUserSubscribed: Boolean,
)