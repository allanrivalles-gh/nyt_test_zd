package com.theathletic.liveblog.ui

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.os.bundleOf
import com.theathletic.extension.toDp
import com.theathletic.fragment.AthleticComposeFragment
import com.theathletic.fragment.compose.rememberViewModel
import com.theathletic.ui.widgets.ModalBottomSheetLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class LiveBlogComposeFragment : AthleticComposeFragment<
    LiveBlogViewModel,
    LiveBlogContract.ViewState>() {

    companion object {
        private const val EXTRA_LIVE_BLOG_ID = "extra_live_blog_id"
        private const val EXTRA_INITIAL_POST_ID = "extra_initial_post_id"

        fun newInstance(liveBlogId: String, postId: String? = null) = LiveBlogComposeFragment().apply {
            arguments = bundleOf(
                EXTRA_LIVE_BLOG_ID to liveBlogId,
                EXTRA_INITIAL_POST_ID to postId
            )
        }
    }

    override fun setupViewModel() = getViewModel<LiveBlogViewModel> {
        parametersOf(
            LiveBlogViewModel.Params(
                arguments?.getString(EXTRA_LIVE_BLOG_ID) ?: "",
                initialPostId = arguments?.getString(EXTRA_INITIAL_POST_ID),
                isDayMode = displayPreferences.shouldDisplayDayMode(context),
                screenWidth = resources.displayMetrics.widthPixels.toDp,
                screenHeight = resources.displayMetrics.heightPixels.toDp,
                status = null,
                leagueId = "",
                gameId = "",
            ),
            navigator
        )
    }

    @Composable
    override fun Compose(state: LiveBlogContract.ViewState) {
        val coroutineScope = rememberCoroutineScope()
        val listState = rememberLazyListState()
        // If we have a sponsor banner then the first live blog post is the third item in the list,
        // if we don't then it is the second item in the list
        val firstPostIndex by produceState(initialValue = 1, state.liveBlog.sponsorBanner) {
            value = when (state.liveBlog.sponsorBanner) {
                null -> 1
                else -> 2
            }
        }

        ModalBottomSheetLayout(
            currentModal = state.currentBottomSheetModal,
            onDismissed = { viewModel.dismissBottomSheet() },
            modalSheetContent = { LiveBlogTextSettingsBottomSheet(it.liveBlogId) }
        ) {
            LiveBlogScreen(
                isLoading = state.isLoading,
                liveBlog = state.liveBlog,
                contentTextSize = state.contentTextSize,
                stagedPostsCount = state.stagedPostsCount,
                initialPostIndex = state.initialPostIndex,
                listState = listState,
                interactor = viewModel,
            )
        }
        LaunchedEffect(Unit) {
            viewModel.eventConsumer.collectLatest { event ->
                when (event) {
                    is LiveBlogContract.Event.ScrollToFirstPost -> coroutineScope.launch {
                        listState.animateScrollToItem(firstPostIndex)
                    }
                }
            }
        }
    }

    @Composable
    private fun LiveBlogTextSettingsBottomSheet(liveBlogId: String) {
        val presenter: TextStyleBottomSheetViewModel = rememberViewModel(
            TextStyleBottomSheetViewModel.Params(liveBlogId = liveBlogId)
        )
        val viewState by presenter.viewState.collectAsState(initial = null)
        val state = viewState ?: return

        LiveBlogTextSettingsBottomSheet(
            dayNightMode = state.dayNightMode,
            contentTextSize = state.textSize,
            displaySystemThemeButton = state.displaySystemThemeButton,
            interactor = presenter
        )
    }
}