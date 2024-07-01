package com.theathletic.notifications

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class IterableNotificationPayload(
    @SerializedName("isGhostPush")
    val isGhostPush: Boolean?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("campaignId")
    val campaignId: Int?,
    @SerializedName("templateId")
    val templateId: Int?,
    @SerializedName("messageId")
    val messageId: String?,
    @SerializedName("defaultAction")
    val defaultAction: DefaultAction?,
) {
    data class DefaultAction(
        @SerializedName("type")
        val type: String?
    )

    companion object : NotificationPayloadCompanion<IterableNotificationPayload> {
        override val payloadKey = "itbl"

        override fun maybeFromJson(json: String): IterableNotificationPayload? {
            return Gson().maybeFromJson(json, IterableNotificationPayload::class.java)
        }
    }
}