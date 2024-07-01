package com.theathletic.comments.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isUnspecified
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.comments.R
import com.theathletic.comments.data.Comment
import com.theathletic.comments.data.CommentsFeed
import com.theathletic.comments.data.CommentsHeader
import com.theathletic.comments.data.DiscussionHeaderTag
import com.theathletic.comments.data.ExcerptCommentsHeader
import com.theathletic.comments.data.Flair
import com.theathletic.comments.data.QandaTiming
import com.theathletic.comments.data.SimpleCommentsHeader
import com.theathletic.comments.ui.components.CommentsUi
import com.theathletic.comments.utility.CommentsDateFormatter
import com.theathletic.datetime.DateUtility
import com.theathletic.datetime.Datetime
import com.theathletic.ui.utility.isLightContrast
import com.theathletic.ui.utility.parseHexColor
import com.theathletic.utility.LogoUtility
import java.util.Date

class CommentsUiMapper @AutoKoin constructor(
    private val dateFormatter: CommentsDateFormatter,
    private val dateUtility: DateUtility
) {
    fun toUiModel(
        commentFeed: CommentsFeed,
        highlightedCommentId: String? = null,
        isUserStaff: Boolean
    ): CommentsUiModel {
        val highlightedCommentIndex = commentFeed.comments.indexOfFirst { it.id == highlightedCommentId }.let { index ->
            if (index == -1) null else index
        }
        return CommentsUiModel(
            header = commentFeed.header?.toUiModel(),
            comments = commentFeed.comments.toUiModel(highlightedCommentId, commentFeed.backgroundColor, isUserStaff),
            commentsCount = commentFeed.commentsCount,
            highlightedCommentIndex = highlightedCommentIndex,
            backgroundColor = commentFeed.backgroundColor
        )
    }

    private fun List<Comment>?.toUiModel(
        highlightedCommentId: String?,
        backgroundColor: Color,
        isUserStaff: Boolean
    ) = this?.mapNotNull { comment ->
        val commentId = comment.id
        val isHighlighted = comment.id == highlightedCommentId
        when {
            comment.isUserReply -> comment.toUserReplyUiModel(isHighlighted, isUserStaff)
            comment.isStaffReply -> comment.toStaffReplyUiModel(isHighlighted, backgroundColor, isUserStaff)
            comment.isUser -> comment.toUserUiModel(isHighlighted, isUserStaff)
            comment.isStaff -> comment.toStaffUiModel(isHighlighted, backgroundColor, isUserStaff)
            else -> null
        }
    }.orEmpty()

    fun toUiModel(
        comment: Comment,
        highlightedCommentId: String? = null,
        backgroundColor: Color,
        isUserStaff: Boolean
    ): CommentsUi.Comments? {
        val isHighlighted = comment.id == highlightedCommentId
        return when {
            comment.isUserReply -> comment.toUserReplyUiModel(isHighlighted, isUserStaff)
            comment.isStaffReply -> comment.toStaffReplyUiModel(isHighlighted, backgroundColor, isUserStaff)
            comment.isUser -> comment.toUserUiModel(isHighlighted, isUserStaff)
            comment.isStaff -> comment.toStaffUiModel(isHighlighted, backgroundColor, isUserStaff)
            else -> null
        }
    }

    private fun Comment.toUserUiModel(
        isHighlighted: Boolean,
        isUserStaff: Boolean
    ) = CommentsUi.Comments.UserComment(
        commentId = id,
        parentId = parentId,
        commentText = comment,
        commentLink = commentLink,
        authorId = authorId,
        authorName = authorName,
        commentedAt = dateFormatter.format(Datetime(commentedAt)),
        isPinned = isPinned,
        hasUserLiked = hasUserLiked,
        likesCount = likesCount,
        replyCount = totalReplies,
        isHighlighted = isHighlighted,
        isAuthor = isAuthor,
        isDeletable = isDeletable && isUserStaff,
        commentMetadata = commentMetadata,
        authorFlairs = authorFlairs.toUiModel()
    )

    private fun Comment.toUserReplyUiModel(
        isHighlighted: Boolean,
        isUserStaff: Boolean,
    ) = CommentsUi.Comments.UserCommentReply(
        toUserUiModel(isHighlighted = isHighlighted, isUserStaff = isUserStaff)
    )

    private fun Comment.toStaffUiModel(
        isHighlighted: Boolean,
        backgroundColor: Color,
        isUserStaff: Boolean,
    ) = CommentsUi.Comments.StaffComment(
        commentInfo = toUserUiModel(isHighlighted = isHighlighted, isUserStaff = isUserStaff),
        avatarUrl = avatarUrl,
        backgroundColor = backgroundColor,
        colorSet = getColorSet(backgroundColor)
    )

    private fun Comment.toStaffReplyUiModel(
        isHighlighted: Boolean,
        backgroundColor: Color,
        isUserStaff: Boolean,
    ) = CommentsUi.Comments.StaffCommentReply(
        toStaffUiModel(
            isHighlighted = isHighlighted,
            backgroundColor = backgroundColor,
            isUserStaff = isUserStaff
        )
    )

    private fun CommentsHeader.toUiModel() = when (this) {
        is SimpleCommentsHeader -> CommentsUi.HeaderModel.SimpleHeader(text = title)
        is ExcerptCommentsHeader -> toHeaderUiModel(timing)
        else -> null
    }

    private fun ExcerptCommentsHeader.toHeaderUiModel(
        timing: QandaTiming?
    ) =
        CommentsUi.HeaderModel.Header(
            badgeUrl = getTags().firstOrNull { it.imageUrl.isNotEmpty() }?.imageUrl,
            labelRes = if (isDiscussion) R.string.comments_header_discussion else R.string.live_discussions_title,
            title = title,
            excerpt = excerpt,
            authorName = author,
            timeStamp = dateUtility.formatGMTTimeAgo(Date(timestamp.timeMillis)),
            backgroundColor = backgroundColorHex,
            liveTag = timing?.toLiveTagUiModel(),
        )

    private fun ExcerptCommentsHeader.getTags(): List<DiscussionHeaderTag> {
        val teamTags = teamIds.map {
            DiscussionHeaderTag(
                imageUrl = LogoUtility.getTeamSmallLogoPath(it),
                showImage = true
            )
        }
        val leagueTags = leagueIds.map {
            DiscussionHeaderTag(
                imageUrl = LogoUtility.getColoredLeagueLogoPath(it),
                showImage = true
            )
        }

        return when {
            teamTags.size + leagueTags.size <= 3 -> teamTags + leagueTags
            teamTags.size <= 3 -> teamTags
            leagueTags.size <= 3 -> leagueTags
            else -> teamTags
        }
    }

    private fun QandaTiming.toLiveTagUiModel(): CommentsUi.LiveTag? {
        return when {
            dateUtility.isInPastMoreThan(endTime.timeMillis, 0) ->
                CommentsUi.LiveTag(labelRes = R.string.community_topic_tag_ended)

            dateUtility.isInPastMoreThan(startTime.timeMillis, 0) ->
                CommentsUi.LiveTag(labelRes = R.string.community_topic_tag_live)

            else -> null
        }
    }

    private fun Flair.toUiModel() = CommentsUi.Comments.CommentFlair(
        title = title,
        contrastColor = contrastColor.parseHexColor()
    )

    private fun List<Flair>.toUiModel() = map { flair ->
        flair.toUiModel()
    }

    private fun getColorSet(backgroundColor: Color): CommentsUi.ColorSet? {
        return if (backgroundColor.isUnspecified) {
            null
        } else if (backgroundColor.isLightContrast()) {
            CommentsUi.ColorSet.lightColorSet
        } else {
            CommentsUi.ColorSet.darkColorSet
        }
    }
}