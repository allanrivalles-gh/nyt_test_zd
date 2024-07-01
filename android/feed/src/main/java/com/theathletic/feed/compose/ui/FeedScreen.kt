package com.theathletic.feed.compose.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.theathletic.ads.ui.AdState
import com.theathletic.feed.compose.ui.components.A1Layout
import com.theathletic.feed.compose.ui.components.A1LayoutUiModel
import com.theathletic.feed.compose.ui.components.DropzoneLayout
import com.theathletic.feed.compose.ui.components.DropzoneLayoutUiModel
import com.theathletic.feed.compose.ui.components.EndOfFeedLayout
import com.theathletic.feed.compose.ui.components.FeaturedGameLayout
import com.theathletic.feed.compose.ui.components.FeaturedGameLayoutUiModel
import com.theathletic.feed.compose.ui.components.FeedDetailsMenuModalSheet
import com.theathletic.feed.compose.ui.components.HeadlinesLayout
import com.theathletic.feed.compose.ui.components.HeadlinesLayoutUiModel
import com.theathletic.feed.compose.ui.components.HeroCarouselLayout
import com.theathletic.feed.compose.ui.components.HeroCarouselLayoutUiModel
import com.theathletic.feed.compose.ui.components.HeroListLayout
import com.theathletic.feed.compose.ui.components.HeroListLayoutUiModel
import com.theathletic.feed.compose.ui.components.ListLayout
import com.theathletic.feed.compose.ui.components.ListLayoutUiModel
import com.theathletic.feed.compose.ui.components.LoadingNextFeedPageIndicatorLayout
import com.theathletic.feed.compose.ui.components.MostPopularLayout
import com.theathletic.feed.compose.ui.components.MostPopularLayoutUiModel
import com.theathletic.feed.compose.ui.components.ScoresCarouselLayout
import com.theathletic.feed.compose.ui.components.ScoresCarouselLayoutUiModel
import com.theathletic.feed.compose.ui.components.TopperHeroLayout
import com.theathletic.feed.compose.ui.components.TopperHeroLayoutUiModel
import com.theathletic.feed.compose.ui.components.podcast.PodcastLayout
import com.theathletic.feed.compose.ui.components.podcast.PodcastLayoutUiModel
import com.theathletic.feed.compose.ui.interaction.ItemInteractor
import com.theathletic.impressions.ImpressionContainer
import com.theathletic.share.ShareTitle
import com.theathletic.share.asString
import com.theathletic.share.startShareTextActivity
import com.theathletic.ui.collectWithLifecycle
import com.theathletic.ui.widgets.ModalBottomSheetLayout
import com.theathletic.ui.widgets.SwipeRefreshIndicator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
internal fun FeedScreen(viewModel: FeedViewModel) {
    val state by viewModel.viewState.collectAsState()
    val context = LocalContext.current

    val feedInteractor = ItemInteractor(
        onClick = { viewModel.itemClicked(it) },
        onLongClick = { viewModel.itemLongClicked(it) },
        onSeeAllClick = { link, analyticsPayload -> viewModel.onSeeAllClick(link, analyticsPayload) },
        onNavLinkClick = { item, link, linkType -> viewModel.onNavLinkClick(item, link, linkType) },
        onVisibilityChange = { visibility, item -> viewModel.onImpressionChange(visibility, item) }
    )

    LaunchedEffect(Unit) {
        viewModel.viewEvent.filterIsInstance<FeedEvent.Share>().collect { event ->
            context.startShareTextActivity(
                ShareTitle.DEFAULT.asString(context),
                event.permalink
            )
        }
    }

    when {
        state.isLoading -> Timber.d("show some loading screen")
        else -> {
            ModalBottomSheetLayout(
                isVisible = state.modalSheetOptions.isNotEmpty(),
                onDismissed = viewModel::dismissModalSheet,
                content = {
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(isRefreshing = state.isRefreshing),
                        indicator = { swipeRefreshState, triggerDp ->
                            SwipeRefreshIndicator(
                                state = swipeRefreshState,
                                refreshTriggerDistance = triggerDp
                            )
                        },
                        onRefresh = {
                            viewModel.refresh()
                        }
                    ) {
                        ImpressionContainer {
                            FeedUi(
                                state.uiModel,
                                feedInteractor,
                                onNextPageRequested = viewModel::fetchNextPage,
                                scrollToTopRequests = viewModel.viewEvent.filterIsInstance()
                            )
                        }
                    }
                },
                modalSheetContent = {
                    FeedDetailsMenuModalSheet(state.modalSheetOptions) {
                        viewModel.detailsMenuOptionSelected(it)
                    }
                },
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun FeedUi(
    uiModel: FeedUiModel,
    itemInteractor: ItemInteractor,
    onNextPageRequested: () -> Unit,
    scrollToTopRequests: Flow<FeedEvent.ScrollToTop>
) {
    val state = rememberLazyListState()
    val scoresCarouselState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val nextPageTriggerConfiguration = remember(uiModel.layouts.size) {
        NextPageTriggerConfiguration(
            threshold = 3,
            currentItemsCount = uiModel.layouts.size,
        )
    }
    val onNextPageRequested by rememberUpdatedState(onNextPageRequested)

    scrollToTopRequests.collectWithLifecycle {
        coroutineScope.launch { state.animateScrollToItem(0) }
    }

    LaunchedEffect(uiModel.scoresCarouselPosition) {
        scoresCarouselState.animateScrollToItem(uiModel.scoresCarouselPosition, scrollOffset = -50)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { testTagsAsResourceId = true }
            .testTag("FeedList"),
        state = state
    ) {
        itemsIndexed(
            uiModel.layouts,
            key = { _, item -> item.id },
            contentType = { _, item -> item::class }
        ) { index, layout ->
            LaunchedEffect(nextPageTriggerConfiguration) {
                if (nextPageTriggerConfiguration.shouldTrigger(index = index)) onNextPageRequested()
            }

            layout.GetComposableLayout(itemInteractor, scoresCarouselState)

            if (layout.hasSpacer(nextLayout = uiModel.layouts.getOrNull(index + 1))) {
                Spacer(modifier = Modifier.padding(top = 6.dp))
            }
        }
        item {
            if (uiModel.pageInfo.hasNextPage) {
                LoadingNextFeedPageIndicatorLayout()
            } else {
                EndOfFeedLayout {
                    coroutineScope.launch { state.animateScrollToItem(0) }
                }
            }
        }
    }
}

@Composable
private fun LayoutUiModel.GetComposableLayout(itemInteractor: ItemInteractor, scoresCarouselState: LazyListState) {
    when (this) {
        is TopperHeroLayoutUiModel -> TopperHeroLayout(this, itemInteractor)
        is HeroListLayoutUiModel -> HeroListLayout(this, itemInteractor)
        is HeroCarouselLayoutUiModel -> HeroCarouselLayout(this, itemInteractor)
        is A1LayoutUiModel -> A1Layout(this, itemInteractor)
        is ListLayoutUiModel -> ListLayout(this, itemInteractor)
        is HeadlinesLayoutUiModel -> HeadlinesLayout(this, itemInteractor)
        is MostPopularLayoutUiModel -> MostPopularLayout(this, itemInteractor)
        is PodcastLayoutUiModel -> PodcastLayout(this)
        is FeaturedGameLayoutUiModel -> FeaturedGameLayout(this, itemInteractor)
        is ScoresCarouselLayoutUiModel -> ScoresCarouselLayout(this, itemInteractor, scoresCarouselState)
        is DropzoneLayoutUiModel -> DropzoneLayout(this)
    }
}

private fun LayoutUiModel.hasSpacer(nextLayout: LayoutUiModel?): Boolean {
    if (nextLayout is DropzoneLayoutUiModel) return false
    if (this is DropzoneLayoutUiModel) return isCollapsed
    return true
}

private val DropzoneLayoutUiModel.isCollapsed: Boolean
    get() {
        val item = items.firstOrNull() ?: return false
        return item.adState == AdState.Collapsed
    }