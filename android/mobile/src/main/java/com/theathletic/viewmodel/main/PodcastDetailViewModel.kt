package com.theathletic.viewmodel.main

import android.os.Bundle
import androidx.collection.LongSparseArray
import androidx.collection.forEach
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.viewModelScope
import com.theathletic.R
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.audio.data.ListenFeedRepository
import com.theathletic.audio.data.remote.AudioApi
import com.theathletic.entity.main.PodcastDownloadEntity
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.entity.main.PodcastItem
import com.theathletic.entity.main.getSortableDate
import com.theathletic.event.DataChangeEvent
import com.theathletic.event.SnackbarEvent
import com.theathletic.event.ToolbarCollapseEvent
import com.theathletic.extension.applySchedulers
import com.theathletic.extension.extGetString
import com.theathletic.extension.extLogError
import com.theathletic.extension.isNetworkUnavailable
import com.theathletic.fragment.main.PodcastDetailFragment
import com.theathletic.podcast.analytics.PodcastAnalyticsContext
import com.theathletic.podcast.data.LegacyPodcastRepository
import com.theathletic.podcast.data.PodcastDetailData
import com.theathletic.podcast.data.PodcastRepository
import com.theathletic.podcast.download.PodcastDownloadStateStore
import com.theathletic.repository.resource.Resource
import com.theathletic.repository.safeApiRequest
import com.theathletic.rxbus.RxBus
import com.theathletic.utility.NetworkManager
import com.theathletic.viewmodel.BaseViewModel
import com.theathletic.widget.StatefulLayout
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class PodcastDetailViewModel @AutoKoin constructor(
    @Assisted extras: Bundle?,
    podcastAnalyticsContext: PodcastAnalyticsContext,
    private val audioApi: AudioApi,
    private val podcastDownloadStateStore: PodcastDownloadStateStore,
    private val analytics: Analytics,
    private val podcastRepo: PodcastRepository,
    private val listenFeedRepository: ListenFeedRepository,
) : BaseViewModel(), LifecycleObserver {

    val state = ObservableInt(StatefulLayout.PROGRESS)
    val isDataReloading = ObservableBoolean(false)
    val podcastEpisodeList = ObservableArrayList<PodcastEpisodeItem>()
    var podcast: ObservableField<PodcastItem> = ObservableField()
    val isFollowing: ObservableBoolean = ObservableBoolean(false)
    private lateinit var podcastData: PodcastDetailData
    private var podcastId: Long = -1L
    private var podcastDeleteDisposable: Disposable? = null
    private var podcastDataDisposable: Disposable? = null
    private var downloadDisposable: Disposable? = null
    private var podcastFollowedStatusChangeEventDisposable: Disposable? = null
    private var episodeChangeEventDisposable: Disposable? = null
    private var followJob: Job? = null

    init {
        handleExtras(extras)

        podcastData = LegacyPodcastRepository.getPodcastDetailData(podcastId)
        podcastDataDisposable = podcastData.getDataObservable().applySchedulers().subscribe(
            { resource ->
                Timber.i("[PodcastDetailViewModel] Observer status: ${resource?.status}.")
                resource?.data?.let { processData(it) }
                handleDownloadedEpisodesArray(podcastDownloadStateStore.getCurrentDownloadStates())

                val isLoadingFinished = resource?.status != Resource.Status.LOADING
                val isContentEmpty = podcastEpisodeList.isEmpty()

                // Tt In case the loading is finished, we should estimate the new state
                when {
                    isLoadingFinished && isContentEmpty && NetworkManager.getInstance().isOffline() -> {
                        state.set(StatefulLayout.OFFLINE)
                        NetworkManager.getInstance().executeWhenOnline { podcastData.load() }
                    }
                    isLoadingFinished && isContentEmpty -> state.set(StatefulLayout.EMPTY)
                    isLoadingFinished -> state.set(StatefulLayout.CONTENT)
                }

                // Tt Set correct pullToRefresh state
                isDataReloading.set(state.get() != StatefulLayout.PROGRESS && !isLoadingFinished)
            },
            {
                it.extLogError()
                isDataReloading.set(false)
                state.set(StatefulLayout.CONTENT)
                if (it.isNetworkUnavailable()) {
                    state.set(StatefulLayout.OFFLINE)
                } else {
                    state.set(StatefulLayout.EMPTY)
                }
                sendEvent(ToolbarCollapseEvent())
            }
        )

        episodeChangeEventDisposable = RxBus.instance.register(RxBus.PodcastEpisodePlayedStateChangeEvent::class.java).subscribe(
            { event ->
                podcastEpisodeList.filter { it.id == event.episodeId }.forEach { it.timeElapsed = event.progress; it.finished = event.finished }
            },
            Throwable::extLogError
        )

        downloadDisposable = podcastDownloadStateStore.downloadStates
            .subscribe { array -> handleDownloadedEpisodesArray(array) }

        podcastFollowedStatusChangeEventDisposable = RxBus.instance.register(RxBus.PodcastFollowedStatusChangeEvent::class.java).subscribe(
            { event ->
                if (podcast.get()?.id == event.podcastId) {
                    isFollowing.set(event.isFollowed)
                    podcast.get()?.isFollowing = event.isFollowed
                }
            },
            Throwable::extLogError
        )

        analytics.track(
            Event.Podcast.View(
                view = "podcast_page",
                element = podcastAnalyticsContext.source.analyticsElement,
                object_type = "podcast_id",
                object_id = podcastId.toString()
            )
        )

        podcastData.load()
    }

    override fun onCleared() {
        podcastData.dispose()
        podcastDeleteDisposable?.dispose()
        podcastDataDisposable?.dispose()
        downloadDisposable?.dispose()
        podcastFollowedStatusChangeEventDisposable?.dispose()
        episodeChangeEventDisposable?.dispose()
        super.onCleared()
    }

    fun reloadData() {
        if (!isDataReloading.get() && state.get() != StatefulLayout.PROGRESS) {
            isDataReloading.set(true)
            podcastData.reload()
        }
    }

    @Suppress("LongMethod")
    fun switchFollowStatus() {
        if ((followJob?.isActive == false) || isDataReloading.get())
            return

        isDataReloading.set(true)
        if (isFollowing.get()) {
            LegacyPodcastRepository.setPodcastFollowStatus(podcastId, false)
            isFollowing.set(false)

            analytics.track(
                Event.Podcast.Remove(
                    view = "podcast_page",
                    object_id = podcastId.toString()
                )
            )

            followJob = viewModelScope.launch {
                safeApiRequest {
                    audioApi.unfollowPodcast(podcastId.toString())
                }.onSuccess {
                    isDataReloading.set(false)

                    // Temporary until things are moved into new podcast repository
                    podcastRepo.refreshFollowed()
                    listenFeedRepository.fetchListenFeed()
                }.onError {
                    it.extLogError()
                    isDataReloading.set(false)
                    isFollowing.set(true)
                    LegacyPodcastRepository.setPodcastFollowStatus(podcastId, true)
                }
            }
        } else {
            LegacyPodcastRepository.setPodcastFollowStatus(podcastId, true)
            isFollowing.set(true)

            analytics.track(
                Event.Podcast.Add(
                    view = "podcast_page",
                    object_id = podcastId.toString()
                )
            )

            followJob = viewModelScope.launch {
                safeApiRequest {
                    audioApi.followPodcast(podcastId.toString())
                }.onSuccess {
                    analytics.track(
                        Event.Podcast.FollowClick(
                            podcastId.toString(),
                            "podcastScreen"
                        )
                    )
                    isDataReloading.set(false)

                    // Temporary until things are moved into new podcast repository
                    podcastRepo.refreshFollowed()
                    listenFeedRepository.fetchListenFeed()
                }.onError {
                    it.extLogError()
                    isDataReloading.set(false)
                    isFollowing.set(false)
                    LegacyPodcastRepository.setPodcastFollowStatus(podcastId, false)
                }
            }
        }
    }

    fun onDeletePodcastClick(item: PodcastEpisodeItem) {
        podcastDeleteDisposable = LegacyPodcastRepository.deletePodcastEpisode(item.id).subscribe { success ->
            if (success) {
                item.isDownloaded = false
                item.downloadProgress.set(-1)
            } else {
                sendEvent(SnackbarEvent(R.string.podcast_downloaded_delete_error.extGetString()))
            }
        }
    }

    private fun processData(data: PodcastItem) {
        podcast.set(data)

        isFollowing.set(data.isFollowing)

        podcastEpisodeList.clear()
        podcastEpisodeList.addAll(data.episodes.sortedByDescending { it.getSortableDate() })

        sendEvent(DataChangeEvent())
    }

    private fun handleDownloadedEpisodesArray(array: LongSparseArray<PodcastDownloadEntity>) {
        array.forEach { key, downloadItem ->
            val item = podcastEpisodeList.firstOrNull { it.id == key }
            item?.isDownloaded = downloadItem.isDownloaded()
            item?.downloadProgress?.set(downloadItem.progress.toInt())
        }
    }

    private fun handleExtras(extras: Bundle?) {
        podcastId = extras?.getLong(PodcastDetailFragment.EXTRA_PODCAST_ID) ?: -1L
    }
}