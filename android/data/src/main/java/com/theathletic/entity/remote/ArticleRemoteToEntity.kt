package com.theathletic.entity.remote

import com.theathletic.ArticleCommentsQuery
import com.theathletic.ArticleQuery
import com.theathletic.ArticleRelatedContentQuery
import com.theathletic.article.data.remote.SingleArticleFetcher
import com.theathletic.datetime.asGMTString
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.article.RelatedContent
import com.theathletic.entity.article.isDiscussionPost
import com.theathletic.entity.article.isHeadlinePost
import com.theathletic.entity.article.isQAndAPost
import com.theathletic.entity.discussions.CommentEntity
import com.theathletic.entity.main.FeedItemEntryType
import com.theathletic.fragment.Article
import com.theathletic.fragment.Discussion
import com.theathletic.fragment.FeedArticleLite
import com.theathletic.fragment.LiveBlogLite
import com.theathletic.fragment.Qanda
import com.theathletic.type.LiveStatus
import com.theathletic.utility.safeLet
import java.util.Date

fun Article.toEntity(
    entryType: FeedItemEntryType,
    scheduledDate: Long? = null
): ArticleEntity {
    val stringId = id
    val author = author.fragments.user
    return ArticleEntity(
        articleId = stringId.toLong(),
        articlePublishDate = Date(published_at).asGMTString(),
        articleTitle = title,
        articleHeaderImg = image_uri.toString(),
        excerpt = this@toEntity.excerpt_plaintext,
        permalink = this@toEntity.permalink,
        commentsCount = comment_count.toLong(),
        authorName = author.name,
        authorImg = author.asStaff?.fragments?.staff?.avatar_uri,
        authorDescription = author.asStaff?.fragments?.staff?.full_description,
        primaryTag = primary_tag,
        subscriberScore = subscriber_score,
        entryType = entryType,
        scheduledDate = scheduledDate?.asGMTString()
    )
}

fun Qanda.toEntity(): ArticleEntity {
    return ArticleEntity().also { entity ->
        entity.articleId = id.toLong()
        entity.articleTitle = title
        entity.articleHeaderImg = image_uri.toString()
        entity.authorName = author.fragments.user.name
        with(author.fragments.user.asStaff?.fragments?.staff) {
            entity.authorImg = this?.avatar_uri.orEmpty()
            entity.authorDescription = this?.description.orEmpty()
        }
        entity.commentsCount = comment_count.toLong()
        entity.articlePublishDate = Date(published_at).asGMTString()
        entity.startTimeGmt = started_at?.let { Date(it).asGMTString() }
        entity.endTimeGmt = ended_at?.let { Date(it).asGMTString() }
        entity.permalink = permalink
        entity.entryType = FeedItemEntryType.LIVE_DISCUSSION
    }
}

fun Discussion.toEntity(): ArticleEntity {
    return ArticleEntity().also { entity ->
        entity.articleId = id.toLong()
        entity.articleTitle = title
        entity.articleHeaderImg = image_uri.toString()
        entity.authorName = author.fragments.user.name
        entity.commentsCount = comment_count.toLong()
        entity.articlePublishDate = Date(published_at).asGMTString()
        entity.permalink = permalink
        entity.entryType = FeedItemEntryType.USER_DISCUSSION
    }
}

fun FeedArticleLite.toEntity(): ArticleEntity {
    return ArticleEntity(
        articleId = id.toLong(),
        articleTitle = title,
        articleHeaderImg = image_uri.toString(),
        articlePublishDate = Date(published_at).asGMTString(),
        commentsCount = comment_count.toLong(),
        excerpt = excerpt,
        permalink = permalink,
        entryType = FeedItemEntryType.ARTICLE,
        authorName = author.name,
        primaryTag = primary_tag_string
    )
}

fun ArticleQuery.ArticleById.adTargetingMap(): Map<String, String?> = mapOf(
    "als_test_clientside" to this.ad_targeting_params?.als_test_clientside,
    "auth" to this.ad_targeting_params?.auth,
    "byline" to this.ad_targeting_params?.byline,
    "coll" to this.ad_targeting_params?.coll,
    "gscat" to this.ad_targeting_params?.gscat,
    "id" to this.ad_targeting_params?.id,
    "keywords" to this.ad_targeting_params?.keywords,
    "org" to this.ad_targeting_params?.org,
    "plat" to this.ad_targeting_params?.plat,
    "prop" to this.ad_targeting_params?.prop,
    "tags" to this.ad_targeting_params?.tags,
    "typ" to this.ad_targeting_params?.typ,
    "tt" to this.ad_targeting_params?.tt
)

