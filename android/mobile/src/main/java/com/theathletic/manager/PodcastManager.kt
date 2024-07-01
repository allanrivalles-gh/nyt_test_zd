package com.theathletic.manager

import android.content.ComponentName
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.os.bundleOf
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableFloat
import androidx.databinding.ObservableInt
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.theathletic.AthleticApplication
import com.theathletic.entity.main.PodcastEpisodeDetailTrackItem
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.entity.main.PodcastTrack
import com.theathletic.entity.main.getBestSource
import com.theathletic.entity.main.getSortableDate
import com.theathletic.extension.applySchedulers
import com.theathletic.extension.extAddOnPropertyChangedCallback
import com.theathletic.extension.extLogError
import com.theathletic.podcast.data.LegacyPodcastRepository
import com.theathletic.podcast.data.PodcastRepository
import com.theathletic.podcast.data.remote.PodcastRestApi
import com.theathletic.podcast.state.PodcastPlayerStateBus
import com.theathletic.rxbus.RxBus
import com.theathletic.service.PodcastService
import com.theathletic.service.PodcastServicePlaybackAction
import com.theathletic.user.UserManager
import com.theathletic.utility.Preferences
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object PodcastManager : IPodcastManager, KoinComponent {
    override var activeTrack: ObservableField<PodcastTrack?> = ObservableField()
    override val playbackState = ObservableInt(PlaybackStateCompat.STATE_NONE)
    override val currentBufferProgress: ObservableInt = ObservableInt(-1)
    override val currentProgress: ObservableInt = ObservableInt(-1)
    override var currentPlayBackSpeed: ObservableFloat = ObservableFloat(Preferences.lastPodcastPlaybackSpeed)
    override var playBackSpeedEnabled: ObservableBoolean = ObservableBoolean(true)
    val shouldBeMiniPlayerVisible = ObservableBoolean(false)
    val trackList: ObservableArrayList<PodcastTrack> = ObservableArrayList()
    val bitmapMap: HashMap<String, Bitmap?> = hashMapOf()
    var maxDuration: ObservableInt = ObservableInt(0)
    private var transportControls: MediaControllerCompat.TransportControls? = null
    private var mediaBrowserCompat: MediaBrowserCompat? = null
    private val podcastRestApi by inject<PodcastRestApi>()
    private val podcastPlayerStateBus by inject<PodcastPlayerStateBus>()
    private val podcastRepository by inject<PodcastRepository>()

    init {
        playbackState.extAddOnPropertyChangedCallback { _, _, _ ->
            shouldBeMiniPlayerVisible.set(playbackState.get() == PlaybackStateCompat.STATE_PLAYING || playbackState.get() == PlaybackStateCompat.STATE_PAUSED)
        }
        activeTrack.extAddOnPropertyChangedCallback { _, _, _ ->
            podcastPlayerStateBus.updateActiveTrack(activeTrack.get())
        }
        playbackState.extAddOnPropertyChangedCallback { _, _, _ ->
            podcastPlayerStateBus.updatePlaybackState(playbackState.get())
        }
        currentProgress.extAddOnPropertyChangedCallback { _, _, _ ->
            podcastPlayerStateBus.updateProgress(currentProgress.get())
        }
    }

    fun skipToNext(): Boolean {
        return if (trackList.indexOf(activeTrack.get()) + 1 < trackList.size) {
            activeTrack.set(trackList[trackList.indexOf(activeTrack.get()) + 1])
            true
        } else false
    }

    fun skipToPrevious(): Boolean {
        return if (trackList.indexOf(activeTrack.get()) - 1 >= 0) {
            activeTrack.set(trackList[trackList.indexOf(activeTrack.get()) - 1])
            true
        } else false
    }

    fun playFromQuery(query: String?): Disposable {
        trackPodcastListenedState()
        return LegacyPodcastRepository.getPodcastIdBySearchQuery(query ?: "")
            .flatMap { id ->
                LegacyPodcastRepository.getPodcastEpisodeList(id).map { episodeList ->
                    if (episodeList.isEmpty()) {
                        podcastRestApi.getPodcastDetail(id).subscribe(
                            {
                                LegacyPodcastRepository.savePodcastDetailData(it)
                                setupTrackList(it.episodes)
                            },
                            {
                                it.extLogError()
                            }
                        )
                    } else {
                        setupTrackList(episodeList)
                    }
                }
            }
            .toSingle()
            .applySchedulers()
            .flatMap { getTransportControlsSingle() }
            .doOnSubscribe { shouldBeMiniPlayerVisible.set(false) }
            .subscribe(
                { transportControls ->
                    val bundle = Bundle()
                    bundle.putInt(PodcastService.EXTRAS_START_PROGRESS_SECONDS, 0)
                    activeTrack.get()?.getBestSource()?.let { sourceUrl ->
                        transportControls.playFromUri(Uri.parse(sourceUrl), bundle)
                        currentProgress.set(0)
                    }
                },
                { it.extLogError() }
            )
    }

    suspend fun playFrom(
        item: PodcastEpisodeItem,
        track: PodcastEpisodeDetailTrackItem? = null,
        downloadedSection: Boolean = false
    ) {
        val podcast = podcastRepository.podcastEpisodeEntityById(item.id.toString()) ?: return
        val startPosition = (track?.startPosition ?: podcast.timeElapsedMs / 1000).toInt()
        return play(item.id, item.podcastId, startPosition, item.duration, downloadedSection)
    }

    suspend fun play(
        episodeId: Long,
        podcastId: Long,
        startPosition: Int = 0,
        duration: Long = 0L,
        downloadedSection: Boolean = false
    ) {
        trackPodcastListenedState()

        val episodes = when {
            downloadedSection -> podcastRepository.downloadedEpisodesImmediate()
            else -> podcastRepository.podcastEpisodesImmediate(podcastId)
        }
        setupTrackList(episodes, episodeId, startPosition)

        shouldBeMiniPlayerVisible.set(false)
        val controls = getTransportControls()

        val seekPosition = if (startPosition + 5 > duration) 0 else startPosition
        val bundle = bundleOf(PodcastService.EXTRAS_START_PROGRESS_SECONDS to seekPosition)
        activeTrack.get()?.getBestSource()?.let { sourceUrl ->
            controls.playFromUri(Uri.parse(sourceUrl), bundle)
            currentProgress.set(seekPosition * 1000)
        }
    }

    fun destroy() {
        mediaBrowserCompat?.disconnect()
        transportControls?.sendCustomAction(PodcastServicePlaybackAction.KILL_PLAYER.value, null)
        transportControls = null
        mediaBrowserCompat = null
    }

    fun getTransportControlsSingle(): Single<MediaControllerCompat.TransportControls> = Single.defer {
        val appContext = AthleticApplication.getContext()
        if (transportControls == null) {
            Single.create { emitter ->
                val callback = object : MediaBrowserCompat.ConnectionCallback() {
                    override fun onConnected() {
                        super.onConnected()
                        val token = if (mediaBrowserCompat?.isConnected == true) {
                            mediaBrowserCompat?.sessionToken
                        } else {
                            null
                        }

                        if (token != null) {
                            transportControls = MediaControllerCompat(appContext, token).transportControls
                            emitter.onSuccess(transportControls!!)
                        } else {
                            emitter.onError(IllegalStateException())
                        }
                    }
                }

                if (mediaBrowserCompat == null) {
                    mediaBrowserCompat = MediaBrowserCompat(
                        appContext,
                        ComponentName(appContext, PodcastService::class.java),
                        callback,
                        Bundle()
                    )
                }

                mediaBrowserCompat?.connect()
            }
        } else {
            Single.just(transportControls)
        }
    }

    suspend fun getTransportControls(): MediaControllerCompat.TransportControls =
        suspendCancellableCoroutine { continuation ->
            // TODO remove this from here
            val appContext = AthleticApplication.getContext()

            val currentControls = transportControls
            if (currentControls != null) {
                continuation.resumeWith(Result.success(currentControls))
                return@suspendCancellableCoroutine
            }

            val callback = object : MediaBrowserCompat.ConnectionCallback() {
                override fun onConnected() {
                    super.onConnected()
                    val token = if (mediaBrowserCompat?.isConnected == true) {
                        mediaBrowserCompat?.sessionToken
                    } else {
                        null
                    }

                    if (token != null) {
                        transportControls = MediaControllerCompat(
                            appContext,
                            token
                        ).transportControls.also {
                            continuation.resumeWith(Result.success(it))
                        }
                    } else {
                        continuation.cancel(
                            IllegalStateException("Unable to connect to transport controls")
                        )
                    }
                }
            }

            if (mediaBrowserCompat == null) {
                mediaBrowserCompat = MediaBrowserCompat(
                    appContext,
                    ComponentName(appContext, PodcastService::class.java),
                    callback,
                    Bundle()
                )
            }

            mediaBrowserCompat?.connect()
        }

    override fun trackPodcastListenedState(onComplete: Boolean) {
        val episodeId = activeTrack.get()?.episodeId ?: return
        val duration = maxDuration.get()
        val progress = currentProgress.get().toLong()
        val finished = if (onComplete) onComplete else progress > duration - PodcastService.TOLERATED_FINISH_DURATION

        trackPodcastListenedState(episodeId, progress, finished)
    }

    override fun trackPodcastListenedState(
        episodeId: Long,
        progress: Long,
        isFinished: Boolean
    ) {
        podcastRepository.updatePodcastEntityListenedState(
            episodeId = episodeId,
            progressMs = progress,
            finished = isFinished,
            ignoreMerger = true
        )

        val position = (progress / 1000f).toInt()
        LegacyPodcastRepository.setPodcastEpisodeProgress(episodeId, position)
        LegacyPodcastRepository.setPodcastEpisodeFinished(episodeId, isFinished)

        RxBus.instance.post(RxBus.PodcastEpisodePlayedStateChangeEvent(episodeId, position, isFinished))
    }

    private fun setupTrackList(
        episodeList: List<PodcastEpisodeItem>,
        selectedEpisodeId: Long? = null,
        selectedEpisodeTimeElapsed: Int = 0
    ) {
        bitmapMap.clear()
        trackList.clear()
        trackList.addAll(
            episodeList
                .sortedByDescending { it.getSortableDate() }
                .filter { UserManager.isUserSubscribed() || it.isTeaser }
                .map {
                    PodcastTrack.fromEpisode(it).also { track ->
                        if (bitmapMap.keys.contains(track.imageUrl))
                            track.bitmapKey.set(track.imageUrl)
                        loadImageAsBitmapForTrack(track.imageUrl)
                    }
                }
        )

        val selectedTrack = trackList.firstOrNull { it.episodeId == selectedEpisodeId }
        activeTrack.set(selectedTrack ?: trackList.firstOrNull())
        currentProgress.set(if (selectedTrack != null) selectedEpisodeTimeElapsed * 1000 else 0)
    }

    private fun loadImageAsBitmapForTrack(imageUrl: String) {
        if (!bitmapMap.keys.contains(imageUrl)) {
            bitmapMap[imageUrl] = null

            Glide.with(AthleticApplication.getContext())
                .asBitmap()
                .load(imageUrl)
                .apply(RequestOptions.overrideOf(800)) // Source images are about 1200 x 1200. This can cause OOM.
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {
                        // We should release and clear all resources used in onResourceReady.
                    }

                    override fun onResourceReady(
                        imageResource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        bitmapMap[imageUrl] = imageResource.copy(imageResource.config, true)
                        trackList.filter { it.imageUrl == imageUrl }.forEach { it.bitmapKey.set(imageUrl) }
                    }
                })
        }
    }
}