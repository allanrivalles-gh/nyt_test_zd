package com.theathletic.rooms.create.ui

import androidx.lifecycle.LifecycleOwner
import com.theathletic.R
import com.theathletic.ui.UiModel
import com.theathletic.ui.list.BindingDiffAdapter
import com.theathletic.ui.list.Divider

class LiveRoomTagSearchAdapter(
    lifecycleOwner: LifecycleOwner,
    interactor: LiveRoomTaggingContract.Presenter
) : BindingDiffAdapter(lifecycleOwner, interactor) {
    override fun getLayoutForModel(model: UiModel): Int {
        return when (model) {
            is LiveRoomTagSearchResultUiModel -> R.layout.list_item_live_room_tag_search_result
            is LiveRoomHostSearchResultUiModel -> R.layout.list_item_live_room_host_search_result
            is Divider -> R.layout.list_item_profile_divider
            else -> throw IllegalStateException("LiveRoomTagSearchAdapter doesn't support $model")
        }
    }
}