package com.theathletic.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.theathletic.AthleticApplication
import com.theathletic.R
import com.theathletic.extension.extGetString
import com.theathletic.extension.notificationManager
import timber.log.Timber

class FirebaseMessagingHelper {
    fun initLocalNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT < 26)
            return

        val channel = NotificationChannel(R.string.local_channel.extGetString(), "Athletic Local Push Messages", NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = "Receiving Athletic push messages"
        channel.enableLights(true)
        channel.lightColor = Color.WHITE
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(200, 100)
        context.notificationManager.createNotificationChannel(channel)
    }

    fun showLocalNotification(
        notificationId: Int,
        title: String,
        body: String,
        contentIntent: PendingIntent?
    ) {
        val context = AthleticApplication.getContext()

        val bigStyle = NotificationCompat.BigTextStyle()
        bigStyle.setBigContentTitle(title)
        bigStyle.bigText(body)

        initLocalNotificationChannel(context)
        context.notificationManager.notify(
            notificationId,
            NotificationCompat.Builder(context, R.string.local_channel.extGetString())
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(contentIntent)
                .setStyle(bigStyle)
                .setSmallIcon(R.drawable.ic_notification_small)
                .setAutoCancel(true)
                .build()
        )
        Timber.i("[FCM] Local notification created")
    }
}