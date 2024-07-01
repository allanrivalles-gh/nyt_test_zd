package com.theathletic.data

import com.theathletic.entity.main.PodcastItem
import com.theathletic.podcast.data.PodcastGeneralFeedData
import com.theathletic.podcast.data.remote.PodcastRestApi
import io.reactivex.Maybe
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PodcastChannelFeedData(private val channelId: Long) : PodcastGeneralFeedData(), KoinComponent {

    private val podcastRestApi by inject<PodcastRestApi>()

    override fun getTopicId(): String = "channel_$channelId"

    override fun getNetworkCall(): Maybe<List<PodcastItem>> =
        podcastRestApi.getPodcastChannelFeed(channelId)
}