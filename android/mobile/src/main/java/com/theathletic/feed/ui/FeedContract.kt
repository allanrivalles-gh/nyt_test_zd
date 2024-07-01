package com.theathletic.feed.ui

import com.theathletic.ads.ui.AdWrapperUiModel
import com.theathletic.analytics.impressions.ImpressionVisibilityListener
import com.theathletic.feed.ui.models.BasicSectionHeader
import com.theathletic.feed.ui.models.FeedAnnouncement
import com.theathletic.feed.ui.models.FeedAuthorHeader
import com.theathletic.feed.ui.models.FeedCuratedCarouselItem
import com.theathletic.feed.ui.models.FeedCuratedGroupedItem
import com.theathletic.feed.ui.models.FeedCuratedGroupedItemRead
import com.theathletic.feed.ui.models.FeedCuratedTopperHero
import com.theathletic.feed.ui.models.FeedEndOfFeed
import com.theathletic.feed.ui.models.FeedHeadlineListItem
import com.theathletic.feed.ui.models.FeedHeroItem
import com.theathletic.feed.ui.models.FeedHeroTabletItem
import com.theathletic.feed.ui.models.FeedInsiderItem
import com.theathletic.feed.ui.models.FeedLeftImageItem
import com.theathletic.feed.ui.models.FeedMostPopularArticle
import com.theathletic.feed.ui.models.FeedPodcastEpisodeGrouped
import com.theathletic.feed.ui.models.FeedScoresCarouselItem
import com.theathletic.feed.ui.models.FeedSeeAllButton
import com.theathletic.feed.ui.models.FeedSideBySideItem
import com.theathletic.feed.ui.models.FeedSingleHeadlineItem
import com.theathletic.feed.ui.models.FeedSpotlightModel
import com.theathletic.feed.ui.models.FeedTopperGroupedItem
import com.theathletic.feed.ui.models.LiveBlogCarouselItem
import com.theathletic.feed.ui.models.LiveRoomUiModel
import com.theathletic.feed.ui.models.RecommendedPodcastSeriesGridItem
import com.theathletic.feed.ui.models.StandaloneFeedHeader
import com.theathletic.frontpage.ui.trendingtopics.TrendingTopicGridItem
import com.theathletic.presenter.Interactor
import com.theathletic.ui.UiModel
import com.theathletic.ui.list.AthleticFeedAdapter
import com.theathletic.utility.PrivacyRegion

interface FeedContract {

    interface Presenter :
        Interactor,
        AthleticFeedAdapter.OnPostBindAtPositionListener,
        ImpressionVisibilityListener,
        TrendingTopicGridItem.Interactor,
        FeedAnnouncement.Interactor,
        FeedPodcastEpisodeGrouped.Interactor,
        RecommendedPodcastSeriesGridItem.Interactor,
        FeedCuratedTopperHero.Interactor,
        FeedCuratedGroupedItem.Interactor,
        FeedCuratedGroupedItemRead.Interactor,
        FeedTopperGroupedItem.Interactor,
        FeedSingleHeadlineItem.Interactor,
        FeedHeadlineListItem.Interactor,
        FeedSeeAllButton.Interactor,
        FeedMostPopularArticle.Interactor,
        FeedLeftImageItem.Interactor,
        FeedSideBySideItem.Interactor,
        FeedHeroItem.Interactor,
        FeedCuratedCarouselItem.Interactor,
        FeedInsiderItem.Interactor,
        FeedScoresCarouselItem.Interactor,
        FeedSpotlightModel.Interactor,
        BasicSectionHeader.Interactor,
        FeedAuthorHeader.Interactor,
        FeedEndOfFeed.Interactor,
        FeedHeroTabletItem.Interactor,
        LiveRoomUiModel.Interactor,
        AdWrapperUiModel.Interactor,
        LiveBlogCarouselItem.Interactor {
        fun onPullToRefresh()
    }

    data class ViewState(
        val showSpinner: Boolean = true,
        val uiModels: List<UiModel> = emptyList(),
        val followHeader: StandaloneFeedHeader
    ) : com.theathletic.ui.ViewState

    sealed class Event : com.theathletic.utility.Event() {
        class ShowArticleLongClickSheet(
            val articleId: Long,
            val isBookmarked: Boolean,
            val isRead: Boolean
        ) : Event()
        class ShowDiscussionLongClickSheet(val discussionId: Long) : Event()
        object ScrollToTopOfFeed : Event()
        object ScrollToTopHeadlines : Event()
        object SolicitAppRating : Event()
        class SolicitPrivacyUpdate(val privacyRegion: PrivacyRegion) : Event()
        class ShowPodcastEpisodeOptionSheet(
            val episodeId: Long,
            val isFinished: Boolean,
            val isDownloaded: Boolean
        ) : Event()
        class TrackFeedView(val trackFeedView: () -> Unit) : Event()
    }
}