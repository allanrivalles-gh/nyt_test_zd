package com.theathletic.feed.compose.ui.analytics

import com.theathletic.analytics.IAnalytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.feed.compose.data.FeedType
import com.theathletic.feed.ui.models.SeeAllAnalyticsPayload
import com.theathletic.impressions.ImpressionEvent

internal class FeedAnalytics @AutoKoin constructor(val analytics: IAnalytics) {

    // TODO: Modify based on FeedType
    val view = "home"

    fun view(event: ImpressionEvent, type: FeedType, id: Long? = null) {
        val (filterType, filterId) = if (type != FeedType.DISCOVER) {
            // Todo: Do we use Ids for the team feed for example? Should we add it to the feed request?
            Pair(type.name.lowercase(), id)
        } else {
            Pair(null, null)
        }

        analytics.track(
            Event.Frontpage.Impression(
                view = view,
                impress_start_time = event.startTime,
                impress_end_time = event.endTime,
                object_type = event.objectType,
                object_id = event.objectId,
                element = event.element,
                container = event.container,
                page_order = event.pageOrder.toLong(),
                h_index = event.hIndex,
                v_index = event.vIndex,
                parent_object_id = event.parentObjectId,
                parent_object_type = event.parentObjectType,
                filter_type = filterType,
                filter_id = filterId
            )
        )
    }

    fun click(clickPayload: ClickPayload, feedType: FeedType, id: String) {
        analytics.track(
            Event.Feed.Click(
                view = view,
                element = clickPayload.container,
                object_type = clickPayload.objectType,
                object_id = id,
                container = clickPayload.container,
                page_order = clickPayload.moduleIndex.toString(),
                v_index = clickPayload.vIndex.toString(),
                h_index = clickPayload.hIndex.toString(),
                parent_object_type = clickPayload.parentType ?: "",
                parent_object_id = clickPayload.parentId ?: "",
                game_id = clickPayload.gameId ?: ""
            )
        )
    }

    fun seeAllClick(seeAllAnalyticsPayload: SeeAllAnalyticsPayload) {
        analytics.track(
            Event.Feed.Click(
                view = view,
                element = "see_all",
                object_type = "",
                object_id = "",
                container = seeAllAnalyticsPayload.container,
                page_order = seeAllAnalyticsPayload.moduleIndex.toString(),
                v_index = "",
                h_index = "",
                parent_object_type = seeAllAnalyticsPayload.parentObjectType,
                parent_object_id = seeAllAnalyticsPayload.parentObjectId
            )
        )
    }

    fun navLinkClick(navLinkAnalyticsPayload: NavLinkAnalyticsPayload, id: String) {
        analytics.track(
            Event.Feed.Click(
                view = view,
                element = navLinkAnalyticsPayload.container,
                object_type = navLinkAnalyticsPayload.objectType,
                object_id = id,
                container = navLinkAnalyticsPayload.container,
                page_order = navLinkAnalyticsPayload.moduleIndex.toString(),
                v_index = navLinkAnalyticsPayload.vIndex.toString(),
                h_index = navLinkAnalyticsPayload.hIndex.toString(),
                parent_object_type = navLinkAnalyticsPayload.parentType ?: "",
                parent_object_id = navLinkAnalyticsPayload.parentId ?: "",
                box_score_tab = navLinkAnalyticsPayload.boxScoreTab
            )
        )
    }
}