package com.theathletic.auth.data.remote

import com.theathletic.auth.data.OAuthRequest
import com.theathletic.auth.data.OAuthResponse
import com.theathletic.auth.data.ReferredArticleId
import com.theathletic.entity.authentication.AuthenticationResponse
import com.theathletic.entity.authentication.PasswordCredentials
import com.theathletic.entity.authentication.UserData
import com.theathletic.user.UserManager
import io.reactivex.Completable
import io.reactivex.Maybe
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthenticationApi {
    @POST("v5/register_or_login")
    suspend fun authWithOAuth2(
        @Body oAuthRequest: OAuthRequest
    ): OAuthResponse

    // Auth - Email
    @POST("v5/auth")
    suspend fun authWithEmail(
        @Body passwordCredentials: PasswordCredentials
    ): AuthenticationResponse

    @GET("v5/user_dynamic_data/{id}")
    fun getUserData(@Path("id") id: Long = UserManager.getCurrentUserId()): Maybe<UserData>

    @FormUrlEncoded
    @POST("v5/edit_customer")
    fun editUser(
        @Field("userid") userId: Long,
        @Field("fname") fName: String,
        @Field("lname") lName: String,
        @Field("edit_email") editEmail: String
    ): Completable

    @GET("v5/get_last_article_by_ip")
    fun getReferredArticle(): Maybe<Response<ReferredArticleId>>
}