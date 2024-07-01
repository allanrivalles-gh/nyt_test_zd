package com.theathletic.slidestories.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.ui.LoadingState
import com.theathletic.ui.updateState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SlideStoriesViewModel @AutoKoin constructor(
    @Assisted private val params: Params,
    private val gestureTranslator: GestureTranslator,
    private val autoPlayManager: AutoPlayManager
) : ViewModel() {

    data class Params(
        val storiesId: String
    )

    private val _viewState = MutableStateFlow(SlideStoriesViewState())
    val viewState = _viewState.asStateFlow()

    private val _viewEvents = MutableSharedFlow<SlideStoriesViewEvent>()
    val viewEvents = _viewEvents.asSharedFlow()

    init {
        // fetch the slide stories
        fetchData()
        observeGestureEvents()
        observeAutoPlayUpdates()
        autoPlayManager.start()
    }

    private fun observeGestureEvents() {
        viewModelScope.launch {
            gestureTranslator.gestureEvents.collect { event ->
                when (event) {
                    GestureTranslator.Event.LEFT_TAP -> onNavigateBack()
                    GestureTranslator.Event.RIGHT_TAP -> onNavigateNext()
                    GestureTranslator.Event.PRESS_TO_PAUSE_START -> autoPlayManager.pause()
                    GestureTranslator.Event.PRESS_TO_PAUSE_END -> autoPlayManager.resume()
                    GestureTranslator.Event.CLOSE -> onClose()
                }
            }
        }
    }

    private fun observeAutoPlayUpdates() {
        viewModelScope.launch {
            autoPlayManager.progressUpdates.collect { progress ->
                val updatedSlideProgress = slideProgress.toMutableList()
                var currentIndex = currentSlideIndex
                updatedSlideProgress[currentIndex] = progress
                if (progress == 1F) {
                    if (updatedSlideProgress.lastIndex < currentIndex + 1) {
                        onClose()
                    } else {
                        currentIndex += 1
                        autoPlayManager.start(getSlideDuration(currentIndex))
                    }
                }
                _viewState.updateState {
                    copy(
                        slideProgress = updatedSlideProgress,
                        currentSlideIndex = currentIndex
                    )
                }
            }
        }
    }

    fun onClose() {
        autoPlayManager.stop()
        viewModelScope.launch { _viewEvents.emit(SlideStoriesViewEvent.Close) }
    }

    fun onGesture(event: RawGestureEvent) {
        gestureTranslator.onGestureEvent(event)
    }

    private fun onNavigateBack() {
        if (currentSlideIndex > 0) {
            val updatedSlideProgress = slideProgress.toMutableList()
            val newIndex = currentSlideIndex - 1
            updatedSlideProgress.onEachIndexed { index, _ -> if (index >= newIndex) updatedSlideProgress[index] = 0f }
            _viewState.updateState {
                copy(
                    slideProgress = updatedSlideProgress,
                    currentSlideIndex = newIndex
                )
            }
            autoPlayManager.start(getSlideDuration(newIndex))
        }
    }

    private fun onNavigateNext() {
        val updatedSlideProgress = slideProgress.toMutableList()
        when {
            currentSlideIndex != slides?.lastIndex -> {
                val newIndex = currentSlideIndex + 1
                updatedSlideProgress[newIndex - 1] = 1f
                _viewState.updateState {
                    copy(
                        slideProgress = updatedSlideProgress,
                        currentSlideIndex = newIndex
                    )
                }
                autoPlayManager.start(getSlideDuration(newIndex))
            }
            currentSlideIndex == slides?.lastIndex -> {
                updatedSlideProgress[currentSlideIndex] = 1f
                _viewState.updateState {
                    copy(slideProgress = updatedSlideProgress)
                }
                onClose()
            }
        }
    }

    private fun getSlideDuration(slideIndex: Int) =
        slides?.getOrNull(slideIndex)?.slideDuration ?: DEFAULT_DELAY_BETWEEN_SLIDES

    private val currentSlideIndex: Int
        get() = _viewState.value.currentSlideIndex

    private val slides: List<SlideStoriesUiModel.Slide>?
        get() = _viewState.value.uiModel?.slides

    private val slideProgress: List<Float>
        get() = _viewState.value.slideProgress

    private fun fetchData() {
        // todo: Adil replace once we have API setup
        val uiModel = SlideStoriesUiModel(
            id = "dummyDataId",
            slides = SlideStoriesTestSlides.slides
        )
        _viewState.updateState {
            copy(
                loadingState = LoadingState.FINISHED,
                uiModel = uiModel,
                slideProgress = MutableList(SlideStoriesTestSlides.slides.size) { 0F },
                currentSlideIndex = 0
            )
        }
    }
}