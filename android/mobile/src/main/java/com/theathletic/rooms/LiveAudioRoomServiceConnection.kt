package com.theathletic.rooms

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.theathletic.service.LiveAudioRoomControls
import com.theathletic.service.LiveAudioRoomService

class LiveAudioRoomServiceConnection : ServiceConnection {

    private var controls: LiveAudioRoomControls? = null
    var onControlsAvailable: ((controls: LiveAudioRoomControls) -> Unit)? = null
        set(value) {
            field = when {
                controls != null -> {
                    value?.invoke(controls!!)
                    null
                }
                else -> value
            }
        }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        (service as? LiveAudioRoomControls)?.let { controls ->
            this@LiveAudioRoomServiceConnection.controls = controls
            onControlsAvailable?.invoke(controls)
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        controls = null
    }

    fun connect(context: Context) {
        if (controls == null) {
            context.bindService(
                Intent(context, LiveAudioRoomService::class.java),
                this,
                Context.BIND_AUTO_CREATE
            )
        }
    }

    fun disconnect(context: Context) {
        if (controls != null) {
            context.unbindService(this)
        }
    }
}