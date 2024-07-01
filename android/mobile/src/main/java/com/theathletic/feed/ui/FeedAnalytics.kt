package com.theathletic.feed.ui

import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Exposes
import com.theathletic.feed.FeedType
import com.theathletic.feed.ui.models.FeedAnnouncementAnalyticsPayload
import com.theathletic.feed.ui.models.FeedArticleAnalyticsPayload
import com.theathletic.feed.ui.models.FeedCuratedItemAnalyticsPayload
import com.theathletic.feed.ui.models.FeedDiscoveryAnalyticsPayload
import com.theathletic.feed.ui.models.FeedHeadlineAnalyticsPayload
import com.theathletic.feed.ui.models.FeedInsiderAnalyticsPayload
import com.theathletic.feed.ui.models.FeedPodcastEpisodeAnalyticsPayload
import com.theathletic.feed.ui.models.FeedPodcastShowAnalyticsPayload
import com.theathletic.feed.ui.models.FeedScoresAnalyticsPayload
import com.theathletic.feed.ui.models.LiveBlogAnalyticsPayload
import com.theathletic.feed.ui.models.LiveRoomAnalyticsPayload
import com.theathletic.feed.ui.models.SeeAllAnalyticsPayload
import com.theathletic.frontpage.ui.trendingtopics.TrendingTopicAnalyticsPayload

interface FeedAnalytics {
    var view: String

    fun FeedArticleAnalyticsPayload.click(id: Long)
    fun FeedHeadlineAnalyticsPayload.click(id: String)
    fun FeedCuratedItemAnalyticsPayload.click(id: String)
    fun FeedCuratedItemAnalyticsPayload.play(id: Long)
    fun FeedCuratedItemAnalyticsPayload.pause(id: Long)
    fun TrendingTopicAnalyticsPayload.click(id: Long)
    fun FeedPodcastEpisodeAnalyticsPayload.click(id: Long)
    fun FeedPodcastEpisodeAnalyticsPayload.play(id: Long)
    fun FeedPodcastEpisodeAnalyticsPayload.pause(id: Long)
    fun FeedAnnouncementAnalyticsPayload.click(id: String)
    fun FeedScoresAnalyticsPayload.click(gameId: String, leagueId: Long)
    fun FeedDiscoveryAnalyticsPayload.click(gameId: String, leagueId: Long)
    fun FeedPodcastShowAnalyticsPayload.click(podcastId: Long)
    fun FeedInsiderAnalyticsPayload.click()
    fun SeeAllAnalyticsPayload.click()
    fun LiveBlogAnalyticsPayload.click(id: String)
    fun LiveRoomAnalyticsPayload.click(id: String)

    fun ImpressionPayload.view(
        startTime: Long,
        endTime: Long,
        feedType: FeedType,
        userId: Long
    )

    fun trackAdOnLoad(pageViewId: String, feedType: FeedType)
}

