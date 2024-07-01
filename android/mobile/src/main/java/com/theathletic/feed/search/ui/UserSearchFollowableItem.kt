package com.theathletic.feed.search.ui

import androidx.annotation.DrawableRes
import com.theathletic.R
import com.theathletic.feed.search.SearchFollowableItem
import com.theathletic.followable.FollowableId
import com.theathletic.followable.FollowableType
import com.theathletic.ui.UiModel

data class UserSearchFollowableItem(
    val id: FollowableId,
    val name: String,
    val logoUri: String?,
    @DrawableRes val logoPlaceholder: Int? = null,
    val circularLogo: Boolean = false,
    @DrawableRes val selectedIcon: Int?,
    val showDivider: Boolean = true
) : UiModel {
    override val stableId = "$name:$id"

    interface Interactor {
        fun onTopicClicked(followableId: FollowableId)
    }
}

fun SearchFollowableItem.toSearchFollowableItem(
    @DrawableRes selectedIcon: Int? = null,
    showDivider: Boolean = true
) = when (followableId.type) {
    FollowableType.TEAM,
    FollowableType.LEAGUE -> {
        UserSearchFollowableItem(
            id = followableId,
            name = name,
            logoUri = imageUrl,
            selectedIcon = selectedIcon,
            showDivider = showDivider
        )
    }
    FollowableType.AUTHOR -> {
        UserSearchFollowableItem(
            id = followableId,
            name = name,
            logoUri = imageUrl,
            selectedIcon = selectedIcon,
            showDivider = showDivider,
            logoPlaceholder = R.drawable.ic_head_placeholder,
            circularLogo = true,
        )
    }
}