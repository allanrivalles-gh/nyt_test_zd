package com.theathletic.audio.data.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.local.InMemorySingleLocalDataSource
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.entity.main.PodcastTopicEntryType
import com.theathletic.entity.room.LiveAudioRoomEntity
import com.theathletic.podcast.data.local.PodcastEpisodeEntity
import com.theathletic.podcast.data.local.PodcastSeriesEntity

object ListenFeedData {

    data class WithIds(
        // Following
        val followingLiveRoomIds: List<AthleticEntity.Id> = emptyList(),
        val podcastEpisodeIds: List<AthleticEntity.Id> = emptyList(),
        val followingPodcastIds: List<AthleticEntity.Id> = emptyList(),

        // Discover
        val discoverLiveRoomIds: List<AthleticEntity.Id> = emptyList(),
        val discoverPodcastIds: List<AthleticEntity.Id> = emptyList(),
        val categories: List<Category> = emptyList(),
    ) {
        val allEntityIds get() = followingLiveRoomIds + podcastEpisodeIds + followingPodcastIds +
            discoverLiveRoomIds + discoverPodcastIds
    }

    data class WithEntities(
        // Following
        val followingLiveRooms: List<LiveAudioRoomEntity> = emptyList(),
        val podcastEpisodes: List<PodcastEpisodeEntity> = emptyList(),
        val followingPodcasts: List<PodcastSeriesEntity> = emptyList(),

        // Discover
        val discoverLiveRooms: List<LiveAudioRoomEntity> = emptyList(),
        val discoverPodcasts: List<PodcastSeriesEntity> = emptyList(),
        val categories: List<Category> = emptyList(),
    )

    data class Category(
        val id: String,
        val title: String,
        val iconUrl: String,
        val type: PodcastTopicEntryType,
    )
}

class ListenFeedDataLocalDataSource @AutoKoin(Scope.SINGLE) constructor() :
    InMemorySingleLocalDataSource<ListenFeedData.WithIds>()