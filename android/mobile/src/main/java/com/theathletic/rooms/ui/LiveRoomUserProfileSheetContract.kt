package com.theathletic.rooms.ui

import com.theathletic.followable.Followable
import com.theathletic.presenter.Interactor
import com.theathletic.ui.ResourceString

interface LiveRoomUserProfileSheetContract {

    interface Presenter :
        Interactor,
        LiveRoomUserProfileUi.Interactor

    data class ViewState(
        val showSpinner: Boolean,
        val name: ResourceString?,
        val initials: ResourceString?,
        val isLocked: Boolean,
        val showStaffControls: Boolean,
        val currentUserFollowedIds: Set<Followable.Id> = emptySet(),
        val followedItems: List<LiveRoomUserProfileUi.FollowedItem> = emptyList(),
    ) : com.theathletic.ui.ViewState
}