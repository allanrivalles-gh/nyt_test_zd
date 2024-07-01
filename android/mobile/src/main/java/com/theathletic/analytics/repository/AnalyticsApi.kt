package com.theathletic.analytics.repository

import com.theathletic.analytics.data.remote.AnalyticsEventBatch
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AnalyticsApi {
    @Headers("Content-Type: application/json")
    @POST("v1/avro/send")
    suspend fun postAnalytics(@Body analytics: AnalyticsEventBatch)
}