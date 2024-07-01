package com.theathletic.ui.main

import com.theathletic.adapter.main.PodcastDownloadButtonAdapter
import com.theathletic.adapter.main.PodcastPlayButtonController
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.presenter.Interactor
import com.theathletic.ui.BaseView

interface PodcastDetailView : BaseView, PodcastPlayButtonController.Callback, PodcastDownloadButtonAdapter.Callback, Interactor {
    fun onShareClick()
    fun onFollowClick()
    fun onPodcastEpisodeDownloadClick(item: PodcastEpisodeItem)
    fun onPodcastEpisodeShareClick(item: PodcastEpisodeItem)
    fun onPodcastPlayClick(item: PodcastEpisodeItem)
    fun onPodcastEpisodeItemClick(episodeItem: PodcastEpisodeItem)
}