package com.theathletic.audio.ui

import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Exposes
import com.theathletic.feed.ui.modules.audio.LatestPodcastEpisodesModule
import com.theathletic.feed.ui.modules.audio.LiveRoomModule
import com.theathletic.feed.ui.modules.audio.PodcastCarouselModule
import com.theathletic.feed.ui.modules.audio.PodcastCategoriesModule
import timber.log.Timber

interface ListenTabAnalytics {
    fun ImpressionPayload.view(
        tabType: ListenTabContract.TabType,
        startTime: Long,
        endTime: Long,
    )

    fun LiveRoomModule.Payload.click(
        tabType: ListenTabContract.TabType,
        id: String
    )

    fun PodcastCarouselModule.Podcast.Payload.click(
        tabType: ListenTabContract.TabType,
        id: String
    )

    fun LatestPodcastEpisodesModule.Episode.Payload.click(
        tabType: ListenTabContract.TabType,
        id: String
    )

    fun PodcastCategoriesModule.Category.Payload.click(id: String)
}

@Exposes(ListenTabAnalytics::class)
class ListenTabAnalyticsHandler @AutoKoin constructor(
    val analytics: Analytics
) : ListenTabAnalytics {

    override fun ImpressionPayload.view(
        tabType: ListenTabContract.TabType,
        startTime: Long,
        endTime: Long,
    ) {
        Timber.v("Impression for: $objectType:$objectId")
        analytics.track(
            Event.Listen.Impression(
                impress_start_time = startTime,
                impress_end_time = endTime,
                filter_type = tabType.analyticsType,
                object_type = objectType,
                object_id = objectId,
                element = element,
                container = container,
                page_order = pageOrder.toLong(),
                parent_object_id = parentObjectId,
                parent_object_type = parentObjectType
            )
        )
    }

    override fun LiveRoomModule.Payload.click(
        tabType: ListenTabContract.TabType,
        id: String,
    ) {
        analytics.track(
            Event.Listen.Click(
                object_type = "room_id",
                object_id = id,
                element = tabType.analyticsType,
                page_order = moduleIndex.toString(),
            )
        )
    }

    override fun PodcastCarouselModule.Podcast.Payload.click(
        tabType: ListenTabContract.TabType,
        id: String
    ) {
        analytics.track(
            Event.Listen.Click(
                object_type = "podcast_id",
                object_id = id,
                element = tabType.analyticsType,
                page_order = moduleIndex.toString(),
                h_index = hIndex.toString(),
            )
        )
    }

    override fun LatestPodcastEpisodesModule.Episode.Payload.click(
        tabType: ListenTabContract.TabType,
        id: String
    ) {
        analytics.track(
            Event.Listen.Click(
                object_type = "podcast_episode_id",
                object_id = id,
                element = tabType.analyticsType,
                page_order = moduleIndex.toString(),
                v_index = vIndex.toString(),
            )
        )
    }

    override fun PodcastCategoriesModule.Category.Payload.click(id: String) {
        analytics.track(
            Event.Listen.Click(
                object_type = categoryType,
                object_id = id,
                element = "discover",
                page_order = moduleIndex.toString(),
                v_index = vIndex.toString(),
            )
        )
    }

    private val ListenTabContract.TabType.analyticsType get() = when (this) {
        ListenTabContract.TabType.FOLLOWING -> "following"
        ListenTabContract.TabType.DISCOVER -> "discover"
    }
}