package com.theathletic.feed.ui.models

import com.theathletic.analytics.AnalyticsPayload
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.podcast.ui.widget.TinyPodcastPlayer
import com.theathletic.ui.CarouselUiModel
import com.theathletic.ui.UiModel
import com.theathletic.ui.binding.ParameterizedString
import com.theathletic.utility.RecyclerLayout

data class FeedPodcastEpisodeGrouped(
    val id: Long,
    val imageUrl: String,
    val title: String,
    val date: ParameterizedString,
    val duration: ParameterizedString,
    val isDurationTimeRemaining: Boolean,
    val durationSeconds: Int,
    val elapsedSeconds: Int,
    val isFinished: Boolean,
    val downloadProgress: Int,
    val isDownloading: Boolean,
    val isDownloaded: Boolean,
    val podcastPlayerState: TinyPodcastPlayer.ViewState,
    val analyticsPayload: FeedPodcastEpisodeAnalyticsPayload,
    override val impressionPayload: ImpressionPayload?
) : UiModel {

    override val stableId = "FeedPodcastEpisodeGrouped:$id"

    interface Interactor {
        fun onPodcastEpisodeClicked(
            episodeId: Long,
            analyticsPayload: FeedPodcastEpisodeAnalyticsPayload
        )
        fun onPodcastControlClicked(
            episodeId: Long,
            analyticsPayload: FeedPodcastEpisodeAnalyticsPayload
        )
        fun onPodcastEpisodeOptionsClicked(
            episodeId: Long,
            isPlayed: Boolean,
            isDownloaded: Boolean
        )
    }
}

data class RecommendedPodcastsGrid(
    val id: Int,
    val isTablet: Boolean,
    override val carouselItemModels: List<UiModel>
) : CarouselUiModel {
    override val stableId = "RecommendedPodcastsCarousel:$id"

    val recyclerLayout = if (isTablet) {
        RecyclerLayout.RECOMMENDED_PODCASTS_TABLET_GIRD
    } else {
        RecyclerLayout.GRID_VERTICAL
    }
}

data class RecommendedPodcastSeriesGridItem(
    val id: Int,
    val title: String,
    val category: String,
    val imageUrl: String,
    val analyticsPayload: FeedPodcastShowAnalyticsPayload,
    override val impressionPayload: ImpressionPayload
) : UiModel {
    override val stableId = "RecommendedPodcastSeries:$id"

    interface Interactor {
        fun onPodcastSeriesClicked(
            podcastShowId: Long,
            analyticsPayload: FeedPodcastShowAnalyticsPayload
        )
    }
}

data class FeedPodcastEpisodeAnalyticsPayload(
    val moduleIndex: Int,
    val container: String,
    val vIndex: Int? = null,
    val hIndex: Int = -1
) : AnalyticsPayload

data class FeedPodcastShowAnalyticsPayload(
    val moduleIndex: Int,
    val container: String,
    val vIndex: Int? = null,
    val hIndex: Int? = null
) : AnalyticsPayload