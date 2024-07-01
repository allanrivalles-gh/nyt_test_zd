package com.theathletic.frontpage.ui.trendingtopics

import com.theathletic.analytics.AnalyticsPayload
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.ui.UiModel

data class TrendingTopicAnalyticsPayload(
    val moduleIndex: Int,
    val hIndex: Int = 0,
    val vIndex: Int = 0,
    val container: String
) : AnalyticsPayload

data class TrendingTopicGridItem(
    val id: Long,
    val title: String,
    val storyCount: Int,
    val imageUrl: String,
    val analyticsPayload: TrendingTopicAnalyticsPayload,
    override val impressionPayload: ImpressionPayload
) : UiModel {
    override val stableId = "TrendingTopicGridItem:$id:$title"

    interface Interactor {
        fun onTopicClicked(
            id: Long,
            title: String,
            analyticsPayload: TrendingTopicAnalyticsPayload
        )
    }
}