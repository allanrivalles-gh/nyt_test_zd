package com.theathletic.gamedetail.boxscore.ui

import androidx.annotation.StringRes
import com.theathletic.analytics.impressions.ImpressionVisibilityListener
import com.theathletic.boxscore.ui.BaseballCurrentInningPlayUiModel
import com.theathletic.boxscore.ui.BoxScoreRecentPlays
import com.theathletic.boxscore.ui.InjuryReportUi
import com.theathletic.boxscore.ui.RelatedStoriesUi
import com.theathletic.boxscore.ui.bottomsheet.BoxScoreMenuOption
import com.theathletic.comments.ui.components.CommentsUi
import com.theathletic.feed.ui.FeedUiV2
import com.theathletic.presenter.Interactor
import com.theathletic.ui.widgets.ModalBottomSheetType

interface BoxScoreContract {
    interface Presenter :
        Interactor,
        InjuryReportUi.SummaryInteractor,
        BoxScoreRecentPlays.Interactor,
        BaseballCurrentInningPlayUiModel.Interactor,
        RelatedStoriesUi.Interactor,
        ImpressionVisibilityListener

    data class ViewState(
        val showSpinner: Boolean,
        val feedUiModel: FeedUiV2,
        val refreshable: Boolean = true,
        val showToolbar: Boolean = false,
        val finishedInitialLoading: Boolean = false,
        val boxScoreModalSheet: ModalSheetType? = null,
        val boxScoreModalSheetOptions: List<BoxScoreMenuOption>? = null,
        val snackBarMessage: String? = null,
        val commentFlagState: CommentsUi.FlagState? = null,
        val scrollTo: Int = 0
    ) : com.theathletic.ui.ViewState

    sealed class ModalSheetType : ModalBottomSheetType {
        data class ArticleOptionsModalSheet(
            val articleId: Long,
            val isRead: Boolean,
            val isBookmarked: Boolean,
            val permalink: String
        ) : ModalSheetType()

        data class PodcastOptionsModalSheet(
            val podcastId: Long,
            val episodeId: Long,
            val permalink: String
        ) : ModalSheetType()
    }
}

sealed interface BoxScoreViewEvent {
    data class DownloadPodcastEpisode(
        val episodeId: Long,
        val episodeTitle: String,
        val downloadUrl: String
    ) : BoxScoreViewEvent

    data class CancelPodcastEpisodeDownloading(
        val episodeId: Long
    ) : BoxScoreViewEvent

    data class ShowFeedbackMessage(
        @StringRes val stringRes: Int
    ) : BoxScoreViewEvent

    object ShowPaywall : BoxScoreViewEvent
    object ShowNetworkOfflineError : BoxScoreViewEvent
}