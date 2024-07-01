package com.theathletic.article.ui

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.article.ArticleAuthorModel
import com.theathletic.article.ArticleComment
import com.theathletic.article.ArticleCommentsNotLoaded
import com.theathletic.article.ArticleCommentsTitle
import com.theathletic.article.ArticleContentModel
import com.theathletic.article.ArticleDisabledComments
import com.theathletic.article.ArticleFreeUserUpsell
import com.theathletic.article.ArticleImageModel
import com.theathletic.article.ArticlePaywallCTAModel
import com.theathletic.article.ArticlePaywallContentModel
import com.theathletic.article.ArticleRatedImage
import com.theathletic.article.ArticleRatingButtons
import com.theathletic.article.ArticleRatingPadding
import com.theathletic.article.ArticleRatingTitle
import com.theathletic.article.ArticleTitleModel
import com.theathletic.article.ArticleToolbarModel
import com.theathletic.article.ArticleViewMoreComments
import com.theathletic.datetime.DateUtility
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.article.ArticleRating
import com.theathletic.entity.article.RelatedContent
import com.theathletic.entity.authentication.UserPrivilegeLevel
import com.theathletic.entity.discussions.CommentEntity
import com.theathletic.entity.main.FeedItemEntryType
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.ui.Transformer
import com.theathletic.ui.UiModel
import com.theathletic.ui.formatter.CountFormatter
import com.theathletic.user.IUserManager
import com.theathletic.utility.BillingPreferences
import com.theathletic.utility.formatters.CommentsCountNumberFormat

