package com.theathletic.audio.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.audio.data.local.CurrentLiveRoomsData
import com.theathletic.audio.data.local.CurrentLiveRoomsLocalDataSource
import com.theathletic.audio.data.local.ListenFeedData
import com.theathletic.audio.data.local.ListenFeedDataLocalDataSource
import com.theathletic.audio.data.remote.CurrentLiveRoomsFetcher
import com.theathletic.audio.data.remote.ListenFeedDataFetcher
import com.theathletic.data.EmptyParams
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.entity.local.filterTypes
import com.theathletic.entity.room.LiveAudioRoomEntity
import com.theathletic.podcast.data.local.PodcastEpisodeEntity
import com.theathletic.podcast.data.local.PodcastSeriesEntity
import com.theathletic.repository.CoroutineRepository
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ListenFeedRepository @AutoKoin(Scope.SINGLE) constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val entityDataSource: EntityDataSource,
    private val currentLiveRoomsFetcher: CurrentLiveRoomsFetcher,
    private val currentLiveRoomsDataSource: CurrentLiveRoomsLocalDataSource,
    private val listenFeedDataFetcher: ListenFeedDataFetcher,
    private val listenFeedDataSource: ListenFeedDataLocalDataSource,
) : CoroutineRepository {

    override val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    val listenFeed: Flow<ListenFeedData.WithEntities?>
        get() {
            // This creates a flow that will update anytime our ListenFeedDataLocalDataSource gets a new
            // value or whenever any of the live room, podcast, or podcast episode entities update.
            val flow = combine(
                listenFeedDataSource.item,
                entityDataSource.updateFlow.filterTypes(
                    AthleticEntity.Type.LIVE_AUDIO_ROOM,
                    AthleticEntity.Type.PODCAST_EPISODE,
                    AthleticEntity.Type.PODCAST_SERIES,
                ).onStart { emit(emptySet()) }
            ) { feed, _ -> feed }

            return flow.map { lite ->
                lite ?: return@map null

                val allEntities = entityDataSource.getEntities(
                    lite.allEntityIds
                ).associateBy { it.entityId }

                ListenFeedData.WithEntities(
                    followingLiveRooms = allEntities.find<LiveAudioRoomEntity>(lite.followingLiveRoomIds),
                    podcastEpisodes = allEntities.find<PodcastEpisodeEntity>(lite.podcastEpisodeIds),
                    followingPodcasts = allEntities.find<PodcastSeriesEntity>(lite.followingPodcastIds),

                    discoverLiveRooms = allEntities.find<LiveAudioRoomEntity>(lite.discoverLiveRoomIds),
                    discoverPodcasts = allEntities.find<PodcastSeriesEntity>(lite.discoverPodcastIds),
                    categories = lite.categories,
                )
            }.flowOn(dispatcherProvider.io)
        }

    val listenFeedDisk
        get() = suspend {
            val allEntities = entityDataSource.getEntitiesWithType(
                listOf(
                    AthleticEntity.Type.LIVE_AUDIO_ROOM,
                    AthleticEntity.Type.PODCAST_EPISODE,
                    AthleticEntity.Type.PODCAST_SERIES,
                )
            )

            ListenFeedData.WithEntities(
                followingLiveRooms = allEntities.filterIsInstance<LiveAudioRoomEntity>(),
                podcastEpisodes = allEntities.filterIsInstance<PodcastEpisodeEntity>(),
                followingPodcasts = allEntities.filterIsInstance<PodcastSeriesEntity>(),
                discoverLiveRooms = allEntities.filterIsInstance<LiveAudioRoomEntity>(),
                discoverPodcasts = allEntities.filterIsInstance<PodcastSeriesEntity>(),
            )
        }

    fun fetchListenFeed() = repositoryScope.launch {
        listenFeedDataFetcher.fetchRemote(EmptyParams)
    }

    fun getCurrentLiveRooms(fetch: Boolean): Flow<CurrentLiveRoomsData?> {
        if (fetch) {
            repositoryScope.launch {
                currentLiveRoomsFetcher.fetchRemote(EmptyParams)
            }
        }

        return currentLiveRoomsDataSource.item
    }

    private inline fun <reified T : AthleticEntity> Map<AthleticEntity.Id, AthleticEntity>.find(
        ids: List<AthleticEntity.Id>
    ) = ids.mapNotNull { get(it) }.filterIsInstance<T>()
}