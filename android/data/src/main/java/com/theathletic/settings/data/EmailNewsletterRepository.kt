package com.theathletic.settings.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.entity.settings.EmailSettingsResponse
import com.theathletic.network.ResponseStatus
import com.theathletic.repository.Repository
import com.theathletic.repository.safeApiRequest
import com.theathletic.settings.data.remote.SettingsRestApi
import kotlinx.coroutines.rx2.await

class EmailNewsletterRepository @AutoKoin(Scope.SINGLE) constructor(
    private val settingsRestApi: SettingsRestApi
) : Repository {

    suspend fun getUserEmailSettings(userId: Long): ResponseStatus<EmailSettingsResponse> =
        safeApiRequest { settingsRestApi.getUserEmailSettings(userId) }

    suspend fun emailNewsletterSubscribe(newsletter: String) = safeApiRequest {
        settingsRestApi.emailNewsLetterSubscribe(newsletter).await()
    }

    suspend fun emailNewsletterUnsubscribe(newsletter: String) = safeApiRequest {
        settingsRestApi.emailNewsletterUnsubscribe(newsletter).await()
    }
}