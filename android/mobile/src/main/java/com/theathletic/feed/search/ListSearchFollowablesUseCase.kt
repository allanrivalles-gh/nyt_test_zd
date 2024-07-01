package com.theathletic.feed.search

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.followables.data.UserFollowingRepository
import com.theathletic.followables.data.domain.Filter
import com.theathletic.followables.data.domain.Followable
import com.theathletic.followables.data.domain.filter
import com.theathletic.followables.data.domain.formatName
import com.theathletic.followables.data.domain.getImageUrl
import com.theathletic.utility.LogoUtility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
class ListSearchFollowablesUseCase @AutoKoin constructor(
    private val followableRepository: FollowableRepository,
    private val userFollowingRepository: UserFollowingRepository,
    private val logoUtility: LogoUtility
) {
    operator fun invoke(filter: Filter, showShortName: Boolean = false): Flow<List<SearchFollowableItem>> = combine(
        filter.filterStream.debounce(200),
        userFollowingRepository.userFollowingStream,
        followableRepository.followableStream
    ) { searchFilter, userFollowingList, followableList ->
        val filteredList = searchFilter.applyFilter(userFollowingList, followableList)

        filteredList.map { followable ->
            SearchFollowableItem(
                followableId = followable.id,
                graphqlId = if (followable is Followable.Team) followable.graphqlId else "",
                name = if (showShortName) followable.shortName else formatName(followable, followableList),
                imageUrl = followable.getImageUrl(logoUtility),
                isFollowing = followable in userFollowingList
            )
        }
    }

    private fun Filter.applyFilter(
        followingList: List<Followable> = emptyList(),
        followableList: List<Followable> = emptyList()
    ) = when (this) {
        is Filter.Simple -> followingList.filterType(type).filterIn(followableList).filter(query)
        is Filter.NonFollowing -> followableList.filterType(type).filter(query)
        is Filter.Single -> followableList.filterType(type).filter { it.id == id }
    }

    fun List<Followable>.filterType(type: Filter.Type): List<Followable> {
        return when (type) {
            Filter.Type.ALL -> this
            Filter.Type.TEAM -> filterIsInstance<Followable.Team>()
            Filter.Type.LEAGUE -> filterIsInstance<Followable.League>()
            else -> emptyList()
        }
    }

    private fun List<Followable>.filterIn(list: List<Followable>): List<Followable> {
        val ids = list.map { it.id }
        return this.filter { followable ->
            when (followable) {
                is Followable.Team -> followable.leagueId in ids
                is Followable.League -> followable.id in ids
                else -> false
            }
        }
    }
}