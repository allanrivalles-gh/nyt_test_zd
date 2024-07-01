package com.theathletic.comments.analytics

import com.theathletic.analytics.data.ClickSource
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.data.ContentDescriptor
import java.io.Serializable

data class CommentsParamModel constructor(
    val sourceDescriptor: ContentDescriptor,
    val sourceType: CommentsSourceType,
    val isEntryActive: Boolean,
    val launchAction: CommentsLaunchAction?,
    val analyticsPayload: CommentsAnalyticsPayload? = null,
    val clickSource: ClickSource?,
)

sealed class CommentsLaunchAction(open val commentId: String) : Serializable {
    data class Edit(override val commentId: String) : CommentsLaunchAction(commentId)
    data class Reply(val parentId: String, override val commentId: String) : CommentsLaunchAction(commentId)
    data class View(override val commentId: String) : CommentsLaunchAction(commentId)
}