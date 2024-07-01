package com.theathletic.slidestories.ui

import com.theathletic.ui.LoadingState

data class SlideStoriesViewState(
    val loadingState: LoadingState = LoadingState.INITIAL_LOADING,
    val uiModel: SlideStoriesUiModel? = null,
    val slideProgress: List<Float> = emptyList(),
    val currentSlideIndex: Int = 0
)

sealed interface SlideStoriesViewEvent {
    object Close : SlideStoriesViewEvent
}