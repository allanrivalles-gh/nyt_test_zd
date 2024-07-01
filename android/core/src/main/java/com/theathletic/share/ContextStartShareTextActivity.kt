package com.theathletic.share

import android.app.PendingIntent
import android.content.Context
import android.content.Intent

fun Context.startShareTextActivity(
    title: String,
    textToSend: String,
    shareKey: String? = null
) {
    val sendIntent = Intent()
    sendIntent.action = Intent.ACTION_SEND
    sendIntent.putExtra(Intent.EXTRA_TEXT, textToSend)
    sendIntent.type = "text/plain"
    val receiver = Intent(this, ShareBroadcastReceiver::class.java)
    if (shareKey != null) {
        receiver.putExtra(shareKey, true)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        this,
        0,
        receiver,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    startActivity(
        Intent.createChooser(
            sendIntent,
            title,
            pendingIntent.intentSender
        )
    )
}