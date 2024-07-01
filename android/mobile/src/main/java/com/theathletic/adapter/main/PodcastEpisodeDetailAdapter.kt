package com.theathletic.adapter.main

import androidx.databinding.ObservableArrayList
import androidx.databinding.ViewDataBinding
import com.theathletic.R
import com.theathletic.databinding.FragmentPodcastEpisodeDetailHeaderItemBinding
import com.theathletic.entity.main.PodcastEpisodeDetailBaseItem
import com.theathletic.entity.main.PodcastEpisodeDetailHeaderItem
import com.theathletic.entity.main.PodcastEpisodeDetailStoryDividerItem
import com.theathletic.entity.main.PodcastEpisodeDetailStoryItem
import com.theathletic.entity.main.PodcastEpisodeDetailTrackItem
import com.theathletic.ui.main.PodcastEpisodeDetailView
import org.alfonz.adapter.BaseDataBoundRecyclerViewHolder
import org.alfonz.adapter.MultiDataBoundRecyclerAdapter

class PodcastEpisodeDetailAdapter(
    view: PodcastEpisodeDetailView,
    items: ObservableArrayList<PodcastEpisodeDetailBaseItem>
) : MultiDataBoundRecyclerAdapter(view, items) {
    override fun getItemLayoutId(position: Int): Int {
        return when (getItem(position)) {
            is PodcastEpisodeDetailHeaderItem -> R.layout.fragment_podcast_episode_detail_header_item
            is PodcastEpisodeDetailTrackItem -> R.layout.fragment_podcast_episode_detail_track_item
            is PodcastEpisodeDetailStoryItem -> R.layout.fragment_podcast_episode_detail_story_item
            is PodcastEpisodeDetailStoryDividerItem -> R.layout.fragment_podcast_episode_detail_story_divider_item
            else -> R.layout.fragment_main_item_not_implemented
        }
    }

    override fun bindItem(
        holder: BaseDataBoundRecyclerViewHolder<ViewDataBinding>,
        position: Int,
        payloads: MutableList<Any?>?
    ) {
        super.bindItem(holder, position, payloads)
        val binding = holder.binding
        val item = getItem(position)
        if (binding is FragmentPodcastEpisodeDetailHeaderItemBinding && item is PodcastEpisodeDetailHeaderItem) {
            binding.descriptionOverlay.setOnClickListener {
                item.showFullDescription = !item.showFullDescription
                notifyItemChanged(position)
            }
        }
    }
}