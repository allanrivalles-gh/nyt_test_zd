package com.theathletic.comments.data

import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.data.ContentDescriptor

data class CommentInput(
    val content: String,
    val sourceDescriptor: ContentDescriptor,
    val sourceType: CommentsSourceType,
    val parentId: String = "",
    val teamId: String = ""
)