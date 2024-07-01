package com.theathletic.analytics

import com.theathletic.AthleticConfig
import com.theathletic.BuildConfig
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope

class AnalyticsEndpointConfig @AutoKoin(Scope.SINGLE) constructor() {
    private val config = when {
        BuildConfig.DEV_ENVIRONMENT -> staging
        else -> prod
    }
    val endpoint: String = config.endpoint
    val accessToken: String = config.accessToken
    val schemaId: Int = config.schema
    val topic: String = config.topic
}

private data class Config(
    val endpoint: String,
    val accessToken: String,
    val schema: Int,
    val topic: String
)

private val staging = Config(
    endpoint = "https://analytic-proxy-staging.${AthleticConfig.BASE_URL_US}",
    accessToken = "e3p2NHDerBNWkeKQ3suKC6dywRo6xMQ7",
    schema = 26,
    topic = "staging-android-events"
)

private val prod = Config(
    endpoint = "https://analytic-proxy.${AthleticConfig.BASE_URL_US}",
    accessToken = "3Jsgsg2EeX24F96WMFURVhaQKfnAuMZM",
    schema = 26,
    topic = "production-android-events"
)