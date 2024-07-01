package com.theathletic.announcement

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AnnouncementApi {
    @FormUrlEncoded
    @POST("/v5/click_announcement")
    suspend fun announcementClicked(
        @Field("announcement_id") announcementId: String
    )

    @FormUrlEncoded
    @POST("/v5/hide_announcement")
    suspend fun announcementHidden(
        @Field("announcement_id") announcementId: String
    )
}