package com.theathletic.rooms.ui

import androidx.lifecycle.LifecycleOwner
import com.theathletic.R
import com.theathletic.ui.UiModel
import com.theathletic.ui.list.BindingDiffAdapter

class LiveRoomHostControlsAdapter(
    lifecycleOwner: LifecycleOwner,
    val interactor: LiveRoomHostControlsContract.Presenter
) : BindingDiffAdapter(lifecycleOwner, interactor) {

    override fun getLayoutForModel(model: UiModel): Int {
        return when (model) {
            is LiveRoomControlsOnStageHost -> R.layout.list_item_host_controls_host_on_stage
            is LiveRoomControlsOnStageUser -> R.layout.list_item_host_controls_user_on_stage
            is LiveRoomControlsOnStageTitle -> R.layout.list_item_host_controls_on_stage_title
            is LiveRoomControlsRequestsTitle -> R.layout.list_item_host_controls_requests_title
            is LiveRoomControlsSpeakingRequest -> R.layout.list_item_host_controls_audience_request
            else -> throw IllegalStateException("LiveRoomHostControlsAdapter doesn't support $model")
        }
    }
}