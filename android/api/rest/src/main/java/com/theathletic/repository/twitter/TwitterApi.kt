package com.theathletic.repository.twitter

import com.theathletic.news.TwitterUrl
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TwitterApi {
    @GET("oembed")
    suspend fun getTwitterUrl(
        @Query("url") url: String,
        @Query("theme") theme: String
    ): Response<TwitterUrl>
}