package com.theathletic.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AthleticNotificationOpenReceiver : BroadcastReceiver(), KoinComponent {
    private val handler by inject<AthleticNotificationHandler>()

    override fun onReceive(context: Context, intent: Intent) {
        val payload = intent.extras?.let { AthleticNotificationPayload.fromBundle(it) }
        handler.onNotificationOpen(context, payload)
    }
}