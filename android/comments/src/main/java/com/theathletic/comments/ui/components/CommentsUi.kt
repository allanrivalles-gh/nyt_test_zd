package com.theathletic.comments.ui.components

import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.theathletic.comments.FlagReason
import com.theathletic.comments.ui.CommentsDrawerState
import com.theathletic.comments.ui.CommentsUiModel
import com.theathletic.comments.ui.CommentsUndoUserAction
import com.theathletic.comments.ui.CommentsViewState
import com.theathletic.comments.ui.LikeActionUiState
import com.theathletic.comments.ui.preview.CommentsPreviewData
import com.theathletic.comments.ui.preview.CommentsPreviewData.staffCommentBase
import com.theathletic.comments.ui.preview.CommentsPreviewData.userCommentBase
import com.theathletic.entity.user.SortType
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.LoadingState
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.preview.AthleticThemeProvider
import com.theathletic.ui.preview.PreviewContent
import com.theathletic.ui.utility.conditional
import com.theathletic.ui.widgets.BottomSheetTopDragHandler
import com.theathletic.ui.widgets.ModalBottomSheetLayout
import com.theathletic.ui.widgets.ModalBottomSheetType
import com.theathletic.ui.widgets.RemoteImageAsync
import com.theathletic.ui.widgets.ResourceIcon
import com.theathletic.ui.widgets.SwipeRefreshIndicator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min

class CommentsUi {
    sealed class HeaderModel {
        abstract val title: String

        data class SimpleHeader(
            val text: String
        ) : HeaderModel() {
            override val title = text
        }

        data class Header(
            val badgeUrl: String?,
            @StringRes val labelRes: Int,
            override val title: String,
            val excerpt: String,
            val authorName: String,
            val timeStamp: String,
            val backgroundColor: String,
            val liveTag: LiveTag?,
        ) : HeaderModel()
    }

    data class TeamThreadBanner(
        val teamName: String,
        val teamLogo: String,
        val teamColor: Color,
        val showChangeTeamThread: Boolean
    )

    data class TeamThreadsSheet(
        val currentThread: TeamThread,
        val secondThread: TeamThread?
    ) : ModalBottomSheetType

    data class TeamThread(
        val label: String,
        val team: Team
    )

    data class Team(
        val id: String,
        val name: String,
        val logo: String,
        val color: Color
    )

    data class LiveTag(
        @StringRes val labelRes: Int
    )

    sealed interface Comments {
        val commentId: String
        val parentId: String
        val commentText: String
        val commentLink: String
        val authorId: String
        val authorName: String
        val commentedAt: ResourceString
        val isPinned: Boolean
        val hasUserLiked: Boolean
        val likesCount: Int
        val replyCount: Int
        val isHighlighted: Boolean
        val isAuthor: Boolean
        val isDeletable: Boolean
        val commentMetadata: String?
        val authorFlairs: List<CommentFlair>?

        data class UserComment(
            override val commentId: String,
            override val parentId: String,
            override val commentText: String,
            override val commentLink: String,
            override val authorId: String,
            override val authorName: String,
            override val commentedAt: ResourceString,
            override val isPinned: Boolean,
            override val hasUserLiked: Boolean,
            override val likesCount: Int,
            override val replyCount: Int,
            override val isHighlighted: Boolean,
            override val isAuthor: Boolean,
            override val isDeletable: Boolean,
            override val commentMetadata: String?,
            override val authorFlairs: List<CommentFlair>?,
        ) : Comments

        data class UserCommentReply(
            val commentInfo: UserComment
        ) : Comments by commentInfo

        sealed interface StaffComments {
            val avatarUrl: String?
            val backgroundColor: Color
            val colorSet: ColorSet?
        }

        data class StaffComment(
            val commentInfo: UserComment,
            override val avatarUrl: String? = null,
            override val backgroundColor: Color = Color.Unspecified,
            override val colorSet: ColorSet? = null,
        ) : Comments by commentInfo, StaffComments

        data class StaffCommentReply(
            val staffInfo: StaffComment
        ) : Comments by staffInfo, StaffComments by staffInfo

        data class CommentFlair(
            val title: String,
            val contrastColor: Color
        )

        interface Interactor : CommentItemInteractor, CommentInputInteractor {
            fun onClickTweet(tweetUrl: String) {}
        }
    }

