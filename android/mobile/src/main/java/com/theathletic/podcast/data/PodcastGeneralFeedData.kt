package com.theathletic.podcast.data

import com.theathletic.entity.main.PodcastItem
import com.theathletic.podcast.data.local.PodcastDao
import com.theathletic.repository.resource.NetworkBoundResource
import io.reactivex.Maybe
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

abstract class PodcastGeneralFeedData : NetworkBoundResource<List<PodcastItem>>(), KoinComponent {
    val roomDao by inject<PodcastDao>()

    init {
        callback = object : Callback<List<PodcastItem>> {
            override fun saveCallResult(response: List<PodcastItem>) {
                response.forEach { podcast ->
                    podcast.topicIds.add(getTopicId())
                    roomDao.insertPodcast(podcast)
                }
                Timber.v("[ROOM] Saved Podcast Feed item")
            }

            override fun loadFromDb(): Maybe<List<PodcastItem>> = roomDao.getPodcastsByTopicId(getTopicId())

            override fun createNetworkCall(): Maybe<List<PodcastItem>> = getNetworkCall()

            override fun mapData(data: List<PodcastItem>?): List<PodcastItem>? = data
        }
    }

    abstract fun getTopicId(): String

    abstract fun getNetworkCall(): Maybe<List<PodcastItem>>

    fun reload() {
        fetchNetwork(true)
    }
}