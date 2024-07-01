package com.theathletic.test

import com.google.common.truth.Fact.simpleFact
import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.google.common.truth.Subject.Factory
import com.google.common.truth.Truth.assertAbout
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assert_
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.coroutines.CoroutineContext

fun <T> CoroutineScope.testFlowOf(
    flow: Flow<T>,
    context: CoroutineContext = UnconfinedTestDispatcher()
): TestFlow<T> {
    return TestFlow(context, this, flow)
}

class TestFlow<T>(
    context: CoroutineContext,
    coroutineScope: CoroutineScope,
    flow: Flow<T>
) {

    private var collectJob: Job
    internal val events = mutableListOf<T>()
    val numberOfEvents: Int
        get() = events.size

    init {
        collectJob = coroutineScope.launch(context = context) { flow.toList(events) }
    }

    fun finish() {
        collectJob.cancel()
    }
}

class TestFlowSubject<T> private constructor(
    failureMetadata: FailureMetadata,
    flow: TestFlow<T>
) : Subject(failureMetadata, flow) {

    private val events = flow.events
    private val receivedInstances = events.mapNotNull { getClassFromEvent(it) }

    fun lastEvent(): Subject {
        assertEventReceived()
        return assert_().that(events[events.lastIndex])
    }

    fun hasReceivedInstanceOf(vararg clazz: Class<*>) {
        assertThat(receivedInstances).containsAnyIn(clazz)
    }

    fun hasReceivedExactlyInstanceOf(vararg clazz: Class<*>) {
        assertThat(receivedInstances).containsExactlyElementsIn(clazz)
    }

    fun lastEvent(assert: Subject.(T) -> Unit) {
        assertEventReceived()
        assert(events[events.lastIndex])
    }

    fun eventAt(index: Int, assert: Subject.(T) -> Unit) {
        assertEventReceived()
        assertThat(index).isAtLeast(0)
        assertThat(index).isAtMost(events.lastIndex)
        assert(events[index])
    }

    fun hasReceived(vararg event: T?) {
        assertThat(events).containsAnyIn(event)
    }

    fun hasReceivedExactly(vararg event: T?) {
        assertThat(events).containsExactlyElementsIn(event)
    }

    fun hasNoEventReceived() {
        assertThat(events).isEmpty()
    }

    fun eventCount(count: Int) {
        assertThat(events).hasSize(count)
    }

    private fun assertEventReceived() {
        if (events.isEmpty()) failWithoutActual(simpleFact("No events was received"))
    }

    private fun getClassFromEvent(event: T): Class<out T>? {
        return if (event != null) event!!::class.java else null
    }

    companion object {
        internal fun <T> factory() =
            Factory<TestFlowSubject<T>, TestFlow<T>> { data, flow -> TestFlowSubject(data, flow) }
    }
}

fun <T> assertStream(testFlow: TestFlow<T>): TestFlowSubject<T> {
    return assertAbout(TestFlowSubject.factory<T>()).that(testFlow)
}