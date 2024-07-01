package com.theathletic.region.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.theathletic.GetUserContentEditionQuery
import com.theathletic.SetUserContentEditionMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.entity.settings.UserContentEdition
import java.util.Locale
import com.theathletic.type.UserContentEdition as UserContentEditionGraphql

class UserContentEditionApi @AutoKoin(Scope.SINGLE) constructor(
    private val client: ApolloClient
) {
    suspend fun getUserContentEdition(
        userContent: UserContentEdition,
    ): ApolloResponse<GetUserContentEditionQuery.Data> {
        return client.query(
            GetUserContentEditionQuery(fallbackEdition = Optional.present(userContent.toGraphqlUserContentEdition))
        ).execute()
    }

    suspend fun setUserContentEdition(
        userContent: UserContentEdition
    ): ApolloResponse<SetUserContentEditionMutation.Data> {
        return client.mutation(
            SetUserContentEditionMutation(userContent.toGraphqlUserContentEdition)
        ).execute()
    }

    private val UserContentEdition.toGraphqlUserContentEdition: UserContentEditionGraphql
        get() = when (this) {
            UserContentEdition.US -> UserContentEditionGraphql.us
            UserContentEdition.UK -> UserContentEditionGraphql.uk
            else -> Locale.getDefault().toLanguageTag().toUserContentEdition
        }

    private val String.toUserContentEdition: UserContentEditionGraphql
        get() = when (this) {
            UserContentEdition.US.value -> UserContentEditionGraphql.us
            UserContentEdition.UK.value -> UserContentEditionGraphql.uk
            else -> UserContentEditionGraphql.us
        }
}