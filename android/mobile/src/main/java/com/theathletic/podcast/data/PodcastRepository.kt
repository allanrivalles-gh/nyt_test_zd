package com.theathletic.podcast.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.entity.local.EntityQueries
import com.theathletic.entity.main.PodcastEpisodeDetailTrackItem
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.entity.main.PodcastFeed
import com.theathletic.entity.main.PodcastItem
import com.theathletic.extension.doAsync
import com.theathletic.io.DirectoryProvider
import com.theathletic.podcast.data.local.PodcastDao
import com.theathletic.podcast.data.local.PodcastEpisodeEntity
import com.theathletic.podcast.data.remote.PodcastApi
import com.theathletic.podcast.data.remote.PodcastEpisodeRequest
import com.theathletic.podcast.data.remote.PodcastFeedFetcher
import com.theathletic.podcast.data.remote.UpdatePodcastListenedStateRequest
import com.theathletic.podcast.data.remote.UserPodcastsFetcher
import com.theathletic.podcast.data.remote.toEntity
import com.theathletic.podcast.download.PodcastDownloadStateStore
import com.theathletic.repository.CoroutineRepository
import com.theathletic.user.IUserManager
import com.theathletic.utility.coroutines.DispatcherProvider
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.awaitSingleOrNull

class PodcastRepository @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider,
    private val podcastDao: PodcastDao,
    private val userPodcastsFetcher: UserPodcastsFetcher,
    private val podcastFeedFetcher: PodcastFeedFetcher,
    private val userManager: IUserManager,
    private val entityDataSource: EntityDataSource,
    private val entityQueries: EntityQueries,
    private val updatePodcastListenedStateRequest: UpdatePodcastListenedStateRequest,
    private val directoryProvider: DirectoryProvider,
    private val podcastDownloadStateStore: PodcastDownloadStateStore,
    private val podcastApi: PodcastApi
) : CoroutineRepository {

    override val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    suspend fun podcastEpisodeEntityById(
        episodeId: String
    ) = entityDataSource.get<PodcastEpisodeEntity>(episodeId)

    suspend fun podcastEpisodeEntitiesById(
        episodeIds: List<String>
    ) = entityDataSource.get<PodcastEpisodeEntity>(episodeIds)

    fun updatePodcastEntityListenedState(
        episodeId: Long,
        progressMs: Long,
        finished: Boolean,
        ignoreMerger: Boolean = false
    ) = repositoryScope.launch {
        entityDataSource.update<PodcastEpisodeEntity>(
            id = episodeId.toString(),
            ignoreMerger = ignoreMerger
        ) {
            copy(
                timeElapsedMs = progressMs,
                isFinished = finished
            )
        }

        updatePodcastListenedStateRequest.fetchRemote(
            UpdatePodcastListenedStateRequest.Params(
                podcastEpisodeId = episodeId,
                progressMs = progressMs,
                finished = finished
            )
        )
    }

    suspend fun getFollowedPodcastSeries() = entityQueries.getFollowedFlow(
        AthleticEntity.Type.PODCAST_SERIES
    ).first()

    suspend fun podcastEpisodeById(
        episodeId: Long
    ): PodcastEpisodeItem? = podcastDao.getPodcastEpisodeSuspend(episodeId)

    suspend fun getPodcastEpisodeByNumber(
        podcastId: String,
        episodeNumber: Int
    ) = podcastApi.getPodcastEpisodeByNumber(
        PodcastEpisodeRequest(podcastId, episodeNumber)
    ).fragments.podcastEpisode

    val followedPodcasts: Flow<List<PodcastItem>>
        get() = podcastDao.getPodcastFollowedFlow()

    val userEpisodes: Flow<List<PodcastEpisodeItem>>
        get() = podcastDao.getPodcastUserFeedEpisodesFlow()

    val downloadedEpisodes: Flow<List<PodcastEpisodeItem>>
        get() = podcastDao.getPodcastsDownloadedFlow()

    val podcastFeed: Flow<PodcastFeed?>
        get() = podcastDao.getPodcastFeedFlow()

    /**
     * Use downloadedEpisodes Flow if updates are needed, such as showing a list of downloaded podasts.
     */
    suspend fun downloadedEpisodesImmediate() = podcastDao.getPodcastsDownloadedSuspend()

    suspend fun podcastEpisodesImmediate(
        podcastId: Long
    ) = podcastDao.getPodcastEpisodesSuspend(podcastId)

    suspend fun isPodcastSeriesFollowed(podcastId: Long) =
        repositoryScope.async {
            podcastDao.getPodcastFollowStatus(podcastId).awaitSingleOrNull() ?: false
        }.await()

    fun setPodcastFollowStatus(podcastId: Long, isFollowing: Boolean) =
        repositoryScope.async {
            podcastDao.setPodcastFollowStatus(podcastId, isFollowing)
        }

    fun refreshFollowed() = repositoryScope.launch {
        // Launch updates in parallel
        awaitAll(
            async {
                userPodcastsFetcher.fetchRemote(
                    UserPodcastsFetcher.Params(userManager.getCurrentUserId())
                )
            },
            async {
                podcastFeedFetcher.fetchRemote(
                    PodcastFeedFetcher.Params(userManager.getCurrentUserId())
                )
            }
        )
    }

    fun refreshFeed() = repositoryScope.launch {
        podcastFeedFetcher.fetchRemote(
            PodcastFeedFetcher.Params(userManager.getCurrentUserId())
        )
    }

    fun deleteDownloadedPodcastEpisode(podcastEpisodeId: Long) {
        repositoryScope.launch {
            val file = File(getPodcastLocalFilePath(podcastEpisodeId))

            if (file.exists().not() || file.delete()) {
                setPodcastEpisodeDownloaded(podcastEpisodeId, false)
                podcastDownloadStateStore.removeEntity(podcastEpisodeId)
            }
        }
    }

    private fun setPodcastEpisodeDownloaded(podcastEpisodeId: Long, downloaded: Boolean) = doAsync {
        podcastDao.setPodcastEpisodeDownloaded(podcastEpisodeId, downloaded)
    }

    private fun getPodcastLocalFilePath(episodeId: Long) = directoryProvider.downloadedPodcastDirectory()?.path + File.separator + episodeId + ".mp3"

    suspend fun savePodcast(
        episodeId: String,
        podcastId: String,
        episodeTitle: String,
        description: String,
        duration: Long,
        timeElapsed: Int = 0,
        finished: Boolean = false,
        dateGmt: String,
        mp3Url: String,
        imageUrl: String,
        permalinkUrl: String,
        moreEpisodesCount: Int = 0,
        tracks: List<PodcastEpisodeDetailTrackItem> = arrayListOf(),
        isDownloaded: Boolean = false,
        isUserFeed: Boolean = false
    ) {
        repositoryScope.launch {
            val episode = PodcastEpisodeItem().apply {
                this.id = episodeId.toLong()
                this.podcastId = podcastId.toLong()
                this.title = episodeTitle
                this.description = description
                this.duration = duration
                this.timeElapsed = timeElapsed
                this.finished = finished
                this.dateGmt = dateGmt
                this.mp3Url = mp3Url
                this.imageUrl = imageUrl
                this.permalinkUrl = permalinkUrl
                this.moreEpisodesCount = moreEpisodesCount
                this.tracks = tracks
                this.isDownloaded = isDownloaded
                this.isUserFeed = isUserFeed
            }
            podcastDao.insertOrUpdatePodcastEpisode(episode)
            entityDataSource.insertOrUpdate(episode.toEntity())
        }
    }
}