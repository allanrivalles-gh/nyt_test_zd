package com.theathletic.comments.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.theathletic.analytics.data.ClickSource
import com.theathletic.comments.analytics.CommentsAnalyticsPayload
import com.theathletic.comments.analytics.CommentsLaunchAction
import com.theathletic.comments.analytics.CommentsParamModel
import com.theathletic.comments.ui.components.CommentsUi
import com.theathletic.comments.ui.components.FlaggedCommentAlertDialog
import com.theathletic.comments.ui.components.TempBanNotification
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.data.ContentDescriptor
import com.theathletic.gamedetail.ui.GameDetailEvent
import com.theathletic.gamedetail.ui.GameDetailEventConsumer
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.collectWithLifecycle
import com.theathletic.utility.getSerializableCompat
import com.theathletic.utility.safeLet
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private const val EXTRA_SOURCE_DESCRIPTOR = "extra_source_descriptor"
private const val EXTRA_SOURCE_TYPE = "extra_source_type"
private const val EXTRA_ENTRY_ACTIVE = "extra_entry_active"
private const val EXTRA_LAUNCH_ACTION = "extra_launch_action"
private const val EXTRA_ANALYTICS_PAYLOAD = "extra_analytics_payload"
private const val EXTRA_CLICK_SOURCE = "extra_click_source"

class CommentsFragment : Fragment() {
    val navigator by inject<ScreenNavigator> { parametersOf(requireActivity()) }
    private var myViewModel: CommentsViewModel? = null

    companion object {
        @Suppress("LongParameterList")
        fun newInstance(
            contentDescriptor: ContentDescriptor?,
            type: CommentsSourceType,
            isEntryActive: Boolean,
            launchAction: CommentsLaunchAction?,
            analyticsPayload: CommentsAnalyticsPayload?,
            clickSource: ClickSource?,
        ) = CommentsFragment().apply {
            arguments = bundleOf(
                EXTRA_SOURCE_DESCRIPTOR to contentDescriptor,
                EXTRA_SOURCE_TYPE to type,
                EXTRA_ENTRY_ACTIVE to isEntryActive,
                EXTRA_LAUNCH_ACTION to launchAction,
                EXTRA_ANALYTICS_PAYLOAD to analyticsPayload,
                EXTRA_CLICK_SOURCE to clickSource
            )
        }
    }

    private fun presenterParameters() = safeLet(
        arguments,
        arguments?.getParcelable<ContentDescriptor>(EXTRA_SOURCE_DESCRIPTOR)
    ) { arguments, sourceDescriptor ->
        CommentsParamModel(
            sourceDescriptor = sourceDescriptor,
            sourceType = (arguments.getSerializableCompat(EXTRA_SOURCE_TYPE) as? CommentsSourceType)!!,
            isEntryActive = arguments.getBoolean(EXTRA_ENTRY_ACTIVE),
            launchAction = arguments.getSerializableCompat(EXTRA_LAUNCH_ACTION) as? CommentsLaunchAction,
            analyticsPayload = arguments.getSerializableCompat(EXTRA_ANALYTICS_PAYLOAD) as? CommentsAnalyticsPayload,
            clickSource = arguments.getSerializableCompat(EXTRA_CLICK_SOURCE) as? ClickSource
        )
    }

    private fun emptyParams(): CommentsParamModel {
        navigator.finishActivity()
        return CommentsParamModel(
            sourceDescriptor = ContentDescriptor(-1, ""),
            sourceType = CommentsSourceType.ARTICLE,
            isEntryActive = false,
            launchAction = null,
            analyticsPayload = null,
            clickSource = null
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): ComposeView {
        return ComposeView(requireContext()).apply {
            setContent {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                val viewModel = koinViewModel<CommentsViewModel>(
                    parameters = {
                        parametersOf(
                            presenterParameters() ?: emptyParams(),
                            navigator,
                        )
                    }
                )
                val lifecycle = LocalLifecycleOwner.current.lifecycle
                DisposableEffect(lifecycle) {
                    val observer = LifecycleEventObserver { owner, event ->
                        when (event) {
                            Lifecycle.Event.ON_CREATE -> viewModel.onCreate(owner)
                            Lifecycle.Event.ON_RESUME -> viewModel.onResume(owner)
                            Lifecycle.Event.ON_PAUSE -> viewModel.onPause(owner)
                            else -> {}
                        }
                    }
                    lifecycle.addObserver(observer)
                    onDispose { lifecycle.removeObserver(observer) }
                }

                myViewModel = viewModel
                AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
                    CommentsScreen(viewModel)
                }
            }
        }
    }

