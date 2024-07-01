package com.theathletic.network.apollo

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Subscription

fun <D : Subscription.Data> ApolloClient.notPersistedSubscription(subscription: Subscription<D>): ApolloCall<D> {
    return subscription(subscription).enableAutoPersistedQueries(false)
}