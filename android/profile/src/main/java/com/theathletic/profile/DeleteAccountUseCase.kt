package com.theathletic.profile

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.location.data.LocationRepository
import com.theathletic.profile.data.AccountDeletionRepository
import com.theathletic.user.IUserManager

class DeleteAccountUseCase @AutoKoin constructor(
    private val locationRepository: LocationRepository,
    private val accountDeletionRepository: AccountDeletionRepository,
    private val userManager: IUserManager,
) {
    suspend operator fun invoke(): Result<Unit> {
        val user = userManager.getCurrentUser()
        val userId = user?.id ?: return Result.failure(Throwable("Failed to load current user"))
        val email = user.email ?: return Result.failure(Throwable("Current user does not have an email"))
        return accountDeletionRepository.deleteAccount(
            userId = userId.toString(),
            email = email,
            country = locationRepository.getCountryCode(),
            countrySubDivision = locationRepository.getState()
        )
    }
}