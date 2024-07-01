package com.theathletic.news

import android.text.format.DateUtils
import androidx.collection.LongSparseArray
import com.theathletic.R
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.formatter.DisplayFormat
import com.theathletic.entity.main.PodcastDownloadEntity
import com.theathletic.feed.ui.models.FeedPodcastEpisodeAnalyticsPayload
import com.theathletic.feed.ui.models.FeedPodcastEpisodeGrouped
import com.theathletic.podcast.data.local.PodcastEpisodeEntity
import com.theathletic.podcast.state.PodcastPlayerState
import com.theathletic.podcast.ui.PodcastStringFormatter
import com.theathletic.ui.binding.ParameterizedString
import com.theathletic.ui.binding.asParameterized
import com.theathletic.utility.PodcastPlayerStateUtility
import com.theathletic.utility.datetime.DateUtilityImpl
import com.theathletic.utility.datetime.DateUtilityImpl.wasInLastWeek
import java.util.Date
import java.util.concurrent.TimeUnit

class FrontpagePodcastRenderers @AutoKoin constructor(
    private val podcastPlayerStateUtility: PodcastPlayerStateUtility,
    private val podcastStringFormatter: PodcastStringFormatter
) {

    @Suppress("LongParameterList")
    fun feedPodcastEpisodeGrouped(
        podcast: PodcastEpisodeEntity,
        playerState: PodcastPlayerState,
        isDownloaded: Boolean,
        downloads: LongSparseArray<PodcastDownloadEntity>,
        moduleIndex: Int,
        hIndex: Int
    ) = FeedPodcastEpisodeGrouped(
        id = podcast.id.toLong(),
        imageUrl = podcast.imageUrl,
        title = podcast.title,
        date = podcast.getFormattedDate(),
        duration = podcast.getFormattedDuration(playerState),
        isDurationTimeRemaining = podcast.timeElapsedMs != 0L || podcast.isActiveTrack(playerState),
        durationSeconds = podcast.duration.toInt(),
        elapsedSeconds = podcast.getElapsedSeconds(playerState).toInt(),
        isFinished = podcast.isFinished && !podcast.isActiveTrack(playerState),
        isDownloaded = isDownloaded,
        isDownloading = downloads[podcast.id.toLong()]?.isDownloading() ?: false,
        downloadProgress = downloads[podcast.id.toLong()]?.progress?.toInt() ?: 0,
        podcastPlayerState = podcastPlayerStateUtility.getPlayerState(podcast, playerState),
        analyticsPayload = FeedPodcastEpisodeAnalyticsPayload(
            moduleIndex = moduleIndex,
            container = "latest_podcasts_curation",
            hIndex = hIndex
        ),
        impressionPayload = ImpressionPayload(
            element = "latest_podcasts_curation",
            container = "latest_podcasts_curation",
            objectType = "podcast_episode_id",
            objectId = podcast.id,
            pageOrder = moduleIndex,
            hIndex = hIndex.toLong()
        )
    )

    private fun PodcastEpisodeEntity.getFormattedDate(): ParameterizedString {
        val publishedDate = Date(publishedAt.timeMillis)
        return when {
            DateUtils.isToday(publishedAt.timeMillis) -> ParameterizedString(R.string.global_date_today)
            publishedDate.wasInLastWeek() -> DateUtilityImpl.formatGMTDate(
                publishedDate,
                DisplayFormat.WEEKDAY_FULL
            ).asParameterized()
            else -> DateUtilityImpl.formatGMTDate(
                publishedDate,
                DisplayFormat.MONTH_DATE_LONG
            ).asParameterized()
        }
    }

    private fun PodcastEpisodeEntity.getFormattedDuration(playerState: PodcastPlayerState): ParameterizedString {
        val timeElapsedSeconds = getElapsedSeconds(playerState)
        val formattedDuration = podcastStringFormatter.formatTinyPlayerDuration(duration - timeElapsedSeconds)

        return if (timeElapsedSeconds > 0 || isActiveTrack(playerState)) {
            ParameterizedString(R.string.podcast_time_left, formattedDuration)
        } else {
            formattedDuration.asParameterized()
        }
    }

    private fun PodcastEpisodeEntity.getElapsedSeconds(playerState: PodcastPlayerState): Long {
        return if (isActiveTrack(playerState)) {
            TimeUnit.MILLISECONDS.toSeconds(playerState.currentProgressMs.toLong())
        } else {
            TimeUnit.MILLISECONDS.toSeconds(timeElapsedMs)
        }
    }

    private fun PodcastEpisodeEntity.isActiveTrack(playerState: PodcastPlayerState): Boolean {
        return playerState.activeTrack?.let { it.id == id.toLongOrNull() } ?: false
    }
}