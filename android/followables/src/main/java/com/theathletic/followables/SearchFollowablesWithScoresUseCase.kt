package com.theathletic.followables

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.followables.data.UserFollowingRepository
import com.theathletic.followables.data.domain.Filter
import com.theathletic.followables.data.domain.Followable
import com.theathletic.followables.data.domain.FollowableSearchItem
import com.theathletic.followables.data.domain.filter
import com.theathletic.followables.data.domain.formatName
import com.theathletic.followables.data.domain.getImageUrl
import com.theathletic.utility.LogoUtility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
class SearchFollowablesWithScoresUseCase @AutoKoin constructor(
    private val followableRepository: FollowableRepository,
    private val userFollowingRepository: UserFollowingRepository,
    private val logoUtility: LogoUtility
) {
    operator fun invoke(filter: Filter): Flow<List<FollowableSearchItem>> = combine(
        filter.filterStream.debounce(200),
        userFollowingRepository.userFollowingStream,
        followableRepository.scoresFollowableStream
    ) { searchFilter, userFollowingList, followableScoresList ->
        val filteredList = searchFilter.applyFilter(userFollowingList, followableScoresList)

        filteredList.map { followable ->
            val isFollowed = followable in userFollowingList
            val name = formatName(followable, followableScoresList)
            FollowableSearchItem(
                followableId = followable.id,
                graphqlId = if (followable is Followable.Team) followable.graphqlId else "",
                name = name,
                imageUrl = followable.getImageUrl(logoUtility),
                isFollowing = isFollowed
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