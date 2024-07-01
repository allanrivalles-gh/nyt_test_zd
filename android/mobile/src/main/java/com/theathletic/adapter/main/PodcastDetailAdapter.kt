package com.theathletic.adapter.main

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import com.theathletic.R
import com.theathletic.databinding.FragmentPodcastDetailEpisodeItemBinding
import com.theathletic.ui.UiModel
import com.theathletic.ui.list.BindingDiffAdapter
import com.theathletic.ui.list.DataBindingViewHolder
import com.theathletic.ui.main.PodcastDetailView

class PodcastDetailAdapter(
    lifecycleOwner: LifecycleOwner,
    interactor: PodcastDetailView
) : BindingDiffAdapter(lifecycleOwner, interactor) {
    override fun getLayoutForModel(model: UiModel) = R.layout.fragment_podcast_detail_episode_item
    override fun onPostBind(uiModel: UiModel, holder: DataBindingViewHolder<ViewDataBinding>) {
        super.onPostBind(uiModel, holder)
        when (val binding = holder.binding) {
            is FragmentPodcastDetailEpisodeItemBinding -> binding.swipeContainer.reset()
        }
    }

    override fun onViewDetachedFromWindow(holder: DataBindingViewHolder<ViewDataBinding>) {
        super.onViewDetachedFromWindow(holder)
        (holder.binding as? FragmentPodcastDetailEpisodeItemBinding)?.let { binding ->
            binding.textDescription.maxLines = 2
        }
    }
}