package com.theathletic.network.rest

import com.theathletic.AthleticApplication
import com.theathletic.AthleticConfig
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Named
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.utility.LocaleUtility
import com.theathletic.utility.flipper.FlipperClientUtility
import com.theathletic.utility.logging.ICrashLogHandler
import java.io.File
import java.util.concurrent.TimeUnit
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class OkHttpClientProvider @AutoKoin(Scope.SINGLE) constructor(
    @Named("user-agent") private val userAgent: String,
    private val remoteLogHandler: ICrashLogHandler,
    private val flipperClientUtility: FlipperClientUtility,
    private val localeUtility: LocaleUtility
) {
    fun buildBaseClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        // TT Setup Cache File
        val httpCacheDirectory = File(AthleticApplication.getContext().cacheDir, "responses")
        val cacheSize = 20L * 1024L * 1024L // 20 MiB
        builder.cache(Cache(httpCacheDirectory, cacheSize))

        // TT Setup Timeouts
        builder.connectTimeout(15, TimeUnit.SECONDS)
        builder.readTimeout(15, TimeUnit.SECONDS)
        builder.writeTimeout(15, TimeUnit.SECONDS)

        // TT Setup Interceptors
        // Tt Add Caching interceptor
        builder.addNetworkInterceptor(ResponseCacheInterceptor())
        builder.addInterceptor(OfflineResponseCacheInterceptor())
        builder.addInterceptor(AnalyticsSuccessRateInterceptor())
        builder.addInterceptor(AuthExpirationCheckInterceptor(remoteLogHandler))
        builder.addInterceptor(createLoggingInterceptor())
        flipperClientUtility.getOkHttpInterceptor()?.let { flipperInterceptor ->
            builder.addNetworkInterceptor(interceptor = flipperInterceptor)
        }

        return builder.build()
    }

    fun buildFeatureSwitchedAuthClient(baseClient: OkHttpClient): OkHttpClient {
        return baseClient.newBuilder()
            .addInterceptor(
                AuthorizationInterceptor(
                    userAgent,
                    localeUtility.deviceTimeZone.id
                )
            )
            .build()
    }

    fun buildStaticTokenAuthClient(baseClient: OkHttpClient, token: String): OkHttpClient {
        return baseClient.newBuilder()
            .addInterceptor(StaticAuthTokenInterceptor(userAgent, token))
            .build()
    }

    private fun createLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level = if (AthleticConfig.RETROFIT_LOGS) {
            AthleticConfig.RETROFIT_LOG_LEVEL
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }
}