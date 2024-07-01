package com.theathletic.rooms.create.ui

import android.view.LayoutInflater
import com.theathletic.databinding.FragmentLiveRoomTaggingBinding
import com.theathletic.databinding.WidgetLiveRoomTagChipBinding

class LiveRoomTaggingViewStateHandler(
    val binding: FragmentLiveRoomTaggingBinding,
    val interactor: LiveRoomTaggingContract.Presenter,
    val layoutInflater: LayoutInflater,
) {
    private var lastState: LiveRoomTaggingContract.ViewState? = null

    fun renderState(viewState: LiveRoomTaggingContract.ViewState) {
        if (lastState?.selectedChipModels != viewState.selectedChipModels) {
            val tags = viewState.selectedChipModels.map { tag ->
                WidgetLiveRoomTagChipBinding.inflate(layoutInflater).apply {
                    data = tag
                    interactor = this@LiveRoomTaggingViewStateHandler.interactor
                }
            }
            binding.selectedTagList.apply {
                removeAllViews()
                tags.forEach { addView(it.root) }
            }
        }

        lastState = viewState
    }
}