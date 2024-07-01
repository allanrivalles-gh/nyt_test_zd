package com.theathletic.ads.bridge

import android.webkit.WebView
import com.google.common.truth.Truth.assertThat
import com.theathletic.ads.bridge.AdBridge.Companion.DEFAULT_CONTENT_URL
import com.theathletic.ads.bridge.data.local.AdEvent
import com.theathletic.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class AdBridgeTest {
    private lateinit var adBridge: AdBridge

    @Mock lateinit var mockWebView: WebView

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        adBridge = AdBridge()
    }

    @Test
    fun `verify initial event state of adbridge`() = runTest {
        val event = adBridge.observeAdEvents().value
        assertThat(event).isEqualTo(AdEvent.NotInitialized)
    }

    @Test
    fun `verify attach event called after attaching ad bridge to webview`() = runTest {
        adBridge.attach(mockWebView, "")
        val event = adBridge.observeAdEvents().value
        assertThat(event).isEqualTo(AdEvent.Attach)
    }

    @Test
    fun `verify ad config returned when initializing ad targeting`() {
        val adConfig = EXPECTED_CONFIG_STRING
        adBridge.attach(mockWebView, adConfig)
        val adConfigString = adBridge.initializeAdTargeting()
        assertThat(adConfigString).isEqualTo(EXPECTED_CONFIG_STRING)
    }

    @Test
    fun `verify content url returned when fetching url`() {
        val url = EXPECTED_CONTENT_URL
        val adConfig = EXPECTED_CONFIG_STRING
        adBridge.attach(mockWebView, adConfig, url)
        val contentUrl = adBridge.fetchContentUrl()
        assertThat(contentUrl).isEqualTo(EXPECTED_CONTENT_URL)
    }

    @Test
    fun `verify default content url returned when fetching url`() {
        val adConfig = EXPECTED_CONFIG_STRING
        adBridge.attach(mockWebView, adConfig)
        val contentUrl = adBridge.fetchContentUrl()
        assertThat(contentUrl).isEqualTo(DEFAULT_CONTENT_URL)
    }

    @Test
    fun `verify ad request event triggered when sendLogCommand with AdRequestSent event name`() = runTest {
        adBridge.sendLogCommand("{\"eventName\":\"AdRequestSent\",\"eventData\":{\"position\":\"mid1\"}}")
        assertThat(adBridge.observeAdEvents().value).isEqualTo(adRequestFixture(pos = "mid1"))
    }

    @Test
    fun `verify AdNoFill event triggered when sendLogCommand with AdRendered event and isEmpty`() = runTest {
        adBridge.sendLogCommand("{\"eventName\":\"AdRendered\",\"eventData\":{\"position\":\"mid1\",\"isEmpty\":true}}")
        assertThat(adBridge.observeAdEvents().value).isEqualTo(adResponseFixture(pos = "mid1", isEmpty = true))
    }

    @Test
    fun `verify AdResponseSuccess event triggered when sendLogCommand with AdRendered event name`() = runTest {
        adBridge.sendLogCommand("{\"eventName\":\"AdRendered\",\"eventData\":{\"position\":\"mid1\"}}")
        assertThat(adBridge.observeAdEvents().value).isEqualTo(adResponseFixture(pos = "mid1"))
    }

    @Test
    fun `verify AdEvent position is correct for sendLogCommand`() = runTest {
        adBridge.sendLogCommand("{\"eventName\":\"AdRequestSent\",\"eventData\":{\"position\":\"mid1\"}}")
        assertThat(adBridge.observeAdEvents().value).isEqualTo(adRequestFixture(pos = "mid1"))
        adBridge.sendLogCommand("{\"eventName\":\"AdRequestSent\",\"eventData\":{}}")
        assertThat(adBridge.observeAdEvents().value).isEqualTo(adRequestFixture(id = 2))
    }

    @Test
    fun `verify new AdEvent triggered for every sendLogCommand that occurs`() = runTest {
        adBridge.sendLogCommand("{\"eventName\":\"AdRequestSent\",\"eventData\":{\"position\":\"mid1\"}}")
        assertThat(adBridge.observeAdEvents().value).isEqualTo(adRequestFixture(pos = "mid1"))
        adBridge.sendLogCommand("{\"eventName\":\"AdRendered\",\"eventData\":{\"position\":\"mid1\"}}")
        assertThat(adBridge.observeAdEvents().value).isEqualTo(adResponseFixture(id = 2, pos = "mid1"))
        adBridge.sendLogCommand("{\"eventName\":\"AdRendered\",\"eventData\":{\"position\":\"mid1\"}}")
        assertThat(adBridge.observeAdEvents().value).isEqualTo(adResponseFixture(id = 3, pos = "mid1"))
    }

    @Test
    fun `verify AdImpression event triggered when sendLogCommand with AdViewable event name`() {
        adBridge.sendLogCommand("{\"eventName\":\"AdViewable\",\"eventData\":{\"position\":\"mid1\"}}")
        assertThat(adBridge.observeAdEvents().value).isEqualTo(adImpressionFixture(pos = "mid1"))
    }

    private fun adRequestFixture(id: Int = 1, pos: String? = null) = AdEvent.AdRequest(
        id,
        pos ?: "null",
        if (pos != null) mapOf("position" to pos) else emptyMap()
    )

    private fun adResponseFixture(id: Int = 1, pos: String? = null, isEmpty: Boolean? = null): AdEvent {
        return if (isEmpty == true) {
            AdEvent.AdNoFill(
                id,
                pos ?: "null",
                if (pos != null) mapOf("position" to pos, "isEmpty" to isEmpty) else mapOf("isEmpty" to isEmpty)
            )
        } else {
            AdEvent.AdResponseSuccess(
                id,
                pos ?: "null",
                if (pos != null) mapOf("position" to pos) else emptyMap()
            )
        }
    }

    private fun adImpressionFixture(id: Int = 1, pos: String? = null) = AdEvent.AdImpression(
        id,
        pos ?: "null",
        if (pos != null) mapOf("position" to pos) else emptyMap()
    )

    companion object {
        private const val EXPECTED_CONTENT_URL = "https://theathletic.com/12345"
        private const val EXPECTED_CONFIG_STRING =
            "{\"AdRequirements\":{\"ta_page_view_id\":\"1234\",\"prop\":\"athdroid\",\"plat\":\"phone\"}," +
                "\"privacy\":{\"geo\":{\"cc\":\"US\",\"state\":\"CA\"}}," +
                "\"viewport\":{\"width\":700,\"height\":800},\"adUnitPath\":\"/29390238/theathletic\"}"
    }
}