package com.theathletic.ads.bridge

import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.google.android.gms.ads.MobileAds
import com.theathletic.ads.bridge.data.local.AdEvent
import com.theathletic.ads.bridge.data.remote.AdSlotEvent
import com.theathletic.ads.bridge.data.remote.BridgeCommand
import com.theathletic.annotation.autokoin.AutoKoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger

class AdBridge @AutoKoin constructor() {
    private val adEventFlow: MutableStateFlow<AdEvent> = MutableStateFlow(AdEvent.NotInitialized)
    private var adConfig: String? = null
    private var contentUrl: String = DEFAULT_CONTENT_URL
    private val eventId = AtomicInteger()

    fun attach(webView: WebView, adConfig: String, contentUrl: String? = null) {
        this.adConfig = adConfig
        contentUrl?.let {
            this.contentUrl = it
        }
        adEventFlow.value = AdEvent.Attach
        try {
            MobileAds.registerWebView(webView)
            webView.addJavascriptInterface(this, ADS_INTERFACE_NAME)
        } catch (e: Exception) {
            Timber.e("Failed to attach an AdBridge to the webview.")
        }
    }

    fun observeAdEvents() = adEventFlow.asStateFlow()

    @JavascriptInterface
    fun initializeAdTargeting(): String {
        return adConfig ?: "{}"
    }

    @JavascriptInterface
    fun fetchContentUrl(): String {
        return contentUrl
    }

    @JavascriptInterface
    fun sendLogCommand(command: String) {
        val bridgeCommand = BridgeCommand.fromJson(command)
        bridgeCommand?.let {
            val position = it.eventData?.get("position").toString()
            when (it.eventName) {
                AdSlotEvent.AdRequestSent -> {
                    adEventFlow.value = AdEvent.AdRequest(eventId.incrementAndGet(), position, it.eventData)
                }
                AdSlotEvent.AdRendered -> adEventFlow.value = getAdRenderedEvent(position, it.eventData)
                AdSlotEvent.AdViewable -> {
                    adEventFlow.value = AdEvent.AdImpression(eventId.incrementAndGet(), position, it.eventData)
                }
                AdSlotEvent.AdDefined,
                AdSlotEvent.AdCalled,
                AdSlotEvent.UnsupportedEvent -> {
                    // no-op
                }
            }
        }
    }

    private fun getAdRenderedEvent(position: String, data: Map<String, Any>?): AdEvent {
        return if (data?.get("isEmpty") == true) {
            AdEvent.AdNoFill(eventId.incrementAndGet(), position, data)
        } else {
            AdEvent.AdResponseSuccess(eventId.incrementAndGet(), position, data)
        }
    }

    companion object {
        private const val ADS_INTERFACE_NAME = "AdBridge"
        const val DEFAULT_CONTENT_URL = "https://theathletic.com/"
    }
}