package com.theathletic.ui.main

import com.theathletic.entity.main.PodcastEpisodeDetailTrackItem
import org.alfonz.adapter.AdapterView

interface PodcastTrackItemView : AdapterView {
    fun onTrackItemClick(track: PodcastEpisodeDetailTrackItem)
}