package com.theathletic.feed.ui.models

import com.theathletic.analytics.AnalyticsPayload
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.ui.UiModel

data class FeedHeadlineAnalyticsPayload(
    val moduleIndex: Int,
    val container: String,
    val vIndex: Int? = -1,
    val hIndex: Int = -1,
    val parentType: String = "",
    val parentId: String = ""
) : AnalyticsPayload

data class FeedSingleHeadlineItem(
    val id: String,
    val title: String,
    val analyticsPayload: FeedHeadlineAnalyticsPayload,
    override val impressionPayload: ImpressionPayload
) : UiModel {
    override val stableId = "FeedSingleHeadline:$id"

    interface Interactor {
        fun onHeadlineClick(id: String, analyticsPayload: FeedHeadlineAnalyticsPayload)
    }
}

data class FeedHeadlineListItem(
    val id: String,
    val title: String,
    val type: CuratedItemType,
    val analyticsPayload: FeedCuratedItemAnalyticsPayload,
    override val impressionPayload: ImpressionPayload
) : UiModel {
    override val stableId = "FeedHeadlineList:$id"

    interface Interactor : FeedCuratedItemInteractor
}