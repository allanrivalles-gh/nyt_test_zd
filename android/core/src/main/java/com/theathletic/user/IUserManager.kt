package com.theathletic.user

import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.entity.settings.UserContentEdition
import com.theathletic.entity.user.SortType
import com.theathletic.entity.user.UserEntity

interface IUserManager {
    fun isUserLoggedIn(): Boolean

    fun isUserSubscribed(): Boolean

    fun isUserFreeTrialEligible(): Boolean

    fun getCurrentUser(): UserEntity?

    fun getCurrentUserId(): Long

    fun getDeviceId(): String?

    fun setUserContentEdition(region: String)

    fun getUserContentEdition(): UserContentEdition

    fun isCodeOfConductAccepted(): Boolean

    fun isUserSubscribedOnBackend(): Boolean

    fun saveCurrentUser(userEntity: UserEntity?, withRefresh: Boolean = true)

    fun getArticleRating(articleId: Long): Long

    fun addArticleRating(articleId: Long, rating: Long)

    fun logOut()

    fun getCommentsSortType(commentsSourceType: CommentsSourceType): SortType

    fun updateCommentsSortType(commentsSourceType: CommentsSourceType, sortType: SortType)

    fun isUserTempBanned(): Boolean

    fun isCommentRepliesOptIn(): Boolean

    fun isTopSportsNewsOptIn(): Boolean

    fun getBannedDaysLeft(): Int

    val isAnonymous: Boolean
    val isStaff: Boolean
    val isFbLinked: Boolean

    companion object {
        const val NO_USER = -1L
    }
}