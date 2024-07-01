package com.theathletic.liveblog.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theathletic.ads.AdAnalytics
import com.theathletic.ads.bridge.data.local.AdEvent
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.links.deep.DeeplinkEventProducer
import com.theathletic.liveblog.data.LiveBlogLinksRepository
import com.theathletic.ui.EventEmittingViewModel
import com.theathletic.ui.updateState
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LiveBlogWebViewViewModel @AutoKoin constructor(
    @Assisted val params: Params,
    private val liveBlogLinksRepository: LiveBlogLinksRepository,
    private val deeplinkEventProducer: DeeplinkEventProducer,
    private val adConfigUseCase: LiveBlogAdConfigUseCase,
    private val adAnalytics: AdAnalytics
) : ViewModel(), EventEmittingViewModel {
    data class Params(
        val liveBlogId: String,
        val initialPostId: String?,
        val screenWidth: Int,
        val screenHeight: Int
    )

    private val eventBus = MutableSharedFlow<Event>()
    private val _viewState = MutableStateFlow(LiveBlogWebViewUiState(initialPostId = params.initialPostId))
    val viewState: StateFlow<LiveBlogWebViewUiState>
        get() = _viewState
    override val eventConsumer: Flow<Event> = eventBus

    private val pageViewId: String = UUID.randomUUID().toString()

    init {
        viewModelScope.launch {
            initializeAdConfig()
            liveBlogLinksRepository.getLiveBlogLinks(params.liveBlogId)
                .collect { liveBlogLinks ->
                    if (liveBlogLinks == null) {
                        loadData()
                    } else {
                        _viewState.updateState { copy(liveBlogLinks = liveBlogLinks) }
                    }
                }
        }
    }

    fun handleLink(uri: Uri) {
        viewModelScope
            .launch {
                deeplinkEventProducer.emit(uri.toString())
            }
    }

    fun retry() {
        _viewState.updateState { copy(hasError = false) }
        viewModelScope.launch { loadData() }
    }

    fun onBackClick() {
        viewModelScope.launch { eventBus.emit(Event.OnBackClick) }
    }

    fun onShareClick() {
        viewState.value.liveBlogLinks?.let {
            viewModelScope.launch { eventBus.emit(Event.OnShareClick(it.permalink)) }
        }
    }

    fun onWebViewReloaded() {
        _viewState.updateState { copy(webViewLoaded = false) }
    }

    fun onWebViewContentLoaded() {
        _viewState.updateState { copy(webViewLoaded = true, initialPostId = null) }
    }

    fun handleAdEvents(eventFlow: StateFlow<AdEvent>) {
        viewModelScope.launch {
            eventFlow.collect {
                adAnalytics.trackAdEvent(pageViewId, "blog", it)
            }
        }
    }

    private suspend fun loadData() {
        try {
            liveBlogLinksRepository.fetchLiveBlogLinks(params.liveBlogId)
        } catch (error: Throwable) {
            _viewState.updateState {
                if (liveBlogLinks == null) {
                    copy(hasError = true)
                } else {
                    this
                }
            }
        }
    }

    private suspend fun initializeAdConfig() {
        val adConfigJson = adConfigUseCase.invoke(pageViewId, params.screenWidth, params.screenHeight)
        _viewState.updateState {
            copy(adConfig = adConfigJson)
        }
    }
}