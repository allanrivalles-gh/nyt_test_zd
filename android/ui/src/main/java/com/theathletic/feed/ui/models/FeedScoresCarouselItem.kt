package com.theathletic.feed.ui.models

import com.theathletic.analytics.AnalyticsPayload
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.ui.CarouselUiModel
import com.theathletic.ui.UiModel
import com.theathletic.ui.binding.ParameterizedString
import com.theathletic.ui.stableId

data class FeedScoresCarousel(
    val id: Int,
    override val carouselItemModels: List<UiModel>,
    val firstVisibleIndex: Short
) : CarouselUiModel {
    override val stableId = "FeedScoresCarousel:$id:${carouselItemModels.stableId}"
}

data class FeedScoresCarouselItem(
    val id: String,
    val leagueId: Long,

    val topStatusText: ParameterizedString?,
    val isTopStatusGreen: Boolean,
    val bottomStatusText: ParameterizedString,
    val isBottomStatusRed: Boolean,

    val topTeamLogoUrl: String,
    val topTeamName: ParameterizedString,
    val topTeamScore: String,
    val topTeamFaded: Boolean,

    val bottomTeamLogoUrl: String,
    val bottomTeamName: ParameterizedString,
    val bottomTeamScore: String,
    val bottomTeamFaded: Boolean,

    val showDiscussButton: Boolean,

    val scoresAnalyticsPayload: FeedScoresAnalyticsPayload,
    val discoveryAnalyticsPayload: FeedDiscoveryAnalyticsPayload,
    override val impressionPayload: ImpressionPayload
) : UiModel {
    override val stableId = id

    interface Interactor {
        fun onScoresClicked(
            gameId: String,
            leagueId: Long,
            analyticsPayload: FeedScoresAnalyticsPayload
        )
        fun onDiscussClicked(
            gameId: String,
        )
    }
}

data class FeedScoresAnalyticsPayload(
    val moduleIndex: Int,
    val hIndex: Int? = null
) : AnalyticsPayload

data class FeedDiscoveryAnalyticsPayload(
    val moduleIndex: Int,
    val hIndex: Int? = null
) : AnalyticsPayload