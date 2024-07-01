package com.theathletic.preferences.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.settings.data.SettingsRepository
import com.theathletic.user.IUserManager
import timber.log.Timber

class UpdateTopSportNewsNotificationUseCase @AutoKoin constructor(
    private val repository: SettingsRepository,
    private val userManager: IUserManager
) {
    suspend operator fun invoke(optIn: Boolean): Result<Unit> {
        return try {
            repository.updateTopSportsNewsNotification(optIn)
            userManager.saveCurrentUser(
                userManager.getCurrentUser()?.apply {
                    topSportsNewsNotification = optIn
                },
                withRefresh = false
            )
            Result.success(Unit)
        } catch (error: Throwable) {
            Timber.e(error)
            Result.failure(error)
        }
    }
}