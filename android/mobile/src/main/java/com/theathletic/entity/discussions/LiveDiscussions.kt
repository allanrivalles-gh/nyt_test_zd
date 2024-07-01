package com.theathletic.entity.discussions

import android.graphics.Color
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableLong
import com.google.gson.annotations.SerializedName
import com.theathletic.R
import com.theathletic.adapter.TheSame
import com.theathletic.entity.TopicTagEntity
import com.theathletic.entity.authentication.UserPrivilegeLevel
import com.theathletic.entity.user.UserEntity
import com.theathletic.extension.dependantObservableField
import com.theathletic.extension.extGetColor
import com.theathletic.repository.user.UserDataRepository
import com.theathletic.user.UserManager
import com.theathletic.utility.ColorUtility
import com.theathletic.utility.datetime.DateUtilityImpl
import java.io.Serializable
import java.util.ArrayList
import java.util.Date

enum class LiveDiscussionsState(val value: String) : Serializable {
    START_AT("start_at"),
    START_IN("start_in"),
    COMMENTS("comments"),
    ENDED("ended")
}

open class LiveDiscussionsEntity : Serializable {
    @SerializedName("comments")
    var comments: List<LiveDiscussionEntity> = ArrayList()
}

open class LiveDiscussionEntity {
    @SerializedName("comment_id")
    var commentId: Long = 0L

    @SerializedName("parent_id")
    private var parentId: Long = 0L

    @SerializedName("author_id")
    var authorId: Long = 0L

    @SerializedName("author_user_level")
    private var authorUserLevel: Long = 0L

    @SerializedName("user_level")
    private var userLevel: Long = 0L

    @SerializedName("author_profile_picture")
    var authorProfilePicture: String = ""

    @SerializedName("author_name")
    var authorName: String = ""

    @SerializedName("comment")
    var body: String = ""

    @SerializedName("likes_count")
    var likes: Long = 0L

    @SerializedName("is_flagged")
    var isFlagged: Boolean = false

    @SerializedName("is_ambassador")
    var isAmbassador: Boolean = false

    @SerializedName("replies")
    var comments: ArrayList<LiveDiscussionEntity> = ArrayList()

    @SerializedName("comment_date_gmt")
    var commentDateGmt: String = ""

    @SerializedName("is_pinned")
    var isPinned: Boolean = false

    fun getUserLevel(): UserPrivilegeLevel {
        return UserPrivilegeLevel.from(userLevel)
    }

    fun getAuthorUserLevel(): UserPrivilegeLevel {
        return UserPrivilegeLevel.from(authorUserLevel)
    }

    fun getSortableEntryDateTime(): Long = DateUtilityImpl.parseDateFromGMT(commentDateGmt).time

    fun getTimeStamp(): Long = DateUtilityImpl.parseDateFromGMT(commentDateGmt).time

    fun asLiveDiscussionItem() = LiveDiscussionItem(
        commentId = commentId,
        authorId = authorId,
        authorName = authorName,
        authorProfilePicture = authorProfilePicture,
        isStaff = getAuthorUserLevel().isAtLeastAtLevel(UserPrivilegeLevel.CONTRIBUTOR) ||
            getUserLevel().isAtLeastAtLevel(UserPrivilegeLevel.CONTRIBUTOR),
        commentGmtDate = commentDateGmt,
        body = body,
        totalReplies = comments.size.toLong(),
        likes = ObservableLong(likes),
        isLiked = ObservableBoolean(UserDataRepository.isCommentLiked(commentId)),
        isAmbassador = ObservableBoolean(isAmbassador),
        isFlagged = ObservableBoolean(isFlagged),
        isPinned = ObservableBoolean(isPinned)
    )

    fun asLiveDiscussionReplyItem() = LiveDiscussionReplyItem(
        commentId = commentId,
        parentId = parentId,
        authorId = authorId,
        authorName = authorName,
        authorProfilePicture = authorProfilePicture,
        isStaff = getAuthorUserLevel().isAtLeastAtLevel(UserPrivilegeLevel.CONTRIBUTOR) ||
            getUserLevel().isAtLeastAtLevel(UserPrivilegeLevel.CONTRIBUTOR),
        commentGmtDate = commentDateGmt,
        body = body,
        likes = ObservableLong(likes),
        isLiked = ObservableBoolean(UserDataRepository.isCommentLiked(commentId)),
        isAmbassador = ObservableBoolean(isAmbassador),
        isFlagged = ObservableBoolean(isFlagged)
    )
}

// Tt LiveDiscussion Base Item
open class LiveDiscussionBaseItem : Serializable, TheSame {
    open var commentId: Long = 0L

    override fun isItemTheSame(other: Any?): Boolean = when {
        this === other -> true
        other !is LiveDiscussionBaseItem -> false
        commentId == other.commentId -> true
        else -> false
    }

    override fun isContentTheSame(other: Any?): Boolean = when (other) {
        !is LiveDiscussionBaseItem -> false
        else -> true
    }
}

