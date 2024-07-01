package com.theathletic.comments.data

import androidx.compose.ui.graphics.Color
import com.theathletic.analytics.data.ClickSource
import com.theathletic.comments.analytics.CommentsLaunchAction
import com.theathletic.comments.analytics.CommentsParamModel
import com.theathletic.comments.ui.CommentsInputUiState
import com.theathletic.comments.ui.CommentsUiMapper
import com.theathletic.comments.ui.components.CommentsUi
import com.theathletic.comments.ui.components.InputHeaderData
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.data.ContentDescriptor
import com.theathletic.datetime.Datetime

fun commentsFeedFixture(
    comments: List<Comment> = emptyList(),
    commentCount: Int = 0,
    commentsLocked: Boolean = false,
    header: CommentsHeader = SimpleCommentsHeader("Comment Header Title"),
    timing: QandaTiming = QandaTiming(Datetime(1674067554000), Datetime(1674067554020))
) = CommentsFeed(
    comments,
    commentCount,
    commentsLocked,
    header,
    timing
)

fun commentFixture(
    authorId: String = "authorId-1",
    authorName: String = "Michael Bryant",
    authorUserLevel: Int = -1,
    avatarUrl: String = "",
    comment: String = "The developers of this app deserves all the best",
    commentLink: String = "",
    commentedAt: Long = 1674067554000,
    id: String = "commentId-1",
    isFlagged: Boolean = false,
    isPinned: Boolean = false,
    isAuthor: Boolean = false,
    likesCount: Int = 101,
    parentId: String = "commentId-1", // parent id == id means that comment has no parent
    replies: MutableList<Comment> = mutableListOf(),
    totalReplies: Int = 0,
) = Comment(
    authorId = authorId,
    authorName = authorName,
    authorUserLevel = authorUserLevel,
    avatarUrl = avatarUrl,
    comment = comment,
    commentLink = commentLink,
    commentedAt = commentedAt,
    id = id,
    isFlagged = isFlagged,
    isPinned = isPinned,
    isAuthor = isAuthor,
    likesCount = likesCount,
    parentId = parentId,
    replies = replies,
    totalReplies = totalReplies,
)

fun commentInputFixture(
    content: String = "Example of comment",
    sourceDescriptor: ContentDescriptor = ContentDescriptor("sourceId", "title"),
    sourceType: CommentsSourceType = CommentsSourceType.GAME,
    parentId: String = "parentId",
    teamId: String = "teamId"
) = CommentInput(
    content,
    sourceDescriptor,
    sourceType,
    parentId,
    teamId
)

fun commentsParamFixture(
    sourceDescriptor: ContentDescriptor = ContentDescriptor("sourceId", "title"),
    sourceType: CommentsSourceType = CommentsSourceType.GAME,
    isEntryActive: Boolean = false,
    launchAction: CommentsLaunchAction? = null,
    clickSource: ClickSource? = null
) = CommentsParamModel(
    sourceDescriptor = sourceDescriptor,
    sourceType = sourceType,
    isEntryActive = isEntryActive,
    launchAction = launchAction,
    clickSource = clickSource
)

fun headerFixture() = CommentsUi.HeaderModel.SimpleHeader("Comment Header Title")

fun commentsUiModelFixture(
    commentsList: List<Comment>,
    commentsUiMapper: CommentsUiMapper,
    highlightedCommentId: String = "",
    backgroundColor: Color = Color.Unspecified,
    isUserStaff: Boolean = false
) = commentsList.mapNotNull { comment ->
    commentsUiMapper.toUiModel(
        comment = comment,
        highlightedCommentId = highlightedCommentId,
        backgroundColor = backgroundColor,
        isUserStaff = isUserStaff
    )
}

fun commentsInputUiStateFixture(
    isCommentEnabled: Boolean = true,
    inputText: String = "",
    inputHeaderData: InputHeaderData = InputHeaderData.EmptyHeaderData,
    editOrReplyId: String? = null,
    lockedComments: Boolean = false,
) = CommentsInputUiState(
    isCommentEnabled = isCommentEnabled,
    inputText = inputText,
    inputHeaderData = inputHeaderData,
    editOrReplyId = editOrReplyId,
    enableSend = inputText.isNotBlank(),
    lockedComments = lockedComments
)