package com.theathletic.podcast.data

import com.theathletic.entity.main.PodcastLeagueFeed
import com.theathletic.podcast.data.local.PodcastDao
import com.theathletic.podcast.data.remote.PodcastRestApi
import com.theathletic.repository.resource.NetworkBoundResource
import io.reactivex.Maybe
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class PodcastLeagueFeedData(leagueId: Long) : NetworkBoundResource<PodcastLeagueFeed>(), KoinComponent {
    private val roomDao by inject<PodcastDao>()
    private val podcastRestApi by inject<PodcastRestApi>()

    init {
        callback = object : Callback<PodcastLeagueFeed> {
            override fun saveCallResult(response: PodcastLeagueFeed) {
                response.id = leagueId
                roomDao.insertPodcastLeagueFeed(response)
                Timber.v("[ROOM] Saved Podcast Feed item")
            }

            override fun loadFromDb(): Maybe<PodcastLeagueFeed> {
                return roomDao.getPodcastLeagueFeed(leagueId)
            }

            override fun createNetworkCall(): Maybe<PodcastLeagueFeed> =
                podcastRestApi.getPodcastLeagueFeed(leagueId)

            override fun mapData(data: PodcastLeagueFeed?): PodcastLeagueFeed? = data
        }
    }

    fun reload() {
        fetchNetwork(true)
    }
}