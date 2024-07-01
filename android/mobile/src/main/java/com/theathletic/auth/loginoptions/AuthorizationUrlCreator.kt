package com.theathletic.auth.loginoptions

import android.net.Uri
import com.theathletic.AthleticConfig.APPLE_AUTHORIZE_URL
import com.theathletic.AthleticConfig.FB_AUTHORIZE_URL
import com.theathletic.AthleticConfig.GOOGLE_AUTHORIZE_URL
import com.theathletic.AthleticConfig.NYT_AUTHORIZE_URL
import com.theathletic.auth.OAuthFlow
import com.theathletic.utility.coroutines.DispatcherProvider
import com.theathletic.utility.crypto.sha256
import java.util.UUID
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Creates authorization urls for fb, google, and apple oauth authorize endpoints. Creates nonce and
 * nonce hash if needed.
 *
 */
class AuthorizationUrlCreator(
    val dispatchers: DispatcherProvider
) {

    suspend fun createAuthRequestUrl(authFlow: OAuthFlow) = withContext(dispatchers.io) {
        val nonce = UUID.randomUUID().toString()
        val encryptedNonce = nonce.sha256()
        val url = when (authFlow) {
            OAuthFlow.APPLE -> getAppleUrl(encryptedNonce)
            OAuthFlow.FACEBOOK -> getFacebookUrl(encryptedNonce)
            OAuthFlow.GOOGLE -> getGoogleUrl(encryptedNonce)
            OAuthFlow.NYT -> getNYTUrl(encryptedNonce)
        }
        Timber.d("auth url: $url")
        AuthUrl(nonce, url)
    }

    private fun getAppleUrl(encryptedNonce: String): String {
        return Uri.parse(
            """
                $APPLE_AUTHORIZE_URL
            """.trimIndent().replace("\n", "")
        ).toString()
    }

    private fun getGoogleUrl(encryptedNonce: String): String {
        return Uri.parse(
            """
            $GOOGLE_AUTHORIZE_URL
            """.trimIndent().replace("\n", "")
        ).toString()
    }

    private fun getFacebookUrl(encryptedNonce: String): String {
        return Uri.parse(
            """
            $FB_AUTHORIZE_URL
            """.trimIndent().replace("\n", "")
        ).toString()
    }

    private fun getNYTUrl(encryptedNonce: String): String {
        return Uri.parse(
            """
            $NYT_AUTHORIZE_URL
            """.trimIndent().replace("\n", "")
        ).toString()
    }

    data class AuthUrl(val nonce: String, val url: String) {
        override fun toString(): String {
            return "AuthUrl()"
        }
    }
}