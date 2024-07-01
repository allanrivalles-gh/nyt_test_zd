package com.theathletic.adapter.main

import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.podcast.data.LegacyPodcastRepository
import com.theathletic.podcast.download.PodcastDownloadStateStore
import com.theathletic.utility.NetworkManager
import com.theathletic.utility.PaywallUtility
import com.theathletic.utility.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx2.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PodcastDownloadButtonAdapter(private val callback: Callback) : KoinComponent {
    interface Callback {
        fun showPayWall()
        fun showNetworkOfflineError()
        fun showDeleteBottomButtonSheet(item: PodcastEpisodeItem)
        fun downloadPodcastStart(item: PodcastEpisodeItem)
        fun downloadPodcastCancel(item: PodcastEpisodeItem)
    }

    private val podcastDownloadStore by inject<PodcastDownloadStateStore>()
    private val paywallUtility by inject<PaywallUtility>()

    suspend fun onPodcastDownloadClick(episodeId: Long) {
        withContext(Dispatchers.IO) {
            LegacyPodcastRepository.getPodcastEpisode(episodeId).awaitSingleOrNull()
        }?.let {
            onPodcastDownloadClick(it)
        }
    }

    fun onPodcastDownloadClick(item: PodcastEpisodeItem) {
        when {
            item.isDownloaded -> callback.showDeleteBottomButtonSheet(item)
            paywallUtility.shouldUserSeePaywall() && !item.isTeaser -> {
                Preferences.lastGoogleSubArticleId = null
                Preferences.lastGoogleSubPodcastId = item.id
                callback.showPayWall()
            }
            NetworkManager.getInstance().isOffline() -> callback.showNetworkOfflineError()
            podcastDownloadStore.getEntity(item.id).downloadId == -1L -> {
                callback.downloadPodcastStart(item)
            }
            else -> callback.downloadPodcastCancel(item)
        }
    }
}