package com.theathletic.entity.settings

import com.google.gson.annotations.SerializedName

data class EmailSettingsResponse(
    @SerializedName("email_settings") var emailSettings: List<EmailSettingsItem>
)

data class EmailSettingsItem(
    @SerializedName("title") val title: String,
    @SerializedName("email_type") val emailType: String,
    @SerializedName("description") val description: String,
    @SerializedName("value") val value: Boolean,
    @SerializedName("index") val index: Int
)