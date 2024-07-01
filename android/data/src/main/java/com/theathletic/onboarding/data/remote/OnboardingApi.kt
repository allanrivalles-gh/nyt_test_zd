package com.theathletic.onboarding.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Error
import com.theathletic.GetUserAttributionSurveyQuery
import com.theathletic.MarkUserAttributionSurveyAsSeenMutation
import com.theathletic.OnboardingFollowableItemsQuery
import com.theathletic.SubmitUserAttributionSurveyMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.network.apollo.FetchPolicy
import com.theathletic.network.apollo.httpFetchPolicy

class OnboardingApi @AutoKoin(Scope.SINGLE) constructor(
    private val client: ApolloClient
) {
    suspend fun fetchOnboardingFollowableItems(): OnboardingFollowableItemsQuery.Data? {
        // Without a try-catch block here, I see the following error when there's a network error:
        // CIRCULAR REFERENCE:com.apollographql.apollo3.exception.HttpCacheMissException
        try {
            val result = client
                .query(OnboardingFollowableItemsQuery())
                .httpFetchPolicy(FetchPolicy.CacheFirst)
                .execute()
            if (result.hasErrors()) throw OnboardingApiException(result.errors ?: emptyList())
            return result.data
        } catch (e: Exception) {
            throw OnboardingApiException()
        }
    }

    suspend fun fetchUserAttributionSurveyOptions(): GetUserAttributionSurveyQuery.GetUserAttributionSurvey? {
        val result = client
            .query(GetUserAttributionSurveyQuery())
            .httpFetchPolicy(FetchPolicy.CacheFirst)
            .execute()
        if (result.hasErrors()) throw OnboardingApiException(result.errors ?: emptyList())
        return result.data?.getUserAttributionSurvey
    }

    suspend fun postHasSeenSurvey() {
        client.mutation(MarkUserAttributionSurveyAsSeenMutation()).execute()
    }

    suspend fun postSurveySelection(value: String, displayOrder: Int) {
        client.mutation(SubmitUserAttributionSurveyMutation(value, displayOrder)).execute()
    }
}

class OnboardingApiException(errors: List<Error> = emptyList()) : Exception(
    "Unable to fetch feed ${errors.joinToString(" - ")}"
)