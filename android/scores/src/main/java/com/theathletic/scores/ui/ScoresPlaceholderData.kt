package com.theathletic.scores.ui

import com.theathletic.followable.Followable
import com.theathletic.followable.FollowableId
import com.theathletic.main.ui.SimpleNavItem

object ScoresPlaceholderData {

    val placeholderNavigationItems = List(10) {
        SimpleNavItem(
            id = FollowableId(id = "", Followable.Type.TEAM),
            title = "placeholder",
            imageUrl = "   ",
        )
    }
}