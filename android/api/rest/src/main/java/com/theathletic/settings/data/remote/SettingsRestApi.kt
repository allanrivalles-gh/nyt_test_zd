package com.theathletic.settings.data.remote

import com.theathletic.entity.settings.EmailSettingsResponse
import com.theathletic.entity.settings.UserTopics
import com.theathletic.entity.user.UserEntity
import io.reactivex.Completable
import io.reactivex.Maybe
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

@Deprecated("Further moving to use GraphQl api calls")
interface SettingsRestApi {
    @FormUrlEncoded
    @POST("v5/add_user_notification")
    suspend fun addPushSettings(
        @Field("notif_type") notifType: String? = null,
        @Field("notif_name") notifName: String? = null,
        @Field("notif_value") notifValue: Long? = null
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("v5/remove_user_notification")
    suspend fun removePushSettings(
        @Field("notif_type") notifType: String? = null,
        @Field("notif_name") notifName: String? = null,
        @Field("notif_value") notifValue: Long? = null
    ): Response<ResponseBody>

    @GET("v5/user_emails/{user_id}")
    suspend fun getUserEmailSettings(
        @Path("user_id") userId: Long,
        @Header("ApplyOfflineCache") offlineCache: Boolean = true
    ): EmailSettingsResponse

    @FormUrlEncoded
    @POST("v5/add_user_email")
    fun emailNewsLetterSubscribe(
        @Field("email_type") emailType: String
    ): Completable

    @FormUrlEncoded
    @POST("v5/remove_user_email")
    fun emailNewsletterUnsubscribe(
        @Field("email_type") emailType: String
    ): Completable

    @FormUrlEncoded
    @POST("v5/toggle_promo_email")
    fun togglePromoEmail(
        @Field("promo_on") checked: Long
    ): Completable

    @FormUrlEncoded
    @POST("v5/set_user_topics")
    suspend fun setUserTopics(
        @Field("league_ids[]") leagueIds: List<Long>,
        @Field("team_ids[]") teamIds: List<Long>
    ): UserEntity

    @GET("v5/user_topics/{user_id}")
    fun getUserTopics(
        @Path("user_id") userId: Long
    ): Maybe<UserTopics>
}