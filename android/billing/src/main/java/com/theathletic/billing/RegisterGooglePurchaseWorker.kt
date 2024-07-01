package com.theathletic.billing

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest.MIN_BACKOFF_MILLIS
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.theathletic.analytics.IAnalytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.billing.RegisterGooglePurchaseWorker.Companion.TOKEN_KEY
import com.theathletic.user.IUserManager
import com.theathletic.user.IUserManager.Companion.NO_USER
import com.theathletic.utility.IPreferences
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

@Suppress("LongParameterList")
class RegisterGooglePurchaseScheduler(
    private val shouldScheduleImmediately: Boolean = BuildConfig.DEBUG,
    private val userManager: IUserManager,
    private val workManager: WorkManager,
    private val analytics: IAnalytics,
    private val areDebugBillingToolsEnabled: () -> Boolean
) {

    fun scheduleIfNeeded(purchaseToken: String) {
        /**
         * We will only log the google subscription if all of those conditions are met:
         *  1) There is a user with a valid User ID
         *  2) Debug billing tools are not enabled
         */
        when {
            userManager.getCurrentUserId() == NO_USER -> return
            areDebugBillingToolsEnabled() -> return
        }

        schedule(purchaseToken)
    }

    private fun schedule(token: String) {
        val constraints = if (shouldScheduleImmediately) {
            Constraints.NONE
        } else {
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        }

        val purchaseData = workDataOf(TOKEN_KEY to token)

        val registerGoogleSubWorker = OneTimeWorkRequestBuilder<RegisterGooglePurchaseWorker>()
            .setConstraints(constraints)
            .setInputData(purchaseData)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        analytics.track(Event.Billing.LogGoogleSubStart)
        workManager.enqueueUniqueWork(
            "RegisterGoogleSubscriptionRequest:$token",
            ExistingWorkPolicy.KEEP,
            registerGoogleSubWorker
        )
    }
}

class RegisterGooglePurchaseWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {
    private val preferences by inject<IPreferences>()
    private val userManager by inject<IUserManager>()
    private val dispatcherProvider by inject<DispatcherProvider>()
    private val billingRepository by inject<BillingRepository>()
    private val analytics by inject<IAnalytics>()

    companion object {
        private const val MAX_RETRY_ATTEMPTS = 7
        const val TOKEN_KEY = "token"
    }

    data class WorkerInputs(val token: String)

    class InvalidInputsException : Exception()

    private fun parseJobInputs(inputData: Data?): WorkerInputs {
        val data = inputData ?: throw InvalidInputsException()
        val token = data.getString(TOKEN_KEY)
        return if (token == null) {
            throw InvalidInputsException()
        } else {
            WorkerInputs(token)
        }
    }

    override suspend fun doWork(): Result {
        val parsedInputs = try {
            parseJobInputs(inputData)
        } catch (e: InvalidInputsException) {
            analytics.track(Event.Billing.LogGoogleSubFailure)
            return Result.failure()
        }

        val purchaseData = billingRepository.getPurchaseDataByToken(parsedInputs.token)
        if (purchaseData == null) {
            analytics.track(Event.Billing.LogGoogleSubFailure)
            return Result.failure()
        }

        return withContext(dispatcherProvider.io) {
            try {
                val response = billingRepository.registerGooglePurchase(
                    purchaseData,
                    userManager.getDeviceId()
                )
                when {
                    response.isSuccessful -> {
                        preferences.logGoogleSubLastToken = parsedInputs.token
                        billingRepository.deletePurchaseDataByToken(purchaseData.googleToken)
                        analytics.track(Event.Billing.LogGoogleSubSuccess)
                        Result.success()
                    }

                    else -> parseResponseFromHttpStatus(response.code())
                }
            } catch (e: HttpException) {
                parseResponseFromHttpStatus(e.code())
            } catch (e: Exception) {
                // giving multiple attempts in case there are unknown exceptions
                if (runAttemptCount >= MAX_RETRY_ATTEMPTS) {
                    analytics.track(Event.Billing.LogGoogleSubFailure)
                    Result.failure()
                } else {
                    analytics.track(Event.Billing.LogGoogleSubRetry)
                    Result.retry()
                }
            }
        }
    }

    private fun parseResponseFromHttpStatus(status: Int): Result {
        return if (status in (500..599)) {
            analytics.track(Event.Billing.LogGoogleSubRetry)
            Result.retry()
        } else {
            analytics.track(Event.Billing.LogGoogleSubFailure)
            Result.failure()
        }
    }
}