package com.theathletic.data

import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.assertStream
import com.theathletic.test.runTest
import com.theathletic.test.testFlowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RemoteToLocalFetcherTest {

    @get:Rule val coroutineTestRule = CoroutineTestRule()

    private lateinit var fetcher: TestFetcher

    private val fakeDatabase = FakeDatabase()

    @Before
    fun setup() {
        fetcher = TestFetcher(fakeDatabase, coroutineTestRule.dispatcherProvider)
    }

    @Test
    fun `successful fetch, map and write to db`() = runTest {
        val testFlow = testFlowOf(fakeDatabase.valueStream)
        launch(coroutineTestRule.dispatcher) { fetcher.fetchRemote(TestFetcher.Params(USER_ID)) }

        fetcher.succeed(TestRemoteModel("1", USER_ID))

        assertStream(testFlow).hasReceivedExactly(TestDbModel(1, USER_ID))

        testFlow.finish()
    }

    @Test
    fun `failed fetch logs error`() = runTest {
        val testFlow = testFlowOf(fakeDatabase.valueStream)
        launch(coroutineTestRule.dispatcher) { fetcher.fetchRemote(TestFetcher.Params(USER_ID)) }

        fetcher.fail()

        assertStream(testFlow).hasNoEventReceived()

        testFlow.finish()
    }

    @Test
    fun `successful fetch, bad mapping logs error`() = runTest {
        val testFlow = testFlowOf(fakeDatabase.valueStream)
        launch(coroutineTestRule.dispatcher) { fetcher.fetchRemote(TestFetcher.Params(USER_ID)) }

        fetcher.succeed(TestRemoteModel("a", USER_ID))

        assertStream(testFlow).hasNoEventReceived()
        testFlow.finish()
    }

    @Test
    fun `successful fetch, mapping and failed db write logs error`() = runTest {
        val testFlow = testFlowOf(fakeDatabase.valueStream)
        launch(coroutineTestRule.dispatcher) { fetcher.fetchRemote(TestFetcher.Params(USER_ID)) }

        fetcher.failDbWrites = true
        fetcher.succeed(TestRemoteModel("1", USER_ID))

        assertStream(testFlow).hasNoEventReceived()
        testFlow.finish()
    }

    @Test
    fun `simultaneous calls with same params gives only one db update`() = runBlockingTest {
        val testFlow = testFlowOf(fakeDatabase.valueStream)
        launch(coroutineTestRule.dispatcher) { fetcher.fetchRemote(TestFetcher.Params(USER_ID)) }
        launch(coroutineTestRule.dispatcher) { fetcher.fetchRemote(TestFetcher.Params(USER_ID)) }

        fetcher.succeed(TestRemoteModel("1", USER_ID))

        assertStream(testFlow).hasReceivedExactly(TestDbModel(1, USER_ID))
        testFlow.finish()
    }

    @Test
    fun `synchronous calls with same params gives multiple db updates`() = runTest {
        val testFlow = testFlowOf(fakeDatabase.valueStream)
        launch(coroutineTestRule.dispatcher) { fetcher.fetchRemote(TestFetcher.Params(USER_ID)) }

        fetcher.succeed(TestRemoteModel("1", USER_ID))

        launch(coroutineTestRule.dispatcher) { fetcher.fetchRemote(TestFetcher.Params(USER_ID_2)) }

        fetcher.succeed(TestRemoteModel("2", USER_ID_2))

        assertStream(testFlow).hasReceivedExactly(
            TestDbModel(1, USER_ID),
            TestDbModel(2, USER_ID_2)
        )

        testFlow.finish()
    }

    companion object {
        const val USER_ID = 99L
        const val USER_ID_2 = 100L
    }
}