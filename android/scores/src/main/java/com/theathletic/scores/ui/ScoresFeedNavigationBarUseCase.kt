package com.theathletic.scores.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.followable.Followable
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.main.ui.NavigationItem
import com.theathletic.main.ui.ScoresNavItem
import com.theathletic.main.ui.SimpleNavItem
import com.theathletic.repository.user.LeagueLocal
import com.theathletic.repository.user.TeamLocal
import com.theathletic.utility.LogoUtility

class ScoresFeedNavigationBarUseCase @AutoKoin constructor(
    private val followableRepository: FollowableRepository,
    private val logoUtility: LogoUtility,
) {
    suspend operator fun invoke(navigationBarItems: List<Followable.Id>): List<NavigationItem> {
        return navigationBarItems.mapNotNull {
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