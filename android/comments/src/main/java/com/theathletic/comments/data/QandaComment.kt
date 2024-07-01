package com.theathletic.comments.data

data class QandaComment(
    val commentId: String,
    val authorId: String,
    val authorName: String,
    val authorUserLevel: Int,
    val authorAvatarUrl: String,
    val parentCommentId: String?,
    val parentUserId: String?
)