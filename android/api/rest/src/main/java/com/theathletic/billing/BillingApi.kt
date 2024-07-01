package com.theathletic.billing

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface BillingApi {

    @Suppress("LongParameterList")
    @FormUrlEncoded
    @POST("v5/log_google_sub")
    suspend fun registerGoogleSubscription(
        @Field("subscription_id") subscriptionId: String,
        @Field("token") token: String,
        @Field("source") source: String? = null,
        @Field("article_id") articleId: Long?,
        @Field("podcast_episode_id") podcastEpisodeId: Long?,
        @Field("device_id") deviceId: String?,
        @Field("price") price: Double?,
        @Field("price_currency") priceCurrency: String?,
        @Field("product_identifier") productSku: String,
        @Field("plan_term") planTerm: String?,
        @Field("plan_num") planNum: String?
    ): Response<Boolean>
}