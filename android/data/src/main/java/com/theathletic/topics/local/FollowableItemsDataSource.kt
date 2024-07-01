package com.theathletic.topics.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.local.InMemoryStaticLocalDataSource

class FollowableItemsDataSource @AutoKoin(Scope.SINGLE) constructor() :
    InMemoryStaticLocalDataSource<Any, FollowableItems>() {

    private object Key

    fun put(data: FollowableItems) {
        put(Key, data)
    }

    fun getItem() = get(Key)
}

data class FollowableItems(
    val teams: List<FollowableTeam>,
    val leagues: List<FollowableLeague>
)

data class FollowableTeam(
    val id: Long,
    val name: String,
    val url: String
)

data class FollowableLeague(
    val id: Long,
    val name: String,
    val url: String
)