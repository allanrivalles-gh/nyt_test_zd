package com.theathletic.audio.ui

import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.StringRes
import com.theathletic.R
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.audio.data.local.ListenFeedData
import com.theathletic.datetime.DateUtility
import com.theathletic.entity.main.PodcastTopicEntryType
import com.theathletic.entity.room.LiveAudioRoomEntity
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.FeedUiV2
import com.theathletic.feed.ui.PodcastDownloadWrapper
import com.theathletic.feed.ui.modules.audio.EmptyPodcastsModule
import com.theathletic.feed.ui.modules.audio.LatestPodcastEpisodesModule
import com.theathletic.feed.ui.modules.audio.LatestPodcastEpisodesModule.Episode
import com.theathletic.feed.ui.modules.audio.LiveRoomModule
import com.theathletic.feed.ui.modules.audio.PodcastCarouselModule
import com.theathletic.feed.ui.modules.audio.PodcastCategoriesModule
import com.theathletic.podcast.data.local.PodcastEpisodeEntity
import com.theathletic.podcast.data.local.PodcastSeriesEntity
import com.theathletic.podcast.state.PodcastPlayerState
import com.theathletic.ui.Transformer
import com.theathletic.utility.LogoUtility

class ListenTabTransformer @AutoKoin constructor(
    private val dateUtility: DateUtility
) : Transformer<ListenFollowingState, ListenTabContract.ViewState> {

    override fun transform(data: ListenFollowingState): ListenTabContract.ViewState {

        val modules = when (data.tabType) {
            ListenTabContract.TabType.FOLLOWING -> data.feedData?.buildFollowingTab(
                data.podcastPlayerState,
                data.downloadedPodcastIds,
                data.podcastDownloadData,
            )
            ListenTabContract.TabType.DISCOVER -> data.feedData?.buildDiscoverTab()
        } ?: emptyList()

        return ListenTabContract.ViewState(
            showSpinner = data.loadingState.isFreshLoadingState,
            feedUiModel = FeedUiV2(modules)
        )
    }

    private fun ListenFeedData.WithEntities.buildFollowingTab(
        podcastPlayerState: PodcastPlayerState,
        downloadedPodcastIds: List<String>,
        podcastDownloadData: PodcastDownloadWrapper,
    ): List<FeedModuleV2> {
        val liveRoomModules = followingLiveRooms.toModules()
        val latestEpisodeModule = if (followingPodcasts.isEmpty()) {
            listOf(EmptyPodcastsModule)
        } else {
            podcastEpisodes.toLatestEpisodesModule(
                podcastPlayerState = podcastPlayerState,
                downloadedPodcastIds = downloadedPodcastIds,
                podcastDownloadState = podcastDownloadData,
                moduleIndex = liveRoomModules.size,
            )
        }
        val followingPodcastsCarousel = followingPodcasts.toCarouselModule(
            title = R.string.podcast_feed_your_shows,
            moduleIndex = liveRoomModules.size + latestEpisodeModule.size,
        )
        return liveRoomModules +
            latestEpisodeModule +
            followingPodcastsCarousel
    }

    private fun ListenFeedData.WithEntities.buildDiscoverTab(): List<FeedModuleV2> {
        val liveRoomModules = discoverLiveRooms.toModules()
        val discoverPodcastsCarousel = discoverPodcasts.toCarouselModule(
            title = R.string.podcast_feed_recommended,
            moduleIndex = liveRoomModules.size,
        )
        val categoriesModule = categories.toModule(
            moduleIndex = liveRoomModules.size + discoverPodcastsCarousel.size
        )

        return liveRoomModules +
            discoverPodcastsCarousel +
            categoriesModule
    }

    private fun List<LiveAudioRoomEntity>.toModules() = mapIndexedNotNull { moduleIndex, entity ->
        val endedAt = entity.endedAt
        if (endedAt != null && dateUtility.isInPastMoreThan(endedAt.timeMillis, 0)) {
            return@mapIndexedNotNull null
        }
        LiveRoomModule(
            id = entity.id,
            title = entity.title,
            description = entity.subtitle,
            logos = entity.topicImages,
            hostImageUrls = entity.hosts.map { it.imageUrl },
            backgroundTintColor = when (entity.tags.size) {
                1 -> entity.tags.firstOrNull()?.color
                else -> null
            },
            analyticsPayload = LiveRoomModule.Payload(moduleIndex = moduleIndex),
            impressionPayload = ImpressionPayload(
                element = "live_room",
                container = "live_room",
                objectType = "room_id",
                objectId = entity.id,
                pageOrder = moduleIndex,
            )
        )
    }

    private fun List<PodcastEpisodeEntity>.toLatestEpisodesModule(
        podcastPlayerState: PodcastPlayerState,
        downloadedPodcastIds: List<String>,
        podcastDownloadState: PodcastDownloadWrapper,
        moduleIndex: Int,
    ): List<LatestPodcastEpisodesModule> = listOf(
        LatestPodcastEpisodesModule(
            id = moduleIndex.toString(),
            episodes = mapIndexed { vIndex, entity ->
                Episode(
                    id = entity.id,
                    imageUrl = entity.imageUrl,
                    publishedDate = entity.publishedAt,
                    title = entity.title,
                    progressMs = when (podcastPlayerState.activeTrack?.id?.toString()) {
                        entity.id -> podcastPlayerState.currentProgressMs.toLong()
                        else -> entity.timeElapsedMs
                    },
                    durationMs = entity.duration * 1000,
                    playbackState = podcastPlayerState.getPlaybackState(entity.id),
                    downloadState = when {
                        downloadedPodcastIds.contains(entity.id) ->
                            Episode.DownloadState.DOWNLOADED
                        podcastDownloadState[entity.id]?.isDownloading() == true ->
                            Episode.DownloadState.DOWNLOADING
                        else -> Episode.DownloadState.NOT_DOWNLOADED
                    },
                    isFinished = entity.isFinished,
                    payload = Episode.Payload(
                        moduleIndex = moduleIndex,
                        vIndex = vIndex,
                    ),
                )
            }
        )
    )

    private fun List<PodcastSeriesEntity>.toCarouselModule(
        @StringRes title: Int,
        moduleIndex: Int,
    ) = listOf(
        PodcastCarouselModule(
            id = moduleIndex.toString(),
            title = title,
            podcasts = mapIndexed { hIndex, entity ->
                PodcastCarouselModule.Podcast(
                    id = entity.id,
                    title = entity.title,
                    subtitle = entity.category,
                    imageUrl = entity.imageUrl,
                    analyticsPayload = PodcastCarouselModule.Podcast.Payload(
                        moduleIndex = moduleIndex,
                        hIndex = hIndex,
                    ),
                )
            }
        )
    )

    private fun List<ListenFeedData.Category>.toModule(moduleIndex: Int) = listOf(
        PodcastCategoriesModule(
            id = moduleIndex.toString(),
            categories = mapIndexed { vIndex, category ->
                PodcastCategoriesModule.Category(
                    id = category.id,
                    type = category.type,
                    name = category.title,
                    imageUrl = when (category.type) {
                        PodcastTopicEntryType.LEAGUE -> LogoUtility.getColoredLeagueLogoPath(
                            category.id.toLong()
                        )
                        else -> category.iconUrl
                    },
                    payload = PodcastCategoriesModule.Category.Payload(
                        categoryType = when (category.type) {
                            PodcastTopicEntryType.LEAGUE -> "league_id"
                            else -> "channel_id"
                        },
                        moduleIndex = moduleIndex,
                        vIndex = vIndex,
                    ),
                )
            }
        )
    )

    private fun PodcastPlayerState.getPlaybackState(
        episodeId: String
    ): Episode.PlaybackState {
        if (episodeId != activeTrack?.episodeId?.toString()) {
            return Episode.PlaybackState.NONE
        }

        return when (playbackState) {
            PlaybackStateCompat.STATE_CONNECTING,
            PlaybackStateCompat.STATE_BUFFERING -> Episode.PlaybackState.LOADING
            PlaybackStateCompat.STATE_PLAYING -> Episode.PlaybackState.PLAYING
            else -> Episode.PlaybackState.NONE
        }
    }
}