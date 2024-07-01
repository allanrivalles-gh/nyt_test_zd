package com.theathletic.scores.ui

import com.theathletic.followable.Followable
import com.theathletic.followable.FollowableId
import com.theathletic.main.ui.SimpleNavItem
import com.theathletic.scores.R

object ScorePreviewData {

    fun scoreHeaderLeagueData() = listOf(
        SimpleNavItem(
            id = FollowableId("1", Followable.Type.LEAGUE),
            title = "NFL",
            imageUrl = "",
            placeholder = R.drawable.ic_team_logo_placeholder,
        ),
        SimpleNavItem(
            id = FollowableId("2", Followable.Type.LEAGUE),
            title = "NBA",
            imageUrl = "",
            placeholder = R.drawable.ic_team_logo_placeholder,
        ),
        SimpleNavItem(
            id = FollowableId("3", Followable.Type.LEAGUE),
            title = "EFL",
            imageUrl = "",
            placeholder = R.drawable.ic_team_logo_placeholder,
        ),
        SimpleNavItem(
            id = FollowableId("4", Followable.Type.LEAGUE),
            title = "NHL",
            imageUrl = "",
            placeholder = R.drawable.ic_team_logo_placeholder,
        ),
        SimpleNavItem(
            id = FollowableId("5", Followable.Type.LEAGUE),
            title = "WNBA",
            imageUrl = "",
            placeholder = R.drawable.ic_team_logo_placeholder,
        ),
        SimpleNavItem(
            id = FollowableId("6", Followable.Type.LEAGUE),
            title = "MLB",
            imageUrl = "",
            placeholder = R.drawable.ic_team_logo_placeholder,
        ),
        SimpleNavItem(
            id = FollowableId("6", Followable.Type.LEAGUE),
            title = "MLS",
            imageUrl = "",
            placeholder = R.drawable.ic_team_logo_placeholder,
        ),
    )
}