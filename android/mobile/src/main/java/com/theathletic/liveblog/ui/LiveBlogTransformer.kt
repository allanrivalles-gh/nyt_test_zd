package com.theathletic.liveblog.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.liveblog.ui.renders.LiveBlogRenderers
import com.theathletic.ui.Transformer

class LiveBlogTransformer @AutoKoin constructor(
    private val liveBlogRenderers: LiveBlogRenderers
) : Transformer<
        LiveBlogState,
        LiveBlogContract.ViewState> {
    override fun transform(data: LiveBlogState): LiveBlogContract.ViewState {
        val stagedCount = data.stagedLiveBlog?.posts?.size ?: 0
        return LiveBlogContract.ViewState(
            liveBlog = liveBlogRenderers.renderLiveBlog(data.liveBlog, data.tweetUrlToHtml, data.adMap),
            stagedPostsCount = stagedCount.minus(
                data.liveBlog?.posts?.size ?: stagedCount
            ).coerceAtLeast(0),
            initialPostIndex = data.liveBlog?.posts?.indexOfFirst { it.id == data.initialPostId } ?: -1,
            currentBottomSheetModal = data.currentBottomSheetModal,
            isLoading = data.loadingState.isFreshLoadingState,
            contentTextSize = data.contentTextSize,
        )
    }
}