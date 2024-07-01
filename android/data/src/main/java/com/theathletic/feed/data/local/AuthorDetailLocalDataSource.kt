package com.theathletic.feed.data.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.local.InMemoryLocalDataSource

class AuthorDetailLocalDataSource @AutoKoin(Scope.SINGLE) constructor() :
    InMemoryLocalDataSource<Long, AuthorDetails>()

data class AuthorDetails(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val description: String,
    val twitterHandle: String
)