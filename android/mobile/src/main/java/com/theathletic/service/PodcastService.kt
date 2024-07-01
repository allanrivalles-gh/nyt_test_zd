package com.theathletic.service

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PAUSE
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_PAUSE
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SEEK_TO
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_NEXT
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.Observable
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.theathletic.R
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.entity.main.getBestSource
import com.theathletic.extension.extAddOnPropertyChangedCallback
import com.theathletic.extension.extLogError
import com.theathletic.extension.isPauseEnabled
import com.theathletic.extension.isPlayEnabled
import com.theathletic.extension.isSkipToNextEnabled
import com.theathletic.extension.isSkipToPreviousEnabled
import com.theathletic.extension.notificationManager
import com.theathletic.extension.runOnUiThread
import com.theathletic.main.ui.MainActivity
import com.theathletic.manager.PodcastManager
import com.theathletic.media.androidauto.AndroidAuto
import com.theathletic.media.androidauto.AndroidAutoMediaLoader
import com.theathletic.podcast.ExoPlayerPodcastPlayer
import com.theathletic.rxbus.RxBus
import com.theathletic.utility.NetworkManager
import com.theathletic.utility.Preferences
import com.theathletic.utility.logging.ICrashLogHandler
import com.theathletic.viewmodel.main.SLEEP_TIMER_EPISODE_END
import io.reactivex.disposables.CompositeDisposable
import java.io.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber

enum class PodcastServicePlaybackAction(val value: String) {
    FORWARD_10_SEC("forward_10_sec"),
    BACKWARD_10_SEC("backward_10_sec"),
    KILL_PLAYER("kill_player")
}

interface PodcastServiceCallback {
    fun startPlayingActiveTrack(forceNetwork: Boolean = false)
    fun switchToState(state: Int, errorMessage: String? = null)
}

class PodcastService : MediaBrowserServiceCompat(), PodcastServiceCallback {
    companion object {
        const val PROGRESS_UPDATE_INTERVAL: Long = 200L
        const val TOLERATED_FINISH_DURATION: Long = 5_000L
        const val ANR_WATCHDOG_DELAY: Long = 4_000L // Tt this is just below 5s limit, after which app reports ANR, if startForeground was not called.
        const val PODCAST_CHANNEL: String = "com.theathletic.android.PODCAST"
        const val PODCAST_NOTIFICATION_ID: Int = 19
        const val EXTRAS_START_PROGRESS_SECONDS = "extras_start_progress_seconds"
        private const val ACTION_NOTIFICATION_DISMISSED: String = "com.theathletic.NOTIFICATION_DISMISSED"
    }

    val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private lateinit var mediaSessionCompat: MediaSessionCompat
    private lateinit var mediaController: MediaControllerCompat
    private lateinit var mediaControllerCallback: MediaControllerCallback
    private lateinit var noisyReceiver: NoisyReceiver
    private lateinit var notificationBuilder: NotificationBuilder
    private val notificationDismissedReceiver = NotificationDismissReceiver()

    private lateinit var podcastPlayer: ExoPlayerPodcastPlayer
    private val compositeDisposable = CompositeDisposable()
    private var mustCallStartForeground = false
    private val progressHandler: Handler = Handler(Looper.getMainLooper())
    private val crashLogHandler by inject<ICrashLogHandler>()
    private val analytics by inject<Analytics>()

    private val androidAutoMediaLoader by inject<AndroidAutoMediaLoader>()
    private val athleticApplication by inject<Application>()

    private val progressRunnable = object : Runnable {
        override fun run() {
            progressHandler.postDelayed(this, PROGRESS_UPDATE_INTERVAL)

            if (!podcastPlayer.isPlaying)
                return

            PodcastManager.currentProgress.set(podcastPlayer.currentPositionMs)
            PodcastManager.currentBufferProgress.set(podcastPlayer.bufferProgressPct)
        }
    }

