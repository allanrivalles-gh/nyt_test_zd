package com.theathletic.followables.data.domain

import com.theathletic.followable.FollowableId
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class Filter {
    abstract val query: String
    abstract val type: Type

    data class Simple(override val query: String = "", override val type: Type = Type.ALL) : Filter()
    data class NonFollowing(override val query: String = "", override val type: Type = Type.ALL) : Filter()
    data class Single(val id: FollowableId, override val query: String = "", override val type: Type = Type.ALL) : Filter()

    private val _filterStream = MutableSharedFlow<Filter>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
        .apply { tryEmit(this@Filter) }
    val filterStream = _filterStream.asSharedFlow()

    fun update(filter: (current: Filter) -> Filter) {
        _filterStream.tryEmit(
            filter(_filterStream.replayCache.first())
        )
    }

    enum class Type {
        ALL,
        TEAM,
        LEAGUE,
        AUTHOR,
        TEAM_AND_LEAGUE
    }
}