// Tt LiveDiscussion Text Base Item
open class LiveDiscussionTextBaseItem : LiveDiscussionBaseItem(), Serializable {
    open var authorId: Long = 0L
    open var authorName: String = ""
    open var authorProfilePicture: String? = ""
    open var commentGmtDate: String = ""
    open var body: String = ""
    open var totalReplies: Long = 0L
    open var isStaff: Boolean = false
    open var likes: ObservableLong = ObservableLong(0L)
    open var isLiked: ObservableBoolean = ObservableBoolean(false)
    open var isFlagged: ObservableBoolean = ObservableBoolean(false)
    open var isAmbassador: ObservableBoolean = ObservableBoolean(false)

    override fun isContentTheSame(other: Any?): Boolean = when {
        other !is LiveDiscussionTextBaseItem -> false
        body != other.body -> false
        totalReplies != other.totalReplies -> false
        likes != other.likes -> false
        isLiked != other.isLiked -> false
        isFlagged != other.isFlagged -> false
        isAmbassador != other.isAmbassador -> false
        else -> true
    }

    fun getFormattedDate(): String = DateUtilityImpl.formatTimeAgoFromGMT(commentGmtDate)

    fun determineMoreOptionVisibility(
        authorId: Long,
        isFlagged: ObservableBoolean,
        currentUser: UserEntity?
    ): Boolean {
        val userId = currentUser?.id ?: -1L

        return when {
            authorId == userId -> true // You can always edit or delete your own comment
            !isFlagged.get() -> true // You can flag comment that is not yours, in case it is not yet flagged
            else -> false // Otherwise the option menu is going to be always hidden
        }
    }
}

// Tt LiveDiscussion Item
data class LiveDiscussionItem(
    override var commentId: Long,
    override var authorId: Long,
    override var authorName: String,
    override var authorProfilePicture: String?,
    override var isStaff: Boolean,
    override var commentGmtDate: String,
    override var body: String,
    override var totalReplies: Long,
    override var likes: ObservableLong,
    override var isLiked: ObservableBoolean,
    override var isAmbassador: ObservableBoolean,
    override var isFlagged: ObservableBoolean,
    var isPinned: ObservableBoolean,
    var backgroundColor: Int = R.color.white.extGetColor(),
    var ended: ObservableBoolean = ObservableBoolean(false)
) : LiveDiscussionTextBaseItem() {
    @Transient
    val shouldMoreOptionBeVisible: ObservableField<Boolean> = dependantObservableField(isFlagged) {
        return@dependantObservableField determineMoreOptionVisibility(authorId, isFlagged, UserManager.getCurrentUser())
    }

    fun getComplimentaryColor(): Int {
        return ColorUtility.getContrastFontColor(backgroundColor)
    }
}

// Tt LiveDiscussion Reply Item
data class LiveDiscussionReplyItem(
    override var commentId: Long,
    var parentId: Long,
    override var authorId: Long,
    override var authorName: String,
    override var authorProfilePicture: String?,
    override var isStaff: Boolean,
    override var commentGmtDate: String,
    override var body: String,
    override var likes: ObservableLong,
    override var isLiked: ObservableBoolean,
    override var isAmbassador: ObservableBoolean,
    override var isFlagged: ObservableBoolean,
    var backgroundColor: Int = R.color.white.extGetColor(),
    var ended: ObservableBoolean = ObservableBoolean(false)
) : LiveDiscussionTextBaseItem() {
    @Transient
    val shouldMoreOptionBeVisible: ObservableField<Boolean> = dependantObservableField(isFlagged) {
        return@dependantObservableField determineMoreOptionVisibility(authorId, isFlagged, UserManager.getCurrentUser())
    }

    fun getComplimentaryColor(): Int {
        return ColorUtility.getContrastFontColor(backgroundColor)
    }
}

// Tt LiveDiscussion Header Item
data class LiveDiscussionHeaderItem(
    var startTimeGmt: String = "",
    var endTimeGmt: String = "",
    var header: String = "",
    var headerImage: String = "",
    var title: String,
    var excerpt: String,
    var author: String,
    var commentsCountText: String = "",
    var backgroundColor: Int = Color.WHITE,
    var fontColor: Int = Color.BLACK,
    var entityTags: List<TopicTagEntity>? = ArrayList(),
    var liveDiscussionsId: Long = 0,
    var liveDiscussionsStartsAt: String = "",
    var liveDiscussionsEndsAt: String = ""
) : LiveDiscussionBaseItem() {
    fun getFormattedHeaderDate(): String = DateUtilityImpl.formatTimeAgoFromGMT(if (hasEnded()) endTimeGmt else startTimeGmt)

    private fun hasEnded(): Boolean = (DateUtilityImpl.parseDateFromGMT(endTimeGmt).time - Date().time) <= 0L
}

// Tt LiveDiscussion Session Ended Item
class LiveDiscussionSessionStatusItem(val endTimeGmt: String) : LiveDiscussionBaseItem() {
    fun isLive(): Boolean {
        val endTimeRemaining = DateUtilityImpl.parseDateFromGMT(endTimeGmt).time - Date().time
        return endTimeRemaining >= 0L
    }

    fun getRemainingTime(): Long {
        return DateUtilityImpl.parseDateFromGMT(endTimeGmt).time - Date().time
    }
}

// Tt LiveDiscussion Empty Item
class LiveDiscussionEmptyItem : LiveDiscussionBaseItem()