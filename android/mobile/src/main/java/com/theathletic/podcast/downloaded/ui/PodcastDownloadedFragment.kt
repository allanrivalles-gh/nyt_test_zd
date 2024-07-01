
package com.theathletic.podcast.downloaded.ui

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.theathletic.R
import com.theathletic.adapter.main.PodcastDownloadButtonAdapter
import com.theathletic.adapter.main.PodcastPlayButtonController
import com.theathletic.analytics.AnalyticsPayload
import com.theathletic.analytics.data.ClickSource
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.podcast.analytics.PodcastNavigationSource
import com.theathletic.podcast.download.PodcastDownloadStateStore
import com.theathletic.podcast.ui.IPodcastEpisodeItemView
import com.theathletic.podcast.ui.PodcastDeleteDialog
import com.theathletic.podcast.ui.PodcastEpisodeListItem
import com.theathletic.service.PodcastDownloadService
import com.theathletic.ui.UiModel
import com.theathletic.ui.list.AthleticListFragment
import com.theathletic.utility.ActivityUtility
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel

class PodcastDownloadedFragment :
    AthleticListFragment<PodcastDownloadedViewModel, IPodcastDownloadedView>(),
    IPodcastDownloadedView,
    IPodcastEpisodeItemView,
    PodcastPlayButtonController.Callback,
    PodcastDownloadButtonAdapter.Callback {

    private val analytics by inject<Analytics>()

    var clearMenuItem: MenuItem? = null

    private val podcastDownloadButtonAdapter = PodcastDownloadButtonAdapter(this)
    private val podcastPlayClickController by inject<PodcastPlayButtonController>()

    override val backgroundColorRes = R.color.ath_grey_65

    override fun setupViewModel() = getViewModel<PodcastDownloadedViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbarBrand.mainAppbar.background = ColorDrawable(resources.getColor(R.color.ath_grey_70, null))
        viewModel.podcastCount.observe(
            viewLifecycleOwner,
            Observer { episodes -> clearMenuItem?.isVisible = episodes > 0 }
        )
    }

    override fun getLayoutForModel(model: UiModel): Int {
        return when (model) {
            is PodcastEpisodeListItem -> R.layout.list_item_podcast_episode
            is PodcastDownloadedSizeItem -> R.layout.list_item_downloaded_podcast_size
            PodcastEmptyDownloadsItem -> R.layout.empty_podcast_downloads
            else -> throw IllegalArgumentException("${model.javaClass} not supported")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_podcasts_downloaded, menu)
        clearMenuItem = menu.findItem(R.id.action_clear).apply {
            isVisible = (viewModel.podcastCount.value ?: 0) > 0
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear -> {
                showClearDialog()
                true
            }
            else -> false
        }
    }

    private fun showClearDialog() {
        AlertDialog.Builder(requireContext(), R.style.Theme_Athletic_Dialog_Dark)
            .setTitle(R.string.podcast_clear)
            .setMessage(R.string.podcast_clear_confirm_description)
            .setCancelable(true)
            .setNegativeButton(R.string.global_action_cancel) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.podcast_clear) { _, _ ->
                viewModel.clearDownloadedPodcasts()
            }
            .create()
            .show()
    }

    override fun onPodcastEpisodeItemClick(item: PodcastEpisodeListItem) {
        ActivityUtility.startPodcastEpisodeDetailActivity(
            requireContext(),
            item.id,
            PodcastNavigationSource.DOWNLOADED
        )
    }

    override fun onPodcastDownloadClick(item: PodcastEpisodeListItem) {
        lifecycleScope.launch {
            podcastDownloadButtonAdapter.onPodcastDownloadClick(item.id)
        }
    }

    override fun onPodcastPlayClick(item: PodcastEpisodeListItem) {
        lifecycleScope.launch {
            podcastPlayClickController.onPodcastPlayClick(
                episodeId = item.id,
                callback = this@PodcastDownloadedFragment
            )
        }
    }

    override fun showDeleteBottomButtonSheet(item: PodcastEpisodeItem) {
        PodcastDeleteDialog.show(requireActivity()) {
            viewModel.onDeletePodcastClick(item)
        }
    }

    override fun downloadPodcastStart(item: PodcastEpisodeItem) {
        PodcastDownloadService.downloadFile(requireActivity(), item.id, item.title, item.mp3Url)
    }

    override fun downloadPodcastCancel(item: PodcastEpisodeItem) {
        PodcastDownloadService.cancelDownload(requireActivity(), item.id)
        item.downloadProgress.set(PodcastDownloadStateStore.NOT_DOWNLOADED)
    }

    override fun firePlayAnalyticsEvent(podcastEpisodeId: Long, payload: AnalyticsPayload?) {
        analytics.track(
            Event.Podcast.Play(
                view = "podcast_downloads",
                element = "downloads",
                object_id = podcastEpisodeId.toString()
            )
        )
    }

    override fun firePauseAnalyticsEvent(podcastEpisodeId: Long, payload: AnalyticsPayload?) {
        analytics.track(
            Event.Podcast.Pause(
                view = "podcast_downloads",
                element = "downloads",
                object_id = podcastEpisodeId.toString()
            )
        )
    }

    override fun showPayWall() {
        ActivityUtility.startPlansActivity(
            requireContext(),
            ClickSource.PODCAST_PAYWALL
        )
    }

    override fun showNetworkOfflineError() = showSnackbar(R.string.global_network_offline)
}