package com.theathletic.network.rest

import com.theathletic.AthleticConfig
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.user.UserManager
import com.theathletic.utility.LocaleUtilityImpl
import com.theathletic.utility.NetworkManager
import com.theathletic.utility.Preferences
import com.theathletic.utility.logging.ICrashLogHandler
import java.io.IOException
import java.util.concurrent.TimeUnit
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import timber.log.Timber

class AuthorizationInterceptor(
    private val userAgent: String,
    private val timezoneId: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().apply {
            Timber.d("Decorating request with token: ${Preferences.accessToken}")
            Preferences.accessToken?.let { addHeader("Authorization", "Bearer $it") }
            addHeader("Accept-Language", LocaleUtilityImpl.acceptLanguage)
            addHeader("X-App-Version", AthleticConfig.VERSION_NAME)
            addHeader("X-Ath-Version", AthleticConfig.VERSION_NAME)
            addHeader("User-Agent", userAgent)
            addHeader("X-Ath-Platform", AthleticConfig.HEADER_ATH_PLATFORM_VALUE)
            addHeader("A-Ath-Timezone", timezoneId)
            addHeader("apollographql-client-name", AthleticConfig.HEADER_ATH_PLATFORM_VALUE)
            addHeader(
                "apollographql-client-version",
                if (AthleticConfig.DEBUG) {
                    "${AthleticConfig.VERSION_NAME}-debug"
                } else {
                    AthleticConfig.VERSION_NAME
                }
            )
            removeHeader("Pragma")
        }
        return chain.proceed(request.build())
    }
}

class StaticAuthTokenInterceptor(
    private val userAgent: String,
    private val token: String = AthleticConfig.REST_ACCESS_TOKEN
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().apply {
            addHeader("Authorization", "Bearer $token")
            addHeader("Accept-Language", LocaleUtilityImpl.acceptLanguage)
            addHeader("X-App-Version", AthleticConfig.VERSION_NAME)
            addHeader("X-Ath-Version", AthleticConfig.VERSION_NAME)
            addHeader("User-Agent", userAgent)
            addHeader("X-Ath-Platform", AthleticConfig.HEADER_ATH_PLATFORM_VALUE)
            removeHeader("Pragma")
        }
        return chain.proceed(request.build())
    }
}

class ResponseCacheInterceptor : Interceptor {
    private val cacheMaxAgeMillis = TimeUnit.DAYS.toMillis(30)

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request()).newBuilder().apply {
            if (chain.request().header("ApplyOfflineCache") != null) {
                removeHeader("ApplyOfflineCache")
                header("Cache-Control", "public, max-age=$cacheMaxAgeMillis")
            }
        }.build()
    }
}

class OfflineResponseCacheInterceptor : Interceptor {
    private val cacheMaxStaleMillis = TimeUnit.DAYS.toMillis(30)
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder().apply {
                if (chain.request().header("ApplyOfflineCache") != null) {
                    removeHeader("ApplyOfflineCache")
                    header("Cache-Control", "public, only-if-cached, max-stale=$cacheMaxStaleMillis")
                }
                if (NetworkManager.connected.get())
                    cacheControl(CacheControl.FORCE_NETWORK)
            }.build()
        )
    }
}

class AnalyticsSuccessRateInterceptor : Interceptor, KoinComponent {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        return try {
            val response = chain.proceed(request)
            val callDuration = response.receivedResponseAtMillis - response.sentRequestAtMillis
            val endpoint = response.request.url.encodedPath
            get<Analytics>().track(
                Event.Global.RequestFinish(
                    duration = callDuration.toString(),
                    endpoint = endpoint
                )
            )
            response
        } catch (error: IOException) {
            // Prevent us from logging request_failed for case where user has airplane mode enabled
            if (NetworkManager.getInstance().isOnline()) {
                get<Analytics>().track(
                    Event.Global.RequestFailed(
                        request.url.encodedPath,
                        error.message ?: error.javaClass.simpleName
                    )
                )
            }
            chain.proceed(request)
        }
    }
}

class UnauthorizedEndpointException(msg: String) : Exception(msg)

class AuthExpirationCheckInterceptor(
    private val remoteLogHandler: ICrashLogHandler
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code == 401 && UserManager.shouldLogOutOn401()) {
            remoteLogHandler.logException(
                UnauthorizedEndpointException("Logged-in user received 401 with response: $response")
            )
            // Tt The API call was unauthorized. Log out the user now!
            UserManager.logOutWithAuthenticationStart()
        }
        return response
    }
}