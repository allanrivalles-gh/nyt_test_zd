package com.theathletic.article.ui

import com.theathletic.analytics.data.ClickSource
import com.theathletic.analytics.data.ContentType
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.comments.analytics.isArticle
import com.theathletic.comments.analytics.isHeadline
import com.theathletic.comments.analytics.isPodcast
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.article.isHeadlinePost
import com.theathletic.entity.user.SortType
import com.theathletic.news.container.HeadlineContainerAnalytics

class ArticleAnalytics @AutoKoin constructor(
    private val analytics: Analytics,
    private val headlineAnalytics: HeadlineContainerAnalytics
) {
    private var hasTrackedArticleView: Boolean = false
    private var hasTrackedRelatedArticleImpression: Boolean = false
    private val nytStore = "store.nytimes.com/products"
    private val athleticCampaign = "campaign.theathletic.com"

    fun trackShareClick(article: ArticleEntity?) {
        if (article.isInvalidArticle) return

        analytics.track(
            Event.Global.GenericShare(
                method = "Link",
                content_type = article?.articleTitle.orEmpty(),
                content = ContentType.ARTICLE.value,
                item_id = article?.articleId.toString()
            )
        )
        analytics.track(
            Event.Article.BottomBarShareBegins(
                object_id = article?.articleId.toString()
            )
        )
    }

    fun trackViewMoreComments(article: ArticleEntity?) {
        if (article.isInvalidArticle) return

        analytics.track(
            Event.Comments.ViewMoreClick(
                view = "article",
                object_type = "article_id",
                object_id = article?.articleId.toString()
            )
        )
    }

    fun trackRelatedArticleImpression(article: ArticleEntity?, percentInView: Float) {
        if (article.isInvalidArticle) return

        if (percentInView > 0f && !hasTrackedRelatedArticleImpression) {
            hasTrackedRelatedArticleImpression = true
            analytics.track(Event.Article.RecommendedView())
        }
    }

    fun trackShareComplete(article: ArticleEntity?) {
        if (article.isInvalidArticle) return

        analytics.track(
            Event.Article.BottomBarShareComplete(
                object_id = article?.articleId.toString()
            )
        )
    }

    fun trackArticleView(
        article: ArticleEntity?,
        showPaywall: Boolean,
        source: String
    ) {
        if (hasTrackedArticleView || article == null) return
        when {
            article.isHeadlinePost -> headlineAnalytics.trackView(article.articleId.toString(), source)
            showPaywall -> analytics.track(Event.Article.PaywallView)
            else -> {
                analytics.track(
                    Event.Global.View(
                        ContentType.ARTICLE.toString(),
                        article.articleId.toString()
                    )
                )
            }
        }
        hasTrackedArticleView = true
    }

    fun trackAdOnLoad(
        article: ArticleEntity?,
        pageViewId: String
    ) {
        if (article.isInvalidArticle) return
        analytics.track(
            Event.Global.AdOnLoad(
                view = "article",
                ad_view_id = pageViewId
            )
        )
    }

    fun trackFreeArticleRead(article: ArticleEntity?) {
        if (article.isInvalidArticle) return
        analytics.track(Event.Article.FreeArticleRead)
    }

    fun trackArticleRead(
        article: ArticleEntity?,
        showPaywall: Boolean,
        percentRead: Int,
        source: String
    ) {
        if (article == null) return
        if (showPaywall) {
            analytics.track(
                Event.Article.View(
                    article_id = article.articleId.toString(),
                    has_paywall = "1",
                    percent_read = "",
                    source = ClickSource.PAYWALL.toString(),
                    paywall_type = "no_plan_paywall"
                )
            )
        } else {
            analytics.track(
                Event.Article.View(
                    article_id = article.articleId.toString(),
                    percent_read = percentRead.toString(),
                    source = source
                )
            )
        }
    }

    private val ArticleEntity?.isInvalidArticle: Boolean
        get() = this == null || this.isHeadlinePost

    fun trackRoomMiniPlayerClicked(article: ArticleEntity?) {
        if (article.isInvalidArticle) return
        analytics.track(
            Event.LiveRoom.Click(
                view = "liveroom_miniplayer",
                element = "open",
                object_type = "room_id",
                object_id = article?.articleId.toString()
            )
        )
    }

    fun trackOnRoomCloseClick(article: ArticleEntity?) {
        if (article.isInvalidArticle) return
        analytics.track(
            Event.LiveRoom.Click(
                view = "liveroom_miniplayer",
                element = "close",
                object_type = "room_id",
                object_id = article?.articleId.toString()
            )
        )
    }

    fun trackTextStyleClick(article: ArticleEntity?) {
        article?.let {
            if (it.isHeadlinePost) {
                headlineAnalytics.trackTextStyleClick(it.id)
            } else {
                analytics.track(Event.Article.TextStyleClick(object_id = it.id))
            }
        }
    }

    fun trackCommentsClick(article: ArticleEntity?) {
        article?.let {
            if (it.isHeadlinePost) {
                headlineAnalytics.trackCommentsOpen(it.id)
            } else {
                analytics.track(
                    Event.Comments.CommentsClick(
                        view = "article",
                        object_type = "article_id",
                        object_id = it.id
                    )
                )
            }
        }
    }

    fun trackLikeCommentAction(
        commentId: String,
        sourceId: String,
        sourceType: CommentsSourceType,
        filterType: SortType,
        index: Int,
        isLike: Boolean,
    ) {
        if (isLike) {
            trackLikeComment(commentId, sourceType, sourceId, filterType, index)
        } else {
            trackUnlikeComment(commentId, sourceType, sourceId, filterType, index)
        }
    }

    private fun trackLikeComment(
        commentId: String,
        sourceType: CommentsSourceType,
        sourceId: String,
        filterType: SortType,
        index: Int
    ) {
        analytics.track(
            Event.Comments.Like(
                view = "article",
                object_type = "comment_id",
                object_id = commentId,
                article_id = if (sourceType.isArticle) sourceId else "",
                podcast_episode_id = if (sourceType.isPodcast) sourceId else "",
                headline_id = if (sourceType.isHeadline) sourceId else "",
                filter_type = filterType.value,
                v_index = index.toString()
            )
        )
    }

    private fun trackUnlikeComment(
        commentId: String,
        sourceType: CommentsSourceType,
        sourceId: String,
        filterType: SortType,
        index: Int
    ) {
        analytics.track(
            Event.Comments.Unlike(
                view = "article",
                object_type = "comment_id",
                object_id = commentId,
                article_id = if (sourceType.isArticle) sourceId else "",
                podcast_episode_id = if (sourceType.isPodcast) sourceId else "",
                headline_id = if (sourceType.isHeadline) sourceId else "",
                filter_type = filterType.value,
                v_index = index.toString()
            )
        )
    }

    fun trackFlagComment(commentId: String, filterType: SortType, index: Int) {
        analytics.track(
            Event.Comments.Flag(
                view = "article",
                element = "comment",
                object_type = "comment_id",
                object_id = commentId,
                filter_type = filterType.value,
                v_index = index.toString()
            )
        )
    }

    fun trackExternalUrl(articleId: String, clickSource: String, url: String) {
        when {
            isInContentModuleUrl(url) -> trackInContentModuleUrl(articleId, clickSource, url)
            else -> {}
        }
    }

    private fun trackInContentModuleUrl(articleId: String, clickSource: String, url: String) {
        val inContentAnalytics = InContentModuleAnalytics.fromUrl(url)

        analytics.track(
            Event.Article.InContentModuleClick(
                view = clickSource.toInContentAnalytics(),
                object_id = articleId,
                product_id = inContentAnalytics.productId,
                module_type = inContentAnalytics.moduleType
            )
        )
    }
    private fun isInContentModuleUrl(url: String) = url.contains(nytStore) || url.contains(athleticCampaign)

    enum class InContentModuleAnalytics(val productId: String, val moduleType: String) {
        FOOTBALL_100("nfl100", "book"),
        UNKNOWN("", "");

        companion object {
            fun fromUrl(url: String) = when {
                url.contains("football-100") -> FOOTBALL_100
                else -> UNKNOWN
            }
        }
    }
}

private fun String.toInContentAnalytics() = this.replace(" ", "").lowercase()