package com.theathletic.followables

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.followable.Followable.Type
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.followables.data.UserFollowingRepository
import com.theathletic.followables.data.domain.Filter
import com.theathletic.followables.data.domain.Followable
import com.theathletic.followables.data.domain.FollowableItem
import com.theathletic.followables.data.domain.filter
import com.theathletic.followables.data.domain.filterNot
import com.theathletic.followables.data.domain.formatName
import com.theathletic.followables.data.domain.getImageUrl
import com.theathletic.utility.LogoUtility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
class ListFollowableUseCase @AutoKoin constructor(
    private val followableRepository: FollowableRepository,
    private val userFollowingRepository: UserFollowingRepository,
    private val logoUtility: LogoUtility
) {
    operator fun invoke(filter: Filter): Flow<List<FollowableItem>> = combine(
        filter.filterStream.debounce(200),
        userFollowingRepository.userFollowingStream,
        followableRepository.followableStream
    ) { followableFilter, followingList, followableList ->
        val followingIds = followingList.map { it.id }.toSet()
        val filteredList = followableFilter.applyFilter(followingList, followableList)

        filteredList.map { followable ->
            val isFollowing = followable.id in followingIds
            val name = formatName(followable, followableList)
            FollowableItem(
                followableId = followable.id,
                name = name,
                imageUrl = followable.getImageUrl(logoUtility),
                isFollowing = isFollowing
            )
        }
    }

    private fun Filter.applyFilter(
        followingList: List<Followable> = emptyList(),
        followableList: List<Followable> = emptyList()
    ) = when (this) {
        is Filter.Simple -> followableList.filterType(type).filter(query)
        is Filter.NonFollowing -> followableList.filterNot(followingList).filterType(type).filter(query)
        is Filter.Single -> followableList.filter { it.id == this.id }
    }

    private fun List<Followable>.filterType(type: Filter.Type): List<Followable> {
        return when (type) {
            Filter.Type.ALL -> this
            Filter.Type.TEAM -> filterIsInstance<Followable.Team>()
            Filter.Type.LEAGUE -> filterIsInstance<Followable.League>()
            Filter.Type.AUTHOR -> filterIsInstance<Followable.Author>()
            Filter.Type.TEAM_AND_LEAGUE -> filter { followable ->
                followable.id.type == Type.TEAM ||
                    followable.id.type == Type.LEAGUE
            }
        }
    }
}