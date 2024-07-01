package com.theathletic.article

import com.theathletic.test.FixedTimeProvider
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class FreeArticleTrackerRepositoryTest {
    private lateinit var dataSource: FreeArticleTrackerDataSource
    private lateinit var repository: FreeArticleTrackerRepository

    @Before
    fun setUp() {
        dataSource = mock()
        repository = FreeArticleTrackerRepository(dataSource, FixedTimeProvider())
    }

    @Test
    fun `returns tracker if it is already initialized`() {
        repository.getTracker()

        // The tracker should now be initialized, so getTracker() should return it without calling load()
        repository.getTracker()

        // Verify that load() was only called once
        verify(dataSource).load()
    }

    @Test
    fun `loads initial state and save state when snapshot changes`() {
        // Call getTracker()
        val tracker = repository.getTracker()

        // Simulate snapshot change
        val newState = FreeArticleTracker.Snapshot(FreeArticleTracker.Moment(2023, 6), hashSetOf(1L, 2L))
        tracker.onSnapshotChanged!!(newState)

        // Verify that save() was called with the new state
        verify(dataSource).save(newState)
    }
}