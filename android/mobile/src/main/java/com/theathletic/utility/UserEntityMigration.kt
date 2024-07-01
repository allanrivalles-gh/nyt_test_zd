package com.theathletic.utility

import android.content.SharedPreferences
import androidx.core.content.edit
import com.theathletic.entity.user.UserEntity
import com.theathletic.extension.extLogError
import com.theathletic.extension.get
import java.util.Date

object UserEntityMigration {
    private const val PREF_USER_ID = "pref_user_id"
    private const val PREF_USER_FIRST_NAME = "pref_user_first_name"
    private const val PREF_USER_LAST_NAME = "pref_user_last_name"
    private const val PREF_USER_SUBSCRIPTION_END_DATE = "pref_user_subscription_end_date"
    private const val PREF_USER_EMAIL = "pref_user_email"
    private const val PREF_USER_LEVEL = "pref_user_level"
    private const val PREF_USER_FB_LINKED = "pref_user_fb_linked"
    private const val PREF_USER_FB_ID = "pref_user_fb_id"
    private const val PREF_USER_AVATAR = "pref_user_avatar"
    private const val PREF_USER_COMMENTS_ACTIVATED = "pref_user_comments_activated"
    private const val PREF_USER_COMMENTS_NOTIFICATION = "pref_user_comments_notification"
    private const val PREF_USER_TOP_SPORTS_NEWS_NOTIFICATION = "pref_user_top_sports_news_notification"
    private const val PREF_USER_HAS_STRIPE_FAILURE = "pref_user_has_stripe_failure"
    private const val PREF_USER_HAS_INVALID_EMAIL = "pref_user_has_invalid_email"
    private const val PREF_USER_IS_IN_GRACE_PERIOD = "pref_user_is_api_subscribed"
    private const val PREF_USER_IS_AMBASSADOR = "pref_user_is_ambassador"
    private const val PREF_USER_IS_ANONYMOUS = "pref_user_is_anonymous"
    private const val PREF_USER_PRIVACY_POLICY_ACCEPTED = "pref_user_privacy_policy_accepted"
    private const val PREF_USER_TERMS_AND_CONDITIONS_ACCEPTED =
        "pref_user_terms_and_conditions_accepted"
    private const val PREF_USER_CAN_HOST_LIVE_ROOMS = "pref_user_can_host_live_rooms"
    private const val PREF_USER_CHAT_CODE_OF_CONDUCT = "pref_user_chat_code_of_conduct"

    fun migrate(sharedPreferences: SharedPreferences, setUserEntity: (UserEntity?) -> Unit) {
        if ((sharedPreferences[PREF_USER_ID, -1L] ?: -1L) != -1L) {
            val userEntity = getUserEntityByField(sharedPreferences)
            setUserEntity(userEntity)
            clearUserFieldsFromPreferences(sharedPreferences)
        }
    }

    @Suppress("ComplexMethod")
    private fun getUserEntityByField(sharedPreferences: SharedPreferences): UserEntity? {
        val user = UserEntity()
        try {
            user.id = sharedPreferences[PREF_USER_ID, -1L] ?: -1L
            user.firstName = sharedPreferences[PREF_USER_FIRST_NAME, null]
            user.lastName = sharedPreferences[PREF_USER_LAST_NAME, null]
            user.endDate = sharedPreferences[PREF_USER_SUBSCRIPTION_END_DATE, Date()] ?: Date()
            user.email = sharedPreferences[PREF_USER_EMAIL, null]
            // TODO(Todd): tmp conversion to Long, we will update the object model to use Int in another commit
            user.setUserLevelRaw((sharedPreferences[PREF_USER_LEVEL, 0] ?: 0).toLong())
            user.isFbLinked = sharedPreferences[PREF_USER_FB_LINKED, 0] ?: 0
            user.fbId = sharedPreferences[PREF_USER_FB_ID, null]
            user.avatarUrl = sharedPreferences[PREF_USER_AVATAR, null]
            user.commentsNotification =
                sharedPreferences[PREF_USER_COMMENTS_NOTIFICATION, 0] ?: 0
            user.topSportsNewsNotification =
                sharedPreferences[PREF_USER_TOP_SPORTS_NEWS_NOTIFICATION, false] ?: false
            user.hasInvalidEmail =
                sharedPreferences[PREF_USER_HAS_INVALID_EMAIL, false] ?: false
            user.isInGracePeriod =
                sharedPreferences[PREF_USER_IS_IN_GRACE_PERIOD, false] ?: false
            user.isAmbassador = sharedPreferences[PREF_USER_IS_AMBASSADOR, false] ?: false
            user.isAnonymous = sharedPreferences[PREF_USER_IS_ANONYMOUS, false] ?: false
            user.privacyPolicy =
                sharedPreferences[PREF_USER_PRIVACY_POLICY_ACCEPTED, false] ?: false
            user.termsAndConditions =
                sharedPreferences[PREF_USER_TERMS_AND_CONDITIONS_ACCEPTED, false] ?: false
            user.canHostLiveRoom =
                sharedPreferences[PREF_USER_CAN_HOST_LIVE_ROOMS, false] ?: false
            user.isCodeOfConductAccepted =
                sharedPreferences[PREF_USER_CHAT_CODE_OF_CONDUCT, false] ?: false
        } catch (e: Exception) {
            e.extLogError()
            clearUserFieldsFromPreferences(sharedPreferences)
            return null
        }

        return user
    }

    private fun clearUserFieldsFromPreferences(sharedPreferences: SharedPreferences) {
        sharedPreferences.edit {
            remove(PREF_USER_ID)
            remove(PREF_USER_FIRST_NAME)
            remove(PREF_USER_LAST_NAME)
            remove(PREF_USER_SUBSCRIPTION_END_DATE)
            remove(PREF_USER_EMAIL)
            remove(PREF_USER_LEVEL)
            remove(PREF_USER_FB_LINKED)
            remove(PREF_USER_FB_ID)
            remove(PREF_USER_AVATAR)
            remove(PREF_USER_COMMENTS_ACTIVATED)
            remove(PREF_USER_COMMENTS_NOTIFICATION)
            remove(PREF_USER_TOP_SPORTS_NEWS_NOTIFICATION)
            remove(PREF_USER_HAS_STRIPE_FAILURE)
            remove(PREF_USER_HAS_INVALID_EMAIL)
            remove(PREF_USER_IS_IN_GRACE_PERIOD)
            remove(PREF_USER_IS_AMBASSADOR)
            remove(PREF_USER_IS_ANONYMOUS)
            remove(PREF_USER_PRIVACY_POLICY_ACCEPTED)
            remove(PREF_USER_TERMS_AND_CONDITIONS_ACCEPTED)
            remove(PREF_USER_CAN_HOST_LIVE_ROOMS)
            remove(PREF_USER_CHAT_CODE_OF_CONDUCT)
        }
    }
}