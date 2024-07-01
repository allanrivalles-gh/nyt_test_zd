package com.theathletic.user.data.remote

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest.MIN_BACKOFF_MILLIS
import androidx.work.WorkerParameters
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Named
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.network.ResponseStatus
import com.theathletic.repository.safeApiRequest
import com.theathletic.user.IUserManager
import com.theathletic.user.data.UserRepository
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PrivacyAcknowledgmentScheduler @AutoKoin(Scope.SINGLE) constructor(
    @Named("application-context") private val appContext: Context
) {
    fun schedule() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val privacyAcknowledgmentWorker = OneTimeWorkRequestBuilder<PrivacyAcknowledgmentWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(appContext).enqueueUniqueWork(
            "PrivacyAcknowledgmentRequest",
            ExistingWorkPolicy.KEEP,
            privacyAcknowledgmentWorker
        )
    }
}

class PrivacyAcknowledgmentWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {
    private val userRepository by inject<UserRepository>()
    private val userManager by inject<IUserManager>()
    private val analytics by inject<Analytics>()

    companion object {
        private const val MAX_RETRY_ATTEMPTS = 5
    }

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        val response = safeApiRequest {
            userRepository.acceptTermsAndPrivacy()
        }
        when (response) {
            is ResponseStatus.Success -> {
                userManager.getCurrentUser()?.let { user ->
                    user.termsAndConditions = true
                    user.privacyPolicy = true
                    userManager.saveCurrentUser(user)
                }
                trackAcknowledgmentStatus("success")
                Result.success()
            }
            is ResponseStatus.Error -> {
                if (runAttemptCount >= MAX_RETRY_ATTEMPTS) {
                    trackAcknowledgmentStatus("failure")
                    Result.failure()
                } else {
                    trackAcknowledgmentStatus("retry")
                    Result.retry()
                }
            }
        }
    }

    private fun trackAcknowledgmentStatus(status: String) = analytics.track(Event.User.PrivacyAcknowledgment(status))
}