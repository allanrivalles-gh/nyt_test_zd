package com.theathletic.entity.authentication

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

data class PasswordCredentials(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
) {
    @Keep
    @SerializedName("grant_type")
    val grantType = "password"
}