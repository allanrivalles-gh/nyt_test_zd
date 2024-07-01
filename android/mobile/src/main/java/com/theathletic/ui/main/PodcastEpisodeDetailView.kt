package com.theathletic.ui.main

import com.theathletic.adapter.main.PodcastDownloadButtonAdapter
import com.theathletic.adapter.main.PodcastPlayButtonController
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.ui.BaseView
import org.alfonz.adapter.AdapterView

interface PodcastEpisodeDetailView : BaseView, AdapterView, PodcastTrackItemView, PodcastStoryItemView, PodcastPlayButtonController.Callback, PodcastDownloadButtonAdapter.Callback {
    fun onShareClick()
    fun onPodcastDownloadClick(item: PodcastEpisodeItem)
    fun onPodcastPlayClick(item: PodcastEpisodeItem)
    fun onCommentsOpenClick(item: PodcastEpisodeItem)
}