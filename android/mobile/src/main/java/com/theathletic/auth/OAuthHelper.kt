package com.theathletic.auth

import androidx.annotation.Keep
import com.google.gson.Gson
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.auth.data.AuthenticationRepository
import com.theathletic.auth.data.OAuthResponse
import com.theathletic.network.ResponseStatus
import com.theathletic.repository.safeApiRequest
import com.theathletic.utility.coroutines.DispatcherProvider
import com.theathletic.utility.logging.ICrashLogHandler
import com.theathletic.utility.serialization.ParseResult
import com.theathletic.utility.serialization.safeParse
import java.net.URLDecoder

@Keep
class OAuthHelper @AutoKoin constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val authService: AuthenticationRepository,
    private val crashLogHandler: ICrashLogHandler,
    private val authenticator: Authenticator,
    private val gson: Gson
) {

    suspend fun useOAuth(rawOAuthResult: String, flow: OAuthFlow?): OAuthResult {
        if (flow == null) return OAuthResult.FAILURE

        var didUserCancelFacebookAuth = false
        val parseResult: ParseResult<ThirdPartyOAuthResponse> = safeParse(dispatcherProvider.io) {
            val json = extractJsonFromRawResult(rawOAuthResult)
            didUserCancelFacebookAuth = didUserCancelFacebookAuth(json)
            gson.fromJson(json, ThirdPartyOAuthResponse::class.java)!!
        }

        return when {
            didUserCancelFacebookAuth -> OAuthResult.CANCELLED
            parseResult is ParseResult.Success -> {
                val response = parseResult.body
                handleOAuthResponse(
                    safeApiRequest {
                        authService.authWithOAuth2(
                            grantType = flow.analyticsName,
                            tokenCode = response.token,
                            firstName = response.user?.name?.firstName,
                            lastName = response.user?.name?.lastName,
                            email = response.user?.email,
                            sub = response.sub
                        )
                    }
                )
            }
            parseResult is ParseResult.Error -> {
                crashLogHandler.logException(parseResult.throwable)
                OAuthResult.FAILURE
            }
            else -> OAuthResult.CANCELLED
        }
    }

    private fun didUserCancelFacebookAuth(jsonString: String) =
        jsonString.contains("user_denied", true)

    private suspend fun handleOAuthResponse(response: ResponseStatus<OAuthResponse>): OAuthResult {
        return if (response is ResponseStatus.Success) {
            authenticator.onSuccessfulSignup(response.body.user, response.body.accessToken)
            OAuthResult.SUCCESS
        } else {
            OAuthResult.FAILURE
        }
    }

    private fun extractJsonFromRawResult(rawOAuthResult: String): String {
        return URLDecoder.decode(
            rawOAuthResult,
            Charsets.UTF_8.name()
        ).removePrefix("theathletic://oauth-callback?")
    }
}

@Keep
enum class OAuthFlow(val analyticsName: String) {
    APPLE("apple"),
    GOOGLE("google"),
    FACEBOOK("facebook"),
    NYT("nyt");
}

@Keep
enum class OAuthResult {
    SUCCESS,
    FAILURE,
    CANCELLED
}