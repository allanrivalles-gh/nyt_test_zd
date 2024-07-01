package com.theathletic.podcast.ui

import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.theathletic.R
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.extension.extGetColor
import com.theathletic.extension.extGetDrawable
import com.theathletic.podcast.download.PodcastDownloadStateStore
import com.theathletic.podcast.state.PodcastPlayerState
import com.theathletic.podcast.state.PodcastPlayerStateBus
import com.theathletic.ui.UiModel
import com.theathletic.utility.datetime.DateUtilityImpl
import com.theathletic.utility.formatters.CommentsCountNumberFormat
import java.util.concurrent.TimeUnit

class PodcastFeedEpisodeItemPresenter(
    private val podcastPlayerStateBus: PodcastPlayerStateBus,
    private val podcastDownloadStateStore: PodcastDownloadStateStore
) {

    fun transform(
        sectionId: String,
        model: PodcastEpisodeItem,
        showDivider: Boolean
    ): UiModel {

        val playerState = podcastPlayerStateBus.currentState
        val downloadEntity = podcastDownloadStateStore.latestState[model.id]

        val downloadProgress = when {
            downloadEntity != null -> downloadEntity.progress.toInt()
            model.isDownloaded -> PodcastDownloadStateStore.DOWNLOAD_COMPLETE
            else -> PodcastDownloadStateStore.NOT_DOWNLOADED
        }

        return PodcastEpisodeListItem(
            id = model.id,
            sectionId = sectionId,
            title = model.title,
            imageUrl = model.imageUrl ?: "",
            formattedDate = DateUtilityImpl.formatPodcastDate(model.dateGmt),
            duration = model.duration,
            finished = model.finished,

            playDrawable = playDrawable(model, playerState),
            isPlayClickable = !playerState.isConnecting(),

            downloadDrawable = downloadDrawable(downloadProgress),
            downloadTint = model.downloadDrawableTint,
            downloadProgress = downloadProgress,

            formattedDuration = DateUtilityImpl.formatPodcastDurationHHmmss(
                TimeUnit.SECONDS.toMillis(model.duration)
            ),

            formattedCommentCount = CommentsCountNumberFormat.format(model.numberOfComments),
            showCommentCount = model.numberOfComments > 0,

            showDivider = showDivider,
            analyticsInfo = PodcastEpisodeListItem.AnalyticsInfo(model.podcastId)
        )
    }

    private fun playDrawable(
        model: PodcastEpisodeItem,
        playerState: PodcastPlayerState
    ): Drawable? {
        val drawable: Drawable? = when {
            playerState.activeTrack?.id == model.id &&
                playerState.isConnecting() -> R.drawable.anim_podcast_play_connecting

            playerState.activeTrack?.id == model.id &&
                playerState.isPlaying() -> R.drawable.ic_pause_2_padded

            else -> R.drawable.ic_play_2_padded
        }.extGetDrawable()

        if (model.finished) {
            drawable?.mutate()?.apply { setTint(R.color.gray.extGetColor()) }
        }
        (drawable as? AnimatedVectorDrawable)?.start()

        return drawable
    }

    private val PodcastEpisodeItem.downloadDrawableTint @ColorRes get() = if (finished) R.color.ath_grey_45 else R.color.ath_grey_10

    @DrawableRes
    private fun downloadDrawable(
        downloadProgress: Int
    ): Int {
        return when (downloadProgress) {
            PodcastDownloadStateStore.NOT_DOWNLOADED -> R.drawable.ic_podcast_download_v2
            PodcastDownloadStateStore.DOWNLOAD_COMPLETE -> R.drawable.ic_circle_checkmark
            else -> R.drawable.ic_podcast_download_stop_borderless
        }
    }
}