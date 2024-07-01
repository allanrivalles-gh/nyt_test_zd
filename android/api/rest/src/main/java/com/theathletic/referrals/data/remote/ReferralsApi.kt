package com.theathletic.referrals.data.remote

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ReferralsApi {
    @FormUrlEncoded
    @POST("v5/create_referral_url")
    suspend fun createReferralUrl(
        @Field("user_id") userId: Long
    ): Response<CreateReferralUrlResponse>
}

@Keep
data class CreateReferralUrlResponse(
    @SerializedName("referral_url") val referralUrl: String
)