    data class ColorSet(
        val authorTextColor: Color,
        val dateTextColor: Color,
        val tagTextColor: Color,
        val tagBackgroundColor: Color,
        val commentTextColor: Color,
        val iconsColor: Color,
        val iconsTextColor: Color
    ) {
        // Default
        companion object {
            val defaultColorSet
                @Composable
                @ReadOnlyComposable
                get() = ColorSet(
                    authorTextColor = AthTheme.colors.dark700,
                    dateTextColor = AthTheme.colors.dark500,
                    tagTextColor = AthTheme.colors.dark200,
                    tagBackgroundColor = AthTheme.colors.dark700,
                    commentTextColor = AthTheme.colors.dark700,
                    iconsColor = AthTheme.colors.dark500,
                    iconsTextColor = AthTheme.colors.dark700
                )

            // Light
            val lightColorSet = ColorSet(
                authorTextColor = AthColor.Gray700,
                dateTextColor = AthColor.Gray700,
                tagTextColor = AthColor.Gray200,
                tagBackgroundColor = AthColor.Gray800,
                commentTextColor = AthColor.Gray700,
                iconsColor = AthColor.Gray800,
                iconsTextColor = AthColor.Gray700
            )

            // Dark
            val darkColorSet = ColorSet(
                authorTextColor = AthColor.Gray200,
                dateTextColor = AthColor.Gray200,
                tagTextColor = AthColor.Gray700,
                tagBackgroundColor = AthColor.Gray200,
                commentTextColor = AthColor.Gray200,
                iconsColor = AthColor.Gray200,
                iconsTextColor = AthColor.Gray300
            )
        }
    }

    @Stable
    data class FlagState(
        val openDialog: Boolean = false,
        val selectedOption: FlagReason = FlagReason.NONE,
        val commentId: String? = null
    )

    interface Interactor {
        fun onBackButtonPressed()
        fun onSortOptionSelected(selectedOption: SortType)
        fun onPullToRefresh()
        fun onFinishedScrollingToComment()
        fun onLinkClick(url: String)
        fun onClickTeamBannerChange()
        fun onDismissTeamThreadsSheet()
        fun onSwitchedTeamThread(teamId: String)
    }
}

private val CommentsUi.Comments.isARootComment: Boolean
    get() = this is CommentsUi.Comments.UserComment || this is CommentsUi.Comments.StaffComment

private fun List<CommentsUi.Comments>.isNextCommentARoot(index: Int): Boolean {
    val nextIndex = index + 1
    return nextIndex <= lastIndex && get(nextIndex).isARootComment
}

