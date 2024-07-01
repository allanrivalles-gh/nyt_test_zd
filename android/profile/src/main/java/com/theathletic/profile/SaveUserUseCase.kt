package com.theathletic.profile

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.network.ResponseStatus
import com.theathletic.profile.ui.Customer
import com.theathletic.user.IUserManager
import com.theathletic.user.data.UserRepository

class SaveUserUseCase @AutoKoin constructor(
    private val userManager: IUserManager,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        customer: Customer,
    ): Result<Unit> {
        val response = userRepository.editUser(
            userId = userManager.getCurrentUserId(),
            firstName = customer.firstName,
            lastName = customer.lastName,
            email = customer.email,
        )

        return when (response) {
            is ResponseStatus.Success -> {
                saveCurrentUser(customer)
                Result.success(Unit)
            }
            is ResponseStatus.Error -> {
                Result.failure(response.throwable)
            }
        }
    }

    private fun saveCurrentUser(customer: Customer) {
        val currentUser = userManager.getCurrentUser()
        currentUser?.firstName = customer.firstName
        currentUser?.lastName = customer.lastName
        currentUser?.email = customer.email
        userManager.saveCurrentUser(
            currentUser
        )
    }
}