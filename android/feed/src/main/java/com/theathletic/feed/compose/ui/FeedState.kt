package com.theathletic.feed.compose.ui

import com.theathletic.feed.compose.ui.ads.FeedAdsState
import com.theathletic.feed.compose.ui.components.FeedDetailsMenuOption

internal data class FeedState(
    val uiModel: FeedUiModel = FeedUiModel(
        id = "",
        layouts = emptyList(),
        pageInfo = FeedUiModel.PageInfo(currentPage = -1, hasNextPage = true),
        scoresCarouselPosition = 0
    ),
    val adsState: FeedAdsState = FeedAdsState(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isLoadingNextPage: Boolean = false,
    val modalSheetOptions: List<FeedDetailsMenuOption> = emptyList(),
) {
    val isFetching: Boolean
        get() = isLoading || isRefreshing || isLoadingNextPage
}

internal sealed interface FeedEvent {
    data class Share(val permalink: String) : FeedEvent

    object ScrollToTop : FeedEvent
}