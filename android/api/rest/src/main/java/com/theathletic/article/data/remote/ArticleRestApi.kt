package com.theathletic.article.data.remote

import com.theathletic.entity.article.ArticleEntity
import io.reactivex.Maybe
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ArticleRestApi {
    @GET("v5cached/articles/{id}")
    fun getArticle(@Path("id") id: Long): Maybe<Response<ArticleEntity>>

    @FormUrlEncoded
    @POST("v5/save_user_article")
    fun saveStory(
        @Field("article_id") articleId: Long
    ): Maybe<Response<Long>>

    @FormUrlEncoded
    @POST("v5/unsave_user_article")
    fun removeStory(
        @Field("article_id") articleId: Long
    ): Maybe<Response<Long>>

    @GET("v5/log_article_rating")
    suspend fun setArticleRated(
        @Query("article_id") articleId: Long,
        @Query("rating_id") ratingId: Long,
        @Query("platform") platform: String = "android"
    ): Response<Unit>

    @POST("v5/log_article_read")
    suspend fun setArticleRead(
        @Query("article_id") articleId: Long,
        @Query("is_read") isRead: Boolean
    )
}