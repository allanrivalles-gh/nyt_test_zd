package com.theathletic.profile

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.profile.data.ManageAccountUser
import com.theathletic.profile.data.toDomain
import com.theathletic.user.IUserManager

class GetCurrentUserUseCase @AutoKoin constructor(
    private val userManager: IUserManager,
) {
    operator fun invoke(): Result<ManageAccountUser> {
        val currentUser = userManager.getCurrentUser()
            ?: return Result.failure(Throwable("Unable to find current user"))
        return Result.success(currentUser.toDomain(userManager.isUserSubscribed()))
    }
}