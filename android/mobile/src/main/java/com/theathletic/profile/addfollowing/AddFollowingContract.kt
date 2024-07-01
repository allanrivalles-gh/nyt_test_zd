package com.theathletic.profile.addfollowing

import com.theathletic.profile.ui.AddFollowingUi
import com.theathletic.profile.ui.FollowableItemUi
import com.theathletic.ui.ViewState

interface AddFollowingContract {

    interface Interactor : AddFollowingUi.Interactor

    data class AddFollowingViewState(
        val isLoading: Boolean,
        val searchText: String,
        val addedItems: List<FollowableItemUi.FollowableItem>,
        val searchableItems: List<FollowableItemUi.FollowableItem>
    ) : ViewState
}