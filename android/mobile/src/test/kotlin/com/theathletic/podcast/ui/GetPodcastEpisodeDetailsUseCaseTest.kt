package com.theathletic.podcast.ui

import com.google.common.truth.Truth.assertThat
import com.theathletic.gamedetail.boxscore.ui.GetPodcastEpisodeDetailsUseCase
import com.theathletic.podcast.data.PodcastRepository
import com.theathletic.test.CoroutineTestRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.Test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
class GetPodcastEpisodeDetailsUseCaseTest {
    @get:Rule val coroutineTestRule = CoroutineTestRule()

    private lateinit var getPodcastEpisodeDetailsUseCase: GetPodcastEpisodeDetailsUseCase
    private val podcastRepository = mockk<PodcastRepository>(relaxed = true)

    private val podcastEpisodeId: Long = 156L
    private val podcastId: Long = 100L
    private val titleValue: String = "Sample Title"
    private val mp3UrlValue: String = "Download Url"
    private val isTeaserValue: Boolean = false
    private val isDownloadedValue: Boolean = true

    @Before
    fun setUp() {
        getPodcastEpisodeDetailsUseCase = GetPodcastEpisodeDetailsUseCase(podcastRepository)
    }

    @Test
    fun `returns null if the podcast episode does not exists`() {
        coEvery { podcastRepository.podcastEpisodeById(podcastEpisodeId) } returns null

        val podcastEpisodeDetails = runBlocking { getPodcastEpisodeDetailsUseCase(podcastId, podcastEpisodeId) }

        assertThat(podcastEpisodeDetails).isNull()
    }

    @Test
    fun `returns podcast episode details object if the podcast episode does not exists`() {
        coEvery { podcastRepository.isPodcastSeriesFollowed(podcastId) } returns true

        coEvery { podcastRepository.podcastEpisodeById(podcastEpisodeId) } returns com.theathletic.entity.main.PodcastEpisodeItem().apply {
            isDownloaded = isDownloadedValue
            title = titleValue
            mp3Url = mp3UrlValue
            isTeaser = isTeaserValue
        }

        val podcastEpisodeDetails = runBlocking { getPodcastEpisodeDetailsUseCase(podcastId, podcastEpisodeId) }

        assertThat(podcastEpisodeDetails).isNotNull()
        assertThat(podcastEpisodeDetails?.downloadUrl).isEqualTo(mp3UrlValue)
        assertThat(podcastEpisodeDetails?.title).isEqualTo(titleValue)
        assertThat(podcastEpisodeDetails?.isDownloaded).isEqualTo(isDownloadedValue)
        assertThat(podcastEpisodeDetails?.isTeaser).isEqualTo(isTeaserValue)
        assertThat(podcastEpisodeDetails?.isFollowed).isEqualTo(true)
    }
}