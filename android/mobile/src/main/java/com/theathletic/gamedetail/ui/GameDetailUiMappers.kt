package com.theathletic.gamedetail.ui

import com.theathletic.comments.ui.components.CommentsUi
import com.theathletic.comments.utility.CommentsDateFormatter
import com.theathletic.datetime.Datetime
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.ui.utility.parseHexColor

fun GameDetailLocalModel.TopComment.toCommentUiModel(
    dateFormatter: CommentsDateFormatter,
    isLiked: Boolean
): CommentsUi.Comments {
    return if (isStaff) {
        toStaffUiModel(dateFormatter, isLiked)
    } else {
        toUserUiModel(dateFormatter, isLiked)
    }
}

private fun GameDetailLocalModel.TopComment.toUserUiModel(
    dateFormatter: CommentsDateFormatter,
    hasUserLiked: Boolean
) = CommentsUi.Comments.UserComment(
    commentId = id,
    parentId = parentId,
    commentText = comment,
    commentLink = permalink,
    authorId = "",
    authorName = authorName,
    commentedAt = dateFormatter.format(Datetime(commentedAt)),
    isPinned = false,
    hasUserLiked = hasUserLiked,
    likesCount = likesCount,
    replyCount = 0,
    isHighlighted = false,
    isAuthor = false,
    isDeletable = false,
    commentMetadata = commentMetadata,
    authorFlairs = authorGameFlairs.map { it.toFlairsUiModel() }
)

private fun GameDetailLocalModel.TopComment.toStaffUiModel(
    dateFormatter: CommentsDateFormatter,
    hasUserLiked: Boolean
) = CommentsUi.Comments.StaffComment(
    commentInfo = toUserUiModel(dateFormatter, hasUserLiked),
    avatarUrl = avatarUrl
)

private fun GameDetailLocalModel.AuthorGameFlair.toFlairsUiModel() =
    CommentsUi.Comments.CommentFlair(name, iconContrastColor.parseHexColor())