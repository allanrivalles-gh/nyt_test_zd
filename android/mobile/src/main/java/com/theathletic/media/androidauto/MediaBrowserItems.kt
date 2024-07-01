package com.theathletic.media.androidauto

import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.core.os.bundleOf
import com.theathletic.R
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.entity.main.PodcastItem
import com.theathletic.extension.extGetDrawable
import com.theathletic.extension.extGetString
import com.theathletic.extension.toBitmap

private fun generateMediaKey(type: String, id: Long) = "${type}_$id"

fun PodcastItem.toMediaBrowserSection(section: String): MediaBrowserCompat.MediaItem {
    val item = MediaDescriptionCompat.Builder()
        .setMediaId(generateMediaKey(section, id))
        .setTitle(title)
        .setDescription(description)
        .setIconUri(Uri.parse(imageUrl))
        .build()

    return MediaBrowserCompat.MediaItem(item, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE)
}

fun PodcastEpisodeItem.toMediaBrowserItem(
    section: String,
    isDownloaded: Boolean = false
): MediaBrowserCompat.MediaItem {
    val item = MediaDescriptionCompat.Builder()
        .setMediaId(generateMediaKey(section, podcastId))
        .setTitle(title)
        .setDescription(description)
        .setIconUri(Uri.parse(imageUrl))
        .setMediaUri(Uri.parse(mp3Url))
        .setExtras(
            bundleOf(
                MediaMetadataCompat.METADATA_KEY_DURATION to duration,
                AndroidAuto.Extras.ID to id,
                AndroidAuto.Extras.PODCAST_ID to podcastId,
                AndroidAuto.Extras.DOWNLOADED_SECTION to isDownloaded
            )
        ).build()

    return MediaBrowserCompat.MediaItem(item, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
}

val ANDROID_AUTO_FOLLOWING_ROOT get() = MediaBrowserCompat.MediaItem(
    MediaDescriptionCompat.Builder()
        .setMediaId(AndroidAuto.Section.FOLLOWING_ROOT)
        .setTitle(R.string.podcast_auto_following_title.extGetString())
        .setIconBitmap(R.drawable.ic_athletic_a_logo.extGetDrawable()?.toBitmap())
        .setExtras(
            bundleOf(
                AndroidAuto.ContentStyle.SUPPORTED to true,
                AndroidAuto.ContentStyle.BROWSABLE_HINT to AndroidAuto.ContentStyle.GRID_ITEM_HINT_VALUE
            )
        )
        .build(),
    MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
)

val ANDROID_AUTO_DOWNLOADED_ROOT get() = MediaBrowserCompat.MediaItem(
    MediaDescriptionCompat.Builder()
        .setMediaId(AndroidAuto.Section.DOWNLOADED_ROOT)
        .setTitle(R.string.podcast_auto_downloaded_title.extGetString())
        .setIconBitmap(R.drawable.ic_podcast_download.extGetDrawable()?.toBitmap())
        .build(),
    MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
)