package com.theathletic.podcast.ui

import com.google.common.truth.Truth.assertThat
import com.theathletic.gamedetail.boxscore.ui.IsPodcastEpisodeDownloadingUseCase
import com.theathletic.podcast.download.PodcastDownloadStateStore
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import org.junit.Before

class IsPodcastEpisodeDownloadingUseCaseTest {

    private lateinit var isPodcastEpisodeDownloadingUseCase: IsPodcastEpisodeDownloadingUseCase
    private val podcastDownloadStateStore = mockk<PodcastDownloadStateStore>(relaxed = true)

    private val podcastEpisodeId: Long = 157L

    @Before
    fun setUp() {
        isPodcastEpisodeDownloadingUseCase = IsPodcastEpisodeDownloadingUseCase(podcastDownloadStateStore)
    }

    @Test
    fun `returns true if provided episode Id is being actively downloaded`() {
        every { podcastDownloadStateStore.latestState.get(podcastEpisodeId)?.isDownloading() } returns true
        assertThat(isPodcastEpisodeDownloadingUseCase.invoke(podcastEpisodeId)).isTrue()
    }

    @Test
    fun `returns false if provided episode Id is not an active download`() {
        every { podcastDownloadStateStore.latestState.get(podcastEpisodeId)?.isDownloading() } returns false
        assertThat(isPodcastEpisodeDownloadingUseCase.invoke(podcastEpisodeId)).isFalse()
    }
}