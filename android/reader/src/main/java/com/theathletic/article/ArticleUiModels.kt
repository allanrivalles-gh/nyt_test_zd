package com.theathletic.article

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.theathletic.ads.AdConfig
import com.theathletic.ui.ContentTextSize
import com.theathletic.ui.UiModel

data class ArticleContentModel(
    val contentString: String,
    val contentUrl: String?,
    val contentTextSize: ContentTextSize,
    val adConfig: AdConfig?
) : UiModel {
    override val stableId = "ArticleContentModel"

    interface Interactor {
        fun onUrlClick(url: String)
        fun onPageLoaded()
        fun onFullscreenToggled(isFullscreen: Boolean)
    }
}

data class ArticlePaywallContentModel(
    val contentString: String,
    val contentUrl: String?,
    val contentTextSize: ContentTextSize,
    val adConfig: AdConfig?
) : UiModel {
    override val stableId = "ArticlePaywallContentModel"

    interface Interactor {
        fun onPageLoaded()
    }
}

data class ArticleImageModel(
    val imageUrl: String
) : UiModel {
    override val stableId = "ArticleImageModel"
}

data class ArticleTitleModel(
    val title: String
) : UiModel {
    override val stableId = "ArticleTitleModel"
}

data class ArticleAuthorModel(
    val authorId: Long,
    val name: String,
    val imageUrl: String,
    val isImageVisible: Boolean,
    val isAuthorClickable: Boolean,
    val date: String
) : UiModel {
    override val stableId = "ArticleAuthorModel"

    interface Interactor {
        fun onAuthorClicked(authorId: Long)
    }
}

data class ArticleToolbarModel(
    val commentCount: String,
    val showComments: Boolean,
    val isBookmarked: Boolean,
    @DrawableRes val bookmarkIcon: Int,
    val showWebViewUpgrade: Boolean
) : UiModel {
    override val stableId = "ArticleToolbarModel"

    interface Interactor {
        fun onCommentsClick()
        fun onBookmarkClick(isBookmarked: Boolean)
        fun onTextStyleClick()
        fun onShareClick()
        fun onWebViewUpgradeClick()
    }
}

data class ArticleRatingTitle(
    @StringRes val stringRes: Int
) : UiModel {
    override val stableId = "ArticleRatingTitle:$stringRes"
}

object ArticleRatingButtons : UiModel {
    override val stableId = "ArticleRatingButtons"

    interface Interactor {
        fun onMehRating()
        fun onSolidRating()
        fun onAwesomeRating()
    }
}

data class ArticleRatedImage(
    @DrawableRes val ratingImg: Int
) : UiModel {
    override val stableId = "ArticleRatedImage"
}

class ArticlePaywallCTAModel : UiModel {
    override val stableId = "ArticlePaywall"

    interface Interactor {
        fun onPaywallContinueClick()
    }
}

data class ArticleFreeUserUpsell(
    @StringRes val buttonTextRes: Int
) : UiModel {
    override val stableId = "ArticleFreeUserUpsell"

    interface Interactor {
        fun onViewPlanClick()
    }
}

object ArticleRatingPadding : UiModel {
    override val stableId = "ArticleRatingPadding"
}

data class ArticleCommentsNotLoaded(
    val isLoading: Boolean,
) : UiModel {
    override val stableId = "ArticleCommentsNotLoaded"

    interface Interactor {
        fun onTapToReloadComments()
    }
}

data class ArticleCommentsTitle(
    val commentCount: Int
) : UiModel {
    override val stableId = "ArticleCommentsTitle"
}

data class ArticleComment(
    val id: Long,
    val userName: String,
    val isStaff: Boolean,
    val date: String,
    val replies: String,
    val comment: String,
    val permalink: String,
    val likes: String,
    val isLikeEnabled: Boolean,
    val isNotFlagged: Boolean,
    val isLiked: Boolean,
    @DrawableRes val likesIconRes: Int,
    @ColorRes val likesIconTint: Int
) : UiModel {
    override val stableId = "ArticleComment:$id"

    interface Interactor {
        fun onCommentLiked(commentId: String, isCurrentlyLiked: Boolean, index: Int)
        fun onCommentReply(parentId: String, commentId: String)
        fun onCommentOptionsClicked(commentId: String, index: Int)
        fun onCommentShare(permalink: String)
    }
}

object ArticleViewMoreComments : UiModel {
    override val stableId = "ArticleViewMoreComments"

    interface Interactor {
        fun onViewMoreComments()
    }
}

data class ArticleDisabledComments(@StringRes val textRes: Int) : UiModel {
    override val stableId = "ArticleDisabledComments"

    interface Interactor {
        fun onCodeOfConductClick()
        fun onMessageModerator()
    }
}