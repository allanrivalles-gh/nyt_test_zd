package com.theathletic.compass

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.theathletic.BuildConfig
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CompassApi {
    /**
     * Get an ab configuration file from the network
     */
    @Suppress("LongParameterList")
    @GET("compass/v4/config")
    suspend fun getConfig(
        @Query("last_change_date") lastChangeDate: String,
        @Query("identifier") deviceId: String,
        @Query("os_version") osVersion: String,
        @Query("model") deviceModel: String,
        @Query("brand") deviceBrand: String,
        @Query("carrier") deviceCarrier: String,
        @Query("locale") localeString: String,
        @Query("explorer_type") explorerType: String = "android",
        @Query("type") requestorType: String = "mobile",
        @Query("app_version") appVersion: String = BuildConfig.VERSION_NAME,
        @Query("bundle_identifier") bundleIdentifier: String = BuildConfig.APPLICATION_ID,
        @Query("user_identifier") userId: Long? = null,
        @Query("device_push_token") devicePushToken: String? = null
    ): CompassConfigResponse

    /**
     * Ping the server whenever the first step of an experiment has been exposed to the user
     */
    @POST("compass/v1/exposed")
    suspend fun postExposure(@Body exposedRequest: ExposedRequest)

    @Keep
    data class ExposedRequest(
        val identity: Identity,
        @SerializedName("experiment_id") val experimentId: String,
        @SerializedName("variant_id") val variantId: String
    )

    @Keep
    data class Identity(
        @SerializedName("device_id") val deviceId: String?,
        @SerializedName("user_id") val userId: Long?
    )

    @Keep
    data class CompassConfigResponse(
        val timestamp: String,
        val experiments: List<ExperimentResponse>
    )

    @Keep
    data class ExperimentResponse(
        val variant: String,
        val id: String,
        val data: List<FieldResponse>?
    )
}