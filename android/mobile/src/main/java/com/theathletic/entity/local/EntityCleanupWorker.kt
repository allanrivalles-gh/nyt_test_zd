package com.theathletic.entity.local

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.utility.coroutines.DispatcherProvider
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class EntityCleanupScheduler @AutoKoin(Scope.SINGLE) constructor() {

    companion object {
        private const val WORK_NAME = "EntityCleanupWorker"
        private const val TIME_DAYS = 1L
    }

    fun schedule(applicationContext: Context) {
        val cleanupRequest = PeriodicWorkRequestBuilder<EntityCleanupWorker>(
            TIME_DAYS,
            TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            cleanupRequest
        )
    }
}

class EntityCleanupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val entityDataSource by inject<EntityDataSource>()
    private val dispatcherProvider by inject<DispatcherProvider>()

    override suspend fun doWork(): Result = withContext(dispatcherProvider.io) {
        Timber.v("Attempting to cleanup old entities")
        try {
            entityDataSource.deleteOldEntities()
            Result.success()
        } catch (e: Exception) {
            Timber.e(e)
            Result.failure()
        }
    }
}