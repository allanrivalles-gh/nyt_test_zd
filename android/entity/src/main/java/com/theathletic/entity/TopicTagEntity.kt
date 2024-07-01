package com.theathletic.entity

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TopicTagEntity(
    @SerializedName("id") var id: Long,
    @SerializedName("type") var type: String,
    @SerializedName("color") var color: String,
    @SerializedName("label") var label: String
)