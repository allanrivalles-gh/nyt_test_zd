package com.theathletic.preferences.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Error
import com.theathletic.SetTopSportsNewsOptMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.network.apollo.FetchPolicy
import com.theathletic.network.apollo.httpFetchPolicy

class SettingsApi @AutoKoin(Scope.SINGLE) constructor(
    private val client: ApolloClient
) {
    suspend fun setTopSportsNewsNotificationOpt(optIn: Boolean) {
        val result = client.mutation(SetTopSportsNewsOptMutation(optIn))
            .httpFetchPolicy(FetchPolicy.NetworkOnly)
            .execute()

        if (result.hasErrors()) throw NotificationNotUpdatedException("topSportsNews", result.errors ?: emptyList())
        val isUpdated = result.data?.setTopSportsNewsOpt?.response ?: false
        if (isUpdated.not()) throw NotificationNotUpdatedException("topSportsNews")
    }
}

class NotificationNotUpdatedException(notification: String = "", errors: List<Error> = emptyList()) :
    Exception("Unable to update notification for $notification: ${errors.joinToString(" - ")}")