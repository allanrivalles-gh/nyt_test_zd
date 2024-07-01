package com.theathletic.comments.ui

import androidx.annotation.StringRes
import com.theathletic.comments.game.TeamThreads
import com.theathletic.comments.ui.components.CommentsUi
import com.theathletic.comments.ui.components.InputHeaderData
import com.theathletic.comments.ui.components.InputHeaderData.EmptyHeaderData
import com.theathletic.data.ContentDescriptor
import com.theathletic.entity.user.SortType
import com.theathletic.ui.LoadingState

data class CommentsViewState constructor(
    val sourceDescriptor: ContentDescriptor,
    @StringRes val title: Int? = null,
    val commentsUiModel: CommentsUiModel = CommentsUiModel(),
    val threadsUiState: ThreadsUiState = ThreadsUiState(),
    val inputUiState: CommentsInputUiState = CommentsInputUiState(isCommentDrawerFeatureEnabled = false),
    val likeActionUiState: LikeActionUiState = LikeActionUiState(),
    val teamThreads: TeamThreads? = null,
    val sortedBy: SortType,
    val scrollToIndex: Int? = null,
    val highlightedCommentId: String? = null,
    val loadingState: LoadingState = LoadingState.INITIAL_LOADING,
) {
    val comments get() = commentsUiModel.comments
}

data class ThreadsUiState(
    val teamThreadBanner: CommentsUi.TeamThreadBanner? = null,
    val teamThreadsSheet: CommentsUi.TeamThreadsSheet? = null,
)

data class CommentsInputUiState constructor(
    val isCommentEnabled: Boolean = false,
    val inputText: String = "",
    val isCommentDrawerFeatureEnabled: Boolean = false,
    val inputHeaderData: InputHeaderData = EmptyHeaderData,
    val editOrReplyId: String? = null,
    val enableSend: Boolean = false,
    val lockedComments: Boolean = false,
    val drawerState: CommentsDrawerState = CommentsDrawerState.CLOSED,
    val availableUndo: CommentsUndoUserAction? = null,
)

data class LikeActionUiState(
    private val availability: Map<String, Boolean> = mapOf(),
) {
    fun enable(commentId: String) = copy(
        availability = availability.toMutableMap().apply { put(commentId, true) }
    )

    fun disable(commentId: String) = copy(
        availability = availability.toMutableMap().apply { put(commentId, false) }
    )

    fun isEnabled(commentId: String): Boolean {
        return availability[commentId] ?: true
    }
}

sealed interface CommentsViewEvent {
    object NavigateBack : CommentsViewEvent
    object NavigateToCodeOfConduct : CommentsViewEvent
    class ShareComment(val commentLink: String) : CommentsViewEvent
    object ShowPaywall : CommentsViewEvent
    object ShowCreateAccount : CommentsViewEvent
    object ShowCodeOfConduct : CommentsViewEvent
    data class ShowTempBanMessage(val daysLeft: Int) : CommentsViewEvent
    data class FlagComment(val commentId: String) : CommentsViewEvent
    data class ShowFeedbackMessage(@StringRes val stringRes: Int) : CommentsViewEvent
    data class OpenTweet(val tweetUrl: String) : CommentsViewEvent
    data class OpenUrl(val url: String) : CommentsViewEvent
}

enum class CommentsDrawerState {
    CLOSED, COLLAPSED, OPEN
}

/**
 * Represents an undo action that's available to the user
 */
class CommentsUndoUserAction(
    @StringRes val titleStringResId: Int,
    val priorState: CommentsInputUiState
)