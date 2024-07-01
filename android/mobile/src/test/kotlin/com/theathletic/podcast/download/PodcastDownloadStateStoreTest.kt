package com.theathletic.podcast.download

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PodcastDownloadStateStoreTest {

    private lateinit var podcastDownloadStore: PodcastDownloadStateStore

    @Before
    fun beforeEach() {
        podcastDownloadStore = PodcastDownloadStateStore()
    }

    @Test
    fun `first call to getEntity return an empty DownloadEntityItem`() {
        val entity = podcastDownloadStore.getEntity(1)

        assertEquals(1L, entity.podcastEpisodeId)
        assertEquals(-1L, entity.downloadId)
        assertEquals(-1L, entity.progress)
    }

    @Test
    fun `second call to getEntity returns original item after update`() {
        val entity = podcastDownloadStore.getEntity(1)

        entity.downloadId = 3L
        entity.podcastEpisodeName = "Test Podcast"
        entity.setProgress(0L)

        podcastDownloadStore.updateEntity(entity)

        val updatedEntity = podcastDownloadStore.getEntity(1)

        assertEquals(1L, updatedEntity.podcastEpisodeId)
        assertEquals(3L, updatedEntity.downloadId)
        assertEquals("Test Podcast", updatedEntity.podcastEpisodeName)
        assertEquals(0L, updatedEntity.progress)
    }

    @Test
    fun `calling removeEntity cancels download`() {
        val entity = podcastDownloadStore.getEntity(1)

        entity.downloadId = 3L
        entity.podcastEpisodeName = "Test Podcast"
        entity.setProgress(0L)

        podcastDownloadStore.updateEntity(entity)

        assertTrue(podcastDownloadStore.hasDownloadsInProgress())

        podcastDownloadStore.removeEntity(1)

        assertFalse(podcastDownloadStore.hasDownloadsInProgress())
    }

    @Test
    fun `calling updateEntity updates the downloadStates subject`() {
        val entity = podcastDownloadStore.getEntity(1)

        entity.downloadId = 3L
        entity.podcastEpisodeName = "Test Podcast"
        entity.setProgress(0L)

        podcastDownloadStore.updateEntity(entity)

        assertEquals(1, podcastDownloadStore.getCurrentDownloadStates().size())
        assertEquals(3L, podcastDownloadStore.getCurrentDownloadStates().get(1)?.downloadId ?: -1L)
    }

    @Test
    fun `getEntityByDownloadId returns properly`() {
        val entity = podcastDownloadStore.getEntity(1)

        entity.downloadId = 3L
        entity.podcastEpisodeName = "Test Podcast"
        entity.setProgress(0L)

        podcastDownloadStore.updateEntity(entity)
        val foundEntity = podcastDownloadStore.getEntityByDownloadId(3L)

        assertEquals(1, foundEntity?.podcastEpisodeId ?: -1L)
    }
}