@Suppress("LongMethod") // TODO Dimas: clean this more in the UI improvements ticket
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CommentsUi(
    viewState: CommentsViewState,
    feedbackMessage: String? = null,
    interactor: CommentsUi.Interactor,
    itemInteractor: CommentsUi.Comments.Interactor,
) {
    val localFocusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val localKeyboardController = LocalSoftwareKeyboardController.current
    val feedbackMessageSnackbarHostState = remember { SnackbarHostState() }
    // We use a second snackbar host (and state) here because the undo snackbar has its own custom layout
    val undoSnackbarHostState = remember { SnackbarHostState() }

    val commentsUiModel by remember(viewState.commentsUiModel) { mutableStateOf(viewState.commentsUiModel) }
    val inputUiState by remember(viewState.inputUiState) { mutableStateOf(viewState.inputUiState) }
    val threadsUiState by remember(viewState.threadsUiState) { mutableStateOf(viewState.threadsUiState) }

    KeyboardStateHandler(inputUiState.drawerState, focusRequester, localKeyboardController, localFocusManager)
    FeedbackMessageHandler(feedbackMessage, feedbackMessageSnackbarHostState)
    AvailableUndoHandler(
        commentsUndoUserAction = inputUiState.availableUndo,
        onUndoNoLongerAvailable = {},
        commentInputInteractor = itemInteractor,
        snackbarHostState = undoSnackbarHostState
    )

    Surface {
        if (viewState.loadingState == LoadingState.INITIAL_LOADING) {
            ProgressIndicator()
        } else {
            ModalBottomSheetLayout(
                currentModal = threadsUiState.teamThreadsSheet,
                onDismissed = interactor::onDismissTeamThreadsSheet,
                modalSheetContent = {
                    threadsUiState.teamThreadsSheet?.let { sheet ->
                        CommentsTeamThreadsSheetContent(
                            sheet.currentThread,
                            sheet.secondThread,
                            onThreadSwitch = interactor::onSwitchedTeamThread
                        )
                    }
                }
            ) {
                CommentsLayout(
                    title = viewState.title,
                    header = commentsUiModel.header,
                    comments = commentsUiModel.comments,
                    commentsCount = commentsUiModel.commentsCount,
                    inputHeaderData = inputUiState.inputHeaderData,
                    teamThreadBanner = threadsUiState.teamThreadBanner,
                    inputText = inputUiState.inputText,
                    isCommentEnabled = inputUiState.isCommentEnabled,
                    enableSend = inputUiState.enableSend,
                    isCommentDrawerFeatureEnabled = inputUiState.isCommentDrawerFeatureEnabled,
                    drawerState = inputUiState.drawerState,
                    sortedBy = viewState.sortedBy,
                    loadingState = viewState.loadingState,
                    likeActionUiState = viewState.likeActionUiState,
                    scrollToIndex = viewState.scrollToIndex,
                    feedbackMessageSnackbarHostState = feedbackMessageSnackbarHostState,
                    undoSnackbarHostState = undoSnackbarHostState,
                    interactor = interactor,
                    itemInteractor = itemInteractor,
                    focusRequester = focusRequester,
                    localFocusManager = localFocusManager,
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun KeyboardStateHandler(
    drawerState: CommentsDrawerState,
    focusRequester: FocusRequester,
    localKeyboardController: SoftwareKeyboardController?,
    localFocusManager: FocusManager
) {
    LaunchedEffect(drawerState) {
        if (drawerState == CommentsDrawerState.OPEN) {
            focusRequester.requestFocus()
            localKeyboardController?.show()
        } else {
            localFocusManager.clearFocus()
            localKeyboardController?.hide()
        }
    }
}

@Composable
private fun FeedbackMessageHandler(feedbackMessage: String?, snackbarHostState: SnackbarHostState) {
    LaunchedEffect(feedbackMessage) {
        if (feedbackMessage == null) {
            snackbarHostState.currentSnackbarData?.dismiss()
        } else {
            snackbarHostState.showSnackbar(
                message = feedbackMessage,
                duration = SnackbarDuration.Short
            )
        }
    }
}

@Composable
private fun AvailableUndoHandler(
    commentsUndoUserAction: CommentsUndoUserAction?,
    onUndoNoLongerAvailable: () -> Unit,
    commentInputInteractor: CommentInputInteractor,
    snackbarHostState: SnackbarHostState
) {
    if (commentsUndoUserAction == null) {
        snackbarHostState.currentSnackbarData?.dismiss()
    } else {
        val currentUndoUserAction by rememberUpdatedState(commentsUndoUserAction)
        val currentOnUndoNoLongerAvailable by rememberUpdatedState(onUndoNoLongerAvailable)
        val resources = LocalContext.current.resources
        LaunchedEffect(commentsUndoUserAction) {
            val job = launch {
                val snackbarResult = snackbarHostState.showSnackbar(
                    message = resources.getString(currentUndoUserAction.titleStringResId),
                    duration = SnackbarDuration.Indefinite,
                    actionLabel = resources.getString(R.string.comments_undo_action_label)
                )
                when (snackbarResult) {
                    SnackbarResult.Dismissed -> currentOnUndoNoLongerAvailable()
                    SnackbarResult.ActionPerformed -> {
                        commentInputInteractor.onUndoCancel(currentUndoUserAction.priorState)
                    }
                }
            }
            delay(6000) // Requirements call for specific duration instead of the usual SnackbarDuration.SHORT
            job.cancel()
        }
    }
}

@Composable
private fun CommentsTeamThreadsSheetContent(
    currentThread: CommentsUi.TeamThread,
    secondThread: CommentsUi.TeamThread?,
    onThreadSwitch: (String) -> Unit = {}
) {
    Column {
        BottomSheetTopDragHandler()
        TeamThreadSheetContentCurrentThread(currentThread)
        secondThread?.let {
            TeamThreadSheetContentSecondThread(onThreadSwitch, secondThread)
        }
    }
}

@Composable
private fun TeamThreadSheetContentCurrentThread(currentThread: CommentsUi.TeamThread) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
            .background(AthTheme.colors.dark200)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(currentThread.team.color, shape = CircleShape)
                .size(48.dp)
        ) {
            RemoteImageAsync(
                url = currentThread.team.logo,
                placeholder = R.drawable.ic_team_logo_placeholder,
                fallbackImage = R.drawable.ic_team_logo_placeholder,
                modifier = Modifier.padding(8.dp)
            )
        }
        Text(
            text = currentThread.label,
            style = AthTextStyle.Slab.Bold.Small.copy(
                color = AthTheme.colors.dark800
            ),
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

@Composable
private fun TeamThreadSheetContentSecondThread(
    onThreadSwitch: (String) -> Unit,
    secondThread: CommentsUi.TeamThread
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 25.dp, vertical = 16.dp)
            .clickable {
                onThreadSwitch(secondThread.team.id)
            }
    ) {
        RemoteImageAsync(
            url = secondThread.team.logo,
            placeholder = R.drawable.ic_team_logo_placeholder,
            fallbackImage = R.drawable.ic_team_logo_placeholder,
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = secondThread.label,
            style = AthTextStyle.Calibre.Utility.Medium.Large.copy(
                color = AthTheme.colors.dark800
            ),
            modifier = Modifier
                .padding(start = 18.dp)
                .weight(1f)
        )
        Image(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            colorFilter = ColorFilter.tint(AthTheme.colors.dark800),
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

@Suppress("LongMethod") // TODO - McClelland - Consider consolidating params and/or breaking this into smaller methods after ATH-24093 is merged
@Composable
private fun CommentsLayout(
    title: Int?,
    header: CommentsUi.HeaderModel?,
    comments: List<CommentsUi.Comments>,
    commentsCount: Int,
    inputHeaderData: InputHeaderData,
    teamThreadBanner: CommentsUi.TeamThreadBanner?,
    inputText: String,
    isCommentEnabled: Boolean,
    enableSend: Boolean,
    isCommentDrawerFeatureEnabled: Boolean,
    drawerState: CommentsDrawerState,
    sortedBy: SortType,
    likeActionUiState: LikeActionUiState,
    loadingState: LoadingState,
    scrollToIndex: Int?,
    feedbackMessageSnackbarHostState: SnackbarHostState,
    undoSnackbarHostState: SnackbarHostState,
    interactor: CommentsUi.Interactor,
    itemInteractor: CommentsUi.Comments.Interactor,
    focusRequester: FocusRequester,
    localFocusManager: FocusManager,
) {
    Column(
        modifier = Modifier
            .background(AthTheme.colors.dark200)
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                })
            }
    ) {
        CommentsToolbar(title, interactor)

        Box(modifier = Modifier.weight(1f)) {
            CommentsListLayout(
                header = header,
                teamThreadBanner = teamThreadBanner,
                comments = comments,
                commentsCount = commentsCount,
                sortedBy = sortedBy,
                isCommentDrawerFeatureEnabled = isCommentDrawerFeatureEnabled,
                drawerState = drawerState,
                inputHeaderData = inputHeaderData,
                scrollToIndex = scrollToIndex,
                loadingState = loadingState,
                likeActionUiState = likeActionUiState,
                interactor = interactor,
                itemInteractor = itemInteractor
            )

            SnackbarHost(
                hostState = feedbackMessageSnackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .alpha(0.85f)
                    .animateContentSize()
            )

            SnackbarHost(
                hostState = undoSnackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .animateContentSize()
            ) { snackBarData ->
                Snackbar(
                    modifier = Modifier.padding(8.dp).padding(16.dp),
                    action = {
                        UndoSnackBarButton(snackBarData.actionLabel) { snackBarData.performAction() }
                    },
                    backgroundColor = AthTheme.colors.dark300,
                    content = {
                        UndoSnackbarText(snackBarData.message)
                    }
                )
            }
        }

        if (isCommentDrawerFeatureEnabled) {
            Divider(
                color = AthTheme.colors.dark300,
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
            )
            CommentsInput(
                inputText = inputText,
                drawerState = drawerState,
                isEnabled = isCommentEnabled,
                enableSend = enableSend,
                focusRequester = focusRequester,
                focusManager = localFocusManager,
                inputHeaderData = inputHeaderData,
                interactor = itemInteractor
            )
        } else {
            LegacyCommentsInput(
                inputText = inputText,
                isEnabled = isCommentEnabled,
                enableSend = enableSend,
                focusRequester = focusRequester,
                focusManager = localFocusManager,
                inputHeaderData = inputHeaderData,
                onCommentInputClick = itemInteractor::onCommentInputClick,
                onTextChanged = itemInteractor::onTextChanged,
                onSendClick = itemInteractor::onSendClick,
                onCancelInput = itemInteractor::onCancelInput
            )
        }
    }
}

@Composable
private fun UndoSnackBarButton(actionLabel: String?, onClick: (() -> Unit)?) {
    TextButton(
        onClick = { onClick?.invoke() }
    ) {
        Text(
            text = actionLabel ?: "",
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            textDecoration = TextDecoration.Underline
        )
    }
}

@Composable
private fun UndoSnackbarText(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.background(color = AthTheme.colors.dark300)
    ) {
        ResourceIcon(
            modifier = Modifier.padding(8.dp),
            resourceId = R.drawable.ic_alert_red
        )
        Text(
            text = text,
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Calibre.Utility.Medium.Small,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ProgressIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AthTheme.colors.dark200)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = AthTheme.colors.dark600,
        )
    }
}

@Composable
private fun CommentsToolbar(title: Int?, interactor: CommentsUi.Interactor) {
    title?.let {
        Toolbar(title = stringResource(id = title), onBackClicked = interactor::onBackButtonPressed)
    }
}

@Composable
private fun CommentsListLayout(
    header: CommentsUi.HeaderModel?,
    teamThreadBanner: CommentsUi.TeamThreadBanner?,
    comments: List<CommentsUi.Comments>,
    commentsCount: Int,
    sortedBy: SortType,
    isCommentDrawerFeatureEnabled: Boolean,
    drawerState: CommentsDrawerState,
    inputHeaderData: InputHeaderData,
    scrollToIndex: Int?,
    loadingState: LoadingState,
    likeActionUiState: LikeActionUiState,
    interactor: CommentsUi.Interactor,
    itemInteractor: CommentsUi.Comments.Interactor,
) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(scrollToIndex) {
        if (scrollToIndex != null) {
            val offset = if (scrollToIndex == 0) 160 else 0
            lazyListState.animateScrollToItem(index = scrollToIndex, scrollOffset = offset)
            interactor.onFinishedScrollingToComment()
        }
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = loadingState == LoadingState.RELOADING),
        indicator = { state, triggerDp ->
            SwipeRefreshIndicator(state = state, refreshTriggerDistance = triggerDp)
        },
        onRefresh = interactor::onPullToRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 0.dp)
        ) {
            item {
                Column(modifier = Modifier.conditional(comments.isEmpty()) { fillParentMaxSize() }) {
                    HeaderSection(header, teamThreadBanner, sortedBy, commentsCount, interactor)
                    if (comments.isEmpty()) {
                        EmptyComments()
                    }
                }
            }

            itemsIndexed(comments) { index, item ->
                Comment(
                    comment = item,
                    interactor = itemInteractor,
                    index = index,
                    isLikeEnabled = likeActionUiState.isEnabled(item.commentId)
                )
                if (comments.isNextCommentARoot(index)) Divider(color = AthTheme.colors.dark100, thickness = 6.dp)
            }
        }

        if (shouldShowScrollToTop(isCommentDrawerFeatureEnabled, drawerState, inputHeaderData)) {
            ScrollToTop(scope = coroutineScope, listState = lazyListState)
        }
    }

    if (comments.isNotEmpty()) {
        CommentsImpressionTracker(
            listState = lazyListState,
            listItems = comments,
            onViewVisibilityChanged = itemInteractor::onVisibilityChanged
        )
    }
}

