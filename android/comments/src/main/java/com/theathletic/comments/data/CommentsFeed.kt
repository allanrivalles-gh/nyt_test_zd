package com.theathletic.comments.data

import androidx.compose.ui.graphics.Color
import com.theathletic.datetime.Datetime
import com.theathletic.entity.user.SortType
import com.theathletic.ui.utility.parseHexColor

data class CommentsFeed(
    val comments: List<Comment> = emptyList(),
    val commentsCount: Int = 0,
    val commentsLocked: Boolean = true,
    val header: CommentsHeader? = null,
    val timing: QandaTiming? = null,
) {
    val backgroundColor
        get() = if (header is ExcerptCommentsHeader) {
            header.backgroundColorHex.parseHexColor()
        } else {
            Color.Unspecified
        }

    fun addComment(
        newComment: Comment,
        sortedBy: SortType
    ): CommentsFeedUpdate {
        val addedIndex: Int = if (newComment.isReply) {
            comments.indexOfLast { it.parentId == newComment.parentId } + 1
        } else {
            when (sortedBy) {
                SortType.MOST_LIKED, SortType.OLDEST, SortType.TRENDING -> comments.lastIndex.inc()
                SortType.NEWEST -> 0
            }
        }
        val updatedList = comments.toMutableList().apply {
            add(addedIndex, newComment)
        }.toList()

        return CommentsFeedUpdate(copy(comments = updatedList, commentsCount = updatedList.size), addedIndex)
    }

    fun deleteComment(commentId: String): CommentsFeedUpdate {
        val updatedList = comments.filterNot {
            it.id == commentId || it.parentId == commentId
        }.toList()

        return CommentsFeedUpdate(copy(comments = updatedList, commentsCount = updatedList.size))
    }

    fun editComment(
        commentId: String,
        newText: String,
    ): CommentsFeedUpdate {
        val indexedComment = comments.withIndex().firstOrNull { it.value.id == commentId }
        val editedIndex = indexedComment?.index ?: -1
        val updatedList = comments.toMutableList().apply {
            indexedComment?.let { indexedComment ->
                val newComment = indexedComment.value.copy(comment = newText)
                set(indexedComment.index, newComment)
                indexedComment.index
            }
        }.toList()

        return CommentsFeedUpdate(copy(comments = updatedList), editedIndex)
    }

    fun addLikeAction(commentId: String, likeAction: LikeAction): CommentsFeedUpdate {
        val indexedComment = comments.withIndex().firstOrNull { it.value.id == commentId }
        val likedIndex = indexedComment?.index ?: -1
        val updatedList = comments.toMutableList().apply {
            indexedComment?.let { indexedComment ->
                val newComment = indexedComment.value.updateLikes(likeAction)
                set(indexedComment.index, newComment)
            }
        }.toList()

        return CommentsFeedUpdate(updatedCommentsFeed = copy(comments = updatedList), likedIndex)
    }
}

data class CommentsFeedUpdate(
    val updatedCommentsFeed: CommentsFeed,
    val updatedIndex: Int = -1,
)

data class Comment(
    val authorId: String,
    val authorName: String,
    val authorUserLevel: Int,
    val avatarUrl: String? = null,
    val comment: String,
    val commentLink: String = "",
    val commentedAt: Long,
    val id: String,
    val isFlagged: Boolean = false,
    val isPinned: Boolean = false,
    val hasUserLiked: Boolean = false,
    val isAuthor: Boolean = false,
    val isDeletable: Boolean = false,
    var likesCount: Int = 0,
    val parentId: String,
    val replies: List<Comment> = emptyList(),
    val totalReplies: Int = 0,
    val commentMetadata: String? = null,
    val authorFlairs: List<Flair> = emptyList(),
) {
    val isReply = parentId != id
    val isStaff = authorUserLevel > 0
    val isUser = isStaff.not()
    val isStaffReply = isStaff && isReply
    val isUserReply = isUser && isReply
}

data class Flair(
    val id: String,
    val title: String,
    val contrastColor: String,
)

interface CommentsHeader {
    val title: String
}

data class SimpleCommentsHeader(
    override val title: String,
) : CommentsHeader

data class ExcerptCommentsHeader(
    override val title: String,
    val excerpt: String,
    val author: String,
    val timestamp: Datetime,
    val teamIds: List<Int>,
    val leagueIds: List<Int>,
    val inferredLeagueIds: List<Int>,
    val backgroundColorHex: String,
    val timing: QandaTiming?,
    val isDiscussion: Boolean,
) : CommentsHeader

data class QandaTiming(
    val startTime: Datetime,
    val endTime: Datetime,
)

private fun Comment.updateLikes(likeAction: LikeAction) = when (likeAction) {
    LikeAction.LIKE -> copy(hasUserLiked = true, likesCount = likesCount.inc())
    LikeAction.UNLIKE -> copy(hasUserLiked = false, likesCount = likesCount.dec())
}

enum class LikeAction {
    LIKE,
    UNLIKE
}