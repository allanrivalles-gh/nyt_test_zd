package com.theathletic.profile.addfollowing

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.followable.Followable
import com.theathletic.followables.data.domain.FollowableItem
import com.theathletic.profile.ui.FollowableItemUi
import com.theathletic.ui.Transformer

class AddFollowingTransformer @AutoKoin constructor() :
    Transformer<AddFollowingDataState, AddFollowingContract.AddFollowingViewState> {

    override fun transform(data: AddFollowingDataState): AddFollowingContract.AddFollowingViewState {
        with(data) {
            return AddFollowingContract.AddFollowingViewState(
                isLoading = isLoading,
                searchText = searchText,
                addedItems = addedFollowingItems.map { if (loadingIds.contains(it.id)) it.copy(isLoading = true) else it },
                searchableItems = searchableItems.map { it.toUiModel(data.loadingIds, loadingIds) },
            )
        }
    }

    private fun FollowableItem.toUiModel(
        loadingFollowIds: List<Followable.Id>,
        loadingUnfollowIds: List<Followable.Id>
    ) = FollowableItemUi.FollowableItem(
        id = followableId,
        name = name,
        imageUrl = imageUrl,
        isLoading = loadingFollowIds.contains(followableId),
        isFollowing = loadingUnfollowIds.contains(followableId),
        isCircular = followableId.type == Followable.Type.AUTHOR
    )
}