package com.theathletic.news.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.theathletic.DeleteCommentMutation
import com.theathletic.EditCommentMutation
import com.theathletic.FlagCommentMutation
import com.theathletic.HeadlineCommentCountQuery
import com.theathletic.LikeCommentMutation
import com.theathletic.NewsByIdQuery
import com.theathletic.UnlikeCommentMutation
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.asGMTString
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.main.FeedItemEntryType
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.fragment.Article
import com.theathletic.fragment.Comment
import com.theathletic.fragment.Insight
import com.theathletic.fragment.NewsBrief
import com.theathletic.fragment.NewsItem
import com.theathletic.fragment.PodcastEpisode
import com.theathletic.news.News
import com.theathletic.news.NewsComment
import com.theathletic.news.NewsContent
import com.theathletic.news.NewsImage
import com.theathletic.news.NewsInsight
import java.util.Date

fun mapApolloNewsByIdResponse(fromApollo: ApolloResponse<NewsByIdQuery.Data>): News? {
    return mapApolloNewsToNews(fromApollo.data?.newsById?.fragments?.newsItem)
}

fun mapApolloDeleteCommentResponseSuccess(fromApollo: ApolloResponse<DeleteCommentMutation.Data>): Boolean? {
    return fromApollo.data?.deleteComment
}

fun mapApolloEditCommentResponseSuccess(fromApollo: ApolloResponse<EditCommentMutation.Data>): Boolean? {
    return fromApollo.data?.editComment
}

fun mapApolloFlagCommentResponseSuccess(fromApollo: ApolloResponse<FlagCommentMutation.Data>): Boolean? {
    return fromApollo.data?.flagComment
}

fun mapApolloLikeCommentResponseSuccess(fromApollo: ApolloResponse<LikeCommentMutation.Data>): Boolean? {
    return fromApollo.data?.likeComment
}

fun mapApolloUnlikeCommentResponseSuccess(fromApollo: ApolloResponse<UnlikeCommentMutation.Data>): Boolean? {
    return fromApollo.data?.unlikeComment
}

fun ApolloResponse<HeadlineCommentCountQuery.Data>.toLocalModel(): Int? {
    return data?.newsById?.comment_count
}

private fun mapApolloNewsToNews(newsItem: NewsItem?): News? {
    return newsItem?.let { item ->
        News(
            allowComments = !item.disable_comments,
            lockedComments = item.lock_comments,
            commentsCount = item.comment_count,
            content = mapApolloNewsContentsToContents(item),
            lede = item.lede.toString(),
            following = item.following,
            headline = item.headline,
            id = item.id,
            images = mapApolloImageToImage(item),
            importance = item.importance,
            smartBrevity = item.smart_brevity,
            status = item.status,
            type = item.type,
            createdAt = Datetime(item.created_at),
            updatedAt = Datetime(item.last_activity_at),
            user = mapApolloUserToUser(item.user.fragments.user),
            permalink = item.permalink,
            bylineHtml = item.byline_linkable?.app_linked_string ?: item.byline
        )
    }
}

private fun mapApolloImageToImage(newsContent: NewsItem): List<NewsImage> {
    return newsContent.images
        .map { it.fragments.newsImage }
        .map { img ->
            NewsImage(
                imageWidth = img.image_width,
                imageHeight = img.image_height,
                imageUrl = img.image_uri,
                thumbnailWidth = img.thumbnail_width,
                thumbnailHeight = img.thumbnail_height,
                thumbnailUrl = img.thumbnail_uri
            )
        }
}

@Suppress("LongMethod")
private fun mapApolloNewsContentsToContents(newsContent: NewsItem): ArrayList<NewsContent>? {
    val contentList = ArrayList<NewsContent>()
    newsContent.content?.forEach {
        it?.let { contentItem ->
            when (contentItem.__typename) {
                "Insight" -> {
                    contentItem.asInsight?.fragments?.insight?.let { insight ->
                        contentList.add(populateNewsInsight(insight))
                    }
                }

                "Brief" -> {
                    contentItem.asBrief?.fragments?.newsBrief?.let { brief ->
                        contentList.add(brief.toLocalModel())
                    }
                }
                else -> { /* Not Supported Yet! */ }
            }
        }
    }

    return if (contentList.isNullOrEmpty()) null else contentList
}

