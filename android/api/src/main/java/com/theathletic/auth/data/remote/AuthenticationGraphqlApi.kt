package com.theathletic.auth.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.theathletic.CreateAccountMutation
import com.theathletic.WebLoginMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.type.CreateAccountInput
import com.theathletic.type.WebLoginInput

class AuthenticationGraphqlApi @AutoKoin(Scope.SINGLE) constructor(
    private val apolloClient: ApolloClient
) {
    suspend fun authWithEmail(email: String, password: String): ApolloResponse<WebLoginMutation.Data> {
        return apolloClient.mutation(
            WebLoginMutation(WebLoginInput(email = email, password = password))
        ).execute()
    }

    suspend fun createAccount(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        privacy: Boolean,
        tos: Boolean
    ): ApolloResponse<CreateAccountMutation.Data> {
        return apolloClient.mutation(
            CreateAccountMutation(
                CreateAccountInput(
                    email = email,
                    password = password,
                    first_name = firstName,
                    last_name = lastName,
                    privacy = privacy,
                    tos = tos
                )
            )
        ).execute()
    }
}