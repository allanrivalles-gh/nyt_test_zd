package com.theathletic.podcast.data

import com.theathletic.entity.main.PodcastItem
import com.theathletic.podcast.data.remote.PodcastRestApi
import com.theathletic.user.UserManager
import io.reactivex.Maybe
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class PodcastUserFeedData : PodcastGeneralFeedData(), KoinComponent {
    private val podcastRestApi by inject<PodcastRestApi>()

    init {
        callback = object : Callback<List<PodcastItem>> {
            override fun saveCallResult(response: List<PodcastItem>) {
                response.forEach { podcast ->
                    podcast.topicIds.add(getTopicId())
                    roomDao.insertPodcast(podcast)
                }
                Timber.v("[ROOM] Saved Podcast Feed item")
            }

            override fun loadFromDb(): Maybe<List<PodcastItem>> = roomDao.getPodcastFollowedList()

            override fun createNetworkCall(): Maybe<List<PodcastItem>> = getNetworkCall()

            override fun mapData(data: List<PodcastItem>?): List<PodcastItem>? = data
        }
    }

    override fun getTopicId(): String = "channel_${UserManager.getCurrentUserId()}"

    override fun getNetworkCall(): Maybe<List<PodcastItem>> =
        podcastRestApi.getPodcastUserFeed(UserManager.getCurrentUserId())
}