package com.theathletic.podcast.ui

sealed class PlaybackState {
    object Playing : PlaybackState()
    object Completed : PlaybackState()
    object Loading : PlaybackState()
    object None : PlaybackState()
}

enum class DownloadState {
    NOT_DOWNLOADED,
    DOWNLOADING,
    DOWNLOADED
}