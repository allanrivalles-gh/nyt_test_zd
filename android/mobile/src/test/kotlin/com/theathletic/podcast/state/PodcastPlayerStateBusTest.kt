package com.theathletic.podcast.state

import android.support.v4.media.session.PlaybackStateCompat
import com.theathletic.entity.main.PodcastTrack
import com.theathletic.test.assertStream
import com.theathletic.test.runTest
import com.theathletic.test.testFlowOf
import org.junit.Before
import org.junit.Test

class PodcastPlayerStateBusTest {

    private lateinit var podcastStateBus: PodcastPlayerStateBus

    @Before
    fun setUp() {
        podcastStateBus = PodcastPlayerStateBus()
    }

    @Test
    fun `updating active track`() = runTest {
        podcastStateBus.updateActiveTrack(TEST_TRACK)

        val testFlow = testFlowOf(podcastStateBus.stateChangeFlow)
        podcastStateBus.updateActiveTrack(null)

        assertStream(testFlow).hasReceivedExactly(
            PodcastPlayerState(
                activeTrack = TEST_TRACK,
                playbackState = PlaybackStateCompat.STATE_CONNECTING,
                currentProgressMs = 0
            ),
            PodcastPlayerState(
                activeTrack = TEST_TRACK,
                playbackState = PlaybackStateCompat.STATE_PAUSED,
                currentProgressMs = 0
            ),
            PodcastPlayerState(activeTrack = null)
        )

        testFlow.finish()
    }

    @Test
    fun `updating playback state`() = runTest {
        val testFlow = testFlowOf(podcastStateBus.stateChangeFlow)
        podcastStateBus.updateActiveTrack(TEST_TRACK)
        podcastStateBus.updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)

        assertStream(testFlow).lastEvent().isEqualTo(
            PodcastPlayerState(
                activeTrack = TEST_TRACK,
                playbackState = PlaybackStateCompat.STATE_PLAYING,
                currentProgressMs = 0
            )
        )

        testFlow.finish()
    }

    @Test
    fun `changing progress updates the progressChangeSubject`() = runTest {
        val testFlow = testFlowOf(podcastStateBus.progressChangeFlow)

        podcastStateBus.updateActiveTrack(TEST_TRACK)
        podcastStateBus.updateProgress(0)
        podcastStateBus.updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)

        assertStream(testFlow).lastEvent()
            .isEqualTo(
                PodcastPlayerState(
                    activeTrack = TEST_TRACK,
                    currentProgressMs = 0,
                    playbackState = PlaybackStateCompat.STATE_PLAYING
                )
            )

        podcastStateBus.updateProgress(10)

        assertStream(testFlow).lastEvent().isEqualTo(
            PodcastPlayerState(
                activeTrack = TEST_TRACK,
                currentProgressMs = 10,
                playbackState = PlaybackStateCompat.STATE_PLAYING
            )
        )

        testFlow.finish()
    }

    @Test
    fun `changing progress does not update stateChangeSubject`() = runTest {
        podcastStateBus.updateActiveTrack(TEST_TRACK)
        podcastStateBus.updateProgress(0)
        val stateTestFlow = testFlowOf(podcastStateBus.stateChangeFlow)
        val progressTestFlow = testFlowOf(podcastStateBus.progressChangeFlow)

        podcastStateBus.updateProgress(10)
        podcastStateBus.updateProgress(20)
        podcastStateBus.updateProgress(30)

        assertStream(stateTestFlow).eventCount(1)
        assertStream(progressTestFlow).eventCount(4)
        stateTestFlow.finish()
        progressTestFlow.finish()
    }

    companion object {
        val TEST_TRACK = PodcastTrack()
    }
}