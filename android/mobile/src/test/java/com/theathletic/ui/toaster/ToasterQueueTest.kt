package com.theathletic.ui.toaster

import android.app.Activity
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class ToasterQueueTest {

    lateinit var toasterQueue: ToasterQueue

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        toasterQueue = ToasterQueue()
    }

    @Test
    fun `when queue has single request with destroyed activity, return null`() {
        toasterQueue.apply {
            add(createRequest(id = 0, isActivityDestroyed = true))
        }

        val request = toasterQueue.getFirstValidRequest()
        assertNull(request)
    }

    @Test
    fun `when queue has single request with active activity, return request`() {
        toasterQueue.apply {
            add(createRequest(id = 0, isActivityDestroyed = false))
        }

        val request = toasterQueue.getFirstValidRequest()
        assertNotNull(request)
        assertEquals(0, request.textRes)
    }

    @Test
    fun `ignore all requests with destroyed activities`() {
        toasterQueue.apply {
            add(createRequest(id = 0, isActivityDestroyed = true))
            add(createRequest(id = 1, isActivityDestroyed = true))
            add(createRequest(id = 2, isActivityDestroyed = false))
            add(createRequest(id = 3, isActivityDestroyed = true))
        }

        val request = toasterQueue.getFirstValidRequest()
        assertNotNull(request)
        assertEquals(2, request.textRes)

        val nextRequest = toasterQueue.getFirstValidRequest()
        assertNull(nextRequest)
    }

    @Test
    fun `queue does not ignore any requests with active activities`() {
        toasterQueue.apply {
            add(createRequest(id = 0, isActivityDestroyed = false))
            add(createRequest(id = 1, isActivityDestroyed = false))
            add(createRequest(id = 2, isActivityDestroyed = false))
            add(createRequest(id = 3, isActivityDestroyed = false))
        }

        var count = 0
        while (toasterQueue.getFirstValidRequest() != null) {
            count++
        }
        assertEquals(4, count)
    }

    private fun createRequest(id: Int, isActivityDestroyed: Boolean): ToasterQueue.Request {
        val activity = mock<Activity>()
        whenever(activity.isDestroyed).thenReturn(isActivityDestroyed)
        return ToasterQueue.Request(activity, id)
    }
}