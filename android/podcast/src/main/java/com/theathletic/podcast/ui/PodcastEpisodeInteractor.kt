package com.theathletic.podcast.ui

class PodcastEpisodeInteractor(
    val onPlayControlClick: () -> Unit = {},
    val onMenuClick: () -> Unit = {},
    val onClick: () -> Unit = {}
)