package com.theathletic.network.apollo.utility

import com.apollographql.apollo3.api.Error

fun List<Error>?.toMessage() =
    this?.joinToString(separator = "\n") { error -> error.message }.orEmpty()