private fun populateNewsInsight(insight: Insight): NewsInsight {
    return NewsInsight(
        id = insight.id,
        type = insight.type,
        createdAt = insight.created_at.toString(),
        updatedAt = insight.updated_at.toString(),
        status = insight.status,
        audioUrl = insight.audio_uri,
        images = mapApolloInsightImageToNewsImage(insight),
        text = insight.text,
        user = mapApolloUserToUser(insight.user.fragments.user)
    )
}

private fun NewsBrief.toLocalModel(): NewsInsight {
    return NewsInsight(
        id = id,
        type = type,
        createdAt = created_at.toString(),
        updatedAt = updated_at.toString(),
        status = status,
        audioUrl = audio_uri,
        images = images.toNewsImageList(),
        text = text,
        user = mapApolloUserToUser(user.fragments.user)
    )
}

private fun mapApolloInsightImageToNewsImage(insight: Insight): List<NewsImage>? {
    return insight.images
        .map { it.fragments.newsImage }
        .map { img ->
            NewsImage(
                imageWidth = img.image_width,
                imageHeight = img.image_height,
                imageUrl = img.image_uri,
                thumbnailWidth = img.thumbnail_width,
                thumbnailHeight = img.thumbnail_height,
                thumbnailUrl = img.thumbnail_uri
            )
        }
}

private fun List<NewsBrief.Image>.toNewsImageList(): List<NewsImage>? {
    return this.map { it.fragments.newsImage }.map { img ->
        NewsImage(
            imageWidth = img.image_width,
            imageHeight = img.image_height,
            imageUrl = img.image_uri,
            thumbnailWidth = img.thumbnail_width,
            thumbnailHeight = img.thumbnail_height,
            thumbnailUrl = img.thumbnail_uri
        )
    }
}

fun Article.toEntity(entryType: FeedItemEntryType = FeedItemEntryType.ARTICLE): ArticleEntity {
    return ArticleEntity().apply {
        articleId = this@toEntity.id.toLong()
        articlePublishDate = Date(published_at).asGMTString()
        articleTitle = title
        articleHeaderImg = image_uri.toString()
        excerpt = this@toEntity.excerpt_plaintext
        permalink = this@toEntity.permalink
        commentsCount = comment_count.toLong()
        authorName = author.fragments.user.name
        authorImg = author.fragments.user.asStaff?.fragments?.staff?.avatar_uri
        authorDescription = author.fragments.user.asStaff?.fragments?.staff?.full_description
        this.entryType = entryType
    }
}

fun mapNewsPodcastToPodcastItem(podcast: PodcastEpisode?): PodcastEpisodeItem {
    return PodcastEpisodeItem().apply {
        podcast?.let { podcast ->
            id = podcast.id.toLong()
            podcastId = podcast.podcast_id.toLong()
            title = podcast.title
            description = podcast.description
            dateGmt = Date(podcast.published_at).asGMTString()
            duration = podcast.duration.toLong()
            numberOfComments = podcast.comment_count
            mp3Url = podcast.mp3_uri
            imageUrl = podcast.image_uri
            permalinkUrl = podcast.permalink
            isTeaser = podcast.is_teaser
        }
    }
}

fun PodcastEpisode.toLocalModel() = mapNewsPodcastToPodcastItem(this)

// todo: Remove this function after the comments refactoring is completed
fun mapApolloCommentToComment(comment: Comment): NewsComment {
    return NewsComment(
        authorId = comment.author_id,
        authorName = comment.author_name,
        authorUserLevel = comment.author_user_level,
        avatarUrl = comment.avatar_url,
        comment = comment.comment_as_markdown,
        commentedAt = comment.commented_at.toString(),
        id = comment.id,
        isFlagged = comment.is_flagged,
        isPinned = comment.is_pinned,
        likesCount = comment.likes_count,
        parentId = comment.parent_id,
        totalReplies = comment.total_replies,
        replies = mapApolloReplyToNewsComment(comment)?.toMutableList()
    )
}

// todo: Remove this function after the comments refactoring is completed
private fun mapApolloReplyToNewsComment(comment: Comment): List<NewsComment>? {
    return comment.replies?.map { reply ->
        NewsComment(
            authorId = reply.author_id,
            authorName = reply.author_name,
            authorUserLevel = reply.author_user_level,
            avatarUrl = reply.avatar_url,
            comment = reply.comment,
            commentedAt = reply.commented_at.toString(),
            id = reply.id,
            isFlagged = reply.is_flagged,
            isPinned = reply.is_pinned,
            likesCount = reply.likes_count,
            parentId = reply.parent_id,
            totalReplies = reply.total_replies
        )
    }
}