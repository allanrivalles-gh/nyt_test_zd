package com.theathletic.navigation.data.remote

import com.apollographql.apollo3.ApolloClient
import com.theathletic.TabNavigationQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope

class NavigationApi @AutoKoin(Scope.SINGLE) constructor(
    private val client: ApolloClient
) {
    suspend fun getNavigationEntities() = client.query(TabNavigationQuery()).execute()
}