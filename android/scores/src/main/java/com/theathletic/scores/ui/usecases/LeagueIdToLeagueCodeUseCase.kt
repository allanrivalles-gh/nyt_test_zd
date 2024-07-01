package com.theathletic.scores.ui.usecases

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.followable.Followable
import com.theathletic.followable.FollowableId
import com.theathletic.repository.user.FollowableDao
import com.theathletic.scores.data.remote.toGraphqlLeagueCode

class LeagueIdToLeagueCodeUseCase @AutoKoin constructor(
    private val followableDao: FollowableDao,
) {
    suspend operator fun invoke(leagueId: Long) =
        followableDao.getLeague(FollowableId(leagueId.toString(), Followable.Type.LEAGUE))
            ?.league?.toGraphqlLeagueCode?.rawValue.orEmpty()
}