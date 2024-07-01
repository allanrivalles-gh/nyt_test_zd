package com.theathletic.analytics

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.theathletic.analytics.newarch.schemas.KafkaTopic
import com.theathletic.analytics.repository.AnalyticsRepository
import com.theathletic.extension.extLogError
import com.theathletic.utility.coroutines.DispatcherProvider
import java.lang.Exception
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class UploadAnalyticsWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {
    companion object {
        @VisibleForTesting
        val MAX_API_UPLOAD_ATTEMPT_COUNT = 5
    }

    private val repository by inject<AnalyticsRepository>()
    private val dispatcherProvider by inject<DispatcherProvider>()

    override suspend fun doWork(): Result = withContext(dispatcherProvider.io) {
        Timber.v("Uploading analytics v2.")
        try {
            repository.clearEventsOlderThanXDays(7)
            repository.clearFlexibleEventsOlderThanXDays(7)
        } catch (t: Throwable) {
            t.extLogError()
        }

        try {
            while (repository.hasAnalyticsEventsToPost()) {
                val events = repository.getAnalyticsEventsToPost(100)
                repository.uploadAnalyticsEvents(events)
                repository.deleteAnalyticsEvents(events)
            }

            // Upload events for all kafka topics
            val results = KafkaTopic.values().map { topic ->
                async { uploadEventsForTopic(topic) }
            }.awaitAll()

            if (results.any { it == RequestResult.FAIL }) {
                Timber.e("Failure detected")
                retryOrFail()
            } else {
                Timber.d("Success!")
                Result.success()
            }
        } catch (t: Throwable) {
            retryOrFail()
        }
    }

    private suspend fun uploadEventsForTopic(kafkaTopic: KafkaTopic) = try {
        while (repository.hasFlexibleAnalyticsEventsToPost(kafkaTopic)) {
            val events = repository.getFlexibleAnalyticsEventsToPost(kafkaTopic, 100)
            repository.uploadFlexibleAnalyticsEvents(kafkaTopic, events)
            repository.deleteFlexibleAnalyticsEvents(events)
        }
        RequestResult.SUCCESS
    } catch (e: Exception) {
        RequestResult.FAIL
    }

    private suspend fun retryOrFail() = when {
        runAttemptCount >= MAX_API_UPLOAD_ATTEMPT_COUNT -> {
            Timber.i("Max Analytics tries attempted. Wiping flexible events table.")
            repository.deleteAllFlexibleAnalyticsEvents()

            Result.failure()
        }
        else -> {
            Timber.i("Error uploading on attempt #${runAttemptCount + 1}. Retrying.")

            Result.retry()
        }
    }

    private enum class RequestResult { SUCCESS, FAIL }
}