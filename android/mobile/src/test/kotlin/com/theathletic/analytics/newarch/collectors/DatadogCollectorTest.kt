package com.theathletic.analytics.newarch.collectors

import com.theathletic.analytics.DatadogWrapper
import com.theathletic.analytics.newarch.Event
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class DatadogCollectorTest {

    @Mock
    lateinit var mockDatadogWrapper: DatadogWrapper

    private lateinit var datadogCollector: DatadogCollector

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        datadogCollector = DatadogCollector(mockDatadogWrapper)
    }

    @Test
    fun `verify datadog wrapper sends properly formatted log when tracking event`() {
        val properties = mapOf("view" to "home")
        val eventName = "ad-on-load"
        val expectedResult = analyticsEventFixture(eventName, properties, emptyMap())
        datadogCollector.trackEvent(Event.Global.AdOnLoad("home", "12345"), properties, emptyMap())
        verify(mockDatadogWrapper).sendLog(message = eventName, attributes = expectedResult)
    }

    private fun analyticsEventFixture(
        eventName: String,
        arguments: Map<String, String>,
        deeplinkParams: Map<String, String>
    ) = mapOf(

        "metadata" to mapOf("eventName" to eventName),
        "arguments" to arguments,
        "deeplink" to deeplinkParams
    )
}