package com.theathletic.audio.ui

import com.theathletic.feed.ui.FeedUiV2
import com.theathletic.presenter.Interactor

interface ListenTabContract {
    enum class TabType {
        FOLLOWING,
        DISCOVER,
    }

    interface Presenter : Interactor

    data class ViewState(
        val showSpinner: Boolean,
        val feedUiModel: FeedUiV2,
    ) : com.theathletic.ui.ViewState

    sealed class Event : com.theathletic.utility.Event() {
        data class ShowPodcastEpisodeMenu(
            val episodeId: String,
            val isFinished: Boolean,
            val isDownloaded: Boolean,
        ) : Event()
        object ScrollToTopOfFeed : Event()
    }
}