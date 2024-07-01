package com.theathletic.scores.ui.usecases

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.main.ui.NavigationItem
import com.theathletic.main.ui.ScoresNavItem
import com.theathletic.main.ui.SimpleNavItem
import com.theathletic.repository.user.LeagueLocal
import com.theathletic.repository.user.TeamLocal
import com.theathletic.scores.data.ScoresFeedRepository
import com.theathletic.utility.LogoUtility
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class ObserveFeedNavigationItemsUseCase @AutoKoin constructor(
    private val scoresFeedRepository: ScoresFeedRepository,
    private val followableRepository: FollowableRepository,
    private val logoUtility: LogoUtility,
) {
    suspend operator fun invoke(currentFeedId: String): Flow<List<NavigationItem>> {
        return scoresFeedRepository.getScoresFeed(currentFeedId).mapNotNull { feed ->
            feed?.navigationBar?.mapNotNull {
                followableRepository.getFollowable(it)?.let { followable ->
                    val imageUrl = when (followable) {
                        is LeagueLocal -> logoUtility.getColoredLeagueLogoPath(followable.id.id.toLongOrNull())
                        is TeamLocal -> logoUtility.getTeamLogoPath(followable.id.id.toLongOrNull())
                        else -> ""
                    }
                    ScoresNavItem(
                        SimpleNavItem(
                            id = followable.id,
                            title = followable.shortName,
                            imageUrl = imageUrl,
                        )
                    )
                }
            }
        }
    }
}