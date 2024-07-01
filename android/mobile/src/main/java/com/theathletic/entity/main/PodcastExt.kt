package com.theathletic.entity.main

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableField
import com.theathletic.R
import com.theathletic.extension.dependantObservableField
import com.theathletic.extension.extGetColor
import com.theathletic.extension.extGetDrawable
import com.theathletic.extension.extGetPlural
import com.theathletic.extension.extGetString
import com.theathletic.manager.PodcastManager
import com.theathletic.podcast.data.LegacyPodcastRepository
import com.theathletic.utility.datetime.DateUtilityImpl
import com.theathletic.utility.formatters.CommentsCountNumberFormat
import java.io.File

val PodcastEpisodeItem.isConnecting: ObservableField<Boolean> get() = dependantObservableField(PodcastManager.activeTrack, PodcastManager.playbackState) {
    PodcastManager.activeTrack.get()?.episodeId == id && PodcastManager.playbackState.get() == PlaybackStateCompat.STATE_CONNECTING
}

val PodcastEpisodeItem.playDrawable: ObservableField<Drawable> get() = dependantObservableField(PodcastManager.activeTrack, PodcastManager.playbackState) {
    val drawable: Drawable? = when {
        PodcastManager.activeTrack.get()?.episodeId == id && PodcastManager.playbackState.get() == PlaybackStateCompat.STATE_CONNECTING -> R.drawable.anim_podcast_play_connecting.extGetDrawable()
        PodcastManager.activeTrack.get()?.episodeId == id && PodcastManager.playbackState.get() == PlaybackStateCompat.STATE_PLAYING -> R.drawable.ic_pause_2_padded.extGetDrawable()
        else -> R.drawable.ic_play_2_padded.extGetDrawable()
    }
    if (finished) {
        drawable?.mutate()?.apply { setTint(R.color.gray.extGetColor()) }
    }
    if (drawable is AnimatedVectorDrawable) drawable.start()
    drawable
}

val PodcastEpisodeItem.downloadMoreOptionsDrawable: ObservableField<Drawable> get() = dependantObservableField(downloadProgress, observableIsFinished) {
    val drawable = when {
        downloadProgress.get() == -1 -> R.drawable.ic_podcast_download_more_options
        downloadProgress.get() != 100 -> R.drawable.ic_podcast_episode_detail_download_stop_more_options
        else -> R.drawable.ic_podcast_episode_detail_downloaded
    }

    if (finished)
        drawable.extGetDrawable()?.mutate()?.apply { setTint(R.color.gray.extGetColor()) }
    else
        drawable.extGetDrawable()
}

fun PodcastEpisodeItem.podcastEpisodeDetailDownloadDrawable(context: Context): ObservableField<Drawable> = dependantObservableField(downloadProgress, observableIsFinished) {
    val drawableRes = when {
        downloadProgress.get() == -1 -> R.drawable.ic_podcast_download_v2
        downloadProgress.get() != 100 -> R.drawable.ic_podcast_episode_detail_download_stop
        else -> R.drawable.ic_podcast_episode_detail_downloaded
    }
    ContextCompat.getDrawable(context, drawableRes)
}

val PodcastEpisodeItem.podcastDetailDownloadDrawable: ObservableField<Drawable> get() = dependantObservableField(downloadProgress, observableIsFinished) {
    val drawable = when {
        downloadProgress.get() == -1 -> R.drawable.ic_podcast_detail_download
        downloadProgress.get() != 100 -> R.drawable.ic_podcast_detail_download_stop
        else -> R.drawable.ic_podcast_detail_downloaded
    }

    if (finished)
        drawable.extGetDrawable()?.mutate()?.apply { setTint(R.color.gray.extGetColor()) }
    else
        drawable.extGetDrawable()
}

val PodcastEpisodeItem.podcastDetailDownloadText: ObservableField<String> get() = dependantObservableField(downloadProgress) {
    when {
        downloadProgress.get() == -1 -> R.string.podcast_item_download.extGetString()
        downloadProgress.get() != 100 -> "${downloadProgress.get()}%"
        else -> R.string.podcast_item_remove_download.extGetString()
    }
}

val PodcastEpisodeItem.podcastDownloadTextGeneral: ObservableField<String> get() = dependantObservableField(downloadProgress) {
    when {
        downloadProgress.get() == -1 -> R.string.podcast_general_download.extGetString()
        downloadProgress.get() != 100 -> R.string.podcast_general_downloading.extGetString()
        else -> R.string.podcast_general_remove_download.extGetString()
    }
}

val PodcastEpisodeItem.formattedDuration: ObservableField<String> get() = dependantObservableField(PodcastManager.activeTrack, PodcastManager.playbackState) {
    when {
        PodcastManager.activeTrack.get()?.episodeId == id && PodcastManager.playbackState.get() == PlaybackStateCompat.STATE_CONNECTING -> R.string.podcast_state_loading.extGetString()
        PodcastManager.activeTrack.get()?.episodeId == id && PodcastManager.playbackState.get() == PlaybackStateCompat.STATE_PLAYING -> R.string.podcast_state_playing.extGetString()
        finished -> R.string.podcast_state_completed.extGetString()
        timeElapsed <= 0 -> DateUtilityImpl.formatPodcastDuration(duration * 1000)
        else -> DateUtilityImpl.formatPodcastTimeRemaining((duration - timeElapsed) * 1000)
    }
}

fun PodcastEpisodeItem.getSortableDate(): Long = DateUtilityImpl.parseDateFromGMT(dateGmt).time

fun PodcastEpisodeItem.getFormattedDate(): String = DateUtilityImpl.formatPodcastDate(dateGmt)

fun PodcastEpisodeItem.getFormattedCommentsNumber(): String {
    return if (numberOfComments == 0)
        R.string.plural_comments_empty.extGetString()
    else
        R.plurals.plural_comments.extGetPlural(numberOfComments, CommentsCountNumberFormat.format(numberOfComments))
}

fun PodcastEpisodeDetailTrackItem.getFormattedDuration() = DateUtilityImpl.formatPodcastTrackDuration(duration * 1_000)

fun PodcastEpisodeDetailTrackItem.getFormattedTimeSpan() = DateUtilityImpl.formatPodcastTrackTimeSpan(startPosition * 1_000, endPosition * 1_000)

fun PodcastEpisodeDetailStoryItem.getHeadingColor(): Int {
    return when (headingType) {
        "mentioned" -> R.color.green.extGetColor()
        else -> R.color.gray.extGetColor()
    }
}

fun PodcastEpisodeDetailStoryItem.getSortableDate(): Long = DateUtilityImpl.parseDateFromGMT(datetimeGmt).time

fun PodcastTrack.getBestSource(): String {
    val file = File(LegacyPodcastRepository.getPodcastLocalFilePath(id))
    return if (file.exists()) file.toURI().toString() else url
}