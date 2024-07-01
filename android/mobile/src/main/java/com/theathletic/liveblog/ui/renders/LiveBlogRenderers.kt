package com.theathletic.liveblog.ui.renders

import com.theathletic.ads.data.local.AdLocalModel
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.formatter.TimeAgoShortDateFormatter
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.liveblog.data.local.NativeLiveBlog
import com.theathletic.liveblog.data.local.NativeLiveBlogDropzone
import com.theathletic.liveblog.data.local.NativeLiveBlogPost
import com.theathletic.liveblog.data.local.NativeLiveBlogPostBanner
import com.theathletic.liveblog.data.local.NativeLiveBlogPostBasic
import com.theathletic.liveblog.data.local.NativeLiveBlogPostSponsored
import com.theathletic.liveblog.data.local.NativeLiveBlogSponsorImage
import com.theathletic.liveblog.ui.LiveBlogUi
import com.theathletic.ui.binding.toResourceString
import com.theathletic.utility.formatters.CommentsCountNumberFormat
import com.theathletic.utility.safeLet

class LiveBlogRenderers @AutoKoin constructor(
    private val timeAgoShortDateFormatter: TimeAgoShortDateFormatter
) {
    fun renderLiveBlog(
        liveBlog: NativeLiveBlog?,
        tweetUrlToHtml: Map<String, String>,
        adMap: Map<String, AdLocalModel>,
    ) = LiveBlogUi.LiveBlog(
        id = liveBlog?.id.orEmpty(),
        title = liveBlog?.title.orEmpty(),
        publishedAt = timeAgoShortDateFormatter.format(
            datetime = liveBlog?.lastActivityAt ?: Datetime(0),
            params = TimeAgoShortDateFormatter.Params(showUpdated = true)
        ).toResourceString(),
        authorId = liveBlog?.authorId?.toLongOrNull() ?: -1,
        authorName = liveBlog?.authorName.orEmpty(),
        imageUrl = liveBlog?.imageUrl.orEmpty(),
        description = liveBlog?.description.orEmpty(),
        permalink = liveBlog?.permalink.orEmpty(),
        linkForEmbed = liveBlog?.contentUrl.orEmpty(),
        posts = liveBlog?.posts?.mapNotNull { it.toUiModel(tweetUrlToHtml, adMap) } ?: emptyList(),
        tweets = liveBlog?.tweetUrls?.map { tweetUrlToHtml.getTweet(it) } ?: emptyList(),
        hasNextPage = liveBlog?.hasNextPage ?: false,
        sponsorPresentedBy = liveBlog?.sponsorPresentedBy?.toUiModel(),
        sponsorBanner = liveBlog?.sponsorBanner?.toUiModel(),
        isLive = liveBlog?.isLive ?: false
    )

    private fun NativeLiveBlogPost.toUiModel(
        tweetUrlToHtml: Map<String, String>,
        adMap: Map<String, AdLocalModel>
    ): LiveBlogUi.LiveBlogPost? {
        return when (this) {
            is NativeLiveBlogPostBasic -> this.toPost(tweetUrlToHtml)
            is NativeLiveBlogPostBanner -> this.toPost()
            is NativeLiveBlogPostSponsored -> this.toPost()
            is NativeLiveBlogDropzone -> this.toPost(adMap)
            else -> null
        }
    }

    private fun NativeLiveBlogPostBanner.toPost() = LiveBlogUi.LiveBlogPostBanner(
        id = id,
        sponsorBanner = LiveBlogUi.SponsorImage(
            imageUriLight = bannerImage.imageUriLight,
            imageUriDark = bannerImage.imageUriDark
        )
    )

    private fun NativeLiveBlogPostBasic.toPost(tweetUrlToHtml: Map<String, String?>) = LiveBlogUi.LiveBlogPostBasic(
        id = id,
        title = title,
        description = body,
        imageUrl = imageUrl.orEmpty(),
        publishedAt = timeAgoShortDateFormatter.format(
            datetime = publishedAt
        ).toResourceString(),
        authorId = author?.id?.toLongOrNull() ?: -1,
        authorName = author?.name.orEmpty(),
        authorDescription = author?.description.orEmpty(),
        avatarUrl = author?.avatarUri.orEmpty(),
        relatedArticles = articles.map { it.toRelatedArticle() },
        tweets = tweetUrls.map { tweetUrlToHtml.getTweet(it) }
    )

    private fun NativeLiveBlogSponsorImage.toUiModel() = LiveBlogUi.SponsorImage(
        imageUriLight = imageUriLight,
        imageUriDark = imageUriDark,
        label = label
    )

    private fun NativeLiveBlogPostSponsored.toPost(): LiveBlogUi.LiveBlogPostSponsored? {
        return safeLet(article.articleTitle, article.articleHeaderImg) { title, image ->
            LiveBlogUi.LiveBlogPostSponsored(
                id = id,
                articleId = article.id,
                title = title,
                excerpt = article.excerpt ?: "",
                imageUrl = image,
                updatedAt = timeAgoShortDateFormatter.format(
                    datetime = publishedAt
                ).toResourceString(),
                sponsorPresentedBy = sponsorPresentedBy?.toUiModel()
            )
        }
    }

    private fun NativeLiveBlogDropzone.toPost(adMap: Map<String, AdLocalModel>): LiveBlogUi.LiveBlogDropzone? {
        val ad = adMap[dropzoneId] ?: return null
        if (ad.collapsed) return null
        return LiveBlogUi.LiveBlogDropzone(
            id = id,
            dropzoneId = dropzoneId,
            type = type,
            ad = ad.adView?.view
        )
    }

    private fun ArticleEntity.toRelatedArticle() = LiveBlogUi.RelatedArticle(
        id = articleId,
        title = articleTitle.orEmpty(),
        authorName = authorName.orEmpty(),
        imageUrl = articleHeaderImg.orEmpty(),
        commentCount = CommentsCountNumberFormat.format(commentsCount),
        showComments = commentsCount > 0
    )

    private fun Map<String, String?>.getTweet(url: String) = LiveBlogUi.Tweet(url, get(url))
}