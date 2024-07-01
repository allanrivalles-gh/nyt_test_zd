package com.theathletic.audio

import android.content.Context
import android.media.MediaPlayer
import com.theathletic.annotation.autokoin.AutoKoin

class LiveAudioRoomSoundNotifier @AutoKoin constructor() {

    enum class SoundType {
        JOIN_STAGE,
        LEAVE_STAGE,
    }

    private val cache = mutableMapOf<SoundType, MediaPlayer>()

    fun playSound(context: Context, type: SoundType) {
        val mediaPlayer = cache.getOrPut(type) {
            val resource = when (type) {
                SoundType.JOIN_STAGE -> R.raw.notif_on_stage
                SoundType.LEAVE_STAGE -> R.raw.notif_off_stage
            }
            MediaPlayer.create(context, resource)
        }

        mediaPlayer.start()
    }

    fun release() {
        cache.values.forEach { it.release() }
        cache.clear()
    }
}