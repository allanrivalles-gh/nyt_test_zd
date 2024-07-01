package com.theathletic.podcast.data

import com.theathletic.entity.local.EntityDataSource
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.entity.main.PodcastItem
import com.theathletic.extension.applySchedulers
import com.theathletic.podcast.data.local.PodcastDao
import com.theathletic.podcast.data.remote.PodcastRestApi
import com.theathletic.podcast.data.remote.toEntity
import com.theathletic.repository.resource.NetworkBoundResource
import io.reactivex.Maybe
import java.util.Arrays
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class PodcastDetailData(podcastId: Long) : NetworkBoundResource<PodcastItem>(), KoinComponent {
    private val roomDao by inject<PodcastDao>()
    private val podcastRestApi by inject<PodcastRestApi>()
    private val entityDataSource by inject<EntityDataSource>()

    init {
        callback = object : Callback<PodcastItem> {
            override fun saveCallResult(response: PodcastItem) {
                roomDao.insertPodcastDetail(response)
                GlobalScope.launch {
                    entityDataSource.insertOrUpdate(response.episodes.map { it.toEntity() })
                }
                Timber.v("[ROOM] Saved Podcast item with ${response.episodes.size} episodes")
            }

            override fun loadFromDb(): Maybe<PodcastItem> {
                val podcastItemMaybe = roomDao.getPodcast(podcastId).applySchedulers()
                val podcastEpisodesMaybe = roomDao.getPodcastEpisodes(podcastId).applySchedulers()

                return Maybe.zip(Arrays.asList(podcastItemMaybe, podcastEpisodesMaybe)) { result ->
                    val podcastItem = result[0] as PodcastItem
                    val podcastEpisodesList = (result[1] as MutableList<*>).filterIsInstance<PodcastEpisodeItem>()

                    podcastItem.episodes.addAll(podcastEpisodesList)
                    podcastItem
                }
            }

            override fun createNetworkCall(): Maybe<PodcastItem> =
                podcastRestApi.getPodcastDetail(podcastId)

            override fun mapData(data: PodcastItem?): PodcastItem? {
                data?.episodes?.forEach {
                    if (it.isDownloaded)
                        it.downloadProgress.set(100)
                }
                return data
            }
        }
    }

    fun reload() {
        fetchNetwork(true)
    }
}