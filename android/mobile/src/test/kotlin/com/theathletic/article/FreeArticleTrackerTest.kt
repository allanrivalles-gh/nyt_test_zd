package com.theathletic.article

import com.google.common.truth.Truth.assertThat
import com.theathletic.test.FixedTimeProvider
import java.util.Calendar
import org.junit.Before
import org.junit.Test

class FreeArticleTrackerTest {
    private lateinit var timeProvider: FixedTimeProvider
    private lateinit var tracker: FreeArticleTracker

    @Before
    fun setup() {
        timeProvider = FixedTimeProvider()
        tracker = FreeArticleTracker(timeProvider, null)
    }

    @Test
    fun `returns true if limit not reached`() {
        val limit = 2
        val snapshotChanges = mutableListOf<Unit>()
        tracker.onSnapshotChanged = { snapshotChanges.add(Unit) }

        val isFree = tracker.isArticleFree(1L, limit)

        assertThat(isFree).isTrue()
        assertThat(snapshotChanges).hasSize(1)
    }

    @Test
    fun `returns false if limit reached`() {
        val limit = 2
        val snapshotChanges = mutableListOf<Unit>()
        tracker.onSnapshotChanged = { snapshotChanges.add(Unit) }

        tracker.isArticleFree(1L, limit)
        tracker.isArticleFree(2L, limit)

        val isFree = tracker.isArticleFree(3L, limit)

        assertThat(isFree).isFalse()
        assertThat(snapshotChanges).hasSize(2)
    }

    @Test
    fun `returns true for another article after the same article is read twice`() {
        val limit = 2
        val snapshotChanges = mutableListOf<Unit>()
        tracker.onSnapshotChanged = { snapshotChanges.add(Unit) }

        tracker.isArticleFree(1L, limit)
        tracker.isArticleFree(1L, limit)

        val isFree = tracker.isArticleFree(2L, limit)

        assertThat(isFree).isTrue()
        assertThat(snapshotChanges).hasSize(2)
    }

    @Test
    fun `returns true if limit previously reached but month has changed`() {
        val limit = 2
        val snapshotChanges = mutableListOf<Unit>()
        tracker.onSnapshotChanged = { snapshotChanges.add(Unit) }

        val january1st2023 = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2023)
            set(Calendar.MONTH, 0)
            set(Calendar.DAY_OF_MONTH, 1)
        }.timeInMillis
        val february1st2023 = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2023)
            set(Calendar.MONTH, 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }.timeInMillis

        timeProvider.currentTimeMs = january1st2023
        tracker.isArticleFree(1L, limit)
        tracker.isArticleFree(2L, limit)

        timeProvider.currentTimeMs = february1st2023
        val isFree = tracker.isArticleFree(3L, limit)

        assertThat(isFree).isTrue()
        assertThat(snapshotChanges).hasSize(3)
    }
}