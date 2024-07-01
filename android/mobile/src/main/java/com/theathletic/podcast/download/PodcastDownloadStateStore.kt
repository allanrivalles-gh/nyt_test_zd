package com.theathletic.podcast.download

import androidx.collection.LongSparseArray
import com.theathletic.entity.main.PodcastDownloadEntity
import com.theathletic.extension.firstOrNull
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

/**
 * A class which stores the download states of different podcasts. To listen to changes in download
 * state, subscribe to the [downloadStates] subject.
 */
class PodcastDownloadStateStore {

    companion object {
        const val NOT_DOWNLOADED = -1
        const val DOWNLOAD_COMPLETE = 100
    }

    private val downloadStatesArray = LongSparseArray<PodcastDownloadEntity>()

    // Exposed publicly by downloadState so only this class can call onNext() and value
    private val downloadStatesSubject =
        BehaviorSubject.createDefault<LongSparseArray<PodcastDownloadEntity>>(downloadStatesArray)
    val downloadStates: Observable<LongSparseArray<PodcastDownloadEntity>>
        get() = downloadStatesSubject.observeOn(AndroidSchedulers.mainThread())
    val latestState = downloadStatesSubject.value ?: downloadStatesArray

    fun getCurrentDownloadStates() = downloadStatesSubject.value ?: LongSparseArray()

    fun getEntity(podcastEpisodeId: Long): PodcastDownloadEntity {
        return downloadStatesArray.get(podcastEpisodeId)
            ?: PodcastDownloadEntity().apply { this.podcastEpisodeId = podcastEpisodeId }
    }

    fun getEntityByDownloadId(downloadId: Long): PodcastDownloadEntity? {
        return downloadStatesArray.firstOrNull { it.downloadId == downloadId }
    }

    fun updateEntity(item: PodcastDownloadEntity) {
        Timber.v("updatePodcastDownloadEntity(ID: ${item.podcastEpisodeId}, Progress: ${item.progress})")
        downloadStatesSubject.onNext(downloadStatesArray.apply { put(item.podcastEpisodeId, item) })
    }

    fun removeEntity(podcastEpisodeId: Long) {
        Timber.v("removePodcastDownloadEntity($podcastEpisodeId)")
        updateEntity(getEntity(podcastEpisodeId).apply { markAsNotDownloaded() })
    }

    fun hasDownloadsInProgress(): Boolean {
        return downloadStatesArray.firstOrNull { it.isDownloading() } != null
    }
}