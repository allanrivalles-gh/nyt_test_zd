package com.theathletic.auth.remote

import com.theathletic.entity.user.UserEntity
import com.theathletic.extension.toInt
import com.theathletic.fragment.CustomerDetail
import java.util.Date

fun CustomerDetail.toUserEntity(): UserEntity? {
    val longId = id.toLongOrNull()
    return if (longId == null) {
        null
    } else {
        UserEntity().also { entity ->
            entity.id = longId
            entity.email = email
            entity.firstName = first_name
            entity.lastName = last_name
            entity.avatarUrl = avatar_uri
            entity.isAnonymous = is_anonymous == 1
            entity.canHostLiveRoom = can_host_live_rooms
            entity.commentsNotification = notify_comments.toInt()
            entity.topSportsNewsNotification = notify_top_sports_news
            entity.hasInvalidEmail = has_invalid_email
            entity.isFbLinked = (fb_id != null).toInt()
            entity.fbId = fb_id
            entity.endDate = end_date?.let { Date(it) }
            entity.isCodeOfConductAccepted = code_of_conduct_2022
            entity.isEligibleForAttributionSurvey = attribution_survey_eligible
            entity.termsAndConditions = terms_and_conditions
        }
    }
}