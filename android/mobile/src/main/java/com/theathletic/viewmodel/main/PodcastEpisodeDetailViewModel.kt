package com.theathletic.viewmodel.main

import android.os.Bundle
import androidx.collection.LongSparseArray
import androidx.collection.forEach
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.DefaultLifecycleObserver
import com.theathletic.R
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.main.PodcastDownloadEntity
import com.theathletic.entity.main.PodcastEpisodeDetailBaseItem
import com.theathletic.entity.main.PodcastEpisodeDetailHeaderItem
import com.theathletic.entity.main.PodcastEpisodeDetailStoryDividerItem
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.entity.main.getSortableDate
import com.theathletic.event.DataChangeEvent
import com.theathletic.event.SnackbarEvent
import com.theathletic.event.ToolbarCollapseEvent
import com.theathletic.extension.applySchedulers
import com.theathletic.extension.extGetString
import com.theathletic.extension.extLogError
import com.theathletic.extension.isNetworkUnavailable
import com.theathletic.fragment.main.PodcastEpisodeDetailFragment
import com.theathletic.podcast.analytics.PodcastAnalyticsContext
import com.theathletic.podcast.data.LegacyPodcastRepository
import com.theathletic.podcast.data.PodcastEpisodeDetailData
import com.theathletic.podcast.download.PodcastDownloadStateStore
import com.theathletic.repository.resource.Resource
import com.theathletic.utility.NetworkManager
import com.theathletic.viewmodel.BaseViewModel
import com.theathletic.widget.StatefulLayout
import io.reactivex.disposables.Disposable
import timber.log.Timber

class PodcastEpisodeDetailViewModel @AutoKoin constructor(
    @Assisted extras: Bundle?,
    analytics: Analytics,
    podcastAnalyticsContext: PodcastAnalyticsContext,
    private val podcastDownloadStateStore: PodcastDownloadStateStore
) : BaseViewModel(), DefaultLifecycleObserver {

    val state = ObservableInt(StatefulLayout.PROGRESS)
    val recyclerState = ObservableInt(StatefulLayout.PROGRESS)
    val isDataReloading = ObservableBoolean(false)
    val recyclerList = ObservableArrayList<PodcastEpisodeDetailBaseItem>()
    var episode: ObservableField<PodcastEpisodeItem> = ObservableField()
    private lateinit var podcastEpisodeDetailData: PodcastEpisodeDetailData
    private var episodeId: Long = -1L
    private var downloadDisposable: Disposable? = null
    private var podcastDeleteDisposable: Disposable? = null
    private var podcastEpisodeDataDisposable: Disposable? = null

    init {
        handleExtras(extras)

        podcastEpisodeDetailData = LegacyPodcastRepository.getPodcastEpisodeDetailData(episodeId)
        podcastEpisodeDataDisposable = podcastEpisodeDetailData.getDataObservable().applySchedulers().subscribe(
            { resource ->
                Timber.i("[PodcastEpisodeDetailViewModel] Observer status: ${resource?.status}.")
                resource?.data?.let { processData(it) }

                val isLoadingFinished = resource?.status != Resource.Status.LOADING
                val isDataEmpty = episode.get()?.id == null || episode.get()?.title == null
                val isContentEmpty = recyclerList.isEmpty()

                // Tt In case the loading is finished, we should estimate the new state
                when {
                    isLoadingFinished && isContentEmpty && NetworkManager.getInstance().isOffline() -> {
                        state.set(StatefulLayout.OFFLINE)
                        recyclerState.set(StatefulLayout.OFFLINE)
                        NetworkManager.getInstance().executeWhenOnline { podcastEpisodeDetailData.load() }
                    }
                    isLoadingFinished && isDataEmpty -> {
                        state.set(StatefulLayout.EMPTY)
                        recyclerState.set(StatefulLayout.EMPTY)
                    }
                    isLoadingFinished && isContentEmpty -> {
                        state.set(StatefulLayout.CONTENT)
                        recyclerState.set(StatefulLayout.EMPTY)
                    }
                    isLoadingFinished -> {
                        state.set(StatefulLayout.CONTENT)
                        recyclerState.set(StatefulLayout.CONTENT)
                    }
                }

                // Tt Set correct pullToRefresh state
                isDataReloading.set(state.get() != StatefulLayout.PROGRESS && !isLoadingFinished)
            },
            {
                it.extLogError()
                isDataReloading.set(false)
                state.set(if (it.isNetworkUnavailable()) StatefulLayout.OFFLINE else StatefulLayout.EMPTY)
                sendEvent(ToolbarCollapseEvent())
            }
        )

        downloadDisposable = podcastDownloadStateStore.downloadStates
            .subscribe { array -> handleDownloadedEpisodesArray(array) }

        analytics.track(
            Event.Podcast.View(
                view = "podcast_episode",
                element = podcastAnalyticsContext.source.analyticsElement,
                object_type = "podcast_episode_id",
                object_id = episodeId.toString()
            )
        )

        podcastEpisodeDetailData.load()
    }

    override fun onCleared() {
        podcastDeleteDisposable?.dispose()
        podcastEpisodeDetailData.dispose()
        podcastEpisodeDataDisposable?.dispose()
        downloadDisposable?.dispose()
        super.onCleared()
    }

    fun reloadData() {
        if (!isDataReloading.get() && state.get() != StatefulLayout.PROGRESS) {
            isDataReloading.set(true)
            podcastEpisodeDetailData.reload()
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

    private fun processData(episodeItem: PodcastEpisodeItem) {
        episode.set(episodeItem)
        val isItemDescriptionExpanded = recyclerList
            .filterIsInstance<PodcastEpisodeDetailHeaderItem>()
            .firstOrNull()?.showFullDescription ?: false
        recyclerList.clear()
        recyclerList.add(PodcastEpisodeDetailHeaderItem(episodeItem, isItemDescriptionExpanded))
        recyclerList.addAll(episodeItem.tracks.sortedBy { it.trackNumber })

        val sortedStoriesList = episodeItem.stories.sortedBy { it.getSortableDate() }
        sortedStoriesList.forEach {
            recyclerList.add(it)
            if (it != sortedStoriesList.last())
                recyclerList.add(PodcastEpisodeDetailStoryDividerItem())
        }

        // Check download status to immediately update about the download state of the podcast.
        handleDownloadedEpisodesArray(podcastDownloadStateStore.getCurrentDownloadStates())

        sendEvent(DataChangeEvent())
    }

    private fun handleDownloadedEpisodesArray(array: LongSparseArray<PodcastDownloadEntity>) {
        array.forEach { key, downloadItem ->
            if (episode.get()?.id == key) {
                episode.get()?.isDownloaded = downloadItem.isDownloaded()
                episode.get()?.downloadProgress?.set(downloadItem.progress.toInt())
            }
        }
    }

    private fun handleExtras(extras: Bundle?) {
        episodeId = extras?.getLong(PodcastEpisodeDetailFragment.EXTRA_PODCAST_EPISODE_ID) ?: -1L
    }
}