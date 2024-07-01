package com.theathletic.auth.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.auth.data.remote.AuthenticationApi
import com.theathletic.auth.data.remote.AuthenticationGraphqlApi
import com.theathletic.entity.authentication.AuthenticationResponse
import com.theathletic.entity.authentication.PasswordCredentials
import com.theathletic.entity.authentication.UserData
import com.theathletic.entity.user.UserEntity
import com.theathletic.extension.toInt
import com.theathletic.fragment.CustomerDetail
import com.theathletic.user.UserManager
import io.reactivex.Completable
import io.reactivex.Maybe
import java.util.Date
import retrofit2.Response

class AuthenticationRepository @AutoKoin constructor(
    private val authenticationApi: AuthenticationApi,
    private val authenticationGraphqlApi: AuthenticationGraphqlApi
) {
    @Suppress("LongParameterList")
    suspend fun authWithOAuth2(
        grantType: String,
        tokenCode: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        email: String? = null,
        sub: String? = null
    ): OAuthResponse {
        return authenticationApi.authWithOAuth2(
            OAuthRequest(
                grantType,
                tokenCode,
                firstName,
                lastName,
                email,
                sub
            )
        )
    }

    suspend fun authV5WithEmail(
        passwordCredentials: PasswordCredentials
    ): AuthenticationResponse {
        return authenticationApi.authWithEmail(passwordCredentials)
    }

    suspend fun createAccount(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): Pair<UserEntity, String> {
        val result = authenticationGraphqlApi.createAccount(
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName,
            privacy = false, // This starts false and is later set true to indicate user accepted policy
            tos = false // This starts false and is later set true to indicate user accepted policy
        )
        val createdAccount = result.data?.createAccount
        if (result.hasErrors() || createdAccount == null) {
            throw Exception(result.errors?.first()?.message)
        }
        val authToken = createdAccount.fragments.userCredentials.access_token
        val userEntity = createdAccount.fragments.userCredentials.user.fragments.customerDetail.toUserEntity()
        return Pair(userEntity, authToken)
    }

    fun getUserData(id: Long = UserManager.getCurrentUserId()): Maybe<UserData> {
        return authenticationApi.getUserData(id)
    }

    // TODO: Remove with legacy ManageAccountViewModel [ATH-25176]
    fun editUser(
        userId: Long,
        fName: String,
        lName: String,
        editEmail: String
    ): Completable {
        return authenticationApi.editUser(userId, fName, lName, editEmail)
    }

    fun getReferredArticle(): Maybe<Response<ReferredArticleId>> {
        return authenticationApi.getReferredArticle()
    }
}

private fun CustomerDetail.toUserEntity(): UserEntity {
    val domain = UserEntity()
    domain.id = id.toLong()
    domain.firstName = first_name
    domain.lastName = last_name
    domain.endDate = end_date?.let { Date(it) }
    domain.email = email
    domain.userLevel = user_level.toLong()
    domain.isFbLinked = (fb_id != null).toInt()
    domain.fbId = fb_id
    domain.avatarUrl = avatar_uri
    domain.isCodeOfConductAccepted = code_of_conduct_2022
    domain.commentsNotification = notify_comments.toInt()
    domain.topSportsNewsNotification = notify_top_sports_news
    domain.hasInvalidEmail = has_invalid_email
    domain.isInGracePeriod = is_in_grace_period
    domain.isAnonymous = is_anonymous == 1
    domain.privacyPolicy = privacy_policy
    domain.termsAndConditions = terms_and_conditions
    domain.referralsRedeemed = referrals_redeemed
    domain.referralsTotal = referrals_total
    domain.userContentEdition = content_edition?.rawValue.orEmpty()
    domain.isEligibleForAttributionSurvey = attribution_survey_eligible

    return domain
}

@Keep
data class ReferredArticleId(@SerializedName("article_id") val articleId: Long)

@Keep
data class OAuthResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("expires_in")
    val expiresIn: String,
    @SerializedName("token_type")
    val tokenType: String,
    val user: UserEntity
)

@Keep
data class OAuthRequest(
    @SerializedName("grant_type") val grantType: String,
    @SerializedName("token_code") val tokenCode: String? = null,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("sub") val sub: String? = null,
    @SerializedName("device_id") val deviceId: String = UserManager.getDeviceId()
)