    private val networkChangeCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            runOnUiThread {
                startPlayingActiveTrack()
            }
            NetworkManager.connected.removeOnPropertyChangedCallback(this)
        }
    }

    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        var onPrepareSeek = 0

        override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
            super.onPlayFromUri(uri, extras)
            Timber.v("mediaSessionCallback - onPlayFromUri($uri, $extras)")

            switchToState(PlaybackStateCompat.STATE_CONNECTING)

            onPrepareSeek = (extras?.getInt(EXTRAS_START_PROGRESS_SECONDS) ?: 0) * 1000
            PodcastManager.currentProgress.set(onPrepareSeek)
            try {
                try {
                    podcastPlayer.prepare(applicationContext, uri.toString())
                } catch (e: IllegalStateException) {
                    e.extLogError()
                    podcastPlayer.release()
                    initMediaPlayer()
                    podcastPlayer.prepare(applicationContext, uri.toString())
                }

                initMediaSessionMetadata()
            } catch (e: IOException) {
                e.extLogError()
                return
            }

            try {
                startForegroundService()
            } catch (e: IOException) {
                e.extLogError()
            }
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            super.onPlayFromMediaId(mediaId, extras)
            val episodeId = extras?.getLong(AndroidAuto.Extras.ID)
            val podcastId = extras?.getLong(AndroidAuto.Extras.PODCAST_ID)
            val downloadedSection = extras?.getBoolean(AndroidAuto.Extras.DOWNLOADED_SECTION) ?: false

            if (episodeId != null && podcastId != null) {
                serviceScope.launch {
                    PodcastManager.play(episodeId, podcastId, downloadedSection = downloadedSection)
                }
            }
        }

        override fun onPlayFromSearch(query: String?, extras: Bundle?) {
            super.onPlayFromSearch(query, extras)
            PodcastManager.playFromQuery(query)
        }

        override fun onPrepare() {
            Timber.v("mediaSessionCallback - onPrepare()")

            PodcastManager.maxDuration.set(podcastPlayer.durationMs)

            podcastPlayer.seekTo(onPrepareSeek)
            if (onPrepareSeek != 0) {
                onPrepareSeek = 0
            }

            play()

            super.onPrepare()
        }

        override fun onPlay() {
            Timber.v("mediaSessionCallback - onPlay())")

            // We have to check this here because play can be hit from notification.
            // We need to check also for stopped state in case we played to the end of last track,
            // to start playing it from the beginning.
            if (PodcastManager.playbackState.get() == PlaybackStateCompat.STATE_NONE ||
                PodcastManager.playbackState.get() == PlaybackStateCompat.STATE_STOPPED ||
                PodcastManager.playbackState.get() == PlaybackStateCompat.STATE_ERROR
            ) {
                startPlayingActiveTrack()
                return
            }

            if (!isInPlaybackState()) {
                return
            }

            play()

            startForegroundService()
        }

        override fun onPause() {
            Timber.v("mediaSessionCallback - onPause()")

            if (!isInPlaybackState())
                return

            PodcastManager.trackPodcastListenedState()

            if (podcastPlayer.isPlaying) {
                podcastPlayer.pause()
            }

            progressHandler.removeCallbacks(progressRunnable)
        }

        override fun onStop() {
            Timber.v("mediaSessionCallback - onStop()")

            if (!isInPlaybackState())
                return

            PodcastManager.trackPodcastListenedState()

            if (podcastPlayer.isPlaying) {
                podcastPlayer.stop()
            }

            progressHandler.removeCallbacks(progressRunnable)

            switchToState(PlaybackStateCompat.STATE_STOPPED)
        }

        override fun onSkipToNext() {
            onSkipToNext(false)
        }

        fun onSkipToNext(onComplete: Boolean) {
            Timber.v("mediaSessionCallback - onSkipToNext($onComplete)")

            if (onComplete && Preferences.podcastSleepTimestampMillis == SLEEP_TIMER_EPISODE_END) {
                onStop()
                Preferences.clearPodcastSleepTimestamp()
                RxBus.instance.post(RxBus.SleepTimerPauseEvent())
                return
            }

            if (!isInPlaybackState())
                return

            PodcastManager.trackPodcastListenedState(onComplete)

            if (PodcastManager.skipToNext()) {
                onPlayFromUri(
                    Uri.parse(PodcastManager.activeTrack.get()?.getBestSource()),
                    null
                )
            } else {
                onStop()
            }
        }

        override fun onSkipToPrevious() {
            Timber.v("mediaSessionCallback - onSkipToPrevious()")

            if (!isInPlaybackState())
                return

            PodcastManager.trackPodcastListenedState()

            if (PodcastManager.skipToPrevious()) {
                onPlayFromUri(
                    Uri.parse(PodcastManager.activeTrack.get()?.getBestSource()),
                    null
                )
            } else {
                onStop()
            }
        }

        /**
         * First, we need to check if the player was playing before the seeking and store the value.
         * Next, we should pause the playback before seeking, and update the currentProgress with a new value.
         * We also need to immediately call [setMediaPlaybackState] so the Progress inside notification is updated correctly.
         * Then we can safely seekTo new position and set up the [OnSeekCompleteListener]. In there we resume playback
         * in case it was playing before.
         */
        override fun onSeekTo(pos: Long) {
            Timber.v("mediaSessionCallback - onSeekTo($pos)")

            if (!isInPlaybackState())
                return

            PodcastManager.currentProgress.set(pos.toInt())
            setMediaPlaybackState(PodcastManager.playbackState.get())

            podcastPlayer.seekTo(pos.toInt())

            analytics.track(
                Event.Podcast.Seek(
                    object_id = PodcastManager.activeTrack.get()?.id?.toString() ?: ""
                )
            )

            super.onSeekTo(pos)
        }

        override fun onFastForward() {
            Timber.v("mediaSessionCallback - onFastForward()")

            val newSpeed = when (PodcastManager.currentPlayBackSpeed.get()) {
                1f -> 1.2f
                1.2f -> 1.5f
                1.5f -> 2f
                2f -> 0.8f
                else -> 1f
            }

            PodcastManager.currentPlayBackSpeed.set(newSpeed)
            Preferences.lastPodcastPlaybackSpeed = newSpeed

            try {
                podcastPlayer.setPlaybackSpeed(newSpeed)
                if (PodcastManager.playbackState.get() != PlaybackStateCompat.STATE_PLAYING) {
                    podcastPlayer.pause()
                }
            } catch (e: Exception) {
                e.extLogError()
            }
            analytics.track(
                Event.Podcast.Click(
                    view = "podcast_player",
                    element = "play_speed",
                    object_type = "play_speed",
                    object_id = "%.1f".format(newSpeed)
                )
            )

            setMediaPlaybackState(PodcastManager.playbackState.get())
            super.onFastForward()
        }

        override fun onCustomAction(action: String?, extras: Bundle?) {
            super.onCustomAction(action, extras)
            Timber.v("mediaSessionCallback - onCustomAction($action, $extras)")
            when (action) {
                PodcastServicePlaybackAction.FORWARD_10_SEC.value -> {
                    podcastPlayer.currentPositionMs.plus(10000).let {
                        val seekTo = if (it > podcastPlayer.durationMs) podcastPlayer.durationMs else it
                        mediaController.transportControls.seekTo(seekTo.toLong())
                    }

                    val episodeId = PodcastManager.activeTrack.get()?.episodeId?.toString() ?: ""
                    analytics.track(
                        Event.Podcast.Click(
                            view = "podcast_player",
                            element = "fast_forward",
                            object_type = "podcast_episode_id",
                            object_id = episodeId
                        )
                    )
                }
                PodcastServicePlaybackAction.BACKWARD_10_SEC.value -> {
                    podcastPlayer.currentPositionMs.minus(10000).let {
                        val seekTo = if (it < 0) 0 else it
                        mediaController.transportControls.seekTo(seekTo.toLong())
                    }
                    val episodeId = PodcastManager.activeTrack.get()?.episodeId?.toString() ?: ""
                    analytics.track(
                        Event.Podcast.Click(
                            view = "podcast_player",
                            element = "rewind",
                            object_type = "podcast_episode_id",
                            object_id = episodeId
                        )
                    )
                }
                PodcastServicePlaybackAction.KILL_PLAYER.value -> stopService()
            }
        }

        private fun play() {
            podcastPlayer.play()

            switchToState(PlaybackStateCompat.STATE_PLAYING)

            progressHandler.removeCallbacks(progressRunnable)
            progressHandler.post(progressRunnable)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.v("onStartCommand($intent, $flags, $startId)")
        Timber.v("PodcastManager.state: ${getStateLogString(PodcastManager.playbackState.get())}")

        if (intent != null && MediaButtonReceiver.handleIntent(mediaSessionCompat, intent) == null) {
            Timber.v("onStartCommand invoked without mediaSession callback")
            updateNotification(PlaybackStateCompat.STATE_CONNECTING)
        }

        return Service.START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Timber.v("onCreate()")

        initMediaPlayer()
        initMediaSession()
        initMediaSessionMetadata()

        // Tt Init noisy receiver
        mediaSessionCompat.sessionToken?.let { noisyReceiver = NoisyReceiver(sessionToken = it) }

        // Tt Prepare notification builder
        notificationBuilder = NotificationBuilder()

        // Tt Init media controller
        mediaControllerCallback = MediaControllerCallback()
        mediaController = MediaControllerCompat(this, mediaSessionCompat).also {
            it.registerCallback(mediaControllerCallback)
        }
    }

    override fun onDestroy() {
        Timber.v("onDestroy()")

        // Tt ensure we cancel all notifications
        notificationManager.cancel(PODCAST_NOTIFICATION_ID)

        // Tt Release NoisyReceiver
        noisyReceiver.unregister()
        notificationDismissedReceiver.unregister()

        // Tt Release ProgressHandler
        progressHandler.removeCallbacks(progressRunnable)

        // Tt Release mediaSessionCompat
        mediaSessionCompat.run { isActive = false; release() }

        // Tt Unregister mediaController callback
        mediaController.unregisterCallback(mediaControllerCallback)

        // Tt Release MediaPlayer
        podcastPlayer.release()

        // Tt Clear PodcastManagerData
        PodcastManager.activeTrack.set(null)
        PodcastManager.trackList.clear()
        PodcastManager.currentBufferProgress.set(-1)
        PodcastManager.currentProgress.set(-1)

        switchToState(PlaybackStateCompat.STATE_NONE)
        compositeDisposable.dispose()

        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot {
        return when (clientPackageName) {
            packageName -> BrowserRoot(AndroidAuto.Section.EMPTY_ROOT, null)
            AndroidAuto.PACKAGE_NAME,
            AndroidAuto.EMULATOR_PACKAGE_NAME -> BrowserRoot(AndroidAuto.Section.ROOT, null)
            else -> BrowserRoot(AndroidAuto.Section.EMPTY_ROOT, null)
        }
    }

    override fun onLoadChildren(
        parentMediaId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>
    ) {
        androidAutoMediaLoader.loadSection(parentMediaId, result, compositeDisposable)
    }

    private fun initMediaPlayer() {
        podcastPlayer = ExoPlayerPodcastPlayer().apply {
            init(applicationContext)
            onPreparedListener = { mediaSessionCompat.controller?.transportControls?.prepare() }
            onPausedListener = { switchToState(PlaybackStateCompat.STATE_PAUSED) }
            onCompletionListener = { mediaSessionCallback.onSkipToNext(true) }
            onErrorListener = {
                switchToState(
                    PlaybackStateCompat.STATE_ERROR,
                    errorMessage = getString(R.string.global_network_offline)
                )

                if (NetworkManager.getInstance().isOffline()) {
                    NetworkManager.connected.addOnPropertyChangedCallback(networkChangeCallback)
                }
            }
        }
    }

    private fun initMediaSessionMetadata() {
        /**
         * Little bit hacky way on how we obtain the bitmap. They are preloaded once the track list is created.
         * These bitmaps are stored in bitmapMap in the PodcastManager. The key is the image URL, and the value is
         * the bitmap. If we do not have the bitmap yet, we will use default athletic logo instead, and replace it
         * once the bitmap is ready.
         */
        fun getBestPossibleBitmapForTrack(): Bitmap {
            val bitmapKey = PodcastManager.trackList.firstOrNull { it.bitmapKey.get() != null }?.bitmapKey?.get()
            return PodcastManager.bitmapMap[bitmapKey]
                ?: BitmapFactory.decodeResource(applicationContext.resources, R.drawable.ic_athletic_logo)
        }

        val track = PodcastManager.activeTrack.get() ?: return
        var metadataBuilder: MediaMetadataCompat.Builder? = null

        metadataBuilder = MediaMetadataCompat.Builder().apply {
            fun setBitmap(bitmap: Bitmap?) {
                putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, bitmap)
                putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
                putBitmap(MediaMetadataCompat.METADATA_KEY_ART, bitmap)
            }

            putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, track.imageUrl)
            putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, track.imageUrl)
            putString(MediaMetadataCompat.METADATA_KEY_ART_URI, track.imageUrl)
            putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, track.title)
            putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, track.description)
            putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
            putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.description) // Because of Samsung S10
            putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, PodcastManager.trackList.indexOf(track).toLong())
            putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, PodcastManager.trackList.size.toLong())
            putLong(MediaMetadataCompat.METADATA_KEY_DURATION, track.duration * 1000)

            setBitmap(getBestPossibleBitmapForTrack())

            // Let's try to get the best possible bitmapKey
            if (track.bitmapKey.get() == null) {
                // In case of we still do not have bitmapKey for the current track, let's wait until it is available
                // and set a temporary one in the meantime.
                track.bitmapKey.extAddOnPropertyChangedCallback { _, _, onPropertyChangedCallback ->
                    // Update the bitmap only in case of currently playing track is still the same as in the callback!
                    if (track == PodcastManager.activeTrack.get()) {
                        setBitmap(getBestPossibleBitmapForTrack())
                        mediaSessionCompat.setMetadata(metadataBuilder?.build())
                    }
                    // Remove callback. We won't need it anymore
                    track.bitmapKey.removeOnPropertyChangedCallback(onPropertyChangedCallback)
                }
            }
        }

        mediaSessionCompat.setMetadata(metadataBuilder.build())
    }

    private fun initMediaSession() {
        val mediaButtonReceiver = ComponentName(applicationContext, MediaButtonReceiver::class.java)

        mediaSessionCompat = MediaSessionCompat(applicationContext, "Tag", mediaButtonReceiver, null).apply {
            setCallback(mediaSessionCallback)
            /*	If Android can identify the last active media session, it tries to restart the session by sending an
                ACTION_MEDIA_BUTTON Intent to a manifest-registered component (such as a service or BroadcastReceiver).
                You can disable this behavior in API level 21 and higher by setting a null media button receiver. */
            setMediaButtonReceiver(null) // Set null instead of passing pendingIntent!
        }

        sessionToken = mediaSessionCompat.sessionToken
    }

    private fun setMediaPlaybackState(state: Int, errorMessage: String? = null) {
        var action = ACTION_PLAY_PAUSE or ACTION_SEEK_TO

        if (PodcastManager.trackList.firstOrNull() != PodcastManager.activeTrack.get())
            action = action or ACTION_SKIP_TO_PREVIOUS

        if (PodcastManager.trackList.lastOrNull() != PodcastManager.activeTrack.get())
            action = action or ACTION_SKIP_TO_NEXT

        val playbackStateBuilder = PlaybackStateCompat.Builder().apply {
            action = if (state == PlaybackStateCompat.STATE_PLAYING)
                action or ACTION_PAUSE
            else
                action or ACTION_PLAY

            setActions(action)

            errorMessage?.let { setErrorMessage(PlaybackStateCompat.ERROR_CODE_UNKNOWN_ERROR, it) }
            setState(state, PodcastManager.currentProgress.get().toLong(), PodcastManager.currentPlayBackSpeed.get())
        }

        mediaSessionCompat.setPlaybackState(playbackStateBuilder.build())
    }

    override fun startPlayingActiveTrack(forceNetwork: Boolean) {
        Timber.v("[PodcastService] startPlayingActiveTrack())")
        PodcastManager.activeTrack.get()?.let {
            val bundle = Bundle()
            val currentProgress = PodcastManager.currentProgress.get()
            val duration = PodcastManager.maxDuration.get()
            val startPosition = if (currentProgress + TOLERATED_FINISH_DURATION > duration) 0 else currentProgress
            val source = if (forceNetwork) it.url else it.getBestSource()

            bundle.putInt(EXTRAS_START_PROGRESS_SECONDS, startPosition / 1_000)
            mediaSessionCallback.onPlayFromUri(Uri.parse(source), bundle)
        }
    }

    private fun startForegroundService() {
        Timber.v("startForegroundService")
        val intent = Intent(applicationContext, this.javaClass)

        mustCallStartForeground = true
        ContextCompat.startForegroundService(applicationContext, intent)
        Handler(Looper.getMainLooper()).postDelayed(
            {
                if (mustCallStartForeground) {
                    Timber.w("PodcastService watchdog prevented ANR!")
                    crashLogHandler.trackException(
                        ICrashLogHandler.OtherException(
                            "Warning: PodcastService watchdog prevented ANR. " +
                                "startForeground() was not called before in time before threshold."
                        )
                    )
                    updateNotification(PlaybackStateCompat.STATE_CONNECTING)
                }
            },
            ANR_WATCHDOG_DELAY
        )
    }

    private fun isInPlaybackState(state: Int = PodcastManager.playbackState.get()): Boolean {
        return state == PlaybackStateCompat.STATE_PLAYING ||
            state == PlaybackStateCompat.STATE_PAUSED ||
            state == PlaybackStateCompat.STATE_BUFFERING ||
            state == PlaybackStateCompat.STATE_STOPPED
    }

    fun isInForegroundServiceState(state: Int = PodcastManager.playbackState.get()): Boolean {
        return state == PlaybackStateCompat.STATE_PLAYING ||
            state == PlaybackStateCompat.STATE_BUFFERING ||
            state == PlaybackStateCompat.STATE_CONNECTING
    }

    private fun isInErrorState(state: Int) = state == PlaybackStateCompat.STATE_ERROR

    private fun stopService() {
        Timber.v("stopService")
        mediaSessionCallback.onStop()
        Timber.v("Calling stopForeground")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
        stopSelf()
    }

    override fun switchToState(state: Int, errorMessage: String?) {
        Timber.v("Switching to state: ${getStateLogString(state)}")
        setMediaPlaybackState(state, errorMessage)
        PodcastManager.playbackState.set(state)

        mediaSessionCompat.isActive = when (state) {
            PlaybackStateCompat.STATE_NONE -> false
            PlaybackStateCompat.STATE_ERROR -> false
            PlaybackStateCompat.STATE_CONNECTING -> false
            PlaybackStateCompat.STATE_PLAYING -> true
            PlaybackStateCompat.STATE_PAUSED -> true
            PlaybackStateCompat.STATE_STOPPED -> true
            else -> mediaSessionCompat.isActive
        }
    }

    private fun getStateLogString(state: Int) = when (state) {
        PlaybackStateCompat.STATE_NONE -> "NONE"
        PlaybackStateCompat.STATE_CONNECTING -> "CONNECTING"
        PlaybackStateCompat.STATE_PLAYING -> "PLAYING"
        PlaybackStateCompat.STATE_PAUSED -> "PAUSED"
        PlaybackStateCompat.STATE_STOPPED -> "STOPPED"
        PlaybackStateCompat.STATE_ERROR -> "ERROR"
        else -> "UNKNOWN"
    }

    private fun updateNotification(state: Int) {
        val notification = buildNotificationIfPossible(state)

        if (notification != null) {
            Timber.v("Calling startForeground")
            try {
                startForeground(PODCAST_NOTIFICATION_ID, notification)
                mustCallStartForeground = false
            } catch (e: Exception) {
                crashLogHandler.trackException(e, "Failed to startForeground, player state is $state")
            }
        } else {
            crashLogHandler.trackException(ICrashLogHandler.OtherException("Warning: PodcastService was unable to create a notification, player state is $state"))
        }

        // Tt In case of buffering or playing, lets show foreground notification with pause button
        // Tt Otherwise stop the foregroundService and remove notification if necessary
        if (notification != null && isInForegroundServiceState(state)) {
            noisyReceiver.register()
            notificationDismissedReceiver.register()
        } else {
            noisyReceiver.unregister()
            Timber.v("Calling stopForeground with state: ${getStateLogString(state)}")
            val removeNotification = notification == null || (!isInPlaybackState(state) && !isInErrorState(state))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val notificationBehavior = if (removeNotification) STOP_FOREGROUND_REMOVE else STOP_FOREGROUND_DETACH
                stopForeground(notificationBehavior)
            } else {
                stopForeground(removeNotification)
            }
        }
    }

    private fun buildNotificationIfPossible(state: Int): Notification? {
        // Tt Skip building a notification when state is "none"
        val sessionToken = mediaSessionCompat.sessionToken
        return when {
            state == PlaybackStateCompat.STATE_NONE -> null
            sessionToken != null -> notificationBuilder.buildNotification(sessionToken)
            else -> notificationBuilder.buildNotificationWithoutMediaSession()
        }
    }

    // TT Media Controller Callback
    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            Timber.v("onMetadataChanged")
            mediaController.playbackState?.let { updateNotification(it.state) }
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            state?.let {
                Timber.v("onPlaybackStateChanged with state: ${getStateLogString(it.state)}")
                updateNotification(it.state)
            }
        }
    }

    // TT Notifications
    private inner class NotificationBuilder {
        private val skipToPreviousAction = NotificationCompat.Action(
            R.drawable.ic_podcast_notif_previous,
            getString(R.string.podcast_notification_previous),
            MediaButtonReceiver.buildMediaButtonPendingIntent(this@PodcastService, ACTION_SKIP_TO_PREVIOUS)
        )
        private val skipToPreviousActionDisabled = NotificationCompat.Action(
            R.drawable.ic_podcast_notif_previous_disabled,
            getString(R.string.podcast_notification_previous), null
        )
        private val playAction = NotificationCompat.Action(
            R.drawable.ic_podcast_notif_play,
            getString(R.string.podcast_notification_play),
            MediaButtonReceiver.buildMediaButtonPendingIntent(this@PodcastService, ACTION_PLAY)
        )
        private val pauseAction = NotificationCompat.Action(
            R.drawable.ic_podcast_notif_pause,
            getString(R.string.podcast_notification_pause),
            MediaButtonReceiver.buildMediaButtonPendingIntent(this@PodcastService, ACTION_PAUSE)
        )
        private val skipToNextAction = NotificationCompat.Action(
            R.drawable.ic_podcast_notif_next,
            getString(R.string.podcast_notification_next),
            MediaButtonReceiver.buildMediaButtonPendingIntent(this@PodcastService, ACTION_SKIP_TO_NEXT)
        )
        private val skipToNextActionDisabled = NotificationCompat.Action(
            R.drawable.ic_podcast_notif_next_disabled,
            getString(R.string.podcast_notification_next), null
        )

        fun buildNotificationWithoutMediaSession(): Notification {
            if (shouldCreateNowPlayingChannel()) {
                createNowPlayingChannel()
            }

            val builder = NotificationCompat.Builder(this@PodcastService, PODCAST_CHANNEL)

            val contentIntent = PendingIntent.getActivity(
                athleticApplication, 0,
                Intent(this@PodcastService, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                },
                PendingIntent.FLAG_IMMUTABLE
            )

            return builder
                .setContentText(getString(R.string.podcast_notification_loading_player))
                .setContentIntent(contentIntent)
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.drawable.ic_notification_small)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build()
        }

        fun buildNotification(sessionToken: MediaSessionCompat.Token): Notification {
            if (shouldCreateNowPlayingChannel()) {
                createNowPlayingChannel()
            }

            val builder = NotificationCompat.Builder(this@PodcastService, PODCAST_CHANNEL)
            val description = mediaController.metadata?.description
            val playbackState = mediaController.playbackState
            val notificationTitle = if (description?.title.isNullOrBlank()) {
                getString(R.string.podcast_notification_loading_player)
            } else {
                description?.title
            }

            if (playbackState != null) {
                setupMediaStyle(playbackState, builder, sessionToken)
            }

            val deleteIntent = PendingIntent.getBroadcast(
                applicationContext,
                0,
                Intent(this@PodcastService, NotificationDismissReceiver::class.java).apply {
                    action = ACTION_NOTIFICATION_DISMISSED
                },
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
            )

            val contentIntent = PendingIntent.getActivity(
                athleticApplication,
                0,
                Intent(this@PodcastService, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                },
                PendingIntent.FLAG_IMMUTABLE
            )

            return builder
                .setContentText(description?.subtitle)
                .setContentTitle(notificationTitle)
                .setContentIntent(contentIntent)
                .setDeleteIntent(deleteIntent)
                .setLargeIcon(description?.iconBitmap)
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.drawable.ic_notification_small)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build()
        }

        private fun setupMediaStyle(
            playbackState: PlaybackStateCompat,
            builder: NotificationCompat.Builder,
            sessionToken: MediaSessionCompat.Token
        ) {
            // Tt Only add actions for skip back, play/pause, skip forward, based on what's enabled.
            if (playbackState.isSkipToPreviousEnabled) {
                builder.addAction(skipToPreviousAction)
            } else {
                builder.addAction(skipToPreviousActionDisabled)
            }

            if (playbackState.isPauseEnabled) {
                builder.addAction(pauseAction)
            } else if (playbackState.isPlayEnabled) {
                builder.addAction(playAction)
            }

            if (playbackState.isSkipToNextEnabled) {
                builder.addAction(skipToNextAction)
            } else {
                builder.addAction(skipToNextActionDisabled)
            }

            val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(sessionToken)
                .setShowActionsInCompactView(0, 1, 2)
                .setShowCancelButton(true)

            builder.setStyle(mediaStyle)
        }

        private fun shouldCreateNowPlayingChannel() =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !nowPlayingChannelExists()

        @RequiresApi(Build.VERSION_CODES.O)
        private fun nowPlayingChannelExists() = notificationManager.getNotificationChannel(PODCAST_CHANNEL) != null

        @RequiresApi(Build.VERSION_CODES.O)
        private fun createNowPlayingChannel() {
            val notificationChannel = NotificationChannel(
                PODCAST_CHANNEL,
                this@PodcastService.getString(R.string.podcast_player_channel),
                NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.description = this@PodcastService.getString(R.string.podcast_player_channel)

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun onNotificationDismissed() {
        NetworkManager.connected.removeOnPropertyChangedCallback(networkChangeCallback)
    }

    // TT NoisyReceiver
    private inner class NoisyReceiver(sessionToken: MediaSessionCompat.Token) : BroadcastReceiver() {
        private val noisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        private val controller = MediaControllerCompat(this@PodcastService, sessionToken)
        private var registered = false

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY)
                controller.transportControls.pause()
        }

        fun register() {
            if (!registered) {
                ContextCompat.registerReceiver(this@PodcastService, this, noisyIntentFilter, ContextCompat.RECEIVER_NOT_EXPORTED)
                registered = true
            }
        }

        fun unregister() {
            if (registered) {
                this@PodcastService.unregisterReceiver(this)
                registered = false
            }
        }
    }

    // TT NotificationDismissReceiver
    private inner class NotificationDismissReceiver : BroadcastReceiver() {
        private val dismissFilter = IntentFilter(ACTION_NOTIFICATION_DISMISSED)
        private var registered = false

        override fun onReceive(context: Context, intent: Intent) {
            onNotificationDismissed()
            unregister()
        }

        fun register() {
            if (!registered) {
                ContextCompat.registerReceiver(this@PodcastService, this, dismissFilter, ContextCompat.RECEIVER_NOT_EXPORTED)
                registered = true
            }
        }

        fun unregister() {
            if (registered) {
                this@PodcastService.unregisterReceiver(this)
                registered = false
            }
        }
    }
}