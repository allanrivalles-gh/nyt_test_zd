@file:OptIn(ExperimentalCoroutinesApi::class)

package com.theathletic.test

import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class TestDispatcherProvider(testDispatcher: TestDispatcher) : DispatcherProvider {
    override val default: CoroutineDispatcher = testDispatcher
    override val io: CoroutineDispatcher = testDispatcher
    override val main: CoroutineDispatcher = testDispatcher
    override val unconfined: CoroutineDispatcher = testDispatcher
}

class CoroutineTestRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    val dispatcher: CoroutineDispatcher = testDispatcher
    val dispatcherProvider: DispatcherProvider = TestDispatcherProvider(testDispatcher)

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }

    fun advanceTimeBy(delayTimeMillis: Long) {
        testDispatcher.scheduler.advanceTimeBy(delayTimeMillis)
    }
}