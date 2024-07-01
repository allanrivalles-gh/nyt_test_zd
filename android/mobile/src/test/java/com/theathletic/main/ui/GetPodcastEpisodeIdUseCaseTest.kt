package com.theathletic.main.ui

import com.google.common.truth.Truth
import com.theathletic.fragment.PodcastEpisode
import com.theathletic.podcast.data.PodcastRepository
import com.theathletic.test.runTest
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

private const val PODCAST_ID = "14"
private const val EPISODE_NUMBER = 188
private const val PODCAST_EPISODE_ID = "28184"

@RunWith(MockitoJUnitRunner::class)
class GetPodcastEpisodeIdUseCaseTest {

    private val podcastRepository = mockk<PodcastRepository>(relaxed = true)
    private lateinit var getPodcastEpisodeIdUseCase: GetPodcastEpisodeIdUseCase

    @Before
    fun setUp() {
        getPodcastEpisodeIdUseCase = GetPodcastEpisodeIdUseCase(podcastRepository)
    }

    @Test
    fun `successfully returns podcast episode id string when api call is successful`() = runTest {
        coEvery {
            podcastRepository.getPodcastEpisodeByNumber(PODCAST_ID, EPISODE_NUMBER)
        } returns podcastEpisodeItemFixture(PODCAST_EPISODE_ID)

        val result = getPodcastEpisodeIdUseCase(PODCAST_ID, EPISODE_NUMBER)
        Truth.assertThat(result).isEqualTo(Result.success(PODCAST_EPISODE_ID.toLong()))
    }

    @Test
    fun `returns failure with exception when returned id from api is blank`() = runTest {
        coEvery {
            podcastRepository.getPodcastEpisodeByNumber(PODCAST_ID, EPISODE_NUMBER)
        } returns podcastEpisodeItemFixture("")

        val result = getPodcastEpisodeIdUseCase(PODCAST_ID, EPISODE_NUMBER)
        val exception = Throwable("Id is null or blank for $PODCAST_ID episode $EPISODE_NUMBER")
        Truth.assertThat(result.isFailure).isTrue()
        Truth.assertThat(exception.message).isEqualTo("Id is null or blank for $PODCAST_ID episode $EPISODE_NUMBER")
    }

    @Test
    fun `returns failure with exception when api call fails`() = runTest {
        val throwableMessage = "Error"
        coEvery { podcastRepository.getPodcastEpisodeByNumber(PODCAST_ID, EPISODE_NUMBER) } throws Throwable(throwableMessage)

        val result = getPodcastEpisodeIdUseCase(PODCAST_ID, EPISODE_NUMBER)
        val exception = Throwable(throwableMessage)
        Truth.assertThat(result.isFailure).isTrue()
        Truth.assertThat(exception.message).isEqualTo(throwableMessage)
    }
}

fun podcastEpisodeItemFixture(episodeId: String) = PodcastEpisode(
    id = episodeId,
    __typename = "",
    number = 0,
    comment_count = 0,
    description = "",
    duration = 0,
    image_uri = null,
    is_teaser = false,
    mp3_uri = "",
    permalink = "",
    podcast_id = "",
    parent_podcast = null,
    published_at = 0L,
    title = "",
    series_title = null
)