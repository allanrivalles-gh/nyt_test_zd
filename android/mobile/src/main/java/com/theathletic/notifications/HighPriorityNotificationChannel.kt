package com.theathletic.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.os.Build
import androidx.core.app.NotificationCompat
import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.extension.notificationManager
import java.io.IOException
import java.net.URL
import timber.log.Timber

// high priority notification channel used to make sure the notification will be visible even if the app is in the foreground
class HighPriorityNotificationChannel @AutoKoin constructor() {
    data class Alert(
        val title: String,
        val body: String,
        val image: String?,
    )

    private fun ensureChannelExists(context: Context, channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT < 26) return
        val existingChannel = context.notificationManager.getNotificationChannel(channelId)

        // we also recreate the channel if the name has changed (it can happen when changing locale)
        if (existingChannel != null && existingChannel.name == channelName) return

        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        channel.description = ""
        channel.enableLights(true)
        context.notificationManager.createNotificationChannel(channel)
    }

    fun notify(
        context: Context,
        id: Int,
        alert: Alert,
        contentIntent: PendingIntent?
    ) {
        val channelId = "${context.packageName}.athl_high_priority"
        ensureChannelExists(context, channelId, context.getString(R.string.push_settings_iterable_channel))

        val builder = NotificationCompat.Builder(context, channelId)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(alert.title)
            .setContentText(alert.body)
            .setContentIntent(contentIntent)
            .setSmallIcon(R.drawable.ic_notification_small)
            .setAutoCancel(true)

        if (alert.image == null) {
            builder.setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(alert.body)
            )
        } else {
            val image = getBitmapFromURL(alert.image)
            val largeIcon: Icon? = null
            builder.setLargeIcon(image)
            builder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(image)
                    .bigLargeIcon(largeIcon)
                    .setSummaryText(alert.body)
            )
        }

        context.notificationManager.notify(id, builder.build())
    }
}

private fun getBitmapFromURL(url: String): Bitmap? = try {
    val connection = URL(url).openConnection()
    connection.connect()
    BitmapFactory.decodeStream(connection.inputStream)
} catch (e: IOException) {
    Timber.e(e, "Failed to get bitmap from image url for push notification. URL: \"$url\".")
    null
}