package com.theathletic.analytics

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LifecycleObserver
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.jakewharton.rxrelay2.PublishRelay
import com.theathletic.analytics.data.local.AnalyticsEvent
import com.theathletic.analytics.data.local.FlexibleAnalyticsEvent
import com.theathletic.analytics.repository.AnalyticsRepository
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Exposes
import com.theathletic.annotation.autokoin.Named
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.extension.extLogError
import com.theathletic.utility.coroutines.DispatcherProvider
import io.reactivex.rxkotlin.subscribeBy
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

val impressionDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).also {
    it.timeZone = TimeZone.getTimeZone("UTC")
}

interface AnalyticsTracker {
    fun trackEvent(analyticsEvent: AnalyticsEvent)
    fun trackEvent(event: FlexibleAnalyticsEvent)
    fun startAnalyticsUploadWork()
    fun startOneOffUploadWork()
}

@SuppressLint("CheckResult")
@ExperimentalCoroutinesApi
@Exposes(AnalyticsTracker::class)
class AnalyticsTrackerImpl @AutoKoin(Scope.SINGLE) constructor(
    private val analyticsRepository: AnalyticsRepository,
    @Named("application-context") private val applicationContext: Context,
    dispatcherProvider: DispatcherProvider
) : LifecycleObserver, AnalyticsTracker {
    private val uploadCoroutineWorkerName = "analyticsTrackerUploadWorkCoroutine"

    private val analyticsEventRelay = PublishRelay.create<AnalyticsEvent>()
    private val flexibleAnalyticsEventRelay = PublishRelay.create<FlexibleAnalyticsEvent>()

    private val exceptionHandler = CoroutineExceptionHandler { _, error -> error.extLogError() }
    private val analyticsScope = CoroutineScope(
        SupervisorJob() + dispatcherProvider.io + exceptionHandler
    )

    init {
        val bufferTimeSeconds = 10L
        val bufferEventCount = 10

        analyticsEventRelay
            .timestamp()
            .map { timeWrap ->
                val event = timeWrap.value()
                event.timestampMs = timeWrap.time(TimeUnit.MILLISECONDS)
                event.dateTime = impressionDateFormat.format(timeWrap.time())
                event
            }
            .buffer(bufferTimeSeconds, TimeUnit.SECONDS, bufferEventCount)
            .filter { value -> value.isNotEmpty() }
            .subscribeBy(
                onNext = { events ->
                    saveAnalyticsEvents(events)
                },
                onError = { error ->
                    Timber.e(error)
                }
            )

        flexibleAnalyticsEventRelay
            .timestamp()
            .map { timeWrap ->
                val event = timeWrap.value()
                event.timestampMs = timeWrap.time(TimeUnit.MILLISECONDS)
                event
            }
            .buffer(bufferTimeSeconds, TimeUnit.SECONDS, bufferEventCount)
            .filter { value -> value.isNotEmpty() }
            .subscribeBy(
                onNext = this::saveFlexibleAnalyticsEvents,
                onError = Timber::e
            )
    }

    override fun trackEvent(analyticsEvent: AnalyticsEvent) {
        analyticsEventRelay.accept(analyticsEvent)
    }

    override fun trackEvent(event: FlexibleAnalyticsEvent) {
        flexibleAnalyticsEventRelay.accept(event)
    }

    override fun startAnalyticsUploadWork() {
        val workManager = WorkManager.getInstance(applicationContext)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val saveRequestCoroutine = PeriodicWorkRequestBuilder<UploadAnalyticsWorker>(
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            uploadCoroutineWorkerName,
            ExistingPeriodicWorkPolicy.KEEP,
            saveRequestCoroutine
        )
    }

    override fun startOneOffUploadWork() {
        val workManager = WorkManager.getInstance(applicationContext)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val saveRequestCoroutine = OneTimeWorkRequestBuilder<UploadAnalyticsWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueue(saveRequestCoroutine)
    }

    private fun saveAnalyticsEvents(events: List<AnalyticsEvent>) = analyticsScope.launch {
        analyticsRepository.saveAnalyticsEvents(events)
    }

    private fun saveFlexibleAnalyticsEvents(events: List<FlexibleAnalyticsEvent>) {
        analyticsScope.launch {
            analyticsRepository.saveFlexibleAnalyticsEvents(events)
        }
    }
}