package com.theathletic.profile.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.profile.data.remote.AccountDeletionApi

class AccountDeletionRepository @AutoKoin constructor(
    private val accountDeletionApi: AccountDeletionApi,
) {

    suspend fun deleteAccount(
        userId: String,
        email: String,
        country: String?,
        countrySubDivision: String?,
    ): Result<Unit> {
        return accountDeletionApi.deleteAccount(
            userId = userId,
            email = email,
            country = country,
            countrySubDivision = countrySubDivision
        )
    }
}