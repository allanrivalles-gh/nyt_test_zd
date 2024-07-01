package com.theathletic.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.theathletic.R
import com.theathletic.entity.room.LiveAudioRoomEntity
import com.theathletic.extension.notificationManager
import com.theathletic.rooms.ui.LiveAudioRoomActivity

object LiveAudioRoomNotification {

    fun create(
        context: Context,
        roomEntity: LiveAudioRoomEntity,
        mediaSessionToken: MediaSessionCompat.Token
    ): Notification {
        val notificationManager = context.notificationManager

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            notificationManager.getNotificationChannel(LiveAudioRoomService.LIVE_AUDIO_CHANNEL) == null
        ) {
            NotificationChannel(
                LiveAudioRoomService.LIVE_AUDIO_CHANNEL,
                context.getString(R.string.rooms_notification_channel_title),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                notificationManager.createNotificationChannel(this)
            }
        }

        val mainIntent = PendingIntent.getActivity(
            context,
            0,
            LiveAudioRoomActivity.newIntent(context, roomEntity.id).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = PendingIntent.getService(
            context,
            0,
            Intent(context, LiveAudioRoomService::class.java).apply {
                putExtra(LiveAudioRoomService.EXTRA_ROOM_ACTION, LiveAudioRoomService.ACTION_STOP)
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopAction = NotificationCompat.Action.Builder(
            R.drawable.ic_media_stop,
            context.getString(R.string.global_stop),
            stopIntent
        ).build()

        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setShowActionsInCompactView(0)
            .setMediaSession(mediaSessionToken)

        return NotificationCompat.Builder(context, LiveAudioRoomService.LIVE_AUDIO_CHANNEL)
            .setStyle(mediaStyle)
            .addAction(stopAction)
            .setContentTitle(roomEntity.title)
            .setContentText(roomEntity.subtitle)
            .setContentIntent(mainIntent)
            .setOnlyAlertOnce(true)
            .setSmallIcon(R.drawable.ic_notification_small)
            .setColor(context.getColor(R.color.ath_grey_80))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }
}