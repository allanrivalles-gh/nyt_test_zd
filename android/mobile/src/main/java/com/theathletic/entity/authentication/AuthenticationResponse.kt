package com.theathletic.entity.authentication

import com.google.gson.annotations.SerializedName
import com.theathletic.entity.user.UserEntity

data class AuthenticationResponse(
    @SerializedName("access_token") var accessToken: String?,
    @SerializedName("user") var user: UserEntity?
)