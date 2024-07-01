package com.theathletic.ads

interface AdConfigClient {
    val platform: String
    val property: String
        get() = "athdroid"
}