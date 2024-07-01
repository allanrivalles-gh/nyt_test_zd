package com.theathletic.location.data

data class CurrentLocationResponse(
    val city: String? = null,
    val state: String? = null,
    val country: String? = null,
    val continent: String? = null,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val ip: String? = null,
    val responseCode: Int = 0
)