private fun ArticleRelatedContentQuery.Data.toLocalModal(): List<RelatedContent> {
    return articleRelatedContent.mapNotNull { relatedContent ->
        relatedContent.fragments.feedArticleLite?.let {
            return@mapNotNull it.toEntity().toRelatedContent()
        }
        relatedContent.fragments.liveBlogLite?.let {
            return@mapNotNull it.toRelatedContent()
        }
        null
    }
}

private fun ArticleEntity.toRelatedContent() = safeLet(articleTitle, articlePublishDate) { title, publishDate ->
    RelatedContent(
        id = id,
        timestampGmt = publishDate,
        title = title,
        excerpt = excerpt ?: "",
        imageUrl = articleHeaderImg ?: "",
        byline = authorName ?: "",
        commentCount = commentsCount.toInt(),
        isLive = false,
        contentType = when {
            isHeadlinePost -> RelatedContent.ContentType.HEADLINE
            isDiscussionPost -> RelatedContent.ContentType.DISCUSSION
            isQAndAPost -> RelatedContent.ContentType.QANDA
            else -> RelatedContent.ContentType.ARTICLE
        }
    )
}

private fun LiveBlogLite.toRelatedContent() = RelatedContent(
    id = id,
    timestampGmt = Date(lastActivityAt).asGMTString(),
    title = title,
    excerpt = "",
    imageUrl = images.firstOrNull()?.thumbnail_uri ?: "",
    byline = "",
    commentCount = 0,
    isLive = liveStatus == LiveStatus.live,
    contentType = RelatedContent.ContentType.LIVEBLOG
)

fun SingleArticleFetcher.ArticleDataWithExtensions.toLocalModel(): ArticleEntity {
    val article = articleData?.articleById?.let { remoteArticle ->
        ArticleEntity(
            articleId = remoteArticle.id.toLong(),
            articleTitle = remoteArticle.title,
            articleBody = remoteArticle.article_body ?: remoteArticle.article_body_mobile,
            articlePublishDate = remoteArticle.published_at.asGMTString(),
            authorId = remoteArticle.author.asStaff?.id?.toLong(),
            authorImg = remoteArticle.author.asStaff?.avatar_uri,
            authorName = remoteArticle.author.asStaff?.name,
            authors = remoteArticle.authors.map { it.author.name },
            articleHeaderImg = remoteArticle.image_uri,
            isTeaser = remoteArticle.is_teaser,
            teams = remoteArticle.team_urls,
            leagues = remoteArticle.league_urls,
            entryType = FeedItemEntryType.from(remoteArticle.type),
            postTypeId = remoteArticle.post_type_id?.toLong() ?: 0,
            entityKeywords = remoteArticle.entity_keywords,
            excerpt = remoteArticle.excerpt_plaintext,
            commentsCount = articleComments?.getComments?.comment_count?.toLong() ?: 0L,
            newsTopics = remoteArticle.news_topics,
            primaryTag = remoteArticle.primary_tag,
            permalink = remoteArticle.permalink,
            commentsDisabled = remoteArticle.disable_comments,
            commentsLocked = remoteArticle.lock_comments,
            ratingEnabled = remoteArticle.disable_nps.not(),
            leagueShortname = remoteArticle.primary_league_details?.shortname,
            leagueUrl = remoteArticle.primary_league_details?.url,
            sportType = remoteArticle.primary_league_details?.sport_type,
            adUnitPath = remoteArticle.ad_unit_path,
            adTargetingParams = remoteArticle.adTargetingMap()
        )
    }
        ?: cachedArticle
        ?: return ArticleEntity()

    val comments = articleComments?.getComments?.comments?.take(3)?.map { toCommentEntity(it) }
    val relatedContent = articleExtension?.toLocalModal()?.toMutableList()

    return article.copy(
        relatedContent = relatedContent,
        comments = comments,
    )
}

private fun toCommentEntity(remoteComment: ArticleCommentsQuery.Comment): CommentEntity {
    return CommentEntity().apply {
        commentId = remoteComment.id.toLong()
        permalink = remoteComment.comment_permalink ?: ""
        authorUserLevel = remoteComment.author_user_level.toLong()
        authorName = remoteComment.author_name
        body = remoteComment.comment
        likes = remoteComment.likes_count.toLong()
        commentDateGmt = remoteComment.commented_at.asGMTString()
        totalReplies = remoteComment.total_replies
    }
}