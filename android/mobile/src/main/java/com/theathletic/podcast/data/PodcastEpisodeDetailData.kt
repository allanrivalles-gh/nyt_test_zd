package com.theathletic.podcast.data

import com.theathletic.entity.local.EntityDataSource
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.extension.applySchedulers
import com.theathletic.podcast.data.local.PodcastDao
import com.theathletic.podcast.data.remote.PodcastRestApi
import com.theathletic.podcast.data.remote.toEntity
import com.theathletic.repository.resource.NetworkBoundResource
import io.reactivex.Maybe
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class PodcastEpisodeDetailData(episodeId: Long) : NetworkBoundResource<PodcastEpisodeItem>(), KoinComponent {
    private val roomDao by inject<PodcastDao>()
    private val podcastRestApi by inject<PodcastRestApi>()
    private val entityDataSource by inject<EntityDataSource>()

    init {
        callback = object : Callback<PodcastEpisodeItem> {
            override fun saveCallResult(response: PodcastEpisodeItem) {
                roomDao.insertOrUpdatePodcastEpisode(response)
                GlobalScope.launch {
                    entityDataSource.insertOrUpdate(response.toEntity())
                }
                Timber.v("[ROOM] Saved Podcast Episode Item with ${response.tracks.size} tracks")
            }

            override fun loadFromDb(): Maybe<PodcastEpisodeItem> = roomDao.getPodcastEpisode(episodeId).applySchedulers()

            override fun createNetworkCall(): Maybe<PodcastEpisodeItem> =
                podcastRestApi.getPodcastEpisodeDetail(episodeId)

            override fun mapData(data: PodcastEpisodeItem?): PodcastEpisodeItem? {
                if (data?.isDownloaded == true)
                    data.downloadProgress.set(100)
                return data
            }
        }
    }

    fun reload() {
        fetchNetwork(true)
    }
}