package com.theathletic.ui.main

import com.theathletic.entity.main.PodcastEpisodeDetailStoryItem
import org.alfonz.adapter.AdapterView

interface PodcastStoryItemView : AdapterView {
    fun onStoryItemClick(item: PodcastEpisodeDetailStoryItem)
}