package com.theathletic.audio

import android.content.Context
import kotlinx.coroutines.flow.Flow

interface AudioEngine {
    fun initialize(context: Context)
    fun destroy()

    fun changeMute(isMuted: Boolean)
    fun setVolume(level: Float)
    fun setOnStage(onStage: Boolean)

    fun joinChannel(channelName: String, token: String, userId: Long)
    fun leaveChannel()

    val audioRoomState: Flow<AudioEngineState?>
    val userIdToVolume: Flow<Map<String, Int>>
    val events: Flow<AudioEngineEvent>
}

fun createAudioEngine(): AudioEngine = AgoraAudioEngine()

sealed class AudioEngineEvent {
    class SwitchOnStage(val isOnStage: Boolean) : AudioEngineEvent()
}

data class AudioEngineState(
    val channelId: String,
    val usersOnStage: Set<LiveAudioStageUser> = emptySet(),
    val isCurrentUserOnStage: Boolean = false,
    val isCurrentUserMuted: Boolean = false,
)