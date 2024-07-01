package com.theathletic.data.local

import com.theathletic.test.assertStream
import com.theathletic.test.runTest
import com.theathletic.test.testFlowOf
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class InMemoryPagingLocalDataSourceTest {

    lateinit var dataSource: TestInMemoryPagingLocalDataSource

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        dataSource = TestInMemoryPagingLocalDataSource()
    }

    @Test
    fun `Each list is initialized to null`() = runTest {
        val testFlow = testFlowOf(dataSource.getPaginatedList(DataSourceKey.KEY_1))

        assertStream(testFlow).hasReceivedExactly(null)

        testFlow.finish()
    }

    @Test
    fun `Can add items to list by key`() = runTest {
        val testFlow = testFlowOf(dataSource.getPaginatedList(DataSourceKey.KEY_1))

        dataSource.update(DataSourceKey.KEY_1, BATCH_1, 0, false)

        assertStream(testFlow).hasReceivedExactly(
            null,
            PaginatedList(listOf(BATCH_1), false)
        )
        testFlow.finish()
    }

    @Test
    fun `Can append to a list by specifying page index`() = runTest {
        val testFlow = testFlowOf(dataSource.getPaginatedList(DataSourceKey.KEY_1))

        dataSource.update(DataSourceKey.KEY_1, BATCH_1, 0, true)
        dataSource.update(DataSourceKey.KEY_1, BATCH_2, 1, false)

        assertStream(testFlow)
            .lastEvent()
            .isEqualTo(PaginatedList(listOf(BATCH_1, BATCH_2), false))

        testFlow.finish()
    }

    @Test
    fun `Can override a list by specifying a page index of 0`() = runTest {
        val testFlow = testFlowOf(dataSource.getPaginatedList(DataSourceKey.KEY_1))

        // Build initial list
        dataSource.update(DataSourceKey.KEY_1, BATCH_1, 0, true)
        dataSource.update(DataSourceKey.KEY_1, BATCH_2, 1, false)

        // Override that list
        dataSource.update(DataSourceKey.KEY_1, BATCH_3, 0, true)

        assertStream(testFlow)
            .lastEvent()
            .isEqualTo(PaginatedList(listOf(BATCH_3), true))
        testFlow.finish()
    }

    @Test
    fun `Can override a page by specifying a page index within lists bounds`() = runTest {
        val testFlow = testFlowOf(dataSource.getPaginatedList(DataSourceKey.KEY_1))

        // Build initial list
        dataSource.update(DataSourceKey.KEY_1, BATCH_1, 0, true)
        dataSource.update(DataSourceKey.KEY_1, BATCH_2, 1, false)

        // Override single page
        dataSource.update(DataSourceKey.KEY_1, BATCH_3, 1, false)

        assertStream(testFlow)
            .lastEvent()
            .isEqualTo(PaginatedList(listOf(BATCH_1, BATCH_3), false))
        testFlow.finish()
    }

    @Test
    fun `Each key maintains a separate list`() = runTest {
        val testFlowKey1 = testFlowOf(dataSource.getPaginatedList(DataSourceKey.KEY_1))
        val testFlowKey2 = testFlowOf(dataSource.getPaginatedList(DataSourceKey.KEY_2))

        dataSource.update(DataSourceKey.KEY_1, BATCH_1, 0, false)

        assertStream(testFlowKey1).lastEvent().isEqualTo(PaginatedList(listOf(BATCH_1), false))
        assertStream(testFlowKey2).lastEvent().isNull()

        testFlowKey1.finish()
        testFlowKey2.finish()
    }

    @Test
    fun `Duplicates in later pages get filtered out`() = runTest {
        val testFlow = testFlowOf(dataSource.getPaginatedList(DataSourceKey.KEY_1))

        // Build initial list
        dataSource.update(DataSourceKey.KEY_1, BATCH_1, 0, true)
        dataSource.update(DataSourceKey.KEY_1, BATCH_2_PLUS_DUPLICATE, 1, true)

        assertStream(testFlow)
            .lastEvent()
            .isEqualTo(PaginatedList(listOf(BATCH_1, BATCH_2), true))
        testFlow.finish()
    }

    @Test
    fun `Can update a value in the list on 1st batch`() = runTest {
        val testFlow = testFlowOf(dataSource.getPaginatedList(DataSourceKey.KEY_1))

        dataSource.update(DataSourceKey.KEY_1, BATCH_1, 0, false)
        dataSource.updateItem(
            { it.id == 3 }, DataSourceKey.KEY_1,
            { item ->
                item.copy(value = "updated")
            }
        )

        assertStream(testFlow)
            .lastEvent()
            .isEqualTo(PaginatedList(listOf(BATCH_1_UPDATE_ID_3), false))
        testFlow.finish()
    }

    @Test
    fun `Can update a value in the list on 2nd batch`() = runTest {
        val testFlow = testFlowOf(dataSource.getPaginatedList(DataSourceKey.KEY_1))

        dataSource.update(DataSourceKey.KEY_1, BATCH_1, 0, true)
        dataSource.update(DataSourceKey.KEY_1, BATCH_2, 1, false)

        dataSource.updateItem(
            { it.id == 15 }, DataSourceKey.KEY_1,
            { item -> item.copy(value = "updated") }
        )

        assertStream(testFlow)
            .lastEvent()
            .isEqualTo(PaginatedList(listOf(BATCH_1, BATCH_2_UPDATE_ID_15), false))
        testFlow.finish()
    }

    companion object {
        private val BATCH_1 = List(10) { DataSourceListItem(it) }
        private val BATCH_2 = List(10) { DataSourceListItem(10 + it) }
        private val BATCH_3 = List(10) { DataSourceListItem(20 + it) }

        private val DUPLICATED_ITEM = DataSourceListItem(5)
        private val BATCH_2_PLUS_DUPLICATE = BATCH_2 + DUPLICATED_ITEM

        private val BATCH_1_UPDATE_ID_3 = List(10) {
            if (it == 3) DataSourceListItem(it, "updated") else DataSourceListItem(it)
        }
        private val BATCH_2_UPDATE_ID_15 = List(10) {
            if (10 + it == 15) DataSourceListItem(10 + it, "updated") else DataSourceListItem(10 + it)
        }
    }
}

enum class DataSourceKey { KEY_1, KEY_2 }
data class DataSourceListItem(val id: Int, val value: String = "")

class TestInMemoryPagingLocalDataSource : InMemoryPagingLocalDataSource<DataSourceKey, DataSourceListItem>()