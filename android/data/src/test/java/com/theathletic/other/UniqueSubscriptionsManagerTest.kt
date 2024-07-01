package com.theathletic.other

import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class UniqueSubscriptionsManagerTest {
    private lateinit var cancel: () -> Unit
    private lateinit var subscribe: (ids: Set<String>) -> () -> Unit

    @Before
    fun setUp() {
        cancel = mock()
        subscribe = mock()
        whenever(subscribe.invoke(any())).thenReturn(cancel)
    }

    @Test
    fun `should subscribe to ids when setting ids`() {
        val manager = UniqueSubscriptionsManager(subscribe)

        val subscriptionUpdated = manager.set(setOf("2", "3"))

        verify(subscribe).invoke(setOf("2", "3"))
        assertTrue(subscriptionUpdated)
    }

    @Test
    fun `should cancel then subscribe to new ids when setting ids and ids changed`() {
        val manager = UniqueSubscriptionsManager(subscribe)
        manager.set(setOf("1", "2"))
        reset(subscribe)

        val subscriptionUpdated = manager.set(setOf("2", "3"))

        val inOrder = inOrder(cancel, subscribe)
        inOrder.verify(cancel).invoke()
        inOrder.verify(subscribe).invoke(setOf("2", "3"))
        assertTrue(subscriptionUpdated)
    }

    @Test
    fun `should not cancel and subscribe when setting ids but they are the same as before`() {
        val manager = UniqueSubscriptionsManager(subscribe)
        manager.set(setOf("1", "2"))
        reset(subscribe)

        val subscriptionUpdated = manager.set(setOf("1", "2"))

        verify(cancel, never()).invoke()
        verify(subscribe, never()).invoke(any())
        assertFalse(subscriptionUpdated)
    }

    @Test
    fun `should subscribe to ids when adding ids`() {
        val manager = UniqueSubscriptionsManager(subscribe)

        val subscriptionUpdated = manager.add(setOf("1", "2"))

        verify(subscribe).invoke(setOf("1", "2"))
        assertTrue(subscriptionUpdated)
    }

    @Test
    fun `should cancel then subscribe to combined ids when adding more ids`() {
        val manager = UniqueSubscriptionsManager(subscribe)
        manager.add(setOf("1", "2"))
        reset(subscribe)

        val subscriptionUpdated = manager.add(setOf("2", "3"))

        val inOrder = inOrder(cancel, subscribe)
        inOrder.verify(cancel).invoke()
        inOrder.verify(subscribe).invoke(setOf("1", "2", "3"))
        assertTrue(subscriptionUpdated)
    }

    @Test
    fun `should not subscribe when ids to subscribe is empty`() {
        val manager = UniqueSubscriptionsManager(subscribe)

        val subscriptionUpdated = manager.add(setOf())

        verify(subscribe, never()).invoke(any())
        // this was already empty to start with, that is why it hasn't changed
        assertFalse(subscriptionUpdated)
    }

    @Test
    fun `should cancel then subscribe with remaining ids when removing ids`() {
        val manager = UniqueSubscriptionsManager(subscribe)
        manager.add(setOf("1", "2", "3", "4"))
        reset(subscribe)

        val subscriptionUpdated = manager.remove(setOf("1", "4"))

        val inOrder = inOrder(cancel, subscribe)
        inOrder.verify(cancel).invoke()
        inOrder.verify(subscribe).invoke(setOf("2", "3"))
        assertTrue(subscriptionUpdated)
    }

    @Test
    fun `should cancel and not subscribe again if all previously added ids were removed`() {
        val manager = UniqueSubscriptionsManager(subscribe)
        manager.add(setOf("1", "2"))
        reset(subscribe)

        val subscriptionUpdated = manager.remove(setOf("1", "2"))

        verify(cancel).invoke()
        verify(subscribe, never()).invoke(any())
        assertTrue(subscriptionUpdated)
    }

    @Test
    fun `shouldn't resubscribe if ids have not changed when adding ids`() {
        val manager = UniqueSubscriptionsManager(subscribe)
        manager.add(setOf("1", "2"))
        reset(subscribe)

        val subscriptionUpdated = manager.add(setOf("1", "2"))

        verify(cancel, never()).invoke()
        verify(subscribe, never()).invoke(any())
        assertFalse(subscriptionUpdated)
    }

    @Test
    fun `shouldn't resubscribe if ids have not changed when removing ids`() {
        val manager = UniqueSubscriptionsManager(subscribe)
        manager.add(setOf("1", "2"))
        reset(subscribe)

        val subscriptionUpdated = manager.remove(setOf("3", "4"))

        verify(cancel, never()).invoke()
        verify(subscribe, never()).invoke(any())
        assertFalse(subscriptionUpdated)
    }
}