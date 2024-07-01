package com.theathletic.followables

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.followables.data.UserFollowingRepository
import com.theathletic.followables.data.domain.Followable
import com.theathletic.followables.data.domain.UserFollowing
import com.theathletic.followables.data.domain.getImageUrl
import com.theathletic.utility.LogoUtility
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class ObserveUserFollowingUseCase @AutoKoin constructor(
    private val followableRepository: FollowableRepository,
    private val userFollowingRepository: UserFollowingRepository,
    private val logoUtility: LogoUtility
) {
    operator fun invoke(): Flow<List<UserFollowing>> {
        var ncaaLeagues = emptyList<Followable.League>()
        return userFollowingRepository.userFollowingStream
            .onStart { ncaaLeagues = followableRepository.getCollegeLeagues() }
            .map { followableList ->
                followableList.map { it.mapToUserFollowing(ncaaLeagues) }
            }
    }

    private fun Followable.mapToUserFollowing(ncaaLeagues: List<Followable.League>): UserFollowing {
        val (formattedName, formattedShortName) = formatNames(ncaaLeagues)

        return UserFollowing(
            id = id,
            name = formattedName,
            shortName = formattedShortName,
            imageUrl = getImageUrl(logoUtility),
            color = color
        )
    }

    private fun Followable.formatNames(ncaaLeagues: List<Followable.League>): Pair<String, String> {
        return if (this is Followable.Team) {
            ncaaLeagues
                .firstOrNull { it.id == leagueId }
                ?.let { league -> Pair("$name (${league.shortName})", league.shortName) }
                ?: Pair(name, shortName)
        } else {
            Pair(name, shortName)
        }
    }
}