package com.theathletic.main.ui.listen

import com.theathletic.R
import com.theathletic.adapter.main.PodcastDownloadButtonAdapter
import com.theathletic.analytics.data.ClickSource
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Exposes
import com.theathletic.audio.ui.ListenTabViewModel
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.main.ui.MainActivity
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.podcast.download.PodcastDownloadStateStore
import com.theathletic.podcast.ui.PodcastDeleteDialog
import com.theathletic.service.PodcastDownloadService
import com.theathletic.ui.widgets.dialog.menuSheet
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface ListenTabDelegate : PodcastDownloadButtonAdapter.Callback {
    fun showPodcastEpisodeMenu(
        episodeId: String,
        isFinished: Boolean,
        isDownloaded: Boolean,
    )
}

@Exposes(ListenTabDelegate::class)
class ListenTabDelegateImpl @AutoKoin constructor(
    @Assisted private val activity: MainActivity,
    @Assisted private val navigator: ScreenNavigator,
    @Assisted private val presenter: ListenTabViewModel,
    private val analytics: Analytics,
) : ListenTabDelegate {

    private val podcastDownloadButtonAdapter = PodcastDownloadButtonAdapter(this)

    // Podcast Download callback functions
    override fun showPayWall() {
        navigator.startPlansActivity(ClickSource.PODCAST_PAYWALL)
    }

    override fun showNetworkOfflineError() {
        activity.showSnackbar(R.string.global_network_offline)
    }

    override fun showDeleteBottomButtonSheet(item: PodcastEpisodeItem) {
        PodcastDeleteDialog.show(activity) {
            presenter.onDeletePodcastClick(item.id.toString())
        }
    }

    override fun downloadPodcastStart(item: PodcastEpisodeItem) {
        analytics.track(
            Event.Podcast.Download(
                view = "listen",
                element = "following",
                object_id = item.id.toString()
            )
        )
        PodcastDownloadService.downloadFile(activity, item.id, item.title, item.mp3Url)
    }

    override fun downloadPodcastCancel(item: PodcastEpisodeItem) {
        PodcastDownloadService.cancelDownload(activity, item.id)
        item.downloadProgress.set(PodcastDownloadStateStore.NOT_DOWNLOADED)
    }

    override fun showPodcastEpisodeMenu(
        episodeId: String,
        isFinished: Boolean,
        isDownloaded: Boolean,
    ) {
        menuSheet {
            addEntry(
                iconRes = R.drawable.ic_share,
                textRes = R.string.podcast_more_options_button_share,
                onSelected = {
                    presenter.onShareEpisodeClicked(episodeId)
                }
            )

            if (!isFinished) {
                addEntry(
                    iconRes = R.drawable.ic_check_2,
                    textRes = R.string.podcast_mark_as_played,
                    onSelected = { presenter.onMarkPodcastAsPlayedClicked(episodeId) }
                )
            }

            if (isDownloaded) {
                addEntry(
                    iconRes = R.drawable.ic_x,
                    textRes = R.string.podcast_item_remove_download,
                    onSelected = { presenter.onDeletePodcastClick(episodeId) }
                )
            } else {
                addEntry(
                    iconRes = R.drawable.ic_feed_podcast_downloaded,
                    textRes = R.string.podcast_item_download,
                    onSelected = {
                        // Need to use GlobalScope with the fragile way that menuSheet handles
                        // callbacks. Eventually all menuSheet should be replaced with compose
                        // equivalents, then we can get rid of this GlobalScope reference.
                        GlobalScope.launch {
                            podcastDownloadButtonAdapter.onPodcastDownloadClick(episodeId.toLong())
                        }
                    }
                )
            }
        }.show(activity.supportFragmentManager, null)
    }
}