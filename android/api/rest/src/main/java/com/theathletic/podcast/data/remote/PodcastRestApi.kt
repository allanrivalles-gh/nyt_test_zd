package com.theathletic.podcast.data.remote

import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.entity.main.PodcastItem
import com.theathletic.entity.main.PodcastLeagueFeed
import io.reactivex.Maybe
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PodcastRestApi {
    @GET("v5/podcasts?feed_type=feed")
    suspend fun getFeed(@Query("id") userID: Long): PodcastFeedRemote

    @GET("v5/podcasts?feed_type=league_with_teams")
    fun getPodcastLeagueFeed(
        @Query("id") leagueId: Long
    ): Maybe<PodcastLeagueFeed>

    @GET("v5/podcasts?feed_type=channel")
    fun getPodcastChannelFeed(
        @Query("id") channelId: Long
    ): Maybe<List<PodcastItem>>

    @GET("v5/podcasts?feed_type=user")
    suspend fun getUserFollowedPodcasts(@Query("id") userId: Long): List<PodcastRemote>

    @GET("v5/podcasts?feed_type=user")
    fun getPodcastUserFeed(
        @Query("id") podcastId: Long
    ): Maybe<List<PodcastItem>>

    @GET("v5/podcasts?feed_type=podcast")
    fun getPodcastDetail(
        @Query("id") podcastId: Long
    ): Maybe<PodcastItem>

    @GET("v5/podcasts?feed_type=episode")
    fun getPodcastEpisodeDetail(
        @Query("id") episodeId: Long
    ): Maybe<PodcastEpisodeItem>

    @FormUrlEncoded
    @POST("v5/add_user_podcast")
    fun followPodcast(
        @Field("podcast_id") podcastId: Long
    ): Maybe<Response<Boolean>>

    @FormUrlEncoded
    @POST("v5/remove_user_podcast")
    fun unFollowPodcast(
        @Field("podcast_id") podcastId: Long
    ): Maybe<Response<Boolean>>

    @Suppress("LongParameterList")
    @GET("v5/log_podcast_listen")
    suspend fun sendLogPodcastListen(
        @Query("podcast_episode_id") podcastEpisodeId: Long,
        @Query("time_elapsed") timeElapsedInSeconds: Int,
        @Query("finished") finished: Int,
        @Query("date") date: String,
        @Query("is_subscriber") isSubscriber: Int,
        @Query("platform") platform: String = "Android"
    )
}