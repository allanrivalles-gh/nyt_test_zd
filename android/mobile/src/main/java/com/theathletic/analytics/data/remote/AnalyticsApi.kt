package com.theathletic.analytics.data.remote

import com.theathletic.extension.toInt
import com.theathletic.user.UserManager
import com.theathletic.utility.datetime.DateUtilityImpl
import io.reactivex.Completable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface AnalyticsApi {
    @Suppress("LongParameterList")
    @GET("v5/log_analytics")
    fun sendLogArticleViewAnalytics(
        @Query("event_name") eventName: String,
        @Query("event_key") eventKey: String,
        @Query("event_value") eventValue: String,
        @Query("has_paywall") hasPaywall: Int,
        @Query("percent_read") percentRead: Int?,
        @Query("article_source") source: String? = null,
        @Query("referrer") referrer: String,
        @Query("num_free_articles_viewed") freeArticleViewed: Long = 0,
        @Query("num_paywalls_viewed") paywallArticleViewed: Long = 0,
        @Query("is_subscriber") isSubscriber: Int = UserManager.isUserSubscribed().toInt(),
        @Query("date") date: String = DateUtilityImpl.getCurrentTimeInGMT(),
        @Query("event_timestamp") eventTimestamp: String = DateUtilityImpl.getCurrentTimeInGMT(),
        @Query("device_id") deviceId: String? = UserManager.getDeviceId(),
        @Query("platform") platform: String = "Android",
        @QueryMap deepLinkParams: Map<String, String>?,
        @QueryMap customOptions: Map<String, String> = hashMapOf()
    ): Completable
}