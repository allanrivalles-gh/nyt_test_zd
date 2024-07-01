package com.theathletic.audio.data.remote

import com.theathletic.ListenFeedDataQuery
import com.theathletic.audio.data.local.ListenFeedData
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.entity.main.PodcastTopicEntryType
import com.theathletic.feed.data.remote.toEntity
import com.theathletic.fragment.Podcast
import com.theathletic.podcast.data.local.PodcastSeriesEntity
import com.theathletic.rooms.remote.toEntity

fun ListenFeedDataQuery.PodcastFeed.toLocalModel(): ListenFeedData.WithIds {
    return ListenFeedData.WithIds(
        // Following
        followingLiveRoomIds = user_live_rooms.mapNotNull {
            it?.fragments?.liveRoomFragment?.id?.let { roomId ->
                AthleticEntity.Id(roomId, AthleticEntity.Type.LIVE_AUDIO_ROOM)
            }
        },
        podcastEpisodeIds = user_podcast_episodes?.mapNotNull {
            it?.fragments?.podcastEpisode?.id?.let { episodeId ->
                AthleticEntity.Id(episodeId, AthleticEntity.Type.PODCAST_EPISODE)
            }
        }.orEmpty(),
        followingPodcastIds = user_podcasts.mapNotNull { it?.fragments?.podcast }.map {
            AthleticEntity.Id(it.id, AthleticEntity.Type.PODCAST_SERIES)
        },

        // Discover
        discoverLiveRoomIds = discover_live_rooms.mapNotNull {
            it?.fragments?.liveRoomFragment?.id?.let { roomId ->
                AthleticEntity.Id(roomId, AthleticEntity.Type.LIVE_AUDIO_ROOM)
            }
        },
        discoverPodcastIds = recommended_podcasts?.mapNotNull {
            it?.fragments?.podcast?.id?.let { podcastId ->
                AthleticEntity.Id(podcastId, AthleticEntity.Type.PODCAST_SERIES)
            }
        }.orEmpty(),
        categories = discover?.mapNotNull { it }?.mapNotNull { channel ->
            val rawId = channel.id.split("-").firstOrNull() ?: return@mapNotNull null
            ListenFeedData.Category(
                id = rawId,
                title = channel.name.orEmpty(),
                iconUrl = channel.image_url.orEmpty(),
                type = channel.type?.let { PodcastTopicEntryType.from(it) } ?: PodcastTopicEntryType.UNKNOWN
            )
        }.orEmpty(),
    )
}

fun ListenFeedDataQuery.PodcastFeed.getAllEntities(): List<AthleticEntity> {
    val entities = mutableListOf<AthleticEntity>()

    entities.apply {
        addAll(user_live_rooms.mapNotNull { it?.fragments?.liveRoomFragment?.toEntity() })
        addAll(
            user_podcast_episodes?.mapNotNull { it?.fragments?.podcastEpisode?.toEntity() }.orEmpty()
        )
        addAll(discover_live_rooms.mapNotNull { it?.fragments?.liveRoomFragment?.toEntity() })

        addAll(user_podcasts.mapNotNull { it?.fragments?.podcast?.toEntity(isFollowing = true) })
        addAll(
            recommended_podcasts?.mapNotNull {
                it?.fragments?.podcast?.toEntity(isFollowing = false)
            }.orEmpty()
        )
    }

    return entities
}

private fun Podcast.toEntity(
    isFollowing: Boolean
): PodcastSeriesEntity = PodcastSeriesEntity(
    id = id,
    title = title,
    subtitle = description,
    imageUrl = image_url.orEmpty(),
    category = metadata_string.orEmpty(),
    isFollowing = isFollowing,
    notifyEpisodes = notif_episodes_on ?: false,
)