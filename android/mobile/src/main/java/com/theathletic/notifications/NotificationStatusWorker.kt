package com.theathletic.notifications

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.theathletic.BuildConfig
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.extension.applyEvenDistributionDelay
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class NotificationStatusScheduler(
    private val shouldScheduleImmediately: Boolean = BuildConfig.DEBUG
) {
    fun schedule(appContext: Context) {
        val updateNotificationStatusRequest =
            PeriodicWorkRequestBuilder<NotificationStatusWorker>(1L, TimeUnit.DAYS)
                .applyEvenDistributionDelay(shouldScheduleImmediately)
                .setConstraints(Constraints.NONE)
                .build()

        WorkManager.getInstance(appContext)
            .enqueueUniquePeriodicWork(
                "UpdateNotificationStatusWorker",
                ExistingPeriodicWorkPolicy.REPLACE,
                updateNotificationStatusRequest
            )
    }
}

/**
 * Worker that periodically checks if notifications have been disabled by the user
 */
class NotificationStatusWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {
    private val analytics by inject<Analytics>()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Timber.i("running NotificationStatusWorker")
        if (NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()) {
            analytics.track(Event.Notification.SystemNotificationSettingEnabled())
        } else {
            analytics.track(Event.Notification.SystemNotificationSettingDisabled())
        }
        Result.success()
    }
}