    @Composable
    fun CommentsScreen(viewModel: CommentsViewModel) {
        val viewState by viewModel.viewState.collectAsState()
        var banDaysLeft by remember { mutableStateOf(-1) }
        var flagState by remember { mutableStateOf(CommentsUi.FlagState()) }
        var feedbackMessageRes: Int? by remember { mutableStateOf(null) }

        val isTabSelected = LocalCommentsTabSelected.current
        LaunchedEffect(isTabSelected) { viewModel.onTabSelectionChanged(isTabSelected) }

        viewModel.viewEvents.collectWithLifecycle { event ->
            when (event) {
                is CommentsViewEvent.ShareComment -> navigator.startShareTextActivity(event.commentLink)
                is CommentsViewEvent.NavigateBack -> navigator.finishActivity()
                is CommentsViewEvent.NavigateToCodeOfConduct -> navigator.showCodeOfConduct()
                is CommentsViewEvent.ShowTempBanMessage -> banDaysLeft = event.daysLeft
                is CommentsViewEvent.ShowCreateAccount -> navigator.startCreateAccountWallActivity()
                is CommentsViewEvent.ShowCodeOfConduct -> navigator.startCodeOfConductSheetActivityForResult()
                is CommentsViewEvent.ShowPaywall -> navigator.startPlansActivity(ClickSource.PAYWALL)
                is CommentsViewEvent.FlagComment -> {
                    flagState = CommentsUi.FlagState(openDialog = true, commentId = event.commentId)
                }
                is CommentsViewEvent.ShowFeedbackMessage -> feedbackMessageRes = event.stringRes
                is CommentsViewEvent.OpenTweet -> navigator.openLink(event.tweetUrl)
                is CommentsViewEvent.OpenUrl -> navigator.openLink(event.url)
            }
        }

        /**
         * If navigating to a specific comment is what causes the fragment to be created, the fragments params
         * drive the process of scrolling to the target comment once comments have been loaded. If this fragment
         * has already been created, it listens for subsequent events from outside the fragment to navigate to
         * specific comments.
         *
         * When we eliminate the fragment and migrate to a more purely Compose implementation, this should be
         * eliminated, and we should be pushing the LaunchAction into the CommentsScreen composable.
         */
        get<GameDetailEventConsumer>().collectWithLifecycle { event ->
            when (event) {
                is GameDetailEvent.SelectCommentInDiscussionTab -> {
                    viewModel.onExternalScrollToCommentRequest(event.commentId)
                }
                is GameDetailEvent.ReplyToCommentInDiscussionTab -> {
                    viewModel.onExternalReplyToCommentRequest(event.commentId, event.parentId)
                }
                else -> {
                    /* Nothing */
                }
            }
        }

        Box {
            CommentsUi(
                viewState = viewState,
                feedbackMessage = feedbackMessageRes.resourceToString(),
                interactor = viewModel,
                itemInteractor = viewModel
            )

            VerifyAuthorStatus(
                banDaysLeft = banDaysLeft,
                flagState = flagState,
                onBanStateChanged = { banDaysLeft = it },
                onFlagStateChanged = { flagState = it },
                onCodeOfConductClick = viewModel::showCodeOfConduct,
                onFlagCommentConfirmClick = {
                    viewModel.onFlagComment(flagState.commentId.orEmpty(), flagState.selectedOption)
                }
            )
        }
    }

    @Composable
    private fun VerifyAuthorStatus(
        banDaysLeft: Int,
        flagState: CommentsUi.FlagState,
        onBanStateChanged: (Int) -> Unit,
        onFlagStateChanged: (CommentsUi.FlagState) -> Unit,
        onCodeOfConductClick: () -> Unit,
        onFlagCommentConfirmClick: () -> Unit,
    ) {
        if (banDaysLeft > 0) {
            TempBanNotification(
                modifier = Modifier.padding(bottom = 72.dp),
                numberOfDays = banDaysLeft,
                onCodeOfConductClick = onCodeOfConductClick,
                onDismiss = { onBanStateChanged(-1) }
            )
        }
        if (flagState.openDialog) {
            FlaggedCommentAlertDialog(
                selectedOption = flagState.selectedOption,
                onDismissRequest = { onFlagStateChanged(flagState.copy(openDialog = false)) },
                onConfirmClick = {
                    onFlagStateChanged(flagState.copy(openDialog = false))
                    onFlagCommentConfirmClick()
                },
                onSelectedClick = { flagReason ->
                    onFlagStateChanged(flagState.copy(selectedOption = flagReason))
                }
            )
        }
    }

    @Composable
    private fun Int?.resourceToString() =
        if (this == null) null else stringResource(id = this)
}

// by default we want it to be true, which means, it is not in some tabbed context
val LocalCommentsTabSelected = compositionLocalOf { true }