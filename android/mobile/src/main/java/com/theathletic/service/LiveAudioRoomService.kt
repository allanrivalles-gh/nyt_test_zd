package com.theathletic.service

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.os.IBinder
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.audio.AudioEngine
import com.theathletic.audio.AudioEngineEvent
import com.theathletic.audio.LiveAudioRoomSoundNotifier
import com.theathletic.audio.createAudioEngine
import com.theathletic.chat.data.ChatRepository
import com.theathletic.extension.audioManager
import com.theathletic.rooms.LiveAudioRoomStateManager
import com.theathletic.rooms.LiveRoomRequestWatcher
import com.theathletic.rooms.RoomsRepository
import com.theathletic.rooms.remote.LiveRoomBackgroundSyncer
import com.theathletic.rooms.ui.LiveAudioEvent
import com.theathletic.rooms.ui.LiveAudioEventConsumer
import com.theathletic.rooms.ui.LiveAudioEventProducer
import com.theathletic.user.IUserManager
import com.theathletic.utility.coroutines.collectIn
import com.theathletic.utility.coroutines.doWithPrevious
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber

class LiveAudioRoomService : Service() {

    companion object {
        const val LIVE_AUDIO_CHANNEL: String = "com.theathletic.android.LIVE_AUDIO"

        private const val NOTIF_ID = 0x1337
        const val EXTRA_ROOM_ACTION = "event"
        const val ACTION_STOP = "stop"
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private lateinit var engine: AudioEngine
    private var mediaSession: LiveRoomMediaSession? = null
    private var compareJob: Job? = null

    private val userManager: IUserManager by inject()
    private val roomRepository: RoomsRepository by inject()

    private val roomStateManager: LiveAudioRoomStateManager by inject()
    private val liveAudioEvenControls: LiveAudioEventConsumer by inject()
    private val liveAudioEventProducer: LiveAudioEventProducer by inject()
    private val soundNotifier: LiveAudioRoomSoundNotifier by inject()
    private val backgroundSyncer: LiveRoomBackgroundSyncer by inject()
    private val requestWatcher: LiveRoomRequestWatcher by inject()
    private val chatRepository: ChatRepository by inject()
    private val analytics: Analytics by inject()

    private var audioFocusRequest: AudioFocusRequestCompat? = null

    private val binder by lazy {
        object : LiveAudioRoomControls.Stub() {
            override fun joinRoom(room: String, token: String) {
                this@LiveAudioRoomService.joinRoom(room, token)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.v("onCreate")

        engine = createAudioEngine()
        engine.initialize(this)

        engine.userIdToVolume.collectIn(serviceScope) { roomStateManager.userIdToVolume.value = it }
        engine.audioRoomState.collectIn(serviceScope) { roomStateManager.audioEngineState.value = it }

        engine.events.collectIn(serviceScope) { event ->
            when (event) {
                is AudioEngineEvent.SwitchOnStage -> playStageNotificationSound(event.isOnStage)
            }
        }

        liveAudioEvenControls.collectIn(serviceScope) { event ->
            when (event) {
                is LiveAudioEvent.LeaveRoom -> leaveRoom()
                is LiveAudioEvent.ChangeMute -> engine.changeMute(event.isMuted)
                is LiveAudioEvent.SwapStageStatus -> engine.setOnStage(event.onStage)
                else -> {}
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.v("onDestroy")

        engine.destroy()
        mediaSession?.release()
        soundNotifier.release()

        serviceScope.cancel()
    }

    private fun joinRoom(room: String, token: String) {
        Timber.v("Join Room $room")
        serviceScope.launch {
            val roomEntity = roomRepository.getLiveAudioRoom(room) ?: return@launch
            if (roomStateManager.currentRoom.value?.id == room) {
                Timber.v("Already in room $room")
                return@launch
            }
            roomStateManager.currentRoom.value = roomEntity

            if (audioFocusRequest == null && !requestAudioFocus()) {
                liveAudioEventProducer.emit(LiveAudioEvent.LeaveRoom())
                stopSelf()
                return@launch
            }

            engine.joinChannel(room, token, userManager.getCurrentUserId())

            mediaSession = LiveRoomMediaSession(this@LiveAudioRoomService).also { session ->
                val notification = LiveAudioRoomNotification.create(
                    this@LiveAudioRoomService,
                    roomEntity,
                    session.sessionToken
                )
                startForeground(NOTIF_ID, notification)
            }

            backgroundSyncer.startSync(room, this)

            launch {
                roomEntity.chatRoomId?.let { subscribeToChatEvents(it) }
            }

            compareJob?.cancel()
            compareJob = roomRepository.getLiveAudioRoomFlow(room)
                .doWithPrevious { old, new -> requestWatcher.compare(old, new) }
                .collectIn(serviceScope) {
                    roomStateManager.currentRoom.value = it
                }
        }
    }

    private fun requestAudioFocus(): Boolean {
        val audioAttributes = AudioAttributesCompat.Builder()
            .setUsage(AudioAttributesCompat.USAGE_MEDIA)
            .setContentType(AudioAttributesCompat.CONTENT_TYPE_SPEECH)
            .build()
        audioFocusRequest = AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN)
            .setAudioAttributes(audioAttributes)
            .setOnAudioFocusChangeListener(this::onAudioFocusChanged)
            .build()

        val result = AudioManagerCompat.requestAudioFocus(audioManager, audioFocusRequest!!)
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    private suspend fun subscribeToChatEvents(chatRoomId: String) {
        chatRepository.subscribeToChatEvents(chatRoomId)
    }

    private fun leaveRoom() {
        Timber.v("Leave Room")

        audioFocusRequest?.let {
            AudioManagerCompat.abandonAudioFocusRequest(audioManager, it)
        }
        audioFocusRequest = null

        engine.leaveChannel()
        mediaSession?.release()

        stopForeground(true)

        compareJob?.cancel()
        compareJob = null

        roomStateManager.currentRoom.value = null

        backgroundSyncer.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.v("onStartCommand")

        intent?.getStringExtra(EXTRA_ROOM_ACTION)?.let { action -> handleAction(action) }

        return START_STICKY
    }

    private fun handleAction(action: String) {
        when (action) {
            ACTION_STOP -> serviceScope.launch {
                val currentRoomId = roomStateManager.currentRoom.value?.id ?: return@launch
                analytics.track(
                    Event.LiveRoom.Click(
                        view = "liveroom_lock_screen",
                        element = "leave_room",
                        object_type = "room_id",
                        object_id = currentRoomId,
                        room_id = currentRoomId,
                    )
                )
                liveAudioEventProducer.emit(LiveAudioEvent.LeaveRoom())
            }
            else -> Timber.e("Unknown live audio action $action")
        }
    }

    private fun playStageNotificationSound(isOnStage: Boolean) {
        soundNotifier.playSound(
            this,
            when {
                isOnStage -> LiveAudioRoomSoundNotifier.SoundType.JOIN_STAGE
                else -> LiveAudioRoomSoundNotifier.SoundType.LEAVE_STAGE
            }
        )
    }

    private fun onAudioFocusChanged(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                Timber.v("Lost audio focus permanently. Leaving room...")
                serviceScope.launch {
                    liveAudioEventProducer.emit(LiveAudioEvent.LeaveRoom())
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                Timber.v("Lost audio focus temporarily. Pausing...")
                engine.setVolume(.0f)
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                Timber.v("Lost audio focus temporarily. Ducking...")
                engine.setVolume(.25f)
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                Timber.v("Gained audio focus...")
                engine.setVolume(1f)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        Timber.v("onBind")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Timber.v("onUnbind")
        return super.onUnbind(intent)
    }
}