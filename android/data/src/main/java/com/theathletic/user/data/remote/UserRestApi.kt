package com.theathletic.user.data.remote

import com.theathletic.entity.user.UserEntity
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

@Deprecated("Further moving to use GraphQl api calls")
interface UserRestApi {
    @GET("v5/customer/{id}")
    suspend fun getUser(@Path("id") id: Long): UserEntity

    @FormUrlEncoded
    @POST("/v5/update_user_policy_agreement")
    suspend fun acceptTermsAndPrivacy(
        @Field("privacy_policy") privacy: Boolean = true,
        @Field("terms_and_conditions") terms: Boolean = true
    )

    @FormUrlEncoded
    @POST("v5/edit_customer")
    suspend fun editUser(
        @Field("userid") userId: Long,
        @Field("fname") fName: String,
        @Field("lname") lName: String,
        @Field("edit_email") editEmail: String
    )
}