package com.theathletic.auth

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/*
    This class is used to deserialize the data we receive from Google, Facebook, and Apple
    when a user chooses to sign in with one of those accounts.
 */
@Keep
data class ThirdPartyOAuthResponse(
    val sub: String?,
    val user: UserResponse?,
    @SerializedName(value = "id_token", alternate = ["fb_token"]) val token: String?
)

@Keep
data class UserResponse(
    val name: Name,
    val email: String

) {
    override fun toString(): String {
        return "UserResponse()"
    }
}

@Keep
data class Name(
    val firstName: String,
    val lastName: String,
    val middleName: String
) {
    override fun toString(): String {
        return "Name()"
    }
}