@Exposes(FeedAnalytics::class)
class FeedAnalyticsHandler @AutoKoin constructor(
    val analytics: Analytics
) : FeedAnalytics {

    override var view: String = "home"

    override fun FeedArticleAnalyticsPayload.click(id: Long) {
        analytics.track(
            Event.Feed.Click(
                view = view,
                element = container,
                object_type = "article_id",
                object_id = id.toString(),
                container = container,
                page_order = moduleIndex.toString(),
                v_index = vIndex.toString(),
                h_index = hIndex.toString(),
                parent_object_type = parentType,
                parent_object_id = parentId
            )
        )
    }

    override fun FeedHeadlineAnalyticsPayload.click(id: String) {
        analytics.track(
            Event.Feed.Click(
                view = view,
                element = container,
                object_type = "headline_id",
                object_id = id,
                container = container,
                page_order = moduleIndex.toString(),
                v_index = vIndex.toString(),
                h_index = hIndex.toString(),
                parent_object_type = parentType,
                parent_object_id = parentId
            )
        )
    }

    override fun FeedCuratedItemAnalyticsPayload.click(id: String) {
        analytics.track(
            Event.Feed.Click(
                view = view,
                element = container,
                object_type = objectType,
                object_id = id,
                container = container,
                page_order = moduleIndex.toString(),
                v_index = vIndex.toString(),
                h_index = hIndex.toString(),
                parent_object_type = parentType,
                parent_object_id = parentId
            )
        )
    }

    override fun TrendingTopicAnalyticsPayload.click(id: Long) {
        analytics.track(
            Event.Feed.Click(
                view = view,
                element = container,
                object_type = "topic_id",
                object_id = id.toString(),
                container = container,
                page_order = moduleIndex.toString(),
                v_index = vIndex.toString(),
                h_index = hIndex.toString(),
                parent_object_type = "",
                parent_object_id = ""
            )
        )
    }

    override fun FeedPodcastEpisodeAnalyticsPayload.click(id: Long) {
        analytics.track(
            Event.Feed.Click(
                view = view,
                element = container,
                object_type = "podcast_episode_id",
                object_id = id.toString(),
                container = container,
                page_order = moduleIndex.toString(),
                v_index = vIndex.toString(),
                h_index = hIndex.toString(),
                parent_object_type = "",
                parent_object_id = ""
            )
        )
    }

    override fun FeedAnnouncementAnalyticsPayload.click(id: String) {
        analytics.track(
            Event.Feed.Click(
                view = view,
                element = "announcement",
                object_type = "announcement_id",
                object_id = id,
                container = "announcement",
                page_order = pageOrder.toString(),
                v_index = "",
                h_index = "",
                parent_object_type = "",
                parent_object_id = ""
            )
        )
    }

    override fun LiveBlogAnalyticsPayload.click(id: String) {
        analytics.track(
            Event.Feed.Click(
                view = view,
                element = "live_blogs",
                object_type = "live_blog_id",
                object_id = id,
                container = "live_blogs",
                page_order = pageOrder,
                v_index = verticalIndex,
                h_index = horizontalIndex,
                parent_object_type = "following",
                parent_object_id = ""
            )
        )
    }

    override fun FeedScoresAnalyticsPayload.click(gameId: String, leagueId: Long) {
        analytics.track(
            Event.Feed.Click(
                view = view,
                element = "box_score",
                object_type = "game_id",
                object_id = gameId,
                container = "box_score",
                page_order = moduleIndex.toString(),
                v_index = "",
                h_index = hIndex.toString(),
                parent_object_type = "league_id",
                parent_object_id = leagueId.toString()
            )
        )
    }

    override fun FeedDiscoveryAnalyticsPayload.click(gameId: String, leagueId: Long) {
        analytics.track(
            Event.Feed.Click(
                view = view,
                element = "box_score_discuss",
                object_type = "game_id",
                object_id = gameId,
                container = "box_score",
                page_order = moduleIndex.toString(),
                v_index = "",
                h_index = hIndex.toString(),
                parent_object_type = "league_id",
                parent_object_id = leagueId.toString()
            )
        )
    }

    override fun FeedInsiderAnalyticsPayload.click() {
        analytics.track(
            Event.Feed.Click(
                view = view,
                element = "insider",
                object_type = "author_id",
                object_id = authorId,
                container = "insider",
                page_order = moduleIndex.toString(),
                v_index = "",
                h_index = hIndex.toString(),
                parent_object_id = "",
                parent_object_type = ""
            )
        )
    }

    override fun ImpressionPayload.view(
        startTime: Long,
        endTime: Long,
        feedType: FeedType,
        userId: Long
    ) {
        if (feedType == FeedType.Frontpage) {
            analytics.track(
                Event.Frontpage.Impression(
                    view = view,
                    impress_start_time = startTime,
                    impress_end_time = endTime,
                    object_type = objectType,
                    object_id = objectId,
                    element = element,
                    container = container,
                    page_order = pageOrder.toLong(),
                    h_index = hIndex,
                    v_index = vIndex,
                    parent_object_id = parentObjectId,
                    parent_object_type = parentObjectType
                )
            )
        } else {
            val (filterType, filterId) = feedType.filterTypeAndId(userId)
            analytics.track(
                Event.Feed.Impression(
                    view = view,
                    impress_start_time = startTime,
                    impress_end_time = endTime,
                    object_type = objectType,
                    object_id = objectId,
                    element = element,
                    container = container,
                    page_order = pageOrder.toLong(),
                    h_index = hIndex,
                    v_index = vIndex,
                    parent_object_id = parentObjectId,
                    parent_object_type = parentObjectType,
                    filter_type = filterType,
                    filter_id = filterId
                )
            )
        }
    }

    private fun FeedType.filterTypeAndId(userId: Long) = when (this) {
        is FeedType.User -> Pair("following", userId)
        is FeedType.Team -> Pair("team", this.id)
        is FeedType.League -> Pair("league", this.id)
        is FeedType.Author -> Pair("author", this.id)
        else -> Pair(null, null)
    }

    override fun FeedPodcastShowAnalyticsPayload.click(podcastId: Long) {
        analytics.track(
            Event.Feed.Click(
                view = view,
                element = "recommended_podcast",
                object_type = "podcast_id",
                object_id = podcastId.toString(),
                container = "recommended_podcast",
                page_order = moduleIndex.toString(),
                v_index = vIndex.toString(),
                h_index = hIndex.toString(),
                parent_object_type = "",
                parent_object_id = ""
            )
        )
    }

    override fun FeedCuratedItemAnalyticsPayload.play(id: Long) {
        analytics.track(
            Event.Feed.Play(
                view = view,
                element = container,
                object_id = id.toString(),
                container = container,
                page_order = moduleIndex.toString(),
                v_index = vIndex.toString(),
                h_index = hIndex.toString(),
                parent_object_type = parentType,
                parent_object_id = parentId
            )
        )
    }

    override fun FeedCuratedItemAnalyticsPayload.pause(id: Long) {
        analytics.track(
            Event.Feed.Pause(
                view = view,
                element = container,
                object_id = id.toString(),
                container = container,
                page_order = moduleIndex.toString(),
                v_index = vIndex.toString(),
                h_index = hIndex.toString(),
                parent_object_type = parentType,
                parent_object_id = parentId
            )
        )
    }

    override fun FeedPodcastEpisodeAnalyticsPayload.play(id: Long) {
        analytics.track(
            Event.Feed.Play(
                view = view,
                element = container,
                object_id = id.toString(),
                container = container,
                page_order = moduleIndex.toString(),
                v_index = vIndex.toString(),
                h_index = hIndex.toString(),
                parent_object_type = "",
                parent_object_id = ""
            )
        )
    }

    override fun FeedPodcastEpisodeAnalyticsPayload.pause(id: Long) {
        analytics.track(
            Event.Feed.Pause(
                view = view,
                element = container,
                object_id = id.toString(),
                container = container,
                page_order = moduleIndex.toString(),
                v_index = vIndex.toString(),
                h_index = hIndex.toString(),
                parent_object_type = "",
                parent_object_id = ""
            )
        )
    }

    override fun trackAdOnLoad(
        pageViewId: String,
        feedType: FeedType
    ) {
        val trackedView = when (feedType) {
            is FeedType.League -> "leagues"
            is FeedType.Team -> "teams"
            is FeedType.Author -> "author"
            else -> view
        }
        analytics.track(
            Event.Global.AdOnLoad(
                view = trackedView,
                ad_view_id = pageViewId
            )
        )
    }

    override fun SeeAllAnalyticsPayload.click() {
        analytics.track(
            Event.Feed.Click(
                view = view,
                element = "see_all",
                object_type = "",
                object_id = "",
                container = container,
                page_order = moduleIndex.toString(),
                v_index = "",
                h_index = "",
                parent_object_type = parentObjectType,
                parent_object_id = parentObjectId
            )
        )
    }

    override fun LiveRoomAnalyticsPayload.click(id: String) {
        analytics.track(
            Event.Feed.Click(
                view = view,
                element = "live_room",
                object_type = "room_id",
                object_id = id,
                container = "",
                page_order = moduleIndex.toString(),
                v_index = "",
                h_index = "",
                parent_object_type = "",
                parent_object_id = "",
            )
        )
    }
}