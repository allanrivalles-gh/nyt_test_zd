package com.theathletic.author.data.remote

import com.theathletic.author.data.AuthorDetailResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface AuthorApi {
    // TT Author Detail
    @GET("v5/authors/{id}")
    suspend fun getAuthorDetail(
        @Path("id") authorId: Long,
        @Header("ApplyOfflineCache") offlineCache: Boolean = true
    ): Response<AuthorDetailResponse>
}