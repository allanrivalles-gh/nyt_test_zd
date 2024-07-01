package com.theathletic.notifications

import android.app.PendingIntent
import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.iterable.iterableapi.IterableFirebaseMessagingService
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.context.DeepLinkParams
import com.theathletic.utility.Preferences
import kotlin.math.abs
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

private const val KEY_CAMPAIGN_ID = "campaignId"
private const val KEY_IS_GHOST_PUSH = "isGhostPush"

class FirebaseMessagingService : FirebaseMessagingService(), KoinComponent {
    private val analytics by inject<Analytics>()
    private val highPriorityNotificationChannel by inject<HighPriorityNotificationChannel>()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("[push]: onNewToken")

        // Tt Iterable
        IterableFirebaseMessagingService.handleTokenRefresh()

        // Tt Get updated InstanceID token.
        Preferences.pushTokenKey = token
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("[push]: FirebaseMessagingService.onMessageReceived")

        val handledByIterable = IterableFirebaseMessagingService.handleMessageReceived(this, remoteMessage)

        var campaignData: NotificationCampaignData? = null
        if (handledByIterable) {
            Timber.d("[push]: handled by iterable")

            val payload = IterableNotificationPayload.fromMap(remoteMessage.data)
            campaignData = NotificationCampaignData.fromIterable(payload)
        } else {
            Timber.d("[push]: handled by athletic")
            val payload = AthleticNotificationPayload.fromMap(remoteMessage.data)
            campaignData = NotificationCampaignData.fromAthletic(payload)

            remoteMessage.notification?.let { notification ->
                if (campaignData.isGhostPush) return@let

                // it doesn't make sense to show the notification if both are not set
                if (notification.title.isNullOrEmpty() && notification.body.isNullOrEmpty()) return@let

                val intent = Intent(this, AthleticNotificationOpenReceiver::class.java)
                    .putExtraFromMap(AthleticNotificationPayload.payloadKey, remoteMessage.data)
                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val alert = HighPriorityNotificationChannel.Alert(
                    notification.title ?: "",
                    notification.body ?: "",
                    notification.imageUrl?.toString(),
                )
                highPriorityNotificationChannel.notify(
                    this,
                    remoteMessage.identifier(),
                    alert,
                    pendingIntent
                )
            }
        }

        val pushMetablobParams = HashMap<String, String>()
        campaignData.campaignId?.let { pushMetablobParams[KEY_CAMPAIGN_ID] = it }
        pushMetablobParams[KEY_IS_GHOST_PUSH] = campaignData.isGhostPush.toString()
        analytics.updateContext(DeepLinkParams(campaignData.source, pushMetablobParams))
    }
}

private fun Intent.putExtraFromMap(name: String, from: Map<String, String>): Intent = putExtra(name, from[name])

// same logic used in `IterableNotificationHelperImpl.createNotification`
private fun RemoteMessage.identifier(): Int = abs(messageId?.hashCode() ?: System.currentTimeMillis().toInt())