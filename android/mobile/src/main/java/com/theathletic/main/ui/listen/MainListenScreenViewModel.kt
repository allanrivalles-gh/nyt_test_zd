package com.theathletic.main.ui.listen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.podcast.data.PodcastRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainListenScreenViewModel @AutoKoin constructor(
    eventConsumer: ListenTabEventConsumer,
    private val analytics: Analytics,
    private val podcastRepository: PodcastRepository,
) : ViewModel() {

    private val _viewState: MutableStateFlow<ListenScreenViewState> =
        MutableStateFlow(ListenScreenViewState(CurrentlySelectedTab.FOLLOWING_TAB))
    val viewState = _viewState.asStateFlow()

    init {
        eventConsumer.observe<ListenTabEvent.SwitchToDiscoverTab>(viewModelScope) {
            _viewState.emit(ListenScreenViewState(CurrentlySelectedTab.DISCOVER_TAB))
        }

        viewModelScope.launch {
            // Default to Discover tab if there are no followed podcasts
            if (podcastRepository.followedPodcasts.first().isEmpty()) {
                _viewState.emit(ListenScreenViewState(CurrentlySelectedTab.DISCOVER_TAB))
            }
        }
    }

    fun onInitialSelectedTab(initialSelectedTabIndex: Int?) {
        initialSelectedTabIndex?.let { initialTab ->
            viewModelScope.launch {
                val tab = CurrentlySelectedTab.fromPosition(initialTab) ?: CurrentlySelectedTab.FOLLOWING_TAB
                _viewState.emit(ListenScreenViewState(tab))
            }
        }
    }

    fun trackTabView(position: Int) {
        CurrentlySelectedTab.fromPosition(position)?.let {
            analytics.track(
                Event.Listen.View(
                    element = it.analyticsObjectType
                )
            )
        }
    }

    fun trackItemClicked(position: Int) {
        CurrentlySelectedTab.fromPosition(position)?.let {
            analytics.track(
                Event.Podcast.Click(
                    view = "listen",
                    element = "feed_navigation",
                    object_type = it.analyticsObjectType
                )
            )
        }
    }
}

enum class CurrentlySelectedTab(val position: Int, val analyticsObjectType: String) {
    FOLLOWING_TAB(0, "following"),
    DISCOVER_TAB(1, "discover");

    companion object {
        infix fun fromPosition(position: Int): CurrentlySelectedTab? = CurrentlySelectedTab.values().firstOrNull {
            it.position == position
        }
    }
}

data class ListenScreenViewState(
    val currentlySelectedTab: CurrentlySelectedTab
)