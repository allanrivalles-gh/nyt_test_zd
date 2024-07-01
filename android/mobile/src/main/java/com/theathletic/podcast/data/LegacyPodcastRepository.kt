package com.theathletic.podcast.data

import android.os.Environment
import com.theathletic.AthleticApplication
import com.theathletic.data.PodcastChannelFeedData
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.entity.main.PodcastItem
import com.theathletic.extension.applySchedulers
import com.theathletic.extension.doAsync
import com.theathletic.extension.extLogError
import com.theathletic.podcast.data.local.PodcastDao
import com.theathletic.podcast.download.PodcastDownloadStateStore
import com.theathletic.rxbus.RxBus
import io.reactivex.Single
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.rx2.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Deprecated("Use new PodcastRepository")
object LegacyPodcastRepository : KoinComponent {

    private val podcastDownloadStateStore by inject<PodcastDownloadStateStore>()
    private val podcastDao by inject<PodcastDao>()

    fun getPodcastLeagueFeedData(leagueId: Long) = PodcastLeagueFeedData(leagueId)

    fun getPodcastChannelFeedData(channelId: Long) = PodcastChannelFeedData(channelId)

    fun getPodcastUserFeedData() = PodcastUserFeedData()

    fun getPodcastDetailData(podcastId: Long) = PodcastDetailData(podcastId)

    fun getPodcastEpisodeDetailData(episodeId: Long) = PodcastEpisodeDetailData(episodeId)

    fun getDownloadedPodcastsList() = podcastDao.getPodcastsDownloaded()

    fun getPodcastEpisodeList(podcastId: Long) = podcastDao.getPodcastEpisodes(podcastId)

    fun getPodcastEpisode(episodeId: Long) = podcastDao.getPodcastEpisode(episodeId).map { episodeItem ->
        if (episodeItem.isDownloaded)
            episodeItem.downloadProgress.set(100)
        episodeItem
    }

    fun getPodcastIdBySearchQuery(query: String) = podcastDao.getPodcastIdByTitleSearch(query)

    fun savePodcastDetailData(podcastDetail: PodcastItem) = doAsync {
        podcastDao.insertPodcastDetail(podcastDetail)
    }

    fun setPodcastFollowStatus(podcastId: Long, isFollowing: Boolean) = doAsync {
        podcastDao.setPodcastFollowStatus(podcastId, isFollowing)
        RxBus.instance.post(RxBus.PodcastFollowedStatusChangeEvent(podcastId, isFollowing))
    }

    fun getPodcastFollowStatus(podcastId: Long) = podcastDao.getPodcastFollowStatus(podcastId)

    fun setPodcastEpisodeProgress(episodeId: Long, position: Int) = doAsync {
        podcastDao.setPodcastEpisodeProgress(episodeId, position)
    }

    fun setPodcastEpisodeFinished(episodeId: Long, finished: Boolean) = doAsync {
        podcastDao.setPodcastEpisodeFinished(episodeId, finished)
    }

    /**
     * @return true if files were deleted succesfully, false if an error occured
     */
    suspend fun clearDownloadedPodcasts(): Boolean = withContext(Dispatchers.IO) {
        val downloadedPodcasts = getDownloadedPodcastsList().awaitSingleOrNull()
        var foundError = false

        downloadedPodcasts?.forEach {
            if (!deletePodcastEpisode(it.id).await()) {
                foundError = true
            }
        }
        !foundError
    }

    fun deletePodcastEpisode(podcastEpisodeId: Long): Single<Boolean> = Single
        .fromCallable {
            val file = File(getPodcastLocalFilePath(podcastEpisodeId))

            if (!file.exists()) {
                setPodcastEpisodeDownloaded(podcastEpisodeId, false)
                podcastDownloadStateStore.removeEntity(podcastEpisodeId)
                return@fromCallable true
            }

            if (file.delete()) {
                setPodcastEpisodeDownloaded(podcastEpisodeId, false)
                podcastDownloadStateStore.removeEntity(podcastEpisodeId)
                true
            } else {
                false
            }
        }
        .applySchedulers()
        .doOnError { it.extLogError() }
        .onErrorReturnItem(false)

    fun setPodcastEpisodeDownloaded(podcastEpisodeId: Long, downloaded: Boolean) = doAsync {
        podcastDao.setPodcastEpisodeDownloaded(podcastEpisodeId, downloaded)
    }

    fun clearAllCachedData() = doAsync {
        podcastDao.clear()
    }

    fun getPodcastLocalFilePath(episodeId: Long) = AthleticApplication.getContext()
        .getExternalFilesDir(Environment.DIRECTORY_PODCASTS)?.toString() + getPodcastLocalFileSubPath(episodeId)

    fun getPodcastLocalFileSubPath(episodeId: Long) = File.separator + "AthleticPodcasts" +
        File.separator + episodeId + ".mp3"

    fun insertPodcastEpisodeStandalone(item: PodcastEpisodeItem) {
        podcastDao.insertPodcastEpisodeStandalone(item)
    }

    fun setPodcastCommentsCount(podcastEpisodeId: Long, commentsCount: Int) = doAsync {
        podcastDao.setArticleCommentsCount(podcastEpisodeId, commentsCount)
    }
}