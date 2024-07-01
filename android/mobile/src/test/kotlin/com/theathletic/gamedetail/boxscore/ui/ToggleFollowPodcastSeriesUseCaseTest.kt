package com.theathletic.gamedetail.boxscore.ui

import com.theathletic.audio.data.remote.AudioApi
import com.theathletic.podcast.data.PodcastRepository
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.runTest
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import java.io.IOException
import kotlin.test.Test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
class ToggleFollowPodcastSeriesUseCaseTest {
    @get:Rule val coroutineTestRule = CoroutineTestRule()

    private lateinit var toggleFollowPodcastSeriesUseCase: ToggleFollowPodcastSeriesUseCase

    private val podcastRepository = mockk<PodcastRepository>(relaxed = true)
    private val audioApi = mockk<AudioApi>()

    private val podcastId = 156L

    @Before
    fun setUp() {
        toggleFollowPodcastSeriesUseCase = ToggleFollowPodcastSeriesUseCase(podcastRepository, audioApi)
    }

    @Test
    fun `adds the podcast episode to followed podcast episode collection if the podcast episode is not followed by the user`() = runTest {
        coEvery { (podcastRepository.isPodcastSeriesFollowed(any())) } returns false

        toggleFollowPodcastSeriesUseCase(podcastId)

        verify { podcastRepository.setPodcastFollowStatus(podcastId, true) }
        verify { podcastRepository.refreshFollowed() }
    }

    @Test
    fun `removes the podcast episode from the followed podcast episode collection if the podcast episode is followed by the user`() = runTest {
        coEvery { (podcastRepository.isPodcastSeriesFollowed(any())) } returns true

        toggleFollowPodcastSeriesUseCase(podcastId)

        verify { podcastRepository.setPodcastFollowStatus(podcastId, false) }
        verify { podcastRepository.refreshFollowed() }
    }

    @Test
    fun `error is thrown during removing podcast episode from the followable podcast episode collection when user tries to unfollow a podcast episode`() = runTest {
        coEvery { (podcastRepository.isPodcastSeriesFollowed(any())) } returns true
        coEvery { (audioApi.unfollowPodcast(podcastId.toString())) } throws IOException()

        toggleFollowPodcastSeriesUseCase(podcastId)

        verify(atLeast = 1) { podcastRepository.setPodcastFollowStatus(podcastId, false) }
        verify { podcastRepository.refreshFollowed() }
        verify(atLeast = 1) { podcastRepository.setPodcastFollowStatus(podcastId, true) }
    }

    @Test
    fun `error is thrown during adding podcast episode to followable podcast episode collection when user tries to follow a podcast episode`() = runTest {
        coEvery { (podcastRepository.isPodcastSeriesFollowed(any())) } returns false
        coEvery { (audioApi.followPodcast(podcastId.toString())) } throws IOException()

        toggleFollowPodcastSeriesUseCase(podcastId)

        verify(atLeast = 1) { podcastRepository.setPodcastFollowStatus(podcastId, true) }
        verify { podcastRepository.refreshFollowed() }
        verify(atLeast = 1) { (podcastRepository).setPodcastFollowStatus(podcastId, false) }
    }
}