package com.theathletic.comments.data

import com.theathletic.AddNewCommentMutation
import com.theathletic.CommentsForArticleQuery
import com.theathletic.CommentsForDiscussionQuery
import com.theathletic.CommentsForGameQuery
import com.theathletic.CommentsForHeadlineQuery
import com.theathletic.CommentsForPodcastEpisodeQuery
import com.theathletic.CommentsForQandaQuery
import com.theathletic.datetime.Datetime
import com.theathletic.fragment.Flairs

private typealias RemoteComment = com.theathletic.fragment.Comment

fun CommentsForHeadlineQuery.Data.toDomain() = CommentsFeed(
    comments = this.getComments.comments.map { it.fragments.comment.toDomain() },
    commentsCount = this.newsById?.fragments?.newsItem?.comment_count ?: 0,
    commentsLocked = this.newsById?.fragments?.newsItem?.lock_comments ?: false
)

fun CommentsForArticleQuery.Data.toDomain() = CommentsFeed(
    comments = getComments.comments.map { it.fragments.comment.toDomain() },
    commentsCount = getComments.comment_count,
    commentsLocked = articleById?.lock_comments ?: false,
    header = SimpleCommentsHeader(articleById?.title.orEmpty())
)

fun CommentsForPodcastEpisodeQuery.Data.toDomain() = CommentsFeed(
    comments = getComments.comments.map { it.fragments.comment.toDomain() },
    commentsCount = podcastEpisodeById?.comment_count ?: 0,
    commentsLocked = false,
    header = SimpleCommentsHeader(podcastEpisodeById?.title.orEmpty())
)

fun CommentsForDiscussionQuery.Data.toDomain(): CommentsFeed? {
    val discussion = articleById ?: return null

    return CommentsFeed(
        comments = getComments.comments.map { it.fragments.comment.toDomain() },
        commentsCount = discussion.comment_count,
        commentsLocked = discussion.lock_comments,
        header = ExcerptCommentsHeader(
            title = discussion.title,
            excerpt = discussion.excerpt,
            author = discussion.author.name,
            timestamp = Datetime(discussion.published_at),
            teamIds = discussion.team_ids?.mapNotNull { it?.toInt() } ?: emptyList(),
            leagueIds = discussion.league_ids?.mapNotNull { it?.toInt() } ?: emptyList(),
            inferredLeagueIds = discussion.inferred_league_ids?.mapNotNull { it?.toInt() } ?: emptyList(),
            backgroundColorHex = discussion.team_hex?.split(",")?.firstOrNull().orEmpty(),
            timing = null,
            isDiscussion = true
        )
    )
}

fun CommentsForQandaQuery.Data.toDomain(): CommentsFeed? {
    val article = articleById ?: return null
    val qanda = qandaById ?: return null
    return CommentsFeed(
        comments = getComments.comments.map { it.fragments.comment.toDomain() },
        commentsCount = article.comment_count,
        commentsLocked = article.lock_comments,
        header = ExcerptCommentsHeader(
            title = article.title,
            excerpt = article.excerpt_plaintext,
            author = article.author.name,
            timestamp = Datetime(article.published_at),
            teamIds = article.team_ids?.mapNotNull { it?.toInt() } ?: emptyList(),
            leagueIds = article.league_ids?.mapNotNull { it?.toInt() } ?: emptyList(),
            inferredLeagueIds = article.inferred_league_ids?.mapNotNull { it?.toInt() } ?: emptyList(),
            backgroundColorHex = article.team_hex?.split(",")?.firstOrNull().orEmpty(),
            isDiscussion = false,
            timing = qanda.toDomain()
        ),
        timing = qanda.toDomain()
    )
}

private fun CommentsForQandaQuery.QandaById.toDomain() = QandaTiming(
    startTime = Datetime(started_at ?: 0),
    endTime = Datetime(ended_at ?: 0)
)

fun CommentsForGameQuery.Data.toDomain() = CommentsFeed(
    comments = getComments.comments.map { it.toDomain() },
    commentsCount = getComments.comment_count,
    commentsLocked = false,
)

fun CommentsForGameQuery.Comment.toDomain(): Comment {
    val flairs = fragments.flairs.author_game_flairs.filterNotNull().map { it.toDomain() }
    val replyFlairs = replies?.associate { it.id to it.fragments.flairs }.orEmpty()
    return fragments.comment.toDomain(flairs, replyFlairs)
}

fun Flairs.Author_game_flair.toDomain() = Flair(
    id = id,
    title = this.name,
    contrastColor = this.icon_contrast_color
)

fun AddNewCommentMutation.AddNewComment.toDomain(isAuthor: Boolean = false): Comment {
    val flairs = fragments.flairs.author_game_flairs.filterNotNull().map { it.toDomain() }
    return fragments.comment.toDomain(flairs).copy(isAuthor = isAuthor)
}

fun RemoteComment.toDomain(
    flairs: List<Flair> = emptyList(),
    replyFlairs: Map<String, Flairs> = emptyMap()
) = Comment(
    authorId = this.author_id,
    authorName = this.author_name,
    authorUserLevel = this.author_user_level,
    avatarUrl = this.avatar_url,
    comment = this.comment_as_markdown,
    commentLink = this.comment_permalink.orEmpty(),
    commentedAt = this.commented_at,
    id = this.id,
    isFlagged = this.is_flagged,
    isPinned = this.is_pinned,
    isDeletable = this.is_deletable,
    likesCount = this.likes_count,
    parentId = if (this.parent_id == "0") this.id else this.parent_id,
    totalReplies = this.total_replies,
    commentMetadata = this.comment_metadata,
    replies = mapApolloReplyToComment(this, replyFlairs),
    authorFlairs = flairs
)

private fun mapApolloReplyToComment(
    comment: RemoteComment,
    replyFlairs: Map<String, Flairs> = emptyMap()
): List<Comment> {
    return comment.replies?.map { reply ->
        Comment(
            authorId = reply.author_id,
            authorName = reply.author_name,
            authorUserLevel = reply.author_user_level,
            avatarUrl = reply.avatar_url,
            comment = reply.comment,
            commentLink = reply.comment_permalink.orEmpty(),
            commentedAt = reply.commented_at,
            id = reply.id,
            isFlagged = reply.is_flagged,
            isPinned = reply.is_pinned,
            isDeletable = reply.is_deletable,
            likesCount = reply.likes_count,
            parentId = reply.parent_id,
            commentMetadata = reply.comment_metadata,
            totalReplies = reply.total_replies,
            authorFlairs = replyFlairs[reply.id]?.author_game_flairs?.filterNotNull()?.map { it.toDomain() }.orEmpty()
        )
    } ?: emptyList()
}