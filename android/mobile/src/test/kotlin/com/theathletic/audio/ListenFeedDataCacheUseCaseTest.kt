package com.theathletic.audio

import com.theathletic.audio.data.ListenFeedRepository
import com.theathletic.audio.data.local.ListenFeedData
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.podcast.data.PodcastRepository
import com.theathletic.test.assertStream
import com.theathletic.test.runTest
import com.theathletic.test.testFlowOf
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

internal class ListenFeedDataCacheUseCaseTest {

    private lateinit var listenFeedDataCacheUseCase: ListenFeedDataCacheUseCase
    @Mock lateinit var listenFeedRepository: ListenFeedRepository
    @Mock lateinit var podcastRepository: PodcastRepository

    private val listenFeedDisk = ListenFeedData.WithEntities(
        followingLiveRooms = ListenFeedDataFixtures.LiveAudioRoom.listOfLiveRoomFixtures(),
        podcastEpisodes = ListenFeedDataFixtures.PodcastEpisode.listOfFollowingPodcastsFixtures(),
        followingPodcasts = ListenFeedDataFixtures.PodcastSeries.listOfFollowingPodcastsSeriesFixtures()
    )

    private val listenFeed = ListenFeedData.WithEntities(
        followingLiveRooms = ListenFeedDataFixtures.LiveAudioRoom.listOfLiveRoomFixtures("1", "2", "3"),
        podcastEpisodes = ListenFeedDataFixtures.PodcastEpisode.listOfPodcastsFixtures(),
        followingPodcasts = ListenFeedDataFixtures.PodcastSeries.listOfPodcastsSeriesFixtures()
    )

    private val downloadedEpisodes = ListenFeedDataFixtures.PodcastEpisode.listOfFollowingPodcastsFixtures().map { episode ->
        PodcastEpisodeItem().apply {
            id = episode.id.toLong()
        }
    }

    private val latestEpisodes = ListenFeedDataFixtures.PodcastEpisode.listOfLatestPodcastsFixtures()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        listenFeedDataCacheUseCase = ListenFeedDataCacheUseCase(
            listenFeedRepository,
            podcastRepository
        )
        whenever(listenFeedRepository.listenFeedDisk).thenReturn { listenFeedDisk }
    }

    @Test
    fun `emits network data when data can be retrieved`() = runTest {
        whenever(listenFeedRepository.listenFeed).thenReturn(flowOf(listenFeed))

        val testFlow = testFlowOf(listenFeedDataCacheUseCase())
        assertStream(testFlow).hasReceivedExactly(listenFeed)
        testFlow.finish()
    }

    @Test
    fun `emits cached data when network data cannot be retrieved`() = runTest {
        whenever(listenFeedRepository.listenFeed).thenReturn(flowOf(null))
        whenever(podcastRepository.downloadedEpisodesImmediate()).thenReturn(downloadedEpisodes)

        val testFlow = testFlowOf(listenFeedDataCacheUseCase())
        val finalFeedData = listenFeedDisk.copy(
            podcastEpisodes = latestEpisodes
        )
        assertStream(testFlow).hasReceivedExactly(finalFeedData)
        testFlow.finish()
    }

    @Test
    fun `emits empty podcast episodes when using disk cache and there are no podcasts are followed by the user`() = runTest {
        whenever(listenFeedRepository.listenFeedDisk).thenReturn {
            listenFeedDisk.copy(
                followingPodcasts = ListenFeedDataFixtures.PodcastSeries.listOfNotFollowingPodcastsSeriesFixtures()
            )
        }
        whenever(listenFeedRepository.listenFeed).thenReturn(flowOf(null))
        whenever(podcastRepository.downloadedEpisodesImmediate()).thenReturn(downloadedEpisodes)

        val testFlow = testFlowOf(listenFeedDataCacheUseCase())
        val expectedFeedData = listenFeedDisk.copy(
            followingPodcasts = emptyList(),
            podcastEpisodes = latestEpisodes
        )
        assertStream(testFlow).hasReceivedExactly(expectedFeedData)
        testFlow.finish()
    }

    @Test
    fun `emits followed podcast episodes when using disk cache and there are podcasts followed by the user`() = runTest {
        whenever(listenFeedRepository.listenFeedDisk).thenReturn {
            listenFeedDisk.copy(
                followingPodcasts = ListenFeedDataFixtures.PodcastSeries.listOfFollowingPodcastsSeriesFixtures()
            )
        }
        whenever(listenFeedRepository.listenFeed).thenReturn(flowOf(null))
        whenever(podcastRepository.downloadedEpisodesImmediate()).thenReturn(downloadedEpisodes)

        val testFlow = testFlowOf(listenFeedDataCacheUseCase())
        val expectedFeedData = listenFeedDisk.copy(
            followingPodcasts = ListenFeedDataFixtures.PodcastSeries.listOfFollowingPodcastsSeriesFixtures(),
            podcastEpisodes = latestEpisodes
        )
        assertStream(testFlow).hasReceivedExactly(expectedFeedData)
        testFlow.finish()
    }

    @Test
    fun `emits empty podcast episodes if there are no downloaded episodes`() = runTest {
        whenever(listenFeedRepository.listenFeed).thenReturn(flowOf(null))
        whenever(podcastRepository.downloadedEpisodesImmediate()).thenReturn(emptyList())

        val testFlow = testFlowOf(listenFeedDataCacheUseCase())
        val expectedFeedData = listenFeedDisk.copy(
            podcastEpisodes = emptyList()
        )
        assertStream(testFlow).hasReceivedExactly(expectedFeedData)
        testFlow.finish()
    }
}