class ArticleTransformer @AutoKoin constructor(
    private val userDataRepository: IUserDataRepository,
    private val userManager: IUserManager,
    private val billingPreferences: BillingPreferences,
    private val dateUtility: DateUtility,
    private val countFormatter: CountFormatter
) : Transformer<ArticleDataState, ArticleContract.ViewState> {

    private companion object {
        const val MAX_RELATED_ARTICLES = 4
    }

    override fun transform(data: ArticleDataState): ArticleContract.ViewState {
        val models = mutableListOf<UiModel>()

        data.articleEntity?.let { article ->
            models.addAll(article.getTopperModels(data.isAuthorClickable))
            if (data.showPaywall) {
                addPaywallModels(models, article, data)
            } else {
                addSubscriberModels(models, article, data)
            }
        }

        return ArticleContract.ViewState(
            showSpinner = data.showSpinner,
            liveRoomData = data.liveRoomData,
            showLiveRoom = data.liveRoomData != null,
            lastScrollPercentage = data.articleEntity?.lastScrollPercentage ?: 0,
            toolbarModel = data.articleEntity.getToolbar(data.isBookmarked, data.showWebviewUpgradeInToolbar),
            uiModels = models
        )
    }

    private fun addPaywallModels(
        models: MutableList<UiModel>,
        article: ArticleEntity,
        data: ArticleDataState
    ) {
        models.add(
            ArticlePaywallContentModel(
                article.articleBody.orEmpty(),
                article.permalink,
                data.contentTextSize,
                data.adConfig
            )
        )
        models.add(ArticlePaywallCTAModel())
    }

    private fun addSubscriberModels(
        models: MutableList<UiModel>,
        article: ArticleEntity,
        data: ArticleDataState
    ) {
        models.add(
            ArticleContentModel(
                article.articleBody.orEmpty(),
                article.permalink,
                data.contentTextSize,
                data.adConfig
            )
        )
        if (data.htmlIsLoaded) {
            if (!data.isRatedAtLaunch && article.ratingEnabled) {
                models.addAll(getRatings(data.userRating))
            }
            models.addAll(article.getCommentsSection(data))
            models.addAll(article.getRelatedContent())
        }
    }

    private fun ArticleEntity.getTopperModels(isAuthorClickable: Boolean): List<UiModel> {
        return if (entryType == FeedItemEntryType.ARTICLE_FRANCHISE) {
            emptyList()
        } else {
            listOf(
                ArticleImageModel(articleHeaderImg.orEmpty()),
                ArticleTitleModel(articleTitle.orEmpty()),
                ArticleAuthorModel(
                    authorId ?: -1,
                    authorName.orEmpty(),
                    authorImg.orEmpty(),
                    !authorImg.isNullOrBlank(),
                    isAuthorClickable,
                    dateUtility.formatGMTTimeAgo(articlePublishDate.orEmpty())
                )
            )
        }
    }

    private fun ArticleEntity?.getToolbar(
        isBookmarked: Boolean,
        showWebViewUpgrade: Boolean
    ): ArticleToolbarModel {
        return if (this != null) {
            ArticleToolbarModel(
                commentCount = CommentsCountNumberFormat.format(commentsCount),
                showComments = !commentsDisabled,
                isBookmarked = isBookmarked,
                bookmarkIcon = if (isBookmarked) {
                    R.drawable.ic_article_bookmark_selected
                } else {
                    R.drawable.ic_article_bookmark
                },
                showWebViewUpgrade = showWebViewUpgrade
            )
        } else {
            ArticleToolbarModel(
                commentCount = "",
                showComments = false,
                isBookmarked = false,
                bookmarkIcon = R.drawable.ic_article_bookmark,
                showWebViewUpgrade
            )
        }
    }

    private fun getRatings(userRating: Long?): List<UiModel> {
        val title = when (userRating) {
            ArticleRating.MEH.value -> ArticleRatingTitle(R.string.article_rating_title_thanks_meh)
            ArticleRating.SOLID.value -> ArticleRatingTitle(R.string.article_rating_title_thanks_solid)
            ArticleRating.AWESOME.value -> ArticleRatingTitle(R.string.article_rating_title_thanks_awesome)
            else -> ArticleRatingTitle(R.string.article_rating_title_add_rating)
        }

        val image = when (userRating) {
            ArticleRating.MEH.value -> ArticleRatedImage(R.drawable.ic_article_head_meh_checked)
            ArticleRating.SOLID.value -> ArticleRatedImage(R.drawable.ic_article_head_solid_checked)
            ArticleRating.AWESOME.value -> ArticleRatedImage(R.drawable.ic_article_head_awesome_checked)
            else -> ArticleRatingButtons
        }

        if (!userManager.isUserSubscribed() && userRating != null) {
            return if (billingPreferences.hasPurchaseHistory) {
                listOf(
                    ArticleRatingTitle(R.string.article_rating_title_thanks_subscribe),
                    image,
                    ArticleFreeUserUpsell(R.string.article_rating_button_subscribe),
                    ArticleRatingPadding
                )
            } else {
                listOf(
                    ArticleRatingTitle(R.string.article_rating_title_thanks_subscribe_trial),
                    image,
                    ArticleFreeUserUpsell(R.string.article_rating_button_subscribe_trial),
                    ArticleRatingPadding
                )
            }
        }

        return listOf(title, image, ArticleRatingPadding)
    }

    private fun ArticleEntity.getCommentsSection(data: ArticleDataState): List<UiModel> {
        val items = mutableListOf<UiModel>()
        when {
            commentsDisabled -> items.add(ArticleDisabledComments(R.string.article_comments_disabled_title))
            commentsLocked -> items.add(ArticleDisabledComments(R.string.article_comments_locked_title))
        }

        if (commentsDisabled.not() && commentsCount > 0) {
            if (comments.isNullOrEmpty()) {
                // This section of code is executed when we're working with a cached article.
                // Such articles are typically loaded in contexts where comments are not utilized.
                // Examples of such contexts include the feed or saved stories sections.
                items.add(ArticleCommentsNotLoaded(isLoading = data.isReloadingComments))
            } else {
                items.add(ArticleCommentsTitle(commentsCount.toInt()))
                comments?.forEach { commentEntity ->
                    val isLikeEnabled = data.likeActionState.isEnabled(commentEntity.commentId.toString())
                    val isLiked = userDataRepository.isCommentLiked(commentEntity.commentId)
                    val isNotFlagged = userDataRepository.isCommentFlagged(commentEntity.commentId).not()
                    items.add(
                        ArticleComment(
                            id = commentEntity.commentId,
                            userName = commentEntity.authorName,
                            isStaff = commentEntity.isStaff(),
                            date = dateUtility.formatGMTTimeAgo(commentEntity.commentDateGmt),
                            replies = commentEntity.totalReplies.toString(),
                            comment = commentEntity.body,
                            likes = commentEntity.likes.toString(),
                            isNotFlagged = isNotFlagged,
                            isLikeEnabled = isLikeEnabled,
                            isLiked = isLiked,
                            permalink = commentEntity.permalink,
                            likesIconRes = if (isLiked) R.drawable.ic_thumb_up_full else R.drawable.ic_thumb_up,
                            likesIconTint = if (isLiked) R.color.ath_grey_10 else R.color.ath_grey_45,
                        )
                    )
                }
                items.add(ArticleViewMoreComments)
            }
        }

        return items
    }

    private fun CommentEntity.isStaff() =
        getAuthorUserLevel().isAtLeastAtLevel(UserPrivilegeLevel.CONTRIBUTOR) ||
            getUserLevel().isAtLeastAtLevel(UserPrivilegeLevel.CONTRIBUTOR)

    private fun ArticleEntity.getRelatedContent(): List<UiModel> {
        val relatedItems = relatedContent?.filterNot {
            userDataRepository.isItemRead(it.id.toLongOrNull() ?: -1)
        }?.take(MAX_RELATED_ARTICLES)?.map { it.toRelatedContentItem() }

        return if (relatedItems != null && relatedItems.isNotEmpty()) {
            relatedItems.toMutableList<UiModel>().apply {
                add(0, RelatedContentSectionTitle)
            }
        } else {
            emptyList()
        }
    }

    private fun RelatedContent.toRelatedContentItem(): RelatedContentItem {
        val showTimeAgo = contentType == RelatedContent.ContentType.LIVEBLOG
        return RelatedContentItem(
            id = relatedContentItemId,
            imageUrl = imageUrl,
            timeAgo = dateUtility.formatGMTTimeAgo(timestampGmt),
            showTimeAgo = showTimeAgo,
            title = title,
            byline = byline,
            showByline = byline.isNotEmpty() && showTimeAgo.not(),
            commentCount = countFormatter.formatCommentCount(commentCount),
            showComments = commentCount > 0 && showTimeAgo.not(),
            showLiveStatus = isLive
        )
    }

    private val RelatedContent.relatedContentItemId: RelatedContentItemId
        get() {
            val contentType = when (this.contentType) {
                RelatedContent.ContentType.ARTICLE -> RelatedContentItemId.ContentType.ARTICLE
                RelatedContent.ContentType.HEADLINE -> RelatedContentItemId.ContentType.HEADLINE
                RelatedContent.ContentType.DISCUSSION -> RelatedContentItemId.ContentType.DISCUSSION
                RelatedContent.ContentType.QANDA -> RelatedContentItemId.ContentType.QANDA
                RelatedContent.ContentType.LIVEBLOG -> RelatedContentItemId.ContentType.LIVEBLOG
                else -> RelatedContentItemId.ContentType.ARTICLE
            }
            return RelatedContentItemId(id, contentType)
        }
}