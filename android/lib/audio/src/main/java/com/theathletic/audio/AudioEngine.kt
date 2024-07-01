package com.theathletic.audio

import android.content.Context
import android.util.Log
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import java.lang.Exception

internal class AgoraAudioEngine : AudioEngine {

    companion object {
        const val STANDARD_VOLUME = 200
    }

    private var agoraEngine: RtcEngine? = null

    override val audioRoomState: Flow<AudioEngineState?> get() = _audioRoomState
    private var _audioRoomState: MutableStateFlow<AudioEngineState?> = MutableStateFlow(null)

    override val userIdToVolume: Flow<Map<String, Int>> get() = _userIdToVolume
    private var _userIdToVolume = MutableStateFlow<Map<String, Int>>(emptyMap())

    override val events: Flow<AudioEngineEvent> get() = _events
    private var _events = MutableSharedFlow<AudioEngineEvent>()

    private val eventHandler = object : IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            Timber.v("Joined channel: $channel uid: $uid")
            _audioRoomState.value = AudioEngineState(channelId = channel)
        }

        override fun onError(err: Int) {
            Timber.v("onError: $err")
        }

        override fun onLeaveChannel(stats: RtcStats?) {
            Timber.v("Left channel")
            _audioRoomState.value = null
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            Timber.v("User Joined $uid")
            _audioRoomState.updateIfNotNull {
                copy(usersOnStage = usersOnStage + LiveAudioStageUser(uid.toString()))
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            Timber.v("User Offline $uid")
            _audioRoomState.updateIfNotNull {
                val newList = usersOnStage.toMutableSet()
                    .apply { removeAll { it.id == uid.toString() } }

                copy(usersOnStage = newList)
            }
        }

        override fun onAudioVolumeIndication(
            speakers: Array<out AudioVolumeInfo>,
            totalVolume: Int
        ) {
            _userIdToVolume.value = speakers.associate { it.uid.toString() to it.volume }
        }

        override fun onLocalAudioStateChanged(state: Int, error: Int) {
            Timber.v("onLocalAudioStateChanged: $state")
            _audioRoomState.updateIfNotNull {
                copy(
                    isCurrentUserMuted = state == Constants.LOCAL_AUDIO_STREAM_STATE_STOPPED ||
                        state == Constants.LOCAL_AUDIO_STREAM_STATE_FAILED
                )
            }
        }

        override fun onRemoteAudioStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
            Timber.v("onRemoteAudioStateChanged User: $uid State: $state")
            _audioRoomState.updateIfNotNull {
                val new = LiveAudioStageUser(
                    id = uid.toString(),
                    isMuted = state == Constants.REMOTE_AUDIO_STATE_STOPPED,
                )
                val newList = usersOnStage.map { old -> if (old.id == uid.toString()) new else old }

                copy(usersOnStage = newList.toSet())
            }
        }

        override fun onClientRoleChanged(oldRole: Int, newRole: Int) {
            Timber.v("onClientRoleChanged from $oldRole to $newRole")
            _audioRoomState.value?.also { state ->
                when {
                    state.isCurrentUserOnStage && newRole == Constants.CLIENT_ROLE_AUDIENCE -> {
                        _events.tryEmit(AudioEngineEvent.SwitchOnStage(isOnStage = false))
                    }
                    !state.isCurrentUserOnStage && newRole == Constants.CLIENT_ROLE_BROADCASTER -> {
                        _events.tryEmit(AudioEngineEvent.SwitchOnStage(isOnStage = true))
                    }
                }
            }
            _audioRoomState.updateIfNotNull {
                copy(isCurrentUserOnStage = newRole == Constants.CLIENT_ROLE_BROADCASTER)
            }
        }
    }

    override fun initialize(context: Context) {
        if (agoraEngine != null) return

        try {
            agoraEngine = RtcEngine.create(
                context,
                context.getString(R.string.agora_app_id),
                eventHandler
            ).apply {
                setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
                setAudioProfile(
                    Constants.AUDIO_PROFILE_SPEECH_STANDARD,
                    Constants.AUDIO_SCENARIO_DEFAULT
                )

                adjustPlaybackSignalVolume(STANDARD_VOLUME)
                enableAudioVolumeIndication(200, 3, false)
            }
        } catch (e: Exception) {
            Timber.e("Failed to initialize agora engine: ${Log.getStackTraceString(e)}")
        }
    }

    override fun destroy() {
        agoraEngine?.leaveChannel()
        _audioRoomState.value = null
        RtcEngine.destroy()
        agoraEngine = null
    }

    override fun joinChannel(channelName: String, token: String, userId: Long) {
        if (_audioRoomState.value?.channelId == channelName) {
            Timber.v("Already in room $channelName")
            return
        }

        agoraEngine?.setClientRole(Constants.CLIENT_ROLE_AUDIENCE)
        if (_audioRoomState.value?.channelId != null) {
            agoraEngine?.leaveChannel()
        }
        agoraEngine?.joinChannel(token, channelName, null, userId.toInt())
    }

    override fun leaveChannel() {
        agoraEngine?.leaveChannel()
    }

    override fun changeMute(isMuted: Boolean) {
        agoraEngine?.enableLocalAudio(!isMuted)
        agoraEngine?.muteLocalAudioStream(isMuted)
    }

    override fun setVolume(level: Float) {
        agoraEngine?.adjustPlaybackSignalVolume((STANDARD_VOLUME * level).toInt())
    }

    override fun setOnStage(onStage: Boolean) {
        agoraEngine?.setClientRole(
            when {
                onStage -> Constants.CLIENT_ROLE_BROADCASTER
                else -> Constants.CLIENT_ROLE_AUDIENCE
            }
        )
    }
}

private fun <T> MutableStateFlow<T?>.updateIfNotNull(block: (T.() -> T)) {
    val currentValue = this.value ?: return
    this.value = block(currentValue)
}