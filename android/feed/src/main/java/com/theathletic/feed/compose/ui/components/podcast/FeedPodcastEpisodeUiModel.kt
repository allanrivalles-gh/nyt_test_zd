package com.theathletic.feed.compose.ui.components.podcast

import com.theathletic.feed.compose.SOURCE_FEED
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.compose.ui.analytics.AnalyticsData
import com.theathletic.links.deep.Deeplink
import com.theathletic.podcast.ui.DownloadState
import com.theathletic.podcast.ui.PlaybackState
import com.theathletic.podcast.ui.PodcastEpisodeUiModel

data class FeedPodcastEpisodeUiModel(
    override val podcastId: String,
    override val id: String,
    override val permalink: String,
    override val date: String,
    override val title: String,
    override val description: String,
    override val duration: String,
    override val progress: Float,
    override val imageUrl: String,
    override val playbackState: PlaybackState,
    override val downloadState: DownloadState,
    override val analyticsData: AnalyticsData? = null
) : LayoutUiModel.Item, PodcastEpisodeUiModel {
    override fun deepLink(): Deeplink = Deeplink.podcast(podcastId).addSource(SOURCE_FEED) // TODO: Convert to Podcast Episode Deeplink once we support it.
}