private fun shouldShowScrollToTop(
    isCommentDrawerFeatureEnabled: Boolean,
    drawerState: CommentsDrawerState,
    inputHeaderData: InputHeaderData
) = (isCommentDrawerFeatureEnabled && drawerState != CommentsDrawerState.OPEN) ||
    (isCommentDrawerFeatureEnabled.not() && inputHeaderData is InputHeaderData.EmptyHeaderData)

@Composable
fun EmptyComments() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_comments_empty),
            contentDescription = null,
            colorFilter = ColorFilter.tint(AthTheme.colors.dark500)
        )
        Text(
            text = stringResource(id = R.string.comments_empty_placeholder),
            color = AthTheme.colors.dark500,
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun CommentsImpressionTracker(
    listState: LazyListState,
    listItems: List<CommentsUi.Comments>,
    onViewVisibilityChanged: (String, Int, Float) -> Unit,
) {
    val visibilityState = remember { mutableMapOf<Int, Float>() }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .collect {
                it.visibleItemsInfo.forEachIndexed { localIndex, composable ->
                    listItems.getOrNull(localIndex)?.let { comment ->
                        val visibility = when (localIndex) {
                            0 -> (composable.size + min(composable.offset, 0)).toFloat() / composable.size

                            listState.layoutInfo.visibleItemsInfo.indices.last -> {
                                val lastVisiblePx = listState.layoutInfo.viewportEndOffset - composable.offset
                                lastVisiblePx.toFloat() / composable.size
                            }

                            else -> 1.0f
                        }.coerceIn(0f, 1f)

                        if (visibilityState[composable.index] != visibility) {
                            visibilityState[composable.index] = visibility
                            onViewVisibilityChanged(comment.commentId, localIndex, visibility)
                        }
                    }
                }
            }
    }
}

