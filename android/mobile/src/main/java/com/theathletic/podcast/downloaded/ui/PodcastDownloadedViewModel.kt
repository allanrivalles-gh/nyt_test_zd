package com.theathletic.podcast.downloaded.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.theathletic.R
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.entity.main.getSortableDate
import com.theathletic.event.SnackbarEvent
import com.theathletic.extension.extGetString
import com.theathletic.extension.getDirectorySizeMb
import com.theathletic.io.DirectoryProvider
import com.theathletic.podcast.PodcastEpisodeUpdater
import com.theathletic.podcast.data.LegacyPodcastRepository
import com.theathletic.podcast.data.PodcastRepository
import com.theathletic.podcast.state.PodcastPlayerStateBus
import com.theathletic.podcast.ui.PodcastFeedEpisodeItemPresenter
import com.theathletic.ui.LoadingState
import com.theathletic.ui.UiModel
import com.theathletic.ui.list.LegacyAthleticListViewModel
import com.theathletic.ui.list.ListLoadingItem
import com.theathletic.ui.list.list
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await

class PodcastDownloadedViewModel @AutoKoin constructor(
    private val podcastPlayerStateBus: PodcastPlayerStateBus,
    private val directoryProvider: DirectoryProvider,
    private val episodeItemPresenter: PodcastFeedEpisodeItemPresenter,
    private val podcastRepository: PodcastRepository,
    private val analytics: Analytics
) : LegacyAthleticListViewModel(), PodcastEpisodeUpdater {

    private val _uiModels = MutableLiveData<List<UiModel>>()
    override val uiModels: LiveData<List<UiModel>> = _uiModels

    private val podcastEpisodes = mutableMapOf<Long, List<PodcastEpisodeItem>>()

    private val _podcastCount = MutableLiveData(0)
    val podcastCount: LiveData<Int> = _podcastCount

    private var directorySizeMb = 0.0f

    init {
        trackPodcastState()
        loadDownloadedEpisodes()
        rerender()

        analytics.track(Event.Podcast.View(view = "podcast_downloads", element = "downloads"))
    }

    private fun trackPodcastState() {
        podcastPlayerStateBus.stateChangeFlow.collectIn(viewModelScope) { playerState ->
            podcastEpisodes.values.forEach { it.updateState(playerState) }
            rerender()
        }
    }

    private fun loadDownloadedEpisodes() = viewModelScope.launch {
        setLoadingState(LoadingState.INITIAL_LOADING)
        podcastRepository.downloadedEpisodes.collect { episodeList ->
            podcastEpisodes.clear()
            podcastEpisodes.putAll(
                episodeList.let { list ->
                    list.sortedByDescending { it.getSortableDate() }
                        .groupBy { it.podcastId }
                }
            )
            _podcastCount.value = podcastEpisodes.keys.size
            calculateDownloadedSize()

            setLoadingState(LoadingState.FINISHED)
            rerender()
        }
    }

    private fun calculateDownloadedSize() {
        directorySizeMb = directoryProvider.downloadedPodcastDirectory()?.getDirectorySizeMb() ?: 0.0f
    }

    fun clearDownloadedPodcasts() {
        viewModelScope.launch {
            val success = LegacyPodcastRepository.clearDownloadedPodcasts()
            if (!success) {
                sendEvent(SnackbarEvent(R.string.podcast_downloaded_delete_error.extGetString()))
            }
            loadDownloadedEpisodes()
        }
    }

    fun onDeletePodcastClick(item: PodcastEpisodeItem) {
        analytics.track(
            Event.Podcast.Click(
                view = "podcast_downloads",
                element = "downloads",
                object_type = "podcast_episode_id",
                object_id = item.id.toString()
            )
        )
        viewModelScope.launch {
            val success = LegacyPodcastRepository.deletePodcastEpisode(item.id).await()

            if (success) {
                loadDownloadedEpisodes()
            } else {
                sendEvent(SnackbarEvent(R.string.podcast_downloaded_delete_error.extGetString()))
            }
        }
    }

    fun rerender() {
        _uiModels.postValue(generateList())
    }

    private fun generateList() = when {
        loadingState.value == LoadingState.INITIAL_LOADING -> listOf(ListLoadingItem)
        podcastEpisodes.isEmpty() -> listOf(PodcastEmptyDownloadsItem)
        else -> getPopulatedList()
    }

    private fun getPopulatedList() = list {
        if (directorySizeMb > 0f) {
            single { PodcastDownloadedSizeItem(directorySizeMb) }
        }

        podcastEpisodes.keys.forEach { showId ->
            section(title = null) {
                val episodes = podcastEpisodes[showId] ?: emptyList()

                episodes.map { model ->
                    episodeItemPresenter.transform(
                        showId.toString(),
                        model,
                        showDivider = true
                    )
                }
            }
        }
    }
}