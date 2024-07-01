package com.theathletic.adapter.main

import androidx.databinding.ObservableArrayList
import com.theathletic.R
import com.theathletic.entity.main.PodcastEpisodeDetailBaseItem
import com.theathletic.entity.main.PodcastEpisodeDetailStoryDividerItem
import com.theathletic.entity.main.PodcastEpisodeDetailStoryItem
import com.theathletic.entity.main.PodcastEpisodeDetailTrackItem
import com.theathletic.ui.main.PodcastBigPlayerView
import org.alfonz.adapter.MultiDataBoundRecyclerAdapter

class PodcastBigPlayerAdapter(
    view: PodcastBigPlayerView,
    items: ObservableArrayList<PodcastEpisodeDetailBaseItem>
) : MultiDataBoundRecyclerAdapter(view, items) {
    override fun getItemLayoutId(position: Int): Int {
        return when (getItem(position)) {
            is PodcastEpisodeDetailTrackItem -> R.layout.fragment_podcast_episode_detail_track_item
            is PodcastEpisodeDetailStoryItem -> R.layout.fragment_podcast_episode_detail_story_item
            is PodcastEpisodeDetailStoryDividerItem -> R.layout.fragment_podcast_episode_detail_story_divider_item
            else -> R.layout.fragment_main_item_not_implemented
        }
    }
}