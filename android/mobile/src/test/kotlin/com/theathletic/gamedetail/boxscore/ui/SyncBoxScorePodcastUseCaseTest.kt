package com.theathletic.gamedetail.boxscore.ui

import com.google.common.truth.Truth.assertThat
import com.theathletic.boxscore.data.local.BoxScoreLocalDataSource
import com.theathletic.datetime.asGMTString
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.gamedetail.boxscore.SyncBoxScorePodcastUseCase
import com.theathletic.gamedetail.boxscore.ui.BoxScoreFixture.boxScoreFeedFixture
import com.theathletic.podcast.data.PodcastRepository
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.runTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import java.util.Date
import kotlin.test.Test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)

class SyncBoxScorePodcastUseCaseTest {

    private val boxScoreFeed = boxScoreFeedFixture()
    private val gameId = "1"
    private val podcastIds = 5L

    @get:Rule val coroutineTestRule = CoroutineTestRule()

    private lateinit var syncBoxScorePodcastUseCase: SyncBoxScorePodcastUseCase

    private val podcastRepository = mockk<PodcastRepository>(relaxed = true)
    private val boxScoreLocalDataSource = mockk<BoxScoreLocalDataSource>(relaxed = true)

    @Before
    fun setUp() {
        syncBoxScorePodcastUseCase = SyncBoxScorePodcastUseCase(podcastRepository, boxScoreLocalDataSource)
    }

    @Test
    fun `update the local data source only when the podcast episode already exists in the database when data is refreshed`() = runTest {
        coEvery { podcastRepository.podcastEpisodeById(any()) } returns PodcastEpisodeItem().apply { id = podcastIds }

        val syncResult = syncBoxScorePodcastUseCase(gameId, boxScoreFeed)

        assertThat(syncResult.boxScore).isNotNull()
        assertThat(syncResult.hasPodcastEpisodes).isTrue()
        coVerify { boxScoreLocalDataSource.update(gameId, syncResult.boxScore) }
        coVerify(inverse = true) {
            podcastRepository.savePodcast(
                episodeId = "5",
                podcastId = "5",
                episodeTitle = "Sample podcast",
                description = "",
                duration = 0L,
                dateGmt = Date(0).asGMTString(),
                mp3Url = "",
                imageUrl = "",
                permalinkUrl = "",
                isDownloaded = false
            )
        }
    }

    @Test
    fun `add the podcast episode to the database when it does not exists when data is refreshed`() = runTest {
        coEvery { podcastRepository.podcastEpisodeById(any()) } returns null

        val syncResult = syncBoxScorePodcastUseCase(gameId, boxScoreFeed)

        assertThat(syncResult.boxScore).isNotNull()
        assertThat(syncResult.hasPodcastEpisodes).isTrue()
        verify(inverse = true) { boxScoreLocalDataSource.update(gameId, syncResult.boxScore) }
        coVerify {
            podcastRepository.savePodcast(
                episodeId = "5",
                podcastId = "5",
                episodeTitle = "Sample podcast",
                description = "",
                duration = 0L,
                dateGmt = Date(0).asGMTString(),
                mp3Url = "",
                imageUrl = "",
                permalinkUrl = "",
                isDownloaded = false
            )
        }
    }
}