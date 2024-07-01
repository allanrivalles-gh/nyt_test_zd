package com.theathletic.entity.authentication

import com.google.gson.annotations.SerializedName

/**
This object stores the information about purchased subscription.
 */
data class SubscriptionDataEntity(
    @SerializedName("productId") val productId: String,
    @SerializedName("token") val token: String
)