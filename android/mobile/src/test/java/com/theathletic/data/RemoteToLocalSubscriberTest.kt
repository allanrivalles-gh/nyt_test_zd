package com.theathletic.data

import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.assertStream
import com.theathletic.test.runTest
import com.theathletic.test.testFlowOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RemoteToLocalSubscriberTest {

    @get:Rule val coroutineTestRule = CoroutineTestRule()

    private lateinit var subscriber: TestSubscriber
    private val fakeDatabase = FakeDatabase()

    @Before
    fun setup() {
        subscriber = TestSubscriber(fakeDatabase, coroutineTestRule.dispatcherProvider)
    }

    @Test
    fun `subscriber listens for updates and maps them to local models`() = runTest {
        val testDbFlow = testFlowOf(fakeDatabase.valueStream)
        val subscriberJob = launch(coroutineTestRule.dispatcher) {
            subscriber.subscribe(TestSubscriber.Params(USER_ID))
        }

        subscriber.receiveRemoteMessage(REMOTE_MODEL_1)
        subscriber.receiveRemoteMessage(REMOTE_MODEL_2)
        subscriber.receiveRemoteMessage(REMOTE_MODEL_3)

        assertStream(testDbFlow).hasReceivedExactly(
            LOCAL_MODEL_1,
            LOCAL_MODEL_2,
            LOCAL_MODEL_3
        )

        testDbFlow.finish()
        subscriberJob.cancel()
    }

    @Test
    fun `subscriber stops listening when the scope is cancelled`() = runTest {
        val scope = CoroutineScope(coroutineTestRule.dispatcher)
        val testDbFlow = testFlowOf(fakeDatabase.valueStream)
        scope.launch { subscriber.subscribe(TestSubscriber.Params(USER_ID)) }

        subscriber.receiveRemoteMessage(REMOTE_MODEL_1)
        subscriber.receiveRemoteMessage(REMOTE_MODEL_2)
        scope.cancel()
        subscriber.receiveRemoteMessage(REMOTE_MODEL_3)

        assertStream(testDbFlow).hasReceivedExactly(
            LOCAL_MODEL_1,
            LOCAL_MODEL_2
        )

        testDbFlow.finish()
    }

    companion object {
        const val USER_ID = 99L

        val REMOTE_MODEL_1 = TestRemoteModel("1", USER_ID)
        val REMOTE_MODEL_2 = TestRemoteModel("2", USER_ID)
        val REMOTE_MODEL_3 = TestRemoteModel("3", USER_ID)

        val LOCAL_MODEL_1 = TestDbModel(1, USER_ID)
        val LOCAL_MODEL_2 = TestDbModel(2, USER_ID)
        val LOCAL_MODEL_3 = TestDbModel(3, USER_ID)
    }
}