package com.theathletic.boxscore.data.di

import android.support.v4.media.session.PlaybackStateCompat
import com.theathletic.boxscore.ObservePodcastStateUseCase
import com.theathletic.boxscore.data.BoxScoreFeedFixtures
import com.theathletic.boxscore.data.local.BoxScoreLocalDataSource
import com.theathletic.boxscore.data.local.BoxScorePodcastState
import com.theathletic.entity.main.PodcastTrack
import com.theathletic.podcast.state.PodcastPlayerState
import com.theathletic.podcast.state.PodcastPlayerStateBus
import com.theathletic.podcast.state.configurableProgressChangeFlow
import com.theathletic.podcast.ui.DownloadState
import com.theathletic.podcast.ui.PlaybackState
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.assertStream
import com.theathletic.test.runTest
import com.theathletic.test.testFlowOf
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ObservePodcastStateUseCaseTest {
    @get:Rule val coroutineTestRule = CoroutineTestRule()

    private lateinit var observePodcastStateUseCase: ObservePodcastStateUseCase

    private val podcastPlayerStateBus = mockk<PodcastPlayerStateBus>()
    private val boxScoreLocalDataSource = mockk<BoxScoreLocalDataSource>()

    private val gameId = "001"
    private val podcastEpisodeId = "7"

    @BeforeTest
    fun setUp() {
        observePodcastStateUseCase = ObservePodcastStateUseCase(boxScoreLocalDataSource, podcastPlayerStateBus)
    }

    @Test
    fun `emit box score feed when podcast state change from none to playing with time elapse update`() = runTest {
        val boxScore = BoxScoreFeedFixtures.getBoxScoreArticleAndPodcast(
            PlaybackState.None,
            timeElapsed = 0,
            downloadState = DownloadState.NOT_DOWNLOADED
        )
        every { (podcastPlayerStateBus.configurableProgressChangeFlow(5000)) } returns
            flowOf(getPodcastState(PlaybackStateCompat.STATE_PLAYING, 150000))

        every { (boxScoreLocalDataSource.getItem(any())) } returns boxScore

        val testFlow = testFlowOf(observePodcastStateUseCase(gameId))

        val boxScorePodcastState = BoxScorePodcastState(
            boxScore = boxScore,
            podcastEpisodeId = podcastEpisodeId,
            playbackState = PlaybackState.Playing
        )

        assertStream(testFlow).lastEvent().isEqualTo(boxScorePodcastState)
    }

    @Test
    fun `emit BoxScorePodcastState podcast state change from loading to play`() = runTest {
        val boxScore = BoxScoreFeedFixtures.getBoxScoreArticleAndPodcast(
            PlaybackState.Loading,
            timeElapsed = 30000,
            downloadState = DownloadState.NOT_DOWNLOADED
        )
        every { (podcastPlayerStateBus.configurableProgressChangeFlow(300000)) } returns
            flowOf(getPodcastState(PlaybackStateCompat.STATE_CONNECTING, 0))

        every { (boxScoreLocalDataSource.getItem(any())) } returns boxScore

        var testFlow = testFlowOf(observePodcastStateUseCase(gameId))

        var boxScorePodcastState = BoxScorePodcastState(
            boxScore = boxScore,
            podcastEpisodeId = podcastEpisodeId,
            playbackState = PlaybackState.Loading
        )

        assertStream(testFlow).lastEvent().isEqualTo(boxScorePodcastState)

        every { (podcastPlayerStateBus.configurableProgressChangeFlow(5000)) } returns
            flowOf(getPodcastState(PlaybackStateCompat.STATE_PLAYING, 300000))

        testFlow = testFlowOf(observePodcastStateUseCase(gameId))

        boxScorePodcastState = BoxScorePodcastState(
            boxScore = boxScore,
            podcastEpisodeId = podcastEpisodeId,
            playbackState = PlaybackState.Playing
        )

        assertStream(testFlow).lastEvent().isEqualTo(boxScorePodcastState)
    }

    @Test
    fun `emit BoxScorePodcastState podcast state change from playing to pause`() = runTest {
        val boxScore = BoxScoreFeedFixtures.getBoxScoreArticleAndPodcast(
            PlaybackState.None,
            timeElapsed = 30000,
            downloadState = DownloadState.NOT_DOWNLOADED
        )
        every { (podcastPlayerStateBus.configurableProgressChangeFlow(5000)) } returns
            flowOf(getPodcastState(PlaybackStateCompat.STATE_PLAYING, 300000))

        every { (boxScoreLocalDataSource.getItem(any())) } returns boxScore

        var testFlow = testFlowOf(observePodcastStateUseCase(gameId))

        var boxScorePodcastState = BoxScorePodcastState(
            boxScore = boxScore,
            podcastEpisodeId = podcastEpisodeId,
            playbackState = PlaybackState.Playing
        )

        assertStream(testFlow).lastEvent().isEqualTo(boxScorePodcastState)

        every { (podcastPlayerStateBus.configurableProgressChangeFlow(5000)) } returns
            flowOf(getPodcastState(PlaybackStateCompat.STATE_PAUSED, 300000))

        testFlow = testFlowOf(observePodcastStateUseCase(gameId))

        boxScorePodcastState = BoxScorePodcastState(
            boxScore = boxScore,
            podcastEpisodeId = podcastEpisodeId,
            playbackState = PlaybackState.None
        )

        assertStream(testFlow).lastEvent().isEqualTo(boxScorePodcastState)
    }

    private fun getPodcastState(playbackState: Int, currentProgressMs: Int) = PodcastPlayerState(
        activeTrack = PodcastTrack().apply { episodeId = 7L },
        playbackState = playbackState,
        currentProgressMs = currentProgressMs
    )
}