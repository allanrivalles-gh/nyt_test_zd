package com.theathletic.podcast

import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.podcast.state.PodcastPlayerState

interface PodcastEpisodeUpdater {
    fun List<PodcastEpisodeItem>.updateState(playerState: PodcastPlayerState) {
        this.filter { it.id == playerState.activeTrack?.id }
            .forEach {
                it.timeElapsed = (playerState.currentProgressMs / 1000f).toInt()
                // TODO (matt) Add when PodcastPlayerStateBus supports finished
                // it.finished = event.finished
            }
    }
}