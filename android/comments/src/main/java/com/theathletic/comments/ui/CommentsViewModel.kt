package com.theathletic.comments.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theathletic.analytics.data.ClickSource
import com.theathletic.analytics.impressions.ImpressionCalculator
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.comments.AddCommentUseCase
import com.theathletic.comments.AnalyticsCommentTeamIdUseCase
import com.theathletic.comments.FetchCommentsUseCase
import com.theathletic.comments.FlagReason
import com.theathletic.comments.LikeCommentsUseCase
import com.theathletic.comments.ObserveCommentsUseCase
import com.theathletic.comments.R
import com.theathletic.comments.analytics.CommentsAnalyticsV2
import com.theathletic.comments.analytics.CommentsLaunchAction
import com.theathletic.comments.analytics.CommentsParamModel
import com.theathletic.comments.data.Comment
import com.theathletic.comments.data.CommentInput
import com.theathletic.comments.data.CommentsFeed
import com.theathletic.comments.data.CommentsRepository
import com.theathletic.comments.data.LikeAction
import com.theathletic.comments.game.ObserveTeamThreadsUseCase
import com.theathletic.comments.game.SwitchTeamThreadUseCase
import com.theathletic.comments.game.TeamThreads
import com.theathletic.comments.game.toTeamThreadBannerUiModel
import com.theathletic.comments.game.toTeamThreadSheetUiModel
import com.theathletic.comments.ui.components.CommentsUi
import com.theathletic.comments.ui.components.InputHeaderData
import com.theathletic.comments.utility.CommentsViewVisibilityManager
import com.theathletic.comments.utility.DwellEventsEmitter
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.data.ContentDescriptor
import com.theathletic.entity.user.SortType
import com.theathletic.extension.extLogError
import com.theathletic.extension.nullIfEmpty
import com.theathletic.featureswitch.Features
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.ui.LoadingState
import com.theathletic.ui.updateState
import com.theathletic.user.IUserManager
import com.theathletic.user.data.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import kotlin.math.max

