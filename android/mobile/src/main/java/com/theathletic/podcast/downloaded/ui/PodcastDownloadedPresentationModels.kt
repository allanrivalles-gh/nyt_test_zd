package com.theathletic.podcast.downloaded.ui

import com.theathletic.ui.UiModel

data class PodcastDownloadedSizeItem(
    val downloadedSize: Float
) : UiModel {
    override val stableId = "DOWNLOADED_SIZE"
}

object PodcastEmptyDownloadsItem : UiModel {
    override val stableId = "EMPTY_DOWNLOADS"
}