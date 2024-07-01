package com.theathletic.article.ui

import com.theathletic.article.ArticleAuthorModel
import com.theathletic.article.ArticleComment
import com.theathletic.article.ArticleCommentsNotLoaded
import com.theathletic.article.ArticleContentModel
import com.theathletic.article.ArticleDisabledComments
import com.theathletic.article.ArticleFreeUserUpsell
import com.theathletic.article.ArticlePaywallCTAModel
import com.theathletic.article.ArticlePaywallContentModel
import com.theathletic.article.ArticleRatingButtons
import com.theathletic.article.ArticleToolbarModel
import com.theathletic.article.ArticleViewMoreComments
import com.theathletic.comments.FlagReason
import com.theathletic.presenter.Interactor
import com.theathletic.rooms.ui.LiveAudioRoomMiniPlayerInteractor
import com.theathletic.rooms.ui.LiveAudioRoomMiniPlayerUiModel
import com.theathletic.ui.UiModel

interface ArticleContract {

    interface Presenter :
        Interactor,
        ArticleReadCalculator.OnArticleReadListener,
        LiveAudioRoomMiniPlayerInteractor,
        ArticleContentModel.Interactor,
        ArticleAuthorModel.Interactor,
        ArticleToolbarModel.Interactor,
        ArticleRatingButtons.Interactor,
        ArticleFreeUserUpsell.Interactor,
        ArticleCommentsNotLoaded.Interactor,
        ArticleComment.Interactor,
        ArticleDisabledComments.Interactor,
        ArticleViewMoreComments.Interactor,
        RelatedContentItem.Interactor,
        ArticlePaywallCTAModel.Interactor,
        ArticlePaywallContentModel.Interactor {

        fun onCommentClick(commentId: String)
        fun onEditCommentClicked(commentId: Long)
        fun onDeleteCommentClicked(commentId: Long)
        fun onFlagCommentClick(commentId: Long, index: Int)
        fun flagComment(commentId: Long, flagType: FlagReason)
        fun onRelatedArticleImpression(percentInView: Float)
    }

    data class ViewState(
        val showSpinner: Boolean,
        val liveRoomData: LiveAudioRoomMiniPlayerUiModel?,
        val showLiveRoom: Boolean,
        val toolbarModel: ArticleToolbarModel,
        val lastScrollPercentage: Int?,
        val uiModels: List<UiModel>
    ) : com.theathletic.ui.ViewState

    sealed class Event : com.theathletic.utility.Event() {
        data class ToggleFullscreen(val isFullscreen: Boolean) : Event()
        object ShowTextStyleBottomSheet : Event()
        data class ShowCommentOptionsSheet(
            val commentId: Long,
            val isUserAuthor: Boolean,
            val isCommentLocked: Boolean,
            val commentIndex: Int
        ) : Event()

        data class ShowReportCommentDialog(val commentId: Long) : Event()
        object ShowWebViewUpgradeDialog : Event()
    }
}