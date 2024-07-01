package com.theathletic.podcast.data.local

import com.theathletic.entity.local.merge.EntityMerger
import kotlin.math.max

object PodcastEpisodeMerger : EntityMerger<PodcastEpisodeEntity>() {
    override fun merge(old: PodcastEpisodeEntity, new: PodcastEpisodeEntity): PodcastEpisodeEntity {
        return new.run {
            copy(
                timeElapsedMs = max(old.timeElapsedMs, new.timeElapsedMs),
                isFinished = old.isFinished || new.isFinished,
            )
        }
    }
}