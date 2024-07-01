
package com.theathletic.podcast.browse

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.theathletic.R
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.podcast.analytics.PodcastNavigationSource
import com.theathletic.podcast.ui.PodcastListItem
import com.theathletic.ui.UiModel
import com.theathletic.ui.list.AthleticListFragment
import com.theathletic.ui.list.ListSectionTitleItem
import com.theathletic.utility.ActivityUtility
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class BrowsePodcastFragment :
    AthleticListFragment<BrowsePodcastViewModel, IBrowsePodcastView>(),
    IBrowsePodcastView {

    companion object {
        const val GRID_FULL_WIDTH = 2
        const val GRID_HALF_WIDTH = 1
    }

    private val analytics by inject<Analytics>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarBrand.mainAppbar.background = ColorDrawable(resources.getColor(R.color.ath_grey_70, null))
    }

    override val backgroundColorRes = R.color.ath_grey_70

    override fun setupViewModel() = getViewModel<BrowsePodcastViewModel> {
        parametersOf(activity?.intent?.extras ?: Bundle())
    }

    override fun setupRecyclerView(recyclerView: RecyclerView) {
        super.setupRecyclerView(recyclerView)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), GRID_FULL_WIDTH).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (viewModel.uiModels.value!![position]) {
                        is PodcastListItem -> GRID_HALF_WIDTH
                        else -> GRID_FULL_WIDTH
                    }
                }
            }
        }
    }

    override fun getLayoutForModel(model: UiModel): Int {
        return when (model) {
            is ListSectionTitleItem -> R.layout.list_section_title5
            is PodcastListItem -> R.layout.list_item_podcast_show_subtitled
            else -> throw IllegalArgumentException("${model.javaClass} not supported")
        }
    }

    override fun onPodcastItemClick(podcast: PodcastListItem) {
        analytics.track(
            Event.Podcast.Click(
                view = "podcast_browse",
                element = "discover",
                object_type = "podcast_id",
                object_id = podcast.id.toString()
            )
        )
        ActivityUtility.startPodcastDetailActivity(
            requireContext(),
            podcast.id,
            PodcastNavigationSource.DISCOVER
        )
    }
}