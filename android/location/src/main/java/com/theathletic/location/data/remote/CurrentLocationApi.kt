package com.theathletic.location.data.remote

import com.theathletic.location.data.CurrentLocationResponse
import retrofit2.http.GET

interface CurrentLocationApi {
    @GET("svc/location/v1/current.json")
    suspend fun getCurrentLocation(): CurrentLocationResponse
}