@Preview
@Composable
private fun CommentsScreenPreview() {
    CommentsUi(
        viewState = CommentsPreviewData.viewState.copy(
            commentsUiModel = CommentsUiModel(comments = listOf(userCommentBase, staffCommentBase))
        ),
        interactor = CommentsPreviewData.CommentsUiPreviewInteractor,
        itemInteractor = CommentsPreviewData.CommentsItemPreviewInteractor
    )
}

@Preview
@Composable
private fun CommentsScreenPreview_Light() {
    AthleticTheme(lightMode = true) {
        CommentsUi(
            viewState = CommentsPreviewData.viewState.copy(
                commentsUiModel = CommentsUiModel(comments = listOf(userCommentBase, staffCommentBase, userCommentBase, userCommentBase, userCommentBase))
            ),
            interactor = CommentsPreviewData.CommentsUiPreviewInteractor,
            itemInteractor = CommentsPreviewData.CommentsItemPreviewInteractor
        )
    }
}

@Preview
@Composable
private fun CommentsScreenEmptyPreview_Light() {
    AthleticTheme(lightMode = true) {
        CommentsUi(
            viewState = CommentsPreviewData.viewState.copy(commentsUiModel = CommentsUiModel()),
            interactor = CommentsPreviewData.CommentsUiPreviewInteractor,
            itemInteractor = CommentsPreviewData.CommentsItemPreviewInteractor
        )
    }
}

