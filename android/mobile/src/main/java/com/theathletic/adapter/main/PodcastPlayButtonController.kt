package com.theathletic.adapter.main

import android.net.Uri
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.os.bundleOf
import com.theathletic.analytics.AnalyticsPayload
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.main.PodcastEpisodeDetailTrackItem
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.entity.main.getBestSource
import com.theathletic.manager.PodcastManager
import com.theathletic.podcast.data.PodcastRepository
import com.theathletic.service.PodcastService
import com.theathletic.utility.NetworkManager
import com.theathletic.utility.PaywallUtility
import com.theathletic.utility.Preferences

class PodcastPlayButtonController @AutoKoin constructor(
    private val podcastRepository: PodcastRepository,
    private val paywallUtility: PaywallUtility
) {
    interface Callback {
        fun showPayWall()
        fun showNetworkOfflineError()

        fun firePlayAnalyticsEvent(podcastEpisodeId: Long, payload: AnalyticsPayload?) {
            // default empty
        }
        fun firePauseAnalyticsEvent(podcastEpisodeId: Long, payload: AnalyticsPayload?) {
            // default empty
        }
    }

    suspend fun onPodcastPlayClick(
        episodeId: Long,
        callback: Callback,
        track: PodcastEpisodeDetailTrackItem? = null,
        analyticsPayload: AnalyticsPayload? = null
    ) {
        val episode = podcastRepository.podcastEpisodeById(episodeId) ?: return
        onPodcastPlayClick(callback, episode, track = track, analyticsPayload = analyticsPayload)
    }

    private suspend fun onPodcastPlayClick(
        callback: Callback,
        item: PodcastEpisodeItem,
        track: PodcastEpisodeDetailTrackItem? = null,
        downloadedSection: Boolean = false,
        analyticsPayload: AnalyticsPayload? = null
    ) {
        val activeTrack = PodcastManager.activeTrack.get()
        val trackIsNotActive = activeTrack == null || activeTrack.id != item.id

        when {
            paywallUtility.shouldUserSeePaywall() && !item.isTeaser -> {
                Preferences.lastGoogleSubArticleId = null
                Preferences.lastGoogleSubPodcastId = item.id
                callback.showPayWall()
            }
            // We want to enable control of active track and enable control of downloaded items
            NetworkManager.getInstance().isOffline() && !item.isDownloaded && trackIsNotActive -> {
                callback.showNetworkOfflineError()
            }
            PodcastManager.activeTrack.get()?.episodeId == item.id -> {
                onPodcastPlayClickActiveTrack(
                    callback,
                    item.id,
                    PodcastManager.playbackState.get(),
                    track?.startPosition?.times(1_000L),
                    analyticsPayload
                )
            }
            PodcastManager.playbackState.get() != PlaybackStateCompat.STATE_CONNECTING -> {
                callback.firePlayAnalyticsEvent(item.id, analyticsPayload)
                PodcastManager.playFrom(item, track, downloadedSection = downloadedSection)
            }
        }
    }

    private suspend fun onPodcastPlayClickActiveTrack(
        callback: Callback,
        episodeId: Long,
        playbackState: Int,
        progressMs: Long? = null,
        analyticsPayload: AnalyticsPayload? = null
    ) {
        val controls = PodcastManager.getTransportControls()

        when (playbackState) {
            PlaybackStateCompat.STATE_PLAYING -> {
                callback.firePauseAnalyticsEvent(episodeId, analyticsPayload)

                if (progressMs == null) {
                    controls.pause()
                } else {
                    controls.seekTo(progressMs)
                }
            }
            PlaybackStateCompat.STATE_PAUSED,
            PlaybackStateCompat.STATE_STOPPED -> {
                callback.firePlayAnalyticsEvent(episodeId, analyticsPayload)

                controls.play()
                if (progressMs != null) {
                    controls.seekTo(progressMs)
                }
            }
            PlaybackStateCompat.STATE_NONE,
            PlaybackStateCompat.STATE_ERROR -> {
                callback.firePlayAnalyticsEvent(episodeId, analyticsPayload)

                controls.playFromUri(
                    Uri.parse(PodcastManager.activeTrack.get()?.getBestSource()),
                    bundleOf(PodcastService.EXTRAS_START_PROGRESS_SECONDS to PodcastManager.currentProgress.get() / 1000)
                )
                if (progressMs != null) {
                    controls.seekTo(progressMs)
                }
            }
            else -> controls.pause()
        }
    }
}