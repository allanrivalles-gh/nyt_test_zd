package com.theathletic.analytics

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.theathletic.analytics.data.local.AnalyticsEvent
import com.theathletic.analytics.repository.AnalyticsRepository
import com.theathletic.di.autoKoinModules
import com.theathletic.injection.analyticsModule
import com.theathletic.test.CoroutineTestRule
import com.theathletic.utils.assertInstance
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class UploadAnalyticsWorkerTest : KoinTest {

    @get:Rule val coroutineTestRule = CoroutineTestRule()

    private val repository by inject<AnalyticsRepository>()

    private val context = mock<Context>()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(
            listOf(analyticsModule) + autoKoinModules +
                module { single { coroutineTestRule.dispatcherProvider } }
        )
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Before
    fun setUp() {
        declareMock<AnalyticsRepository>()
    }

    @Test
    fun `purging stale events succeeds`() = runBlocking {
        val events = createEventList()
        setupSuccessfulMockRepository(events)

        val worker = TestListenableWorkerBuilder<UploadAnalyticsWorker>(context).build()
        val result = worker.doWork()

        verify(repository).clearEventsOlderThanXDays(any())
        assertInstance<ListenableWorker.Result.Success>(result)
    }

    @Test
    fun `uploads and deletes events succeeds`() = runBlocking {
        val events = createEventList()
        setupSuccessfulMockRepository(events)

        whenever(repository.hasAnalyticsEventsToPost()).thenReturn(true).thenReturn(false)

        val worker = TestListenableWorkerBuilder<UploadAnalyticsWorker>(context).build()
        val result = worker.doWork()

        verify(repository).getAnalyticsEventsToPost(any())
        verify(repository).uploadAnalyticsEvents(eq(events))
        verify(repository).deleteAnalyticsEvents(eq(events))
        assertInstance<ListenableWorker.Result.Success>(result)
    }

    @Test
    fun `uploads and deletes multiple pages successfully`() = runBlocking {
        val events = List(100) {
            declareMock<AnalyticsEvent>()
        }

        setupSuccessfulMockRepository(events)
        whenever(repository.getAnalyticsEventsToPost(any())).thenReturn(events)
        whenever(repository.hasAnalyticsEventsToPost())
            .thenReturn(true)
            .thenReturn(true)
            .thenReturn(true)
            .thenReturn(false)

        val worker = TestListenableWorkerBuilder<UploadAnalyticsWorker>(context).build()
        val result = worker.doWork()

        verify(repository, times(3)).getAnalyticsEventsToPost(any())
        verify(repository, times(3)).uploadAnalyticsEvents(eq(events))
        verify(repository, times(3)).deleteAnalyticsEvents(eq(events))
        assertInstance<ListenableWorker.Result.Success>(result)
    }

    @Test
    fun `upload and delete succeeds when purge fails`() = runBlocking {
        val events = createEventList()
        setupSuccessfulMockRepository(events)
        whenever(repository.clearEventsOlderThanXDays(any())).thenThrow(RuntimeException())

        val worker = TestListenableWorkerBuilder<UploadAnalyticsWorker>(context).build()
        val result = worker.doWork()

        assertInstance<ListenableWorker.Result.Success>(result)
    }

    @Test
    fun `api first fail returns a retry status`() = runBlocking {
        val events = createEventList()
        setupSuccessfulMockRepository(events)
        whenever(repository.uploadAnalyticsEvents(any())).thenThrow(RuntimeException())

        val worker = TestListenableWorkerBuilder<UploadAnalyticsWorker>(context).build()
        val result = worker.doWork()

        verify(repository, never()).deleteAnalyticsEvents(events)
        assertInstance<ListenableWorker.Result.Retry>(result)
    }

    @Test
    fun `failing max times return a failure status`() = runBlocking {
        val events = createEventList()
        setupSuccessfulMockRepository(events)
        whenever(repository.hasAnalyticsEventsToPost()).thenReturn(true)
        whenever(repository.uploadAnalyticsEvents(any())).thenThrow(RuntimeException())

        for (i in 1 until UploadAnalyticsWorker.MAX_API_UPLOAD_ATTEMPT_COUNT) {
            val result = TestListenableWorkerBuilder<UploadAnalyticsWorker>(
                context,
                runAttemptCount = i
            ).build().doWork()
            assertInstance<ListenableWorker.Result.Retry>(result)
        }

        val result = TestListenableWorkerBuilder<UploadAnalyticsWorker>(
            context,
            runAttemptCount = UploadAnalyticsWorker.MAX_API_UPLOAD_ATTEMPT_COUNT
        ).build().doWork()
        assertInstance<ListenableWorker.Result.Failure>(result)
    }

    @Test
    fun `any other error returns a retry status`() = runBlocking {
        val events = createEventList()
        setupSuccessfulMockRepository(events)
        whenever(repository.getAnalyticsEventsToPost(any())).thenThrow(RuntimeException())

        val worker = TestListenableWorkerBuilder<UploadAnalyticsWorker>(context).build()
        val result = worker.doWork()

        verify(repository, never()).deleteAnalyticsEvents(events)
        assertInstance<ListenableWorker.Result.Retry>(result)
    }

    private fun createEventList() = listOf(mock<AnalyticsEvent>())

    private fun setupSuccessfulMockRepository(eventsForMock: List<AnalyticsEvent>) = runBlocking {
        whenever(repository.clearEventsOlderThanXDays(any())).thenReturn(Unit)
        whenever(repository.getAnalyticsEventsToPost(any())).thenReturn(eventsForMock)
        whenever(repository.uploadAnalyticsEvents(any())).thenReturn(Unit)
        whenever(repository.deleteAnalyticsEvents(any())).thenReturn(Unit)
        whenever(repository.hasAnalyticsEventsToPost()).thenReturn(true).thenReturn(false)

        whenever(repository.getFlexibleAnalyticsEventsToPost(any(), any())).thenReturn(listOf())
        whenever(repository.uploadFlexibleAnalyticsEvents(any(), any())).thenReturn(Unit)
        whenever(repository.deleteFlexibleAnalyticsEvents(any())).thenReturn(Unit)
        whenever(repository.hasFlexibleAnalyticsEventsToPost(any())).thenReturn(false)
    }
}