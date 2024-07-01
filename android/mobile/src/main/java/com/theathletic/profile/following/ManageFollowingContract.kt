package com.theathletic.profile.following

import com.theathletic.profile.ui.FollowableItemUi
import com.theathletic.profile.ui.ManageFollowingUi
import com.theathletic.profile.ui.ViewMode
import com.theathletic.ui.ViewState

interface ManageFollowingContract {

    interface Interactor : ManageFollowingUi.Interactor

    data class FollowingViewState(
        val followingItems: List<FollowableItemUi.FollowableItem> = emptyList(),
        val viewMode: ViewMode
    ) : ViewState
}