package com.theathletic.notifications

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class AthleticNotificationPayload(
    @SerializedName("messageId")
    val messageId: String,
    @SerializedName("url")
    val url: String?,
    @SerializedName("contentId")
    val contentId: String?,
    @SerializedName("athletic")
    val athletic: Metadata,
) {
    data class Metadata(
        @SerializedName("campaignId")
        val campaignId: String?,
        @SerializedName("isGhostPush")
        val isGhostPush: Boolean,
    )

    companion object : NotificationPayloadCompanion<AthleticNotificationPayload> {
        override val payloadKey = "athletic"

        override fun maybeFromJson(json: String): AthleticNotificationPayload? {
            return Gson().maybeFromJson(json, AthleticNotificationPayload::class.java)
        }
    }
}