@Preview
@Composable
private fun CommentsScreenEmptyPreview_Dark() {
    AthleticTheme(lightMode = false) {
        CommentsUi(
            viewState = CommentsPreviewData.viewState.copy(commentsUiModel = CommentsUiModel()),
            interactor = CommentsPreviewData.CommentsUiPreviewInteractor,
            itemInteractor = CommentsPreviewData.CommentsItemPreviewInteractor
        )
    }
}

@Preview
@Composable
private fun CommentsScreenBannerWithCommentsPreview(
    @PreviewParameter(AthleticThemeProvider::class)
    athleticTheme: PreviewContent,
) {
    athleticTheme {
        CommentsUi(
            viewState = CommentsPreviewData.viewState,
            interactor = CommentsPreviewData.CommentsUiPreviewInteractor,
            itemInteractor = CommentsPreviewData.CommentsItemPreviewInteractor
        )
    }
}

@Preview
@Composable
private fun CommentsTeamThreadsSheetPreview(
    @PreviewParameter(AthleticThemeProvider::class)
    athleticTheme: PreviewContent,
) {
    athleticTheme {
        CommentsUi(
            viewState = CommentsPreviewData.viewState,
            interactor = CommentsPreviewData.CommentsUiPreviewInteractor,
            itemInteractor = CommentsPreviewData.CommentsItemPreviewInteractor
        )
    }
}