package com.theathletic.featureintro.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theathletic.analytics.IAnalytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.featureintro.data.local.FeatureIntro
import com.theathletic.links.deep.DeeplinkEventProducer
import com.theathletic.ui.updateState
import com.theathletic.utility.FeatureIntroductionPreferences
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeatureIntroViewModel @AutoKoin constructor(
    createFeatureIntroUseCase: CreateFeatureIntroUseCase,
    featureIntroductionPreferences: FeatureIntroductionPreferences,
    private val deeplinkEventProducer: DeeplinkEventProducer,
    private val analytics: IAnalytics,
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        FeatureIntroViewState(
            uiModel = FeatureIntroUiModel(
                pageCount = 0,
                pages = emptyList()
            ),
            currentPage = 0
        )
    )
    val viewState = _viewState.asStateFlow()

    private val _viewEvents = MutableSharedFlow<FeatureIntroViewEvent>()
    val viewEvents = _viewEvents.asSharedFlow()

    // Set useExamples to true to use example/test intro pages
    private val featureIntroDomain: FeatureIntro = createFeatureIntroUseCase()

    init {
        featureIntroductionPreferences.hasSeenFeatureIntro = true
        _viewState.updateState {
            copy(uiModel = featureIntroDomain.toUiModel())
        }
    }

    fun onPageChanged(page: Int) {
        _viewState.updateState { copy(currentPage = page) }
        analytics.track(
            Event.FeatureIntro.View(featureIntroDomain.getAnalyticsView(page))
        )
    }

    fun onNextAction() {
        val currentPage = _viewState.value.currentPage
        if (isOnLastPage(currentPage)) {
            viewModelScope.launch {
                featureIntroDomain.destinationUrl?.let { deeplinkEventProducer.emit(it) }
                trackCloseFeatureIntro("ok")
                _viewEvents.emit(FeatureIntroViewEvent.CloseScreen)
            }
        } else {
            val targetPage = currentPage + 1
            _viewState.updateState { copy(currentPage = targetPage) }
        }
    }

    fun onClose() {
        viewModelScope.launch {
            trackCloseFeatureIntro("dismiss")
            _viewEvents.emit(FeatureIntroViewEvent.CloseScreen)
        }
    }

    private fun isOnLastPage(currentPage: Int) = currentPage == featureIntroDomain.pageCount - 1

    private fun trackCloseFeatureIntro(element: String) {
        val currentPage = _viewState.value.currentPage
        analytics.track(
            Event.FeatureIntro.Click(
                view = featureIntroDomain.getAnalyticsView(currentPage),
                element = element
            )
        )
    }
}

data class FeatureIntroViewState(
    val uiModel: FeatureIntroUiModel,
    val currentPage: Int = 0
)

sealed interface FeatureIntroViewEvent {
    object CloseScreen : FeatureIntroViewEvent
}

private fun FeatureIntro.toUiModel() = FeatureIntroUiModel(
    pageCount = pageCount,
    pages = pages.map {
        FeatureIntroUiModel.Page(
            title = it.title,
            description = it.description,
            image = it.image,
            buttonLabel = it.buttonLabel
        )
    }
)