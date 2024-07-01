package com.theathletic.podcast.ui

import com.theathletic.gamedetail.boxscore.ui.DeleteDownloadedPodcastEpisodeUseCase
import com.theathletic.podcast.data.PodcastRepository
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import org.junit.Before

class DeleteDownloadedPodcastEpisodeUseCaseTest {

    private lateinit var deleteDownloadedPodcastEpisodeUseCase: DeleteDownloadedPodcastEpisodeUseCase
    private val podcastRepository = mockk<PodcastRepository>(relaxed = true)

    private val podcastEpisodeId: Long = 157L

    @Before
    fun setUp() {
        deleteDownloadedPodcastEpisodeUseCase = DeleteDownloadedPodcastEpisodeUseCase(podcastRepository)
    }

    @Test
    fun `File is successfully deleted if it exists for the given podcast episode Id`() {
        deleteDownloadedPodcastEpisodeUseCase.invoke(podcastEpisodeId)

        verify { podcastRepository.deleteDownloadedPodcastEpisode(podcastEpisodeId) }
    }
}