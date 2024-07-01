package com.theathletic.entity.user

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import com.theathletic.entity.authentication.UserPrivilegeLevel
import java.io.Serializable
import java.util.Date

@JsonClass(generateAdapter = true)
class UserEntity : Serializable {
    @SerializedName("id")
    var id: Long? = null

    @SerializedName("fname")
    var firstName: String? = null

    @SerializedName("lname")
    var lastName: String? = null

    @SerializedName("end_date")
    var endDate: Date? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("user_level")
    var userLevel: Long = 0

    @SerializedName("is_fb_linked")
    var isFbLinked: Int = 0

    @SerializedName("fb_id")
    var fbId: String? = null

    @SerializedName("avatar_url")
    var avatarUrl: String? = null

    @SerializedName("temp_ban_end_date")
    var tempBanEndTime: String? = null

    @SerializedName("code_of_conduct_2022")
    var isCodeOfConductAccepted: Boolean = false

    @SerializedName("notif_comments")
    var commentsNotification: Int = 0

    @SerializedName("notif_top_sports_news")
    var topSportsNewsNotification: Boolean = false

    @SerializedName("should_log_user_out")
    var shouldLogUserOut: Boolean = false

    @SerializedName("has_invalid_email")
    var hasInvalidEmail: Boolean = false

    @SerializedName("is_in_grace_period")
    var isInGracePeriod: Boolean = false

    @SerializedName("is_ambassador")
    var isAmbassador: Boolean = false

    @SerializedName("is_anonymous")
    var isAnonymous: Boolean = false

    @SerializedName("privacy_policy")
    var privacyPolicy: Boolean = false

    @SerializedName("terms_and_conditions")
    var termsAndConditions: Boolean = false

    @SerializedName("referrals_redeemed")
    var referralsRedeemed: Int = 0

    @SerializedName("referrals_total")
    var referralsTotal: Int = 5

    @SerializedName("content_edition")
    var userContentEdition: String = ""

    @SerializedName("attribution_survey_eligible")
    var isEligibleForAttributionSurvey: Boolean = false

    var canHostLiveRoom: Boolean = false

    // Tt Manual deserialization
    var ambassadorLeagueIds: MutableList<Long> = mutableListOf()
    var ambassadorTeamIds: MutableList<Long> = mutableListOf()
    var ambassadorCityIds: MutableList<Long> = mutableListOf()

    var commentsSortType: CommentsSortType? = null

    fun getUserNickName(): String {
        if (!firstName.isNullOrEmpty() && !lastName.isNullOrEmpty())
            return firstName + " " + lastName?.first() + "."
        return ""
    }

    fun getUserFullName(): String {
        return when {
            firstName.isNullOrBlank() && lastName.isNullOrBlank() -> ""
            firstName.isNullOrBlank() -> lastName ?: ""
            lastName.isNullOrBlank() -> firstName ?: ""
            else -> "$firstName $lastName"
        }
    }

    fun getUserLevel(): UserPrivilegeLevel {
        return UserPrivilegeLevel.from(userLevel)
    }

    fun getUserLevelRaw(): Long {
        return userLevel
    }

    fun setUserLevelRaw(userLevel: Long) {
        this.userLevel = userLevel
    }
}