@Suppress("LargeClass") // TODO: This class will be reworked soon with ATH-24093
class CommentsViewModel @AutoKoin constructor(
    @Assisted private val commentsParams: CommentsParamModel,
    private val userManager: IUserManager,
    private val userDataRepository: IUserDataRepository,
    private val commentsRepository: CommentsRepository,
    private val commentsAnalytics: CommentsAnalyticsV2,
    private val userRepository: UserRepository,
    private val observeCommentsUseCase: ObserveCommentsUseCase,
    private val fetchCommentsUseCase: FetchCommentsUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val likeCommentsUseCase: LikeCommentsUseCase,
    private val observeTeamThreadsUseCase: ObserveTeamThreadsUseCase,
    private val switchTeamThreadUseCase: SwitchTeamThreadUseCase,
    private val analyticsCommentTeamIdUseCase: AnalyticsCommentTeamIdUseCase,
    private val dwellEventsEmitter: DwellEventsEmitter,
    private val visibilityManager: CommentsViewVisibilityManager,
    private val impressionCalculator: ImpressionCalculator,
    private val commentsUiMapper: CommentsUiMapper,
    private val features: Features,
) : CommentsAnalyticsV2 by commentsAnalytics,
    CommentsUi.Interactor,
    CommentsUi.Comments.Interactor,
    ViewModel(),
    DefaultLifecycleObserver {

    private var launchAction = commentsParams.launchAction

    private var dwellEventsEmitterJob: Job? = null
    private var commentsFeed: CommentsFeed = CommentsFeed()

    private val _viewState: MutableStateFlow<CommentsViewState> = MutableStateFlow(
        CommentsViewState(
            sourceDescriptor = commentsParams.sourceDescriptor,
            title = commentsParams.sourceType.toTitleRes(),
            sortedBy = userManager.getCommentsSortType(commentsParams.sourceType),
            inputUiState = CommentsInputUiState(
                isCommentDrawerFeatureEnabled = features.isCommentDrawerEnabled,
                inputHeaderData = defaultInputHeaderData(headerTitle = null, sourceDescriptor = commentsParams.sourceDescriptor)
            ),
            highlightedCommentId = commentsParams.launchAction?.commentId
        )
    )
    val viewState = _viewState.asStateFlow()

    private val _viewEvents = MutableSharedFlow<CommentsViewEvent>()
    val viewEvents = _viewEvents.asSharedFlow()

    override fun onCreate(owner: LifecycleOwner) {
        observeTeamThreadsUseCase(commentsParams.sourceDescriptor.id)
            .onEach { onTeamThreadsUpdate(it) }
            .launchIn(viewModelScope)

        observeCommentsUseCase(commentsParams.sourceDescriptor.id, commentsParams.sourceType, viewModelScope)
            .onEach { onCommentsUpdate(it) }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            visibilityManager.isVisible.collect { visible ->
                if (visible) {
                    val uid = UUID.randomUUID().toString()
                    trackCommentsView(uid)
                    dwellEventsEmitterJob = launch {
                        dwellEventsEmitter.start { seconds ->
                            commentsAnalytics.trackCommentDwell(
                                sourceId = commentsParams.sourceDescriptor.id,
                                sourceType = commentsParams.sourceType,
                                analyticsPayload = commentsParams.analyticsPayload,
                                seconds = seconds,
                                uid = uid,
                            )
                        }
                    }
                } else {
                    dwellEventsEmitterJob?.cancel()
                    dwellEventsEmitterJob = null
                }
            }
        }

        initialLoading()
        trackArticleView()
        initializeImpressionTracking()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        visibilityManager.onResumed()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        visibilityManager.onPaused()
    }

    fun onTabSelectionChanged(isTabSelected: Boolean) {
        visibilityManager.onTabSelectionChanged(isTabSelected)
    }

    fun onExternalScrollToCommentRequest(targetCommentId: String) {
        launchAction = CommentsLaunchAction.View(targetCommentId)
        refreshDataAndScrollToComment(targetCommentId)
    }

    fun onExternalReplyToCommentRequest(targetCommentId: String, parentId: String) {
        launchAction = CommentsLaunchAction.Reply(parentId, targetCommentId)
        refreshDataAndScrollToComment(targetCommentId)
    }

    private fun refreshDataAndScrollToComment(targetCommentId: String) {
        _viewState.updateState { copy(loadingState = LoadingState.RELOADING, highlightedCommentId = targetCommentId) }
        refreshData()
        _viewState.updateState {
            val commentsUiModel = updateCommentUiModel()
            copy(
                scrollToIndex = commentsUiModel.highlightedCommentIndex
            )
        }
    }

    private fun initialLoading() {
        _viewState.updateState { copy(loadingState = LoadingState.INITIAL_LOADING) }
        refreshData()
    }

    private fun initializeImpressionTracking() {
        impressionCalculator.configure({ payload, startTime, endTime ->
            payload.impress(
                getCurrentTeamId(true),
                startTime,
                endTime
            )
        })
    }

    private fun trackCommentsView(uid: String = UUID.randomUUID().toString()) {
        commentsAnalytics.trackAllComments(
            sourceId = commentsParams.sourceDescriptor.id,
            sourceType = commentsParams.sourceType,
            analyticsPayload = commentsParams.analyticsPayload,
            uid = uid,
            teamId = getCurrentTeamId()
        )
    }

    private fun onTeamThreadsUpdate(teamThreads: TeamThreads?) {
        _viewState.updateState {
            copy(
                teamThreads = teamThreads,
                threadsUiState = ThreadsUiState(teamThreadBanner = teamThreads?.toTeamThreadBannerUiModel()),
                // Important side-effect: disabling text field also allows the pointerInput modifier to detect gestures
                inputUiState = inputUiState.copy(isCommentEnabled = isUserAllowedToComment(teamThreads))
            )
        }
    }

    private fun getCurrentTeamId(useLegacy: Boolean = false) = analyticsCommentTeamIdUseCase(
        isTeamSpecificComment = isTeamSpecificThreadDiscussion(),
        team = _viewState.value.teamThreads?.current?.team,
        useLegacy = useLegacy
    )

    private fun trackArticleView() {
        if (commentsParams.sourceType.shouldTrackArticleView) {
            commentsAnalytics.trackArticleView(
                articleId = commentsParams.sourceDescriptor.id,
                source = commentsParams.clickSource ?: ClickSource.UNKNOWN
            )
        }
    }

    private fun onCommentsUpdate(data: CommentsFeed) {
        commentsFeed = data
        _viewState.updateState {
            val commentsUiModel = updateCommentUiModel()
            copy(
                commentsUiModel = commentsUiModel,
                inputUiState = inputUiState.copy(
                    lockedComments = data.commentsLocked,
                    inputHeaderData = defaultInputHeaderData(commentsUiModel.header?.title, commentsParams.sourceDescriptor)
                ),
                loadingState = LoadingState.FINISHED,
                scrollToIndex = commentsUiModel.highlightedCommentIndex
            )
        }
    }

    private fun refreshData() {
        viewModelScope.launch {
            fetchCommentsUseCase(
                sourceId = commentsParams.sourceDescriptor.id,
                sortType = _viewState.value.sortedBy,
                sourceType = commentsParams.sourceType
            )
            _viewState.updateState {
                copy(
                    commentsUiModel = updateCommentUiModel(),
                    loadingState = LoadingState.FINISHED,
                    scrollToIndex = _viewState.value.scrollToIndex,
                    inputUiState = inputUiState.copy(
                        isCommentEnabled = isUserAllowedToComment(_viewState.value.teamThreads)
                    )
                )
            }
        }
    }

    private fun isUserAllowedToComment(teamThreads: TeamThreads?): Boolean {
        val isMissingTeamThreads = isTeamSpecificThreadDiscussion() && teamThreads == null
        return userManager.isUserTempBanned().not() || isMissingTeamThreads
    }

    private fun updateUserSortPreferences(
        selectedOption: SortType,
        onUpdateFinished: (Boolean) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                userRepository.updateUserSortPreferences(
                    commentsParams.sourceType,
                    selectedOption
                )
                onUpdateFinished(true)
            } catch (e: Exception) {
                Timber.e(e)
                onUpdateFinished(false)
            }
        }
    }

    override fun onBackButtonPressed() {
        viewModelScope.launch { _viewEvents.emit(CommentsViewEvent.NavigateBack) }
    }

    override fun onSortOptionSelected(selectedOption: SortType) {
        val oldSortOption = _viewState.value.sortedBy
        _viewState.updateState { copy(sortedBy = selectedOption, loadingState = LoadingState.RELOADING) }
        updateUserSortPreferences(selectedOption) { success ->
            if (success) {
                refreshData()
                commentsAnalytics.trackSortComments(
                    sourceId = commentsParams.sourceDescriptor.id,
                    sourceType = commentsParams.sourceType,
                    sortBy = selectedOption,
                    teamId = getCurrentTeamId(),
                    analyticsPayload = commentsParams.analyticsPayload
                )
            } else {
                viewModelScope.launch {
                    _viewEvents.emit(CommentsViewEvent.ShowFeedbackMessage(R.string.global_error))
                }
                _viewState.updateState { copy(sortedBy = oldSortOption, loadingState = LoadingState.FINISHED) }
            }
        }
    }

    override fun onPullToRefresh() {
        _viewState.updateState { copy(loadingState = LoadingState.RELOADING, highlightedCommentId = null) }
        refreshData()
        commentsAnalytics.trackRefreshComments(
            sourceId = commentsParams.sourceDescriptor.id,
            sourceType = commentsParams.sourceType,
            teamId = getCurrentTeamId(),
            analyticsPayload = commentsParams.analyticsPayload
        )
    }

    override fun onFinishedScrollingToComment() {
        _viewState.updateState { copy(scrollToIndex = null) }

        launchAction?.also { action ->
            if (action is CommentsLaunchAction.Reply) {
                onReplyClick(action.parentId, action.commentId)
            }
        }
    }

    override fun onLinkClick(url: String) {
        viewModelScope.launch {
            _viewEvents.emit(CommentsViewEvent.OpenUrl(url))
        }
    }

    override fun onClickTeamBannerChange() {
        _viewState.updateState {
            copy(
                threadsUiState = threadsUiState.copy(
                    teamThreadsSheet = teamThreads?.getTeamThreadContextSwitch()?.toTeamThreadSheetUiModel()
                ),
            )
        }
    }

    override fun onDismissTeamThreadsSheet() {
        _viewState.updateState { copy(threadsUiState = threadsUiState.copy(teamThreadsSheet = null)) }
    }

    override fun onSwitchedTeamThread(teamId: String) {
        _viewState.updateState {
            copy(
                threadsUiState = threadsUiState.copy(teamThreadsSheet = null),
                loadingState = LoadingState.RELOADING
            )
        }
        val currentTeamId = _viewState.value.teamThreads?.teamId
        viewModelScope.launch {
            switchTeamThreadUseCase(commentsParams.sourceDescriptor.id, teamId)
                .onSuccess {
                    trackThreadsSwitch(currentTeamId, teamId)
                    trackCommentsView()
                    refreshData()
                }
                .onFailure {
                    viewModelScope.launch {
                        _viewEvents.emit(CommentsViewEvent.ShowFeedbackMessage(R.string.global_error))
                    }
                }
        }
    }

    private fun trackThreadsSwitch(currentTeamId: String?, teamId: String) {
        commentsAnalytics.trackThreadSwitch(
            gameStatus = commentsParams.analyticsPayload?.gameStatusView.orEmpty(),
            gameId = commentsParams.sourceDescriptor.id,
            currentTeamId = currentTeamId,
            clickedTeamId = teamId
        )
    }

    override fun onLikeClick(commentId: String, index: Int) {
        _viewState.updateState { copy(likeActionUiState = likeActionUiState.disable(commentId)) }
        runIfCodeOfConductIsAccepted(
            onFinished = { _viewState.updateState { copy(likeActionUiState = likeActionUiState.enable(commentId)) } },
            likeAction = true
        ) {
            viewModelScope.launch {
                val comment = _viewState.value.commentsUiModel.comments.firstOrNull {
                    it.commentId == commentId
                } ?: return@launch
                val response = likeCommentsUseCase(hasUserLiked = comment.hasUserLiked, commentId = commentId)
                val likeAction = comment.hasUserLiked.toLikeAction()
                response.onSuccess {
                    commentsFeed = commentsFeed.addLikeAction(commentId, likeAction).updatedCommentsFeed
                    _viewState.updateState {
                        copy(
                            commentsUiModel = updateCommentUiModel(),
                            likeActionUiState = likeActionUiState.enable(commentId)
                        )
                    }
                    trackLikeAnalytics(likeAction, commentId, index)
                }.onFailure {
                    _viewState.updateState { copy(likeActionUiState = likeActionUiState.enable(commentId)) }
                }
            }
        }
    }

    private fun trackLikeAnalytics(
        likeAction: LikeAction,
        commentId: String,
        index: Int,
    ) {
        if (likeAction == LikeAction.LIKE) {
            commentsAnalytics.trackLikeComment(
                commentId = commentId,
                sourceId = commentsParams.sourceDescriptor.id,
                sourceType = commentsParams.sourceType,
                filterType = _viewState.value.sortedBy,
                index = index,
                teamId = getCurrentTeamId(),
            )
        } else {
            commentsAnalytics.trackUnlikeComment(
                commentId = commentId,
                sourceId = commentsParams.sourceDescriptor.id,
                sourceType = commentsParams.sourceType,
                filterType = _viewState.value.sortedBy,
                index = index,
                teamId = getCurrentTeamId(),
            )
        }
    }

    override fun onTextChanged(newText: String) {
        _viewState.updateState {
            copy(
                inputUiState = inputUiState.copy(
                    inputText = newText,
                    enableSend = newText.isNotBlank(),
                    availableUndo = null
                )
            )
        }
    }

    override fun onKeyboardOpenChanged(newState: Boolean) {
        var inputHeaderData = viewState.value.inputUiState.inputHeaderData
        if (newState && inputHeaderData is InputHeaderData.EmptyHeaderData) {
            inputHeaderData = defaultInputHeaderData()
        }
        _viewState.updateState {
            copy(
                inputUiState = inputUiState.copy(
                    inputHeaderData = inputHeaderData,
                    drawerState = when {
                        features.isCommentDrawerEnabled.not() -> CommentsDrawerState.CLOSED
                        newState -> CommentsDrawerState.OPEN
                        inputUiState.inputText.isNotEmpty() -> CommentsDrawerState.COLLAPSED
                        else -> CommentsDrawerState.CLOSED
                    }
                )
            )
        }
    }

    override fun onSendClick(onFinished: () -> Unit) =
        runIfCodeOfConductIsAccepted(onFinished = onFinished) {
            when {
                _viewState.value.inputUiState.inputText.isBlank() -> {
                    viewModelScope.launch {
                        _viewEvents.emit(CommentsViewEvent.ShowFeedbackMessage(R.string.comments_input_empty))
                    }
                    onFinished()
                }
                _viewState.value.inputUiState.inputHeaderData.isEditing -> {
                    editComment(onFinished)
                }
                else -> {
                    addComment(onFinished)
                }
            }
        }

    override fun onShareClick(permalink: String) {
        viewModelScope.launch {
            _viewEvents.emit(CommentsViewEvent.ShareComment(permalink))
        }
    }

    override fun onDeleteClick(commentId: String) {
        viewModelScope.launch {
            commentsRepository.deleteComment(commentId)
                .onSuccess {
                    commentsFeed = commentsFeed.deleteComment(commentId).updatedCommentsFeed
                    _viewState.updateState { copy(commentsUiModel = updateCommentUiModel()) }
                    viewModelScope.launch {
                        _viewEvents.emit(CommentsViewEvent.ShowFeedbackMessage(R.string.comments_delete_snackbar_success))
                    }
                }
                .onError {
                    it.extLogError()
                    viewModelScope.launch {
                        _viewEvents.emit(CommentsViewEvent.ShowFeedbackMessage(R.string.comments_delete_snackbar_fail))
                    }
                }
        }
        commentsAnalytics.trackDeleteComment(commentId)
    }

    override fun onEditClick(commentId: String, text: String) {
        _viewState.updateState {
            copy(
                inputUiState = inputUiState.copy(
                    drawerState = CommentsDrawerState.OPEN,
                    inputHeaderData = InputHeaderData.EditHeaderData(
                        features.isCommentDrawerEnabled
                    ),
                    inputText = text,
                    editOrReplyId = commentId
                )
            )
        }
    }

    override fun onCommentInputClick() {
        if (userManager.isUserTempBanned()) {
            if (features.isCommentDrawerEnabled) {
                _viewState.updateState {
                    copy(
                        inputUiState = inputUiState.copy(
                            inputHeaderData = InputHeaderData.TempBannedHeaderData(userManager.getBannedDaysLeft()),
                            drawerState = CommentsDrawerState.COLLAPSED
                        )
                    )
                }
            } else {
                viewModelScope.launch {
                    _viewEvents.emit(CommentsViewEvent.ShowTempBanMessage(max(1, userManager.getBannedDaysLeft())))
                }
            }
        }
    }

    override fun onReplyClick(parentId: String, commentId: String) {
        if (userManager.isUserTempBanned()) {
            if (features.isCommentDrawerEnabled) {
                _viewState.updateState {
                    copy(
                        inputUiState = inputUiState.copy(
                            inputHeaderData = InputHeaderData.TempBannedHeaderData(userManager.getBannedDaysLeft()),
                            drawerState = CommentsDrawerState.COLLAPSED
                        )
                    )
                }
            } else {
                viewModelScope.launch {
                    _viewEvents.emit(CommentsViewEvent.ShowTempBanMessage(userManager.getBannedDaysLeft()))
                }
            }
        } else {
            replyComment(parentId, commentId)
        }
    }

    override fun onCancelInput(inputHeaderData: InputHeaderData) {
        if (features.isCommentDrawerEnabled) {
            if (inputHeaderData is InputHeaderData.EditHeaderData) {
                _viewState.updateState {
                    copy(
                        inputUiState = inputUiState.copy(
                            inputHeaderData = defaultInputHeaderData(),
                            inputText = "",
                            drawerState = CommentsDrawerState.CLOSED,
                            enableSend = false,
                            editOrReplyId = null
                        )
                    )
                }
            } else {
                // TODO - McClelland - Additional logic needed as part of ATH-23713?
                var undoAction: CommentsUndoUserAction? = null
                if (_viewState.value.inputUiState.inputText.isNotEmpty()) {
                    undoAction = CommentsUndoUserAction(
                        titleStringResId = R.string.comments_undo_snackbar,
                        priorState = _viewState.value.inputUiState
                    )
                }
                _viewState.updateState {
                    copy(
                        inputUiState = inputUiState.copy(
                            drawerState = CommentsDrawerState.CLOSED,
                            inputHeaderData = defaultInputHeaderData(),
                            inputText = "",
                            enableSend = false,
                            editOrReplyId = null,
                            availableUndo = undoAction
                        )
                    )
                }
            }
        } else {
            _viewState.updateState {
                copy(
                    inputUiState = inputUiState.copy(
                        drawerState = CommentsDrawerState.CLOSED,
                        inputHeaderData = defaultInputHeaderData(),
                        inputText = "",
                        enableSend = false,
                        editOrReplyId = null
                    )
                )
            }
        }
    }

    override fun onCodeOfConductClick() {
        showCodeOfConduct()
    }

    override fun onUndoCancel(priorState: CommentsInputUiState) {
        _viewState.updateState {
            // Restore comment that was being edited at the time the user originally tapped cancel button
            copy(
                inputUiState = inputUiState.copy(
                    inputText = priorState.inputText,
                    drawerState = CommentsDrawerState.OPEN,
                    isCommentDrawerFeatureEnabled = features.isCommentDrawerEnabled,
                    inputHeaderData = priorState.inputHeaderData,
                    enableSend = priorState.enableSend,
                    editOrReplyId = priorState.editOrReplyId,
                    availableUndo = null
                )
            )
        }
    }

    override fun onFlagClick(commentId: String, index: Int) = runIfCodeOfConductIsAccepted(
        likeAction = false,
        requiresSubscription = true,
        onFinished = {}
    ) {
        viewModelScope.launch {
            _viewEvents.emit(CommentsViewEvent.FlagComment(commentId))
        }
        commentsAnalytics.trackFlagComment(commentId, _viewState.value.sortedBy, index, teamId = getCurrentTeamId())
    }

    override fun onClickTweet(tweetUrl: String) {
        viewModelScope.launch {
            _viewEvents.emit(CommentsViewEvent.OpenTweet(tweetUrl))
        }
    }

    override fun onVisibilityChanged(commentId: String, commentIndex: Int, visibility: Float) {
        if (isTeamSpecificThreadDiscussion() || isGameDiscussion()
        ) {
            impressionCalculator.onViewVisibilityChanged(
                commentsAnalytics.getImpressionPayload(commentId, commentsParams.sourceDescriptor.id, commentIndex),
                visibility
            )
        }
    }

    private fun isGameDiscussion() = commentsParams.sourceType == CommentsSourceType.GAME

    private fun isTeamSpecificThreadDiscussion() = commentsParams.sourceType == CommentsSourceType.TEAM_SPECIFIC_THREAD

    private fun defaultInputHeaderData(
        headerTitle: String? = _viewState.value.commentsUiModel.header?.title?.nullIfEmpty(),
        sourceDescriptor: ContentDescriptor = _viewState.value.sourceDescriptor
    ): InputHeaderData {
        return if (userManager.isUserTempBanned()) {
            InputHeaderData.TempBannedHeaderData(userManager.getBannedDaysLeft())
        } else if (features.isCommentDrawerEnabled) {
            // In some cases, as with the game Discuss tab, there is no header, and we rely on
            // the title passed in from the place from which comments were originally opened
            val inputHeaderTitle = headerTitle ?: sourceDescriptor.title
            InputHeaderData.TopLevelCommentHeaderData(inputHeaderTitle)
        } else {
            InputHeaderData.EmptyHeaderData
        }
    }

    private fun replyComment(parentId: String, commentId: String) {
        val indexedComment = _viewState.value.comments.withIndex().firstOrNull { it.value.commentId == commentId }
        val inputHeaderData =
            InputHeaderData.ReplyHeaderData(author = indexedComment?.value?.authorName.orEmpty())
        _viewState.updateState {
            copy(
                inputUiState = inputUiState.copy(
                    inputHeaderData = inputHeaderData,
                    editOrReplyId = parentId,
                    drawerState = CommentsDrawerState.OPEN
                ),
                scrollToIndex = indexedComment?.index
            )
        }
    }

    private fun addComment(onFinished: () -> Unit) {
        val currentInputHeaderData = _viewState.value.inputUiState.inputHeaderData
        viewModelScope.launch {
            _viewEvents.emit(CommentsViewEvent.ShowFeedbackMessage(R.string.news_comments_submitting_comment))
        }
        _viewState.updateState {
            copy(
                inputUiState = inputUiState.copy(
                    enableSend = false,
                    inputHeaderData = defaultInputHeaderData()
                )
            )
        }

        viewModelScope.launch {
            addCommentUseCase(
                CommentInput(
                    sourceDescriptor = _viewState.value.sourceDescriptor,
                    sourceType = commentsParams.sourceType,
                    content = _viewState.value.inputUiState.inputText.trim(),
                    parentId = _viewState.value.inputUiState.editOrReplyId.orEmpty(),
                    teamId = _viewState.value.teamThreads?.current?.teamId.orEmpty()
                )
            ).onSuccess { comment ->
                onCommentAdded(comment)
                onFinished()
            }.onFailure { error ->
                onCommentAddError(error, currentInputHeaderData)
                onFinished()
            }
        }
        commentsAnalytics.trackCommentSubmission(commentsParams.sourceDescriptor.id, commentsParams.sourceType)
    }

    private fun onCommentAdded(comment: Comment) {
        val commentsFeedUpdate = commentsFeed.addComment(
            newComment = comment,
            sortedBy = _viewState.value.sortedBy
        )
        commentsFeed = commentsFeedUpdate.updatedCommentsFeed

        _viewState.updateState {
            copy(
                commentsUiModel = updateCommentUiModel(),
                inputUiState = inputUiState.copy(
                    inputText = "",
                    enableSend = false,
                    editOrReplyId = null,
                    inputHeaderData = defaultInputHeaderData(),
                ),
                scrollToIndex = commentsFeedUpdate.updatedIndex
            )
        }
        viewModelScope.launch {
            _viewEvents.emit(CommentsViewEvent.ShowFeedbackMessage(R.string.comments_send_snackbar_success))
        }
    }

    private fun onCommentAddError(error: Throwable, inputHeaderData: InputHeaderData) {
        error.extLogError()
        _viewState.updateState {
            copy(
                inputUiState = inputUiState.copy(
                    enableSend = inputUiState.inputText.isNotBlank(),
                    inputHeaderData = inputHeaderData
                )
            )
        }
        viewModelScope.launch {
            _viewEvents.emit(CommentsViewEvent.ShowFeedbackMessage(R.string.comments_send_snackbar_fail))
        }
    }

    private fun editComment(onFinished: () -> Unit) {
        val inputUiState = _viewState.value.inputUiState
        val editId = inputUiState.editOrReplyId ?: return
        val newText = inputUiState.inputText
        val currentInputHeaderData = inputUiState.inputHeaderData
        viewModelScope.launch {
            _viewEvents.emit(CommentsViewEvent.ShowFeedbackMessage(R.string.news_comments_submitting_comment))
        }

        _viewState.updateState {
            copy(
                inputUiState = inputUiState.copy(
                    enableSend = false,
                )
            )
        }
        viewModelScope.launch {
            commentsRepository.editComment(
                editId,
                newText
            )
                .onSuccess {
                    val commentsFeedUpdate = commentsFeed.editComment(editId, newText)
                    commentsFeed = commentsFeedUpdate.updatedCommentsFeed

                    _viewState.updateState {
                        copy(
                            commentsUiModel = updateCommentUiModel(),
                            inputUiState = inputUiState.copy(
                                drawerState = CommentsDrawerState.CLOSED,
                                inputText = "",
                                enableSend = false,
                                inputHeaderData = defaultInputHeaderData(),
                                editOrReplyId = null,
                            ),
                            scrollToIndex = commentsFeedUpdate.updatedIndex,
                        )
                    }
                    _viewEvents.emit(CommentsViewEvent.ShowFeedbackMessage(R.string.comments_send_snackbar_success))
                    onFinished()
                }
                .onError {
                    it.extLogError()
                    _viewState.updateState {
                        copy(
                            inputUiState = inputUiState.copy(
                                inputHeaderData = currentInputHeaderData,
                                enableSend = inputUiState.inputText.isNotBlank(),
                            )
                        )
                    }
                    _viewEvents.emit(CommentsViewEvent.ShowFeedbackMessage(R.string.global_error))
                    onFinished()
                }
        }
        commentsAnalytics.trackEditComment(editId)
    }

    private fun runIfCodeOfConductIsAccepted(
        onFinished: () -> Unit,
        likeAction: Boolean = false,
        requiresSubscription: Boolean = false,
        action: () -> Unit,
    ) {
        viewModelScope.launch {
            when {
                _viewState.value.inputUiState.lockedComments -> {
                    _viewEvents.emit(CommentsViewEvent.ShowFeedbackMessage(R.string.comments_locked_message))
                    clearCommentInput()
                    onFinished()
                }
                shouldShowPayWall(requiresSubscription) -> {
                    _viewEvents.emit(CommentsViewEvent.ShowPaywall)
                    clearCommentInput()
                    onFinished()
                }
                userManager.isAnonymous && !likeAction -> {
                    _viewEvents.emit(CommentsViewEvent.ShowCreateAccount)
                    clearCommentInput()
                    onFinished()
                }
                userManager.isCodeOfConductAccepted().not() -> {
                    _viewEvents.emit(CommentsViewEvent.ShowCodeOfConduct)
                    onFinished()
                }
                else -> action.invoke()
            }
        }
    }

    private fun updateCommentUiModel() = commentsUiMapper.toUiModel(
        commentFeed = commentsFeed,
        highlightedCommentId = _viewState.value.highlightedCommentId,
        isUserStaff = userManager.isStaff
    )

    private fun clearCommentInput() {
        _viewState.updateState { copy(inputUiState = inputUiState.copy(inputText = "")) }
    }

    private fun shouldShowPayWall(requiresSubscription: Boolean) =
        (userManager.isUserLoggedIn() && (userManager.isUserSubscribed() || requiresSubscription)).not()

    fun showCodeOfConduct() {
        viewModelScope.launch {
            _viewEvents.emit(CommentsViewEvent.NavigateToCodeOfConduct)
        }
    }

    fun onFlagComment(commentId: String, flagType: FlagReason) {
        viewModelScope.launch {
            commentsRepository.flagComment(commentId, flagType)
                .onSuccess {
                    userDataRepository.markCommentFlagged(commentId.toLong(), true)
                    _viewEvents.emit(CommentsViewEvent.ShowFeedbackMessage(R.string.comments_flag_snackbar_success))
                }
                .onError {
                    it.extLogError()
                    _viewEvents.emit(CommentsViewEvent.ShowFeedbackMessage(R.string.global_error))
                }
        }
    }

    private val CommentsSourceType.shouldTrackArticleView get() = isDiscussion() || isQanda()

    private fun Boolean.toLikeAction(): LikeAction = if (this) LikeAction.UNLIKE else LikeAction.LIKE

    private fun CommentsSourceType.toTitleRes() = when {
        isDiscussion() -> R.string.comments_header_discussion
        isQanda() -> R.string.live_discussions_title
        isGame() -> null
        else -> R.string.comments_header_article
    }
}