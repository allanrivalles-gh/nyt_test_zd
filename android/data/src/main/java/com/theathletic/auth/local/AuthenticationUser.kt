package com.theathletic.auth.local

import com.theathletic.entity.user.UserEntity

data class AuthenticationUser(
    val token: String,
    val userEntity: UserEntity
)