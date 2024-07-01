package com.theathletic.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.theathletic.BuildConfig
import com.theathletic.extension.applyEvenDistributionDelay
import com.theathletic.network.ResponseStatus.Success
import com.theathletic.repository.safeApiRequest
import com.theathletic.user.UserManager
import com.theathletic.user.data.UserRepository
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserUpdateScheduler(
    private val shouldScheduleImmediately: Boolean = BuildConfig.DEBUG
) {
    fun schedule(appContext: Context) {
        val constraints = if (shouldScheduleImmediately) {
            Constraints.NONE
        } else {
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        }

        val updateUserRequest =
            PeriodicWorkRequestBuilder<UpdateUserWorker>(6, TimeUnit.HOURS)
                .applyEvenDistributionDelay(shouldScheduleImmediately)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(appContext).enqueueUniquePeriodicWork(
            "UpdateUserRequest",
            ExistingPeriodicWorkPolicy.REPLACE,
            updateUserRequest
        )
    }
}

class UpdateUserWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val userRepository by inject<UserRepository>()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        if (UserManager.isUserLoggedIn()) {
            val response = safeApiRequest { userRepository.fetchUser(UserManager.getCurrentUserId()) }
            if (response is Success) {
                UserManager.updateCurrentUser(userEntity = response.body)
            }
        }
        Result.success()
    }
}