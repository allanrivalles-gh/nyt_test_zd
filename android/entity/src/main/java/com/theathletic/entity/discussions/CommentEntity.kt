package com.theathletic.entity.discussions

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import com.theathletic.entity.authentication.UserPrivilegeLevel

@JsonClass(generateAdapter = true)
class CommentEntity {
    @SerializedName("comment_id")
    var commentId: Long = 0L

    @SerializedName("parent_id")
    var parentId: Long = 0L

    @SerializedName("author_id")
    var authorId: Long = 0L

    @SerializedName("author_user_level")
    var authorUserLevel: Long = 0L

    @SerializedName("user_level")
    var userLevel: Long = 0L

    @SerializedName("author_name")
    var authorName: String = ""

    @SerializedName("author_profile_picture")
    var authorProfilePicture: String = ""

    @SerializedName("comment")
    var body: String = ""

    @SerializedName("likes_count")
    var likes: Long = 0L

    @SerializedName("is_pinned")
    var isPinned: Boolean = false

    @SerializedName("is_flagged")
    var isFlagged: Boolean = false

    @SerializedName("is_ambassador")
    var isAmbassador: Boolean = false

    @SerializedName("replies")
    var comments: List<CommentEntity> = emptyList()

    @SerializedName("comment_date_gmt")
    var commentDateGmt: String = ""

    var commentLocked: Boolean = false

    var totalReplies: Int = 0

    var permalink: String = ""

    fun getUserLevel(): UserPrivilegeLevel {
        return UserPrivilegeLevel.from(userLevel)
    }

    fun getAuthorUserLevel(): UserPrivilegeLevel {
        return UserPrivilegeLevel.from(authorUserLevel)
    }
}